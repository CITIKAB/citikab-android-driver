package com.trioangle.goferdriver.util;

import android.support.annotation.IntDef;
import android.support.annotation.StringDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

public class CommonKeys {
    public static final String KEY_MANUAL_BOOKED_RIDER_NAME = "name";
    public static final String KEY_MANUAL_BOOKED_RIDER_CONTACT_NUMBER = "number";
    public static final String KEY_MANUAL_BOOKED_RIDER_PICKU_LOCATION = "pickuploc";
    public static final String KEY_MANUAL_BOOKED_RIDER_PICKU_DATE_AND_TIME = "datetime";
    public static final String KEY_TYPE = "type";
    public static final int YES = 1;
    public static final int NO = 0;
    public static final String KEY_IS_NEED_TO_PLAY_SOUND = "playSound";
    public static Boolean isLoggable = true;
    public static final String DeviceTypeAndroid = "2";


    //public static String baseUrl = "http://gofer.trioangle.com/";
   // public static String baseUrl = "http://192.168.0.219/product/Gofer-Enterprise-2.0/public/";
   // public static String baseUrl = "http://trioangledemo.com/citikab/public/";
    public static String baseUrl = "http://3.208.43.254/";


    public static String WorkKeyForUpdateGPS = "updateGPSWorker";
    public static String WorkTagForUpdateGPS = "updateGPSTag";

    public static String imageUrl = baseUrl+"images/";
    public static String apiBaseUrl = baseUrl+"api/";
    public static String termPolicyUrl = baseUrl;

    public static String chatFirebaseDatabaseName = "driver_rider_trip_chats";
    public static String LiveTrackingFirebaseDatabaseName = "live_tracking";
    public static String FIREBASE_CHAT_MESSAGE_KEY = "message";
    public static String FIREBASE_CHAT_TYPE_KEY = "type";
    public static String FIREBASE_CHAT_TYPE_RIDER = "rider";
    public static String FIREBASE_CHAT_TYPE_DRIVER = "driver";

    public static boolean IS_ALREADY_IN_TRIP = false;

    public static final int ACTIVITY_REQUEST_CODE_START_FACEBOOK_ACCOUNT_KIT = 102;

    public static final String FIREBASE_CHAT_ACTIVITY_SOURCE_ACTIVITY_TYPE_CODE = "sourceActivityCode";
    public static final int FIREBASE_CHAT_ACTIVITY_REDIRECTED_FROM_RIDER_OR_DRIVER_PROFILE = 110;
    public static final int FIREBASE_CHAT_ACTIVITY_REDIRECTED_FROM_NOTIFICATION = 111;

    public static final String NUMBER_VALIDATION_API_RESULT_OLD_USER = "1";
    public static final String NUMBER_VALIDATION_API_RESULT_NEW_USER = "0";
    public static final int FACEBOOK_ACCOUNT_KIT_RESULT_NEW_USER = 157; // Number declared randomly, not specifically
    public static final int FACEBOOK_ACCOUNT_KIT_RESULT_OLD_USER = 158; // Number declared randomly, not specifically

    public static final String FACEBOOK_ACCOUNT_KIT_MESSAGE_KEY="message";
    public static final String FACEBOOK_ACCOUNT_KIT_PHONE_NUMBER_KEY="phoneNumber";
    public static final String FACEBOOK_ACCOUNT_KIT_PHONE_NUMBER_COUNTRY_CODE_KEY="countryCode";
    public static String TripId="trip_id";

    @IntDef({FirebaseChatserviceTriggeredFrom.backgroundService, FirebaseChatserviceTriggeredFrom.chatActivity})
    @Retention(RetentionPolicy.SOURCE)
    public @interface FirebaseChatserviceTriggeredFrom {
        int backgroundService = 0;
        int chatActivity = 1;

    }

    @StringDef({DriverStatus.Online, DriverStatus.Offline})
    @Retention(RetentionPolicy.SOURCE)
    public @interface DriverStatus {
        String  Online = "Online";
        String Offline = "Offline";

    }

    @StringDef({TripStaus.ConfirmArrived, TripStaus.BeginTrip,TripStaus.EndTrip})
    @Retention(RetentionPolicy.SOURCE)
    public @interface TripStaus {
        String  ConfirmArrived = "CONFIRM YOU'VE ARRIVED";
        String BeginTrip = "Begin Trip";
        String EndTrip = "End Trip";
        String Pending =   "Pending";
    }


    @IntDef({ManualBookingPopupType.bookedInfo, ManualBookingPopupType.cancel,ManualBookingPopupType.reminder})
    @Retention(RetentionPolicy.SOURCE)
    public @interface ManualBookingPopupType {
        int bookedInfo = 1;
        int cancel = 0;
        int reminder = 2;

    }

    @StringDef({RideBookedType.schedule, RideBookedType.auto,RideBookedType.manualBooking})
    @Retention(RetentionPolicy.SOURCE)
    public @interface RideBookedType {
        String schedule = "Schedule Booking";
        String auto = "";
        String manualBooking = "Manual Booking";

    }

}