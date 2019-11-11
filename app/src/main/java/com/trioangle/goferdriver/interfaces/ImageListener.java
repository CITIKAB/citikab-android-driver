package com.trioangle.goferdriver.interfaces;

import okhttp3.RequestBody;

/**
 * Created by trioangle on 9/7/18.
 */

public interface ImageListener {
    void onImageCompress(String filePath, RequestBody requestBody);
}

