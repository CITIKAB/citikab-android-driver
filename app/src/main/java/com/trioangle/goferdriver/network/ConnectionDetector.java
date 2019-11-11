package com.trioangle.goferdriver.network;

/**
 * @package com.trioangle.goferdriver.network
 * @subpackage network
 * @category ConnectionDetector
 * @author Trioangle Product Team
 * @version 1.5
 */

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/*
* Check internet connection is available or not
*/
public class ConnectionDetector {

    private Context context;

    public ConnectionDetector(Context context) {
        this.context = context;
    }

    public boolean isConnectingToInternet() {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager != null) {
            NetworkInfo[] info = connectivityManager.getAllNetworkInfo();
            if (info != null) {
                //for (int i = 0; i < info.length; i++) {
                for (int i = 0; i < 3; i++) {
                    if (info[i].getState() == NetworkInfo.State.CONNECTED) {
                        return true;
                    }
                }
            }
        }
        return false;
    }
}