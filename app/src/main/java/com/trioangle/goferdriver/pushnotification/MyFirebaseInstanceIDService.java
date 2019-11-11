package com.trioangle.goferdriver.pushnotification;
/**
 * @package com.trioangle.goferdriver.pushnotification
 * @subpackage pushnotification model
 * @category MyFirebaseInstanceIDService
 * @author Trioangle Product Team
 * @version 1.5
 */

import android.content.SharedPreferences;
import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;
import com.trioangle.goferdriver.configs.SessionManager;
import com.trioangle.goferdriver.interfaces.ApiService;
import com.trioangle.goferdriver.interfaces.ServiceListener;
import com.trioangle.goferdriver.model.JsonResponse;
import com.trioangle.goferdriver.network.AppController;
import com.trioangle.goferdriver.util.CommonMethods;
import com.trioangle.goferdriver.util.RequestCallback;

import java.util.HashMap;

import javax.inject.Inject;

/* ************************************************************
                MyFirebaseInstanceIDService
Its used to get the push notification FirebaseInstanceIDService function
*************************************************************** */
public class MyFirebaseInstanceIDService extends FirebaseInstanceIdService implements ServiceListener {

    private static final String TAG = MyFirebaseInstanceIDService.class.getSimpleName();
    public @Inject
    ApiService apiService;
    public @Inject
    SessionManager sessionManager;

    @Override
    public void onTokenRefresh() {
        super.onTokenRefresh();
        String refreshedToken = FirebaseInstanceId.getInstance().getToken();
        AppController.getAppComponent().inject(this);
        // Saving reg id to shared preferences
        storeRegIdInPref(refreshedToken);

        // sending reg id to your server
        sendRegistrationToServer(refreshedToken);

    }

    private void sendRegistrationToServer(final String token) {
        // sending FCM token to server
        CommonMethods.DebuggableLogE(TAG, "sendRegistrationToServer: " + token);
        sessionManager.setDeviceId(token);

        if (sessionManager.getAccessToken() != null) {
            updateDeviceId();
        }
    }

    /*
    * Update driver device id
    */
    private void storeRegIdInPref(String token) {
        SharedPreferences pref = getApplicationContext().getSharedPreferences(Config.SHARED_PREF, 0);
        SharedPreferences.Editor editor = pref.edit();
        editor.putString("regId", token);
        editor.commit();
    }

    public HashMap<String, String> getDeviceId() {
        HashMap<String, String> driverStatusHashMap = new HashMap<>();
        driverStatusHashMap.put("user_type", sessionManager.getType());
        driverStatusHashMap.put("device_type", sessionManager.getDeviceType());
        driverStatusHashMap.put("device_id", sessionManager.getDeviceId());
        driverStatusHashMap.put("token", sessionManager.getAccessToken());
        return driverStatusHashMap;
    }

    public void updateDeviceId() {
        if (sessionManager.getAccessToken() != null && sessionManager.getDeviceId() != null) {
            apiService.updateDevice(getDeviceId()).enqueue(new RequestCallback(this));
        }
    }

    @Override
    public void onSuccess(JsonResponse jsonResp, String data) {

    }

    @Override
    public void onFailure(JsonResponse jsonResp, String data) {

    }

}

