package com.ddhigh.earthquake;

import android.location.Location;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * @project Study
 * @package com.ddhigh.earthquake
 * @user xialeistudio
 * @date 2016/2/23 0023
 */
public class Quake {
    private Date date;
    private String datails;
    private Location location;
    private double magnitude;
    private String link;

    public Date getDate() {
        return date;
    }

    public String getDefails() {
        return datails;
    }

    public Location getLocation() {
        return location;
    }

    public double getMagnitude() {
        return magnitude;
    }

    public String getLink() {
        return link;
    }

    public Quake(Date date, String datails, Location location, double magnitude, String link) {
        this.date = date;
        this.datails = datails;
        this.location = location;
        this.magnitude = magnitude;
        this.link = link;
    }

    @Override
    public String toString() {
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm", Locale.CHINA);
        String dateStr = sdf.format(date);
        return dateStr + ": " + magnitude + " " + datails;
    }
}
