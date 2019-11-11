package com.trioangle.goferdriver.home;
/**
 * @package com.trioangle.goferdriver.home
 * @subpackage home
 * @category RequestReceiveActivity
 * @author Trioangle Product Team
 * @version 1.5
 */

import android.app.ActivityManager;
import android.app.KeyguardManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.PowerManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
import com.squareup.picasso.Picasso;
import com.trioangle.goferdriver.MainActivity;
import com.trioangle.goferdriver.R;
import com.trioangle.goferdriver.configs.SessionManager;
import com.trioangle.goferdriver.database.AddFirebaseDatabase;
import com.trioangle.goferdriver.datamodel.RiderDetailsModel;
import com.trioangle.goferdriver.helper.CircularMusicProgressBar;
import com.trioangle.goferdriver.helper.CustomDialog;
import com.trioangle.goferdriver.helper.FirebaseDatabaseAdder;
import com.trioangle.goferdriver.helper.WaveDrawable;
import com.trioangle.goferdriver.interfaces.ApiService;
import com.trioangle.goferdriver.interfaces.ServiceListener;
import com.trioangle.goferdriver.map.GpsService;
import com.trioangle.goferdriver.model.JsonResponse;
import com.trioangle.goferdriver.network.AppController;
import com.trioangle.goferdriver.service.UpdateGPSWorker;
import com.trioangle.goferdriver.service.WorkerUtils;
import com.trioangle.goferdriver.util.CommonKeys;
import com.trioangle.goferdriver.util.CommonMethods;
import com.trioangle.goferdriver.util.RequestCallback;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Locale;

import javax.inject.Inject;

import butterknife.ButterKnife;
import butterknife.InjectView;
import io.github.krtkush.lineartimer.LinearTimer;
import io.github.krtkush.lineartimer.LinearTimerView;

import static com.trioangle.goferdriver.util.CommonMethods.DebuggableLogE;

/* ************************************************************
                      RequestReceiveActivity
Its used to get RequestReceiveActivity for rider with details
*************************************************************** */
public class RequestReceiveActivity extends AppCompatActivity implements LinearTimer.TimerListener, ServiceListener {

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
    @InjectView(R.id.request_receive_dialog_layout)
    public RelativeLayout request_receive_dialog_layout;
    @InjectView(R.id.linearTimer)
    public LinearTimerView linearTimerView;
    @InjectView(R.id.map_snap)
    public ImageView map_snap;
    @InjectView(R.id.req_min)
    public TextView req_min;
    @InjectView(R.id.req_address)
    public TextView req_address;
    public WaveDrawable waveDrawable;
    public MediaPlayer mPlayer;
    public ProgressDialog pd;
    public CircularMusicProgressBar progressBar;
    public int count = 1;
    public String JSON_DATA;
    public String min, req_id, pickup_address;
    public String staticMapURL;
    protected boolean isInternetAvailable;
    private LinearTimer linearTimer;
    private long duration = 10 * 1000;

    private AddFirebaseDatabase addFirebaseDatabase=new AddFirebaseDatabase();

    RiderDetailsModel riderModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_request_receive);

        ButterKnife.inject(this);
        AppController.getAppComponent().inject(this);
        dialog = commonMethods.getAlertDialog(this);
        PowerManager pm = (PowerManager) getApplicationContext().getSystemService(Context.POWER_SERVICE);
        PowerManager.WakeLock wakeLock = pm.newWakeLock((PowerManager.SCREEN_BRIGHT_WAKE_LOCK | PowerManager.FULL_WAKE_LOCK | PowerManager.ACQUIRE_CAUSES_WAKEUP), "TAG");
        wakeLock.acquire();
        KeyguardManager keyguardManager = (KeyguardManager) getApplicationContext().getSystemService(Context.KEYGUARD_SERVICE);
        KeyguardManager.KeyguardLock keyguardLock = keyguardManager.newKeyguardLock("TAG");
        keyguardLock.disableKeyguard();
                    /*
                    *  Common loader and internet check
                    */
        isInternetAvailable = commonMethods.isOnline(this);

        req_min = (TextView) findViewById(R.id.req_min);
        req_address = (TextView) findViewById(R.id.req_address);

                    /*
                    *  After rider send request driver can receive the request
                    */
        JSON_DATA = sessionManager.getPushJson();


                    /*
                    *   Get Rider details and request details
                    */
        if (JSON_DATA != null) {
            try {

                JSONObject jsonObject = new JSONObject(JSON_DATA);
                if (jsonObject.getJSONObject("custom").has("ride_request")) {
                    min = jsonObject.getJSONObject("custom").getJSONObject("ride_request").getString("min_time");
                    req_id = jsonObject.getJSONObject("custom").getJSONObject("ride_request").getString("request_id");
                    pickup_address = jsonObject.getJSONObject("custom").getJSONObject("ride_request").getString("pickup_location");


                    if (Integer.parseInt(min) > 1) {
                        req_min.setText(min + " " + getResources().getString(R.string.minutes));
                    } else {
                        req_min.setText(min + " " + getResources().getString(R.string.minute));
                    }
                    req_address.setText(pickup_address);
                }

                progressBar = (CircularMusicProgressBar) findViewById(R.id.album_art);

                String lat = jsonObject.getJSONObject("custom").getJSONObject("ride_request").getString("pickup_latitude");
                String log = jsonObject.getJSONObject("custom").getJSONObject("ride_request").getString("pickup_longitude");
                String pickupstr = lat + "," + log;
                String positionOnMap = "&markers=size:mid|icon:" + CommonKeys.imageUrl + "man_marker.png|" + pickupstr;

                staticMapURL = "https://maps.googleapis.com/maps/api/staticmap?size=250x250&" +
                        lat + "," + log +
                        "" + positionOnMap + "&zoom=14" +
                        "&key=" + sessionManager.getGoogleMapKey() + "&language=" +
                        Locale.getDefault();
                Picasso.with(this).load(staticMapURL)
                        .into(progressBar);
                // set progress to 40%

                circularProgressfunction();
                progressBar.setValue(100);

                progressBar.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        try {
                            linearTimer.pauseTimer();
                            //linearTimer.resetTimer();
                        } catch (IllegalStateException e) {
                            e.printStackTrace();
                            // Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                        waveDrawable.stopAnimation();
                        mPlayer.release();

                        pd = new ProgressDialog(RequestReceiveActivity.this);
                        pd.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                        pd.setMessage(getResources().getString(R.string.acceptingpickup));
                        pd.setCancelable(false);
                        pd.show();
                    /*
                    *  Accept request API call
                    */


                        if (isInternetAvailable) {
                            acceptDriver();
                        } else {
                            commonMethods.showMessage(RequestReceiveActivity.this, dialog, getResources().getString(R.string.Interneterror));
                        }
                    }
                });


            } catch (JSONException e) {

            }
        }

    }

    /*
    *  Request page circular animation function
    */
    public void circularProgressfunction() {
        Display display = getWindowManager().getDefaultDisplay();
        int width = display.getWidth();
        int height = display.getHeight();
        int radius;
        if (width < height)
            radius = (int) (width / 2.2);
        else
            radius = (int) (height / 2.2);
        System.out.print("radius: " + radius);
        //radius-= 4;
        radius = (int) (radius / getResources().getDisplayMetrics().density);
        System.out.print("Height: " + height + " Width: " + width);

        waveDrawable = new WaveDrawable(getResources().getColor(R.color.app_continue), width - 250);


        ViewTreeObserver vto = linearTimerView.getViewTreeObserver();
        vto.addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            public boolean onPreDraw() {
                linearTimerView.getViewTreeObserver().removeOnPreDrawListener(this);
                int finalHeight1 = linearTimerView.getMeasuredHeight();
                int finalWidth1 = linearTimerView.getMeasuredWidth();
                System.out.print("Height: " + finalHeight1 + " Width: " + finalWidth1);
                map_snap.getLayoutParams().height = (int) (finalHeight1 / 1.1);
                map_snap.getLayoutParams().width = (int) (finalWidth1 / 1.1);
                map_snap.requestLayout();
                return true;
            }
        });
        System.out.print("radius: " + radius);
        linearTimerView.setCircleRadiusInDp(radius);
        count = 1;

        linearTimer = new LinearTimer.Builder()
                .linearTimerView(linearTimerView)
                .duration(duration)
                .timerListener(this)
                .progressDirection(LinearTimer.COUNTER_CLOCK_WISE_PROGRESSION)
                .preFillAngle(0)
                .endingAngle(360)
                .getCountUpdate(LinearTimer.COUNT_UP_TIMER, 1000)
                .build();

        request_receive_dialog_layout.setBackgroundDrawable(waveDrawable);
        Interpolator interpolator = new LinearInterpolator();

        waveDrawable.setWaveInterpolator(interpolator);
        waveDrawable.startAnimation();

        try {
            linearTimerView.clearAnimation();
            linearTimerView.animate();
            linearTimerView.setAnimation(null);
            linearTimer.startTimer();
            linearTimerView.setVisibility(View.GONE);

        } catch (IllegalStateException e) {
            e.printStackTrace();
        }
    }

    /*
    *  After given time automatically to stop animation
    */
    @Override
    public void animationComplete() {

        PowerManager pm = (PowerManager) getApplicationContext().getSystemService(Context.POWER_SERVICE);
        PowerManager.WakeLock wakeLock = pm.newWakeLock((PowerManager.SCREEN_BRIGHT_WAKE_LOCK | PowerManager.FULL_WAKE_LOCK | PowerManager.ACQUIRE_CAUSES_WAKEUP), "TAG");
        wakeLock.acquire();

        KeyguardManager keyguardManager = (KeyguardManager) getApplicationContext().getSystemService(Context.KEYGUARD_SERVICE);
        KeyguardManager.KeyguardLock keyguardLock = keyguardManager.newKeyguardLock("TAG");
        keyguardLock.disableKeyguard();

        waveDrawable.stopAnimation();
        //  finish();
        if (count == 1) {
            count++;


            sessionManager.setPushJson(null);

            Intent requestaccept = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(requestaccept);
            finish();


        }
    }


    /*
    *   Animation time
    */
    @Override
    public void timerTick(long tickUpdateInMillis) {
        CommonMethods.DebuggableLogI("Time left", String.valueOf(tickUpdateInMillis));

        /*String formattedTime = String.format("%02d:%02d",
                TimeUnit.MILLISECONDS.toMinutes(tickUpdateInMillis),
                TimeUnit.MILLISECONDS.toSeconds(tickUpdateInMillis)
                        - TimeUnit.MINUTES
                        .toSeconds(TimeUnit.MILLISECONDS.toHours(tickUpdateInMillis)));*/
        mPlayer = MediaPlayer.create(this, R.raw.gofer);
        mPlayer.start();
        mPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                mp.release();
            }
        });
    }

    @Override
    public void onTimerReset() {

    }


    @Override
    public void onSuccess(JsonResponse jsonResp, String data) {
        if (jsonResp.isSuccess()) {
            onSuccessAccept(jsonResp);
        } else if (!TextUtils.isEmpty(jsonResp.getStatusMsg())) {
            commonMethods.showMessage(this, dialog, jsonResp.getStatusMsg());
            pd.dismiss();
            Intent requestaccept = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(requestaccept);
            finish();
        }
    }

    @Override
    public void onFailure(JsonResponse jsonResp, String data) {
        if (!TextUtils.isEmpty(jsonResp.getStatusMsg())) {
            commonMethods.showMessage(this, dialog, jsonResp.getStatusMsg());
            pd.dismiss();
        }
    }

    @Override
    public void onBackPressed() {
        //This Method is Empty Because we have to strict the backpress When Request Receive
    }

    /*
    *   Get request rider details after accept
    */
    public void acceptDriver() {
        apiService.acceptRequest(sessionManager.getType(), req_id, "Trip", sessionManager.getAccessToken()).enqueue(new RequestCallback(this));
    }

    public void onSuccessAccept(JsonResponse jsonResp) {
        pd.dismiss();
        riderModel = gson.fromJson(jsonResp.getStrResponse(), RiderDetailsModel.class);

        addFirebaseDatabase.updateRequestTable(riderModel.getRiderId(),String.valueOf(riderModel.getTripId()));

        sessionManager.setRiderName(riderModel.getRiderName());
        sessionManager.setRiderRating(riderModel.getRatingValue());
        sessionManager.setRiderProfilePic(riderModel.getRiderThumbImage());
        sessionManager.setBookingType(riderModel.getBookingType());
        sessionManager. setTripId(riderModel.getTripId().toString());
        sessionManager.setSubTripStatus(getResources().getString(R.string.confirm_arrived));
        //sessionManager.setTripStatus("CONFIRM YOU'VE ARRIVED");
        sessionManager.setTripStatus(CommonKeys.TripStaus.ConfirmArrived);
        sessionManager.setPaymentMethod(riderModel.getPaymentMethod());

        sessionManager.setDriverAndRiderAbleToChat(true);
        CommonMethods.startFirebaseChatListenerService(this);



        /*if (!CommonMethods.isMyBackgroundServiceRunning(GpsService.class,this)) {
            Intent GPSservice = new Intent(getApplicationContext(), GpsService.class);
            startService(GPSservice);
        }*/
        if(!WorkerUtils.isWorkRunning(CommonKeys.WorkTagForUpdateGPS)) {
            DebuggableLogE("locationupdate", "StartWork:");
            WorkerUtils.startWorkManager(CommonKeys.WorkKeyForUpdateGPS, CommonKeys.WorkTagForUpdateGPS, UpdateGPSWorker.class);
        }

        //  acceptedDriverDetails = new AcceptedDriverDetails(ridername, mobilenumber, profileimg, ratingvalue, cartype, pickuplocation, droplocation, pickuplatitude, droplatitude, droplongitude, pickuplongitude);
//        mPlayer.stop();
        Intent requestaccept = new Intent(getApplicationContext(), RequestAcceptActivity.class);
        requestaccept.putExtra("riderDetails", riderModel);
        requestaccept.putExtra("tripstatus", getResources().getString(R.string.confirm_arrived));
        requestaccept.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(requestaccept);
        finish();
    }


}
