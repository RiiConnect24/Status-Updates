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

package xyz.rc24.status.model.component;

import com.google.gson.annotations.SerializedName;
import xyz.rc24.status.Constants;

public enum ComponentStatus
{
    @SerializedName("operational")
    OPERATIONAL(Constants.GREEN, Constants.GREEN_EMOTE, "Operational"),
    @SerializedName("degraded_performance")
    DEGRADED_PERFORMANCE(Constants.YELLOW, Constants.YELLOW_EMOTE, "Degraded Performance"),
    @SerializedName("partial_outage")
    PARTIAL_OUTAGE(Constants.RED, Constants.RED_EMOTE, "Partial Outage"),
    @SerializedName("major_outage")
    MAJOR_OUTAGE(Constants.BLACK, Constants.GRAY_EMOTE, "Major Outage"),
    @SerializedName("under_maintenance")
    UNDER_MAINTENANCE(Constants.PURPLE, Constants.PURPLE_EMOTE, "Under Maintenance");

    private final int color;
    private final String emote;
    private final String name;

    ComponentStatus(int color, String emote, String name)
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
