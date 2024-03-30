package xyz.rc24.status.model.incident;

import com.google.gson.annotations.SerializedName;
import xyz.rc24.status.Constants;

public enum IncidentImpact
{
    @SerializedName("none")
    NONE(Constants.GREEN_EMOTE, "None"),
    @SerializedName("maintenance")
    MAINTENANCE(Constants.PURPLE_EMOTE, "Under Maintenance"),
    @SerializedName("minor")
    MINOR(Constants.YELLOW_EMOTE, "Minor"),
    @SerializedName("major")
    MAJOR(Constants.RED_EMOTE, "Major"),
    @SerializedName("critical")
    CRITICAL(Constants.GRAY_EMOTE, "Critical");

    private final String emote;
    private final String name;

    IncidentImpact(String emote, String name)
    {
        this.emote = emote;
        this.name = name;
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
