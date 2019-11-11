package com.trioangle.goferdriver.datamodel;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by trioangle on 9/12/18.
 */

public class TodayTripModel implements Serializable {

    private String MapPath;

    private String type;
    @SerializedName("total_fare")
    @Expose
    private String totalFare;
    @SerializedName("trip_path")
    @Expose
    private String tripPath;
    @SerializedName("map_image")
    @Expose
    private String mapImage;

    @SerializedName("id")
    @Expose
    private String id;
    @SerializedName("user_id")
    @Expose
    private String userId;
    @SerializedName("pickup_latitude")
    @Expose
    private String pickupLatitude;
    @SerializedName("pickup_longitude")
    @Expose
    private String pickupLongitude;
    @SerializedName("drop_latitude")
    @Expose
    private String dropLatitude;
    @SerializedName("drop_longitude")
    @Expose
    private String dropLongitude;
    @SerializedName("pickup_location")
    @Expose
    private String pickupLocation;
    @SerializedName("drop_location")
    @Expose
    private String dropLocation;
    @SerializedName("car_id")
    @Expose
    private String carId;
    @SerializedName("request_id")
    @Expose
    private String requestId;
    @SerializedName("driver_id")
    @Expose
    private String driverId;
    @SerializedName("total_time")
    @Expose
    private String totalTime;
    @SerializedName("total_km")
    @Expose
    private String totalKm;
    @SerializedName("time_fare")
    @Expose
    private String timeFare;
    @SerializedName("distance_fare")
    @Expose
    private String distanceFare;
    @SerializedName("base_fare")
    @Expose
    private String baseFare;
    @SerializedName("wallet_amount")
    @Expose
    private String walletAmount;
    @SerializedName("applied_owe_amount")
    @Expose
    private String appliedOweAmount;
    @SerializedName("remaining_owe_amount")
    @Expose
    private String remainingOweAmount;
    @SerializedName("owe_amount")
    @Expose
    private String oweAmount;
    @SerializedName("driver_thumb_image")
    @Expose
    private String driverThumbImage;
    @SerializedName("driver_payout")
    @Expose
    private String driverPayout;
    @SerializedName("driver_name")
    @Expose
    private String driverName;
    @SerializedName("deleted_at")
    @Expose
    private String deletedAt;
    @SerializedName("vehicle_name")
    @Expose
    private String vehicleName;
    @SerializedName("updated_at")
    @Expose
    private String updatedAt;
    @SerializedName("created_at")
    @Expose
    private String createdAt;
    @SerializedName("status")
    @Expose
    private String status;
    @SerializedName("access_fee")
    @Expose
    private String accessFee;
    @SerializedName("promo_amount")
    @Expose
    private String promoAmount;
    @SerializedName("payment_method")
    @Expose
    private String paymentMethod;


    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getTripPath() {
        return tripPath;
    }

    public void setTripPath(String tripPath) {
        this.tripPath = tripPath;
    }

    public String getMapImage() {
        return mapImage;
    }

    public void setMapImage(String mapImage) {
        this.mapImage = mapImage;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getPickupLatitude() {
        return pickupLatitude;
    }

    public void setPickupLatitude(String pickupLatitude) {
        this.pickupLatitude = pickupLatitude;
    }

    public String getPickupLongitude() {
        return pickupLongitude;
    }

    public void setPickupLongitude(String pickupLongitude) {
        this.pickupLongitude = pickupLongitude;
    }

    public String getDropLatitude() {
        return dropLatitude;
    }

    public void setDropLatitude(String dropLatitude) {
        this.dropLatitude = dropLatitude;
    }

    public String getDropLongitude() {
        return dropLongitude;
    }

    public void setDropLongitude(String dropLongitude) {
        this.dropLongitude = dropLongitude;
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

    public String getCarId() {
        return carId;
    }

    public void setCarId(String carId) {
        this.carId = carId;
    }

    public String getRequestId() {
        return requestId;
    }

    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }

    public String getDriverId() {
        return driverId;
    }

    public void setDriverId(String driverId) {
        this.driverId = driverId;
    }

    public String getTotalTime() {
        return totalTime;
    }

    public void setTotalTime(String totalTime) {
        this.totalTime = totalTime;
    }

    public String getTotalKm() {
        return totalKm;
    }

    public void setTotalKm(String totalKm) {
        this.totalKm = totalKm;
    }

    public String getTimeFare() {
        return timeFare;
    }

    public void setTimeFare(String timeFare) {
        this.timeFare = timeFare;
    }

    public String getDistanceFare() {
        return distanceFare;
    }

    public void setDistanceFare(String distanceFare) {
        this.distanceFare = distanceFare;
    }

    public String getBaseFare() {
        return baseFare;
    }

    public void setBaseFare(String baseFare) {
        this.baseFare = baseFare;
    }

    public String getWalletAmount() {
        return walletAmount;
    }

    public void setWalletAmount(String walletAmount) {
        this.walletAmount = walletAmount;
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

    public String getOweAmount() {
        return oweAmount;
    }

    public void setOweAmount(String oweAmount) {
        this.oweAmount = oweAmount;
    }

    public String getDriverThumbImage() {
        return driverThumbImage;
    }

    public void setDriverThumbImage(String driverThumbImage) {
        this.driverThumbImage = driverThumbImage;
    }

    public String getDriverPayout() {
        return driverPayout;
    }

    public void setDriverPayout(String driverPayout) {
        this.driverPayout = driverPayout;
    }

    public String getDriverName() {
        return driverName;
    }

    public void setDriverName(String driverName) {
        this.driverName = driverName;
    }

    public String getDeletedAt() {
        return deletedAt;
    }

    public void setDeletedAt(String deletedAt) {
        this.deletedAt = deletedAt;
    }

    public String getVehicleName() {
        return vehicleName;
    }

    public void setVehicleName(String vehicleName) {
        this.vehicleName = vehicleName;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getAccessFee() {
        return accessFee;
    }

    public void setAccessFee(String accessFee) {
        this.accessFee = accessFee;
    }

    public String getPromoAmount() {
        return promoAmount;
    }

    public void setPromoAmount(String promoAmount) {
        this.promoAmount = promoAmount;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public String getMapPath() {
        return MapPath;
    }

    public void setMapPath(String mapPath) {
        MapPath = mapPath;
    }

    public String getTotalFare() {
        return totalFare;
    }

    public void setTotalFare(String totalFare) {
        this.totalFare = totalFare;
    }
}
