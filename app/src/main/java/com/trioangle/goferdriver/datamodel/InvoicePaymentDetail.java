package com.trioangle.goferdriver.datamodel;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class InvoicePaymentDetail {

    @SerializedName("status_message")
    @Expose
    private String status_message;

    @SerializedName("status_code")
    @Expose
    private String status_code;

    @SerializedName("total_time")
    @Expose
    private String totalTime;

    @SerializedName("pickup_location")
    @Expose
    private String pickupLocation;

    @SerializedName("drop_location")
    @Expose
    private String dropLocation;

    @SerializedName("payment_method")
    @Expose
    private String paymentMethod;

    @SerializedName("payment_status")
    @Expose
    private String paymentStatus;

    @SerializedName("applied_owe_amount")
    @Expose
    private String appliedOweAmount;

    @SerializedName("remaining_owe_amount")
    @Expose
    private String remainingOweAmount;

    @SerializedName("total_fare")
    @Expose
    private String totalFare;

    @SerializedName("driver_payout")
    @Expose
    private String driverPayout;

    @SerializedName("paypal_app_id")
    @Expose
    private String paypalAppId;

    @SerializedName("paypal_mode")
    @Expose
    private String paypalMode;

    @SerializedName("promo_amount")
    @Expose
    private String promoAmount;

    @SerializedName("trip_status")
    @Expose
    private String tripStatus;

    @SerializedName("invoice")
    @Expose
    private ArrayList<InvoiceModel> invoice;

    @SerializedName("driver_image")
    @Expose
    private String driverImage;

    @SerializedName("driver_name")
    @Expose
    private String driverName;

    @SerializedName("rider_image")
    @Expose
    private String riderImage;

    @SerializedName("rider_name")
    @Expose
    private String riderName;

    public String getStatus_message() {
        return status_message;
    }

    public void setStatus_message(String status_message) {
        this.status_message = status_message;
    }

    public String getStatus_code() {
        return status_code;
    }

    public void setStatus_code(String status_code) {
        this.status_code = status_code;
    }

    public String getTotalTime() {
        return totalTime;
    }

    public void setTotalTime(String totalTime) {
        this.totalTime = totalTime;
    }

    public String getPickupLocation() {
        return pickupLocation;
    }

    public void setPickupLocation(String pickupLocation) {
        this.pickupLocation = pickupLocation;
    }

    public String getDropLocation() {
        return dropLocation;
    }

    public void setDropLocation(String dropLocation) {
        this.dropLocation = dropLocation;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public String getPaymentStatus() {
        return paymentStatus;
    }

    public void setPaymentStatus(String paymentStatus) {
        this.paymentStatus = paymentStatus;
    }

    public String getAppliedOweAmount() {
        return appliedOweAmount;
    }

    public void setAppliedOweAmount(String appliedOweAmount) {
        this.appliedOweAmount = appliedOweAmount;
    }

    public String getRemainingOweAmount() {
        return remainingOweAmount;
    }

    public void setRemainingOweAmount(String remainingOweAmount) {
        this.remainingOweAmount = remainingOweAmount;
    }

    public String getTotalFare() {
        return totalFare;
    }

    public void setTotalFare(String totalFare) {
        this.totalFare = totalFare;
    }

    public String getDriverPayout() {
        return driverPayout;
    }

    public void setDriverPayout(String driverPayout) {
        this.driverPayout = driverPayout;
    }

    public String getPaypalAppId() {
        return paypalAppId;
    }

    public void setPaypalAppId(String paypalAppId) {
        this.paypalAppId = paypalAppId;
    }

    public String getPaypalMode() {
        return paypalMode;
    }

    public void setPaypalMode(String paypalMode) {
        this.paypalMode = paypalMode;
    }

    public String getPromoAmount() {
        return promoAmount;
    }

    public void setPromoAmount(String promoAmount) {
        this.promoAmount = promoAmount;
    }

    public String getTripStatus() {
        return tripStatus;
    }

    public void setTripStatus(String tripStatus) {
        this.tripStatus = tripStatus;
    }

    public ArrayList<InvoiceModel> getInvoice() {
        return invoice;
    }

    public void setInvoice(ArrayList<InvoiceModel> invoice) {
        this.invoice = invoice;
    }

    public String getDriverImage() {
        return driverImage;
    }

    public void setDriverImage(String driverImage) {
        this.driverImage = driverImage;
    }

    public String getDriverName() {
        return driverName;
    }

    public void setDriverName(String driverName) {
        this.driverName = driverName;
    }

    public String getRiderImage() {
        return riderImage;
    }

    public void setRiderImage(String riderImage) {
        this.riderImage = riderImage;
    }

    public String getRiderName() {
        return riderName;
    }

    public void setRiderName(String riderName) {
        this.riderName = riderName;
    }
}
