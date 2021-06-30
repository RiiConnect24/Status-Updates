package xyz.rc24.status.model;

import com.google.gson.annotations.SerializedName;
import org.jetbrains.annotations.Nullable;
import xyz.rc24.status.Constants;

import java.util.List;

public class Component
{
    @SerializedName("id")
    public String id;

    @SerializedName("name")
    public String name;

    @SerializedName("status")
    public Status status;

    @SerializedName("group")
    public boolean isGroup;

    @SerializedName("components")
    public @Nullable List<String> childComponents;

    public enum Status
    {
        @SerializedName("operational")
        OPERATIONAL(Constants.GREEN, Constants.GREEN_EMOTE, "Operational"),
        @SerializedName("degraded_performance")
        DEGRADED_PERFORMANCE(Constants.YELLOW, Constants.YELLOW_EMOTE, "Degraded Performance"),
        @SerializedName("partial_outage")
        PARTIAL_OUTAGE(Constants.YELLOW, Constants.YELLOW_EMOTE, "Partial Outage"),
        @SerializedName("major_outage")
        MAJOR_OUTAGE(Constants.RED, Constants.RED_EMOTE, "Major Outage"),
        @SerializedName("under_maintenance")
        UNDER_MAINTENANCE(Constants.BLACK, Constants.GRAY_EMOTE, "Under Maintenance");

        private final int color;
        private final String emote;
        private final String name;

        Status(int color, String emote, String name)
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

    @Override
    public String toString()
    {
        return "Component{" + "name='" + name + '\'' + ", status=" + status + ", isGroup=" + isGroup +
                ", childComponents=" + childComponents + '}';
    }
}
