package xyz.rc24.status.model;

import com.google.gson.annotations.SerializedName;
import xyz.rc24.status.Constants;

import java.sql.Timestamp;
import java.util.List;

public class Incident
{
    @SerializedName("name")
    public String name;

    @SerializedName("shortlink")
    public String url;

    @SerializedName("created_at")
    public Timestamp createdAt;

    @SerializedName("impact")
    public Impact impact;

    @SerializedName("incident_updates")
    public List<Update> updates;

    public static class Update
    {
        @SerializedName("status")
        public Status status;

        @SerializedName("body")
        public String body;

        @SerializedName("updated_at")
        public Timestamp updatedAt;

        @Override
        public String toString()
        {
            return "Update{" + "status=" + status + ", body='" + body + '\'' + ", updatedAt=" + updatedAt + '}';
        }
    }

    public enum Impact
    {
        @SerializedName("none")
        NONE(Constants.GREEN_EMOTE, "None"),
        @SerializedName("minor")
        MINOR(Constants.YELLOW_EMOTE, "Minor"),
        @SerializedName("major")
        MAJOR(Constants.RED_EMOTE, "Major"),
        @SerializedName("critical")
        CRITICAL(Constants.RED_EMOTE, "Critical");

        private final String emote;
        private final String name;

        Impact(String emote, String name)
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

    public enum Status
    {
        @SerializedName("investigating")
        INVESTIGATING(Constants.RED, Constants.RED_EMOTE, "Investigating"),
        @SerializedName("identified")
        IDENTIFIED(Constants.YELLOW, Constants.YELLOW_EMOTE, "Identified"),
        @SerializedName("monitoring")
        MONITORING(Constants.YELLOW, Constants.YELLOW_EMOTE, "Monitoring"),
        @SerializedName("resolved")
        RESOLVED(Constants.GREEN, Constants.GREEN_EMOTE, "Resolved");

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
        return "Incident{" + "name='" + name + '\'' + ", createdAt=" + createdAt +
                ", impact=" + impact + ", updates=" + updates + '}';
    }
}
