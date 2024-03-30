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
import xyz.rc24.status.Constants;

public enum IncidentStatus
{
    // Incident
    @SerializedName("investigating")
    INVESTIGATING(Constants.RED, Constants.RED_EMOTE, "Investigating"),
    @SerializedName("identified")
    IDENTIFIED(Constants.YELLOW, Constants.YELLOW_EMOTE, "Identified"),
    @SerializedName("monitoring")
    MONITORING(Constants.YELLOW, Constants.YELLOW_EMOTE, "Monitoring"),
    @SerializedName("resolved")
    RESOLVED(Constants.GREEN, Constants.GREEN_EMOTE, "Resolved"),

    // Maintenance
    @SerializedName("scheduled")
    SCHEDULED(Constants.PURPLE, Constants.PURPLE_EMOTE, "Scheduled Maintenance"),
    @SerializedName("in_progress")
    IN_PROGRESS(Constants.RED, Constants.RED_EMOTE, "In Progress"),
    @SerializedName("verifying")
    VERIFYING(Constants.YELLOW, Constants.YELLOW_EMOTE, "Verifying"),
    @SerializedName("completed")
    COMPLETED(Constants.GREEN, Constants.GREEN_EMOTE, "Completed");

    private final int color;
    private final String emote;
    private final String name;

    IncidentStatus(int color, String emote, String name)
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
