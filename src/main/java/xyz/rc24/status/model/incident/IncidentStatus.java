package xyz.rc24.status.model.incident;

import com.google.gson.annotations.SerializedName;
import xyz.rc24.status.Constants;

public enum IncidentStatus
{
    // Incident
    @SerializedName("investigating")
    INVESTIGATING(Constants.RED, Constants.RED_EMOTE, "Investigating"),
    @SerializedName("identified")
    IDENTIFIED(Constants.YELLOW, Constants.YELLOW_EMOTE, "Identified"),
    @SerializedName("monitoring")
    MONITORING(Constants.YELLOW, Constants.YELLOW_EMOTE, "Monitoring"),
    @SerializedName("resolved")
    RESOLVED(Constants.GREEN, Constants.GREEN_EMOTE, "Resolved"),

    // Maintenance
    @SerializedName("scheduled")
    SCHEDULED(Constants.PURPLE, Constants.PURPLE_EMOTE, "Scheduled Maintenance"),
    @SerializedName("in_progress")
    IN_PROGRESS(Constants.RED, Constants.RED_EMOTE, "In Progress"),
    @SerializedName("verifying")
    VERIFYING(Constants.YELLOW, Constants.YELLOW_EMOTE, "Verifying"),
    @SerializedName("completed")
    COMPLETED(Constants.GREEN, Constants.GREEN_EMOTE, "Completed");

    private final int color;
    private final String emote;
    private final String name;

    IncidentStatus(int color, String emote, String name)
    {
        this.color = color;
        this.emote = emote;
        this.name = name;
    }

    public int getColor()
    {
        return color;
    }

    public String getEmote()
    {
        return emote;
    }

    public String getName()
    {
        return name;
    }
}
