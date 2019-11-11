package com.trioangle.goferdriver.signinsignup;
/**
 * @package com.trioangle.gofereatsdriver
 * @subpackage gofereatsdriver.views.signinsignup
 * @category DocumentUploadActivity
 * @author Trioangle Product Team
 * @version 1.0
 */

import android.app.Activity;
import android.app.ActivityOptions;
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
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.squareup.picasso.Picasso;
import com.trioangle.goferdriver.BuildConfig;
import com.trioangle.goferdriver.MainActivity;
import com.trioangle.goferdriver.R;
import com.trioangle.goferdriver.configs.SessionManager;
import com.trioangle.goferdriver.dependencies.module.ImageCompressAsyncTask;
import com.trioangle.goferdriver.helper.Constants;
import com.trioangle.goferdriver.helper.CustomDialog;
import com.trioangle.goferdriver.helper.RunTimePermission;
import com.trioangle.goferdriver.interfaces.ApiService;
import com.trioangle.goferdriver.interfaces.ImageListener;
import com.trioangle.goferdriver.interfaces.ServiceListener;
import com.trioangle.goferdriver.model.JsonResponse;
import com.trioangle.goferdriver.network.AppController;
import com.trioangle.goferdriver.util.CommonMethods;
import com.trioangle.goferdriver.util.RequestCallback;
import com.trioangle.goferdriver.util.RuntimePermissionDialogFragment;

import net.hockeyapp.android.utils.ImageUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import okhttp3.RequestBody;

import static com.trioangle.goferdriver.util.RuntimePermissionDialogFragment.CAMERA_PERMISSION_ARRAY;
import static com.trioangle.goferdriver.util.RuntimePermissionDialogFragment.checkPermissionStatus;


/*************************************************************
 Document Upload Activity
 Its used to get upload carriage document details
 *************************************************************** */
public class DocumentUploadActivity extends AppCompatActivity implements ServiceListener, ImageListener,RuntimePermissionDialogFragment.RuntimePermissionRequestedCallback {

    public AlertDialog dialog;
    public @Inject
    SessionManager sessionManager;
    public @Inject
    CommonMethods commonMethods;
    public @Inject
    ApiService apiService;
    public @Inject
    CustomDialog customDialog;
    public @Inject
    Gson gson;
    public @Inject
    ImageUtils imageUtils;
    public @Inject
    RunTimePermission runTimePermission;

    /**
     * Annotation  using ButterKnife lib to Injection and OnClick
     **/

    public @InjectView(R.id.tvTapToAdd)
    TextView tvTapToAdd;
    public @InjectView(R.id.tvTitle)
    TextView tvTitle;
    public @InjectView(R.id.ivBack)
    ImageView ivBack;
    public @InjectView(R.id.tvDocTitle)
    TextView tvDocTitle;
    public @InjectView(R.id.ivImage)
    ImageView ivImage;
    public @InjectView(R.id.rltTapToAdd)
    RelativeLayout rltTapToAdd;
    public int docType = 0;
    private File imageFile = null;
    private String imagePath = "";
    private Uri imageUri;

    @OnClick(R.id.ivBack)
    public void onBack() {
        onBackPressed();
    }

    @OnClick(R.id.rltNext)
    public void onNext() {
        if (docType == 1&&sessionManager.getDoc1()!=null&&!sessionManager.getDoc1().equals("")) callActivity(2, getResources().getString(R.string.driverlicense_front));
        else if (docType == 2&&sessionManager.getDoc2()!=null&&!sessionManager.getDoc2().equals("")) callActivity(3, getResources().getString(R.string.motor_insurance));
        else if (docType == 3&&sessionManager.getDoc3()!=null&&!sessionManager.getDoc3().equals("")) callActivity(4, getResources().getString(R.string.registeration));
        else if (docType == 4&&sessionManager.getDoc4()!=null&&!sessionManager.getDoc4().equals("")) callActivity(5, getResources().getString(R.string.contract_carriage));
        else if (docType == 5&&sessionManager.getDoc5()!=null&&!sessionManager.getDoc5().equals("")) continueToMain();
    }

    /**
     * To redirect to Main Activity or Payment page based on paypal email
     */


    public void continueToMain() throws NullPointerException {

        if (!TextUtils.isEmpty(sessionManager.getDoc1())
                &&!TextUtils.isEmpty(sessionManager.getDoc2())
                &&!TextUtils.isEmpty(sessionManager.getDoc3())
                &&!TextUtils.isEmpty(sessionManager.getDoc4())
                &&!TextUtils.isEmpty(sessionManager.getDoc5())) {
            
            if (!sessionManager.getDriverSignupStatus().equals("Active")) {

                sessionManager.setDriverSignupStatus("Pending");
            }
            // commented the below line, because we removed add paypal details initially, it will be continued through settings page
        /*if (sessionManager.getPaypalEmail().length() > 0) {
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
            Intent x = new Intent(getApplicationContext(), MainActivity.class);
            x.putExtra("signinup", true);
            Bundle bndlanimation = ActivityOptions.makeCustomAnimation(getApplicationContext(), R.anim.cb_fade_in, R.anim.cb_face_out).toBundle();
            startActivity(x, bndlanimation);
            finish();
        }else {
            commonMethods.showMessage(this,dialog,getResources().getString(R.string.doc_upload_error));
            //Toast.makeText(this, getResources().getString(R.string.doc_upload_error), Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Add Docs
     */
    @OnClick(R.id.rltTapToAdd)
    public void uploadImage() {
        checkPermissionStatus(this, getSupportFragmentManager(), this, CAMERA_PERMISSION_ARRAY,0,0);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_document_upload);
        ButterKnife.inject(this);
        AppController.getAppComponent().inject(this);
        dialog = commonMethods.getAlertDialog(this);

        tvTitle.setText(getResources().getString(R.string.documents));

        getIntents();
    }

    /**
     * Get Intent data from previous activity for to set document title, image.
     **/
    public void getIntents() {
        /**
         * @param type  1 for licence back,
         *              2 for licence front,
         *              3 for insurance
         *              4 for registration
         *              5 for carriage
         *
         * @param title Document title
         */
        tvDocTitle.setText(getIntent().getStringExtra("title"));
        docType = getIntent().getIntExtra("type", 0);
        getImage();
    }

    public void callActivity(int type, String title) {
        Intent documentUpload = new Intent(getApplicationContext(), DocumentUploadActivity.class);
        documentUpload.putExtra("type", type);
        documentUpload.putExtra("title", title);
        startActivity(documentUpload);
        overridePendingTransition(R.anim.ub__slide_in_right, R.anim.ub__slide_out_left);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.ub__slide_in_left, R.anim.ub__slide_out_right);
    }

    /**
     * Document upload functions started
     */

    public void pickProfileImg() {
        View view = getLayoutInflater().inflate(R.layout.camera_dialog_layout, null);
        LinearLayout lltCamera = (LinearLayout) view.findViewById(R.id.llt_camera);
        LinearLayout lltLibrary = (LinearLayout) view.findViewById(R.id.llt_library);

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
                Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                imageFile = commonMethods.cameraFilePath(DocumentUploadActivity.this);
                Uri imageUri = FileProvider.getUriForFile(DocumentUploadActivity.this, BuildConfig.APPLICATION_ID + ".provider", imageFile);

                try {
                    List<ResolveInfo> resolvedIntentActivities = DocumentUploadActivity.this.getPackageManager().queryIntentActivities(cameraIntent, PackageManager.MATCH_DEFAULT_ONLY);
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
                startActivityForResult(cameraIntent, 1);
                commonMethods.refreshGallery(DocumentUploadActivity.this, imageFile);
            }
        });

        lltLibrary.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                bottomSheetDialog.dismiss();
                imageFile = commonMethods.getDefaultFileName(DocumentUploadActivity.this);


                Intent i = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(i, Constants.REQUEST_CODE_GALLERY);
            }
        });
    }


    /*
      * Get image path from gallery
      */
    @SuppressWarnings("deprecation")
    private void onSelectFromGalleryResult(Intent data) {

        Bitmap bm;
        if (data != null) {
            Uri selectedImage = data.getData();
            String[] filePathColumn = {MediaStore.Images.Media.DATA};

            Cursor cursor = getContentResolver().query(selectedImage, filePathColumn, null, null, null);
            cursor.moveToFirst();
            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            String picturePath = cursor.getString(columnIndex);
            cursor.close();
            bm = BitmapFactory.decodeFile(picturePath);
            ivImage.setImageBitmap(bm);

            imagePath = picturePath;

            if (!TextUtils.isEmpty(imagePath)) {
                commonMethods.showProgressDialog(this, customDialog);
                new ImageCompressAsyncTask(docType, DocumentUploadActivity.this, imagePath, this).execute();
            }
        }
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            switch (requestCode) {
                case 1:

                    imageUri = Uri.fromFile(imageFile);
                    imagePath = imageUri.getPath();


                    if (!TextUtils.isEmpty(imagePath)) {
                        commonMethods.showProgressDialog(this, customDialog);
                        new ImageCompressAsyncTask(docType, DocumentUploadActivity.this, imagePath, this).execute();
                    }
                    break;
                case Constants.REQUEST_CODE_GALLERY:

                    onSelectFromGalleryResult(data);
                    break;
                default:
                    break;
            }
        }
    }


    /**
     * To save image in the session
     * @param imageUrl
     */

    public void saveImage(String imageUrl) {
        if (docType == 1) sessionManager.setDoc1(imageUrl);
        else if (docType == 2) sessionManager.setDoc2(imageUrl);
        else if (docType == 3) sessionManager.setDoc3(imageUrl);
        else if (docType == 4) sessionManager.setDoc4(imageUrl);
        else if (docType == 5) sessionManager.setDoc5(imageUrl);
    }

    /**
     * To Load image from the session
     */

    public void getImage() {
        if (docType == 1) loadImage(sessionManager.getDoc1());
        else if (docType == 2) loadImage(sessionManager.getDoc2());
        else if (docType == 3) loadImage(sessionManager.getDoc3());
        else if (docType == 4) loadImage(sessionManager.getDoc4());
        else if (docType == 5) loadImage(sessionManager.getDoc5());
    }


    /**
     * To load image
     * @param imageUrl to load image
     */

    public void loadImage(String imageUrl) {
        if (!TextUtils.isEmpty(imageUrl)) {
            tvTapToAdd.setText(R.string.taptochange);

            Picasso.with(getApplicationContext()).load(imageUrl)
                    .placeholder(R.drawable.progress_animation).error(R.drawable.driver_doc).into(ivImage);
        } else {
            tvTapToAdd.setText(R.string.taptoadd);
            if (docType == 1 || docType == 2) {
                ivImage.setImageDrawable(getResources().getDrawable(R.drawable.driver_doc));
            } else {
                ivImage.setImageDrawable(getResources().getDrawable(R.drawable.v_doc));
            }
        }
    }


    /**
     * To fetch RequestBody from ImageCompressAsyncTask
     * @param filePath file path of the image
     * @param requestBody request body from image compress async task
     */

    @Override
    public void onImageCompress(String filePath, RequestBody requestBody) {
        commonMethods.hideProgressDialog();
        if (!TextUtils.isEmpty(filePath) && requestBody != null) {
            commonMethods.showProgressDialog(this, customDialog);
            apiService.uploadDocumentImage(requestBody, sessionManager.getAccessToken()).enqueue(new RequestCallback(this));
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

        if (jsonResp.isSuccess()) {

            onSuccessUploadImage(jsonResp);
        } else if (!TextUtils.isEmpty(jsonResp.getStatusMsg())) {

            commonMethods.showMessage(this, dialog, jsonResp.getStatusMsg());
        }
    }

    @Override
    public void onFailure(JsonResponse jsonResp, String data) {

        CommonMethods.DebuggableLogI("Data check ", "Data check ");
    }

    /**
     * To fetch success response of image upload
     * @param jsonResponse success json response
     */

    private void onSuccessUploadImage(JsonResponse jsonResponse) {
        String imageurl = (String) commonMethods.getJsonValue(jsonResponse.getStrResponse(), "document_url", String.class);



        saveImage(imageurl);
        loadImage(imageurl);
    }


    @Override
    public void permissionGranted(int requestCodeForCallbackIdentificationCode, int requestCodeForCallbackIdentificationCodeSubDivision) {
        pickProfileImg();

    }

    @Override
    public void permissionDenied(int requestCodeForCallbackIdentificationCode, int requestCodeForCallbackIdentificationCodeSubDivision) {

    }
}
