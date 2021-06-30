package xyz.rc24.status.model;

import com.google.gson.annotations.SerializedName;
import xyz.rc24.status.Constants;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Statuspage
{
    @SerializedName("page.name")
    public String name;

    @SerializedName("page.url")
    public String url;

    @SerializedName("components")
    public List<Component> components;

    @SerializedName("incidents")
    public List<Incident> incidents;

    /*@SerializedName("scheduled_maintenances")
    public List<ScheduledMaintenance> scheduledMaintenances;*/

    @SerializedName("status")
    public Status status;

    private Map<String, Component> componentsById;

    public Map<String, Component> getComponentsById()
    {
        if(!(componentsById == null))
            return componentsById;

        Map<String, Component> map = new HashMap<>();
        for(Component component : components)
            map.put(component.id, component);

        this.componentsById = map;
        return map;
    }

    public static class Status
    {
        @SerializedName("indicator")
        public String indicator;

        @SerializedName("description")
        public String description;

        public int getColor()
        {
            switch(indicator)
            {
                case "none":
                    return Constants.GREEN;
                case "minor":
                    return Constants.YELLOW;
                case "major":
                case "critical":
                    return Constants.RED;
            }

            return Constants.BLACK;
        }

        public String getEmote()
        {
            switch(indicator)
            {
                case "none":
                    return Constants.GREEN_EMOTE;
                case "minor":
                    return Constants.YELLOW_EMOTE;
                case "major":
                case "critical":
                    return Constants.RED_EMOTE;
            }

            return "";
        }

        @Override
        public String toString()
        {
            return "Status{" + "indicator='" + indicator + '\'' + ", description='" + description + '\'' + '}';
        }
    }

    @Override
    public String toString()
    {
        return "Statuspage{" + "name='" + name + '\'' + ", url='" + url + '\'' + ", components=" +
                components + ", incidents=" + incidents + ", status=" + status + '}';
    }
}
