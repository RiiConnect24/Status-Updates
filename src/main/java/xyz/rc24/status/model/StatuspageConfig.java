package xyz.rc24.status.model;

import java.util.Map;

public class StatuspageConfig
{
    private String name;
    private String url;
    private String incidentsWebhook;
    private String updatesWebhook;

    private long unresolvedIncidentsMessage;
    private Map<String, Long> componentMessages;

    public String getName()
    {
        return name;
    }

    public String getIncidentsUrl()
    {
        return url + "/api/v2/incidents.json";
    }

    public String getSummaryUrl()
    {
        return url + "/api/v2/summary.json";
    }

    public String getIncidentsWebhook()
    {
        return incidentsWebhook;
    }

    public String getUpdatesWebhook()
    {
        return updatesWebhook;
    }

    public long getUnresolvedIncidentsMessage()
    {
        return unresolvedIncidentsMessage;
    }

    public Map<String, Long> getComponentMessages()
    {
        return componentMessages;
    }
}
