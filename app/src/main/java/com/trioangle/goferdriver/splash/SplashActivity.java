package com.trioangle.goferdriver.splash;
/**
 * @package com.trioangle.goferdriver.splash
 * @subpackage Splash
 * @category GetDriverTripDetails
 * @author Trioangle Product Team
 * @version 1.5
 */


import android.app.ActivityOptions;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.StrictMode;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.view.ContextThemeWrapper;
import android.text.TextUtils;
import android.view.View;

import com.trioangle.goferdriver.MainActivity;
import com.trioangle.goferdriver.R;
import com.trioangle.goferdriver.configs.SessionManager;
import com.trioangle.goferdriver.interfaces.ApiService;
import com.trioangle.goferdriver.interfaces.ServiceListener;
import com.trioangle.goferdriver.model.JsonResponse;
import com.trioangle.goferdriver.network.AppController;
import com.trioangle.goferdriver.signinsignup.DocHomeActivity;
import com.trioangle.goferdriver.signinsignup.RegisterCarDetailsActivity;
import com.trioangle.goferdriver.signinsignup.SigninSignupHomeActivity;
import com.trioangle.goferdriver.util.CommonKeys;
import com.trioangle.goferdriver.util.CommonMethods;
import com.trioangle.goferdriver.util.RequestCallback;

import net.hockeyapp.android.UpdateManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Locale;

import javax.inject.Inject;

import butterknife.ButterKnife;
import butterknife.InjectView;

/* ************************************************************
                splash
Its used to start animation screen of splash page
*************************************************************** */
public class SplashActivity extends AppCompatActivity implements ServiceListener {

    // Splash screen timer
    private static int SPLASH_TIME_OUT = 2000;
    public @Inject
    SessionManager sessionManager;

    public @Inject
    ApiService apiService;

    public @Inject
    CommonMethods commonMethods;

    public AlertDialog dialog;



    private String userid;
    private String driverStatus;

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ButterKnife.inject(this);
        AppController.getAppComponent().inject(this);

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        dialog = commonMethods.getAlertDialog(this);

        userid = sessionManager.getAccessToken();
        driverStatus = sessionManager.getDriverSignupStatus();
        sessionManager.setDeviceType("2");
        sessionManager.setType("driver");

        // Set Locale Language
        setLocale();
        callForceUpdateAPI();


            /*
             * Showing splash screen with a timer. This will be useful when you
             * want to show case your app logo / company
             */



    }

    private void callForceUpdateAPI() {

        if (!commonMethods.isOnline(getApplicationContext())) {
            commonMethods.showMessage(this, dialog, getResources().getString(R.string.no_connection));
        } else {
            apiService.checkVersion(CommonMethods.getAppVersionNameFromGradle(this), sessionManager.getType(),CommonKeys.DeviceTypeAndroid).enqueue(new RequestCallback(this));
        }
    }

    public void setLocale() {
        String lang = sessionManager.getLanguage();

        if (!lang.equals("")) {
            String langC = sessionManager.getLanguageCode();
            Locale locale = new Locale(langC);
            Locale.setDefault(locale);
            Configuration config = new Configuration();
            config.locale = locale;
            SplashActivity.this.getResources().updateConfiguration(config, SplashActivity.this.getResources().getDisplayMetrics());
        } else {
            sessionManager.setLanguage("English");
            sessionManager.setLanguageCode("en");
        }


    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        unregisterManagers();
    }
    private void startMainActivity() {
        Intent x = new Intent(getApplicationContext(), MainActivity.class);
        x.putExtra("signinup", true);
        Bundle bndlanimation = ActivityOptions.makeCustomAnimation(getApplicationContext(), R.anim.cb_fade_in, R.anim.cb_face_out).toBundle();
        startActivity(x, bndlanimation);
        finish();
    }

    private void unregisterManagers() {
        UpdateManager.unregister();
    }


    @Override
    public void onSuccess(JsonResponse jsonResp, String data) {
        if (jsonResp.isSuccess()) {
            try {
                JSONObject response = new JSONObject(jsonResp.getStrResponse());
                if (response.has("force_update")) {
                    Boolean foreceUpdate = response.getBoolean("force_update");
                    if(!foreceUpdate){
                        moveToNextScreen();
                    }else{
                        showSettingsAlert();
                    }
                }
            } catch (JSONException j) {
                j.printStackTrace();
            }

        } else if (!TextUtils.isEmpty(jsonResp.getStatusMsg())) {
            commonMethods.showMessage(this, dialog, jsonResp.getStatusMsg());
        }
    }

    @Override
    public void onFailure(JsonResponse jsonResp, String data) {

    }

    public void showSettingsAlert() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(
                new ContextThemeWrapper(this, R.style.AlertDialogCustom));
        alertDialog.setCancelable(false);
        //alertDialog.setTitle("Update");
        alertDialog.setMessage("Please update our app to enjoy the latest features!");
        alertDialog.setPositiveButton("Visit play store",
                (dialog, which) -> {
                    CommonMethods.openPlayStore(this);
                    this.finish();
                });
        alertDialog.show();
    }

    public void moveToNextScreen(){
        if (userid != null) {

            // Driver status is car_details then automatically redirect to Driver credentials page
            if (driverStatus != null && driverStatus.equals("Car_details")) {
                Intent x = new Intent(getApplicationContext(), RegisterCarDetailsActivity.class);
                Bundle bndlanimation = ActivityOptions.makeCustomAnimation(getApplicationContext(), R.anim.cb_fade_in, R.anim.cb_face_out).toBundle();
                startActivity(x, bndlanimation);
                finish();
            } else if (driverStatus != null && driverStatus.equals("Document_details")) {
                Intent x = new Intent(getApplicationContext(), DocHomeActivity.class);
                Bundle bndlanimation = ActivityOptions.makeCustomAnimation(getApplicationContext(), R.anim.cb_fade_in, R.anim.cb_face_out).toBundle();
                startActivity(x, bndlanimation);
                finish();
            } else if (driverStatus != null && driverStatus.equals("Pending")) {
                // Driver status is pending then automatically redirect to paypal email page or home page
                        /*if (sessionManager.getPaypalEmail().length() > 0) {
                            Intent x = new Intent(getApplicationContext(), MainActivity.class);
                            Bundle bndlanimation = ActivityOptions.makeCustomAnimation(getApplicationContext(), R.anim.cb_fade_in, R.anim.cb_face_out).toBundle();
                            startActivity(x, bndlanimation);
                            finish();
                        } else {
                            Intent signin = new Intent(getApplicationContext(), PaymentPage.class);
                            startActivity(signin);
                            overridePendingTransition(R.anim.ub__slide_in_right, R.anim.ub__slide_out_left);
                        }*/
                Intent x = new Intent(getApplicationContext(), MainActivity.class);
                Bundle bndlanimation = ActivityOptions.makeCustomAnimation(getApplicationContext(), R.anim.cb_fade_in, R.anim.cb_face_out).toBundle();
                startActivity(x, bndlanimation);
                finish();

            } else if (driverStatus != null && driverStatus.equals("Active")) {
                // commented the below line, because we removed add paypal details initially, it will be continued through settings page

                        /*// Driver status is car_details then automatically redirect to Driver paypal page or home page
                        if (sessionManager.getPaypalEmail().length() > 0) {
                            Intent x = new Intent(getApplicationContext(), MainActivity.class);
                            x.putExtra("signinup", true);
                            Bundle bndlanimation = ActivityOptions.makeCustomAnimation(getApplicationContext(), R.anim.cb_fade_in, R.anim.cb_face_out).toBundle();
                            startActivity(x, bndlanimation);
                            finish();
                        } else {
                            Intent signin = new Intent(getApplicationContext(), PaymentPage.class);
                            startActivity(signin);
                            overridePendingTransition(R.anim.ub__slide_in_right, R.anim.ub__slide_out_left);
                        }*/
                // hence forth, we moved directly to main page
                startMainActivity();

            } else {
                // Driver status is car_details then automatically redirect to Driver signin signup home page
                Intent x = new Intent(getApplicationContext(), SigninSignupHomeActivity.class);
                Bundle bndlanimation = ActivityOptions.makeCustomAnimation(getApplicationContext(), R.anim.cb_fade_in, R.anim.cb_face_out).toBundle();
                startActivity(x, bndlanimation);
                finish();
            }
        } else {
            // Driver status is car_details then automatically redirect to Driver signin signup home page
            Intent x = new Intent(getApplicationContext(), SigninSignupHomeActivity.class);
            Bundle bndlanimation = ActivityOptions.makeCustomAnimation(getApplicationContext(), R.anim.cb_fade_in, R.anim.cb_face_out).toBundle();
            startActivity(x, bndlanimation);
            finish();
        }
    }
}
