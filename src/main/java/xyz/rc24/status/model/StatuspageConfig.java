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

import java.util.Map;

public class StatuspageConfig
{
    private String name;
    private String url;
    private String incidentsWebhook;
    private String updatesWebhook;

    private long unresolvedIncidentsMessage;
    private Map<String, Long> componentMessages;

    public String getName()
    {
        return name;
    }

    public String getIncidentsUrl()
    {
        return url + "/api/v2/incidents.json";
    }

    public String getSummaryUrl()
    {
        return url + "/api/v2/summary.json";
    }

    public String getIncidentsWebhook()
    {
        return incidentsWebhook;
    }

    public String getUpdatesWebhook()
    {
        return updatesWebhook;
    }

    public long getUnresolvedIncidentsMessage()
    {
        return unresolvedIncidentsMessage;
    }

    public Map<String, Long> getComponentMessages()
    {
        return componentMessages;
    }
}
