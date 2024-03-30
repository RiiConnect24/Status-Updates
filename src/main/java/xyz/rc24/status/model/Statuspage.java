/*
 * MIT License
 *
 * Copyright (c) 2019-2021 RiiConnect24
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package xyz.rc24.status.model;

import com.google.gson.annotations.SerializedName;
import xyz.rc24.status.model.component.Component;
import xyz.rc24.status.model.incident.Incident;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Statuspage
{
    @SerializedName("page.name")
    public String name;

    @SerializedName("components")
    public List<Component> components;

    @SerializedName("incidents")
    public List<Incident> incidents;

    @SerializedName("scheduled_maintenances")
    public List<ScheduledMaintenance> scheduledMaintenances;

    @SerializedName("status")
    public Status status;

    private Map<String, Component> componentsById;

    public Map<String, Component> getComponentsById()
    {
        if(componentsById != null)
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
        public Impact indicator;

        @SerializedName("description")
        public String description;

        @Override
        public String toString()
        {
            return "Status{" +
                    "indicator=" + indicator +
                    ", description='" + description + '\'' +
                    '}';
        }
    }

    @Override
    public String toString()
    {
        return "Statuspage{" +
                "name='" + name + '\'' +
                ", components=" + components +
                ", incidents=" + incidents +
                ", scheduledMaintenances=" + scheduledMaintenances +
                ", status=" + status +
                ", componentsById=" + componentsById +
                '}';
    }
}
