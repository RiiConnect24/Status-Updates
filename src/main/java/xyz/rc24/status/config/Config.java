package xyz.rc24.status.config;

import java.util.List;
import java.util.Map;

public class Config
{
    public List<WatchedPage> watchedPages = List.of();

    public int webhookServerPort = 8080;
    public Map<String, String> incidentWebhooks = Map.of();

    public static class WatchedPage
    {
        public String name;
        public String id;
        public String url;
        public String statusWebhook;
        public String incidentsWebhook;

        public long statusMessageId;
    }
}
