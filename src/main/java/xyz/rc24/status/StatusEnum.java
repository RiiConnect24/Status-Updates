package xyz.rc24.status;

import java.awt.Color;

enum StatusEnum
{
    STREAMING("334859814771359744", "#593695"),
    ONLINE("334859814410911745", "#43b581"),
    IDLE("334859813869584384", "#faa61a"),
    DND("334859814029099008", "#f04747"),
    OFFLINE("334859814423232514", "#000000");

    public final Color color;
    public final String emoteId;

    StatusEnum(String emoteId, String color)
    {
        this.color = Color.decode(color);
        this.emoteId = emoteId;
    }

    public String getEmoteLink()
    {
        return "https://cdn.discordapp.com/emojis/" + emoteId + ".png";
    }

    public String getEmote()
    {
        return "<:c:" + emoteId + ">";
    }

    public static StatusEnum fromIndicator(String indicator)
    {
        switch(indicator)
        {
            case "none":
                return ONLINE;
            case "minor":
                return IDLE;
            case "major":
                return DND;
            case "critical":
                return OFFLINE;
            default:
                return ONLINE;
        }
    }

    public static StatusEnum fromComponent(String component)
    {
        switch(component)
        {
            case "operational":
                return ONLINE;
            case "degraded_performance":
			case "under_maintenance":
                return IDLE;
            case "partial_outage":
                return DND;
            case "major_outage":
                return OFFLINE;
            default:
                return ONLINE;
        }
    }

    public static StatusEnum fromIncident(String incident)
    {
        switch(incident.toLowerCase())
        {
            case "investigating":
                return DND;
            case "identified":
                return IDLE;
            case "monitoring":
                return IDLE;
            case "resolved":
                return ONLINE;
            case "postmortem":
                return STREAMING;
            default:
                return STREAMING;
        }
    }

    public static String textFormat(String text)
    {
        return text.substring(0, 1).toUpperCase() + text.replace("_", " ").substring(1);
    }
}
