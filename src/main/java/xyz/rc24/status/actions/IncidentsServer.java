/*
 * MIT License
 * Copyright (c) 2019-2021 RiiConnect24
 * Copyright (c) 2019-2024 Artuto
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
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
import club.minnced.discord.webhook.send.WebhookEmbed;
import club.minnced.discord.webhook.send.WebhookEmbed.EmbedFooter;
import club.minnced.discord.webhook.send.WebhookEmbedBuilder;
import com.google.gson.JsonObject;
import spark.Request;
import spark.Response;
import spark.Spark;
import xyz.rc24.status.Constants;
import xyz.rc24.status.StatusApp;
import xyz.rc24.status.config.Config;
import xyz.rc24.status.model.incident.Incident;
import xyz.rc24.status.model.incident.IncidentStatus;

import java.time.OffsetDateTime;

import static spark.Spark.halt;

public class IncidentsServer
{
    private final StatusApp app;

    public IncidentsServer(StatusApp app)
    {
        this.app = app;
        Spark.port(app.getConfig().webhookServerPort);
        Spark.post("/incident/:id/:token", this::webhook);
    }

    private String webhook(Request req, Response res)
    {
        String id = req.params("id");
        String token = req.params("token");
        String userAgent = req.userAgent();

        if(id == null || id.isEmpty() || token == null || token.isEmpty())
            halt(400, "Page ID or Token are empty or null");
        else if(!req.contentType().equals("application/json"))
            halt(400, "Content-Type must be json!");

        if(userAgent == null || !userAgent.startsWith("statuspage.io"))
            halt(401, "Invalid User-Agent");

        String configToken = app.getConfig().incidentWebhooks.get(id);
        if(configToken == null)
            halt(404, "ID Not Found");
        else if(!token.equals(configToken))
            halt(403, "Invalid Token");

        res.status(204);
        handle(req.body(), id);
        return "";
    }

    private void handle(String body, String id)
    {
        JsonObject json = app.getGson().fromJson(body, JsonObject.class);
        StatusApp.LOGGER.debug("Received payload: {}", body);

        if(!json.has("incident"))
            return;

        Config.WatchedPage config = app.getConfig().watchedPages.stream()
                .filter(page -> page.id.equals(id)).findFirst().orElse(null);
        Incident incident = app.getGson().fromJson(json.get("incident"), Incident.class);
        Incident.Update update = incident.updates.stream().findFirst().orElse(null);
        if(update == null)
            return;

        StatusApp.LOGGER.info("Received incident update for {} ({})",
                config == null ? "Unknown" : config.name, id);
        if(config != null)
        {
            try(WebhookClient webhook = app.createClient(config.name, config.incidentsWebhook))
            {
                webhook.send(embed(incident, update));
            }
        }

        app.getWatcher().refreshPage(id); // trigger an update
    }

    private WebhookEmbed embed(Incident incident, Incident.Update update)
    {
        IncidentStatus status = update.status;
        String emote = status.getEmote().replaceAll("<a?:\\w+:(\\d+)>", "$1");

        return new WebhookEmbedBuilder()
                .setTitle(new WebhookEmbed.EmbedTitle(incident.name, incident.url))
                .setDescription(update.body)
                .setFooter(new EmbedFooter(status.getName(), String.format(Constants.EMOTE_URL, emote)))
                .setTimestamp(OffsetDateTime.now())
                .setColor(status.getColor())
                .build();
    }
}
