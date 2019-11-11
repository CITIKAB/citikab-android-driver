package com.trioangle.goferdriver.map;
/**
 * @package com.trioangle.goferdriver.map
 * @subpackage map
 * @category GpsService
 * @author Trioangle Product Team
 * @version 1.5
 */

import android.Manifest;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.os.PowerManager;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.trioangle.goferdriver.R;
import com.trioangle.goferdriver.configs.SessionManager;
import com.trioangle.goferdriver.datamodel.LatLngModel;
import com.trioangle.goferdriver.helper.ComplexPreferences;
import com.trioangle.goferdriver.helper.CustomDialog;
import com.trioangle.goferdriver.helper.NetworkUtil;

import com.trioangle.goferdriver.interfaces.ApiService;
import com.trioangle.goferdriver.interfaces.ServiceListener;
import com.trioangle.goferdriver.model.JsonResponse;
import com.trioangle.goferdriver.network.AppController;
import com.trioangle.goferdriver.util.CommonKeys;
import com.trioangle.goferdriver.util.CommonMethods;
import com.trioangle.goferdriver.util.GPSBackgroundServiceRestarterBroadcastReceiver;
import com.trioangle.goferdriver.util.RequestCallback;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import static com.trioangle.goferdriver.util.Enums.REQ_UPDATE_ONLINE;

/* ************************************************************
                      GpsService
Get and update driver current location in server
*************************************************************** */
public class GpsService extends Service implements ServiceListener {

    // LOCATION_AND_WRITEPERMISSION_ARRAY time interval
    private static final int LOCATION_INTERVAL = 1000 * 1 * 1; // 1000 * 60 * 1 for 1 minute 1000 * 10 * 1 for 10 seconds
    private static final int LOCATION_INTERVAL_CHECK = 1000 * 1 * 1;
    private static final int LOCATION_UPDATE_INTERVAL = 1000 * 10 * 1; // 1000 * 60 * 1 for 1 minute 1000 * 10 * 1 for 10 seconds
    private static final float LOCATION_DISTANCE = 10; // 30 meters
    private static final float maxDistance = (float) 0.4;
    private static final String TAG = "GPS Service";
    public static List<LatLng> latLngList =new ArrayList<LatLng>();
    public @Inject
    ApiService apiService;
    public @Inject
    SessionManager sessionManager;
    public @Inject
    CommonMethods commonMethods;
    public @Inject
    CustomDialog customDialog;
    boolean updateLatLng = false;
    public AlertDialog dialog;
    public Location mLastLocation = null;
    public Location mCheckLocation = null;
    public int count = 0;
    public int counts = 0;
    public double oldlatitude = 0;
    public double oldlongitude = 0;
    public boolean gps_enabled = false;
    public boolean network_enabled = false;
    public MyTimerTask timerTask;
    public boolean updateLocationandReport = false;
    public DecimalFormat twoDForm;
    protected boolean isInternetAvailable;
    private boolean first = true;
    private boolean isbeginfirst = true;
    private boolean isotherfirst = true;
    private float distanceInMeters = 0;
    private float distanceInKM = 0;
    LatLngModel latLngModel;
    int currentHourIn12Format;
    int currentMinIn12Format;
    private float totalDistanceInKM = 0;
    private Timer timer = new Timer();
    private PowerManager.WakeLock wakeLock;
    ComplexPreferences complexPreferences;
    private LocationListener listener;
    private LocationManager locationManager;
    private DatabaseReference mFirebaseDatabase;
    private ValueEventListener mSearchedLocationReferenceListener;
    private long locationUpdatedAt = Long.MIN_VALUE;
    private int FASTEST_INTERVAL = LOCATION_INTERVAL_CHECK; // use whatever suits you
    private Location location = null;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {

        CommonMethods.DebuggableLogD("GPS Service Called", "GPS Service Called");

        PowerManager mgr = (PowerManager) getSystemService(Context.POWER_SERVICE);
        wakeLock = mgr.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "MyWakeLock");
        complexPreferences = ComplexPreferences.getComplexPreferences(this, "mypref", MODE_PRIVATE);
        latLngModel = complexPreferences.getObject("latLngList", LatLngModel.class);
        latLngList = new ArrayList<LatLng>();


            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                System.out.println("checking in foreground notify one : ");

                startMyOwnForeground();
            }else{
                System.out.println("checking in foreground notify two : ");
                startForegroundService();
            }



        if (latLngModel != null && latLngModel.latLngList != null) {
            latLngList.addAll(latLngModel.latLngList);

        }

        AppController.getAppComponent().inject(this);
        dialog = commonMethods.getAlertDialog(this);
        FirebaseDatabase mFirebaseInstance = FirebaseDatabase.getInstance();

        // get reference to 'Driver LOCATION_AND_WRITEPERMISSION_ARRAY' node
        mFirebaseDatabase = mFirebaseInstance.getReference(CommonKeys.LiveTrackingFirebaseDatabaseName);

        listener = new LocationListener() {
            @Override
            public void onLocationChanged(Location LastLocation) {
                CommonMethods.DebuggableLogD("GPS Service Loc Changed", LastLocation.toString());
                System.out.println("onLocationChanged called : "+LastLocation.toString());
                sessionManager.setCurrentLatitude(Double.toString(LastLocation.getLatitude()));
                sessionManager.setLatitude(Double.toString(LastLocation.getLatitude()));
                sessionManager.setCurrentLongitude(Double.toString(LastLocation.getLongitude()));
                sessionManager.setLongitude(Double.toString(LastLocation.getLongitude()));
                if (sessionManager.getTripId() != null && !sessionManager.getTripId().equals("")) {
                    float distance = 0;
                    if (location != null) {
                        distance = LastLocation.distanceTo(location);
                        if (distance < 30) {
                            updateLocationFireBase(Double.toString(LastLocation.getLatitude()),
                                    Double.toString(LastLocation.getLongitude()),
                                    sessionManager.getTripId());
                        }
                    }
                } else {
                    CommonMethods.DebuggableLogE(TAG, "Driver LOCATION_AND_WRITEPERMISSION_ARRAY data removed!");
                    if (mSearchedLocationReferenceListener != null)
                        mFirebaseDatabase.removeEventListener(mSearchedLocationReferenceListener);
                }
                location = LastLocation;
            }

            @Override
            public void onStatusChanged(String s, int i, Bundle bundle) {


            }

            @Override
            public void onProviderEnabled(String s) {


            }

            @Override
            public void onProviderDisabled(String s) {


            }
        };

        locationManager = (LocationManager) getApplicationContext().getSystemService(Context.LOCATION_SERVICE);

        //noinspection MissingPermission
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }

        //exceptions will be thrown if provider is not permitted.
        try {
            gps_enabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        } catch (Exception ex) {
        }
        try {
            network_enabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        } catch (Exception ex) {
        }

        //don't start listeners if no provider is enabled

        if (network_enabled)

            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, LOCATION_INTERVAL, LOCATION_DISTANCE, listener);
        //Newly Added
          /*  if (locationManager != null) {
                mLastLocation = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                if (mLastLocation!=null){
                    oldlatitude=mLastLocation.getLatitude();
                    oldlongitude=mLastLocation.getLongitude();
                }
            }*/
        if (gps_enabled)

            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, LOCATION_INTERVAL, LOCATION_DISTANCE, listener);


    }

    private void startForegroundService()
    {

        // Create notification default intent.
        Intent intent = new Intent();
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);

        // Create notification builder.
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this);

        // Make notification show big text.
        NotificationCompat.BigTextStyle bigTextStyle = new NotificationCompat.BigTextStyle();
        bigTextStyle.setBigContentTitle("Rey Driver");
        //bigTextStyle.bigText(" foreground service ");
        // Set big text style.
        builder.setStyle(bigTextStyle);

        builder.setWhen(System.currentTimeMillis());
        builder.setSmallIcon(R.mipmap.ic_launcher);
        Bitmap largeIconBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.applogo);
        builder.setLargeIcon(largeIconBitmap);
        // Make the notification max priority.
        builder.setPriority(Notification.PRIORITY_MAX);
        // Make head-up notification.
        builder.setFullScreenIntent(pendingIntent, true);


        //if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) builder.setChannelId(youChannelID);
        // Add Pause button intent in notification.
       /* Intent pauseIntent = new Intent(this, GpsService.class);
        pauseIntent.setAction(ACTION_PAUSE);
        PendingIntent pendingPrevIntent = PendingIntent.getService(this, 0, pauseIntent, 0);
        NotificationCompat.Action prevAction = new NotificationCompat.Action(android.R.drawable.ic_media_pause, "Pause", pendingPrevIntent);
        builder.addAction(prevAction);*/

        // Build the notification.
        Notification notification = builder.build();

        // Start foreground service.
        startForeground(1, notification);

    }

    
    @RequiresApi(api = Build.VERSION_CODES.O)
    private void startMyOwnForeground(){
        String NOTIFICATION_CHANNEL_ID = "com.example.simpleapp";
        String channelName = "My Background Service";
        NotificationChannel chan = new NotificationChannel(NOTIFICATION_CHANNEL_ID, channelName, NotificationManager.IMPORTANCE_NONE);
        chan.setLightColor(Color.BLUE);
        chan.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);
        NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        assert manager != null;
        manager.createNotificationChannel(chan);

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID);
        Notification notification = notificationBuilder.setOngoing(true)
                .setSmallIcon(R.drawable.applogo)
                .setContentTitle("App is running in background")
                .setPriority(NotificationManager.IMPORTANCE_MIN)
                .setCategory(Notification.CATEGORY_SERVICE)
                .build();
        startForeground(2, notification);
    }

    /*
     * Wake lock started
     */
    private void acquireWakeLock() {
        try {
            wakeLock.acquire();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /*
     * Wake lock end
     */
    private void releaseWakeLock() {
        try {
            wakeLock.release();
        } catch (Exception e) {

        }
    }



    @Override
    public void onDestroy() {



        if (locationManager != null) {
            //noinspection MissingPermission
            locationManager.removeUpdates(listener);
        }
        timer.cancel();

        String str = sessionManager.getTripStatus();
        //String str2 = getResources().getString(R.string.begin_trip);
        String str2 = CommonKeys.TripStaus.BeginTrip;
        if (str != null && str.equals(str2)) {
            latLngModel = new LatLngModel();

            Calendar rightNow = Calendar.getInstance();

            currentHourIn12Format = rightNow.get(Calendar.HOUR_OF_DAY);
            currentMinIn12Format = rightNow.get(Calendar.MINUTE);


            latLngModel.setLatLngList(latLngList);
            latLngModel.setHour(currentHourIn12Format);
            latLngModel.setMin(currentMinIn12Format);
            complexPreferences.putObject("latLngList", latLngModel);
            complexPreferences.commit();
            System.out.println("latLngList : " + latLngList.size());
            Intent broadcastIntent = new Intent(this, GPSBackgroundServiceRestarterBroadcastReceiver.class);
            sendBroadcast(broadcastIntent);
        }
        System.out.println("latlng size one : "+latLngList.size());
        if(latLngList.size()>0){
            oldlatitude = latLngList.get(latLngList.size()-1).latitude;
            oldlongitude = latLngList.get(latLngList.size()-1).longitude;

        }
        System.out.println("old latitude : "+oldlatitude);
        System.out.println("old longitude : "+oldlongitude);

        super.onDestroy();

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        timerTask = new MyTimerTask();
        timer.scheduleAtFixedRate(timerTask, LOCATION_UPDATE_INTERVAL, LOCATION_UPDATE_INTERVAL);
        return START_STICKY;
    }

    @Override
    public void onSuccess(JsonResponse jsonResp, String data) {
        if (jsonResp.isSuccess()) {
            onSuccessloc();
        } else if (!TextUtils.isEmpty(jsonResp.getStatusMsg())) {
            Intent j = new Intent("location_update");
            j.putExtra("type", "Update");
            j.putExtra("Lat", Double.valueOf(sessionManager.getLatitude()));
            j.putExtra("Lng", Double.valueOf(sessionManager.getLongitude()));
            j.putExtra("km", String.valueOf(distanceInKM));
            j.putExtra("status", "Else");
            sendBroadcast(j);
        }
    }

    @Override
    public void onFailure(JsonResponse jsonResp, String data) {
        Intent j = new Intent("location_update");
        j.putExtra("type", "Update");
        j.putExtra("Lat", Double.valueOf(sessionManager.getLatitude()));
        j.putExtra("Lng", Double.valueOf(sessionManager.getLongitude()));
        j.putExtra("km", String.valueOf(distanceInKM));
        j.putExtra("status", "error");
        sendBroadcast(j);
    }

    public HashMap<String, String> getLocation() {
        HashMap<String, String> locationHashMap = new HashMap<>();
        locationHashMap.put("latitude", sessionManager.getLatitude());
        locationHashMap.put("longitude", sessionManager.getLongitude());
        locationHashMap.put("user_type", sessionManager.getType());
        locationHashMap.put("car_id", sessionManager.getVehicleId());
        locationHashMap.put("status", sessionManager.getDriverStatus());
        locationHashMap.put("token", sessionManager.getAccessToken());
        return locationHashMap;
    }

    /*
     * Update driver current location
     */
    public void updateOnlineStatus(HashMap<String, String> locationHashMap) {

        apiService.updateLocation(locationHashMap).enqueue(new RequestCallback(REQ_UPDATE_ONLINE, this));

    }

    public void onSuccessloc() {
        Intent i = new Intent("location_update");
        i.putExtra("type", "Update");
        i.putExtra("Lat", Double.valueOf(sessionManager.getLatitude()));
        i.putExtra("Lng", Double.valueOf(sessionManager.getLongitude()));
        i.putExtra("km", String.valueOf(distanceInKM));
        sendBroadcast(i);

    }


    /*
     * Get location change and update
     */
    public void updateLocationChange() {
        twoDForm = new DecimalFormat("#.#######");
        updateLocationandReport = false;
        CommonMethods.DebuggableLogD("GPS LOCATION_AND_WRITEPERMISSION_ARRAY Changed", "GPS LOCATION_AND_WRITEPERMISSION_ARRAY Changed" + mLastLocation.toString());
        System.out.println("Network Connectivity : " + NetworkUtil.getConnectivityStatusString(this));
        isInternetAvailable = commonMethods.isOnline(this);

        //isInternetAvailable = true;
        if(updateLatLng){
            oldlatitude = mLastLocation.getLatitude();
            oldlongitude = mLastLocation.getLongitude();
            updateLatLng=false;
        }else if(latLngList.size()>0){
            oldlatitude = latLngList.get(latLngList.size()-1).latitude;
            oldlongitude = latLngList.get(latLngList.size()-1).longitude;
        }else{
            oldlatitude = mLastLocation.getLatitude();
            oldlongitude = mLastLocation.getLongitude();
        }



        if (isInternetAvailable) {
            /*
             * Send location to broadcast
             */
            CommonMethods.DebuggableLogE("locationupdate", "mLastLocation:" + mLastLocation.getLatitude());
            Intent i = new Intent("location_update");
            i.putExtra("type", "change");
            i.putExtra("Lat", mLastLocation.getLatitude());
            i.putExtra("Lng", mLastLocation.getLongitude());
            i.putExtra("net", isInternetAvailable);
            sendBroadcast(i);
            updateLocationandReport = false;
            CommonMethods.DebuggableLogD("GPS Net available", "GPS Net available " + isInternetAvailable);

            /*
             * check update first time
             */
            if (count == 0) {
                locationUpdatedAt = System.currentTimeMillis();
                updateLocationandReport = true;
            } else {

                long secondsElapsed = TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis() - locationUpdatedAt);

                String str = sessionManager.getTripStatus();
                //String str2 = getResources().getString(R.string.begin_trip);
                String str2 = CommonKeys.TripStaus.BeginTrip;
                if (str != null && str.equals(str2)) {
                    sessionManager.setDriverStatus(CommonKeys.DriverStatus.Online);
                }
                CommonMethods.DebuggableLogE(TAG, "Trip Status : " + sessionManager.getTripStatus());
                CommonMethods.DebuggableLogE(TAG, "Driver Status : " + sessionManager.getDriverStatus());
                secondsElapsed = secondsElapsed * 1000;
                if (secondsElapsed >= FASTEST_INTERVAL) {
                    // check location accuracy here
                    locationUpdatedAt = System.currentTimeMillis();
                    updateLocationandReport = true;
                } else {
                    updateLocationandReport = true;  // for checking
                }

                /*
                 * User offline release wake lock
                 */
                if (sessionManager.getDriverStatus().equals("Offline")) {
                    updateLocationandReport = false;
                    if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        releaseWakeLock();
                    }

                } else {
                    if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        acquireWakeLock();
                    }

                }
            }

            /*
             *  Update location
             */
            CommonMethods.DebuggableLogD("GPS location report ", String.valueOf(updateLocationandReport));
            if (updateLocationandReport) {
                count++;
                counts++;

                sessionManager.setLatitude(String.valueOf(mLastLocation.getLatitude()));
                sessionManager.setLongitude(String.valueOf(mLastLocation.getLongitude()));
                String str = sessionManager.getTripStatus();
                //String str2 = getResources().getString(R.string.begin_trip);
                String str2 = CommonKeys.TripStaus.BeginTrip;
                //String arrive = getResources().getString(R.string.confirm_arrived);
                String arrive = CommonKeys.TripStaus.ConfirmArrived;
                if (str != null && str.equals(str2)) {
                    /*if (first || oldlatitude == 0) {
                        oldlatitude = mLastLocation.getLatitude();
                        oldlongitude = mLastLocation.getLongitude();

                    }*/
                    double oldlatitudeTemp;
                    double oldlongitudeTemp;

                    if (isbeginfirst) {
                        count = 0;
                        counts = 0;
                        isbeginfirst = false;
                        isotherfirst = true;
                        totalDistanceInKM = 0;
                        distanceInKM = 0;
                        distanceInMeters = 0;
                    }
                    CommonMethods.DebuggableLogD("GPS location check OLD", first + " " + String.valueOf(oldlatitude) + " " + String.valueOf(oldlongitude));
                    CommonMethods.DebuggableLogD("GPS location check NEW", first + " " + String.valueOf(mLastLocation.getLatitude()) + " " + String.valueOf(mLastLocation.getLongitude()));
                    if (first || (oldlatitude != mLastLocation.getLatitude() || oldlongitude != mLastLocation.getLongitude())) {
                        first = false;

                        distanceInKM = 0;
                        distanceInMeters = 0;
                        Location oldLocation = new Location("");//provider name is unnecessary
                        oldLocation.setLatitude(oldlatitude);//your coords of course
                        oldLocation.setLongitude(oldlongitude);

                        Location newLocation = new Location("");//provider name is unnecessary
                        newLocation.setLatitude(mLastLocation.getLatitude());//your coords of course
                        newLocation.setLongitude(mLastLocation.getLongitude());




                        /*if(oldMin>newMin){

                        }*/
                        distanceInMeters = oldLocation.distanceTo(newLocation);


                        distanceInKM = (float) (distanceInMeters / 1000.0);

                        try {
                            distanceInKM = Float.valueOf(twoDForm.format(distanceInKM).replaceAll(",", "."));
                        } catch (Exception e) {
                            e.printStackTrace();
                        }





                    } else {
                        distanceInKM = 0;
                        distanceInMeters = 0;
                        System.out.println("Distance in km two : "+distanceInKM);

                    }
                    //totalDistanceInKM = totalDistanceInKM + distanceInKM;
                    /*System.out.println("Distance in km three : "+distanceInKM);
                    System.out.println("Total Distance in km one : "+totalDistanceInKM);*/

                    oldlatitudeTemp = oldlatitude;
                    oldlongitudeTemp = oldlongitude;

                    Location startPoint=new Location("locationA");
                    startPoint.setLatitude(oldlatitudeTemp);
                    startPoint.setLongitude(oldlongitudeTemp);

                    Location endPoint=new Location("locationA");
                    endPoint.setLatitude(Double.valueOf(sessionManager.getLatitude()));
                    endPoint.setLongitude(Double.valueOf(sessionManager.getLongitude()));

                    float distanceTo = startPoint.distanceTo(endPoint);


                    System.out.println("Distance To One : "+distanceTo);
                    if(distanceTo<1000){


                        LatLng newLatLng = new LatLng(Double.valueOf(sessionManager.getLatitude()), Double.valueOf(sessionManager.getLongitude()));
                        latLngList.add(newLatLng);

                       /* oldlatitude = mLastLocation.getLatitude();
                        oldlongitude = mLastLocation.getLongitude();*/
                    }else{
                        updateLatLng=true;
                    }




                    for (int j = 0; j < latLngList.size(); j++) {
                        System.out.println("LatLngList latitude " + j + " : " + latLngList.get(j).latitude);
                        System.out.println("LatLngList longitude " + j + " : " + latLngList.get(j).longitude);
                    }

                    Calendar rightNow = Calendar.getInstance();

                    currentHourIn12Format = rightNow.get(Calendar.HOUR_OF_DAY);
                    currentMinIn12Format = rightNow.get(Calendar.MINUTE);


                    latLngModel = new LatLngModel();


                    latLngModel.setLatLngList(latLngList);
                    latLngModel.setHour(currentHourIn12Format);
                    latLngModel.setMin(currentMinIn12Format);
                    complexPreferences.putObject("latLngList", latLngModel);
                    complexPreferences.commit();

                    if(totalDistanceInKM>0){
                        distanceInKM = totalDistanceInKM;
                        totalDistanceInKM = 0;
                    }
                    System.out.println("Distance To Two : "+distanceTo);

                    if(distanceTo<1000){
                        HashMap<String, String> locationHashMap = new HashMap<>();
                        locationHashMap.put("latitude", sessionManager.getLatitude());
                        locationHashMap.put("longitude", sessionManager.getLongitude());
                        locationHashMap.put("total_km", String.valueOf(distanceInKM));
                        locationHashMap.put("user_type", sessionManager.getType());
                        locationHashMap.put("car_id", sessionManager.getVehicleId());
                        locationHashMap.put("trip_id", sessionManager.getTripId());
                        locationHashMap.put("status", "Trip");
                        locationHashMap.put("token", sessionManager.getAccessToken());


                        updateOnlineStatus(locationHashMap);
                    }



                } else if (str != null && str.equals(arrive)) {

                    HashMap<String, String> locationHashMap = new HashMap<>();
                    locationHashMap.put("latitude", sessionManager.getLatitude());
                    locationHashMap.put("longitude", sessionManager.getLongitude());
                    locationHashMap.put("total_km", "0");
                    locationHashMap.put("user_type", sessionManager.getType());
                    locationHashMap.put("car_id", sessionManager.getVehicleId());
                    locationHashMap.put("trip_id", sessionManager.getTripId());
                    locationHashMap.put("status", sessionManager.getDriverStatus());
                    locationHashMap.put("token", sessionManager.getAccessToken());
                    updateOnlineStatus(locationHashMap);
                } else {

                    if (isotherfirst) {
                        count = 0;
                        counts = 0;
                        isbeginfirst = true;
                        isotherfirst = false;
                        totalDistanceInKM = 0;
                        distanceInKM = 0;
                        distanceInMeters = 0;
                    }


                    if ((sessionManager.getDriverStatus() != null) && sessionManager.getDriverStatus().equals(getResources().getString(R.string.online))) {
                        HashMap<String, String> locationHashMap = new HashMap<>();
                        locationHashMap.put("latitude", sessionManager.getLatitude());
                        locationHashMap.put("longitude", sessionManager.getLongitude());
                        locationHashMap.put("user_type", sessionManager.getType());
                        locationHashMap.put("car_id", sessionManager.getVehicleId());
                        locationHashMap.put("status", sessionManager.getDriverStatus());
                        locationHashMap.put("token", sessionManager.getAccessToken());
                        updateOnlineStatus(locationHashMap);
                    }

                }
                updateLocationandReport = false;

                Intent j = new Intent("location_update");
                j.putExtra("type", "Updates");
                j.putExtra("Lat", Double.valueOf(sessionManager.getLatitude()));
                j.putExtra("Lng", Double.valueOf(sessionManager.getLongitude()));
                j.putExtra("km", String.valueOf(distanceInKM));
                j.putExtra("status", sessionManager.getTripStatus());
                sendBroadcast(j);

            } else {
                Intent j = new Intent("location_update");
                j.putExtra("type", "Updates");
                j.putExtra("Lat", Double.valueOf(sessionManager.getLatitude()));
                j.putExtra("Lng", Double.valueOf(sessionManager.getLongitude()));
                j.putExtra("km", String.valueOf(distanceInKM));
                j.putExtra("status", "Report false");
                sendBroadcast(j);
            }

        } else {


            double oldlatitudeTemp;
            double oldlongitudeTemp;

            if (first || (oldlatitude != mLastLocation.getLatitude() || oldlongitude != mLastLocation.getLongitude())) {
                first = false;

                distanceInKM = 0;
                distanceInMeters = 0;
                Location oldLocation = new Location("");//provider name is unnecessary
                oldLocation.setLatitude(oldlatitude);//your coords of course
                oldLocation.setLongitude(oldlongitude);

                Location newLocation = new Location("");//provider name is unnecessary
                newLocation.setLatitude(mLastLocation.getLatitude());//your coords of course
                newLocation.setLongitude(mLastLocation.getLongitude());




                        /*if(oldMin>newMin){

                        }*/
                distanceInMeters = oldLocation.distanceTo(newLocation);


                distanceInKM = (float) (distanceInMeters / 1000.0);

                try {
                    distanceInKM = Float.valueOf(twoDForm.format(distanceInKM).replaceAll(",", "."));
                } catch (Exception e) {
                    e.printStackTrace();
                }


                double
                oldlatitude = mLastLocation.getLatitude();
                oldlongitude = mLastLocation.getLongitude();

            } else {
                distanceInKM = 0;
                distanceInMeters = 0;
                System.out.println("Distance in km two : "+distanceInKM);

            }
            totalDistanceInKM = totalDistanceInKM + distanceInKM;
            System.out.println("Distance in km three : "+distanceInKM);
            System.out.println("Total Distance in km one : "+totalDistanceInKM);
            Date currentTime = Calendar.getInstance().getTime();




            oldlatitudeTemp = oldlatitude;
            oldlongitudeTemp = oldlongitude;

            Location startPoint=new Location("locationA");
            startPoint.setLatitude(oldlatitudeTemp);
            startPoint.setLongitude(oldlongitudeTemp);

            Location endPoint=new Location("locationA");
            endPoint.setLatitude(Double.valueOf(sessionManager.getLatitude()));
            endPoint.setLongitude(Double.valueOf(sessionManager.getLongitude()));

            float distanceTo = startPoint.distanceTo(endPoint);

            System.out.println("Distance To Three : "+distanceTo);


            if(distanceTo<1000){


                LatLng newLatLng = new LatLng(Double.valueOf(sessionManager.getLatitude()), Double.valueOf(sessionManager.getLongitude()));
                latLngList.add(newLatLng);

                       /* oldlatitude = mLastLocation.getLatitude();
                        oldlongitude = mLastLocation.getLongitude();*/
            }else{
                updateLatLng=true;
            }






            /*LatLng newLatLng = new LatLng(Double.valueOf(sessionManager.getLatitude()), Double.valueOf(sessionManager.getLongitude()));
            latLngList.add(newLatLng);*/


            for (int j = 0; j < latLngList.size(); j++) {
                System.out.println("LatLngList latitude " + j + " : " + latLngList.get(j).latitude);
                System.out.println("LatLngList longitude " + j + " : " + latLngList.get(j).longitude);
            }

            Calendar rightNow = Calendar.getInstance();

            currentHourIn12Format = rightNow.get(Calendar.HOUR_OF_DAY);
            currentMinIn12Format = rightNow.get(Calendar.MINUTE);


            latLngModel = new LatLngModel();


            latLngModel.setLatLngList(latLngList);
            latLngModel.setHour(currentHourIn12Format);
            latLngModel.setMin(currentMinIn12Format);
            complexPreferences.putObject("latLngList", latLngModel);
            complexPreferences.commit();



            Intent i = new Intent("location_update");
            i.putExtra("type", "change");
            i.putExtra("Lat", mLastLocation.getLatitude());
            i.putExtra("Lng", mLastLocation.getLongitude());
            i.putExtra("net", isInternetAvailable);
            sendBroadcast(i);
            CommonMethods.DebuggableLogD("GPS Net unavailable", "GPS Net unavailable" + updateLocationandReport);
        }
    }

    /**
     * Creating new user node under 'users'
     */
    private void updateLocationFireBase(String lat, String lng, String tripid) {
        // TODO

        DriverLocation driverLocation = new DriverLocation(lat, lng);

        CommonMethods.DebuggableLogE(TAG, "Driver LOCATION_AND_WRITEPERMISSION_ARRAY data update" + tripid + " " + driverLocation.lat + " " + driverLocation.lng);
        mFirebaseDatabase.child(tripid).setValue(driverLocation);

        Intent j = new Intent("location_update");
        j.putExtra("type", "DataBase");
        j.putExtra("Lat", Double.valueOf(lat));
        j.putExtra("Lng", Double.valueOf(lng));
        //j.putExtra("km",String.valueOf(distanceInKM));
        //j.putExtra("status",sessionManager.getTripStatus());
        sendBroadcast(j);
        /*if(isCheck.equals("check")) {
            isCheck="checked";*/
        if (mSearchedLocationReferenceListener == null) {
            addLatLngChangeListener(); // Get Driver Lat Lng

        } else {

        }
           /*
        }*/
    }

    /**
     * Driver LOCATION_AND_WRITEPERMISSION_ARRAY change listener
     */
    private void addLatLngChangeListener() {

        CommonMethods.DebuggableLogE(TAG, "Driver LOCATION_AND_WRITEPERMISSION_ARRAY data called");
        /*if(mSearchedLocationReferenceListener!=null)
            mFirebaseDatabase.removeEventListener(mSearchedLocationReferenceListener);*/
        // User data change listener
        final Query query = mFirebaseDatabase.child(sessionManager.getTripId());

        mSearchedLocationReferenceListener = query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                if (sessionManager.getTripId() != null) {
                    DriverLocation driverLocation = dataSnapshot.getValue(DriverLocation.class);

                    // Check for null
                    if (driverLocation == null) {
                        CommonMethods.DebuggableLogE(TAG, "Driver LOCATION_AND_WRITEPERMISSION_ARRAY data is null!");
                        return;
                    }

                    CommonMethods.DebuggableLogE(TAG, "Driver LOCATION_AND_WRITEPERMISSION_ARRAY data is changed!" + driverLocation.lat + ", " + driverLocation.lng);
                } else {
                    query.removeEventListener(this);
                    mFirebaseDatabase.removeEventListener(this);
                    mFirebaseDatabase.onDisconnect();
                }

            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                CommonMethods.DebuggableLogE(TAG, "Failed to read user", error.toException());
            }
        });
    }





    /*
     * GPS location update
     */
    class MyTimerTask extends TimerTask {


        @Override
        public void run() {
            CommonMethods.DebuggableLogD("` ", "ss");
            if (!TextUtils.isEmpty(sessionManager.getCurrentLatitude()) && !TextUtils.isEmpty(sessionManager.getCurrentLongitude())) {
                Double lat = Double.valueOf(sessionManager.getCurrentLatitude());
                Double lng = Double.valueOf(sessionManager.getCurrentLongitude());
                mLastLocation = new Location("curloc");
                mLastLocation.setLatitude(lat);
                mLastLocation.setLongitude(lng);


                DecimalFormat twoDForm = new DecimalFormat("#.######");
                String zerolat = twoDForm.format(mLastLocation.getLatitude());
                String zerolng = twoDForm.format(mLastLocation.getLongitude());
                String onelat;
                String onelng;
                if (mCheckLocation != null) {
                    onelat = twoDForm.format(mCheckLocation.getLatitude());
                    onelng = twoDForm.format(mCheckLocation.getLongitude());
                } else {
                    onelat = twoDForm.format(0.0);
                    onelng = twoDForm.format(0.0);
                }
                String networkCheck = NetworkUtil.getConnectivityStatusString(getApplicationContext());


                System.out.println("zerolat and lng : "+zerolat+":"+zerolng);
                System.out.println("onelat and lng : "+onelat+":"+onelng);




                if (!zerolat.equals(onelat) || !zerolng.equals(onelng) || mCheckLocation == null) {
                /*if (networkCheck.equals("Not connected to Internet")) {
                    if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                        // TODO: Consider calling
                        //    ActivityCompat#requestPermissions
                        // here to request the missing permissions, and then overriding
                        //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                        //                                          int[] grantResults)
                        // to handle the case where the user grants the permission. See the documentation
                        // for ActivityCompat#requestPermissions for more details.
                        return;
                    }
                    locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
                            2000,
                            10, locationListenerGPS);
                }else{

                }*/
                }
                updateLocationChange();
                mCheckLocation = mLastLocation;
            } else {
                CommonMethods.DebuggableLogV("locationupdate", "change:" + 0);

                Intent i = new Intent("location_update");
                i.putExtra("type", "change");
                i.putExtra("Lat", 0);
                i.putExtra("Lng", 0);
                i.putExtra("net", false);
                sendBroadcast(i);
            }
        }
    }

}
