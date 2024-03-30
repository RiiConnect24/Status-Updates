/*
 * MIT License
 * Copyright (c) 2019-2021 RiiConnect24
 * Copyright (c) 2019-2024 Artuto
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package xyz.rc24.status.model.incident;

import com.google.gson.annotations.SerializedName;
import xyz.rc24.status.model.Impact;

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
        return "Incident{" +
                "name='" + name + '\'' +
                ", url='" + url + '\'' +
                ", createdAt=" + createdAt +
                ", impact=" + impact +
                ", updates=" + updates +
                '}';
    }
}
