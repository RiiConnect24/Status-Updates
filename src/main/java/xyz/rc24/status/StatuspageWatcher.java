package xyz.rc24.status;

import club.minnced.discord.webhook.send.AllowedMentions;
import club.minnced.discord.webhook.send.WebhookEmbed;
import club.minnced.discord.webhook.send.WebhookEmbedBuilder;
import club.minnced.discord.webhook.send.WebhookMessage;
import club.minnced.discord.webhook.send.WebhookMessageBuilder;
import com.google.gson.stream.JsonReader;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xyz.rc24.status.config.Config;
import xyz.rc24.status.model.Component;
import xyz.rc24.status.model.Incident;
import xyz.rc24.status.model.Statuspage;

import java.io.IOException;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.time.OffsetDateTime;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

import static xyz.rc24.status.StatusApp.Webhooks;

public class StatuspageWatcher implements Runnable
{
    private final Logger logger;
    private final StatusApp app;

    public StatuspageWatcher(StatusApp app)
    {
        this.logger = LoggerFactory.getLogger(StatuspageWatcher.class);
        this.app = app;
    }

    @Override
    public void run()
    {
        for(Config.WatchedPage config : app.getConfig().watchedPages)
        {
            Webhooks clients = app.getClients().get(config.id);
            app.getThreadPool().submit(() ->
            {
                Statuspage page = retrieveStatus(config.url);
                if(page == null) return;

                List<WebhookEmbed> embeds = statusEmbeds(page);
                clients.getStatus().edit(config.statusMessageId, message(embeds, page.status.description));
            });
        }
    }

    @Nullable
    private Statuspage retrieveStatus(String url)
    {
        Request request = new Request.Builder().url(url + "/api/v2/summary.json").build();
        try(Response response = app.getHttpClient().newCall(request).execute())
        {
            ResponseBody body = response.body();
            if(!(response.isSuccessful()) || body == null)
                throw new IOException("Response unsuccessful. Status code: " + response.code());

            try(var isReader = new InputStreamReader(body.byteStream()); var jsonReader = new JsonReader(isReader))
            {return app.getGson().fromJson(jsonReader, Statuspage.class);}
        }
        catch(Exception e)
        {
            logger.error("Failed to retrieve {} status:", url, e);
            return null;
        }
    }

    private List<WebhookEmbed> statusEmbeds(Statuspage statuspage)
    {
        List<WebhookEmbed> embeds = new LinkedList<>();

        for(Component component : statuspage.components)
        {
            if(!(component.isGroup) || component.childComponents == null)
                continue;

            Component.Status status = component.status;
            var embed = new WebhookEmbedBuilder()
                    .setTitle(new WebhookEmbed.EmbedTitle(status.getEmote() + " " + component.name + ": " +
                            status.getName(), null))
                    .setColor(status.getColor());

            for(String childId : component.childComponents)
            {
                Component child = statuspage.getComponentsById().get(childId);
                Component.Status childStatus = child.status;
                embed.addField(new WebhookEmbed.EmbedField(true, child.name, childStatus.getEmote() + " " +
                        childStatus.getName()));
            }

            embed.setFooter(new WebhookEmbed.EmbedFooter("Last Updated", null));
            embed.setTimestamp(OffsetDateTime.now());
            embeds.add(embed.build());
        }

        List<Incident> incidents = statuspage.incidents;
        var incidentsEmbed = new WebhookEmbedBuilder()
                .setTitle(new WebhookEmbed.EmbedTitle(statuspage.status.getEmote() + (incidents.isEmpty() ?
                        " No incidents reported" : " Current Incidents:"), null))
                .setColor(statuspage.status.getColor())
                .setFooter(new WebhookEmbed.EmbedFooter("Last Updated", null))
                .setTimestamp(OffsetDateTime.now());

        // incidents
        for(Incident incident : incidents)
        {
            Incident.Impact impact = incident.impact;
            StringBuilder description = new StringBuilder();
            SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy HH:mm z");

            for(Incident.Update update : incident.updates.stream().limit(5).collect(Collectors.toList()))
            {
                description.append("▫**").append(update.status.getName()).append("**: ")
                        .append("\n").append(update.body).append("\n");
            }

            incidentsEmbed.addField(new WebhookEmbed.EmbedField(false, impact.getEmote() + " " + incident.name +
                    ": " + impact.getName() + " [" + format.format(incident.createdAt) + "]", description.toString()));
        }

        embeds.add(incidentsEmbed.build());
        return embeds;
    }

    private WebhookMessage message(List<WebhookEmbed> embeds, String content)
    {
        return new WebhookMessageBuilder()
                .addEmbeds(embeds)
                .setContent(content)
                .setAllowedMentions(AllowedMentions.none())
                .build();
    }
}
