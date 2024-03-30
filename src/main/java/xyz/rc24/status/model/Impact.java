package xyz.rc24.status.model;

import com.google.gson.annotations.SerializedName;
import xyz.rc24.status.Constants;

public enum Impact
{
    @SerializedName("none")
    NONE(Constants.GREEN, Constants.GREEN_EMOTE, "None"),
    @SerializedName("maintenance")
    MAINTENANCE(Constants.PURPLE, Constants.PURPLE_EMOTE, "Under Maintenance"),
    @SerializedName("minor")
    MINOR(Constants.YELLOW, Constants.YELLOW_EMOTE, "Minor"),
    @SerializedName("major")
    MAJOR(Constants.RED, Constants.RED_EMOTE, "Major"),
    @SerializedName("critical")
    CRITICAL(Constants.BLACK, Constants.GRAY_EMOTE, "Critical");

    private final int color;
    private final String emote;
    private final String name;

    Impact(int color, String emote, String name)
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
