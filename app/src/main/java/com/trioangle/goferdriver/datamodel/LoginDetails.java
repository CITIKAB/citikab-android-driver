package com.trioangle.goferdriver.datamodel;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by trioangle on 9/7/18.
 */


public class LoginDetails implements Serializable {

    @SerializedName("status_message")
    @Expose
    private String statusMessage;
    @SerializedName("status_code")
    @Expose
    private String statusCode;
    @SerializedName("access_token")
    @Expose
    private String token;
    @SerializedName("car_detais")
    @Expose
    private ArrayList<CarDetails> carDetailModel;
    @SerializedName("user_status")
    @Expose
    private String userStatus;
    @SerializedName("currency_symbol")
    @Expose
    private String currencySymbol;
    @SerializedName("currency_code")
    @Expose
    private String currencyCode;
    @SerializedName("vehicle_id")
    @Expose
    private String vehicleId;
    @SerializedName("payout_id")
    @Expose
    private String payoutId;
    @SerializedName("google_map_key")
    @Expose
    private String googleMapKey;
    @SerializedName("fb_id")
    @Expose
    private String fbId;

    @SerializedName("country_code")
    @Expose
    private String countryCode;

    @SerializedName("mobile_number")
    @Expose
    private String mobileNumber;

    @SerializedName("company_id")
    @Expose
    private String companyId;



    public String getCountryCode() {
        return countryCode;
    }

    public void setCountryCode(String countryCode) {
        this.countryCode = countryCode;
    }

    public String getMobileNumber() {
        return mobileNumber;
    }

    public void setMobileNumber(String mobileNumber) {
        this.mobileNumber = mobileNumber;
    }

    public String getGoogleMapKey() {
        return googleMapKey;
    }

    public void setGoogleMapKey(String googleMapKey) {
        this.googleMapKey = googleMapKey;
    }

    public String getFbId() {
        return fbId;
    }

    public void setFbId(String fbId) {
        this.fbId = fbId;
    }

    public String getPayoutId() {
        return payoutId;
    }

    public void setPayoutId(String payoutId) {
        this.payoutId = payoutId;
    }

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

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public ArrayList<CarDetails> getCarDetailModel() {
        return carDetailModel;
    }

    public void setCarDetailModel(ArrayList<CarDetails> carDetailModel) {
        this.carDetailModel = carDetailModel;
    }

    public String getUserStatus() {
        return userStatus;
    }

    public void setUserStatus(String userStatus) {
        this.userStatus = userStatus;
    }

    public String getCurrencySymbol() {
        return currencySymbol;
    }

    public void setCurrencySymbol(String currencySymbol) {
        this.currencySymbol = currencySymbol;
    }

    public String getCurrencyCode() {
        return currencyCode;
    }

    public void setCurrencyCode(String currencyCode) {
        this.currencyCode = currencyCode;
    }

    public String getVehicleId() {
        return vehicleId;
    }

    public void setVehicleId(String vehicleId) {
        this.vehicleId = vehicleId;
    }

    public String getCompanyId() {
        return companyId;
    }

    public void setCompanyId(String companyId) {
        this.companyId = companyId;
    }
}