package com.trioangle.goferdriver.facebookAccountKit;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;


import com.facebook.accountkit.Account;
import com.facebook.accountkit.AccountKit;
import com.facebook.accountkit.AccountKitCallback;
import com.facebook.accountkit.AccountKitError;
import com.facebook.accountkit.AccountKitLoginResult;
import com.facebook.accountkit.PhoneNumber;
import com.facebook.accountkit.ui.AccountKitActivity;
import com.facebook.accountkit.ui.AccountKitConfiguration;
import com.facebook.accountkit.ui.LoginType;
import com.google.gson.Gson;
import com.trioangle.goferdriver.R;
import com.trioangle.goferdriver.configs.SessionManager;
import com.trioangle.goferdriver.datamodel.NumberValidationModel;
import com.trioangle.goferdriver.helper.CustomDialog;
import com.trioangle.goferdriver.interfaces.ApiService;
import com.trioangle.goferdriver.interfaces.ServiceListener;
import com.trioangle.goferdriver.model.JsonResponse;
import com.trioangle.goferdriver.network.AppController;
import com.trioangle.goferdriver.util.CommonKeys;
import com.trioangle.goferdriver.util.CommonMethods;
import com.trioangle.goferdriver.util.RequestCallback;

import javax.inject.Inject;

import static com.trioangle.goferdriver.util.CommonKeys.FACEBOOK_ACCOUNT_KIT_PHONE_NUMBER_COUNTRY_CODE_KEY;
import static com.trioangle.goferdriver.util.CommonKeys.FACEBOOK_ACCOUNT_KIT_PHONE_NUMBER_KEY;
import static com.trioangle.goferdriver.util.CommonMethods.DebuggableLogD;
import static com.trioangle.goferdriver.util.CommonMethods.DebuggableLogI;


import android.content.DialogInterface;

import android.os.CountDownTimer;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.CycleInterpolator;
import android.view.animation.TranslateAnimation;
import android.widget.*;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

import com.hbb20.CountryCodePicker;
import java.util.Locale;


public class FacebookAccountKitActivity extends AppCompatActivity implements ServiceListener {

    public @Inject
    SessionManager sessionManager;

    public @Inject
    CommonMethods commonMethods;

    public @Inject
    ApiService apiService;

    public @Inject
    Gson gson;

    public @Inject
    CustomDialog customDialog;

    boolean isPhoneNumberLayoutIsVisible = true;

    @InjectView(R.id.tv_mobile_heading)
    TextView mobileNumberHeading;

    @InjectView(R.id.tv_otp_resend_label)
    TextView tvResendOTPLabel;

    @InjectView(R.id.tv_otp_resend_countdown)
    TextView tvResendOTPCountdown;

    @InjectView(R.id.tv_resend_button)
    TextView tvResendOTP;

    @InjectView(R.id.tv_otp_error_field)
    TextView tvOTPErrorMessage;

    @InjectView(R.id.cl_phone_number_input)
    ConstraintLayout ctlPhoneNumber;

    @InjectView(R.id.cl_otp_input)
    ConstraintLayout ctlOTP;

    @InjectView(R.id.pb_number_verification)
    ProgressBar pbNumberVerification;

    @InjectView(R.id.imgv_next)
    ImageView imgvArrow;

    @InjectView(R.id.rl_edittexts)
    RelativeLayout rlEdittexts;

    @InjectView(R.id.one)
    EditText edtxOne;
    @InjectView(R.id.two)
    EditText edtxTwo;
    @InjectView(R.id.three)
    EditText edtxThree;
    @InjectView(R.id.four)
    EditText edtxFour;

    @InjectView(R.id.phone)
    EditText edtxPhoneNumber;

    public @InjectView(R.id.ccp)
    CountryCodePicker ccp;

    @InjectView(R.id.fab_verify)
    CardView cvNext;

    private int isForForgotPassword = 0;
    private String otp="";
    private String receivedOTPFromServer;
    private long resendOTPWaitingSecond = 120000;
    private CountDownTimer resentCountdownTimer, backPressCounter;
    private boolean isDeletable = true;

    @OnClick(R.id.fab_verify)
    public void startAnimationd() {
        //startAnimation();
        if (isPhoneNumberLayoutIsVisible && edtxPhoneNumber.getText().toString().length() > 5) {
            callSendOTPAPI();
        } else if (!isPhoneNumberLayoutIsVisible) {

            verifyOTP();
        }
        /*showOTPfield();
        showOTPMismatchIssue();*/

    }

    @OnClick(R.id.tv_resend_button)
    public void resendOTP() {
        callSendOTPAPI();
        //runCountdownTimer();

    }

    private void verifyOTP() {
        if (!otp.equalsIgnoreCase("")) {
            if (otp.equalsIgnoreCase(receivedOTPFromServer)) {

                //CommonMethods.showUserMessage("Success");
                Intent returnIntent = new Intent();
                returnIntent.putExtra(FACEBOOK_ACCOUNT_KIT_PHONE_NUMBER_KEY, edtxPhoneNumber.getText().toString().trim());
                returnIntent.putExtra(FACEBOOK_ACCOUNT_KIT_PHONE_NUMBER_COUNTRY_CODE_KEY, ccp.getSelectedCountryCodeWithPlus().replaceAll("\\+", ""));
                //returnIntent.putExtra(FACEBOOK_ACCOUNT_KIT_MESSAGE_KEY, numberValidationModel.getStatusMessage());
                setResult(RESULT_OK, returnIntent);
                finish();
            } else {
                showOTPMismatchIssue();
            }
        }
    }

    private void shakeEdittexts() {
        TranslateAnimation shake = new TranslateAnimation(0, 20, 0, 0);
        shake.setDuration(500);
        shake.setInterpolator(new CycleInterpolator(3));
        rlEdittexts.startAnimation(shake);

    }

    public void showOTPMismatchIssue() {
        shakeEdittexts();
        tvOTPErrorMessage.setVisibility(View.VISIBLE);
    }

    public void runCountdownTimer() {
        tvResendOTP.setVisibility(View.GONE);
        tvResendOTPCountdown.setVisibility(View.VISIBLE);
        tvResendOTPLabel.setText(getResources().getString(R.string.send_OTP_again_in));
        if (resentCountdownTimer != null) {
            resentCountdownTimer.cancel();
        }
        resentCountdownTimer = new CountDownTimer(resendOTPWaitingSecond, 1000) {

            public void onTick(long millisUntilFinished) {
                tvResendOTPCountdown.setText(String.valueOf(millisUntilFinished / 1000));
            }

            public void onFinish() {
                tvResendOTPCountdown.setVisibility(View.GONE);
                tvResendOTPLabel.setText(getResources().getString(R.string.resend_otp));
                tvResendOTP.setVisibility(View.VISIBLE);
            }
        }.start();
    }
    @OnClick(R.id.tv_back_otp_arrow)
    public void showPhoneNumberField() {
        cvNext.setCardBackgroundColor(getResources().getColor(R.color.light_blue_button_color));
        cvNext.setEnabled(true);
        ctlPhoneNumber.setVisibility(View.VISIBLE);
        ctlOTP.setVisibility(View.GONE);
        isPhoneNumberLayoutIsVisible = true;
        tvResendOTP.setVisibility(View.GONE);
        tvResendOTPLabel.setVisibility(View.GONE);
        tvResendOTPCountdown.setVisibility(View.GONE);
        resentCountdownTimer.cancel();
    }

    @InjectView(R.id.tv_back_phone_arrow)
    TextView tvPhoneBack;

    @InjectView(R.id.tv_back_otp_arrow)
    TextView tvOTPback;

    @OnClick(R.id.tv_back_phone_arrow)
    public void finishThisActivity() {
        super.onBackPressed();
    }

    public void showOTPfield() {
        ctlPhoneNumber.setVisibility(View.GONE);
        ctlOTP.setVisibility(View.VISIBLE);
        isPhoneNumberLayoutIsVisible = false;

        runCountdownTimer();

        tvResendOTPLabel.setVisibility(View.VISIBLE);

        //fillDemoOTP();
    }



    public AlertDialog dialog;

    public final int FACEBOOK_ACCOUNTKIT_REQUEST_CODE = 157;

    String facebookVerifiedPhoneNumber, facebookVerifiedCountryCode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mobile_number_verification);
        ButterKnife.inject(this);
        AppController.getAppComponent().inject(this);
        initViews();
        initOTPTextviewListener();
        //startCallingFacebookKit();
    }

    private void initViews() {
        getIntentValues();
        if(otp.equalsIgnoreCase(""))
        {
            otp = "";
            cvNext.setCardBackgroundColor(getResources().getColor(R.color.quantum_grey400));

        }
        cvNext.setEnabled(true);
        ccp.setAutoDetectedCountry(true);
        if (Locale.getDefault().getLanguage().equals("fa") || Locale.getDefault().getLanguage().equals("ar")) {
            ccp.changeDefaultLanguage(CountryCodePicker.Language.ARABIC);
        }
        edtxPhoneNumber.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (edtxPhoneNumber.getText().toString().length() > 5) {
                    cvNext.setCardBackgroundColor(getResources().getColor(R.color.light_blue_button_color));
                } else {
                    cvNext.setCardBackgroundColor(getResources().getColor(R.color.quantum_grey400));
                }
            }
        });

        initDirectionChanges();
    }

    private void initDirectionChanges() {
        String laydir = getResources().getString(R.string.layout_direction);

        if ("1".equals(laydir)) {
            cvNext.setRotation(180);
            tvPhoneBack.setRotation(180);
            tvOTPback.setRotation(180);

        }
    }

    private void getIntentValues() {
        try {
            isForForgotPassword = getIntent().getIntExtra("usableType", 0);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void initOTPTextviewListener() {
        edtxOne.addTextChangedListener(new OtpTextWatcher());
        edtxTwo.addTextChangedListener(new OtpTextWatcher());
        edtxThree.addTextChangedListener(new OtpTextWatcher());
        edtxFour.addTextChangedListener(new OtpTextWatcher());

        edtxOne.setOnKeyListener(new OtpTextBackWatcher());
        edtxTwo.setOnKeyListener(new OtpTextBackWatcher());
        edtxThree.setOnKeyListener(new OtpTextBackWatcher());
        edtxFour.setOnKeyListener(new OtpTextBackWatcher());


    }

    private class OtpTextWatcher implements TextWatcher {


        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            DebuggableLogI("Gofer", "Textchange");
            if (edtxOne.isFocused()) {
                if (edtxOne.getText().toString().length() > 0)     //size as per your requirement
                {
                    edtxTwo.requestFocus();
                    edtxTwo.setSelectAllOnFocus(true);
                    //one.setBackgroundResource(R.drawable.d_buttomboardermobilenumber);
                }
            } else if (edtxTwo.isFocused()) {
                if (edtxTwo.getText().toString().length() > 0)     //size as per your requirement
                {
                    edtxThree.requestFocus();
                    edtxThree.setSelectAllOnFocus(true);
                    //two.setBackgroundResource(R.drawable.d_buttomboardermobilenumber);
                } else {
                    edtxOne.requestFocus();
                    edtxOne.setSelectAllOnFocus(true);
                    // edtxOne.setSelection(1);
                }
            } else if (edtxThree.isFocused()) {
                if (edtxThree.getText().toString().length() > 0)     //size as per your requirement
                {
                    edtxFour.requestFocus();
                    edtxFour.setSelectAllOnFocus(true);
                    //three.setBackgroundResource(R.drawable.d_buttomboardermobilenumber);
                } else {
                    edtxTwo.requestFocus();
                    edtxTwo.setSelectAllOnFocus(true);
                    //edtxTwo.setSelection(1);
                }
            } else if (edtxFour.isFocused()) {
                if (edtxFour.getText().toString().length() == 0) {
                    edtxThree.requestFocus();
                }
            }

            if (edtxOne.getText().toString().trim().length() > 0 && edtxTwo.getText().toString().trim().length() > 0 && edtxThree.getText().toString().trim().length() > 0 && edtxFour.getText().toString().trim().length() > 0) {
                otp = edtxOne.getText().toString().trim() + edtxTwo.getText().toString().trim() + edtxThree.getText().toString().trim() + edtxFour.getText().toString().trim();
                cvNext.setCardBackgroundColor(getResources().getColor(R.color.light_blue_button_color));
                cvNext.setEnabled(true);
            } else {
                otp = "";
                cvNext.setCardBackgroundColor(getResources().getColor(R.color.quantum_grey400));
                cvNext.setEnabled(false);
            }
            tvOTPErrorMessage.setVisibility(View.GONE);
        }

        public void afterTextChanged(Editable editable) {
            DebuggableLogI("Gofer", "Textchange");

        }
    }

    private class OtpTextBackWatcher implements View.OnKeyListener {

        @Override
        public boolean onKey(View v, int keyCode, KeyEvent event) {
            DebuggableLogD("keycode", keyCode + "");
            DebuggableLogD("keyEvent", event.toString());
            if (keyCode == KeyEvent.KEYCODE_DEL && isDeletable) {
                switch (v.getId()) {
                    case R.id.one: {
                        edtxOne.getText().clear();
                        break;
                    }
                    case R.id.two: {
                        edtxTwo.getText().clear();
                        edtxOne.requestFocus();
                        edtxOne.setSelectAllOnFocus(true);
                        break;
                    }
                    case R.id.three: {
                        edtxThree.getText().clear();
                        edtxTwo.requestFocus();
                        edtxTwo.setSelectAllOnFocus(true);
                        break;
                    }
                    case R.id.four: {
                        edtxFour.getText().clear();
                        edtxThree.requestFocus();
                        edtxThree.setSelectAllOnFocus(true);
                        //edtxThree.setSelection(1);
                        break;
                    }

                }
                countdownTimerForOTPBackpress();
                return true;
            }else{
                return false;
            }
        }
    }

    public void countdownTimerForOTPBackpress(){
        isDeletable =false;
        if(backPressCounter != null)backPressCounter.cancel();
        backPressCounter = new CountDownTimer(100, 1000) {

            public void onTick(long millisUntilFinished) {
                //tvResendOTPCountdown.setText(String.valueOf(millisUntilFinished / 1000));
            }

            public void onFinish() {
                isDeletable = true;
            }
        }.start();
    }


    public void startAnimation() {
       /* View redLayout = findViewById(R.id.tv_mobile_heading);
        ViewGroup parent = findViewById(R.id.cl_phone_number_input);

        Transition transition = new Slide(Gravity.START);
        transition.setDuration(600);
        transition.addTarget(R.id.tv_mobile_heading);

        TransitionManager.beginDelayedTransition(parent, transition);
        redLayout.setVisibility(false ? View.VISIBLE : View.GONE);*/

        Animation RightSwipe = AnimationUtils.loadAnimation(this, R.anim.ub__slide_out_left);
        mobileNumberHeading.startAnimation(RightSwipe);
        RightSwipe.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                mobileNumberHeading.setVisibility(View.GONE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
    }

    public void callSendOTPAPI() {
        showProgressBarAndHideArrow(true);
        apiService.numbervalidation(edtxPhoneNumber.getText().toString(), ccp.getSelectedCountryCodeWithPlus().replaceAll("\\+", ""), sessionManager.getType(), sessionManager.getLanguageCode(), String.valueOf(isForForgotPassword)).enqueue(new RequestCallback(this));
    }

    public void showProgressBarAndHideArrow(Boolean status) {
        if (status) {
            pbNumberVerification.setVisibility(View.VISIBLE);
            imgvArrow.setVisibility(View.GONE);
        } else {
            pbNumberVerification.setVisibility(View.GONE);
            imgvArrow.setVisibility(View.VISIBLE);
        }
    }
    public static void openFacebookAccountKitActivity(Activity activity) {
        Intent facebookIntent = new Intent(activity, FacebookAccountKitActivity.class);
        facebookIntent.putExtra("usableType", 0);
        activity.startActivityForResult(facebookIntent, CommonKeys.ACTIVITY_REQUEST_CODE_START_FACEBOOK_ACCOUNT_KIT);
    }

    public static void openFacebookAccountKitActivity(Activity activity, int type) {
        Intent facebookIntent = new Intent(activity, FacebookAccountKitActivity.class);
        facebookIntent.putExtra("usableType", type);
        activity.startActivityForResult(facebookIntent, CommonKeys.ACTIVITY_REQUEST_CODE_START_FACEBOOK_ACCOUNT_KIT);
    }


    private void startCallingFacebookKit() {
        final Intent intent = new Intent(this, AccountKitActivity.class);
        AccountKitConfiguration.AccountKitConfigurationBuilder configurationBuilder =
                new AccountKitConfiguration.AccountKitConfigurationBuilder(LoginType.PHONE, AccountKitActivity.ResponseType.TOKEN);
        // PhoneNumber phoneNumber = new PhoneNumber(sessionManager.getTemporaryCountryCode(), sessionManager.getTemporaryPhonenumber());
        intent.putExtra(AccountKitActivity.ACCOUNT_KIT_ACTIVITY_CONFIGURATION, configurationBuilder.build());
        startActivityForResult(intent, FACEBOOK_ACCOUNTKIT_REQUEST_CODE);
        overridePendingTransition(R.anim.ub__slide_in_right, R.anim.ub__slide_out_left);
    }

    @Override
    protected void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == FACEBOOK_ACCOUNTKIT_REQUEST_CODE) { // confirm that this response matches your request
            AccountKitLoginResult loginResult = data.getParcelableExtra(AccountKitLoginResult.RESULT_KEY);
            if (loginResult.getError() != null || loginResult.wasCancelled()) {
                //showErrorMessageAndCloseActivity();
                finish();

            } else {
                getPhoneNumber();
            }
        }
    }

    public void getPhoneNumber() {
        AccountKit.getCurrentAccount(new AccountKitCallback<Account>() {
            @Override
            public void onSuccess(final Account account) {
                String phoneNumbers, countryCode, phoneNumberWihtoutPlusSign, temporaryPhoneNumber;

                // Get phone number
                PhoneNumber phoneNumber = account.getPhoneNumber();
                phoneNumbers = phoneNumber.getPhoneNumber().toString();
                phoneNumberWihtoutPlusSign = phoneNumbers.replace("+", "");
                countryCode = phoneNumber.getCountryCode();
                callPhoneNumberValidationAPI(phoneNumberWihtoutPlusSign, countryCode);
            }

            @Override
            public void onError(final AccountKitError error) {
                showErrorMessageAndCloseActivity();

                // Handle Error
            }
        });
    }

    /*public void phoneNumberChangedErrorMessage() {
        //commonMethods.showMessage(this, dialog, getString(R.string.InvalidMobileNumber));
        Toast.makeText(this, getString(R.string.InvalidMobileNumber), Toast.LENGTH_SHORT).show();
    }
*/

    /*void facebookAccountKitNumberVerificationSuccess() {
        setResult(ApiSessionAppConstants.FACEBOOK_ACCOUNT_KIT_VERIFACATION_SUCCESS);
        finish();
    }

    void facebookAccountKitNumberVerificationFailure() {
        setResult(ApiSessionAppConstants.FACEBOOK_ACCOUNT_KIT_VERIFACATION_FAILURE);
        finish();
    }*/

    // api call
    void callPhoneNumberValidationAPI(String facebookVerifiedPhoneNumber, String facebookVerifiedCountryCode) {
        this.facebookVerifiedCountryCode = facebookVerifiedCountryCode;
        this.facebookVerifiedPhoneNumber = facebookVerifiedPhoneNumber;
        commonMethods.showProgressDialog(this, customDialog);
        apiService.numbervalidation(facebookVerifiedPhoneNumber, facebookVerifiedCountryCode, sessionManager.getType(), sessionManager.getLanguageCode(), "").enqueue(new RequestCallback(this));
    }
    @Override
    public void onSuccess(JsonResponse jsonResp, String data) {
        showProgressBarAndHideArrow(false);

        cvNext.setCardBackgroundColor(getResources().getColor(R.color.quantum_grey400));

        /*Intent returnIntent = new Intent();
        returnIntent.putExtra(FACEBOOK_ACCOUNT_KIT_PHONE_NUMBER_KEY, facebookVerifiedPhoneNumber);
        returnIntent.putExtra(FACEBOOK_ACCOUNT_KIT_PHONE_NUMBER_COUNTRY_CODE_KEY, facebookVerifiedCountryCode);

        NumberValidationModel numberValidationModel = gson.fromJson(jsonResp.getStrResponse(), NumberValidationModel.class);

        if (numberValidationModel.getStatusCode().equals(CommonKeys.NUMBER_VALIDATION_API_RESULT_OLD_USER)) {

            returnIntent.putExtra(FACEBOOK_ACCOUNT_KIT_MESSAGE_KEY, numberValidationModel.getStatusMessage());
            setResult(CommonKeys.FACEBOOK_ACCOUNT_KIT_RESULT_OLD_USER, returnIntent);
            finish();

        } else if (numberValidationModel.getStatusCode().equals(CommonKeys.NUMBER_VALIDATION_API_RESULT_NEW_USER)) {

            returnIntent.putExtra(FACEBOOK_ACCOUNT_KIT_MESSAGE_KEY, numberValidationModel.getStatusMessage());
            setResult(CommonKeys.FACEBOOK_ACCOUNT_KIT_RESULT_NEW_USER, returnIntent);
            finish();
        } else {
            CommonMethods.DebuggableLogI(numberValidationModel.getStatusCode(), numberValidationModel.getStatusMessage());
        }*/
        //NumberValidationModel numberValidationModel = gson.fromJson(jsonResp.getStrResponse(), NumberValidationModel.class);
/*

        if (numberValidationModel.getStatusCode().equals(CommonKeys.NUMBER_VALIDATION_API_RESULT_OLD_USER)) {

        } else if (numberValidationModel.getStatusCode().equals(CommonKeys.NUMBER_VALIDATION_API_RESULT_NEW_USER)) {
        }
*/

        if (jsonResp.isSuccess()) {
            cvNext.setEnabled(false);
            receivedOTPFromServer = (String) commonMethods.getJsonValue(jsonResp.getStrResponse(), "otp", String.class);

            showOTPfield();

        } else if (!TextUtils.isEmpty(jsonResp.getStatusMsg())) {
            commonMethods.hideProgressDialog();
            commonMethods.showMessage(this, dialog, jsonResp.getStatusMsg());
            showSettingsAlert(jsonResp.getStatusMsg());
        }
    }

    public void showSettingsAlert(String statusMsg) {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(
                this);
        //alertDialog.setTitle(statusMsg);
        alertDialog.setMessage(statusMsg);
        alertDialog.setCancelable(false);
        alertDialog.setPositiveButton(getResources().getString(R.string.ok),
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }
                });

        alertDialog.show();
    }


    @Override
    public void onFailure(JsonResponse jsonResp, String data) {
        commonMethods.hideProgressDialog();
        showErrorMessageAndCloseActivity();
    }

    private void showErrorMessageAndCloseActivity() {
        CommonMethods.showServerInternalErrorMessage(this);
        finish();
    }

    @Override
    public void onBackPressed() {

        if (isPhoneNumberLayoutIsVisible) {
            super.onBackPressed();
        } else {
            cvNext.setCardBackgroundColor(getResources().getColor(R.color.light_blue_button_color));
            cvNext.setEnabled(true);
            showPhoneNumberField();

        }
    }
}

