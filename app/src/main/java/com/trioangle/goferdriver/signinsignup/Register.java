package com.trioangle.goferdriver.signinsignup;
/**
 * @package com.trioangle.goferdriver.signinsignup
 * @subpackage signinsignup model
 * @category Register
 * @author Trioangle Product Team
 * @version 1.5
 **/

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.TextInputLayout;
import android.support.v13.view.ViewCompat;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.Html;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.gson.Gson;
import com.hbb20.CountryCodePicker;
import com.trioangle.goferdriver.R;
import com.trioangle.goferdriver.configs.SessionManager;
import com.trioangle.goferdriver.datamodel.ForgetPwdModel;
import com.trioangle.goferdriver.datamodel.LoginDetails;
import com.trioangle.goferdriver.facebookAccountKit.FacebookAccountKitActivity;
import com.trioangle.goferdriver.helper.Constants;
import com.trioangle.goferdriver.helper.CustomDialog;
import com.trioangle.goferdriver.interfaces.ApiService;
import com.trioangle.goferdriver.interfaces.ServiceListener;
import com.trioangle.goferdriver.model.JsonResponse;
import com.trioangle.goferdriver.network.AppController;
import com.trioangle.goferdriver.placesearch.PlacesAutoCompleteAdapter;
import com.trioangle.goferdriver.placesearch.RecyclerItemClickListener;
import com.trioangle.goferdriver.util.ApiSessionAppConstants;
import com.trioangle.goferdriver.util.CommonKeys;
import com.trioangle.goferdriver.util.CommonMethods;
import com.trioangle.goferdriver.util.RequestCallback;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Locale;

import javax.inject.Inject;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

import static com.trioangle.goferdriver.util.CommonKeys.FACEBOOK_ACCOUNT_KIT_PHONE_NUMBER_COUNTRY_CODE_KEY;
import static com.trioangle.goferdriver.util.CommonKeys.FACEBOOK_ACCOUNT_KIT_PHONE_NUMBER_KEY;
import static com.trioangle.goferdriver.util.Enums.REQ_REG;
import static com.trioangle.goferdriver.util.Enums.REQ_VALIDATE_NUMBER;

/* ************************************************************
                Register
Its used to get the driver register detail function
*************************************************************** */
public class Register extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, ServiceListener {

    private static final LatLngBounds BOUNDS_INDIA = new LatLngBounds(
            new LatLng(-0, 0), new LatLng(0, 0));
    private static String TAG = "Register";
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
    public @InjectView(R.id.input_first)
    EditText input_first;
    public @InjectView(R.id.input_last)
    EditText input_last;
    public @InjectView(R.id.emaitext)
    EditText emaitext;
    public @InjectView(R.id.passwordtext)
    EditText passwordtext;
    public @InjectView(R.id.cityText)
    EditText cityText;
    public @InjectView(R.id.mobile_number)
    EditText mobile_number;
    public @InjectView(R.id.input_layout_first)
    TextInputLayout input_layout_first;
    public @InjectView(R.id.input_layout_last)
    TextInputLayout input_layout_last;
    public @InjectView(R.id.emailName)
    TextInputLayout emailName;
    public @InjectView(R.id.passwordName)
    TextInputLayout passwordName;
    public @InjectView(R.id.cityName)
    TextInputLayout cityName;
    public @InjectView(R.id.mobile_code)
    CountryCodePicker ccp;
    public @InjectView(R.id.btn_continue)
    Button btn_continue;
    public @InjectView(R.id.loginlink)
    TextView loginlink;
    public @InjectView(R.id.dochome_back)
    ImageView dochome_back;
    public @InjectView(R.id.scrollView)
    ScrollView scrollView;
    public @InjectView(R.id.error_mob)
    RelativeLayout error_mob;
    public @InjectView(R.id.location_placesearch)
    RecyclerView mRecyclerView;
    protected GoogleApiClient mGoogleApiClient;
    protected boolean isInternetAvailable;
    private String oldstring = "";
    private boolean isCity = false;
    private PlacesAutoCompleteAdapter mAutoCompleteAdapter;

    public String facebookKitVerifiedMobileNumber="", facebookVerifiedMobileNumberCountryCode="";
    /*
     *   Text watcher for city search
     */
    protected TextWatcher PlaceTextWatcher = new TextWatcher() {

        int count = 0;

        public void onTextChanged(final CharSequence s, int start, int before,
                                  int count) {

            if (s.toString().equals("")) {

                mRecyclerView.setVisibility(View.GONE);
            }

        }

        public void beforeTextChanged(CharSequence s, int start, int count,
                                      int after) {
            if (s.toString().equals("")) {
                scrollView.post(new Runnable() {
                    @Override
                    public void run() {
                        scrollView.scrollTo(0, loginlink.getBottom());
                        cityText.requestFocus();
                    }
                });
                mRecyclerView.setVisibility(View.GONE);
            }

        }

        public void afterTextChanged(final Editable s) {

            if (!s.toString().equals("") && mGoogleApiClient.isConnected()) {
                if (cityText.getText().toString().length() > 0) {
                    if (!oldstring.equals(s.toString())) {
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                oldstring = s.toString();
                                count++;
                                isCity = false;
                                mAutoCompleteAdapter.getFilter().filter(s.toString());
                                mRecyclerView.setVisibility(View.VISIBLE);
                            }
                        }, 1000);
                    } else {
                        if (isCity)
                            mRecyclerView.setVisibility(View.GONE);
                    }
                } else {
                    mRecyclerView.setVisibility(View.GONE);
                }
            } else if (!mGoogleApiClient.isConnected()) {
                Log.w(Constants.PlacesTag, Constants.API_NOT_CONNECTED);
            }

            if (s.toString().equals("")) {
                mRecyclerView.setVisibility(View.GONE);
            }

        }
    };

    /*
     *   Check email is valid or not
     */
    private static boolean isValidEmail(String email) {
        return !TextUtils.isEmpty(email) && android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    @OnClick(R.id.loginlink)
    public void loginLink() {
        Intent intent = new Intent(getApplicationContext(), SigninActivity.class);
        startActivity(intent);
        finish();
    }

    @OnClick(R.id.btn_continue)
    public void btnContinue() {
        numberRegister();
    }

    @OnClick(R.id.dochome_back)
    public void dochomeBack() {
        onBackPressed();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        buildGoogleApiClient();
        setContentView(R.layout.activity_register);

        ButterKnife.inject(this);
        AppController.getAppComponent().inject(this);

        getMobileNumerFromIntentAndSetToEditText();
        dialog = commonMethods.getAlertDialog(this);


        isInternetAvailable = commonMethods.isOnline(this);

        error_mob.setVisibility(View.GONE);

        input_first.addTextChangedListener(new NameTextWatcher(input_first));
        input_last.addTextChangedListener(new NameTextWatcher(input_last));
        emaitext.addTextChangedListener(new NameTextWatcher(emaitext));
        passwordtext.addTextChangedListener(new NameTextWatcher(passwordtext));
        cityText.addTextChangedListener(new NameTextWatcher(cityText));
        mobile_number.addTextChangedListener(new NameTextWatcher(mobile_number));

        mAutoCompleteAdapter = new PlacesAutoCompleteAdapter(this, R.layout.location_search,
                mGoogleApiClient, BOUNDS_INDIA, null);

        mRecyclerView.setVisibility(View.GONE);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setNestedScrollingEnabled(false);
        LinearLayoutManager mLinearLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLinearLayoutManager);
        mRecyclerView.setAdapter(mAutoCompleteAdapter);


        //mobile_number.setText(sessionManager.getPhoneNumber());
        //ccp.setCountryForPhoneCode(Integer.parseInt(sessionManager.getCountryCode()));
        //mobile_number.setKeyListener(null);
        //mobile_number.setEnabled(false);

        /*
         *   City search from google API
         */
        mRecyclerView.addOnItemTouchListener(
                new RecyclerItemClickListener(this, new RecyclerItemClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        final PlacesAutoCompleteAdapter.PlaceAutocomplete item = mAutoCompleteAdapter.getItem(position);
                        if (item != null) {

                            CommonMethods.DebuggableLogI("TAG", "Autocomplete item selected: " + item.description);

                            oldstring = item.addresss.toString();
                            isCity = true;
                            validateCity();
                            cityText.setText(item.addresss.toString());
                            cityText.setSelection(cityText.getText().length());
                            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                            imm.hideSoftInputFromWindow(cityText.getWindowToken(), 0);

                            mRecyclerView.setVisibility(View.GONE);
                        }
                    }
                })
        );

        /*
         *   Redirect to signin page
         */
        loginlink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), SigninActivity.class);
                startActivity(intent);
                finish();
            }
        });

        /*
         *   Validate registration fields
         */
        btn_continue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                numberRegister();


            }
        });

        dochome_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        ccp.setOnCountryChangeListener(new CountryCodePicker.OnCountryChangeListener() {
            @Override
            public void onCountrySelected() {
                ccp.getSelectedCountryName(); //  Toast.makeText(getApplicationContext(), "Updated " + ccp.getSelectedCountryName(), Toast.LENGTH_SHORT).show();
            }
        });
        if (Locale.getDefault().getLanguage().equals("fa")) {
            ccp.changeDefaultLanguage(CountryCodePicker.Language.ARABIC);
        }
        /** Setting mobile number depends upon country code**/


    }

    private void getMobileNumerFromIntentAndSetToEditText() {
        if (getIntent() != null) {
            facebookKitVerifiedMobileNumber = getIntent().getStringExtra(FACEBOOK_ACCOUNT_KIT_PHONE_NUMBER_KEY);
            facebookVerifiedMobileNumberCountryCode = getIntent().getStringExtra(FACEBOOK_ACCOUNT_KIT_PHONE_NUMBER_COUNTRY_CODE_KEY);
        }
        mobile_number.setText(facebookKitVerifiedMobileNumber);
        mobile_number.setEnabled(false);
        ccp.setCountryForPhoneCode(Integer.parseInt(facebookVerifiedMobileNumberCountryCode));
        ccp.setCcpClickable(false);




    }

    private void numberRegister() {


        isInternetAvailable = commonMethods.isOnline(this);
        if (!validateFirst()) {
            return;
        }
        if (!validateLast()) {
            return;
        }
        if (!validateEmail()) {      //setting error message in submit button
            emailName.setError(getString(R.string.error_msg_email));
            return;
        } else {
            emailName.setError(null);
        }
        if (!validatePhone()) {
            error_mob.setVisibility(View.VISIBLE);
            ColorStateList colorStateList = ColorStateList.valueOf(getResources().getColor(R.color.error_red));
            ViewCompat.setBackgroundTintList(mobile_number, colorStateList);
            return;
        } else {
            error_mob.setVisibility(View.GONE);
            ColorStateList colorStateList = ColorStateList.valueOf(getResources().getColor(R.color.app_continue));
            ViewCompat.setBackgroundTintList(mobile_number, colorStateList);
        }
        if (!validatePassword()) {
            passwordName.setError(getString(R.string.error_msg_password));
            return;
        } else {
            passwordName.setError(null);
        }

        if (!validateCity()) {
            return;
        }


        /*sessionManager.setFirstName(input_first.getText().toString());
        sessionManager.setLastName(input_last.getText().toString());
        sessionManager.setEmail(emaitext.getText().toString());
        sessionManager.setTemporaryPhonenumber(mobile_number.getText().toString());
        sessionManager.setPassword(passwordtext.getText().toString());
        sessionManager.setTemporaryCountryCode(ccp.getSelectedCountryCodeWithPlus().replaceAll("\\+", ""));
        sessionManager.setCity(cityText.getText().toString());*/

        if (isInternetAvailable) {
            commonMethods.showProgressDialog(Register.this, customDialog);
            //apiService.numberValidation(sessionManager.getType(), sessionManager.getTemporaryPhonenumber(), sessionManager.getTemporaryCountryCode(), "", sessionManager.getLanguageCode()).enqueue(new RequestCallback(REQ_VALIDATE_NUMBER,this));
            apiService.registerOtp(sessionManager.getType(), facebookKitVerifiedMobileNumber, facebookVerifiedMobileNumberCountryCode, emaitext.getText().toString(),input_first.getText().toString(), input_last.getText().toString(), passwordtext.getText().toString(), cityText.getText().toString(), sessionManager.getDeviceId(), sessionManager.getDeviceType(), sessionManager.getLanguageCode()).enqueue(new RequestCallback(REQ_REG, this));

        } else {
            commonMethods.showMessage(this, dialog, getResources().getString(R.string.Interneterror));
        }
    }

    /*
     *   Validate first name
     */
    private boolean validateFirst() {
        if (input_first.getText().toString().trim().isEmpty()) {
            input_layout_first.setError(getString(R.string.error_msg_firstname));
            requestFocus(input_first);
            return false;
        } else {
            input_layout_first.setErrorEnabled(false);
        }

        return true;
    }

    /*
     *   Validate last name
     */
    private boolean validateLast() {
        if (input_last.getText().toString().trim().isEmpty()) {
            input_layout_last.setError(getString(R.string.error_msg_lastname));
            requestFocus(input_last);
            return false;
        } else {
            input_layout_last.setErrorEnabled(false);
        }
        return true;
    }

    /*
     *   Validate email address
     */
    private boolean validateEmail() {
        String email = emaitext.getText().toString().trim();

        if (email.isEmpty() || !isValidEmail(email)) {
            //emailName.setError(getString(R.string.error_msg_email));
            requestFocus(emaitext);
            return false;
        } else {
            emailName.setErrorEnabled(false);
        }

        return true;
    }

    /*
     *   Validate phone number
     */
    private boolean validatePhone() {
        if (mobile_number.getText().toString().trim().isEmpty() || mobile_number.getText().toString().length() < 6) {

            requestFocus(mobile_number);
            return false;
        } else {
            error_mob.setVisibility(View.GONE);
            ColorStateList colorStateList = ColorStateList.valueOf(getResources().getColor(R.color.app_continue));
            ViewCompat.setBackgroundTintList(mobile_number, colorStateList);
        }
        return true;
    }

    /*
     *   Validate city
     */
    private boolean validateCity() {

        isCity = !cityText.getText().toString().equals("");


        if (!isCity) {
            if (cityText.getText().toString().equals("")) {
                cityName.setError(getString(R.string.error_msg_city));
            } else {
                cityName.setError(getString(R.string.error_msg_city));
            }
            requestFocus(cityText);
            return false;
        } else {
            cityName.setErrorEnabled(false);
        }

        return true;
    }

    /*
     *   Validate password
     */
    private boolean validatePassword() {
        if (passwordtext.getText().toString().trim().isEmpty() || passwordtext.getText().toString().length() < 6) {
            requestFocus(passwordtext);
            return false;
        } else {
            passwordName.setErrorEnabled(false);
        }

        return true;
    }

    private void requestFocus(View view) {
        if (view.requestFocus()) {
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        }
    }

    @Override
    public void onSuccess(JsonResponse jsonResp, String data) {
        commonMethods.hideProgressDialog();
        if (!jsonResp.isOnline()) {
            if (!TextUtils.isEmpty(data))
                commonMethods.showMessage(this, dialog, data);
            return;
        }
        switch (jsonResp.getRequestCode()) {
            case REQ_REG: {
                onSuccessRegisterPwd(jsonResp);
                break;
            }
        }


    }



    @Override
    public void onFailure(JsonResponse jsonResp, String data) {
        commonMethods.hideProgressDialog();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent signin = new Intent(getApplicationContext(), SigninSignupHomeActivity.class);
        startActivity(signin);
        overridePendingTransition(R.anim.ub__slide_in_left, R.anim.ub__slide_out_right);
    }


    /*
     *    Google API called for place search
     */
    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .addApi(Places.GEO_DATA_API)
                .build();
    }

    @Override
    public void onConnected(Bundle bundle) {
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


        CommonMethods.DebuggableLogD(TAG, "ON connected");

    }

    @Override
    public void onConnectionSuspended(int i) {
        CommonMethods.DebuggableLogI(TAG, "Connection suspended");
        mGoogleApiClient.connect();
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        CommonMethods.DebuggableLogI(TAG, "Connection Failed");
    }

    @Override
    public void onResume() {
        super.onResume();
        if (!mGoogleApiClient.isConnected() && !mGoogleApiClient.isConnecting()) {
            CommonMethods.DebuggableLogV("Google API", "Connecting");
            mGoogleApiClient.connect();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (mGoogleApiClient.isConnected()) {
            CommonMethods.DebuggableLogV("Google API", "Dis-Connecting");
            mGoogleApiClient.disconnect();
        }
    }

    /*
     *
     *   Edit text, Text watcher
     */
    private class NameTextWatcher implements TextWatcher {

        private View view;

        private NameTextWatcher(View view) {
            this.view = view;
        }

        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            CommonMethods.DebuggableLogI("i Check", Integer.toString(i));
        }

        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            if (mobile_number.getText().toString().startsWith("0")) {
                mobile_number.setText("");
            }
        }

        public void afterTextChanged(Editable editable) {
            switch (view.getId()) {
                case R.id.input_first:
                    validateFirst();
                    break;
                case R.id.input_last:
                    validateLast();
                    break;
                case R.id.emaitext:
                    validateEmail();
                    break;
                case R.id.passwordtext:
                    validatePassword();
                    break;
                case R.id.mobile_number:
                    validatePhone();
                    break;
                case R.id.cityText:
                    validateCity();
                    break;
                default:
                    break;
            }
        }
    }

    public void initAccountKitEmailFlow() {
        FacebookAccountKitActivity.openFacebookAccountKitActivity(this);

    }



    private void onSuccessRegisterPwd(JsonResponse jsonResp) {
        commonMethods.hideProgressDialog();

        LoginDetails signInUpResultModel = gson.fromJson(jsonResp.getStrResponse(), LoginDetails.class);

        if (signInUpResultModel != null) {

            try {

                if (signInUpResultModel.getStatusCode().matches("1")) {


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


            } catch (JSONException e) {
                e.printStackTrace();
            }

        }


    }
}
