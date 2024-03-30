package xyz.rc24.status.model;

import com.google.gson.annotations.SerializedName;
import xyz.rc24.status.model.incident.IncidentStatus;

import java.sql.Timestamp;
import java.util.List;

public class ScheduledMaintenance
{
    @SerializedName("name")
    public String name;

    @SerializedName("scheduled_for")
    public Timestamp scheduledFor;

    @SerializedName("status")
    public IncidentStatus status;

    @SerializedName("incident_updates")
    public List<Update> updates;

    public static class Update
    {
        @SerializedName("status")
        public IncidentStatus status;

        @SerializedName("body")
        public String body;

        @Override
        public String toString()
        {
            return "Update{" +
                    "status=" + status +
                    ", body='" + body + '\'' +
                    '}';
        }
    }

    @Override
    public String toString()
    {
        return "ScheduledMaintenance{" +
                "name='" + name + '\'' +
                ", scheduledFor=" + scheduledFor +
                ", status=" + status +
                ", updates=" + updates +
                '}';
    }
}
