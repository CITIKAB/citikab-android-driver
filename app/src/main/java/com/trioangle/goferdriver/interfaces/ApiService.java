package com.trioangle.goferdriver.interfaces;

import java.util.HashMap;
import java.util.LinkedHashMap;

import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;
import retrofit2.http.QueryMap;

/* ************************************************************
                      ApiService
    Contains all api service call methods
*************************************************************** */

public interface ApiService {


    // Upload Documents image
    @POST("document_upload")
    Call<ResponseBody> uploadDocumentImage(@Body RequestBody RequestBody, @Query("token") String token);

    // Upload Profile image
    @POST("upload_profile_image")
    Call<ResponseBody> uploadProfileImage(@Body RequestBody RequestBody, @Query("token") String token);


    //Login
    @GET("login")
    Call<ResponseBody> login(@Query("mobile_number") String mobilenumber, @Query("user_type") String usertype, @Query("country_code") String countrycode, @Query("password") String password, @Query("device_id") String deviceid, @Query("device_type") String devicetype, @Query("language") String language);


    //Login
    @GET("vehicle_details")
    Call<ResponseBody> vehicleDetails(@Query("vehicle_id") long vehicleid, @Query("vehicle_name") String vehiclename, @Query("vehicle_type") String vehicletype, @Query("vehicle_number") String vehiclenumber, @Query("token") String token);

    //Forgot password
    @GET("forgotpassword")
    Call<ResponseBody> forgotpassword(@Query("mobile_number") String mobile_number,@Query("user_type") String user_type, @Query("country_code") String country_code,  @Query("password") String password, @Query("device_type") String device_type, @Query("device_id") String device_id, @Query("language") String language);


    @GET("add_payout")
    Call<ResponseBody> addPayout(@Query("email_id") String emailId, @Query("user_type") String userType, @Query("token") String token);


    //Cancel trip
    @GET("cancel_trip")
    Call<ResponseBody> cancelTrip(@Query("user_type") String type, @Query("cancel_reason") String cancel_reason, @Query("cancel_comments") String cancel_comments, @Query("trip_id") String trip_id, @Query("token") String token);

    //Forgot password
    @GET("accept_request")
    Call<ResponseBody> acceptRequest(@Query("user_type") String type, @Query("request_id") String request_id, @Query("status") String status, @Query("token") String token);

    //Confirm Arrival
    @GET("cash_collected")
    Call<ResponseBody> cashCollected(@Query("trip_id") String trip_id, @Query("token") String token);

    //Confirm Arrival
    @GET("arive_now")
    Call<ResponseBody> ariveNow(@Query("trip_id") String trip_id, @Query("token") String token);

    //Begin Trip
    @GET("begin_trip")
    Call<ResponseBody> beginTrip(@Query("trip_id") String trip_id, @Query("begin_latitude") String begin_latitude, @Query("begin_longitude") String begin_longitude, @Query("token") String token);

    //End Trip
    @POST("end_trip")
    Call<ResponseBody> endTrip(@Body RequestBody RequestBody);

    /*//End Trip
    @GET("end_trip")
    Call<ResponseBody> endTrip(@Query("trip_id") String trip_id, @Query("end_latitude") String begin_latitude, @Query("end_longitude") String begin_longitude, @Query("token") String token);*/

    //Trip Rating
    @GET("trip_rating")
    Call<ResponseBody> tripRating(@Query("trip_id") String trip_id, @Query("rating") String rating,
                                  @Query("rating_comments") String rating_comments, @Query("user_type") String user_type, @Query("token") String token);


    // Update location with lat,lng and driverStatus
    @GET("updatelocation")
    Call<ResponseBody> updateLocation(@QueryMap HashMap<String, String> hashMap);


    @GET("update_device")
    Call<ResponseBody> updateDevice(@QueryMap HashMap<String, String> hashMap);


    // driverStatus Check
    @GET("check_status")
    Call<ResponseBody> updateCheckStatus(@QueryMap HashMap<String, String> hashMap);

    @GET("earning_chart")
    Call<ResponseBody> updateEarningChart(@QueryMap HashMap<String, String> hashMap);

    @GET("driver_rating")
    Call<ResponseBody> updateDriverRating(@QueryMap HashMap<String, String> hashMap);

    @GET("rider_feedback")
    Call<ResponseBody> updateRiderFeedBack(@QueryMap HashMap<String, String> hashMap);

    @GET("get_rider_profile")
    Call<ResponseBody> getRiderDetails(@QueryMap HashMap<String, String> hashMap);

    //Number Validation
    @GET("register")
    Call<ResponseBody> registerOtp(@Query("user_type") String type, @Query("mobile_number") String mobilenumber, @Query("country_code") String countrycode, @Query("email_id") String emailid, @Query("first_name") String first_name, @Query("last_name") String last_name, @Query("password") String password, @Query("city") String city, @Query("device_id") String device_id, @Query("device_type") String device_type,@Query("language") String languageCode);

    @GET("driver_trips_history")
    Call<ResponseBody> driverTripsHistory(@QueryMap HashMap<String, String> hashMap);

    //Driver Profile
    @GET("get_driver_profile")
    Call<ResponseBody> getDriverProfile(@Query("token") String token);



    //Driver Profile
    @GET("driver_bank_details")
    Call<ResponseBody> updateBankDetails(@QueryMap HashMap<String, String> hashMap);

    //Currency list
    @GET("currency_list")
    Call<ResponseBody> getCurrency(@Query("token") String token);

    //language Update
    @GET("language")
    Call<ResponseBody> language(@Query("language") String languageCode, @Query("token") String token);

    // Update User Currency
    @GET("update_user_currency")
    Call<ResponseBody> updateCurrency(@Query("currency_code") String currencyCode, @Query("token") String token);

    @GET("update_driver_profile")
    Call<ResponseBody> updateDriverProfile(@QueryMap LinkedHashMap<String, String> hashMap);

    //Upload Profile Image
    @POST("upload_image")
    Call<ResponseBody> uploadImage(@Body RequestBody RequestBody, @Query("token") String token);

    //Sign out
    @GET("logout")
    Call<ResponseBody> logout(@Query("user_type") String type, @Query("token") String token);

    //Add payout perference
    @FormUrlEncoded
    @POST("add_payout_preference")
    Call<ResponseBody> addPayoutPreference(@Field("token") String token, @Field("address1") String address1, @Field("address2") String address2, @Field("email") String email, @Field("city") String city, @Field("state") String state, @Field("country") String country, @Field("postal_code") String postal_code, @Field("payout_method") String payout_method);

    //Payout Details
    @GET("payout_details")
    Call<ResponseBody> payoutDetails(@Query("token") String token);

    //Get Country List
    @GET("country_list")
    Call<ResponseBody> getCountryList(@Query("token") String token);

    //List of Stripe Supported Countries
    @GET("stripe_supported_country_list")
    Call<ResponseBody> stripeSupportedCountry(@Query("token") String token);

    //Get pre_payment
    @GET("payout_changes")
    Call<ResponseBody> payoutChanges(@Query("token") String token, @Query("payout_id") String payout_id, @Query("type") String type);

    // Add stripe payout preference
    @POST("add_payout_preference")
    Call<ResponseBody> uploadStripe(@Body RequestBody RequestBody, @Query("token") String token);

    // this api called to resume the trip from MainActivity while Driver get-in to app
    @GET("incomplete_trip_details")
    Call<ResponseBody> getInCompleteTripsDetails(@Query("token") String token);

    // get Trip invoice Details  Rider
    @GET("get_invoice")
    Call<ResponseBody> getInvoice(@Query("token") String token,@Query("trip_id") String TripId,@Query("user_type") String userType);

    //Force Update API
    @GET("check_version")
    Call<ResponseBody> checkVersion(@Query("version") String code, @Query("user_type") String type, @Query("device_type") String deviceType);

    //Check user Mobile Number
    @GET("numbervalidation")
    Call<ResponseBody> numbervalidation(@Query("mobile_number") String mobile_number, @Query("country_code") String country_code, @Query("user_type") String user_type, @Query("language") String language, @Query("forgotpassword") String forgotpassword);


//Get Bank Details Prefernce
  /*  @GET("driver_bank_details")
    Call<ResponseBody> driver_bank_details(@Query("account_holder_name") String account_holder_name, @Query("account_number") String account_number, @Query("bank_location") String bank_location, @Query("bank_name") String bank_name, @Query("token") String token,@Query("user_type")String user_type);
*/
}


