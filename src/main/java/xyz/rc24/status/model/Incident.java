package xyz.rc24.status.model;

import com.google.gson.annotations.SerializedName;
import xyz.rc24.status.Constants;

import java.sql.Timestamp;
import java.util.List;

public class Incident
{
    @SerializedName("name")
    public String name;

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
        INVESTIGATING("Investigating"),
        @SerializedName("identified")
        IDENTIFIED("Identified"),
        @SerializedName("monitoring")
        MONITORING("Monitoring"),
        @SerializedName("resolved")
        RESOLVED("Resolved");

        private final String name;

        Status(String name)
        {
            this.name = name;
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
