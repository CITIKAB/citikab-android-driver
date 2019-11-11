package com.trioangle.goferdriver.interfaces;

import com.trioangle.goferdriver.model.JsonResponse;

/**
 * Created by trioangle on 9/7/18.
 */

public interface ServiceListener {

    void onSuccess(JsonResponse jsonResp, String data);

    void onFailure(JsonResponse jsonResp, String data);

    /*void onSuccess(JsonResponse jsonResp, String data);*/
}

