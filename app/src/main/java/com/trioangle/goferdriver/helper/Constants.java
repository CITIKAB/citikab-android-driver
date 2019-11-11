package com.trioangle.goferdriver.helper;

import android.Manifest;

/**
 * @author Trioangle Product Team
 * @version 1.5
 * @package com.trioangle.goferdriver.helper
 * @subpackage helper
 * @category Constants
 */

/* ************************************************************
Constants values for shared preferences
*************************************************************** */

public class Constants {

    public static final String APP_NAME = "Gofer Driver"; // App name
    public static final String FILE_NAME = "Gofer Driver";

    public static final int REQUEST_CODE_GALLERY = 5;

    public static final String STATUS_MSG = "status_message";
    public static final String STATUS_CODE = "status_code";
    public static final String REFRESH_ACCESS_TOKEN = "refresh_token";

    // Google place Search
    public static final String API_NOT_CONNECTED = "Google API not connected";  // Google API not connected
    public static final String SOMETHING_WENT_WRONG = "OOPs!!! Something went wrong...";
    public static final String PROFILEIMAGE = "image_url";
    public static final String[] PERMISSIONS_PHOTO = {Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE};
    public static long mLastClickTime = 0;
    public static String PlacesTag = "Google Places";  // Google place search tag
}
