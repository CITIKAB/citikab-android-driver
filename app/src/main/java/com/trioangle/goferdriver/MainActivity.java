package com.trioangle.goferdriver;


/**
 * @package com.trioangle.goferdriver
 * @subpackage -
 * @category MainActivity
 * @author Trioangle Product Team
 * @version 1.5
 */

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.IntentSender;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SwitchCompat;
import android.text.TextUtils;
import android.view.Display;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.CompoundButton;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.Result;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.gson.Gson;
import com.trioangle.goferdriver.configs.SessionManager;
import com.trioangle.goferdriver.datamodel.DriverStatus;
import com.trioangle.goferdriver.datamodel.InvoiceModel;
import com.trioangle.goferdriver.datamodel.PaymentDetails;
import com.trioangle.goferdriver.datamodel.RiderDetailsModel;
import com.trioangle.goferdriver.fragments.AccountFragment;
import com.trioangle.goferdriver.fragments.EarningFragment;
import com.trioangle.goferdriver.fragments.HomeFragment;
import com.trioangle.goferdriver.fragments.RatingFragment;
import com.trioangle.goferdriver.helper.BottomNavigationViewHelper;
import com.trioangle.goferdriver.helper.CustomDialog;
import com.trioangle.goferdriver.home.RequestAcceptActivity;
import com.trioangle.goferdriver.home.RequestReceiveActivity;
import com.trioangle.goferdriver.interfaces.ApiService;
import com.trioangle.goferdriver.interfaces.ServiceListener;
import com.trioangle.goferdriver.map.AppUtils;
import com.trioangle.goferdriver.map.GpsService;
import com.trioangle.goferdriver.model.JsonResponse;
import com.trioangle.goferdriver.network.AppController;
import com.trioangle.goferdriver.pushnotification.Config;
import com.trioangle.goferdriver.pushnotification.NotificationUtils;
import com.trioangle.goferdriver.rating.PaymentAmountPage;
import com.trioangle.goferdriver.rating.Riderrating;
import com.trioangle.goferdriver.service.UpdateGPSWorker;
import com.trioangle.goferdriver.service.WorkerUtils;
import com.trioangle.goferdriver.util.CommonKeys;
import com.trioangle.goferdriver.util.CommonMethods;
import com.trioangle.goferdriver.util.MyExceptionHandler;
import com.trioangle.goferdriver.util.RequestCallback;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import javax.inject.Inject;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

import static com.trioangle.goferdriver.util.CommonMethods.DebuggableLogI;
import static com.trioangle.goferdriver.util.CommonMethods.startFirebaseChatListenerService;
import static com.trioangle.goferdriver.util.CommonMethods.stopFirebaseChatListenerService;
import static com.trioangle.goferdriver.util.Enums.REQ_DEVICE_STATUS;
import static com.trioangle.goferdriver.util.Enums.REQ_DRIVER_STATUS;
import static com.trioangle.goferdriver.util.Enums.REQ_INCOMPLETE_TRIP_DETAILS;
import static com.trioangle.goferdriver.util.Enums.REQ_UPDATE_ONLINE;

/* ************************************************************
                MainActivity page
Its main page to connected to all the screen pages
*************************************************************** */


public class MainActivity extends AppCompatActivity implements CompoundButton.OnCheckedChangeListener, ServiceListener, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {


    private static final String TAG = "MAP LOCATION";
    public AlertDialog dialog;
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
    public @InjectView(R.id.switch_driverstatus)
    SwitchCompat switch_driverstatus;
    public @InjectView(R.id.txt_driverstatus)
    TextView txt_driverstatus;
    public @InjectView(R.id.txt_checkdriverstatus)
    TextView txt_checkdriverstatus;
    public @InjectView(R.id.view)
    View view;
    public @InjectView(R.id.homelist)
    ImageView homelist;
    public @InjectView(R.id.activity_main)
    RelativeLayout relativeLayout;
    public @InjectView(R.id.iv_line)
    TextView ivLine;
    public @InjectView(R.id.navigation)
    BottomNavigationView bottomNavigationView;
    public boolean isTripBegin = false;
    public String statusmessage;
    public int width;
    public TranslateAnimation animation;
    public Display display;
    public int count = 1;
    public WindowManager wm;
    public Window window;
    protected boolean isInternetAvailable;
    private int backPressed = 0;
    private BroadcastReceiver mRegistrationBroadcastReceiver;
    public static int selectedFrag = 0;
    private Context mContext;

    protected GoogleApiClient mGoogleApiClient;
    protected LocationRequest locationRequest;
    int REQUEST_CHECK_SETTINGS = 100;
    private Status GPSCallbackStatus;
    private boolean isTriggeredFromDriverAPIErrorMessage = false;

    @OnClick(R.id.txt_checkdriverstatus)
    public void txtCheckDriverStatus() {
        isInternetAvailable = commonMethods.isOnline(this);
        if (isInternetAvailable) {
            updateDriverStatus();
        } else {
            commonMethods.showMessage(getApplicationContext(), dialog, getResources().getString(R.string.no_connection));

        }
    }

   private AlertDialog.Builder builder1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.activity_main);
        //Thread.setDefaultUncaughtExceptionHandler(new MyExceptionHandler(this));

        ButterKnife.inject(this);
        AppController.getAppComponent().inject(this);
        mContext = this;

        builder1 = new AlertDialog.Builder(mContext);

        wm = (WindowManager) getApplicationContext().getSystemService(Context.WINDOW_SERVICE);
        window = this.getWindow();

        dialog = commonMethods.getAlertDialog(this);

        /*
         *  Common loader initialize and internet connection check
         */
        isInternetAvailable = commonMethods.isOnline(this);

        if (!isInternetAvailable) {

            dialogfunction(); // Show dialog for internet connection not available
        }


        display = getWindowManager().getDefaultDisplay();
        width = display.getWidth();

        translateAnimation();

        // autoStart();

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this).build();
        try {
            mGoogleApiClient.connect();
        } catch (Exception e) {
            e.printStackTrace();
        }
        locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(10*1000);
        locationRequest.setFastestInterval(5 * 1000);


        initView();


    }





    public void initView() {
        switch_driverstatus.setSwitchPadding(40);
        switch_driverstatus.setOnCheckedChangeListener(this);

        if (sessionManager.getDriverSignupStatus().equals("Pending")) {
            txt_checkdriverstatus.setVisibility(View.VISIBLE);
            txt_driverstatus.setVisibility(View.GONE);
            switch_driverstatus.setVisibility(View.GONE);
            view.setVisibility(View.GONE);
        }



        /*
         * Set driver status
         */
        if (sessionManager.getDriverStatus() == null) {
            txt_driverstatus.setText(getResources().getString(R.string.offline));
            switch_driverstatus.setChecked(false);
            sessionManager.setDriverStatus("Offline");
        } else {
            if (sessionManager.getDriverStatus().equals("Online")) {

                txt_driverstatus.setText(getResources().getString(R.string.online));
                switch_driverstatus.setChecked(true);
                sessionManager.setDriverStatus("Online");
            } else {
                txt_driverstatus.setText(getResources().getString(R.string.offline));
                switch_driverstatus.setChecked(false);
                sessionManager.setDriverStatus("Offline");
            }
        }

        addingFragment();


        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            relativeLayout.setVisibility(View.VISIBLE);
            relativeLayout.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        doCircularReveal();
                                    }
                                }
            );
        }



        /*
         *  FCM push notification receive function
         */
        receivepushnotification();

        /*
         *  Update current location
         */

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {

                isInternetAvailable = commonMethods.isOnline(getApplicationContext());
                if (isInternetAvailable) {
                    updateOnlineStatus();
                } else {
                    commonMethods.showMessage(MainActivity.this, dialog, getResources().getString(R.string.no_connection));
                }
            }
        }, 5000);

        updateDeviceId(); // Update FCM device id


    }

    private void callIncompleteTripDetailsAPI() {
        if (!CommonKeys.IS_ALREADY_IN_TRIP) {
            commonMethods.showProgressDialog(this, customDialog);
            apiService.getInCompleteTripsDetails(sessionManager.getAccessToken()).enqueue(new RequestCallback(REQ_INCOMPLETE_TRIP_DETAILS, this));
        }
    }

    /**
     * Bottom navigation view to show and set fragment for Home page, Earning page, Rating page, Account page
     */

    private void addingFragment() {

        BottomNavigationViewHelper.removeShiftMode(bottomNavigationView);
        if (sessionManager.getUserType()!=null&&!TextUtils.isEmpty(sessionManager.getUserType())&&!sessionManager.getUserType().equalsIgnoreCase("0")&&!sessionManager.getUserType().equalsIgnoreCase("1")) {
            bottomNavigationView.getMenu().getItem(1).setIcon(getResources().getDrawable(R.drawable.tab_trips));
            bottomNavigationView.getMenu().getItem(1).setTitle(getResources().getString(R.string.trips));
        }
        bottomNavigationView.setOnNavigationItemSelectedListener
                (new BottomNavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                        Fragment selectedFragment = null;

                        switch (item.getItemId()) {
                            case R.id.tab_home:
                                if (bottomNavigationView.getSelectedItemId() != R.id.tab_home)
                                    selectedFragment = HomeFragment.newInstance();
                                break;
                            case R.id.tab_earning:
                                if (bottomNavigationView.getSelectedItemId() != R.id.tab_earning)
                                    selectedFragment = EarningFragment.newInstance();
                                break;
                            case R.id.tab_rating:
                                if (bottomNavigationView.getSelectedItemId() != R.id.tab_rating)
                                    selectedFragment = RatingFragment.newInstance();
                                break;
                            case R.id.tab_profile:
                                if (bottomNavigationView.getSelectedItemId() != R.id.tab_profile)
                                    selectedFragment = AccountFragment.newInstance();
                                break;
                            default:
                                break;
                        }
                        if (selectedFragment != null) {
                            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                            transaction.replace(R.id.frame_layout, selectedFragment);
                            transaction.commit();
                        }
                        return true;
                    }
                });


        //Manually displaying the first fragment - one time only
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.frame_layout, HomeFragment.newInstance());
        transaction.commit();
    }


    /**
     * To do translate animation
     */


    public void translateAnimation() {
        animation = new TranslateAnimation(5.0f, width - 50,
                0.0f, 0.0f);          //  new TranslateAnimation(xFrom,xTo, yFrom,yTo)
        animation.setDuration(1000);  // animation duration
        animation.setRepeatCount(1000);  // animation repeat count
        animation.setRepeatMode(2);   // repeat animation (left to right, right to left )
        animation.setFillAfter(true);
        animation.setFillEnabled(true);
        animation.setRepeatMode(ValueAnimator.REVERSE);
        animation.setInterpolator(new AccelerateInterpolator(2));

        animation.setAnimationListener(new Animation.AnimationListener() {

            @Override
            public void onAnimationStart(Animation arg0) {

                animation = new TranslateAnimation(5.0f, width - 150,
                        0.0f, 0.0f);
                animation.setDuration(1000);  // animation duration
                animation.setRepeatCount(1000);  // animation repeat count
                animation.setRepeatMode(2);   // repeat animation (left to right, right to left )
                animation.setFillAfter(true);
                animation.setFillEnabled(true);

                final RelativeLayout.LayoutParams lparams = new RelativeLayout.LayoutParams(50, 10);
                ivLine.setLayoutParams(lparams);
            }

            @Override
            public void onAnimationRepeat(Animation arg0) {


            }

            @Override
            public void onAnimationEnd(Animation arg0) {
                animation.setDuration(1000);  // animation duration
                animation.setRepeatCount(1000);  // animation repeat count
                animation.setRepeatMode(2);   // repeat animation (left to right, right to left )
                animation.setFillAfter(true);
                animation.setFillEnabled(true);
                final RelativeLayout.LayoutParams lparams = new RelativeLayout.LayoutParams(10, 10);
                ivLine.setLayoutParams(lparams);

            }
        });


        //animation.setFillAfter(true);

        ivLine.startAnimation(animation);
    }


    public void updateOnlineStatus() {

        apiService.updateLocation(getLocation()).enqueue(new RequestCallback(REQ_UPDATE_ONLINE, this));

    }


    public void updateDriverStatus() {
        apiService.updateCheckStatus(getStatus()).enqueue(new RequestCallback(REQ_DRIVER_STATUS, this));

    }

    /**
     * Hash map for update driver status
     **/

    public HashMap<String, String> getStatus() {
        HashMap<String, String> driverStatusHashMap = new HashMap<>();
        driverStatusHashMap.put("user_type", sessionManager.getType());
        driverStatusHashMap.put("token", sessionManager.getAccessToken());
        return driverStatusHashMap;
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
     *  FCM push nofication received funcation called
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


                    if (JSON_DATA != null && count == 1) {
                        JSONObject jsonObject = null;
                        try {
                            jsonObject = new JSONObject(JSON_DATA);
                            if (jsonObject.getJSONObject("custom").has("ride_request")) {
                                count++;
                                Intent requstreceivepage = new Intent(getApplicationContext(), RequestReceiveActivity.class);
                                startActivity(requstreceivepage);
                            } else if (jsonObject.getJSONObject("custom").has("cancel_trip")) {
                                statusDialog(getResources().getString(R.string.yourtripcancelledbydriver), 1);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                }
            }
        };
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CHECK_SETTINGS) {
            if (resultCode == RESULT_OK) {
                //GPS Enabled
                callIncompleteTripDetailsAPI();
            } else {
                //GPS not enabled
                //Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                //            startActivity(intent);
                showGPSNotEnabledWarning();
            }

        }
    }

    private void showGPSNotEnabledWarning() {

        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(getResources().getString(R.string.location_not_enabled_please_enable_location));
        builder.setCancelable(true);
        builder.setNegativeButton(getResources().getString(R.string.cancel),(dialogInterface, i) -> dialogInterface.dismiss());
        builder.setPositiveButton(getResources().getString(R.string.ok), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                try {
                    GPSCallbackStatus.startResolutionForResult((Activity) mContext, REQUEST_CHECK_SETTINGS);
                } catch (IntentSender.SendIntentException e) {
                    AppUtils.openLocationEnableScreen(mContext);
                    e.printStackTrace();
                }
            }
        });
        AlertDialog dialog = builder.create();
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE); //before
        dialog.show();

    }

    /*
     *  Check driver status is online or offline
     */
    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        switch (buttonView.getId()) {
            case R.id.switch_driverstatus:
                DebuggableLogI("switch_compat", isChecked + "");
                if (switch_driverstatus.isChecked()) {

                    txt_driverstatus.setText(getResources().getString(R.string.online));
                    sessionManager.setDriverStatus("Online");


                    isInternetAvailable = commonMethods.isOnline(this);
                    if (isInternetAvailable) {
                        if(!isTriggeredFromDriverAPIErrorMessage){
                            updateOnlineStatus();
                        }else{
                            isTriggeredFromDriverAPIErrorMessage = false;
                        }

                    } else {
                        commonMethods.showMessage(MainActivity.this, dialog, getResources().getString(R.string.no_connection));
                    }

                } else {
                    /*if (isMyServiceRunning(GpsService.class)) {
                        Intent GPSservice = new Intent(this, GpsService.class);
                        stopService(GPSservice);
                    }*/
                    if(WorkerUtils.isWorkRunning(CommonKeys.WorkTagForUpdateGPS))
                        WorkerUtils.cancelWorkByTag(CommonKeys.WorkTagForUpdateGPS);
                    txt_driverstatus.setText(getResources().getString(R.string.offline));
                    sessionManager.setDriverStatus("Offline");


                    if (isInternetAvailable) {
                        if(!isTriggeredFromDriverAPIErrorMessage){
                            updateOnlineStatus();
                        }else{
                            isTriggeredFromDriverAPIErrorMessage = false;
                        }

                    } else {
                        commonMethods.showMessage(MainActivity.this, dialog, getResources().getString(R.string.no_connection));
                    }
                }
                break;
            default:
                break;
        }

    }

    /*
     *  Animate home page
     */
    private void doCircularReveal() {

        // get the center for the clipping circle
        int centerX = (relativeLayout.getLeft() + relativeLayout.getRight()) / 2;
        int centerY = (relativeLayout.getTop() + relativeLayout.getBottom()) / 2;

        int startRadius = 0;
        // get the final radius for the clipping circle
        int endRadius = Math.max(relativeLayout.getWidth(), relativeLayout.getHeight());

        // create the animator for this view (the start radius is zero)
        Animator anim =
                null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            anim = ViewAnimationUtils.createCircularReveal(relativeLayout,
                    centerX, centerY, startRadius, endRadius);
        }
        anim.setDuration(1500);
        // make the view invisible when the animation is done
        anim.addListener(new AnimatorListenerAdapter() {
            public void onAnimationStart(Animator animation) {
                // splash_logo.setBackgroundColor(getResources().getColor(R.color.colorblack));
            }

            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                //splash_logo.setBackgroundColor(getResources().getColor(R.color.white));
                //  splash_logo.setVisibility(View.GONE);
            }
        });


        anim.start();
    }


    @Override
    public void onBackPressed() {
        if (backPressed >= 1) {
            // startActivity(new Intent(this, MainActivity.class));
            CommonKeys.IS_ALREADY_IN_TRIP=false;
            finishAffinity();
            super.onBackPressed();


        } else {
            // clean up
            backPressed = backPressed + 1;
            Toast.makeText(this, getResources().getString(R.string.pressbackagain),
                    Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        CommonKeys.IS_ALREADY_IN_TRIP=false;
    }

    @Override
    public void onResume() {
        super.onResume();

        count = 1;
        // register FCM registration complete receiver
        LocalBroadcastManager.getInstance(this).registerReceiver(mRegistrationBroadcastReceiver,
                new IntentFilter(Config.REGISTRATION_COMPLETE));

        // register new push message receiver
        // by doing this, the activity will be notified each time a new message arrives
        LocalBroadcastManager.getInstance(this).registerReceiver(mRegistrationBroadcastReceiver,
                new IntentFilter(Config.PUSH_NOTIFICATION));

        // clear the notification area when the app is opened
        NotificationUtils.clearNotifications(getApplicationContext());

        // code to initiate firebase chat service

        if (!TextUtils.isEmpty(sessionManager.getTripId()) && sessionManager.isDriverAndRiderAbleToChat()) {
            startFirebaseChatListenerService(this);
        } else {
            stopFirebaseChatListenerService(this);
        }



    }

    @Override
    public void onPause() {
        super.onPause();
    }

    public void onSuccessUpdateOnline() {
        if (sessionManager.getDriverStatus().equals("Offline")) {
            /*if (isMyServiceRunning(GpsService.class)) {
                Intent GPSservice = new Intent(getApplicationContext(), GpsService.class);
                stopService(GPSservice);
            }*/
            if(WorkerUtils.isWorkRunning(CommonKeys.WorkTagForUpdateGPS))
                WorkerUtils.cancelWorkByTag(CommonKeys.WorkTagForUpdateGPS);
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
    }

    public void onSuccessUpdateDriverStatus(JsonResponse jsonResp) {
        DriverStatus signInUpResultModel = gson.fromJson(jsonResp.getStrResponse(), DriverStatus.class);
        if (signInUpResultModel != null) {
            String driver_status = signInUpResultModel.getDriverStatus();
            sessionManager.setDriverSignupStatus(driver_status);
            if ("Active".equals(driver_status)) {
                commonMethods.showMessage(this, dialog, getResources().getString(R.string.active));
                txt_checkdriverstatus.setVisibility(View.GONE);
                txt_driverstatus.setVisibility(View.VISIBLE);
                switch_driverstatus.setVisibility(View.VISIBLE);
                view.setVisibility(View.VISIBLE);
            } else {
                commonMethods.showMessage(this, dialog, getResources().getString(R.string.waiting));
            }
        }
    }


    @Override
    public void onSuccess(JsonResponse jsonResp, String data) {

        if (!jsonResp.isOnline()) {
            if (!TextUtils.isEmpty(data))
                commonMethods.showMessage(this, dialog, data);
            return;
        }


        switch (jsonResp.getRequestCode()) {

            case REQ_UPDATE_ONLINE:
                commonMethods.hideProgressDialog();
                if (jsonResp.isSuccess()) {
                    onSuccessUpdateOnline();

                } else if (!TextUtils.isEmpty(jsonResp.getStatusMsg()) && jsonResp.getStatusMsg().equals("Please Complete your current trip")) {


                    String onlinetext = txt_driverstatus.getText().toString();
                    if (onlinetext.equals(getResources().getString(R.string.offline))) {
                        statusmessage = jsonResp.getStatusMsg();
                        dialogfunction2();
isTriggeredFromDriverAPIErrorMessage = true;
                        txt_driverstatus.setText(getResources().getString(R.string.online));
                        sessionManager.setDriverStatus("Online");
                        switch_driverstatus.setChecked(true);
                    }

                }
                break;
            case REQ_DRIVER_STATUS:
                commonMethods.hideProgressDialog();
                if (jsonResp.isSuccess()) {
                    onSuccessUpdateDriverStatus(jsonResp);
                } else if (!TextUtils.isEmpty(jsonResp.getStatusMsg())) {
                    commonMethods.showMessage(this, dialog, jsonResp.getStatusMsg());
                }
                break;
            case REQ_DEVICE_STATUS:
                commonMethods.hideProgressDialog();
                if (!jsonResp.isSuccess()) {
                    commonMethods.showMessage(this, dialog, jsonResp.getStatusMsg());
                }
                break;
            case REQ_INCOMPLETE_TRIP_DETAILS: {
                commonMethods.hideProgressDialog();
                if (jsonResp.isSuccess()) {
                    onSuccessIncompleteTripDetails(jsonResp);
                }

                break;
            }
            default:
                break;
        }
    }

    @Override
    public void onFailure(JsonResponse jsonResp, String data) {
        commonMethods.hideProgressDialog();

    }

    private void onSuccessIncompleteTripDetails(JsonResponse jsonResp) {

        RiderDetailsModel earningModel = gson.fromJson(jsonResp.getStrResponse(), RiderDetailsModel.class);
        String tripStatus = earningModel.getPaymentDetails().getTripStatus();
        sessionManager.setTripId(earningModel.getTripId().toString());
        ArrayList<InvoiceModel> invoiceModels = earningModel.getInvoice();
        PaymentDetails paymentDetails = earningModel.getPaymentDetails();
        sessionManager.setPaymentMethod(paymentDetails.getPaymentMethod());
        // acceptedDriverDetails =new AcceptedDriverDetails(ridername, mobilenumber, profileimg, ratingvalue, cartype, pickuplocation, droplocation, pickuplatitude, droplatitude, droplongitude,pickuplongitude);
        // Pass different data based on trip status
        if ("Scheduled".equals(tripStatus) || "Begin trip".equals(tripStatus) || "End trip".equals(tripStatus)) {
            Intent requstreceivepage = new Intent(this, RequestAcceptActivity.class);
            requstreceivepage.putExtra("riderDetails", earningModel);
            commonMethods.hideProgressDialog();
            if ("Scheduled".equals(tripStatus)) {
                //sessionManager.setTripStatus("CONFIRM YOU'VE ARRIVED");
                sessionManager.setTripStatus(CommonKeys.TripStaus.ConfirmArrived);
                sessionManager.setSubTripStatus(getResources().getString(R.string.confirm_arrived));
                requstreceivepage.putExtra("isTripBegin", false);
                requstreceivepage.putExtra("tripstatus", getResources().getString(R.string.confirm_arrived));
            } else if ("Begin trip".equals(tripStatus)) {
                //sessionManager.setTripStatus("CONFIRM YOU'VE ARRIVED");
                sessionManager.setTripStatus(CommonKeys.TripStaus.ConfirmArrived);
                sessionManager.setSubTripStatus(getResources().getString(R.string.begin_trip));
                requstreceivepage.putExtra("isTripBegin", false);
                requstreceivepage.putExtra("tripstatus", getResources().getString(R.string.begin_trip));
            } else if ("End trip".equals(tripStatus)) {
                //sessionManager.setTripStatus("Begin Trip");
                sessionManager.setTripStatus(CommonKeys.TripStaus.BeginTrip);
                sessionManager.setSubTripStatus(getResources().getString(R.string.end_trip));
                requstreceivepage.putExtra("isTripBegin", true);
                requstreceivepage.putExtra("tripstatus", getResources().getString(R.string.end_trip));
            }
            startActivity(requstreceivepage);
        } else if ("Rating".equals(tripStatus)) {
            //sessionManager.setTripStatus("End Trip");
            sessionManager.setTripStatus(CommonKeys.TripStaus.EndTrip);
            Intent rating = new Intent(this, Riderrating.class);
            rating.putExtra("imgprofile", earningModel.getRiderThumbImage());
            commonMethods.hideProgressDialog();
            startActivity(rating);

        } else if ("Payment".equals(tripStatus)) {

            Bundle bundle = new Bundle();
            bundle.putSerializable("invoiceModels", invoiceModels);
            Intent main = new Intent(this, PaymentAmountPage.class);
            main.putExtra("AmountDetails", jsonResp.getStrResponse());
            main.putExtras(bundle);
            commonMethods.hideProgressDialog();
            startActivity(main);

        }
        overridePendingTransition(R.anim.ub__slide_in_right, R.anim.ub__slide_out_left);


    }

    /*
     *   Internet not available dialog
     */
    public void dialogfunction() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(getResources().getString(R.string.turnoninternet))
                .setCancelable(false)
                .setPositiveButton(getResources().getString(R.string.ok), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        builder.setCancelable(true);
                    }
                });

        AlertDialog alert = builder.create();
        alert.show();
    }


    /*
     *  Dialog for arrive now , begin trip, end trip, payment completed
     */
    public void statusDialog(String message, final int show) {
        /*new AlertDialog.Builder(this)
                .setTitle(message)
                //.setMessage("Are you sure you want to delete this entry?")
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                        dialog.dismiss();
                    }
                })
                .show();*/


        System.out.println("this.isFinishing() : " + this.isFinishing());
        if (!this.isFinishing()) {
            AlertDialog alert11 = builder1.create();
            System.out.println("Print Messag "+message);
            builder1.setMessage(message);
            builder1.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });

            if (!alert11.isShowing()){
                try {
                    alert11.show();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            /*new AlertDialog.Builder(getApplicationContext(),R.style.AppTheme)
                    .setTitle(message)
                    //.setMessage("Are you sure you want to delete this entry?")
                    .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();

                        }
                    }).show();*/
        }


    }

    /*
     *  Receive push notification
     */
    public void receivePushNotification() {
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


                    try {
                        JSONObject jsonObject = new JSONObject(JSON_DATA);

                        if (jsonObject.getJSONObject("custom").has("arrive_now")) {
                            isTripBegin = false;
                            statusDialog(getResources().getString(R.string.driverarrrive), 0);

                        } else if (jsonObject.getJSONObject("custom").has("cancel_trip")) {


                            statusDialog(getResources().getString(R.string.yourtripcancelledbydriver), 1);
                        }


                    } catch (JSONException e) {

                    }
                }
            }
        };
    }

    /*
     *  Dialog for driver status (Active or pending)
     */
    public void dialogfunction2() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(statusmessage)
                .setCancelable(false)
                .setPositiveButton(getResources().getString(R.string.ok), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        //switch_driverstatus.setChecked(false);
                    }
                });

        AlertDialog alert = builder.create();
        alert.show();
    }

    /*
     *  Update driver device id API call
     */

    public void updateDeviceId() {
        if (sessionManager.getAccessToken() != null && sessionManager.getDeviceId() != null) {

            apiService.updateDevice(getDeviceId()).enqueue(new RequestCallback(REQ_DEVICE_STATUS, this));
        }
    }


    /**
     * Hash map for update driver status
     **/

    public HashMap<String, String> getDeviceId() {
        HashMap<String, String> driverStatusHashMap = new HashMap<>();
        driverStatusHashMap.put("user_type", sessionManager.getType());
        driverStatusHashMap.put("device_type", sessionManager.getDeviceType());
        driverStatusHashMap.put("device_id", sessionManager.getDeviceId());
        driverStatusHashMap.put("token", sessionManager.getAccessToken());
        return driverStatusHashMap;
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
     *  Enable Xiaomi device background service enable
     */
    public void autoStart() {
        String manufacturer = "xiaomi";
        if (manufacturer.equalsIgnoreCase(android.os.Build.MANUFACTURER)) {
            AlertDialog.Builder dialog = new AlertDialog.Builder(MainActivity.this);
            dialog.setMessage("Service autostart not enabled!");
            dialog.setPositiveButton("Open autostart settings", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                    //this will open auto start screen where user can enable permission for your app
                    Intent intent = new Intent();
                    intent.setComponent(new ComponentName("com.miui.securitycenter", "com.miui.permcenter.autostart.AutoStartManagementActivity"));
                    startActivity(intent);
                }
            });
            dialog.show();

        }
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(locationRequest);
        builder.setAlwaysShow(true);
        PendingResult result =
                LocationServices.SettingsApi.checkLocationSettings(
                        mGoogleApiClient,
                        builder.build()
                );
        result.setResultCallback(new ResultCallback() {
            @Override
            public void onResult(@NonNull Result result) {
                GPSCallbackStatus = result.getStatus();
                switch (GPSCallbackStatus.getStatusCode()) {
                    case LocationSettingsStatusCodes.SUCCESS:
                        DebuggableLogI(TAG, "All location settings are satisfied.");
                        callIncompleteTripDetailsAPI();
                        break;
                    case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                        DebuggableLogI(TAG, "Location settings are not satisfied. Show the user a dialog to upgrade location settings ");

                        try {
                            // Show the dialog by calling startResolutionForResult(), and check the result
                            // in onActivityResult().
                            GPSCallbackStatus.startResolutionForResult((Activity) mContext, REQUEST_CHECK_SETTINGS);
                        } catch (IntentSender.SendIntentException e) {
                            DebuggableLogI(TAG, "PendingIntent unable to execute request.");
                        }
                        break;
                    case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                        DebuggableLogI(TAG, "Location settings are inadequate, and cannot be fixed here. Dialog not created.");
                        break;
                    default:
                        break;
                }
            }
        });
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }
}
