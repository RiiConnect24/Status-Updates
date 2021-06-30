package xyz.rc24.status.config;

import java.util.List;

public class Config
{
    public List<WatchedPage> watchedPages;

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
