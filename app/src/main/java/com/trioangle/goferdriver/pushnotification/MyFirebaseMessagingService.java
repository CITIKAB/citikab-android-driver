package com.trioangle.goferdriver.pushnotification;
/**
 * @package com.trioangle.goferdriver.pushnotification
 * @subpackage pushnotification model
 * @category MyFirebaseMessagingService
 * @author Trioangle Product Team
 * @version 1.5
 */

import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.google.gson.Gson;
import com.trioangle.goferdriver.R;
import com.trioangle.goferdriver.configs.SessionManager;
import com.trioangle.goferdriver.datamodel.RiderDetailsModel;
import com.trioangle.goferdriver.helper.CommonDialog;
import com.trioangle.goferdriver.helper.ManualBookingDialog;
import com.trioangle.goferdriver.home.RequestAcceptActivity;
import com.trioangle.goferdriver.home.RequestReceiveActivity;
import com.trioangle.goferdriver.map.GpsService;
import com.trioangle.goferdriver.network.AppController;
import com.trioangle.goferdriver.service.UpdateGPSWorker;
import com.trioangle.goferdriver.service.WorkerUtils;
import com.trioangle.goferdriver.util.CommonKeys;
import com.trioangle.goferdriver.util.CommonMethods;

import org.json.JSONException;
import org.json.JSONObject;

import javax.inject.Inject;

/* ************************************************************
                MyFirebaseMessagingService
Its used to get the pushnotification FirebaseMessagingService function
*************************************************************** */
public class MyFirebaseMessagingService extends FirebaseMessagingService {

    private static final String TAG = MyFirebaseMessagingService.class.getSimpleName();

    public @Inject
    SessionManager sessionManager;

    public @Inject
    Gson gson;


    @Override
    public void onCreate() {
        super.onCreate();
        AppController.getAppComponent().inject(this);

    }

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        CommonMethods.DebuggableLogE(TAG, "From: " + remoteMessage.getFrom());

        if (remoteMessage == null)
            return;

        // Check if message contains a data payload.
        if (remoteMessage.getData().size() > 0) {
            CommonMethods.DebuggableLogE(TAG, "Data Payload: " + remoteMessage.getData().toString());

            try {
                JSONObject json = new JSONObject(remoteMessage.getData().toString());
                handleDataMessage(json);
                if (remoteMessage.getNotification() != null) {
                    CommonMethods.DebuggableLogE(TAG, "Notification Body: " + remoteMessage.getNotification().getBody());

                }

            } catch (Exception e) {
                CommonMethods.DebuggableLogE(TAG, "Exception: " + e.getMessage());
            }
        }
    }




    /*
     *  Handle push notification message
     */

    private void handleDataMessage(JSONObject json) {
        CommonMethods.DebuggableLogE(TAG, "push json: " + json.toString());
        String TripStatus = sessionManager.getTripStatus();
        String DriverStatus = sessionManager.getDriverStatus();
        String UserId = sessionManager.getAccessToken();
        try {

            /*
             *  Handle push notification and broadcast message to other activity
             */
            sessionManager.setPushJson(json.toString());

            if (!NotificationUtils.isAppIsInBackground(getApplicationContext())) {
                //CommonMethods.DebuggableLogE(TAG, "IF: " + json.toString());
                // app is in foreground, broadcast the push message
                if (json.getJSONObject("custom").has("ride_request")) {
                    if (DriverStatus.equals("Online")
                            && (TripStatus == null || TripStatus.equals(CommonKeys.TripStaus.EndTrip) || TripStatus.equals(""))
                            && UserId != null) {
                        //  Intent rider=new Intent(getApplicationContext(), Riderrating.class);
                        Intent pushNotification = new Intent(Config.PUSH_NOTIFICATION);
                        pushNotification.putExtra("message", "message");
                        LocalBroadcastManager.getInstance(this).sendBroadcast(pushNotification);
                        //startActivity(rider);
                    }
                } else {
                    Intent pushNotification = new Intent(Config.PUSH_NOTIFICATION);
                    pushNotification.putExtra("message", "message");
                    LocalBroadcastManager.getInstance(this).sendBroadcast(pushNotification);
                }

                if (json.getJSONObject("custom").has("cancel_trip")) {
                    sessionManager.clearTripID();
                    sessionManager.clearTripStatus();
                    sessionManager.setDriverAndRiderAbleToChat(false);
                    CommonMethods.stopFirebaseChatListenerService(getApplicationContext());

                    Intent dialogs = new Intent(getApplicationContext(), CommonDialog.class);
                    System.out.println("Langugage "+getResources().getString(R.string.yourtripcanceledrider));
                    sessionManager.setDialogMessage(getResources().getString(R.string.yourtripcanceledrider));
                    dialogs.putExtra("message", getResources().getString(R.string.yourtripcanceledrider));
                    dialogs.putExtra("type", 1);
                    dialogs.putExtra("status", 1);
                    dialogs.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(dialogs);


                } else if (json.getJSONObject("custom").has("trip_payment")) {
                    String riderProfile=json.getJSONObject("custom").getJSONObject("trip_payment").getString("rider_thumb_image");
                    sessionManager.setRiderProfilePic(riderProfile);

                    Intent dialogs = new Intent(getApplicationContext(), CommonDialog.class);
                    dialogs.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    dialogs.putExtra("message", getResources().getString(R.string.paymentcompleted));
                    dialogs.putExtra("type", 0);
                    dialogs.putExtra("status", 2);
                    startActivity(dialogs);

                } else if (json.getJSONObject("custom").has("custom_message")) {
                    NotificationUtils notificationUtils = new NotificationUtils(getApplicationContext());
                    notificationUtils.playNotificationSound();
                    String message = json.getJSONObject("custom").getJSONObject("custom_message").getString("message_data");
                    String title = json.getJSONObject("custom").getJSONObject("custom_message").getString("title");

                    notificationUtils.generateNotification(getApplicationContext(), message,title);
                } else if (json.getJSONObject("custom").has("manual_booking_trip_booked_info")) {
                    manualBookingTripBookedInfo(CommonKeys.ManualBookingPopupType.bookedInfo, json.getJSONObject("custom").getJSONObject("manual_booking_trip_booked_info"));
                } else if (json.getJSONObject("custom").has("manual_booking_trip_reminder")) {
                    manualBookingTripBookedInfo(CommonKeys.ManualBookingPopupType.reminder, json.getJSONObject("custom").getJSONObject("manual_booking_trip_reminder"));
                } else if (json.getJSONObject("custom").has("manual_booking_trip_canceled_info")) {
                    manualBookingTripBookedInfo(CommonKeys.ManualBookingPopupType.cancel, json.getJSONObject("custom").getJSONObject("manual_booking_trip_canceled_info"));
                } else if (json.getJSONObject("custom").has("manual_booking_trip_assigned")) {
                    manualBookingTripStarts(json.getJSONObject("custom").getJSONObject("manual_booking_trip_assigned"));
                } else {
                    CommonMethods.DebuggableLogE("Ride Request Received", "unable to process");
                }


            } else {
                CommonMethods.DebuggableLogE(TAG, "ELSE: " + json.toString());

                // app is in background, show the notification in notification tray
                if (json.getJSONObject("custom").has("ride_request")) {


                    if (DriverStatus.equals("Online")
                            && (TripStatus == null || TripStatus.equals(CommonKeys.TripStaus.EndTrip) || TripStatus.equals(""))
                            && UserId != null) {
                        sessionManager.setDriverAndRiderAbleToChat(false);
                        CommonMethods.stopFirebaseChatListenerService(getApplicationContext());
                        Intent intent = new Intent(this, RequestReceiveActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                    }

                } else if (json.getJSONObject("custom").has("cancel_trip")) {
                    sessionManager.clearTripID();
                    sessionManager.clearTripStatus();
                    sessionManager.setDriverAndRiderAbleToChat(false);
                    CommonMethods.stopFirebaseChatListenerService(getApplicationContext());
                    Intent dialogs = new Intent(getApplicationContext(), CommonDialog.class);
                    dialogs.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    dialogs.putExtra("message", getResources().getString(R.string.yourtripcanceledrider));
                    dialogs.putExtra("type", 1);
                    dialogs.putExtra("status", 1);
                    startActivity(dialogs);


                } else if (json.getJSONObject("custom").has("trip_payment")) {
                    String riderProfile=json.getJSONObject("custom").getJSONObject("trip_payment").getString("rider_thumb_image");
                    sessionManager.setRiderProfilePic(riderProfile);

                    Intent dialogs = new Intent(getApplicationContext(), CommonDialog.class);
                    dialogs.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    dialogs.putExtra("message", getResources().getString(R.string.paymentcompleted));
                    dialogs.putExtra("type", 0);
                    dialogs.putExtra("status", 2);
                    startActivity(dialogs);


                } else if (json.getJSONObject("custom").has("custom_message")) {
                    NotificationUtils notificationUtils = new NotificationUtils(getApplicationContext());
                    notificationUtils.playNotificationSound();
                    String message = json.getJSONObject("custom").getJSONObject("custom_message").getString("message_data");
                    String title = json.getJSONObject("custom").getJSONObject("custom_message").getString("title");

                    notificationUtils.generateNotification(getApplicationContext(), message,title);
                } else if (json.getJSONObject("custom").has("manual_booking_trip_booked_info")) {
                    manualBookingTripBookedInfo(CommonKeys.ManualBookingPopupType.bookedInfo, json.getJSONObject("custom").getJSONObject("manual_booking_trip_booked_info"));
                } else if (json.getJSONObject("custom").has("manual_booking_trip_reminder")) {
                    manualBookingTripBookedInfo(CommonKeys.ManualBookingPopupType.reminder, json.getJSONObject("custom").getJSONObject("manual_booking_trip_reminder"));
                } else if (json.getJSONObject("custom").has("manual_booking_trip_canceled_info")) {
                    manualBookingTripBookedInfo(CommonKeys.ManualBookingPopupType.cancel, json.getJSONObject("custom").getJSONObject("manual_booking_trip_canceled_info"));
                } else if (json.getJSONObject("custom").has("manual_booking_trip_assigned")) {
                    manualBookingTripStarts(json.getJSONObject("custom").getJSONObject("manual_booking_trip_assigned"));
                } else {
                    CommonMethods.DebuggableLogE("Ride Request Received", "unable to process");
                }

            }
        } catch (JSONException e) {
            CommonMethods.DebuggableLogE(TAG, "Json Exception: " + e.getMessage());
            e.printStackTrace();
        } catch (Exception e) {
            CommonMethods.DebuggableLogE(TAG, "Exception: " + e.getMessage());
        }
    }

    public void manualBookingTripBookedInfo(int manualBookedPopupType, JSONObject jsonObject) {
        String riderName = "", riderContactNumber = "", riderPickupLocation = "", riderPickupDateAndTime = "";
        try {
            riderName = jsonObject.getString("rider_first_name") + " " + jsonObject.getString("rider_last_name");
            riderContactNumber = jsonObject.getString("rider_country_code") + " " + jsonObject.getString("rider_mobile_number");
            riderPickupLocation = jsonObject.getString("pickup_location");
            riderPickupDateAndTime = jsonObject.getString("date") + " - " + jsonObject.getString("time");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        Intent dialogs = new Intent(getApplicationContext(), ManualBookingDialog.class);
        dialogs.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        dialogs.putExtra(CommonKeys.KEY_MANUAL_BOOKED_RIDER_NAME, riderName);
        dialogs.putExtra(CommonKeys.KEY_MANUAL_BOOKED_RIDER_CONTACT_NUMBER, riderContactNumber);
        dialogs.putExtra(CommonKeys.KEY_MANUAL_BOOKED_RIDER_PICKU_LOCATION, riderPickupLocation);
        dialogs.putExtra(CommonKeys.KEY_MANUAL_BOOKED_RIDER_PICKU_DATE_AND_TIME, riderPickupDateAndTime);
        dialogs.putExtra(CommonKeys.KEY_TYPE, manualBookedPopupType);
        startActivity(dialogs);

    }


    public void manualBookingTripStarts(JSONObject jsonResp) {


        RiderDetailsModel riderModel = gson.fromJson(jsonResp.toString(), RiderDetailsModel.class);
        sessionManager.setRiderName(riderModel.getRiderName());
        sessionManager.setRiderRating(riderModel.getRatingValue());
        sessionManager.setRiderProfilePic(riderModel.getRiderThumbImage());
        sessionManager.setBookingType(riderModel.getBookingType());
        sessionManager.setTripId(riderModel.getTripId().toString());
        sessionManager.setSubTripStatus(getResources().getString(R.string.confirm_arrived));
        //sessionManager.setTripStatus("CONFIRM YOU'VE ARRIVED");
        sessionManager.setTripStatus(CommonKeys.TripStaus.ConfirmArrived);
        sessionManager.setPaymentMethod(riderModel.getPaymentMethod());

        sessionManager.setDriverAndRiderAbleToChat(true);
        CommonMethods.startFirebaseChatListenerService(this);

        /*if (!CommonMethods.isMyBackgroundServiceRunning(GpsService.class, this)) {
            Intent GPSservice = new Intent(getApplicationContext(), GpsService.class);
            startService(GPSservice);
        }*/

        if(!WorkerUtils.isWorkRunning(CommonKeys.WorkTagForUpdateGPS)) {
            CommonMethods.DebuggableLogE("locationupdate", "StartWork:");
            WorkerUtils.startWorkManager(CommonKeys.WorkKeyForUpdateGPS, CommonKeys.WorkTagForUpdateGPS, UpdateGPSWorker.class);
        }

        //  acceptedDriverDetails = new AcceptedDriverDetails(ridername, mobilenumber, profileimg, ratingvalue, cartype, pickuplocation, droplocation, pickuplatitude, droplatitude, droplongitude, pickuplongitude);
//        mPlayer.stop();
        Intent requestaccept = new Intent(getApplicationContext(), RequestAcceptActivity.class);
        requestaccept.putExtra("riderDetails", riderModel);
        requestaccept.putExtra("tripstatus", getResources().getString(R.string.confirm_arrived));
        requestaccept.putExtra(CommonKeys.KEY_IS_NEED_TO_PLAY_SOUND,CommonKeys.YES);
        requestaccept.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(requestaccept);
    }
}
