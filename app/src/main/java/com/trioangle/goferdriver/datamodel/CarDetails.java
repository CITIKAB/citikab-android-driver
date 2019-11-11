package com.trioangle.goferdriver.datamodel;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by trioangle on 9/7/18.
 */

public class CarDetails implements Serializable {

    @SerializedName("id")
    @Expose
    private Integer id;

    @SerializedName("car_name")
    @Expose
    private String carName;

    @SerializedName("description")
    @Expose
    private String description;

    @SerializedName("base_fare")
    @Expose
    private String baseFare;

    @SerializedName("capacity")
    @Expose
    private Integer capacity;
    @SerializedName("min_fare")
    @Expose
    private String minFare;

    @SerializedName("per_min")
    @Expose
    private String perMin;

    @SerializedName("per_km")
    @Expose
    private String perKm;

    @SerializedName("schedule_fare")
    @Expose
    private String scheduleFare;

    @SerializedName("schedule_cancel_fare")
    @Expose
    private String scheduleCancelFare;

    @SerializedName("currency_code")
    @Expose
    private String currencyCode;

    @SerializedName("status")
    @Expose
    private String status;

    @SerializedName("currency_symbol")
    @Expose
    private String currencySymbol;

    @SerializedName("original_currency_code")
    @Expose
    private String originalCurrencyCode;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getCarName() {
        return carName;
    }

    public void setCarName(String carName) {
        this.carName = carName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getBaseFare() {
        return baseFare;
    }

    public void setBaseFare(String baseFare) {
        this.baseFare = baseFare;
    }

    public Integer getCapacity() {
        return capacity;
    }

    public void setCapacity(Integer capacity) {
        this.capacity = capacity;
    }

    public String getMinFare() {
        return minFare;
    }

    public void setMinFare(String minFare) {
        this.minFare = minFare;
    }

    public String getPerMin() {
        return perMin;
    }

    public void setPerMin(String perMin) {
        this.perMin = perMin;
    }

    public String getPerKm() {
        return perKm;
    }

    public void setPerKm(String perKm) {
        this.perKm = perKm;
    }

    public String getScheduleFare() {
        return scheduleFare;
    }

    public void setScheduleFare(String scheduleFare) {
        this.scheduleFare = scheduleFare;
    }

    public String getScheduleCancelFare() {
        return scheduleCancelFare;
    }

    public void setScheduleCancelFare(String scheduleCancelFare) {
        this.scheduleCancelFare = scheduleCancelFare;
    }

    public String getCurrencyCode() {
        return currencyCode;
    }

    public void setCurrencyCode(String currencyCode) {
        this.currencyCode = currencyCode;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getCurrencySymbol() {
        return currencySymbol;
    }

    public void setCurrencySymbol(String currencySymbol) {
        this.currencySymbol = currencySymbol;
    }

    public String getOriginalCurrencyCode() {
        return originalCurrencyCode;
    }

    public void setOriginalCurrencyCode(String originalCurrencyCode) {
        this.originalCurrencyCode = originalCurrencyCode;
    }
}