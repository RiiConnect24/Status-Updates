package xyz.rc24.status;

import club.minnced.discord.webhook.WebhookClient;
import club.minnced.discord.webhook.WebhookClientBuilder;
import club.minnced.discord.webhook.send.WebhookEmbed;
import club.minnced.discord.webhook.send.WebhookEmbedBuilder;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.MessageBuilder;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.utils.Checks;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xyz.rc24.status.rest.RestJDA;

import java.io.IOException;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.OffsetDateTime;
import java.time.temporal.ChronoUnit;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

class StatuspageAPI
{
    private final String BASE_URL = "https://wfdt192tn9y2.statuspage.io";
    private final String STATUS_URL = BASE_URL + "/api/v2/summary.json";
    private final String INCIDENTS_URL = BASE_URL + "/api/v2/incidents.json";
    static final String CLICKABLE = "https://status.rc24.xyz/incidents/";

    private final String LATEST_FILE = "latest_status.txt";
    private final int LATEST_MAX_SIZE = 100;
    private final Logger LOG = LoggerFactory.getLogger("StatusUpdates");
    private final List<String> latestIncidents = new LinkedList<>();
    private final ScheduledExecutorService threadpool;
    private final OkHttpClient client;
    private final WebhookClient webhook;
    private final RestJDA restJDA;
    private final long channelId;
    private final long statusMessageId;
    private final long channelsMessageId;
    private final long wiiMailMessageId;
    private final long unresolvedIncidentsMessageId;

    /**
     * Line 0: Webhook URL
     * Line 1: Bot Token
     * Line 2: Channel ID
     * Line 3: Status Message ID
     * Line 4: Channels Message ID
     * Line 5: Wii Mail Message ID
     * Line 6: Unresolved incidents Message ID
     */
    StatuspageAPI() throws Exception
    {
        List<String> config = Files.readAllLines(Paths.get("config.txt"));
        Checks.check(config.size() > 4, "Invalid number of lines in config file");

        threadpool = Executors.newScheduledThreadPool(2);
        client = new OkHttpClient.Builder().build();
        webhook = new WebhookClientBuilder(config.get(0)).setHttpClient(client).build();

        try
        {
            latestIncidents.addAll(Files.readAllLines(Paths.get(LATEST_FILE)));
            while(latestIncidents.size() > LATEST_MAX_SIZE)
                latestIncidents.remove(0);
        }
        catch(Exception e)
        {
            LOG.warn("Failed to read latest incidents: " + e);
        }

        restJDA = new RestJDA(config.get(1), client);
        channelId = Long.parseLong(config.get(2));
        statusMessageId = Long.parseLong(config.get(3));
        channelsMessageId = Long.parseLong(config.get(4));
        wiiMailMessageId = Long.parseLong(config.get(5));
        unresolvedIncidentsMessageId = Long.parseLong(config.get(6));

        threadpool.scheduleWithFixedDelay(this::readIncidents, 0, 3, TimeUnit.MINUTES);
        threadpool.scheduleWithFixedDelay(() ->
        {
            try
            {
                updateEmbed();
            }
            catch(Exception e)
            {
                e.printStackTrace();
                System.exit(-1);
            }
        }, 0, 3, TimeUnit.MINUTES);
    }

    @SuppressWarnings("ConstantConditions")
    private ServiceStatus getStatus() throws Exception
    {
        try(Reader reader = client.newCall(new Request.Builder()
                .get().url(STATUS_URL)
                .header("Content-Type", "application/json")
                .build()).execute().body().charStream())
        {
            JSONObject obj = new JSONObject(new JSONTokener(reader));
            JSONObject status = obj.getJSONObject("status");
            JSONObject page = obj.getJSONObject("page");
            JSONArray components = obj.getJSONArray("components");
            ServiceStatus ss = new ServiceStatus(status.getString("indicator"), status.getString("description"), page.getString("updated_at"));

            JSONObject component;
            for(int i = 0; i < components.length(); i++)
            {
                component = components.getJSONObject(i);
                if(!(component.getBoolean("group")))
                    continue;

                ss.addGroup(component.getString("id"), component.getString("name"), component.getString("status"), new LinkedList<>());
            }

            for(int i = 0; i < components.length(); i++)
            {
                component = components.getJSONObject(i);
                if(component.getBoolean("group"))
                    continue;

                if(!(component.isNull("group_id")))
                {
                    ServiceStatus.Group group = ss.groups.get(component.getString("group_id"));
                    group.addComponent(component.getString("name"), component.getString("status"));
                }
                else
                    ss.addComponent(component.getString("name"), component.getString("status"));
            }

            JSONArray incidents = obj.getJSONArray("incidents");
            if(incidents.length() > 0)
            {
                JSONObject latestIncident = incidents.getJSONObject(incidents.length() - 1);
                JSONObject latestIncidentUpdate = latestIncident.getJSONArray("incident_updates")
                        .getJSONObject(latestIncident.getJSONArray("incident_updates").length() - 1);
                ss.setIncident(latestIncident.getString("name"), latestIncident.getString("id"),
                        latestIncidentUpdate.getString("status"), latestIncidentUpdate.getString("body"),
                        latestIncidentUpdate.getString("created_at"));
                // handleIncidents(incidents);
            }

            return ss;
        }
        catch(Exception e)
        {
            LOG.error("Failed to read summary: ", e);
            throw new Exception(e);
        }

        // return null;
    }

    @SuppressWarnings("ConstantConditions")
    private void readIncidents()
    {
        try(Reader reader = client.newCall(new Request.Builder()
                .get().url(INCIDENTS_URL)
                .header("Content-Type", "application/json")
                .build()).execute().body().charStream())
        {
            JSONObject obj = new JSONObject(new JSONTokener(reader));
            handleIncidents(obj.getJSONArray("incidents"));
        }
        catch(Exception e)
        {
            LOG.error("Failed to read summary: ", e);
        }
    }

    private void handleIncidents(JSONArray incidents)
    {
        List<WebhookEmbed> embeds = new LinkedList<>();
        for(int i = incidents.length() - 1; i >= 0; i--)
        {
            JSONObject incident = incidents.getJSONObject(i);
            JSONArray updates = incident.getJSONArray("incident_updates");
            for(int j = updates.length() - 1; j >= 0; j--)
            {
                JSONObject update = updates.getJSONObject(j);
                OffsetDateTime time = OffsetDateTime.parse(update.getString("created_at"));
                if(time.until(OffsetDateTime.now(), ChronoUnit.DAYS) > 3)
                    continue;

                String uid = incident.getString("id") + "-" + update.getString("id");
                if(latestIncidents.contains(uid))
                    continue;

                latestIncidents.add(uid);
                StatusEnum status = StatusEnum.fromIncident(update.getString("status"));
                String body = update.getString("body");
                if(body.length() > 2048)
                    body = body.substring(0,2040) + " (...)";

                embeds.add(new WebhookEmbedBuilder()
                        .setTitle(new WebhookEmbed.EmbedTitle(incident.getString("name"), CLICKABLE + incident.getString("id")))
                        .setDescription(body)
                        .setColor(status.color.getRGB())
                        .setFooter(new WebhookEmbed.EmbedFooter(StatusEnum.textFormat(update.getString("status")), status.getEmoteLink()))
                        .setTimestamp(time)
                        .build());
            }
        }

        if(!(embeds.isEmpty()))
        {
            while(latestIncidents.size() > LATEST_MAX_SIZE)
                latestIncidents.remove(0);

            StringBuilder sb = new StringBuilder();
            latestIncidents.forEach(i -> sb.append("\n").append(i));

            try
            {
                Files.write(Paths.get(LATEST_FILE), sb.toString().trim().getBytes());
            }
            catch(IOException ex)
            {
                LOG.warn("Failed to write incidents: " + ex);
            }
        }

        while(embeds.size() > 5)
        {
            webhook.send(embeds.subList(0, 5));
            embeds.subList(0, 5).clear();
        }

        if(!(embeds.isEmpty()))
            webhook.send(embeds);
    }

    private void updateEmbed() throws Exception
    {
        ServiceStatus ss = getStatus();

        restJDA.editMessage(channelId, statusMessageId, getStatusEmbed(ss)).queue();
        restJDA.editMessage(channelId, channelsMessageId, getChannelsStatusEmbed(ss)).queue();
        restJDA.editMessage(channelId, wiiMailMessageId, getWiiMailStatusEmbed(ss)).queue();
        restJDA.editMessage(channelId, unresolvedIncidentsMessageId, getUnresolvedIncidentsEmbed(ss)).queue();
    }

    private Message getStatusEmbed(ServiceStatus ss)
    {
        StatusEnum status = ss.status;

        EmbedBuilder embed = new EmbedBuilder();
        embed.setColor(status.color);
        embed.setTitle(status.getEmote() + " " + ss.statusDescription);
        embed.setTimestamp(ss.lastUpdate);
        embed.setFooter("Last Updated", null);

        for(ServiceStatus.Component component : ss.components)
            embed.addField(component.name, component.getStatus().getEmote() + " " + StatusEnum.textFormat(component.status), true);

        return new MessageBuilder().setEmbed(embed.build()).build();
    }

    private Message getChannelsStatusEmbed(ServiceStatus ss)
    {
        ServiceStatus.Group group = ss.groups.get("ncrnc0cdhrtp");
        StatusEnum status = group.getStatus();

        EmbedBuilder embed = new EmbedBuilder();
        embed.setColor(status.color);
        embed.setTitle(status.getEmote() + " " + StatusEnum.textFormat(group.status));
        embed.setTimestamp(ss.lastUpdate);
        embed.setFooter("Last Updated", null);

        for(ServiceStatus.Component component : group.components)
            embed.addField(component.name, component.getStatus().getEmote() + " " + StatusEnum.textFormat(component.status), true);

        return new MessageBuilder().setEmbed(embed.build()).build();
    }

    private Message getWiiMailStatusEmbed(ServiceStatus ss)
    {
        ServiceStatus.Group group = ss.groups.get("rbj7lmdlfypw");
        StatusEnum status = group.getStatus();

        EmbedBuilder embed = new EmbedBuilder();
        embed.setColor(status.color);
        embed.setTitle(status.getEmote() + " " + StatusEnum.textFormat(group.status));
        embed.setTimestamp(ss.lastUpdate);
        embed.setFooter("Last Updated", null);

        for(ServiceStatus.Component component : group.components)
            embed.addField(component.name, component.getStatus().getEmote() + " " + StatusEnum.textFormat(component.status), true);

        return new MessageBuilder().setEmbed(embed.build()).build();
    }

    private Message getUnresolvedIncidentsEmbed(ServiceStatus ss)
    {
        ServiceStatus.Incident incident = ss.latestIncident;
        EmbedBuilder embed = new EmbedBuilder();
        StatusEnum status;
        OffsetDateTime time;

        if(incident == null)
        {
            status = StatusEnum.ONLINE;
            time = ss.lastUpdate;
            embed.addField(status.getEmote() + " No incidents reported", "", false);
        }
        else
        {
            status = incident.getStatus();
            time = incident.time;
            embed.addField(status.getEmote() + " " + incident.name, incident.body, false);
        }

        embed.setColor(status.color);
        embed.setTimestamp(time);
        embed.setFooter("Last Updated", null);

        return new MessageBuilder().setEmbed(embed.build()).build();
    }
}
