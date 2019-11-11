package com.trioangle.goferdriver.home;
/**
 * @package com.trioangle.goferdriver.home
 * @subpackage home
 * @category RequestAcceptActivity
 * @author Trioangle Product Team
 * @version 1.5
 */

import android.Manifest;
import android.animation.ValueAnimator;
import android.app.ActivityManager;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.NetworkOnMainThreadException;
import android.os.ResultReceiver;
import android.os.StrictMode;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.provider.Settings;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.text.method.ScrollingMovementMethod;
import android.util.Base64;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.LinearInterpolator;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.gson.Gson;
import com.squareup.picasso.Picasso;
import com.trioangle.goferdriver.MainActivity;
import com.trioangle.goferdriver.R;
import com.trioangle.goferdriver.configs.SessionManager;
import com.trioangle.goferdriver.datamodel.RiderDetailsModel;
import com.trioangle.goferdriver.firebaseChat.ActivityChat;
import com.trioangle.goferdriver.firebaseChat.FirebaseChatHandler;
import com.trioangle.goferdriver.firebaseChat.FirebaseChatNotificationService;
import com.trioangle.goferdriver.helper.ComplexPreferences;
import com.trioangle.goferdriver.helper.CustomDialog;
import com.trioangle.goferdriver.helper.LatLngInterpolator;
import com.trioangle.goferdriver.interfaces.ApiService;
import com.trioangle.goferdriver.interfaces.ServiceListener;
import com.trioangle.goferdriver.map.AppUtils;
import com.trioangle.goferdriver.map.FetchAddressIntentService;
import com.trioangle.goferdriver.map.GpsService;
import com.trioangle.goferdriver.map.drawpolyline.DownloadTask;
import com.trioangle.goferdriver.map.drawpolyline.PolylineOptionsInterface;
import com.trioangle.goferdriver.model.JsonResponse;
import com.trioangle.goferdriver.network.AppController;
import com.trioangle.goferdriver.proswipebutton.ProSwipeButton;
import com.trioangle.goferdriver.pushnotification.Config;
import com.trioangle.goferdriver.pushnotification.NotificationUtils;
import com.trioangle.goferdriver.rating.PaymentAmountPage;
import com.trioangle.goferdriver.rating.Riderrating;
import com.trioangle.goferdriver.service.UpdateGPSWorker;
import com.trioangle.goferdriver.service.WorkerUtils;
import com.trioangle.goferdriver.util.CommonKeys;
import com.trioangle.goferdriver.util.CommonMethods;
import com.trioangle.goferdriver.util.OnSwipeTouchListener;
import com.trioangle.goferdriver.util.RequestCallback;
import com.trioangle.goferdriver.util.RuntimePermissionDialogFragment;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.ref.WeakReference;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

import javax.inject.Inject;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import id.zelory.compressor.Compressor;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

import static com.trioangle.goferdriver.util.Enums.REQ_ARRIVE_NOW;
import static com.trioangle.goferdriver.util.Enums.REQ_BEGIN_TRIP;
import static com.trioangle.goferdriver.util.Enums.REQ_END_TRIP;


import static com.trioangle.goferdriver.util.RuntimePermissionDialogFragment.STORAGEPERMISSIONARRAY;
import static com.trioangle.goferdriver.util.RuntimePermissionDialogFragment.checkPermissionStatus;
/* ************************************************************
                      RequestAcceptActivity
Its used to get RequestAcceptActivity for rider
*************************************************************** */

public class RequestAcceptActivity extends AppCompatActivity implements OnMapReadyCallback, LocationListener, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, com.google.android.gms.location.LocationListener, ServiceListener, RuntimePermissionDialogFragment.RuntimePermissionRequestedCallback {

    private final static int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
    private static final String TAG = "GPS Service accept";
    public static boolean isRequestAcceptActivity = true;
    public final ArrayList movepoints = new ArrayList();
    public static List<LatLng> routePoints =new ArrayList<LatLng>();

    private BroadcastReceiver act2InitReceiver;

    private String encodedImage;

    String imagePath = "";
    public @Inject
    ApiService apiService;
    public @Inject
    SessionManager sessionManager;
    public @Inject
    CommonMethods commonMethods;
    public @Inject
    Gson gson;
    public @Inject
    CustomDialog customDialog;
    public AlertDialog dialog;
    @InjectView(R.id.pickup_address)
    public TextView pickup_address;
    @InjectView(R.id.ridername)
    public TextView ridername;
    @InjectView(R.id.profileimg)
    public ImageView profileimg;
    @InjectView(R.id.user_details_lay)
    public LinearLayout user_details_lay;
    @InjectView(R.id.latlng)
    public TextView textView;
    @InjectView(R.id.latlng1)
    public TextView textView1;
    @InjectView(R.id.tripastatusbutton)
    public ProSwipeButton tripastatusbutton;
    @InjectView(R.id.navigation)
    LinearLayout navigation;
    @InjectView(R.id.cashtrip_lay)
    public RelativeLayout cashtrip_lay;
    private float totalDistanceVal;

    @InjectView(R.id.fab_start_chat)
    public FloatingActionButton fabChat;

    @OnClick(R.id.navigation)
    public void onclickNavigation() {
        load();
    }

    public LatLng newLatLng = null;
    public int count0 = 0;
    public int count1 = 0;
    public Polyline polyline;
    public Context mContext;
    public LatLng latLong;
    public RiderDetailsModel riderDetailsModel;
    public Marker carmarker;
    public float startbear = 0, endbear = 0;
    public Marker marker;
    public boolean samelocation = false;
    public boolean firstloop = true;
    public float speed = 13f;
    public Handler handler_movemap;
    public ValueAnimator valueAnimator;
    public DecimalFormat twoDForm;
    public TextView distance;
    public String imagepath;
    public String imageInSD;
    public String compressPath;
    private WeakReference<AppCompatActivity> compressImgWeakRef = new WeakReference<>(this);

    /**
     * The formatted location address.
     */
    protected String mAddressOutput;
    protected String mAreaOutput;
    protected String mCityOutput;
    protected String mStateOutput;
    protected boolean isInternetAvailable;
    private GoogleMap mMap;
    private BroadcastReceiver broadcastReceiver;
    private GoogleApiClient mGoogleApiClient;
    private BroadcastReceiver mRegistrationBroadcastReceiver;
    ComplexPreferences complexPreferences;

    /**
     * Receiver registered with this activity to get the response from FetchAddressIntentService.
     */
    private AddressResultReceiver mResultReceiver;
    private Location lastLocation = null;
    public Bitmap statmap;
    protected LocationRequest locationRequest;

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

    public Bitmap getBitmapFromURL(String src) {
        try {
            URL url = new URL(src);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();
            Bitmap myBitmap = BitmapFactory.decodeStream(input);
            return myBitmap;
        } catch (IOException e) {
            e.printStackTrace();
            return statmap;
        } catch (NetworkOnMainThreadException e) {
            e.printStackTrace();
            return statmap;
        } catch (Exception e) {
            e.printStackTrace();
            return statmap;
        }
    }

    @OnClick(R.id.profileimg)
    public void onclickProfile() {
        /*
         *  Redirect to profile page
         */
        Intent intent = new Intent(getApplicationContext(), RiderProfilePage.class);
        intent.putExtra("riderDetails", riderDetailsModel);
        startActivity(intent);
    }

    @OnClick(R.id.user_details_lay)
    public void onclickUser() {
        /*
         *  Redirect to profile page
         */
        Intent intent = new Intent(getApplicationContext(), RiderProfilePage.class);
        intent.putExtra("riderDetails", riderDetailsModel);
        startActivity(intent);
    }

    @OnClick(R.id.fab_start_chat)
    public void startChating() {
        try {
            sessionManager.setRiderName(riderDetailsModel.getRiderName());
            sessionManager.setRiderRating(riderDetailsModel.getRatingValue());
            sessionManager.setRiderProfilePic(riderDetailsModel.getRiderThumbImage());
        } catch (Exception e) {
            e.printStackTrace();
        }
        startActivity(new Intent(this, ActivityChat.class));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.activity_request_accept);
        ButterKnife.inject(this);
        AppController.getAppComponent().inject(this);
        initView();


    }

    private void initView() {
        //start firebase chat listernet service
        startFirebaseChatListenerService();

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        hideChatAccordingToBookingType();
        complexPreferences = ComplexPreferences.getComplexPreferences(this, "mypref", MODE_PRIVATE);

        distance = (TextView) findViewById(R.id.distance);
        statmap = BitmapFactory.decodeResource(getResources(), R.drawable.mapimg);
        dialog = commonMethods.getAlertDialog(this);
        textView.setMovementMethod(new ScrollingMovementMethod());
        textView1.setMovementMethod(new ScrollingMovementMethod());
        twoDForm = new DecimalFormat("#.##########");
        DecimalFormatSymbols dfs = new DecimalFormatSymbols();
        dfs.setDecimalSeparator('.');
        twoDForm.setDecimalFormatSymbols(dfs);
        isInternetAvailable = commonMethods.isOnline(this);


        cashtrip_lay.setVisibility(View.GONE);


        act2InitReceiver= new BroadcastReceiver()
        {

            @Override
            public void onReceive(Context context, Intent intent)
            {
                /*countries = (List<PastTripModel>) intent
                        .getSerializableExtra("list");*/
                // do your listener event stuff

                String internetcheck = intent.getStringExtra("list");
                float distanceVal = intent.getFloatExtra("dist",0);
                totalDistanceVal = totalDistanceVal+distanceVal;

                distance.setText(internetcheck+":"+sessionManager.getTotalDistance());

            }
        };
        LocalBroadcastManager.getInstance(this).registerReceiver(act2InitReceiver, new IntentFilter("activity-2-initialized"));

        /*
         *  If current trip payment method is cash show the cash button
         */
        if (sessionManager.getPaymentMethod().contains("Cash")) {
            cashtrip_lay.setVisibility(View.VISIBLE);
        }
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        mContext = this;
        handler_movemap = new Handler();
        mResultReceiver = new AddressResultReceiver(new Handler());

        /*
         *  Check GPS enable or not
         */
        checkGPSEnable();


        pickup_address.setMovementMethod(new ScrollingMovementMethod());

        /*
         *  Get Trip rider details
         */
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            riderDetailsModel = (RiderDetailsModel) getIntent().getSerializableExtra("riderDetails"); //Obtaining data
            if (getIntent().getIntExtra(CommonKeys.KEY_IS_NEED_TO_PLAY_SOUND, CommonKeys.NO) == CommonKeys.YES) {
                playNotificatinSoundAndViberate();
            }
        }

        tripastatusbutton.setSwipeDistance(0.5f);

        tripastatusbutton.setText(getIntent().getStringExtra("tripstatus"));
        try {
            if (sessionManager.getTripId() != null || !sessionManager.getTripId().equals("null") || !sessionManager.getTripId().equals("")) {
                tripastatusbutton.setText(sessionManager.getSubTripStatus());
            }
        } catch (NullPointerException n) {
            //null
            tripastatusbutton.setText(sessionManager.getSubTripStatus());
        } catch (Exception e) {
            //null
            tripastatusbutton.setText(sessionManager.getSubTripStatus());
        }
        pickup_address.setText(riderDetailsModel.getPickupLocation());
        ridername.setText(riderDetailsModel.getRiderName());
        String imageUr = riderDetailsModel.getRiderThumbImage();

        Picasso.with(getApplicationContext()).load(imageUr)
                .into(profileimg);



        tripastatusbutton.setOnSwipeListener(new ProSwipeButton.OnSwipeListener() {
            @Override
            public void onSwipeConfirm() {

                isInternetAvailable = commonMethods.isOnline(getApplicationContext());
                if (isInternetAvailable) {
                    tripFunction();
                }else{
                    tripastatusbutton.showResultIcon(false, true);
                    commonMethods.showMessage(mContext, dialog, getResources().getString(R.string.no_connection));

                }



            }
        });






        /*
         *  Receive push notification
         */
        receivepushnotification();
    }

    private void tripFunction() {

        isInternetAvailable = commonMethods.isOnline(getApplicationContext());


        if (tripastatusbutton.getText().toString().equals(getResources().getString(R.string.confirm_arrived))) {
            if (isInternetAvailable) {
                arriveRequest();
            } else {
                //tripastatusbutton.showResultIcon(false, true);
                commonMethods.showMessage(mContext, dialog, getResources().getString(R.string.no_connection));
            }

        } else if (tripastatusbutton.getText().toString().equals(getResources().getString(R.string.begin_trip))) {
            /*
             *  Begin trip API call
             */
            if (isInternetAvailable) {

                beginTrip();
                try {
                            /*GpsService gps_service = new GpsService();
                            gps_service.latLngList = new ArrayList<LatLng>();

                            LatLng newLatLng = new LatLng(Double.valueOf(sessionManager.getCurrentLatitude()), Double.valueOf(sessionManager.getCurrentLongitude()));
                            gps_service.latLngList.add(newLatLng);*/
                    routePoints =new ArrayList<LatLng>();
                    LatLng newLatLng = new LatLng(Double.valueOf(sessionManager.getCurrentLatitude()), Double.valueOf(sessionManager.getCurrentLongitude()));
                    routePoints.add(newLatLng);

                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                commonMethods.showMessage(mContext, dialog, getResources().getString(R.string.no_connection));
            }
        } else if (tripastatusbutton.getText().toString().equals(getResources().getString(R.string.end_trip))) {
            sessionManager.setDriverAndRiderAbleToChat(false);
            stopFirebaseChatListenerService();
            deleteFirbaseChatNode();
            LocalBroadcastManager.getInstance(mContext).sendBroadcast(new Intent("stop-foreground-service").putExtra("foreground", "stop"));

            /*
             *  End trip API call
             */
            complexPreferences.clearSharedPreferences();
            if (isInternetAvailable) {

                user_details_lay.setEnabled(false);

                if (Build.VERSION.SDK_INT > 15) {
                    final String[] permissions = {
                            Manifest.permission.WRITE_EXTERNAL_STORAGE,
                            Manifest.permission.READ_EXTERNAL_STORAGE};

                    final List<String> permissionsToRequest = new ArrayList<>();
                    for (String permission : permissions) {
                        if (ActivityCompat.checkSelfPermission(RequestAcceptActivity.this, permission) != PackageManager.PERMISSION_GRANTED) {
                            permissionsToRequest.add(permission);
                        }
                    }
                            /*if (!permissionsToRequest.isEmpty()) {
                                ActivityCompat.requestPermissions(RequestAcceptActivity.this, permissionsToRequest.toArray(new String[permissionsToRequest.size()]), 938);
                            } else*/
                    //endTrip();
                } else {
                    //endTrip();
                }
                try {
                           /* GpsService gps_service = new GpsService();

                            LatLng newLatLng = new LatLng(Double.valueOf(sessionManager.getCurrentLatitude()), Double.valueOf(sessionManager.getCurrentLongitude()));
                            gps_service.latLngList.add(newLatLng);*/

                    LatLng newLatLng = new LatLng(Double.valueOf(sessionManager.getCurrentLatitude()), Double.valueOf(sessionManager.getCurrentLongitude()));
                    routePoints.add(newLatLng);

                } catch (Exception e) {
                    e.printStackTrace();
                }

                checkAllPermission();




            } else {
                //tripastatusbutton.showResultIcon(false, true);
                commonMethods.showMessage(mContext, dialog, getResources().getString(R.string.no_connection));
            }
        }
    }


    private void checkAllPermission() {

        checkPermissionStatus(this, getSupportFragmentManager(), this, STORAGEPERMISSIONARRAY,0,0);
    }


    private void showGPSNotEnabledWarning() {

        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(getResources().getString(R.string.location_not_enabled_please_enable_location));
        builder.setCancelable(true);
        builder.setNegativeButton(getResources().getString(R.string.cancel), (dialogInterface, i) -> dialogInterface.dismiss());
        builder.setPositiveButton(getResources().getString(R.string.ok), (dialogInterface, i) -> AppUtils.openLocationEnableScreen(mContext));
        AlertDialog dialog = builder.create();
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE); //before
        dialog.show();

    }

    private void hideChatAccordingToBookingType() {
        if (sessionManager.getBookingType().equals(CommonKeys.RideBookedType.manualBooking)) {
            fabChat.setVisibility(View.GONE);
        }

    }

    private void playNotificatinSoundAndViberate() {
        try {
            MediaPlayer mPlayer = MediaPlayer.create(this, R.raw.gofer);
            mPlayer.start();
            mPlayer.setLooping(true);

            new Timer().schedule(new TimerTask() {
                @Override
                public void run() {
                    mPlayer.stop();
                    mPlayer.release();
                }
            }, 2000);
        } catch (Exception e) {
            e.printStackTrace();
        }


        try {
            Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
            // Vibrate for 500 milliseconds
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                if (v != null) {
                    v.vibrate(VibrationEffect.createOneShot(500, VibrationEffect.DEFAULT_AMPLITUDE));
                }
            } else {
                //deprecated in API 26
                assert v != null;
                v.vibrate(500);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void deleteFirbaseChatNode() {
        try {
            sessionManager.clearRiderNameRatingAndProfilePicture();
            FirebaseChatHandler.deleteChatNode(sessionManager.getTripId());
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void load() {
        View view = getLayoutInflater().inflate(R.layout.select_navigation_app_bottomsheet, null);
        LinearLayout googleMap = view.findViewById(R.id.llt_google_map);
        LinearLayout wazeMap = view.findViewById(R.id.llt_waze);


        final Dialog bottomSheetDialog = new Dialog(this, R.style.MaterialDialogSheet);
        bottomSheetDialog.setContentView(view);
        bottomSheetDialog.setCancelable(true);
        if (bottomSheetDialog.getWindow() == null) return;
        bottomSheetDialog.getWindow().setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        bottomSheetDialog.getWindow().setGravity(Gravity.BOTTOM);
        bottomSheetDialog.show();

        googleMap.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                bottomSheetDialog.dismiss();
                //cameraIntent();
                navigateViaGoogleMap();
            }
        });

        wazeMap.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                bottomSheetDialog.dismiss();
                navigateViaWazeMap();
            }
        });

    }


    private void startFirebaseChatListenerService() {
        startService(new Intent(this, FirebaseChatNotificationService.class));
    }

    private void stopFirebaseChatListenerService() {
        stopService(new Intent(this, FirebaseChatNotificationService.class));
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

    }

    /*
     *  Get direction for given locations
     */
    private String getDirectionsUrl(LatLng origin, LatLng dest) {

        // Origin of route
        String str_origin = "origin=" + origin.latitude + "," + origin.longitude;

        // Destination of route
        String str_dest = "destination=" + dest.latitude + "," + dest.longitude;

        // Sensor enabled
        String sensor = "sensor=false";
        String mode = "mode=driving";
        // Building the parameters to the web service
        String parameters = str_origin + "&" + str_dest + "&" + sensor + "&" + mode;

        // Output format
        String output = "json";

        // Building the url to the web service
        String url = "https://maps.googleapis.com/maps/api/directions/" + output + "?" + parameters + "&key=" + sessionManager.getGoogleMapKey();


        return url;
    }

    @Override
    public void onConnected(Bundle bundle) {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        Location mLastLocation = LocationServices.FusedLocationApi.getLastLocation(
                mGoogleApiClient);
        if (mLastLocation != null) {
            changeMap(mLastLocation);
            CommonMethods.DebuggableLogD(TAG, "ON connected" + mLastLocation);

        } else
            try {
                LocationServices.FusedLocationApi.removeLocationUpdates(
                        mGoogleApiClient, this);

            } catch (Exception e) {
                e.printStackTrace();
            }
        try {
            LocationRequest mLocationRequest = new LocationRequest();
            mLocationRequest.setInterval(1000);
            mLocationRequest.setFastestInterval(1000);
            mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
            LocationServices.FusedLocationApi.requestLocationUpdates(
                    mGoogleApiClient, mLocationRequest, this);

        } catch (Exception e) {
            e.printStackTrace();
        }

        /*
         *  Accept rider reqeust
         */
        AcceptedRequest();

    }

    @Override
    public void onConnectionSuspended(int i) {
        CommonMethods.DebuggableLogI(TAG, "Connection suspended");
        mGoogleApiClient.connect();
    }

    /*
     *  update driver location changed
     */
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

                // Toast.makeText(getApplicationContext(), "Current speed:" + speed,Toast.LENGTH_SHORT).show();
                CommonMethods.DebuggableLogE("Live tracking ","On Location change");
                changeMap(location);
                sessionManager.setCurrentLatitude(Double.toString(location.getLatitude()));
                sessionManager.setCurrentLongitude(Double.toString(location.getLongitude()));

                // LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }

    /*
     *  Google API client called
     */
    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
    }

    @Override
    protected void onStart() {
        super.onStart();
        try {
            mGoogleApiClient.connect();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();

        if (mGoogleApiClient != null && mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
        }
    }

    /*
     *  Check play service
     */
    private boolean checkPlayServices() {
//        code updated due to deprication, code updated by the reference of @link: https://stackoverflow.com/a/31016761/6899791
//        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this); this is commented due to depricated
        GoogleApiAvailability googleAPI = GoogleApiAvailability.getInstance();
        int resultCode = googleAPI.isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (googleAPI.isUserResolvableError(resultCode)) {
                googleAPI.getErrorDialog(this, resultCode, PLAY_SERVICES_RESOLUTION_REQUEST).show();
            }
            return false;
        }
        return true;
    }

    /*
     *  Show current location in map
     */
    private void changeMap(Location location) {

        CommonMethods.DebuggableLogD(TAG, "Reaching map" + mMap);


        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            return;
        }

        // check if map is created successfully or not
        if (mMap != null) {
            mMap.getUiSettings().setZoomControlsEnabled(false);
            mMap.setMyLocationEnabled(false);
            mMap.getUiSettings().setMyLocationButtonEnabled(false);
            CommonMethods.DebuggableLogE("Live tracking", "langua=" + location.getLatitude());
            latLong = new LatLng(location.getLatitude(), location.getLongitude());

            liveTracking(latLong);


            if (newLatLng == null) {
                CameraPosition cameraPosition = new CameraPosition.Builder()
                        .target(latLong).zoom(15f).tilt(0).build();
                mMap.moveCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
            }

            startIntentService(location);

        } else {
            Toast.makeText(getApplicationContext(),
                    "Sorry! unable to create maps", Toast.LENGTH_SHORT)
                    .show();
        }

        //AcceptedRequest();

    }

    public void navigateViaGoogleMap() {
        try {
            LatLng pickuplatlng = new LatLng(Double.valueOf(riderDetailsModel.getPickupLatitude()), Double.valueOf(riderDetailsModel.getPickupLongitude()));
            LatLng droplatlng = new LatLng(Double.valueOf(riderDetailsModel.getDropLatitude()), Double.valueOf(riderDetailsModel.getDropLongitude()));

            String latlngs = "";
            if (tripastatusbutton.getText().toString().equals(getResources().getString(R.string.confirm_arrived))
                    || tripastatusbutton.getText().toString().equals(getResources().getString(R.string.begin_trip))) {
                latlngs = String.valueOf(pickuplatlng.latitude) + "," + String.valueOf(pickuplatlng.longitude);
            } else {
                latlngs = String.valueOf(droplatlng.latitude) + "," + String.valueOf(droplatlng.longitude);
            }


            String currentlatlngs = sessionManager.getCurrentLatitude() + "," + sessionManager.getCurrentLongitude();


            System.out.println("Drop lat lng: " + String.valueOf(pickuplatlng.latitude) + " : " + String.valueOf(pickuplatlng.longitude) + " : " + String.valueOf(droplatlng.latitude) + " : " + String.valueOf(droplatlng.longitude));

            // Create a Uri from an intent string. Use the result to create an Intent.
            //Uri gmmIntentUri = Uri.parse("google.streetview:cbll="+latlngs);
            Uri gmmIntentUri = Uri.parse("http://maps.google.com/maps?" + "saddr=" + currentlatlngs + "&daddr=" + latlngs);

            //final Intent intent = new Intent(Intent.ACTION_VIEW,Uri.parse("http://maps.google.com/maps?" + "saddr="+ latitude + "," + longitude + "&daddr=" + latitude + "," + longitude));

            // Create an Intent from gmmIntentUri. Set the action to ACTION_VIEW
            Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
            // Make the Intent explicit by setting the Google Maps package
            //mapIntent.setPackage("com.google.android.apps.maps");
            mapIntent.setClassName("com.google.android.apps.maps", "com.google.android.maps.MapsActivity");
            System.out.println("googlemap URI  " + gmmIntentUri);
            startActivity(mapIntent);
            // Attempt to start an activity that can handle the Intent

        } catch (ActivityNotFoundException e) {
            try {
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=com.google.android.apps.maps"));
                startActivity(intent);
            } catch (Exception excep) {
                CommonMethods.showUserMessage(getResources().getString(R.string.google_map_not_found_in_device));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void navigateViaWazeMap() {
        try {

            LatLng pickuplatlng = new LatLng(Double.valueOf(riderDetailsModel.getPickupLatitude()), Double.valueOf(riderDetailsModel.getPickupLongitude()));
            LatLng droplatlng = new LatLng(Double.valueOf(riderDetailsModel.getDropLatitude()), Double.valueOf(riderDetailsModel.getDropLongitude()));

            String latlngs = "";
            if (tripastatusbutton.getText().toString().equals(getResources().getString(R.string.confirm_arrived))
                    || tripastatusbutton.getText().toString().equals(getResources().getString(R.string.begin_trip))) {
                latlngs = String.valueOf(pickuplatlng.latitude) + "," + String.valueOf(pickuplatlng.longitude);
            } else {
                latlngs = String.valueOf(droplatlng.latitude) + "," + String.valueOf(droplatlng.longitude);
            }

            String currentlatlngs = sessionManager.getCurrentLatitude() + "," + sessionManager.getCurrentLongitude();

            String mapRequest = "https://waze.com/ul?ll=" + latlngs + "&from" + currentlatlngs + "&at=now&navigate=yes&zoom=17";

            Uri gmmIntentUri = Uri.parse(mapRequest);
            Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
            mapIntent.setPackage("com.waze");
            startActivity(mapIntent);

        } catch (ActivityNotFoundException activityNotfoundException) {
            try {
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=com.waze"));
                startActivity(intent);
            } catch (Exception e) {
                CommonMethods.showUserMessage(getResources().getString(R.string.waze_google_map_not_found_in_device));

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * Creates an intent, adds location data to it as an extra, and starts the intent service for
     * fetching an address.
     */
    protected void startIntentService(Location mLocation) {
        // Create an intent for passing to the intent service responsible for fetching the address.
        Intent intent = new Intent(this, FetchAddressIntentService.class);

        // Pass the result receiver as an extra to the service.
        intent.putExtra(AppUtils.LocationConstants.RECEIVER, mResultReceiver);

        // Pass the location data as an extra to the service.
        intent.putExtra(AppUtils.LocationConstants.LOCATION_DATA_EXTRA, mLocation);

        // Start the service. If the service isn't already running, it is instantiated and started
        // (creating a process for it if needed); if it is running then it remains running. The
        // service kills itself automatically once all intents are processed.
        startService(intent);
    }

    @Override
    public void onSuccess(JsonResponse jsonResp, String data) {
        //commonMethods.hideProgressDialog();
        tripastatusbutton.showResultIcon(false, true);
        user_details_lay.setEnabled(true);
        if (!jsonResp.isOnline()) {
            if (!TextUtils.isEmpty(data))
                commonMethods.showMessage(this, dialog, data);
            return;
        }


        switch (jsonResp.getRequestCode()) {

            case REQ_ARRIVE_NOW:
                if (jsonResp.isSuccess()) {
                    commonMethods.hideProgressDialog();
                    onSuccessArrive();
                } else if (!TextUtils.isEmpty(jsonResp.getStatusMsg())) {
                    commonMethods.hideProgressDialog();
                    commonMethods.showMessage(this, dialog, jsonResp.getStatusMsg());
                }
                break;
            case REQ_BEGIN_TRIP:
                if (jsonResp.isSuccess()) {
                    commonMethods.hideProgressDialog();
                    onSuccessBegin();
                } else if (!TextUtils.isEmpty(jsonResp.getStatusMsg())) {
                    commonMethods.hideProgressDialog();
                    commonMethods.showMessage(this, dialog, jsonResp.getStatusMsg());
                }
                break;
            case REQ_END_TRIP:
                if (jsonResp.isSuccess()) {
                    commonMethods.hideProgressDialog();
                    onSuccessEnd();
                } else if (!TextUtils.isEmpty(jsonResp.getStatusMsg())) {
                    commonMethods.hideProgressDialog();


                    commonMethods.showMessage(this, dialog, jsonResp.getStatusMsg());
                }
                break;

            default:

                if (!TextUtils.isEmpty(jsonResp.getStatusMsg())) {
                    commonMethods.hideProgressDialog();
                    commonMethods.showMessage(this, dialog, jsonResp.getStatusMsg());
                }
                break;
        }
    }

    @Override
    public void onFailure(JsonResponse jsonResp, String data) {
        commonMethods.hideProgressDialog();
    }

    /*
     *  Check GPS enable or not
     */
    public void checkGPSEnable() {
        if (checkPlayServices()) {
            // If this check succeeds, proceed with normal processing.
            // Otherwise, prompt user to get valid Play Services APK.
            if (!AppUtils.isLocationEnabled(mContext)) {
                // notify user
                AlertDialog.Builder dialog = new AlertDialog.Builder(mContext);
                dialog.setMessage("LOCATION_AND_WRITEPERMISSION_ARRAY not enabled!");
                dialog.setPositiveButton("Open location settings", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                        Intent myIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        startActivity(myIntent);
                    }
                });
                dialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                        // TODO Auto-generated method stub

                    }
                });
                dialog.show();
            }
            buildGoogleApiClient();
        } else {
            Toast.makeText(mContext, "LOCATION_AND_WRITEPERMISSION_ARRAY not supported in this device", Toast.LENGTH_SHORT).show();
        }
    }

    /*
     *  Accept rider reqeust
     */
    public void AcceptedRequest() {
        String polylineurl;
        mMap.clear();

        LatLng pickuplatlng = new LatLng(Double.valueOf(riderDetailsModel.getPickupLatitude()), Double.valueOf(riderDetailsModel.getPickupLongitude()));
        LatLng droplatlng = new LatLng(Double.valueOf(riderDetailsModel.getDropLatitude()), Double.valueOf(riderDetailsModel.getDropLongitude()));

        if (tripastatusbutton.getText().toString().equals(getResources().getString(R.string.confirm_arrived))
                || tripastatusbutton.getText().toString().equals(getResources().getString(R.string.begin_trip))) {
            // Creating MarkerOptions
            MarkerOptions pickupOptions = new MarkerOptions();

            // Setting the position of the marker

            pickupOptions.position(latLong);
            pickupOptions.anchor(0.5f, 0.5f);
            pickupOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.carmap));
            // Add new marker to the Google Map Android API V2
            carmarker = mMap.addMarker(pickupOptions);

            // Creating MarkerOptions
            MarkerOptions dropOptions = new MarkerOptions();

            // Setting the position of the marker
            dropOptions.position(pickuplatlng);
            dropOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.ub__ic_pin_pickup));
            // Add new marker to the Google Map Android API V2
            mMap.addMarker(dropOptions);

            LatLngBounds.Builder builder = new LatLngBounds.Builder();

//the include method will calculate the min and max bound.
            builder.include(latLong);
            builder.include(pickuplatlng);


            newLatLng = latLong;
            //Toast.makeText(getApplicationContext(),"Latlng"+latLong+" \nBearing"+targetBearing,Toast.LENGTH_SHORT).show();


            CameraPosition cameraPosition = new CameraPosition.Builder()
                    .target(latLong)            // Sets the center of the map to current location
                    .zoom(16.5f)                   // Sets the zoom
                    //.bearing(targetBearing)     // Sets the orientation of the camera to east
                    .tilt(0)                   // Sets the tilt of the camera to 0 degrees
                    .build();                   // Creates a CameraPosition from the builder
            mMap.moveCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));

            polylineurl = getDirectionsUrl(latLong, pickuplatlng);
        } else {
            // Creating MarkerOptions
            MarkerOptions pickupOptions = new MarkerOptions();

            // Setting the position of the marker
            pickupOptions.position(latLong);
            pickupOptions.anchor(0.5f, 0.5f);
            pickupOptions.draggable(true);
            pickupOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.carmap));
            // Add new marker to the Google Map Android API V2
            carmarker = mMap.addMarker(pickupOptions);

            // Creating MarkerOptions
            MarkerOptions dropOptions = new MarkerOptions();

            // Setting the position of the marker
            dropOptions.position(droplatlng);
            dropOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.ub__ic_pin_dropoff));
            // Add new marker to the Google Map Android API V2
            mMap.addMarker(dropOptions);

            LatLngBounds.Builder builder = new LatLngBounds.Builder();

//the include method will calculate the min and max bound.
            builder.include(latLong);
            builder.include(droplatlng);


            newLatLng = latLong;


            CameraPosition cameraPosition = new CameraPosition.Builder()
                    .target(latLong)            // Sets the center of the map to current location
                    .zoom(16.5f)                   // Sets the zoom
                    //.bearing(targetBearing)     // Sets the orientation of the camera to east
                    .tilt(0)                   // Sets the tilt of the camera to 0 degrees
                    .build();                   // Creates a CameraPosition from the builder
            mMap.moveCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
            polylineurl = getDirectionsUrl(latLong, droplatlng);
        }


        // Getting URL to the Google Directions API


        DownloadTask downloadTask = new DownloadTask(new PolylineOptionsInterface() {
            @Override
            public void getPolylineOptions(PolylineOptions output, ArrayList points) {

                if (polyline != null)
                    polyline.remove();

                if (mMap != null && output != null) {
                    polyline = mMap.addPolyline(output);
                    //MapAnimator.getInstance().animateRoute(mMap,output.getPoints());
                }
            }
        }, this);

        // Start downloading json data from Google Directions API
        downloadTask.execute(polylineurl);

    }


    /*
     *  Send Arrive now , Begin trip, end trip to rider
     */
    public void arriveRequest() {
        //commonMethods.showProgressDialog(this, customDialog);
        user_details_lay.setEnabled(false);
        apiService.ariveNow(sessionManager.getTripId(), sessionManager.getAccessToken()).enqueue(new RequestCallback(REQ_ARRIVE_NOW, this));
    }

    public void beginTrip() {
        //commonMethods.showProgressDialog(this, customDialog);
        user_details_lay.setEnabled(false);
        apiService.beginTrip(sessionManager.getTripId(), sessionManager.getLatitude(), sessionManager.getLongitude(), sessionManager.getAccessToken()).enqueue(new RequestCallback(REQ_BEGIN_TRIP, this));
    }


    /**
     * Parameters for an api call for end trip
     */

    public void endTrip() {

        Double lat = Double.valueOf(sessionManager.getLastLatitude());
        Double lng = Double.valueOf(sessionManager.getLastLongitude());
        Location lastLocations = new Location("lastloc");
        lastLocations.setLatitude(lat);
        lastLocations.setLongitude(lng);


        Double lats = Double.valueOf(sessionManager.getCurrentLatitude());
        Double lngs= Double.valueOf(sessionManager.getCurrentLongitude());
        Location currentLocation = new Location("curloc");
        currentLocation.setLatitude(lats);
        currentLocation.setLongitude(lngs);

        float distanceF=0;
        if(lastLocations!=null&&currentLocation!=null) {

            float distanceInMeters = lastLocations.distanceTo(currentLocation);

            distanceF = (float) (distanceInMeters / 1000.0);


            try {
                distanceF = Float.valueOf(twoDForm.format(distanceF).replaceAll(",", "."));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        float offlineDistance = sessionManager.getTotalDistance();

        distanceF = distanceF + offlineDistance;
        CommonMethods.DebuggableLogE("Offline distance", ":" +offlineDistance);
        CommonMethods.DebuggableLogE("distanceF", ":" +distanceF);
        sessionManager.setTotalDistance(0);

        HashMap<String, String> imageObject = new HashMap<String, String>();

        imageObject.put("trip_id", sessionManager.getTripId());
        imageObject.put("end_latitude", sessionManager.getLatitude());
        imageObject.put("end_longitude", sessionManager.getLongitude());
        imageObject.put("total_km", String.valueOf(distanceF));
        imageObject.put("token", sessionManager.getAccessToken());
        imageObject.put("image", compressPath);
        CommonMethods.DebuggableLogE("end trip Latitude", sessionManager.getLatitude());
        CommonMethods.DebuggableLogE("end trip Longitude", sessionManager.getLongitude());
        updateProfile(imageObject);

    }


    /**
     * Api calling method based on country type
     *
     * @param imageObject hash Map Datas Based on Country Type
     */
    private void updateProfile(HashMap<String, String> imageObject) {
        MultipartBody.Builder multipartBody = new MultipartBody.Builder();
        multipartBody.setType(MultipartBody.FORM);
        File file = null;
        try {
            file = new File(compressPath);
            String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());

            if (!compressPath.equals("")) {
                multipartBody.addFormDataPart("image", "IMG_" + timeStamp + ".jpg", RequestBody.create(MediaType.parse("image/png"), file));
            }


            for (String key : imageObject.keySet()) {
                multipartBody.addFormDataPart(key, imageObject.get(key).toString());
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        //commonMethods.showProgressDialog(this, customDialog);
        RequestBody formBody = multipartBody.build();
        apiService.endTrip(formBody).enqueue(new RequestCallback(REQ_END_TRIP, this));

    }


    public void onSuccessArrive() {
        tripastatusbutton.setText(getResources().getString(R.string.begin_trip));
        //sessionManager.setTripStatus("CONFIRM YOU'VE ARRIVED");
        sessionManager.setTripStatus(CommonKeys.TripStaus.ConfirmArrived);
        sessionManager.setSubTripStatus(getResources().getString(R.string.begin_trip));

    }

    public void onSuccessBegin() {
        //sessionManager.setTripStatus("Begin Trip");
        sessionManager.setTripStatus(CommonKeys.TripStaus.BeginTrip);
        pickup_address.setText(riderDetailsModel.getDropLocation());
        tripastatusbutton.setText(getResources().getString(R.string.end_trip));
        sessionManager.setSubTripStatus(getResources().getString(R.string.end_trip));

        /*if (!isMyServiceRunning(GpsService.class)) {

            System.out.println("Begin trip service start : ");
            Intent GPSservice = new Intent(getApplicationContext(), GpsService.class);
            startService(GPSservice);
        }*/
        if(!WorkerUtils.isWorkRunning(CommonKeys.WorkTagForUpdateGPS)) {
            CommonMethods.DebuggableLogE("locationupdate", "StartWork:");
            WorkerUtils.startWorkManager(CommonKeys.WorkKeyForUpdateGPS, CommonKeys.WorkTagForUpdateGPS, UpdateGPSWorker.class);
        }
        AcceptedRequest();
    }



    public void onSuccessEnd() {
        //drawStaticMap();


        //sessionManager.setTripStatus("End Trip");
        sessionManager.setTripStatus(CommonKeys.TripStaus.EndTrip);

        /*Intent rating = new Intent(getApplicationContext(), Riderrating.class);
        rating.putExtra("imgprofile", riderDetailsModel.getRiderThumbImage());
        startActivity(rating);*/
        startActivity(new Intent(getApplicationContext(),PaymentAmountPage.class));

    }

    /*
     *  Check service is running or not
     */
    private boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }


    /*
     *  Show dialog for payment completed trip cancelled
     */
    public void statusDialog(String message) {
        System.out.println("Print Message "+message);
        new AlertDialog.Builder(RequestAcceptActivity.this)
                .setMessage(message)
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                        dialog.dismiss();
                        Intent requestaccept = new Intent(getApplicationContext(), MainActivity.class);
                        startActivity(requestaccept);
                        finish();

                    }
                })
                .show();
    }

    /*
     *  Receive push notification
     */
    public void receivepushnotification() {

        mRegistrationBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {

                // checking for type intent filter
                if (intent.getAction().equals(Config.REGISTRATION_COMPLETE)) {
                    // FCM successfully registered
                    // now subscribe to `global` topic to receive app wide notifications
                    FirebaseMessaging.getInstance().subscribeToTopic(Config.TOPIC_GLOBAL);


                } else if (intent.getAction().equals(Config.PUSH_NOTIFICATION)) {
                    // new push notification is received


                    String JSON_DATA = sessionManager.getPushJson();


                    JSONObject jsonObject = null;
                    try {
                        jsonObject = new JSONObject(JSON_DATA);
                        if (jsonObject.getJSONObject("custom").has("cancel_trip")) {

                            statusDialog(getResources().getString(R.string.yourtripcanceledrider));

                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }
            }
        };
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (broadcastReceiver != null) {
            unregisterReceiver(broadcastReceiver);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        /*
         *  Get Latitude and Longitude from GPS service
         */

        if (tripastatusbutton.getText().toString().equals(getResources().getString(R.string.confirm_arrived))
                || tripastatusbutton.getText().toString().equals(getResources().getString(R.string.begin_trip))) {

        } else {
            /*if (!isMyServiceRunning(GpsService.class)) {
                Intent GPSservice = new Intent(getApplicationContext(), GpsService.class);
                startService(GPSservice);
            }*/
            if(!WorkerUtils.isWorkRunning(CommonKeys.WorkTagForUpdateGPS)) {
                CommonMethods.DebuggableLogE("locationupdate", "StartWork:");
                WorkerUtils.startWorkManager(CommonKeys.WorkKeyForUpdateGPS, CommonKeys.WorkTagForUpdateGPS, UpdateGPSWorker.class);
            }
        }


        if (broadcastReceiver == null) {
            broadcastReceiver = new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
                    Location location = new Location(LocationManager.GPS_PROVIDER);
                    if (intent.getStringExtra("type").equals("change")) {
                        count0++;
                        location.setLatitude(intent.getDoubleExtra("Lat", 0d));
                        location.setLongitude(intent.getDoubleExtra("Lng", 0d));
                    } else if (intent.getStringExtra("type").equals("Updates")) {
                        location.setLatitude(intent.getDoubleExtra("Lat", 0d));
                        location.setLongitude(intent.getDoubleExtra("Lng", 0d));
                    } else if (intent.getStringExtra("type").equals("DataBase")) {
                        count1++;
                        location.setLatitude(intent.getDoubleExtra("Lat", 0));
                        location.setLongitude(intent.getDoubleExtra("Lng", 0));
                        String text = String.valueOf(intent.getDoubleExtra("Lat", 0) + " -- " + intent.getDoubleExtra("Lng", 0));
                        textView1.append("\n" + count1 + " * " + text);
                    } else {
                        //count1++;
                        location.setLatitude(intent.getDoubleExtra("Lat", 0));
                        location.setLongitude(intent.getDoubleExtra("Lng", 0));

                    }
                    CommonMethods.DebuggableLogI(TAG, "MyService running...");
                    CommonMethods.DebuggableLogI(TAG, "\n" + location);
                }
            };
        }
        registerReceiver(broadcastReceiver, new IntentFilter("location_update"));


        isRequestAcceptActivity = true;
        // register FCM registration complete receiver
        LocalBroadcastManager.getInstance(this).registerReceiver(mRegistrationBroadcastReceiver,
                new IntentFilter(Config.REGISTRATION_COMPLETE));

        // register new push message receiver
        // by doing this, the activity will be notified each time a new message arrives
        LocalBroadcastManager.getInstance(this).registerReceiver(mRegistrationBroadcastReceiver,
                new IntentFilter(Config.PUSH_NOTIFICATION));

        // clear the notification area when the app is opened
        NotificationUtils.clearNotifications(getApplicationContext());
    }

    @Override
    public void onPause() {
        super.onPause();
        isRequestAcceptActivity = false;
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mRegistrationBroadcastReceiver);
    }


    /* ***************************************************************** */
    /*                  Animate Marker for Live Tracking                 */
    /* ***************************************************************** */

    /*
     *  After driver track location or route
     */

    /*
     *   After driver accept the trip update the pickup and drop route in map
     */
    public void UpdateRoute(LatLng driverlatlng) {

        //mMap.clear();
        LatLng pickuplatlng = new LatLng(Double.valueOf(riderDetailsModel.getPickupLatitude()), Double.valueOf(riderDetailsModel.getPickupLongitude()));
        LatLng droplatlng = new LatLng(Double.valueOf(riderDetailsModel.getDropLatitude()), Double.valueOf(riderDetailsModel.getDropLongitude()));

        // Creating MarkerOptions
        MarkerOptions pickupOptions = new MarkerOptions();

        // Setting the position of the marker

        if (tripastatusbutton.getText().toString().equals(getResources().getString(R.string.confirm_arrived))
                || tripastatusbutton.getText().toString().equals(getResources().getString(R.string.begin_trip))) {
            pickupOptions.position(pickuplatlng);
            pickupOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.ub__ic_pin_pickup));
        } else {
            pickupOptions.position(droplatlng);
            pickupOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.ub__ic_pin_dropoff));
        }
        // Add new marker to the Google Map Android API V2
        //mMap.addMarker(pickupOptions);

        // Creating MarkerOptions
        MarkerOptions dropOptions = new MarkerOptions();


        // Setting the position of the marker
        dropOptions.position(driverlatlng);
        dropOptions.anchor(0.5f, 0.5f);
        dropOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.carmap));
        // Add new marker to the Google Map Android API V2
        //carmarker = mMap.addMarker(dropOptions);


        LatLngBounds.Builder builder = new LatLngBounds.Builder();

//the include method will calculate the min and max bound.
        builder.include(driverlatlng);
        if (tripastatusbutton.getText().toString().equals(getResources().getString(R.string.confirm_arrived))
                || tripastatusbutton.getText().toString().equals(getResources().getString(R.string.begin_trip))) {
            builder.include(pickuplatlng);
        } else {
            builder.include(droplatlng);
        }

        LatLngBounds bounds = builder.build();

        int width = getResources().getDisplayMetrics().widthPixels / 2;
        int height = getResources().getDisplayMetrics().heightPixels / 2;
        int padding = (int) (width * 0.08); // offset from edges of the map 10% of screen

        if (firstloop) {
            CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, width, height, padding);
            mMap.moveCamera(cu);
            firstloop = false;
        }

        String url;
        if (tripastatusbutton.getText().toString().equals(getResources().getString(R.string.confirm_arrived))
                || tripastatusbutton.getText().toString().equals(getResources().getString(R.string.begin_trip))) {
            // Getting URL to the Google Directions API

            url = getDirectionsUrl(driverlatlng, pickuplatlng);
        } else {
            url = getDirectionsUrl(driverlatlng, droplatlng);
        }
        DownloadTask downloadTask = new DownloadTask(new PolylineOptionsInterface() {
            @Override
            public void getPolylineOptions(PolylineOptions output, ArrayList points) {

                if (mMap != null && output != null) {

                    if (polyline != null)
                        polyline.remove();
                    // Toast.makeText(getApplicationContext(), "Map route Updated", Toast.LENGTH_SHORT).show();
                    polyline = mMap.addPolyline(output);


                }
            }
        }, this);

        // Start downloading json data from Google Directions API
        downloadTask.execute(url);
    }

    public void liveTracking(LatLng latLng) {

        if (movepoints.size() < 1) {
            movepoints.add(0, latLng);
            movepoints.add(1, latLng);

        } else {
            movepoints.set(1, movepoints.get(0));
            movepoints.set(0, latLng);
        }

        CommonMethods.DebuggableLogE("Live tracking ","First movepoints1 "+movepoints.get(0));
        CommonMethods.DebuggableLogE("Live tracking ","First movepoints0 "+movepoints.get(1));

        DecimalFormat twoDForm = new DecimalFormat("#.#######");
        DecimalFormatSymbols dfs = new DecimalFormatSymbols();
        dfs.setDecimalSeparator('.');
        twoDForm.setDecimalFormatSymbols(dfs);

        String zerolat = twoDForm.format(((LatLng) movepoints.get(0)).latitude);
        String zerolng = twoDForm.format(((LatLng) movepoints.get(0)).longitude);

        String onelat = twoDForm.format(((LatLng) movepoints.get(1)).latitude);
        String onelng = twoDForm.format(((LatLng) movepoints.get(1)).longitude);

        if (!zerolat.equals(onelat) || !zerolng.equals(onelng)) {
            CommonMethods.DebuggableLogE("Live tracking ","zerolat"+zerolat+" "+zerolng);
            UpdateRoute((LatLng) movepoints.get(1));
            CommonMethods.DebuggableLogE("Live tracking ","movepoints1 "+movepoints.get(0));
            CommonMethods.DebuggableLogE("Live tracking ","movepoints0 "+movepoints.get(1));
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
        marker = carmarker;
        // Move map while marker gone
        ensureMarkerOnBounds(latlng, "updated");

        endbear = (float) bearing(startbearlocation, endbearlocation);
        endbear = (float) (endbear * (180.0 / 3.14));

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
            Location newLoc = new Location(LocationManager.GPS_PROVIDER);
            newLoc.setLatitude(startPosition.latitude);
            newLoc.setLongitude(startPosition.longitude);
            Location prevLoc = new Location(LocationManager.GPS_PROVIDER);
            prevLoc.setLatitude(endPosition.latitude);
            prevLoc.setLongitude(endPosition.longitude);


            //double distance = Double.valueOf(twoDForm.format(newLoc.distanceTo(prevLoc)));
            double distance = Double.valueOf((newLoc.distanceTo(prevLoc)));

            duration = (long) ((distance / speed) * 1000) - 5;

            if (duration >= 1000)
                duration = 950;
            duration = 1015;


            final float startRotation = marker.getRotation();

            final LatLngInterpolator latLngInterpolator = new LatLngInterpolator.LinearFixed();
            if (valueAnimator != null) {
                valueAnimator.cancel();
                valueAnimator.end();
            }
            valueAnimator = ValueAnimator.ofFloat(0, 1);
            valueAnimator.setDuration(duration);
            valueAnimator.setInterpolator(new LinearInterpolator());
            valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    try {
                        float v = animation.getAnimatedFraction();
                        newPosition[0] = latLngInterpolator.interpolate(v, startPosition, endPosition);
                        marker.setPosition(newPosition[0]); // Move Marker
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

    /**
     * To generate static map of that particular trip
     */

    public void drawStaticMap() {

        //GpsService gps_service = new GpsService();

        List<LatLng> newlatLngList =routePoints; //gps_service.latLngList;

        int j = 1;
        String trip_path = "";
        if (newlatLngList.size() > 100) {
            j = newlatLngList.size() / 100;
        }

        for (int i = 0; i < newlatLngList.size(); i = i + j) {
            trip_path = trip_path + "|" + newlatLngList.get(i).latitude + "," + newlatLngList.get(i).longitude;
        }

        LatLng pickuplatlng;
        LatLng droplatlng;
        pickuplatlng = new LatLng(newlatLngList.get(0).latitude, newlatLngList.get(0).longitude);
        droplatlng = new LatLng(newlatLngList.get(newlatLngList.size() - 1).latitude, newlatLngList.get(newlatLngList.size() - 1).longitude);


        String pathString = "&path=color:0x000000ff|weight:4" + trip_path;
        String pickupstr = pickuplatlng.latitude + "," + pickuplatlng.longitude;
        String dropstr = droplatlng.latitude + "," + droplatlng.longitude;
        String positionOnMap = "&markers=size:mid|icon:" + CommonKeys.imageUrl + "pickup.png|" + pickupstr;
        String positionOnMap1 = "&markers=size:mid|icon:" + CommonKeys.imageUrl + "drop.png|" + dropstr;


        String staticMapURL = "https://maps.googleapis.com/maps/api/staticmap?size=640x250&" +
                pickuplatlng.latitude + "," + pickuplatlng.longitude +
                pathString + "" + positionOnMap + "" + positionOnMap1 + //"&zoom=14" +
                "&key=" + sessionManager.getGoogleMapKey() + "&language=" +
                Locale.getDefault();


        Bitmap bm = getBitmapFromURL(staticMapURL);
       /* imagepath = getStringImage(bm);
        //imagepath = imageWrite(bm);*/

        File file = savebitmap("StaticMap", bm);
        file = new Compressor.Builder(compressImgWeakRef.get()).setMaxWidth(1080).setMaxHeight(1920).setQuality(75).setCompressFormat(Bitmap.CompressFormat.JPEG).build().compressToFile(file);
        compressPath = file.getPath();

        if (isInternetAvailable) {
            endTrip();

            //new ProgressTask(RequestAcceptActivity.this).execute();

        } else {
            commonMethods.showMessage(mContext, dialog, getResources().getString(R.string.no_connection));
        }
    }


    /**
     * Function to convert bitmap to base64 format
     *
     * @param bmp bitmap of an image
     * @return base64 format of an bitmap
     */


    public String getStringImage(Bitmap bmp) {
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bmp.compress(Bitmap.CompressFormat.JPEG, 100, baos);
            byte[] imageBytes = baos.toByteArray();
            encodedImage = Base64.encodeToString(imageBytes, Base64.DEFAULT);
            return encodedImage;
        } catch (Exception e) {

        }

        return encodedImage;
    }


    /**
     * to save bitmap to file android
     *
     * @param filename name of the file
     * @return returns file that contains bitmap
     */


    private File savebitmap(String filename, Bitmap bm) {
        String extStorageDirectory = Environment.getExternalStorageDirectory().toString();
        OutputStream outStream = null;

        File file = new File(extStorageDirectory, filename + ".png");

        if (file.exists()) {
            file.delete();

            file = new File(extStorageDirectory, filename + ".png");
            CommonMethods.DebuggableLogE("file exist", "" + file + ",Bitmap= " + filename);
        }
        try {
            // make a new bitmap from your file BitmapFactory.decodeFile(file.getName());
            Bitmap bitmap = bm;

            outStream = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, outStream);
            outStream.flush();
            outStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        CommonMethods.DebuggableLogE("file", "" + file);
        return file;

    }


    /*
     *  image to image path
     */
    public String imageWrite(Bitmap bitmap) {

        String extStorageDirectory = Environment.getExternalStorageDirectory().toString();
        OutputStream outStream = null;
        File file = new File(extStorageDirectory, "slectimage.png");

        try {

            outStream = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, outStream);
            outStream.flush();
            outStream.close();


        } catch (FileNotFoundException e) {
            e.printStackTrace();

        } catch (IOException e) {
            e.printStackTrace();

        }
        imageInSD = "/sdcard/slectimage.png";

        return imageInSD;

    }

    /*
     *   Image upload function called
     */
    protected void imageuploading() {
        // TODO Auto-generated method stub

        try {

            CommonMethods.DebuggableLogE("Image Upload", "Gofer");

            HttpURLConnection connection = null;
            DataOutputStream outputStream = null;

            String pathToOurFile = imagepath;


            String baseurl = CommonKeys.apiBaseUrl;
            String urlServer = baseurl + "map_upload";


            String lineEnd = "\r\n";
            String twoHyphens = "--";
            String boundary = "*****";

            int bytesRead;
            int bytesAvailable;
            int bufferSize;
            byte[] buffer;
            int maxBufferSize = 1 * 1024 * 1024;

            FileInputStream fileInputStream = new FileInputStream(new File(pathToOurFile));

            URL url = new URL(urlServer);
            connection = (HttpURLConnection) url.openConnection();


            // Allow Inputs & Outputs
            connection.setDoInput(true);
            connection.setDoOutput(true);
            connection.setUseCaches(false);

            // Enable POST method
            connection.setRequestMethod("POST");

            connection.setRequestProperty("Connection", "Keep-Alive");
            connection.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + boundary);

            outputStream = new DataOutputStream(connection.getOutputStream());
            outputStream.writeBytes(twoHyphens + boundary + lineEnd);
            outputStream.writeBytes("Content-Disposition: form-data; name=\"image\";filename=\"" + pathToOurFile + "\"" + lineEnd);

            outputStream.writeBytes(lineEnd);

            bytesAvailable = fileInputStream.available();
            bufferSize = Math.min(bytesAvailable, maxBufferSize);
            buffer = new byte[bufferSize];

            // Read file
            bytesRead = fileInputStream.read(buffer, 0, bufferSize);

            while (bytesRead > 0) {
                outputStream.write(buffer, 0, bufferSize);
                bytesAvailable = fileInputStream.available();
                bufferSize = Math.min(bytesAvailable, maxBufferSize);
                bytesRead = fileInputStream.read(buffer, 0, bufferSize);
            }

            outputStream.writeBytes(lineEnd);

            outputStream.writeBytes(twoHyphens + boundary + lineEnd);
            outputStream.writeBytes("Content-Disposition: form-data; name=\"token\"" + lineEnd);

            outputStream.writeBytes(lineEnd);
            outputStream.writeBytes(sessionManager.getAccessToken() + lineEnd);

            outputStream.writeBytes(lineEnd);

            outputStream.writeBytes(twoHyphens + boundary + lineEnd);
            outputStream.writeBytes("Content-Disposition: form-data; name=\"trip_id\"" + lineEnd);

            outputStream.writeBytes(lineEnd);
            outputStream.writeBytes(sessionManager.getTripId() + lineEnd);

            outputStream.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);

            if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                String serverResponseMessage = connection.getResponseMessage();


                fileInputStream.close();
                outputStream.flush();
                outputStream.close();

                DataInputStream inputStream1 = null;
                inputStream1 = new DataInputStream(connection.getInputStream());
                String str = "";
                String Str1_imageurl = "";

                while ((str = inputStream1.readLine()) != null) {
                    CommonMethods.DebuggableLogE("Debug", "Server Response " + str);

                    Str1_imageurl = str;
                    CommonMethods.DebuggableLogE("Debug", "Server Response String imageurl" + str);
                }
                inputStream1.close();


                try {
                    JSONObject user_jsonobj = new JSONObject(Str1_imageurl);
                    for (int i = 0; i < user_jsonobj.length(); i++) {

                        String statuscode = user_jsonobj.getString("status_code");
                        String statusmessage = user_jsonobj.getString("status_message");
                        if (statuscode.matches("1")) {
                            //String user_thumb_image = user_jsonobj.getString("document_url");
                            CommonMethods.DebuggableLogD("OUTPUT 0S", user_jsonobj.toString());

                            //sessionManager.setTripStatus("End Trip");
                            sessionManager.setTripStatus(CommonKeys.TripStaus.EndTrip);

                            /*Intent rating = new Intent(getApplicationContext(), Riderrating.class);
                            rating.putExtra("imgprofile", riderDetailsModel.getRiderThumbImage());
                            startActivity(rating);*/
                            startActivity(new Intent(getApplicationContext(),PaymentAmountPage.class));
                        } else {
                            commonMethods.showMessage(mContext, dialog, statusmessage);
                        }

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else {
                commonMethods.showMessage(mContext, dialog, getResources().getString(R.string.img_failed_msg));
            }


        } catch (Exception e) {

            e.printStackTrace();

        }

    }

    @Override
    public void permissionGranted(int requestCodeForCallbackIdentificationCode, int requestCodeForCallbackIdentificationCodeSubDivision) {
        drawStaticMap();
    }

    @Override
    public void permissionDenied(int requestCodeForCallbackIdentificationCode, int requestCodeForCallbackIdentificationCodeSubDivision) {
        tripastatusbutton.showResultIcon(false, true);
    }

    /**
     * Receiver for data sent from FetchAddressIntentService.
     */
    class AddressResultReceiver extends ResultReceiver {
        public AddressResultReceiver(Handler handler) {
            super(handler);
        }

        /**
         * Receives data sent from FetchAddressIntentService and updates the UI in MainActivity.
         */
        @Override
        protected void onReceiveResult(int resultCode, Bundle resultData) {

            // Display the address string or an error message sent from the intent service.
            mAddressOutput = resultData.getString(AppUtils.LocationConstants.RESULT_DATA_KEY);

            mAreaOutput = resultData.getString(AppUtils.LocationConstants.LOCATION_DATA_AREA);

            mCityOutput = resultData.getString(AppUtils.LocationConstants.LOCATION_DATA_CITY);
            mStateOutput = resultData.getString(AppUtils.LocationConstants.LOCATION_DATA_STREET);

        }
    }

    /*
     *  upload image background task
     */
    private class ProgressTask extends AsyncTask<String, Void, Boolean> {
        private ProgressDialog dialog;
        private Context context;

        public ProgressTask(RequestAcceptActivity requestAcceptActivity) {
            context = requestAcceptActivity;
            dialog = new ProgressDialog(context);
        }

        protected void onPreExecute() {
            dialog = new ProgressDialog(context);
            dialog.setMessage("Processing...");
            dialog.setIndeterminate(false);
            dialog.setCancelable(false);
            dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            dialog.show();
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            if (dialog.isShowing()) {
                dialog.dismiss();
            }

        }

        @Override
        protected Boolean doInBackground(final String... args) {
            try {
                imageuploading();
                return true;
            } catch (Exception e) {
                CommonMethods.DebuggableLogE("Schedule", "UpdateSchedule failed", e);
                return false;
            }
        }

    }

    @Override
    public void onBackPressed() {
        CommonKeys.IS_ALREADY_IN_TRIP=true;
        Intent redirectMain = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(redirectMain);
        finish();
    }
}
