package com.trioangle.goferdriver.helper;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.v4.app.ActivityCompat;

import com.trioangle.goferdriver.network.AppController;

import java.util.ArrayList;

import javax.inject.Inject;

/**
 * Created by trioangle on 9/7/18.
 */

public class RunTimePermission {

    public @Inject
    Context context;
    public @Inject
    ArrayList<String> permissionList;

    private SharedPreferences preferences;

    public RunTimePermission() {
        AppController.getAppComponent().inject(this);
        preferences = context.getSharedPreferences("mcl_permission", Context.MODE_PRIVATE);
    }

    public boolean isFirstTimePermission() {
        return preferences.getBoolean("isFirstTimePermission", false);
    }

    public void setFirstTimePermission(boolean isFirstTime) {
        preferences.edit().putBoolean("isFirstTimePermission", isFirstTime).apply();
    }

    public String getFcmToken() {
        return preferences.getString("fcmToken", "");
    }

    public void setFcmToken(String fcmToken) {
        preferences.edit().putString("fcmToken", fcmToken).apply();
    }

    public ArrayList<String> checkHasPermission(Activity context, String[] permissions) {
        permissionList.clear();
        if (isMarshmallow() && context != null && permissions != null) {
            for (String permission : permissions) {
                if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                    permissionList.add(permission);
                }
            }
        }
        return permissionList;
    }

    /**
     * To check the permission is blocked or not
     * @param context of the activity it is used in
     * @param permissions array of permissions
     * @return returns boolean value
     */


    public boolean isPermissionBlocked(Activity context, String[] permissions) {
        if (isMarshmallow() && context != null && permissions != null) {//&& isFirstTimePermission()) {
            for (String permission : permissions) {
                if (!ActivityCompat.shouldShowRequestPermissionRationale(context, permission)) {
                    return true;
                }
            }
        }
        return false;
    }


    /**
     * To add list of permissions to be asked in the permissisonList
     * @param permissions array of permission to be requested to the user
     * @param grantResults to check wheather results are grant or not
     * @return array list
     */



    public ArrayList<String> onRequestPermissionsResult(String[] permissions, int[] grantResults) {
        permissionList.clear();
        if (grantResults != null && grantResults.length > 0) {
            for (int i = 0; i < permissions.length; i++) {
                if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                    permissionList.add(permissions[i]);
                }
            }
        }
        return permissionList;
    }

    private boolean isMarshmallow() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.M;
    }
}

