package xyz.rc24.status;

import java.time.OffsetDateTime;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

class ServiceStatus
{
    final StatusEnum status;
    final String statusDescription;
    final OffsetDateTime lastUpdate;
    final List<Component> components;
    final Map<String, Group> groups;
    Incident latestIncident;

    ServiceStatus(String indicator, String description, String lastUpdate)
    {
        this.status = StatusEnum.fromIndicator(indicator);
        this.statusDescription = description;
        this.lastUpdate = OffsetDateTime.parse(lastUpdate);
        this.components = new LinkedList<>();
        this.groups = new LinkedHashMap<>();
    }

    void addComponent(String name, String status)
    {
        this.components.add(new Component(name, status));
    }

    void addGroup(String id, String name, String status, List<Component> components)
    {
        this.groups.put(id, new Group(name, status, components));
    }

    void setIncident(String name, String id, String status, String body, String time)
    {
        this.latestIncident = new Incident(name, id, status, body, time);
    }

    public static class Incident
    {
        final String name;
        final String id;
        final String status;
        final String body;
        final OffsetDateTime time;

        private Incident(String name, String id, String status, String body, String time)
        {
            this.name = name;
            this.id = id;
            this.status = status;
            this.body = body;
            this.time = OffsetDateTime.parse(time);
        }

        public String getURL()
        {
            return StatuspageAPI.CLICKABLE + id;
        }

        public StatusEnum getStatus()
        {
            return StatusEnum.fromIncident(status);
        }
    }

    static class Component
    {
        final String name;
        final String status;

        private Component(String name, String status)
        {
            this.name = name;
            this.status = status;
        }

        StatusEnum getStatus()
        {
            return StatusEnum.fromComponent(status);
        }
    }

    static class Group
    {
        final String name;
        final String status;
        final List<Component> components;

        private Group(String name, String status, List<Component> components)
        {
            this.name = name;
            this.status = status;
            this.components = components;
        }

        void addComponent(String name, String status)
        {
            components.add(new Component(name, status));
        }

        StatusEnum getStatus()
        {
            return StatusEnum.fromComponent(status);
        }
    }
}
