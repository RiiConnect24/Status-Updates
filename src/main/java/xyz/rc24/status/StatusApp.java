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

package xyz.rc24.status;

import club.minnced.discord.webhook.WebhookClient;
import club.minnced.discord.webhook.WebhookClientBuilder;
import club.minnced.discord.webhook.send.AllowedMentions;
import com.google.gson.Gson;
import okhttp3.OkHttpClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xyz.rc24.status.actions.IncidentsServer;
import xyz.rc24.status.actions.StatuspageWatcher;
import xyz.rc24.status.config.Config;
import xyz.rc24.status.config.ConfigLoader;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;

public class StatusApp
{
    private final Config config;
    private final Gson gson;
    private final OkHttpClient httpClient;
    private final Map<String, Webhooks> clients;
    private final ExecutorService threadPool;

    public StatusApp()
    {
        this.config = ConfigLoader.load();
        this.gson = new Gson();
        this.httpClient = new OkHttpClient();
        this.clients = new HashMap<>();
        this.threadPool = Executors.newFixedThreadPool(config.watchedPages.size());

        for(Config.WatchedPage page : config.watchedPages)
        {
            WebhookClient status = createClient(page.name, page.statusWebhook);
            WebhookClient incidents = createClient(page.name, page.incidentsWebhook);
            clients.put(page.id, new Webhooks(incidents, status));
        }

        // Start task
        Executors.newSingleThreadScheduledExecutor()
                .scheduleWithFixedDelay(new StatuspageWatcher(this), 5, 5 * 60, TimeUnit.SECONDS);

        new IncidentsServer(this);
    }

    public Config getConfig()
    {
        return config;
    }

    public Gson getGson()
    {
        return gson;
    }

    public OkHttpClient getHttpClient()
    {
        return httpClient;
    }

    public Map<String, Webhooks> getClients()
    {
        return clients;
    }

    public ExecutorService getThreadPool()
    {
        return threadPool;
    }

    public static void main(String[] args)
    {
        LOGGER.info("Initializing...");
        new StatusApp();
    }

    private WebhookClient createClient(String name, String url)
    {
        Matcher m = WebhookClientBuilder.WEBHOOK_PATTERN.matcher(url);
        if(!(m.matches()))
            throw new IllegalArgumentException(name + " has an invalid webhook uRL: " + url);

        return new WebhookClientBuilder(url)
                .setHttpClient(httpClient)
                .setAllowedMentions(AllowedMentions.none())
                .setWait(false)
                .setDaemon(true)
                .build();
    }

    public static class Webhooks
    {
        private final WebhookClient incidents;
        private final WebhookClient status;

        public Webhooks(WebhookClient incidents, WebhookClient status)
        {
            this.incidents = incidents;
            this.status = status;
        }

        public WebhookClient getIncidents()
        {
            return incidents;
        }

        public WebhookClient getStatus()
        {
            return status;
        }
    }

    public static final Logger LOGGER = LoggerFactory.getLogger(StatusApp.class);
}
