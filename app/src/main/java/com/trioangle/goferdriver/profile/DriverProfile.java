package com.trioangle.goferdriver.profile;
/**
 * @package com.trioangle.goferdriver.profile
 * @subpackage profile model
 * @category DriverProfile
 * @author Trioangle Product Team
 * @version 1.5
 */

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.Html;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.hbb20.CountryCodePicker;
import com.squareup.picasso.Picasso;
import com.trioangle.goferdriver.BuildConfig;
import com.trioangle.goferdriver.R;
import com.trioangle.goferdriver.configs.SessionManager;
import com.trioangle.goferdriver.dependencies.module.ImageCompressAsyncTask;
import com.trioangle.goferdriver.facebookAccountKit.FacebookAccountKitActivity;
import com.trioangle.goferdriver.helper.Constants;
import com.trioangle.goferdriver.helper.CustomDialog;
import com.trioangle.goferdriver.helper.RunTimePermission;
import com.trioangle.goferdriver.interfaces.ApiService;
import com.trioangle.goferdriver.interfaces.ImageListener;
import com.trioangle.goferdriver.interfaces.ServiceListener;
import com.trioangle.goferdriver.model.JsonResponse;
import com.trioangle.goferdriver.network.AppController;
import com.trioangle.goferdriver.util.CommonKeys;
import com.trioangle.goferdriver.util.CommonMethods;
import com.trioangle.goferdriver.util.RequestCallback;
import com.trioangle.goferdriver.util.RuntimePermissionDialogFragment;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import javax.inject.Inject;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import de.hdodenhof.circleimageview.CircleImageView;
import okhttp3.RequestBody;

import static com.trioangle.goferdriver.util.CommonKeys.FACEBOOK_ACCOUNT_KIT_MESSAGE_KEY;
import static com.trioangle.goferdriver.util.CommonKeys.FACEBOOK_ACCOUNT_KIT_PHONE_NUMBER_COUNTRY_CODE_KEY;
import static com.trioangle.goferdriver.util.CommonKeys.FACEBOOK_ACCOUNT_KIT_PHONE_NUMBER_KEY;
import static com.trioangle.goferdriver.util.Enums.REQ_DRIVER_PROFILE;
import static com.trioangle.goferdriver.util.Enums.REQ_UPDATE_PROFILE;
import static com.trioangle.goferdriver.util.Enums.REQ_UPLOAD_PROFILE_IMG;
import static com.trioangle.goferdriver.util.RuntimePermissionDialogFragment.CAMERA_PERMISSION_ARRAY;
import static com.trioangle.goferdriver.util.RuntimePermissionDialogFragment.checkPermissionStatus;
/* ************************************************************
                DriverProfile
Its used to get driver profile details to show view on screen
*************************************************************** */

public class DriverProfile extends AppCompatActivity implements ServiceListener, ImageListener, RuntimePermissionDialogFragment.RuntimePermissionRequestedCallback {

    public static final int PICK_IMAGE_REQUEST_CODE = 1888; // the number doesn't matter
    private static final int SELECT_FILE = 1;
    public @Inject
    ApiService apiService;
    public @Inject
    CommonMethods commonMethods;
    public @Inject
    CustomDialog customDialog;
    public @Inject
    Gson gson;
    public @Inject
    RunTimePermission runTimePermission;
    public @Inject
    SessionManager sessionManager;
    public @InjectView(R.id.editicon)
    ImageView editicon;
    public @InjectView(R.id.cameraicon)
    ImageView cameraicon;
    public @InjectView(R.id.arrow)
    ImageView arrow;
    public @InjectView(R.id.input_first)
    EditText input_first;
    public @InjectView(R.id.input_last)
    EditText input_last;
    public @InjectView(R.id.emaitext)
    EditText emaitext;
    public @InjectView(R.id.mobile_number)
    EditText mobile_number;
    public @InjectView(R.id.addresstext)
    EditText addresstext;
    public @InjectView(R.id.addresstext2)
    EditText addresstext2;
    public @InjectView(R.id.citytext)
    EditText citytext;
    public @InjectView(R.id.posttext)
    EditText posttext;
    public @InjectView(R.id.statetext)
    EditText statetext;
    public @InjectView(R.id.input_layout_first)
    TextInputLayout input_layout_first;
    public @InjectView(R.id.input_layout_last)
    TextInputLayout input_layout_last;
    public @InjectView(R.id.emailName)
    TextInputLayout emailName;
    public @InjectView(R.id.addressName)
    TextInputLayout addressName;
    public @InjectView(R.id.addressName2)
    TextInputLayout addressName2;
    public @InjectView(R.id.cityName)
    TextInputLayout cityName;
    public @InjectView(R.id.stateName)
    TextInputLayout stateName;
    public @InjectView(R.id.postName)
    TextInputLayout postName;
    public @InjectView(R.id.titletext)
    TextView titletext;
    public @InjectView(R.id.close)
    TextView close;
    public @InjectView(R.id.tickshad)
    TextView tickshad;
    public @InjectView(R.id.mobile_code)
    CountryCodePicker ccp;
    public @InjectView(R.id.profile_image1)
    CircleImageView profile_image1;
    public @InjectView(R.id.flaglayout)
    RelativeLayout flaglayout;
    public String first_name;
    public String last_name;
    public String mobile_numbers;
    public String country_code;
    public String email_id;
    public String user_thumb_image;
    public String address_line1;
    public String address_line2;
    public String city;
    public String state;
    public String postal_code;
    public Bitmap bm;
    public int docType = 6;          //By Default Set 6 for Profile Image
    private boolean isUpdate = false;
    private AlertDialog dialog;
    private String imagePath = "";
    private File imageFile;
    private Uri imageUri;

    /*
     * check is valid email or not
     */
    private static boolean isValidEmail(String email) {
        return !TextUtils.isEmpty(email) && android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    @OnClick(R.id.arrow)
    public void backpressed() {
        onBackPressed();
    }

    @OnClick(R.id.close)
    public void close() {
        onBackPressed();
    }

    @OnClick(R.id.tickshad)
    public void tickshad() {
        getValidation();
    }

    /*
     * upload profile image
     */
    @OnClick(R.id.profile_image1)
    public void profileimage1() {
        checkAllPermission();
    }

    @OnClick(R.id.cameraicon)
    public void cameraicon() {
        checkAllPermission();
    }

    @OnClick(R.id.flaglayout)
    public void flag() {
        /*sessionManager.setisEdit(true);
        Intent mobile = new Intent(getApplicationContext(), MobileActivity.class);
        startActivity(mobile);*/

        FacebookAccountKitActivity.openFacebookAccountKitActivity(this);
    }

    @OnClick(R.id.mobile_number)
    public void mobilenumber() {
        FacebookAccountKitActivity.openFacebookAccountKitActivity(this);
    }

    /*@OnClick(R.id.mobile_code)
    public void mobilecode() {
        sessionManager.setisEdit(true);
        Intent mobile = new Intent(getApplicationContext(), MobileActivity.class);
        startActivity(mobile);
    }*/

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_driver_profile);
        ButterKnife.inject(this);
        AppController.getAppComponent().inject(this);


        dialog = commonMethods.getAlertDialog(this);

        input_first.addTextChangedListener(new NameTextWatcher(input_first));
        input_last.addTextChangedListener(new NameTextWatcher(input_last));
        emaitext.addTextChangedListener(new NameTextWatcher(emaitext));
        addresstext.addTextChangedListener(new NameTextWatcher(addresstext));
        addresstext2.addTextChangedListener(new NameTextWatcher(addresstext2));
        citytext.addTextChangedListener(new NameTextWatcher(citytext));
        statetext.addTextChangedListener(new NameTextWatcher(statetext));
        posttext.addTextChangedListener(new NameTextWatcher(posttext));


        ccp.setCcpClickable(false);

        /*
         * Get driver profile API call
         */
        String profiledetails = sessionManager.getProfileDetail();
        if (profiledetails == null) {
            getDriver();
        } else {
            /*
             *  Load driver profile API
             *
             */
            loadData(profiledetails);
        }

        input_first.setFocusableInTouchMode(true);
        input_last.setFocusableInTouchMode(true);
        emaitext.setFocusableInTouchMode(true);
        addresstext.setFocusableInTouchMode(true);
        addresstext2.setFocusableInTouchMode(true);
        citytext.setFocusableInTouchMode(true);
        statetext.setFocusableInTouchMode(true);
        posttext.setFocusableInTouchMode(true);
        cameraicon.setVisibility(View.VISIBLE);


    }

    private void getValidation() {
        if (!validateText(input_first, input_layout_first)) {
            return;
        }
        if (!validateText(input_last, input_layout_last)) {
            return;
        }

        if (!validateEmail()) {
            return;
        }
        if (!validatePhone()) {
            return;
        }
        if (!validateText(addresstext, addressName)) {
            return;
        }
        if (!validateText(addresstext2, addressName2)) {
            return;
        }
        if (!validateText(citytext, cityName)) {
            return;
        }
        if (!validateText(statetext, stateName)) {
            return;
        }
        if (!validateText(posttext, postName)) {
            return;
        }

        /*
         *  get Driver profile data
         */
        first_name = input_first.getText().toString();
        last_name = input_last.getText().toString();
        email_id = emaitext.getText().toString();
        mobile_numbers = mobile_number.getText().toString();
        address_line1 = addresstext.getText().toString();
        address_line2 = addresstext2.getText().toString();
        city = citytext.getText().toString();
        state = statetext.getText().toString();
        postal_code = posttext.getText().toString();
        isUpdate = true;

        updateDriver();
    }

    private void updateDriver() {
        commonMethods.showProgressDialog(this, customDialog);
        apiService.updateDriverProfile(getDetails()).enqueue(new RequestCallback(REQ_UPDATE_PROFILE, this));
    }

    /**
     * Api call to fetch profile details
     */

    private void getDriver() {
        commonMethods.showProgressDialog(this, customDialog);
        apiService.getDriverProfile(sessionManager.getAccessToken()).enqueue(new RequestCallback(REQ_DRIVER_PROFILE, this));
    }

    /**
     * Upload Image using Post Method
     */
    private void onSuccessUploadImage(JsonResponse jsonResponse) {
        Toast.makeText(this, R.string.image_uploaded, Toast.LENGTH_SHORT).show();
        user_thumb_image = (String) commonMethods.getJsonValue(jsonResponse.getStrResponse(), Constants.PROFILEIMAGE, String.class);
        loadImage(user_thumb_image);
    }

    /**
     * Load Image
     */
    private void loadImage(String imageurl) {
        commonMethods.hideProgressDialog();
        Picasso.with(getApplicationContext()).load(imageurl).into(profile_image1);

    }

    /**
     * Getting Driver Deatils to Update
     *
     * @return hashmap Datas
     */
    private LinkedHashMap<String, String> getDetails() {
        LinkedHashMap<String, String> hashMap = new LinkedHashMap<>();
        hashMap.put("first_name", first_name);
        hashMap.put("last_name", last_name);
        hashMap.put("email_id", email_id);
        hashMap.put("mobile_number", mobile_numbers);
        hashMap.put("country_code", ccp.getSelectedCountryCodeWithPlus().replaceAll("\\+", ""));
        hashMap.put("address_line1", address_line1);
        hashMap.put("address_line2", address_line2);
        hashMap.put("city", city);
        hashMap.put("state", state);
        hashMap.put("postal_code", postal_code);
        hashMap.put("token", sessionManager.getAccessToken());
        hashMap.put("profile_image", user_thumb_image);
        return hashMap;
    }

    private void checkAllPermission() {

        checkPermissionStatus(this, getSupportFragmentManager(), this, CAMERA_PERMISSION_ARRAY,0,0);
    }

    /**
     * If Deny open and Enable the permission
     */
    private void showEnablePermissionDailog(final int type, String message) {
        if (!customDialog.isVisible()) {
            customDialog = new CustomDialog("Alert", message, getString(R.string.ok), new CustomDialog.btnAllowClick() {
                @Override
                public void clicked() {
                    if (type == 0) callPermissionSettings();
                    else
                        startActivityForResult(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS), 101);
                }
            });
            customDialog.show(getSupportFragmentManager(), "");
        }
    }

    /**
     * Opens the APP Permission Settings Page
     */
    private void callPermissionSettings() {
        Intent intent = new Intent();
        intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        Uri uri = Uri.fromParts("package", getApplicationContext().getPackageName(), null);
        intent.setData(uri);
        startActivityForResult(intent, 300);
    }

    /**
     * Bottom Sheet to choose camera or gallery
     */

    public void pickProfileImg() {
        View view = getLayoutInflater().inflate(R.layout.camera_dialog_layout, null);
        LinearLayout lltCamera = view.findViewById(R.id.llt_camera);
        LinearLayout lltLibrary = view.findViewById(R.id.llt_library);


        final Dialog bottomSheetDialog = new Dialog(this, R.style.MaterialDialogSheet);
        bottomSheetDialog.setContentView(view);
        bottomSheetDialog.setCancelable(true);
        if (bottomSheetDialog.getWindow() == null) return;
        bottomSheetDialog.getWindow().setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        bottomSheetDialog.getWindow().setGravity(Gravity.BOTTOM);
        bottomSheetDialog.show();

        lltCamera.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                bottomSheetDialog.dismiss();
                cameraIntent();
            }
        });

        lltLibrary.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                bottomSheetDialog.dismiss();
                imageFile = commonMethods.getDefaultFileName(DriverProfile.this);

                galleryIntent();
            }
        });
    }


    /**
     * Intent to camera
     */
    private void cameraIntent() {
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        imageFile = commonMethods.cameraFilePath(DriverProfile.this);
        Uri imageUri = FileProvider.getUriForFile(DriverProfile.this, BuildConfig.APPLICATION_ID + ".provider", imageFile);

        try {
            List<ResolveInfo> resolvedIntentActivities = DriverProfile.this.getPackageManager().queryIntentActivities(cameraIntent, PackageManager.MATCH_DEFAULT_ONLY);
            for (ResolveInfo resolvedIntentInfo : resolvedIntentActivities) {
                String packageName = resolvedIntentInfo.activityInfo.packageName;
                grantUriPermission(packageName, imageUri, Intent.FLAG_GRANT_WRITE_URI_PERMISSION | Intent.FLAG_GRANT_READ_URI_PERMISSION);
            }
            cameraIntent.putExtra("return-data", true);
            cameraIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        } catch (Exception e) {
            e.printStackTrace();
        }
        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
        startActivityForResult(cameraIntent, PICK_IMAGE_REQUEST_CODE);
        commonMethods.refreshGallery(DriverProfile.this, imageFile);

    }

    /**
     * Intent to gallery page
     */
    private void galleryIntent() {
        Intent i = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        DriverProfile.this.startActivityForResult(i, SELECT_FILE);
    }



    /*
     *  Get data from camera and gallery
     */



    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case PICK_IMAGE_REQUEST_CODE:
                if (resultCode == Activity.RESULT_OK) {
                    imageUri = Uri.fromFile(imageFile);
                    imagePath = imageUri.getPath();

                    if (!TextUtils.isEmpty(imagePath)) {
                        commonMethods.showProgressDialog(this, customDialog);
                        new ImageCompressAsyncTask(docType, this, imagePath, this).execute();
                    }
                }
                break;
            case SELECT_FILE:
                if (resultCode == Activity.RESULT_OK) {
                    onSelectFromGalleryResult(data);
                }
                break;

            case CommonKeys.ACTIVITY_REQUEST_CODE_START_FACEBOOK_ACCOUNT_KIT: {
                /*if (resultCode == CommonKeys.FACEBOOK_ACCOUNT_KIT_RESULT_NEW_USER) {
                    updateDriverPhoneNumber(data.getStringExtra(FACEBOOK_ACCOUNT_KIT_PHONE_NUMBER_KEY), data.getStringExtra(FACEBOOK_ACCOUNT_KIT_PHONE_NUMBER_COUNTRY_CODE_KEY));
                } else if (resultCode == CommonKeys.FACEBOOK_ACCOUNT_KIT_RESULT_OLD_USER) {
                    commonMethods.showMessage(this, dialog, data.getStringExtra(FACEBOOK_ACCOUNT_KIT_MESSAGE_KEY));

                }*/
                if(resultCode == RESULT_OK){
                    updateDriverPhoneNumber(data.getStringExtra(FACEBOOK_ACCOUNT_KIT_PHONE_NUMBER_KEY), data.getStringExtra(FACEBOOK_ACCOUNT_KIT_PHONE_NUMBER_COUNTRY_CODE_KEY));
                }

            }
            default:
                break;
        }

    }

    public void updateDriverPhoneNumber(String phoneNumber, String countryCode) {
        mobile_number.setText(phoneNumber);
        ccp.setCountryForPhoneCode(Integer.parseInt(countryCode));

    }

    /*
     * Get image path from gallery
     */
    @SuppressWarnings("deprecation")
    private void onSelectFromGalleryResult(Intent data) {

        bm = null;
        if (data != null) {
            Uri selectedImage = data.getData();
            String[] filePathColumn = {MediaStore.Images.Media.DATA};

            Cursor cursor = getContentResolver().query(selectedImage, filePathColumn, null, null, null);
            cursor.moveToFirst();
            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            String picturePath = cursor.getString(columnIndex);
            cursor.close();
            bm = BitmapFactory.decodeFile(picturePath);

            imagePath = picturePath;

            if (!TextUtils.isEmpty(imagePath)) {
                commonMethods.showProgressDialog(this, customDialog);
                new ImageCompressAsyncTask(docType, this, imagePath, this).execute();
            }
        }
    }


    @Override
    public void onImageCompress(String filePath, RequestBody requestBody) {
        apiService.uploadProfileImage(requestBody, sessionManager.getAccessToken()).enqueue(new RequestCallback(REQ_UPLOAD_PROFILE_IMG, this));
    }

    @Override
    public void onSuccess(JsonResponse jsonResp, String data) {

        commonMethods.hideProgressDialog();
        if (!jsonResp.isOnline()) {
            if (!TextUtils.isEmpty(data)) commonMethods.showMessage(this, dialog, data);
            return;
        }
        switch (jsonResp.getRequestCode()) {
            case REQ_UPDATE_PROFILE:
                String statusmessage = jsonResp.getStatusMsg();  //(String) commonMethods.getJsonValue(jsonResp.getStrResponse(), "status_message", String.class);
                if (jsonResp.isSuccess()) {
                    sessionManager.setProfileDetail(jsonResp.getStrResponse());

                    if (isUpdate) {
                        isUpdate = false;
                        commonMethods.showMessage(this, dialog, statusmessage);
                        onBackPressed();
                    } else {
                        loadData(jsonResp.getStrResponse());
                    }
                } else {
                    commonMethods.showMessage(this, dialog, statusmessage);
                }
                break;
            case REQ_UPLOAD_PROFILE_IMG:
                if (jsonResp.isSuccess()) {
                    onSuccessUploadImage(jsonResp);
                } else if (!TextUtils.isEmpty(jsonResp.getStatusMsg())) {
                    commonMethods.showMessage(this, dialog, jsonResp.getStatusMsg());
                }
                break;
            case REQ_DRIVER_PROFILE:
                String currency_code = (String) commonMethods.getJsonValue(jsonResp.getStrResponse(), "currency_code", String.class);
                String currency_symbol = (String) commonMethods.getJsonValue(jsonResp.getStrResponse(), "currency_symbol", String.class);
                String carid = (String) commonMethods.getJsonValue(jsonResp.getStrResponse(), "car_id", String.class);
                currency_symbol = Html.fromHtml(currency_symbol).toString();
                sessionManager.setCurrencyCode(currency_code);
                sessionManager.setCurrencySymbol(currency_symbol);
                sessionManager.setVehicleId(carid);


                break;
            default:
                break;
        }


    }

    @Override
    public void onFailure(JsonResponse jsonResp, String data) {
        if (!TextUtils.isEmpty(jsonResp.getStatusMsg())) {
            commonMethods.showMessage(this, dialog, jsonResp.getStatusMsg());
        }
    }

    public void loadData(String profiledetails) {
        try {
            JSONObject jsonObj = new JSONObject(profiledetails);

            first_name = jsonObj.getString("first_name");
            last_name = jsonObj.getString("last_name");
            mobile_numbers = jsonObj.getString("mobile_number");
            country_code = jsonObj.getString("country_code");
            CommonMethods.DebuggableLogI("Country code from api", country_code);
            email_id = jsonObj.getString("email_id");
            user_thumb_image = jsonObj.getString("profile_image");
            address_line1 = jsonObj.getString("address_line1");
            address_line2 = jsonObj.getString("address_line2");
            city = jsonObj.getString("city");
            state = jsonObj.getString("state");
            postal_code = jsonObj.getString("postal_code");

            sessionManager.setCountryCode(jsonObj.getString("country_code"));
            sessionManager.setDoc1(jsonObj.getString("license_back"));
            sessionManager.setDoc2(jsonObj.getString("license_front"));
            sessionManager.setDoc3(jsonObj.getString("insurance"));
            sessionManager.setDoc4(jsonObj.getString("rc"));
            sessionManager.setDoc5(jsonObj.getString("permit"));

            // String vehicle_type = jsonObj.getString("vehicle_name");
            // String vehicle_number = jsonObj.getString("vehicle_number");


            if (!"".equals(first_name)) input_first.setText(first_name);

            if (!"".equals(last_name)) input_last.setText(last_name);

            if (!"".equals(email_id)) emaitext.setText(email_id);

            if (!"".equals(mobile_numbers)) mobile_number.setText(mobile_numbers);
            sessionManager.setPhoneNumber(mobile_numbers);

            if (!"".equals(address_line1)) addresstext.setText(address_line1);

            if (!"".equals(address_line2)) addresstext2.setText(address_line2);

            if (!"".equals(city)) citytext.setText(city);

            if (!"".equals(state)) statetext.setText(state);

            if (!"".equals(postal_code)) posttext.setText(postal_code);
            if (!"".equals(sessionManager.getCountryCode())) {
                ccp.setCountryForPhoneCode(Integer.parseInt(sessionManager.getCountryCode()));
            }
            Picasso.with(getApplicationContext()).load(user_thumb_image).into(profile_image1);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public boolean validateText(EditText input, TextInputLayout inputerror) {
        if (input.getText().toString().trim().isEmpty()) {
            if (input.getId() == R.id.input_first)
                inputerror.setError(getString(R.string.error_msg_firstname));
            else if (input.getId() == R.id.input_last)
                inputerror.setError(getString(R.string.error_msg_lastname));
            else if (input.getId() == R.id.addresstext)
                inputerror.setError(getString(R.string.error_msg_address));
            else if (input.getId() == R.id.addresstext2)
                inputerror.setError(getString(R.string.error_msg_address));
            else if (input.getId() == R.id.citytext)
                inputerror.setError(getString(R.string.error_msg_city));
            else if (input.getId() == R.id.statetext)
                inputerror.setError(getString(R.string.error_msg_state));
            else if (input.getId() == R.id.posttext)
                inputerror.setError(getString(R.string.error_msg_post));
            requestFocus(input);
            return false;
        } else {
            inputerror.setErrorEnabled(false);
        }

        return true;
    }

    /*
     *  Validate Email address
     */
    private boolean validateEmail() {
        String email = emaitext.getText().toString().trim();

        if (email.isEmpty() || !isValidEmail(email)) {
            emailName.setError(getString(R.string.error_msg_email));
            requestFocus(emaitext);
            return false;
        } else {
            emailName.setErrorEnabled(false);
        }

        return true;
    }

    /*
     *  Validate phone number
     */
    private boolean validatePhone() {
        if (mobile_number.getText().toString().trim().isEmpty() || mobile_number.getText().toString().length() < 6) {
            commonMethods.showMessage(this, dialog, getString(R.string.error_msg_phone));
            requestFocus(mobile_number);
            return false;
        }

        return true;
    }

    /*
     * Edit address request focus
     */
    private void requestFocus(View view) {
        if (view.requestFocus()) {
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (sessionManager.getisEdit()) {
            mobile_number.setText(sessionManager.getPhoneNumber());
            CommonMethods.DebuggableLogI("Country code from session", sessionManager.getCountryCode());
            ccp.setCountryForPhoneCode(Integer.parseInt(sessionManager.getCountryCode()));
            sessionManager.setisEdit(false);
        }
    }

    @Override
    public void permissionGranted(int requestCodeForCallbackIdentificationCode, int requestCodeForCallbackIdentificationCodeSubDivision) {
        pickProfileImg();
    }

    @Override
    public void permissionDenied(int requestCodeForCallbackIdentificationCode, int requestCodeForCallbackIdentificationCodeSubDivision) {

    }

    /*
     *  edit taxtwatcher
     */
    private class NameTextWatcher implements TextWatcher {

        private View view;

        private NameTextWatcher(View view) {
            this.view = view;
        }

        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            CommonMethods.DebuggableLogV("Do", "Nothing");
        }

        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            CommonMethods.DebuggableLogV("Do", "Nothing");
        }

        public void afterTextChanged(Editable editable) {
            switch (view.getId()) {

                case R.id.input_first:
                    validateText(input_first, input_layout_first);
                    break;
                case R.id.input_last:
                    validateText(input_last, input_layout_last);
                    break;
                case R.id.emaitext:
                    validateEmail();
                    break;
                case R.id.mobile_number:
                    validatePhone();
                    break;
                case R.id.addresstext:
                    validateText(addresstext, addressName);
                    break;
                case R.id.addresstext2:
                    validateText(addresstext2, addressName2);
                    break;
                case R.id.citytext:
                    validateText(citytext, cityName);
                    break;
                case R.id.statetext:
                    validateText(statetext, stateName);
                    break;
                case R.id.posttext:
                    validateText(posttext, postName);
                    break;
                default:
                    break;

            }
        }
    }

}
