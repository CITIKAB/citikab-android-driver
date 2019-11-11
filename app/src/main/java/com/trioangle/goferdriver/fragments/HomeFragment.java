package com.trioangle.goferdriver.fragments;
/**
 * @package com.trioangle.goferdriver.fragments
 * @subpackage fragments
 * @category HomeFragment
 * @author Trioangle Product Team
 * @version 1.5
 */

import android.Manifest;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.PowerManager;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.LinearInterpolator;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.trioangle.goferdriver.R;
import com.trioangle.goferdriver.configs.SessionManager;
import com.trioangle.goferdriver.helper.LatLngInterpolator;
import com.trioangle.goferdriver.map.AppUtils;
import com.trioangle.goferdriver.map.GpsService;
import com.trioangle.goferdriver.network.AppController;
import com.trioangle.goferdriver.network.PermissionCamer;
import com.trioangle.goferdriver.service.UpdateGPSWorker;
import com.trioangle.goferdriver.service.WorkerUtils;
import com.trioangle.goferdriver.util.CommonKeys;
import com.trioangle.goferdriver.util.CommonMethods;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.Locale;

import javax.inject.Inject;

import butterknife.ButterKnife;

import static android.content.Context.POWER_SERVICE;
import static com.trioangle.goferdriver.MainActivity.selectedFrag;

/* ************************************************************
                      HomeFragment
Its used get home screen main fragment details
*************************************************************** */
public class HomeFragment extends Fragment implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, LocationListener {

    protected static final int REQUEST_CHECK_SETTINGS = 0x1;
    private final static int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
    private static String TAG = "MAP LOCATION";
    public final ArrayList movepoints = new ArrayList();
    public @Inject
    SessionManager sessionManager;
    public GoogleMap mMap;
    public MapView mMapView;
    public Context mContext;
    public LatLng newLatLng = null;
    public View v;
    public Marker carmarker;
    public float startbear = 0;
    public float endbear = 0;
    public Marker marker;
    public boolean samelocation = false;
    public float speed = 13f;
    public Handler handler_movemap;
    public ValueAnimator valueAnimator;
    public DecimalFormat twoDForm;
    public @Inject
    CommonMethods commonMethods;
    protected boolean isInternetAvailable;
    private GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;
    private Location lastLocation = null;

    public static HomeFragment newInstance() {
        HomeFragment fragment = new HomeFragment();
        selectedFrag=1;
        return fragment;
    }

    /*
     *  Rotate marker
     **/
    private static float computeRotation(float fraction, float start, float end) {
        float normalizeEnd = end - start; // rotate start to 0
        float normalizedEndAbs = (normalizeEnd + 360) % 360;

        float direction = (normalizedEndAbs > 180) ? -1 : 1; // -1 = anticlockwise, 1 = clockwise
        float rotation;
        if (direction > 0) {
            rotation = normalizedEndAbs;
        } else {
            rotation = normalizedEndAbs - 360;
        }

        float result = fraction * rotation + start;
        return (result + 360) % 360;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        v = inflater.inflate(R.layout.fragment_home, container, false);
        ButterKnife.inject(this, v);
        AppController.getAppComponent().inject(this);
        isInternetAvailable = commonMethods.isOnline(getContext());
        mMapView = (MapView) v.findViewById(R.id.mapview);
        mMapView.onCreate(savedInstanceState);
        mMapView.getMapAsync(this); //this is important

        mContext = container.getContext();
        handler_movemap = new Handler();
        /*
         *  Request permission for barrery optimization for run service for background
         **/
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            Intent intent = new Intent();
            String packageName = getContext().getPackageName();
            PowerManager pm = (PowerManager) getContext().getSystemService(POWER_SERVICE);
            if (!pm.isIgnoringBatteryOptimizations(packageName)) {
                intent.setAction(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS);
                intent.setData(Uri.parse("package:" + packageName));
                startActivity(intent);
            }
        }

        twoDForm = new DecimalFormat("#.##########");
        DecimalFormatSymbols dfs = new DecimalFormatSymbols();
        dfs.setDecimalSeparator('.');
        twoDForm.setDecimalFormatSymbols(dfs);

        /*
         *  Request permission for location
         **/

        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            // Do something for lollipop and above versions
            showPermissionDialog();
        } else {
            // do something for phones running an SDK before lollipop
            checkGPSEnable();
        }

        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(1000);
        mLocationRequest.setFastestInterval(1000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        return v;
    }

    /*
     *  Show location permission dialog
     **/
    private void showPermissionDialog() {
        System.out.println("Permission check "+!PermissionCamer.checkPermission(mContext));
        if (!PermissionCamer.checkPermission(mContext)) {
            // android.app.Fragment fragment=(android.app.Fragment)
            requestPermissions(
                    new String[]{android.Manifest.permission.ACCESS_COARSE_LOCATION, android.Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.WRITE_EXTERNAL_STORAGE,
                            Manifest.permission.READ_EXTERNAL_STORAGE},
                    4);
            displayLocationSettingsRequest(mContext);

        } else {
            buildGoogleApiClient();
            //displayLocationSettingsRequest(mContext);
        }
    }

    /*
     *  Request for location permission
     **/
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode) {
            case 4: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        &&grantResults[1]== PackageManager.PERMISSION_GRANTED) {
                    buildGoogleApiClient();
                    displayLocationSettingsRequest(mContext);

                }
                return;
            }
            default: {
                break;
            }
            // other 'case' lines to check for other
            // permissions this app might request
        }
    }

    /*
     *  Check location permission enable or not and show dialog
     **/
    public void checkGPSEnable() {
        if (checkPlayServices()) {
            // If this check succeeds, proceed with normal processing.
            // Otherwise, prompt user to get valid Play Services APK.
            if (!AppUtils.isLocationEnabled(mContext)) {
                // notify user
                AlertDialog.Builder dialog = new AlertDialog.Builder(mContext);
                dialog.setMessage(R.string.location_not_enabled);
                dialog.setPositiveButton(R.string.location_settings, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                        Intent myIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        startActivity(myIntent);
                    }
                });
                dialog.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                        // TODO Auto-generated method stub

                    }
                });
                dialog.show();
                buildGoogleApiClient();
            } else {
                buildGoogleApiClient();
            }
        }
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    public void onMapReady(GoogleMap googleMap) {
        CommonMethods.DebuggableLogV("Locale", "locale==" + getResources().getConfiguration().locale);
        CommonMethods.DebuggableLogV("Locale", "locale==" + Locale.ENGLISH);
        mMap = googleMap;


        if (ActivityCompat.checkSelfPermission(this.mContext, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this.mContext, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        mMap.setMyLocationEnabled(false);
        mMap.getUiSettings().setMyLocationButtonEnabled(false);

    }

    @Override
    public void onConnected(Bundle bundle) {
        if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this.mContext, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }

        try {

            LocationServices.FusedLocationApi.requestLocationUpdates(
                    mGoogleApiClient, mLocationRequest, this);

        } catch (Exception e) {
            e.printStackTrace();
        }


        Location mLastLocation = LocationServices.FusedLocationApi.getLastLocation(
                mGoogleApiClient);

        if (mLastLocation != null) {
            if (sessionManager.getDriverStatus().equals("Online")) {
                /*Intent GPSservice = new Intent(mContext, GpsService.class);
                if (!isMyServiceRunning(GpsService.class)) {
                    mContext.startService(GPSservice);
                }*/
                if(!WorkerUtils.isWorkRunning(CommonKeys.WorkTagForUpdateGPS)) {
                    CommonMethods.DebuggableLogE("locationupdate", "StartWork:");
                    WorkerUtils.startWorkManager(CommonKeys.WorkKeyForUpdateGPS, CommonKeys.WorkTagForUpdateGPS, UpdateGPSWorker.class);
                }
            }
            changeMap(mLastLocation);
            CommonMethods.DebuggableLogD(TAG, "ON connected");

        } else
            try {
                LocationServices.FusedLocationApi.removeLocationUpdates(
                        mGoogleApiClient, this);

            } catch (Exception e) {
                e.printStackTrace();
            }
        try {

            LocationServices.FusedLocationApi.requestLocationUpdates(
                    mGoogleApiClient, mLocationRequest, this);

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /*
     *  Update location on Change
     **/

    @Override
    public void onConnectionSuspended(int i) {
        CommonMethods.DebuggableLogI(TAG, "Connection suspended");
        mGoogleApiClient.connect();
    }

    @Override
    public void onLocationChanged(Location location) {
        try {
            if (location != null) {
                //Toast.makeText(getActivity(), "Current speed:" + location.getSpeed(),Toast.LENGTH_SHORT).show();
                speed = location.getSpeed();

                float calculatedSpeed = 0;
                if (lastLocation != null) {
                    double elapsedTime = (location.getTime() - lastLocation.getTime()) / 1_000; // Convert milliseconds to seconds
                    if (elapsedTime <= 0)
                        elapsedTime = 1;
                    calculatedSpeed = (float) (lastLocation.distanceTo(location) / elapsedTime);
                }
                this.lastLocation = location;

                float speedcheck = location.hasSpeed() ? location.getSpeed() : calculatedSpeed;
                if (!Float.isNaN(speedcheck) && !Float.isInfinite(speedcheck))
                    speed = speedcheck;

                if (speed <= 0)
                    speed = 10;

                changeMap(location);
                sessionManager.setCurrentLatitude(Double.toString(location.getLatitude()));
                sessionManager.setCurrentLongitude(Double.toString(location.getLongitude()));
                if (sessionManager.getDriverStatus().equals("Online")) {
                    /*Intent GPSservice = new Intent(mContext, GpsService.class);
                    if (!isMyServiceRunning(GpsService.class)) {
                        mContext.startService(GPSservice);
                    }*/
                    if(!WorkerUtils.isWorkRunning(CommonKeys.WorkTagForUpdateGPS)) {
                        CommonMethods.DebuggableLogE("locationupdate", "StartWork:");
                        WorkerUtils.startWorkManager(CommonKeys.WorkKeyForUpdateGPS, CommonKeys.WorkTagForUpdateGPS, UpdateGPSWorker.class);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    /*
     *  Update Google connection
     **/

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {


    }

    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this.mContext)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();

        try {
            mGoogleApiClient.connect();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        try {
            mGoogleApiClient.connect();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onStop() {
        super.onStop();

        if (mGoogleApiClient != null && mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
        }
    }

    /*
     *  Check Google play service enable or not
     **/
    private boolean checkPlayServices() {
        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this.mContext);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
                GooglePlayServicesUtil.getErrorDialog(resultCode, this.getActivity(),
                        PLAY_SERVICES_RESOLUTION_REQUEST).show();
            }
            return false;
        }
        return true;
    }

    /*
     *  Get driver current location in map
     **/
    private void changeMap(Location location) {

        CommonMethods.DebuggableLogD(TAG, "Reaching map" + mMap);


        if (ActivityCompat.checkSelfPermission(this.mContext, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this.mContext, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }

        // check if map is created successfully or not
        if (mMap != null) {
            //mMap.clear();
            LatLng latLong;

            mMap.getUiSettings().setZoomControlsEnabled(false);
            mMap.setMyLocationEnabled(false);
            mMap.getUiSettings().setMyLocationButtonEnabled(false);

            latLong = new LatLng(location.getLatitude(), location.getLongitude());

            if (newLatLng == null) {


                CameraPosition cameraPosition = new CameraPosition.Builder()
                        .target(latLong).zoom(16.5f).build();
                mMap.moveCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));

                MarkerOptions pickupOptions = new MarkerOptions();

                // Setting the position of the marker
                pickupOptions.position(latLong);
                pickupOptions.anchor(0.5f, 0.5f);


                pickupOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.carmap));
                // Add new marker to the Google Map Android API V2
                carmarker = mMap.addMarker(pickupOptions);
                //carmarker.setRotation((float) (startbear * (180.0 / 3.14)));
                //carmarker.setFlat(true);
            }

            moveMarker(latLong);

            newLatLng = latLong;


            double longitude = location.getLongitude();
            double latitude = location.getLatitude();

            String log = String.valueOf(longitude);
            String lat = String.valueOf(latitude);
            sessionManager.setLatitude(lat);
            sessionManager.setLongitude(log);

        }

    }


    @Override
    public void onResume() {
        super.onResume();

        mMapView.onResume();

    }

    @Override
    public void onPause() {
        super.onPause();
        mMapView.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mMapView.onDestroy();
        //  LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mMapView.onSaveInstanceState(outState);
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mMapView.onLowMemory();
    }


    /*
     *  Show location request for setting page
     **/
    private void displayLocationSettingsRequest(Context context) {
        GoogleApiClient googleApiClient = new GoogleApiClient.Builder(context)
                .addApi(LocationServices.API).build();
        googleApiClient.connect();

        LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(5000);
        locationRequest.setFastestInterval(5000 / 2);

        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder().addLocationRequest(locationRequest);
        builder.setAlwaysShow(true);

        PendingResult<LocationSettingsResult> result = LocationServices.SettingsApi.checkLocationSettings(googleApiClient, builder.build());
        result.setResultCallback(new ResultCallback<LocationSettingsResult>() {
            @Override
            public void onResult(LocationSettingsResult result) {
                final Status status = result.getStatus();
                switch (status.getStatusCode()) {
                    case LocationSettingsStatusCodes.SUCCESS:
                        CommonMethods.DebuggableLogI(TAG, "All location settings are satisfied.");
                        break;
                    case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                        CommonMethods.DebuggableLogI(TAG, "Location settings are not satisfied. Show the user a dialog to upgrade location settings ");

                        try {
                            // Show the dialog by calling startResolutionForResult(), and check the result
                            status.startResolutionForResult((Activity) mContext, REQUEST_CHECK_SETTINGS);
                        } catch (IntentSender.SendIntentException e) {
                            CommonMethods.DebuggableLogI(TAG, "PendingIntent unable to execute request.");
                        }
                        break;
                    case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                        CommonMethods.DebuggableLogI(TAG, "Location settings are inadequate, and cannot be fixed here. Dialog not created.");
                        break;
                    default: {
                        break;
                    }
                }
            }
        });
    }

    /* ***************************************************************** */
    /*                  Animate Marker for Live Tracking                 */
    /* ***************************************************************** */

    /*
     *  Check service is running or not
     **/
    private boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) mContext.getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    /*
     *  Move marker for given location(Live tracking)
     **/
    public void moveMarker(LatLng latLng) {

        if (movepoints.size() < 1) {
            movepoints.add(0, latLng);
            movepoints.add(1, latLng);

        } else {
            movepoints.set(1, movepoints.get(0));
            movepoints.set(0, latLng);
        }

        DecimalFormat twoDForm = new DecimalFormat("#.#######");
        DecimalFormatSymbols dfs = new DecimalFormatSymbols();
        dfs.setDecimalSeparator('.');
        twoDForm.setDecimalFormatSymbols(dfs);

        String zerolat = twoDForm.format(((LatLng) movepoints.get(0)).latitude);
        String zerolng = twoDForm.format(((LatLng) movepoints.get(0)).longitude);

        String onelat = twoDForm.format(((LatLng) movepoints.get(1)).latitude);
        String onelng = twoDForm.format(((LatLng) movepoints.get(1)).longitude);

        if (!zerolat.equals(onelat) || !zerolng.equals(onelng)) {
            adddefaultMarker((LatLng) movepoints.get(1), (LatLng) movepoints.get(0));
            samelocation = false;
        }
    }
    /*
     *  Move marker
     **/

    /*
     *  Move marker for given locations
     **/
    public void adddefaultMarker(LatLng latlng, LatLng latlng1) {
        Location startbearlocation = new Location(LocationManager.GPS_PROVIDER);
        Location endbearlocation = new Location(LocationManager.GPS_PROVIDER);

        startbearlocation.setLatitude(latlng.latitude);
        startbearlocation.setLongitude(latlng.longitude);

        endbearlocation.setLatitude(latlng1.latitude);
        endbearlocation.setLongitude(latlng1.longitude);

        if (endbear != 0.0) {
            startbear = endbear;
        }


        //carmarker.setPosition(latlng);
        //carmarker.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.carmap));
        carmarker.setFlat(true);
        carmarker.setAnchor(0.5f, 0.5f);
        marker = carmarker;
        // Move map while marker gone
        ensureMarkerOnBounds(latlng, "updated");

        endbear = (float) bearing(startbearlocation, endbearlocation);
        endbear = (float) (endbear * (180.0 / 3.14));
        CommonMethods.DebuggableLogV("float", "doublehain" + startbearlocation.distanceTo(endbearlocation));
        //double distance = Double.valueOf(twoDForm.format(startbearlocation.distanceTo(endbearlocation)));
        double distance = Double.valueOf((startbearlocation.distanceTo(endbearlocation)));

        if (distance > 0)
            animateMarker(latlng1, marker, speed, endbear);

    }

    public void animateMarker(final LatLng destination, final Marker marker, final float speed, final float endbear) {

        final LatLng[] newPosition = new LatLng[1];
        if (marker != null) {
            final LatLng startPosition = marker.getPosition();
            final LatLng endPosition = new LatLng(destination.latitude, destination.longitude);
            long duration;
            final Location newLoc = new Location(LocationManager.GPS_PROVIDER);
            newLoc.setLatitude(startPosition.latitude);
            newLoc.setLongitude(startPosition.longitude);
            Location prevLoc = new Location(LocationManager.GPS_PROVIDER);
            prevLoc.setLatitude(endPosition.latitude);
            prevLoc.setLongitude(endPosition.longitude);


            // double distance = Double.valueOf(twoDForm.format(newLoc.distanceTo(prevLoc)));
            double distance = Double.valueOf((newLoc.distanceTo(prevLoc)));

            duration = (long) ((distance / speed) * 1015);

            if (duration >= 1000)
                duration = 950;
            duration = 1015;

            final float startRotation = marker.getRotation();

            final LatLngInterpolator latLngInterpolator = new LatLngInterpolator.LinearFixed();
            if (valueAnimator != null) {
                valueAnimator.cancel();
                valueAnimator.end();
            }
            valueAnimator = ValueAnimator.ofFloat(0, 1f);
            valueAnimator.setDuration(duration);
            valueAnimator.setInterpolator(new LinearInterpolator());
            valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    try {
                        float v = animation.getAnimatedFraction();
                        newPosition[0] = latLngInterpolator.interpolate(v, startPosition, endPosition);
                        marker.setPosition(newPosition[0]); // Move Marker
                        marker.setAnchor(0.5f, 0.5f);
                        marker.setRotation(computeRotation(v, startRotation, endbear)); // Rotate Marker
                    } catch (Exception ex) {
                        // I don't care atm..
                    }
                }
            });

            valueAnimator.start();
        }
    }

    /*
     *  Find GPS rotate position
     **/
    private double bearing(Location startPoint, Location endPoint) {
        double deltaLongitude = endPoint.getLongitude() - startPoint.getLongitude();
        double deltaLatitude = endPoint.getLatitude() - startPoint.getLatitude();
        double angle = (3.14 * .5f) - Math.atan(deltaLatitude / deltaLongitude);

        if (deltaLongitude > 0) return angle;
        else if (deltaLongitude < 0) return angle + 3.14;
        else if (deltaLatitude < 0) return 3.14;

        return 0.0f;
    }

    /*
     *  move map to center position while marker hide
     **/
    private void ensureMarkerOnBounds(LatLng toPosition, String type) {
        if (marker != null) {
            float currentZoomLevel = (float) mMap.getCameraPosition().zoom;
            float bearing = (float) mMap.getCameraPosition().bearing;
            /*if (16.5f > currentZoomLevel) {
                currentZoomLevel = 16.5f;
            }*/
            CameraPosition cameraPosition = new CameraPosition.Builder()
                    .target(toPosition).zoom(currentZoomLevel).bearing(bearing).build();


            if ("updated".equals(type)) {
                mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
            } else {
                if (!mMap.getProjection().getVisibleRegion().latLngBounds.contains(toPosition)) {
                    mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
                }
            }
        }
    }
}
