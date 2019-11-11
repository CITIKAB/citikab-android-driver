package com.trioangle.goferdriver.datamodel;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by trioangle on 9/11/18.
 */

public class TripDetailsModel implements Serializable {

    @SerializedName("created_at")
    @Expose
    private String createdAt;
    @SerializedName("day")
    @Expose
    private String day;
    @SerializedName("daily_fare")
    @Expose
    private String dailyFare;

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getDay() {
        return day;
    }

    public void setDay(String day) {
        this.day = day;
    }

    public String getDailyFare() {
        return dailyFare;
    }

    public void setDailyFare(String dailyFare) {
        this.dailyFare = dailyFare;
    }
}
