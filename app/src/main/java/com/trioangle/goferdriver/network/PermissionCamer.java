package com.trioangle.goferdriver.network;
/**
 * @package com.trioangle.goferdriver.network
 * @subpackage network
 * @category PermissionCamer
 * @author Trioangle Product Team
 * @version 1.5
 */

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;


/* ************************************************************
Whole application to get the permission
*************************************************************** */

public class PermissionCamer {

    /*
    *  Check permission
    */
    public static boolean checkPermission(final Context context) {
        return ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED;
    }



}
