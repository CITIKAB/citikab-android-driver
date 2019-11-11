package com.trioangle.goferdriver.signinsignup;
/**
 * @package com.trioangle.goferdriver.signinsignup
 * @subpackage signinsignup model
 * @category RegisterOTPActivity
 * @author Trioangle Product Team
 * @version 1.5
 */

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.Html;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.trioangle.goferdriver.R;
import com.trioangle.goferdriver.configs.SessionManager;
import com.trioangle.goferdriver.datamodel.ForgetPwdModel;
import com.trioangle.goferdriver.datamodel.LoginDetails;
import com.trioangle.goferdriver.helper.CustomDialog;
import com.trioangle.goferdriver.interfaces.ApiService;
import com.trioangle.goferdriver.interfaces.ServiceListener;
import com.trioangle.goferdriver.model.JsonResponse;
import com.trioangle.goferdriver.network.AppController;
import com.trioangle.goferdriver.util.CommonMethods;
import com.trioangle.goferdriver.util.RequestCallback;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Locale;

import javax.inject.Inject;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

import static com.trioangle.goferdriver.util.Enums.REQ_NUM_VALID;
import static com.trioangle.goferdriver.util.Enums.REQ_REG;

/* ************************************************************
                RegisterOTPActivity
Its used to get the register mobile number OTP detail function
*************************************************************** */
public class RegisterOTPActivity extends AppCompatActivity implements ServiceListener {


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

    public @InjectView(R.id.resend)
    TextView resend;
    public @InjectView(R.id.codetext)
    TextView codetext;
    public @InjectView(R.id.resend_timer)
    TextView resend_timer;
    public CountDownTimer countDownTimer;
    public String otp;
    public String phoneno;
    public boolean resetpassword;
    public boolean checkresend = false;
    public @InjectView(R.id.progressBar)
    ProgressBar progressBar;
    public @InjectView(R.id.backArrow)
    ImageView backArrow;
    public @InjectView(R.id.one)
    EditText one;
    public @InjectView(R.id.two)
    EditText two;
    public @InjectView(R.id.three)
    EditText three;
    public @InjectView(R.id.four)
    EditText four;
    protected boolean isInternetAvailable;
    /*
     *   Message broadcast receiver
     */
    private BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

        }
    };

    @OnClick(R.id.next)
    public void next() {
        reigsterOtp();
    }

    @OnClick(R.id.resend)
    public void resend() {
        isInternetAvailable = commonMethods.isOnline(this);
        checkresend = true;
        if (isInternetAvailable) {
            numberValidation();
        } else {
            commonMethods.showMessage(this, dialog, getResources().getString(R.string.Interneterror));
        }
    }

    @OnClick(R.id.back)
    public void back() {
        onBackPressed();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registerotp);

        ButterKnife.inject(this);
        AppController.getAppComponent().inject(this);
        dialog = commonMethods.getAlertDialog(this);

        isInternetAvailable = commonMethods.isOnline(this);
        Intent x = getIntent();
        otp = x.getStringExtra("otp");

        if(otp!=null){
            one.setText(otp.substring(0,1).toString());
            two.setText(otp.substring(1,2).toString());
            three.setText(otp.substring(2,3).toString());
            four.setText(otp.substring(3,4).toString());
        }


        resetpassword = x.getBooleanExtra("resetpassword", false);
        phoneno = sessionManager.getPhoneNumber();
        if (sessionManager.getisEdit()) {
            phoneno = x.getStringExtra("phone_number");
        }


        //TO set edit text select all
        one.setSelectAllOnFocus(true);
        two.setSelectAllOnFocus(true);
        three.setSelectAllOnFocus(true);
        four.setSelectAllOnFocus(true);

        //Setting the edit text cursor at Start
        int position = one.getSelectionStart();
        int position2 = two.getSelectionStart();
        int position3 = three.getSelectionStart();
        int position4 = four.getSelectionStart();
        one.setSelection(position);
        two.setSelection(position2);
        three.setSelection(position3);
        four.setSelection(position4);

        two.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if ((event.getAction() == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_DEL)) {

                    two.requestFocus();
                }
                if ((event.getAction() == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_DEL)
                        && two.getText().toString().length() == 0) {

                    one.requestFocus();
                    one.getText().clear();
                }
                return false;
            }
        });

        three.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if ((event.getAction() == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_DEL)) {

                    three.requestFocus();
                }
                if ((event.getAction() == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_DEL)
                        && three.getText().toString().length() == 0) {

                    two.requestFocus();
                    two.getText().clear();
                }
                return false;
            }
        });
        four.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if ((event.getAction() == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_DEL)) {
                    four.requestFocus();
                }
                if ((event.getAction() == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_DEL)
                        && four.getText().toString().length() == 0) {
                    three.requestFocus();
                    three.getText().clear();
                }
                return false;
            }
        });


        int strmi = 10;
        String str1 = getString(R.string.enter4digit) + " " + phoneno;
        int str = str1.length();
        strmi = phoneno.length();
        int start = str1.length() - strmi;

        /*
         *  Countdown for resent OTP button enable
         */
        countdown();

        final SpannableStringBuilder str3 = new SpannableStringBuilder(str1);
        str3.setSpan(new android.text.style.StyleSpan(android.graphics.Typeface.BOLD), start, str, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        codetext.setText(str3);

        /*
         *   Text watcher for OTP fields
         */
        one.addTextChangedListener(new TextWatcher() {

            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // TODO Auto-generated method stub
                if (one.getText().toString().length() == 1)     //size as per your requirement
                {
                    two.requestFocus();
                }
            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // TODO Auto-generated method stub

            }

            public void afterTextChanged(Editable s) {
                // TODO Auto-generated method stub
              /*  if (one.getText().toString().length()==1){
                    two.requestFocus();
                }*/

            }

        });

        two.addTextChangedListener(new TextWatcher() {

            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // TODO Auto-generated method stub
                if (two.getText().toString().length() == 1)     //size as per your requirement
                {
                    three.requestFocus();

                }
            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // TODO Auto-generated method stub

            }

            public void afterTextChanged(Editable s) {
                // TODO Auto-generated method stub

               /* if (two.getText().toString().length() == 0)     //size as per your requirement
                {
                    one.requestFocus();
                }*/
            }

        });

        three.addTextChangedListener(new TextWatcher() {

            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // TODO Auto-generated method stub
                if (three.getText().toString().length() == 1)     //size as per your requirement
                {
                    four.requestFocus();
                    //three.setBackgroundResource(R.drawable.d_buttomboardermobilenumber);
                }
            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // TODO Auto-generated method stub

            }

            public void afterTextChanged(Editable s) {
                // TODO Auto-generated method stub

               /* if (three.getText().toString().length() == 0)     //size as per your requirement
                {
                    two.requestFocus();
                }*/
            }

        });


        four.addTextChangedListener(new TextWatcher() {

            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // TODO Auto-generated method stub
                if (three.getText().toString().length() == 1)     //size as per your requirement
                {
                    four.requestFocus();
                    //four.setBackgroundResource(R.drawable.d_buttomboardermobilenumber);
                }
            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // TODO Auto-generated method stub

            }

            public void afterTextChanged(Editable s) {
                // TODO Auto-generated method stub

               /* if (four.getText().toString().length() == 0)     //size as per your requirement
                {
                    three.requestFocus();
                }*/
            }

        });
    }

    private void numberValidation() {

        isInternetAvailable = commonMethods.isOnline(this);
        checkresend = true;

        if (isInternetAvailable) {

            if (!resetpassword) {

                //apiService.numberValidation(sessionManager.getType(), sessionManager.getPhoneNumber(), sessionManager.getCountryCode(), "", sessionManager.getLanguageCode()).enqueue(new RequestCallback(REQ_NUM_VALID, this));
            } else {

                //apiService.numberValidation(sessionManager.getType(), sessionManager.getPhoneNumber(), sessionManager.getCountryCode(), "1", sessionManager.getLanguageCode()).enqueue(new RequestCallback(REQ_NUM_VALID, this));

            }
        } else {
            commonMethods.showMessage(this, dialog, getResources().getString(R.string.Interneterror));
        }


    }

    public void reigsterOtp() {

        isInternetAvailable = commonMethods.isOnline(this);
        String emtytextone = one.getText().toString().trim();
        String emtytexttwo = two.getText().toString().trim();
        String emtytextthree = three.getText().toString().trim();
        String emtytextfour = four.getText().toString().trim();

        if (emtytextone.isEmpty() || emtytexttwo.isEmpty() || emtytextthree.isEmpty() || emtytextfour.isEmpty()) {
            commonMethods.showMessage(this, dialog, getResources().getString(R.string.otp_emty));
        } else {

            String otpcode = one.getText().toString() + "" + two.getText().toString() + "" + three.getText().toString() + "" + four.getText().toString() + "";
            if (otp.equals(otpcode)) {
                if (sessionManager.getisEdit()) {
                    sessionManager.setPassword(getIntent().getStringExtra("phone_number"));

                    onBackPressed();
                } else {
                    if (!resetpassword) {
                        checkresend = false;

                        String firstnamestr = sessionManager.getFirstName();
                        String lastnamestr = sessionManager.getLastName();
                        String citystr = sessionManager.getCity();
                        String passwordstr = sessionManager.getPassword();


                        try {
                            firstnamestr = URLEncoder.encode(firstnamestr, "UTF-8");
                            lastnamestr = URLEncoder.encode(lastnamestr, "UTF-8");
                            passwordstr = URLEncoder.encode(passwordstr, "UTF-8");
                            citystr = URLEncoder.encode(citystr, "UTF-8");
                        } catch (UnsupportedEncodingException e) {
                            e.printStackTrace();
                        }

                        if (isInternetAvailable) {
                            commonMethods.showProgressDialog(this, customDialog);
                            apiService.registerOtp(sessionManager.getType(), sessionManager.getPhoneNumber(), sessionManager.getCountryCode(), sessionManager.getemail(), firstnamestr, lastnamestr, passwordstr, citystr, sessionManager.getDeviceId(), sessionManager.getDeviceType(),sessionManager.getLanguageCode()).enqueue(new RequestCallback(REQ_REG, this));
                        } else {
                            commonMethods.showMessage(this, dialog, getResources().getString(R.string.Interneterror));
                        }
                    } else {
                        // below code moved to MobileActivity, due to Facebook Kit implementation
                        /*Intent intent = new Intent(getApplicationContext(), ResetPassword.class);
                        startActivity(intent);
                        overridePendingTransition(R.anim.ub__slide_in_right, R.anim.ub__slide_out_left);*/
                    }
                }
            } else {

                commonMethods.showMessage(this, dialog, getResources().getString(R.string.otp_mismatch));
            }

        }


    }


    private void onSuccessForgetPwd(JsonResponse jsonResp) {

        progressBar.setVisibility(View.GONE);
        backArrow.setVisibility(View.VISIBLE);

        ForgetPwdModel forgetPwdModel = gson.fromJson(jsonResp.getStrResponse(), ForgetPwdModel.class);
        if (forgetPwdModel != null) {
            if (forgetPwdModel.getStatusCode().matches("1")) {
                progressBar.setVisibility(View.GONE);
                backArrow.setVisibility(View.VISIBLE);
                if (checkresend) {
                    countdown();
                    otp = forgetPwdModel.getOtp();


                    if(otp!=null){
                        one.setText(otp.substring(0,1).toString());
                        two.setText(otp.substring(1,2).toString());
                        three.setText(otp.substring(2,3).toString());
                        four.setText(otp.substring(3,4).toString());
                    }



                }
            } else {
                if (forgetPwdModel.getStatusMessage().equals("Message sending Failed,please try again..")) {
                    countdown();
                    otp = forgetPwdModel.getOtp();

                    if(otp!=null){
                        one.setText(otp.substring(0,1).toString());
                        two.setText(otp.substring(1,2).toString());
                        three.setText(otp.substring(2,3).toString());
                        four.setText(otp.substring(3,4).toString());
                    }




                } else {
                    commonMethods.showMessage(this, dialog, forgetPwdModel.getStatusMessage());
                }
                progressBar.setVisibility(View.GONE);
                backArrow.setVisibility(View.VISIBLE);
            }
        }

    }


    private void onSuccessRegisterPwd(JsonResponse jsonResp) {

        LoginDetails signInUpResultModel = gson.fromJson(jsonResp.getStrResponse(), LoginDetails.class);

        if (signInUpResultModel != null) {

            try {

                if (signInUpResultModel.getStatusCode().matches("1")) {
                    progressBar.setVisibility(View.GONE);
                    backArrow.setVisibility(View.VISIBLE);

                    String carDeailsModel = gson.toJson(signInUpResultModel.getCarDetailModel());

                    JSONArray cardetails = new JSONArray(carDeailsModel);

                    StringBuilder carType = new StringBuilder();
                    carType.append(getResources().getString(R.string.vehicle_type)).append(",");
                    for (int i = 0; i < cardetails.length(); i++) {
                        JSONObject cartype = cardetails.getJSONObject(i);
                        carType.append(cartype.getString("car_name")).append(",");
                    }

                    sessionManager.setCurrencySymbol(String.valueOf(Html.fromHtml(signInUpResultModel.getCurrencySymbol())));
                    sessionManager.setCurrencyCode(signInUpResultModel.getCurrencyCode());
                    sessionManager.setPaypalEmail(signInUpResultModel.getPayoutId());
                    sessionManager.setDriverSignupStatus(signInUpResultModel.getUserStatus());
                    sessionManager.setCarType(carType.toString());
                    sessionManager.setAcesssToken(signInUpResultModel.getToken());
                    sessionManager.setIsRegister(true);
                    sessionManager.setGoogleMapKey(signInUpResultModel.getGoogleMapKey());
                    commonMethods.hideProgressDialog();
                    Intent signin = new Intent(getApplicationContext(), RegisterCarDetailsActivity.class);
                    startActivity(signin);
                    overridePendingTransition(R.anim.ub__slide_in_right, R.anim.ub__slide_out_left);


                } else {
                    commonMethods.showMessage(this, dialog, signInUpResultModel.getStatusMessage());

                }
                progressBar.setVisibility(View.GONE);
                backArrow.setVisibility(View.VISIBLE);


            } catch (JSONException e) {
                e.printStackTrace();
            }

        }


    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.ub__slide_in_left, R.anim.ub__slide_out_right);
    }

    @Override
    public void onResume() {
        LocalBroadcastManager.getInstance(this).registerReceiver(receiver, new IntentFilter("otp"));
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(receiver);
    }
        /*
         *   Resend notification count down started
         */

    public void countdown() {
        resend.setEnabled(false);
        resend_timer.setVisibility(View.VISIBLE);
        countDownTimer = new CountDownTimer(30000, 1000) {
            public void onTick(long millisUntilFinished) {
                NumberFormat f = NumberFormat.getNumberInstance(Locale.US);
                DecimalFormat formatter = (DecimalFormat) f;
                formatter.applyPattern("00");
                resend_timer.setText("00:" + formatter.format(millisUntilFinished / 1000));
            }

            public void onFinish() {
                resend.setEnabled(true);
                resend_timer.setText("00:00");
                resend_timer.setVisibility(View.INVISIBLE);
                //resend_timer.setText("done!");
            }
        }.start();
    }


    @Override
    public void onSuccess(JsonResponse jsonResp, String data) {
        commonMethods.hideProgressDialog();
        if (!jsonResp.isOnline()) {
            if (!TextUtils.isEmpty(data)) commonMethods.showMessage(this, dialog, data);
            return;
        }


        switch (jsonResp.getRequestCode()) {

            case REQ_REG:
                if (jsonResp.isSuccess()) {

                    onSuccessRegisterPwd(jsonResp);

                } else if (!TextUtils.isEmpty(jsonResp.getStatusMsg())) {

                    commonMethods.showMessage(this, dialog, jsonResp.getStatusMsg());

                }
                break;
            case REQ_NUM_VALID:
                if (jsonResp.isSuccess()) {

                    onSuccessForgetPwd(jsonResp);

                } else if (!TextUtils.isEmpty(jsonResp.getStatusMsg())) {

                    onSuccessForgetPwd(jsonResp);

                }
                break;
            default:
                break;
        }


    }

    @Override
    public void onFailure(JsonResponse jsonResp, String data) {
        progressBar.setVisibility(View.GONE);
        backArrow.setVisibility(View.VISIBLE);
    }


}