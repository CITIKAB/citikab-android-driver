package com.trioangle.goferdriver.configs;
/**
 * @package com.trioangle.gofer
 * @subpackage configs
 * @category SessionManager
 * @author Trioangle Product Team
 * @version 1.0
 **/

import android.content.SharedPreferences;

import com.trioangle.goferdriver.network.AppController;

import javax.inject.Inject;

/*****************************************************************
 Session manager to set and get glopal values
 ***************************************************************/
public class SessionManager {
    public @Inject
    SharedPreferences sharedPreferences;

    public SessionManager() {
        AppController.getAppComponent().inject(this);
    }


    public void clearToken() {
        sharedPreferences.edit().putString("token", "").apply();
    }

    public void clearAll() {
        sharedPreferences.edit().clear().apply();
        //setType("");
    }

    public String getToken() {
        return sharedPreferences.getString("token", "");
    }

    public void setToken(String token) {
        sharedPreferences.edit().putString("token", token).apply();
    }

    public String getAccessToken() {
        return sharedPreferences.getString("access_token", "");
    }

    public void setAcesssToken(String access_token) {
        sharedPreferences.edit().putString("access_token", access_token).apply();
    }

    public String getGoogleMapKey() {
        return sharedPreferences.getString("google_map_key", "");
    }

    public void setGoogleMapKey(String google_map_key) {
        sharedPreferences.edit().putString("google_map_key", google_map_key).apply();
    }

    public String getFacebookAppId() {
        return sharedPreferences.getString("fb_id", "");
    }

    public void setFacebookAppId(String fb_id) {
        sharedPreferences.edit().putString("fb_id", fb_id).apply();
    }

    public String getDriverStatus() {
        return sharedPreferences.getString("driverstatus", "");
    }

    public void setDriverStatus(String access_token) {
        sharedPreferences.edit().putString("driverstatus", access_token).apply();
    }

    public String getDriverSignupStatus() {
        return sharedPreferences.getString("driversignupstatus", "");
    }

    public void setDriverSignupStatus(String access_token) {
        sharedPreferences.edit().putString("driversignupstatus", access_token).apply();
    }

    public String getScheduleDateTime() {
        return sharedPreferences.getString("date_time_for_schedule", "");
    }

    public void setScheduleDateTime(String date_time_to_save) {
        sharedPreferences.edit().putString("date_time_for_schedule", date_time_to_save).apply();
    }

    public String getScheduleDate() {
        return sharedPreferences.getString("date_for_schedule", "");
    }

    public void setScheduleDate(String date_for_schedule) {
        sharedPreferences.edit().putString("date_for_schedule", date_for_schedule).apply();
    }

    public String getPresentTime() {
        return sharedPreferences.getString("present_time_for_schedule", "");
    }

    public void setPresentTime(String present_time_for_schedule) {
        sharedPreferences.edit().putString("present_time_for_schedule", present_time_for_schedule).apply();
    }

    public String getCarName() {
        return sharedPreferences.getString("carname", "");
    }

    public void setCarName(String carname) {
        sharedPreferences.edit().putString("carname", carname).apply();
    }

    public String getPushJson() {
        return sharedPreferences.getString("json", "");
    }

    public void setPushJson(String PushJson) {
        sharedPreferences.edit().putString("json", PushJson).apply();
    }

    public String getType() {
        return sharedPreferences.getString("type", "");
    }

    public void setType(String type) {
        sharedPreferences.edit().putString("type", type).apply();
    }

    public String getDeviceType() {
        return sharedPreferences.getString("devicetype", "");
    }

    public void setDeviceType(String devicetype) {
        sharedPreferences.edit().putString("devicetype", devicetype).apply();
    }

    public float getTotalDistance() {
        return sharedPreferences.getFloat("total_distance", 0);
    }

    public void setTotalDistance(float Latitude) {
        sharedPreferences.edit().putFloat("total_distance", Latitude).apply();
    }

    public String getLatitude() {
        return sharedPreferences.getString("latitude", "");
    }

    public void setLatitude(String Latitude) {
        sharedPreferences.edit().putString("latitude", Latitude).apply();
    }

    public String getLongitude() {
        return sharedPreferences.getString("longitude", "");
    }

    public void setLongitude(String longitude) {
        sharedPreferences.edit().putString("longitude", longitude).apply();
    }

    public String getLastLongitude() {
        return sharedPreferences.getString("lastLong", "");
    }

    public void setLastLongitude(String longitude) {
        sharedPreferences.edit().putString("lastLong", longitude).apply();
    }

    public String getLastLatitude() {
        return sharedPreferences.getString("lastLat", "");
    }

    public void setLastLatitude(String longitude) {
        sharedPreferences.edit().putString("lastLat", longitude).apply();
    }

    public String getCurrentLongitude() {
        return sharedPreferences.getString("currentlong", "");
    }

    public void setCurrentLongitude(String longitude) {
        sharedPreferences.edit().putString("currentlong", longitude).apply();
    }

    public String getCurrentLatitude() {
        return sharedPreferences.getString("currentlat", "");
    }

    public void setCurrentLatitude(String longitude) {
        sharedPreferences.edit().putString("currentlat", longitude).apply();
    }

    public String getLanguage() {
        return sharedPreferences.getString("language", "");
    }

    public void setLanguage(String language) {
        sharedPreferences.edit().putString("language", language).apply();
    }

    public String getLanguageCode() {
        return sharedPreferences.getString("languagecode", "en");
    }

    public void setLanguageCode(String languagecode) {
        sharedPreferences.edit().putString("languagecode", languagecode).apply();
    }

    public String getFacebookId() {
        return sharedPreferences.getString("facebookid", "");
    }

    public void setFacebookId(String languagecode) {
        sharedPreferences.edit().putString("facebookid", languagecode).apply();
    }

    public String getGoogleId() {
        return sharedPreferences.getString("googleid", "");
    }

    public void setGoogleId(String languagecode) {
        sharedPreferences.edit().putString("googleid", languagecode).apply();
    }

    public String getProfilepicture() {
        return sharedPreferences.getString("profilepicture", "");
    }

    public void setProfilepicture(String gender) {
        sharedPreferences.edit().putString("profilepicture", gender).apply();
    }

    public String getGender() {
        return sharedPreferences.getString("gender", "");
    }

    public void setGender(String gender) {
        sharedPreferences.edit().putString("gender", gender).apply();
    }

    public String getCurrency() {
        return sharedPreferences.getString("currency", "");
    }

    public void setCurrency(String currency) {
        sharedPreferences.edit().putString("currency", currency).apply();
    }

    public String getCountryCurrencyType() {
        return sharedPreferences.getString("setCountryCurrencyType", "");
    }

    public void setCountryCurrencyType(String setCountryCurrencyType) {
        sharedPreferences.edit().putString("setCountryCurrencyType", setCountryCurrencyType).apply();
    }

    public String getCountry() {
        return sharedPreferences.getString("country", "");
    }

    public void setCountry(String country) {
        sharedPreferences.edit().putString("country", country).apply();
    }

    public String getFirstName() {
        return sharedPreferences.getString("firstname", "");
    }

    public void setFirstName(String firstName) {
        sharedPreferences.edit().putString("firstname", firstName).apply();
    }

    public String getLastName() {
        return sharedPreferences.getString("lastname", "");
    }

    public void setLastName(String lastName) {
        sharedPreferences.edit().putString("lastname", lastName).apply();
    }

    public void setEmail(String email) {
        sharedPreferences.edit().putString("email", email).apply();
    }

    public String getemail() {
        return sharedPreferences.getString("email", "");
    }

    public String getPassword() {
        return sharedPreferences.getString("password", "");
    }

    public void setPassword(String password) {
        sharedPreferences.edit().putString("password", password).apply();
    }

    public String getPhoneNumber() {
        return sharedPreferences.getString("phoneNumber", "");
    }

    public void setPhoneNumber(String phoneNumber) {
        sharedPreferences.edit().putString("phoneNumber", phoneNumber).apply();
    }

    public void setTemporaryPhonenumber(String phoneNumber) {
        sharedPreferences.edit().putString("TemporaryPhonenumber", phoneNumber).apply();
    }

    public String getTemporaryPhonenumber() {
        return sharedPreferences.getString("TemporaryPhonenumber", "");
    }

    public String getTemporaryCountryCode() {
        return sharedPreferences.getString("TemporaryCountryCode", "");
    }

    public void setTemporaryCountryCode(String countryCode) {
        sharedPreferences.edit().putString("TemporaryCountryCode", countryCode).apply();
    }


    public String getCountryCode() {
        return sharedPreferences.getString("countryCode", "");
    }

    public void setCountryCode(String countryCode) {
        sharedPreferences.edit().putString("countryCode", countryCode).apply();
    }

    public String getDeviceId() {
        return sharedPreferences.getString("deviceId", "");
    }

    public void setDeviceId(String deviceId) {
        sharedPreferences.edit().putString("deviceId", deviceId).apply();
    }

    public String getTripId() {
        return sharedPreferences.getString("tripId", "");
    }

    public void setTripId(String tripId) {
        sharedPreferences.edit().putString("tripId", tripId).apply();
    }

    public void clearTripID() {
        sharedPreferences.edit().remove("tripId").apply();
    }

    public Integer getIsUpdateLocation() {
        return sharedPreferences.getInt("isupldatelocation", 0);
    }

    public void setIsUpdateLocation(Integer isupldatelocation) {
        sharedPreferences.edit().putInt("isupldatelocation", isupldatelocation).apply();
    }

    public Integer getSignuptype() {
        return sharedPreferences.getInt("signuptype", 0);
    }

    public void setSignuptype(Integer signuptype) {
        sharedPreferences.edit().putInt("signuptype", signuptype).apply();
    }

    public Integer getIssignin() {
        return sharedPreferences.getInt("issignin", 0);
    }

    public void setIssignin(Integer issignin) {
        sharedPreferences.edit().putInt("issignin", issignin).apply();
    }

    public String getTripStatus() {
        return sharedPreferences.getString("tripStatus", "");
    }

    public void setTripStatus(String tripStatus) {
        sharedPreferences.edit().putString("tripStatus", tripStatus).apply();
    }

    public void clearTripStatus() {
        sharedPreferences.edit().remove("tripStatus").apply();
    }

    public void setDriverAndRiderAbleToChat(Boolean status) {
        sharedPreferences.edit().putBoolean("setDriverAndRiderAbleToChat", status).apply();
    }

    public Boolean isDriverAndRiderAbleToChat() {
        return sharedPreferences.getBoolean("setDriverAndRiderAbleToChat", false);
    }


    public String getSubTripStatus() {
        return sharedPreferences.getString("SubTripStatus", "");
    }

    public void setSubTripStatus(String SubTripStatus) {
        sharedPreferences.edit().putString("SubTripStatus", SubTripStatus).apply();
    }

    public String getPromoDetail() {
        return sharedPreferences.getString("PromoDetail", "");
    }

    public void setPromoDetail(String PromoDetail) {
        sharedPreferences.edit().putString("PromoDetail", PromoDetail).apply();
    }

    public String getVehicleId() {
        return sharedPreferences.getString("vehicleId", "");
    }

    public void setVehicleId(String vehicleId) {
        sharedPreferences.edit().putString("vehicleId", vehicleId).apply();
    }


    public String getDoc1() {
        return sharedPreferences.getString("doc1", "");
    }

    public void setDoc1(String doc1) {
        sharedPreferences.edit().putString("doc1", doc1).apply();
    }

    public String getDoc2() {
        return sharedPreferences.getString("doc2", "");
    }

    public void setDoc2(String doc2) {
        sharedPreferences.edit().putString("doc2", doc2).apply();
    }

    public String getDoc3() {
        return sharedPreferences.getString("doc3", "");
    }

    public void setDoc3(String doc3) {
        sharedPreferences.edit().putString("doc3", doc3).apply();
    }

    public String getDoc4() {
        return sharedPreferences.getString("doc4", "");
    }

    public void setDoc4(String doc4) {
        sharedPreferences.edit().putString("doc4", doc4).apply();
    }

    public String getDoc5() {
        return sharedPreferences.getString("doc5", "");
    }

    public void setDoc5(String doc5) {
        sharedPreferences.edit().putString("doc5", doc5).apply();
    }

    public String getCurrencyCode() {
        return sharedPreferences.getString("currencyCode", "");
    }

    public void setCurrencyCode(String currencyCode) {
        sharedPreferences.edit().putString("currencyCode", currencyCode).apply();
    }

    public String getCurrencySymbol() {
        return sharedPreferences.getString("currencysymbol", "");
    }

    public void setCurrencySymbol(String currencySymbol) {
        sharedPreferences.edit().putString("currencysymbol", currencySymbol).apply();
    }

    public String getHomeAddress() {
        return sharedPreferences.getString("homeadresstext", "");
    }

    public void setHomeAddress(String homeadresstext) {
        sharedPreferences.edit().putString("homeadresstext", homeadresstext).apply();
    }

    public String getCity() {
        return sharedPreferences.getString("city", "");
    }

    public void setCity(String city) {
        sharedPreferences.edit().putString("city", city).apply();
    }

    public String getWorkAddress() {
        return sharedPreferences.getString("workadresstext", "");
    }

    public void setWorkAddress(String workadresstext) {
        sharedPreferences.edit().putString("workadresstext", workadresstext).apply();
    }

    public String getProfileDetail() {
        return sharedPreferences.getString("profilearratdetail", "");
    }

    public void setProfileDetail(String profilearratdetail) {
        sharedPreferences.edit().putString("profilearratdetail", profilearratdetail).apply();
    }


    public String getPaymentMethod() {
        return sharedPreferences.getString("paymentMethod", "");
    }

    public void setPaymentMethod(String paymentMethod) {
        sharedPreferences.edit().putString("paymentMethod", paymentMethod).apply();
    }

    public String getWalletPaymentMethod() {
        return sharedPreferences.getString("walletpaymentmethod", "");
    }

    public void setWalletPaymentMethod(String walletpaymentmethod) {
        sharedPreferences.edit().putString("walletpaymentmethod", walletpaymentmethod).apply();
    }

    public String getCardValue() {
        return sharedPreferences.getString("cardValue", "");
    }

    public void setCardValue(String cardValue) {
        sharedPreferences.edit().putString("cardValue", cardValue).apply();
    }

    public String getCardBrand() {
        return sharedPreferences.getString("cardBrand", "");
    }

    public void setCardBrand(String cardBrand) {
        sharedPreferences.edit().putString("cardBrand", cardBrand).apply();
    }

    public String getCarType() {
        return sharedPreferences.getString("CarType", "");
    }

    public void setCarType(String CarType) {
        sharedPreferences.edit().putString("CarType", CarType).apply();
    }

    public String getOweAmount() {
        return sharedPreferences.getString("oweAmount", "");
    }

    public void setOweAmount(String oweAmount) {
        sharedPreferences.edit().putString("oweAmount", oweAmount).apply();
    }

    public String getPaypalAppId() {
        return sharedPreferences.getString("paypal_app_id", "");
    }

    public void setPaypalAppId(String paypal_app_id) {
        sharedPreferences.edit().putString("paypal_app_id", paypal_app_id).apply();
    }

    public String getPaypalEmail() {
        return sharedPreferences.getString("PaypalEmail", "");
    }

    public void setPaypalEmail(String PaypalEmail) {
        sharedPreferences.edit().putString("PaypalEmail", PaypalEmail).apply();
    }

    public String getDocCount() {
        return sharedPreferences.getString("imagecount", "");
    }

    public void setDocCount(String imagecount) {
        sharedPreferences.edit().putString("imagecount", imagecount).apply();
    }

    public Boolean getIsRegister() {
        return sharedPreferences.getBoolean("IsRegister", false);
    }

    public void setIsRegister(boolean IsRegister) {
        sharedPreferences.edit().putBoolean("IsRegister", IsRegister).apply();
    }

    public Boolean getIsStripe() {
        return sharedPreferences.getBoolean("isStripe", false);
    }

    public void setIsStripe(boolean isStripe) {
        sharedPreferences.edit().putBoolean("isStripe", isStripe).apply();
    }

    public Boolean getIsrequest() {
        return sharedPreferences.getBoolean("isrequest", false);
    }

    public void setIsrequest(boolean isrequest) {
        sharedPreferences.edit().putBoolean("isrequest", isrequest).apply();
    }

    public Boolean getIsTrip() {
        return sharedPreferences.getBoolean("istrip", false);
    }

    public void setIsTrip(boolean istrip) {
        sharedPreferences.edit().putBoolean("istrip", istrip).apply();
    }

    public Boolean getisEdit() {
        return sharedPreferences.getBoolean("isEdit", false);
    }

    public void setisEdit(boolean isEdit) {
        sharedPreferences.edit().putBoolean("isEdit", isEdit).apply();
    }

    public Boolean getIsWallet() {
        return sharedPreferences.getBoolean("isWallet", false);
    }

    public void setIsWallet(boolean isWallet) {
        sharedPreferences.edit().putBoolean("isWallet", isWallet).apply();
    }

    public Boolean getIsContact() {
        return sharedPreferences.getBoolean("IsContact", false);
    }

    public void setIsContact(boolean IsContact) {
        sharedPreferences.edit().putBoolean("IsContact", IsContact).apply();
    }


    public void setRiderProfilePic(String url) {
        sharedPreferences.edit().putString("riderProfilePic", url).apply();
    }

    public String getRiderProfilePic() {
        return sharedPreferences.getString("riderProfilePic", "");
    }


    public void setRiderRating(String ratingvalue) {
        sharedPreferences.edit().putString("ratingValue", ratingvalue).apply();
    }

    public String getRiderRating() {
        return sharedPreferences.getString("ratingValue", "");
    }

    public void setRiderName(String drivername) {
        sharedPreferences.edit().putString("riderName", drivername).apply();
    }

    public String getRiderName() {
        return sharedPreferences.getString("riderName", "");

    }

    public void clearRiderNameRatingAndProfilePicture() {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.remove("ratingValue");
        editor.remove("riderName");
        editor.remove("riderProfilePic");
        editor.apply();
    }


    public String getCountryName2() {
        return sharedPreferences.getString("countryname2", "");
    }


    public void setCountryName2(String currencyName2) {
        sharedPreferences.edit().putString("countryname2", currencyName2).apply();
    }

    public String getCountryName() {
        return sharedPreferences.getString("countryname", "");
    }


    public void setCountryName(String currencyName) {
        sharedPreferences.edit().putString("countryname", currencyName).apply();
    }

    public String getCurrencyName2() {
        return sharedPreferences.getString("currencyname2", "");
    }


    public void setCurrencyName2(String currencyName2) {
        sharedPreferences.edit().putString("currencyname2", currencyName2).apply();
    }

    public String getStripeCountryCode() {
        return sharedPreferences.getString("StripeCountryCode", "");
    }


    public void setStripeCountryCode(String stripeCountryCode) {
        sharedPreferences.edit().putString("StripeCountryCode", stripeCountryCode).apply();
    }

    public String getPayPalCountryCode() {
        return sharedPreferences.getString("paypalcountrycode", "");
    }


    public void setPayPalCountryCode(String payPalCountryCode) {
        sharedPreferences.edit().putString("paypalcountrycode", payPalCountryCode).apply();
    }

    public void setBookingType(String bookingType) {
        sharedPreferences.edit().putString("bookingType", bookingType).apply();
    }

    public String getBookingType() {
        return sharedPreferences.getString("bookingType", "");
    }

    public void setDialogMessage(String DialogMessage) {
        sharedPreferences.edit().putString("DialogMessage", DialogMessage).apply();
    }

    public String getDialogMessage() {
        return sharedPreferences.getString("DialogMessage", "");
    }

    public String getUserType() {
        return sharedPreferences.getString("UserType", "");
    }

    public void setUserType(String UserType) {
        sharedPreferences.edit().putString("UserType", UserType).apply();
    }
}
