/*
 * MIT License
 *
 * Copyright (c) 2019-2021 RiiConnect24
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package xyz.rc24.status.actions;

import club.minnced.discord.webhook.WebhookClient;
import club.minnced.discord.webhook.send.AllowedMentions;
import club.minnced.discord.webhook.send.WebhookEmbed;
import club.minnced.discord.webhook.send.WebhookEmbed.EmbedField;
import club.minnced.discord.webhook.send.WebhookEmbed.EmbedTitle;
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
import xyz.rc24.status.Constants;
import xyz.rc24.status.StatusApp;
import xyz.rc24.status.config.Config;
import xyz.rc24.status.model.ScheduledMaintenance;
import xyz.rc24.status.model.Statuspage;
import xyz.rc24.status.model.component.Component;
import xyz.rc24.status.model.component.ComponentStatus;
import xyz.rc24.status.model.incident.Incident;
import xyz.rc24.status.model.Impact;

import java.io.IOException;
import java.io.InputStreamReader;
import java.time.OffsetDateTime;
import java.util.LinkedList;
import java.util.List;

import static xyz.rc24.status.StatusApp.LOGGER;

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
            refreshPage(config);
    }

    public void refreshPage(String id)
    {
        for(Config.WatchedPage config : app.getConfig().watchedPages)
        {
            if(config.id.equals(id))
            {
                refreshPage(config);
                break;
            }
        }
    }

    public void refreshPage(Config.WatchedPage config)
    {
        LOGGER.info("Updating status for {}...", config.name);
        WebhookClient client = app.getClients().get(config.id);
        app.getThreadPool().submit(() ->
        {
            Statuspage page = retrieveStatus(config.url);
            if(page == null) return;

            List<WebhookEmbed> embeds = statusEmbeds(page);
            client.edit(config.statusMessageId, message(embeds));
        });
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

            try(var isReader = new InputStreamReader(body.byteStream());
                var jsonReader = new JsonReader(isReader))
            {
                return app.getGson().fromJson(jsonReader, Statuspage.class);
            }
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

        embeds.add(generalEmbed(statuspage));
        embeds.addAll(groupEmbed(statuspage));

        List<Incident> incidents = statuspage.incidents;
        var incidentsEmbed = new WebhookEmbedBuilder()
                .setTitle(new EmbedTitle(statuspage.status.indicator.getEmote() + (incidents.isEmpty() ?
                        " No incidents reported" : " Current Incidents:"), null))
                .setColor(statuspage.status.indicator.getColor())
                .setFooter(new WebhookEmbed.EmbedFooter("Last Updated", null))
                .setTimestamp(OffsetDateTime.now());

        // incidents
        for(Incident incident : incidents)
        {
            Impact impact = incident.impact;
            StringBuilder description = new StringBuilder();

            for(Incident.Update update : incident.updates.stream().limit(5).toList())
            {
                description.append("▫ **").append(update.status.getName()).append("**: ")
                        .append("\n").append(update.body).append("\n");
            }

            incidentsEmbed.addField(new EmbedField(false, impact.getEmote() + " " + incident.name +
                    ": " + impact.getName() + " [<t:" + incident.createdAt.toInstant().getEpochSecond() + ":F>]", description.toString()));
        }

        List<ScheduledMaintenance> maintenances = statuspage.scheduledMaintenances;
        var maintenancesEmbed = new WebhookEmbedBuilder()
                .setTitle(new EmbedTitle((maintenances.isEmpty() ? statuspage.status.indicator.getEmote() : Constants.PURPLE_EMOTE)
                        + (maintenances.isEmpty() ? " No scheduled maintenances" : " Scheduled Maintenances:"), null))
                .setColor(maintenances.isEmpty() ? statuspage.status.indicator.getColor() : Constants.PURPLE)
                .setFooter(new WebhookEmbed.EmbedFooter("Last Updated", null))
                .setTimestamp(OffsetDateTime.now());

        // maintenances
        for(ScheduledMaintenance maintenance : maintenances)
        {
            StringBuilder description = new StringBuilder();

            for(ScheduledMaintenance.Update update : maintenance.updates.stream().limit(5).toList())
            {
                description.append("▫ **").append(update.status.getName()).append("**: ")
                        .append("\n").append(update.body).append("\n");
            }

            maintenancesEmbed.addField(new EmbedField(false, maintenance.name + " [<t:" +
                    maintenance.scheduledFor.toInstant().getEpochSecond() + ":F>]", description.toString()));
        }

        embeds.add(incidentsEmbed.build());
        embeds.add(maintenancesEmbed.build());
        return embeds;
    }

    private WebhookEmbed generalEmbed(Statuspage statuspage)
    {
        Statuspage.Status pageStatus = statuspage.status;
        var embed = new WebhookEmbedBuilder()
                .setTitle(new EmbedTitle(pageStatus.indicator.getEmote() + " " + pageStatus.description, null))
                .setColor(pageStatus.indicator.getColor())
                .setFooter(new WebhookEmbed.EmbedFooter("Last Updated", null))
                .setTimestamp(OffsetDateTime.now());

        for(Component component : statuspage.components)
        {
            if(component.isGroup || !(component.groupId == null))
                continue;

            ComponentStatus status = component.status;
            embed.addField(new EmbedField(true, status.getEmote() + " " + component.name, status.getName()));
        }

        return embed.build();
    }

    private List<WebhookEmbed> groupEmbed(Statuspage statuspage)
    {
        List<WebhookEmbed> embeds = new LinkedList<>();

        for(Component component : statuspage.components)
        {
            if(!(component.isGroup) || component.childComponents == null)
                continue;

            ComponentStatus status = component.status;
            var embed = new WebhookEmbedBuilder()
                    .setTitle(new EmbedTitle(status.getEmote() + " " + component.name + ": " + status.getName(), null))
                    .setColor(status.getColor())
                    .setFooter(new WebhookEmbed.EmbedFooter("Last Updated", null))
                    .setTimestamp(OffsetDateTime.now());

            for(String childId : component.childComponents)
            {
                Component child = statuspage.getComponentsById().get(childId);
                ComponentStatus childStatus = child.status;
                embed.addField(new EmbedField(true, child.name, childStatus.getEmote() + " " +
                        childStatus.getName()));
            }

            embeds.add(embed.build());
        }

        return embeds;
    }

    private WebhookMessage message(List<WebhookEmbed> embeds)
    {
        return new WebhookMessageBuilder()
                .addEmbeds(embeds)
                .setAllowedMentions(AllowedMentions.none())
                .build();
    }
}
