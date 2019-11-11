package com.trioangle.goferdriver.util;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.trioangle.goferdriver.BuildConfig;
import com.trioangle.goferdriver.R;

import java.util.Objects;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

/*
 * Created by: umasankar
 * description: this class will handle all runtime permission in single dialog fragment*/
public class RuntimePermissionDialogFragment extends DialogFragment {

    /*if you need to add permission
     * first add in permission list and then add icon list*/

    //    Permissions List
    public static final String
            CAMERA_PERMISSION = Manifest.permission.CAMERA,
            WRITE_EXTERNAL_STORAGE_PERMISSION = Manifest.permission.WRITE_EXTERNAL_STORAGE,
            LOCATION_PERMISSION = Manifest.permission.ACCESS_FINE_LOCATION,
            CONTACT_PERMISSION = Manifest.permission.READ_CONTACTS,
            DEFAULT_PERMISSION_CODE = Manifest.permission.INTERNET;

    // camera permission Array list

    public static final String [] CAMERA_PERMISSION_ARRAY  = new String[]{CAMERA_PERMISSION,WRITE_EXTERNAL_STORAGE_PERMISSION};
    public static final String [] LOCATION_AND_WRITEPERMISSION_ARRAY = new String[]{LOCATION_PERMISSION,WRITE_EXTERNAL_STORAGE_PERMISSION};
    public static final String [] STORAGEPERMISSIONARRAY = new String[]{WRITE_EXTERNAL_STORAGE_PERMISSION};


    //    icon Lists
    public final int
            cameraIcon = android.R.drawable.ic_menu_camera,
            locationIcon = android.R.drawable.ic_menu_mylocation,
            storageIcon = android.R.drawable.ic_menu_gallery,
            contactIcon = android.R.drawable.ic_menu_call;
    public final int defaultIcon = cameraIcon;
    //public final int storageIcon = android.R.drawable.sym_contact_card; // this may be used as alternative icon for SDcard access


    // request Codes For Callback Identifications
    public static final int
            cameraAndGallaryCallBackCode = 0,
            externalStoreageCallbackCode = 1,
            locationCallbackCode = 2,
            contactCallbackCode = 3;

    private int requestCodeForCallbackIdentificationSubDivision;


    //butterknife view binds
    public @InjectView(R.id.imgv_df_permissionImage)
    ImageView permissionTypeImage;
    public @InjectView(R.id.tv_df_permissionAllow)
    TextView permissionAllow;
    public @InjectView(R.id.tv_df_permissionNotAllow)
    TextView permissionNotAllow;
    public @InjectView(R.id.tv_df_permissionDescription)
    TextView tv_permissionDescription;


    private final int PERMISSION_REQUEST_CODE = 11;
    private String[] permissionsRequestFor = null;
    private Context mContext;
    private RuntimePermissionRequestedCallback callbackListener;
    private int requestCodeForCallbackIdentification = 0, permissionIcon; // default 0
    private String permissionDescription;


    // this variable is declared to handle the allow Textview onClick process Dynamically,
    // if true -> it will request permission,
    // else open settings page to grand permission by user manually
    protected boolean ableToRequestPermission = true;

    public RuntimePermissionDialogFragment() {
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.mContext = context;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.popup_permission_helper, container, false);
        setCancelable(false);
        Objects.requireNonNull(getDialog().getWindow()).setBackgroundDrawableResource(android.R.color.transparent);
        ButterKnife.inject(this, rootView);
        setImageResourceAndPermissionDescriptionForPopup();
        return rootView;
    }

    @OnClick(R.id.tv_df_permissionNotAllow)
    public void notAllowPermission() {
        afterPermissionDenied();
    }

    @OnClick(R.id.tv_df_permissionAllow)
    public void allowPermission() {
        if (ableToRequestPermission) {
            requestNecessaryPermissions();
        } else {
            mContext.startActivity(new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, Uri.parse("package:" + BuildConfig.APPLICATION_ID)));
            dismiss();
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mContext = null;
        callbackListener = null;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {

        if (permissions.length != 0 && grantResults.length != 0) {
            boolean isAnyPermissionDenied = false;
            for (int i = 0; i < permissions.length; i++) {
                String permission = permissions[i];
                int grantResult = grantResults[i];

                if (!shouldShowRequestPermissionRationale(permission) && grantResult != PackageManager.PERMISSION_GRANTED) {
                    notAbleToRequestPermission();
                    return;
                } else if (grantResult != PackageManager.PERMISSION_GRANTED) {
                    afterPermissionDenied();
                    return;
                }
            }
            callbackListener.permissionGranted(requestCodeForCallbackIdentification, requestCodeForCallbackIdentificationSubDivision);
            dismiss();

        } else {
            Toast.makeText(mContext, "permission size 0", Toast.LENGTH_SHORT).show();
        }
    }

    public void setImageResourceAndPermissionDescriptionForPopup() {
        getPermissionRequestedForIconAndDescription();
        permissionTypeImage.setImageResource(permissionIcon);
        tv_permissionDescription.setText(permissionDescription);
    }

    public void getPermissionRequestedForIconAndDescription() {
        switch (permissionsRequestFor[0]) {
            case CAMERA_PERMISSION:
                permissionIcon = cameraIcon;
                permissionDescription = mContext.getResources().getString(R.string.camera_permission_description);
                break;
            case WRITE_EXTERNAL_STORAGE_PERMISSION:
                permissionIcon = storageIcon;
                permissionDescription = mContext.getResources().getString(R.string.storage_permission_description);
                break;
            case LOCATION_PERMISSION:
                permissionIcon = locationIcon;
                permissionDescription = mContext.getResources().getString(R.string.location_permission_description);
                break;
            default:
                permissionIcon = cameraIcon;
                permissionDescription = mContext.getResources().getString(R.string.camera_permission_description);
                break;
        }
    }

    public static void checkPermissionStatus(Context mContext, FragmentManager fragmentManager, RuntimePermissionRequestedCallback callbackListener, String[] permissionsRequestFor, int requestCodeForCallbackIdentification, int requestCodeForCallbackIdentificationSubDivision) {

        /*
         * here function check permission status and then checks shouldAskPermissionForThisAndroidOSVersion or not
         * because some custom phone below Android M request permission from user at Run time example: redmi phones*/

        Boolean allPermissionGranted = true;

        for (String permissionRequestFor : permissionsRequestFor) {
            if (checkSelfPermissions(mContext, permissionRequestFor)) {
                allPermissionGranted = false;
                break;
            }
        }
        if (!allPermissionGranted) {
            if (shouldAskPermissionForThisAndroidOSVersion()) {
                RuntimePermissionDialogFragment runtimePermissionDialogFragment = new RuntimePermissionDialogFragment();
                runtimePermissionDialogFragment.permissionsRequestFor = permissionsRequestFor;
                runtimePermissionDialogFragment.callbackListener = callbackListener;
                runtimePermissionDialogFragment.requestCodeForCallbackIdentification = requestCodeForCallbackIdentification;
                runtimePermissionDialogFragment.requestCodeForCallbackIdentificationSubDivision = requestCodeForCallbackIdentificationSubDivision;
                runtimePermissionDialogFragment.mContext = mContext;
                runtimePermissionDialogFragment.show(fragmentManager, RuntimePermissionDialogFragment.class.getName());
            } else {
//                we write code here becoz of static method, it not static method we call afterPermissionDenied()
                callbackListener.permissionDenied(requestCodeForCallbackIdentification, requestCodeForCallbackIdentificationSubDivision);
                Toast.makeText(mContext, mContext.getResources().getString(R.string.enable_permissions_to_proceed_further), Toast.LENGTH_SHORT).show();

            }
        } else {
            callbackListener.permissionGranted(requestCodeForCallbackIdentification, requestCodeForCallbackIdentificationSubDivision);
        }
    }

    public static boolean checkSelfPermissions(Context mContext, String permissionRequestFor) {
        return (ContextCompat.checkSelfPermission(mContext, permissionRequestFor) != PackageManager.PERMISSION_GRANTED);
    }

    public static boolean shouldAskPermissionForThisAndroidOSVersion() {
        return (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M);
    }

    private void requestNecessaryPermissions() {
        ableToRequestPermission = true;
        requestPermissions(permissionsRequestFor, PERMISSION_REQUEST_CODE);
    }

    private void notAbleToRequestPermission() {
        permissionAllow.setText(mContext.getResources().getString(R.string.settings));
        ableToRequestPermission = false;

    }

    private void afterPermissionDenied() {
        showPermissionDeniedMessageToUser();
        callbackListener.permissionDenied(requestCodeForCallbackIdentification, requestCodeForCallbackIdentificationSubDivision);
        dismiss();
    }

    public void showPermissionDeniedMessageToUser() {
        Toast.makeText(mContext, mContext.getResources().getString(R.string.enable_permissions_to_proceed_further), Toast.LENGTH_SHORT).show();

    }

    public interface RuntimePermissionRequestedCallback {
        void permissionGranted(int requestCodeForCallbackIdentificationCode, int requestCodeForCallbackIdentificationCodeSubDivision);

        void permissionDenied(int requestCodeForCallbackIdentificationCode, int requestCodeForCallbackIdentificationCodeSubDivision);
    }
}
