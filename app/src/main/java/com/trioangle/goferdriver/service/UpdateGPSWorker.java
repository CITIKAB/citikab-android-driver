package com.trioangle.goferdriver.service;

import android.Manifest;
import android.app.ActivityManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.PowerManager;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.trioangle.goferdriver.R;
import com.trioangle.goferdriver.configs.SessionManager;
import com.trioangle.goferdriver.helper.CustomDialog;
import com.trioangle.goferdriver.home.RequestAcceptActivity;
import com.trioangle.goferdriver.interfaces.ApiService;
import com.trioangle.goferdriver.interfaces.ServiceListener;
import com.trioangle.goferdriver.map.DriverLocation;
import com.trioangle.goferdriver.model.JsonResponse;
import com.trioangle.goferdriver.network.AppController;
import com.trioangle.goferdriver.util.CommonKeys;
import com.trioangle.goferdriver.util.CommonMethods;
import com.trioangle.goferdriver.util.RequestCallback;

import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.List;

import javax.inject.Inject;

import androidx.work.Worker;
import androidx.work.WorkerParameters;

import static com.trioangle.goferdriver.util.Enums.REQ_UPDATE_ONLINE;

public class UpdateGPSWorker extends Worker implements ServiceListener {

    private static final String TAG = "GPS Service";

    // Location Variables
    private static final int LOCATION_INTERVAL = 1000 * 1 * 1; // 1000 * 60 * 1 for 1 minute 1000 * 10 * 1 for 10 seconds
    private static final float LOCATION_DISTANCE = 10; // 30 meters
    private static final int MAX_DISTANCE = 750; // Maximum meter for 10 sec
    public @Inject
    ApiService apiService;
    public @Inject
    SessionManager sessionManager;
    public @Inject
    CommonMethods commonMethods;
    public @Inject
    CustomDialog customDialog;
    public AlertDialog dialog;
    public boolean gps_enabled = false;
    public boolean network_enabled = false;
    public DecimalFormat twoDForm;
    protected boolean isInternetAvailable;
    LocationService.LocationResult locationResult;

    // Common variables
    Context context;
    private PowerManager.WakeLock wakeLock;

    // Firebase variables
    private DatabaseReference mFirebaseDatabase;
    private ValueEventListener mSearchedLocationReferenceListener;
    private LocationListener listener;
    private LocationManager locationManager;
    private Location location = null;
    private Location currentLocation = null;
    private Location lastLocation = null;

    // Distance calculation
    private float distance;


    public UpdateGPSWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
        CommonMethods.DebuggableLogE("locationupdate", "UpdateGPSWorker");
        this.context = context;
        AppController.getAppComponent().inject(this);
        PowerManager mgr = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
        wakeLock = mgr.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "MyWakeLock");
        mFirebaseDatabase = FirebaseDatabase.getInstance().getReference(CommonKeys.LiveTrackingFirebaseDatabaseName);
        locationListener();
    }

    @NonNull
    @Override
    public Result doWork() {
        //checkGpsEnable();
        Log.e(TAG, "doWork: running");
       /*locationResult = new LocationService.LocationResult() {
            @Override
            public void gotLocation(Location location) {
                System.out.println("Check4");
                if (location == null) return;
                sessionManager.setCurrentLatitude(String.valueOf(location.getLatitude()));
                sessionManager.setCurrentLongitude(String.valueOf(location.getLongitude()));
                //latitude = location.getLatitude();
                //longitude = location.getLongitude();
            }
        };*/
        CommonMethods.DebuggableLogE("locationupdate", "doWork");
        locationupdateCall();
        startForeService();

        return Result.success();
    }

    /******************************************************
     * Location Listener
     ******************************************************/
    /**
     * Check GPS enable or not
     */
    private void checkGpsEnable() {
        boolean isGpsEnabled = LocationService.defaultHandler().isLocationAvailable(context);
        if (!isGpsEnabled) {
            //startActivityForResult(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS), 101);
            AlertDialog.Builder dialog = new AlertDialog.Builder(context);
            dialog.setMessage(R.string.location_not_enabled);
            dialog.setPositiveButton(R.string.location_settings, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                    Intent myIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                    context.startActivity(myIntent);
                }
            });
            dialog.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                    // TODO Auto-generated method stub

                }
            });
            dialog.show();
        } else {
            //isPermissionGranted = true;
            LocationService.defaultHandler().getLocation(context, locationResult);
        }
    }

    public void locationupdateCall() {
        if (!TextUtils.isEmpty(sessionManager.getCurrentLatitude()) && !TextUtils.isEmpty(sessionManager.getCurrentLongitude())) {
            if(!TextUtils.isEmpty(sessionManager.getLastLatitude()) && !TextUtils.isEmpty(sessionManager.getLastLongitude()))
            {
                Double lat = Double.valueOf(sessionManager.getLastLatitude());
                Double lng = Double.valueOf(sessionManager.getLastLongitude());
                lastLocation = new Location("lastloc");
                lastLocation.setLatitude(lat);
                lastLocation.setLongitude(lng);
            }

            Double lat = Double.valueOf(sessionManager.getCurrentLatitude());
            Double lng = Double.valueOf(sessionManager.getCurrentLongitude());
            currentLocation = new Location("curloc");
            currentLocation.setLatitude(lat);
            currentLocation.setLongitude(lng);
            CommonMethods.DebuggableLogE("locationupdate", "Update Call currentLocation:" +  + currentLocation.getLatitude() + " , " + currentLocation.getLongitude());
            updateLocationChange();
        }
    }

    public void locationListener() {

        listener = new LocationListener() {
            @Override
            public void onLocationChanged(Location LastLocation) {
                CommonMethods.DebuggableLogD("updateGPSWorker Loc Changed", LastLocation.toString());

                sessionManager.setCurrentLatitude(Double.toString(LastLocation.getLatitude()));
                sessionManager.setLatitude(Double.toString(LastLocation.getLatitude()));
                sessionManager.setCurrentLongitude(Double.toString(LastLocation.getLongitude()));
                sessionManager.setLongitude(Double.toString(LastLocation.getLongitude()));

                System.out.println("Trips Status :  "+sessionManager.getTripStatus());
                if (sessionManager.getTripId() != null && !sessionManager.getTripId().equals("")
                        &&sessionManager.getTripStatus() != null && !sessionManager.getTripStatus().equals("")&& !sessionManager.getTripStatus().equals(CommonKeys.TripStaus.EndTrip)
                ) {
                    float distance = 0;
                    if (location != null) {
                        distance = LastLocation.distanceTo(location);
                        System.out.println("distance "+distance);
                        if (distance < 30) {
                            System.out.println("is Calling ");
                            updateLocationFireBase(Double.toString(LastLocation.getLatitude()), Double.toString(LastLocation.getLongitude()), sessionManager.getTripId());
                        }
                    }
                } else {
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

        locationManager.removeUpdates(listener);

        //exceptions will be thrown if provider is not permitted.
        try {
            gps_enabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        } catch (Exception ex) {
        }
        try {
            network_enabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        } catch (Exception ex) {
        }

        /*try {
            new Handler(Looper.getMainLooper()).post(new Runnable() {
                @Override
                public void run() {
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
                    if (network_enabled)
                        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, LOCATION_INTERVAL, LOCATION_DISTANCE, listener);

                    if (gps_enabled)
                        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, LOCATION_INTERVAL, LOCATION_DISTANCE, listener);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }*/

        //All your normal criteria setup
        Criteria criteria = new Criteria();
//Use FINE or COARSE (or NO_REQUIREMENT) here
        criteria.setAccuracy(Criteria.ACCURACY_FINE);
        criteria.setPowerRequirement(Criteria.POWER_LOW);
        criteria.setAltitudeRequired(true);
        criteria.setSpeedRequired(true);
        criteria.setCostAllowed(true);
        criteria.setBearingRequired(true);

//API level 9 and up
        criteria.setHorizontalAccuracy(Criteria.ACCURACY_HIGH);
        criteria.setVerticalAccuracy(Criteria.ACCURACY_HIGH);
        criteria.setBearingAccuracy(Criteria.ACCURACY_LOW);
        criteria.setSpeedAccuracy(Criteria.ACCURACY_HIGH);
        String provider=locationManager.getBestProvider(criteria, true);

        Log.e(TAG, "AllProviders = " + locationManager.getAllProviders().toString());
        Log.e(TAG, "Provider [" + provider + "] best criteria.");

        try {
            new Handler(Looper.getMainLooper()).post(new Runnable() {
                @Override
                public void run() {
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

                    locationManager.requestLocationUpdates(provider, LOCATION_INTERVAL, LOCATION_DISTANCE, listener);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    /****************************************************************
     * Update Lat and Lng in Fire base for live tracking
     ****************************************************************/

    /**
     * Creating new user node under 'users'
     */
    private void updateLocationFireBase(String lat, String lng, String tripid) {
        // TODO

        DriverLocation driverLocation = new DriverLocation(lat, lng);

        mFirebaseDatabase.child(tripid).setValue(driverLocation);

        if (mSearchedLocationReferenceListener == null) {
            addLatLngChangeListener(); // Get Driver Lat Lng
        }
    }

    /**
     * Driver LOCATION_AND_WRITEPERMISSION_ARRAY change listener
     */
    private void addLatLngChangeListener() {

        // User data change listener
        final Query query = mFirebaseDatabase.child(sessionManager.getTripId());

        mSearchedLocationReferenceListener = query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                if (sessionManager.getTripId() != null) {
                    DriverLocation driverLocation = dataSnapshot.getValue(DriverLocation.class);

                    // Check for null
                    if (driverLocation == null) {
                        return;
                    }
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


    /***********************************************************
     * Update location API functions
     ***********************************************************/

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

    /**
     * Update driver current location
     */
    public void updateLocation(HashMap<String, String> locationHashMap) {
        isInternetAvailable = commonMethods.isOnline(context);
        if (isInternetAvailable)
            apiService.updateLocation(locationHashMap).enqueue(new RequestCallback(REQ_UPDATE_ONLINE, this));
    }

    @Override
    public void onSuccess(JsonResponse jsonResp, String data) {
    }

    @Override
    public void onFailure(JsonResponse jsonResp, String data) {

    }


    /**************************************************
     * Wake Lock
     **************************************************/
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
            e.printStackTrace();
        }
    }

    /*
     * Get location change and update
     */
    public void updateLocationChange() {

        int duration = 10;
        String tripStatus = sessionManager.getTripStatus();
        /*String beginTrip = context.getResources().getString(R.string.begin_trip);
        String confirmTrip = context.getResources().getString(R.string.confirm_arrived);*/
        String beginTrip = CommonKeys.TripStaus.BeginTrip;
        String confirmTrip = CommonKeys.TripStaus.ConfirmArrived;

        /*
         * User offline release wake lock
         */
        if (sessionManager.getDriverStatus().equals("Offline")) {
            if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                releaseWakeLock();
            }
        } else {
            if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                acquireWakeLock();
            }
        }

        twoDForm = new DecimalFormat("#.#######");

        /*
         * Send location to broadcast
         */

        if (tripStatus != null && tripStatus.equals(beginTrip)) {
            sessionManager.setDriverStatus(CommonKeys.DriverStatus.Online);
        }

        /*
         *  Update location
         */

        if (lastLocation == null)
            lastLocation = currentLocation;

        CommonMethods.DebuggableLogE("locationupdate", "lastLocation:" +  + lastLocation.getLatitude() + " , " + lastLocation.getLongitude());
        CommonMethods.DebuggableLogE("locationupdate", "currentLocation:" +  + currentLocation.getLatitude() + " , " + currentLocation.getLongitude());

        sessionManager.setLastLatitude(String.valueOf(currentLocation.getLatitude()));
        sessionManager.setLastLongitude(String.valueOf(currentLocation.getLongitude()));

        if (sessionManager.getTotalDistance()>0||lastLocation.getLatitude() != currentLocation.getLatitude() || lastLocation.getLongitude() != currentLocation.getLongitude()) {
            if (tripStatus != null && tripStatus.equals(beginTrip)) {


                distance = 0;

                float distanceInMeters = lastLocation.distanceTo(currentLocation);

                distance = (float) (distanceInMeters / 1000.0);


                try {
                    distance = Float.valueOf(twoDForm.format(distance).replaceAll(",", "."));
                } catch (Exception e) {
                    e.printStackTrace();
                }

                CommonMethods.DebuggableLogE("locationupdate", "Distance:" + distance+"Max Distance"+MAX_DISTANCE);
                if ((distance*1000) < MAX_DISTANCE) {
                    LatLng newLatLng = new LatLng(Double.valueOf(sessionManager.getLatitude()), Double.valueOf(sessionManager.getLongitude()));
                    //latLngList.add(newLatLng);
                    RequestAcceptActivity.routePoints.add(newLatLng);

                    isInternetAvailable = commonMethods.isOnline(context);
                    if (isInternetAvailable) {
                        float offlineDistance = sessionManager.getTotalDistance();

                        distance = distance + offlineDistance;
                        sessionManager.setTotalDistance(0);

                        CommonMethods.DebuggableLogE("locationupdate", "Distance:" + distance);

                        HashMap<String, String> locationHashMap = new HashMap<>();
                        locationHashMap.put("latitude", sessionManager.getLatitude());
                        locationHashMap.put("longitude", sessionManager.getLongitude());
                        locationHashMap.put("total_km", String.valueOf(distance));
                        locationHashMap.put("user_type", sessionManager.getType());
                        locationHashMap.put("car_id", sessionManager.getVehicleId());
                        locationHashMap.put("trip_id", sessionManager.getTripId());
                        locationHashMap.put("status", "Trip");
                        locationHashMap.put("token", sessionManager.getAccessToken());

                        updateLocation(locationHashMap);
                    } else {
                        CommonMethods.DebuggableLogE("locationupdate", "Offline Distance:" +(sessionManager.getTotalDistance()+distance));
                        sessionManager.setTotalDistance(sessionManager.getTotalDistance() + distance);
                    }

                }

               startForeService();

            } else if (tripStatus != null && tripStatus.equals(confirmTrip)) {

                HashMap<String, String> locationHashMap = new HashMap<>();
                locationHashMap.put("latitude", sessionManager.getLatitude());
                locationHashMap.put("longitude", sessionManager.getLongitude());
                locationHashMap.put("total_km", "0");
                locationHashMap.put("user_type", sessionManager.getType());
                locationHashMap.put("car_id", sessionManager.getVehicleId());
                locationHashMap.put("trip_id", sessionManager.getTripId());
                locationHashMap.put("status", sessionManager.getDriverStatus());
                locationHashMap.put("token", sessionManager.getAccessToken());
                updateLocation(locationHashMap);
            } else {
                duration = 30;
                if ((sessionManager.getDriverStatus() != null) && sessionManager.getDriverStatus().equals(context.getResources().getString(R.string.online))) {
                    HashMap<String, String> locationHashMap = new HashMap<>();
                    locationHashMap.put("latitude", sessionManager.getLatitude());
                    locationHashMap.put("longitude", sessionManager.getLongitude());
                    locationHashMap.put("user_type", sessionManager.getType());
                    locationHashMap.put("car_id", sessionManager.getVehicleId());
                    locationHashMap.put("status", sessionManager.getDriverStatus());
                    locationHashMap.put("token", sessionManager.getAccessToken());
                    updateLocation(locationHashMap);
                }

            }
        }

        Intent j = new Intent("location_update");
        j.putExtra("type", "Updates");
        j.putExtra("Lat", Double.valueOf(sessionManager.getLatitude()));
        j.putExtra("Lng", Double.valueOf(sessionManager.getLongitude()));
        j.putExtra("km", String.valueOf(distance));
        j.putExtra("status", sessionManager.getTripStatus());
        context.sendBroadcast(j);

        if (!WorkerUtils.isWorkRunning(CommonKeys.WorkTagForUpdateGPS)) {
            CommonMethods.DebuggableLogE("locationupdate", "startWorkManager:");
            WorkerUtils.startWorkManager(CommonKeys.WorkKeyForUpdateGPS, CommonKeys.WorkTagForUpdateGPS, duration, UpdateGPSWorker.class,false);
        }else {
            CommonMethods.DebuggableLogE("locationupdate", "cancel and startWorkManager:");
            WorkerUtils.cancelWorkByTag(CommonKeys.WorkTagForUpdateGPS);
            WorkerUtils.startWorkManager(CommonKeys.WorkKeyForUpdateGPS, CommonKeys.WorkTagForUpdateGPS, duration, UpdateGPSWorker.class,false);
        }

        //startForeService();
    }

    private void startForeService(){
        //if (!isServiceRunning(getApplicationContext(), "ForeService")) {
            Intent intent = new Intent(getApplicationContext(), ForeService.class);
            //intent.setAction("android.intent.action.RESPOND_VIA_MESSAGE");
            try {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    CommonMethods.DebuggableLogE("Foreground Service", "o and above");
                    context.startForegroundService(intent);
                } else {
                    CommonMethods.DebuggableLogE("Foreground Service", "below o");
                    context.startService(intent);
                }
            } catch (Exception e) {
            }
       // }
    }

    private boolean isServiceRunning(Context context, String serviceName) {
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        if (am == null) {
            return false;
        }
        List<ActivityManager.RunningServiceInfo> infos = am.getRunningServices(100);
        for (ActivityManager.RunningServiceInfo info : infos) {
            String className = info.service.getClassName();
            if(serviceName.equals(className))
                return true;
        }
        return false;
    }


}
