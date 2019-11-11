package com.trioangle.goferdriver.datamodel;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by trioangle on 9/12/18.
 */

public class RatingModel implements Serializable {

    @SerializedName("status_message")
    @Expose
    private String statusMessage;
    @SerializedName("status_code")
    @Expose
    private String statusCode;
    @SerializedName("total_rating")
    @Expose
    private String totalRating;
    @SerializedName("total_rating_count")
    @Expose
    private String totalRatingCount;
    @SerializedName("driver_rating")
    @Expose
    private String driverRating;
    @SerializedName("five_rating_count")
    @Expose
    private String fiveRatingCount;

    public String getStatusMessage() {
        return statusMessage;
    }

    public void setStatusMessage(String statusMessage) {
        this.statusMessage = statusMessage;
    }

    public String getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(String statusCode) {
        this.statusCode = statusCode;
    }

    public String getTotalRating() {
        return totalRating;
    }

    public void setTotalRating(String totalRating) {
        this.totalRating = totalRating;
    }

    public String getTotalRatingCount() {
        return totalRatingCount;
    }

    public void setTotalRatingCount(String totalRatingCount) {
        this.totalRatingCount = totalRatingCount;
    }

    public String getDriverRating() {
        return driverRating;
    }

    public void setDriverRating(String driverRating) {
        this.driverRating = driverRating;
    }

    public String getFiveRatingCount() {
        return fiveRatingCount;
    }

    public void setFiveRatingCount(String fiveRatingCount) {
        this.fiveRatingCount = fiveRatingCount;
    }
}
