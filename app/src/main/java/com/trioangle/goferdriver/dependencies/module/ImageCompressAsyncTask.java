package com.trioangle.goferdriver.dependencies.module;
/**
 * @package com.trioangle.com.trioangle.goferdriver
 * @subpackage backgroundtask
 * @category ImageCompressAsyncTask
 * @author Trioangle Product Team
 * @version 1.0
 **/

import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;

import com.trioangle.goferdriver.configs.SessionManager;
import com.trioangle.goferdriver.interfaces.ImageListener;
import com.trioangle.goferdriver.network.AppController;

import java.io.File;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import javax.inject.Inject;

import id.zelory.compressor.Compressor;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

/*****************************************************************
 Compress image while upload in background
 ****************************************************************/
public class ImageCompressAsyncTask extends AsyncTask<Void, Void, Void> {
    public @Inject
    SessionManager sessionManager;
    private String filePath = "", compressPath = "";
    private WeakReference<AppCompatActivity> compressImgWeakRef;
    private RequestBody requestBody;
    private ImageListener imageListener;
    private int type = 0;
    private String docType = "";
    private String uploadType = "image";

    public ImageCompressAsyncTask(int type, AppCompatActivity activity, String filePath, ImageListener imageListener) {
        AppController.getAppComponent().inject(this);
        this.compressImgWeakRef = new WeakReference<>(activity);
        this.filePath = filePath;
        this.imageListener = imageListener;
        this.type = type;
    }

    /**
     * Call when before call the WS.
     */
    @Override
    protected void onPreExecute() {
        if (this.compressImgWeakRef == null) {
            this.cancel(true);
        }
    }

    /**
     * action to be performed in background
     */
    @Override
    protected Void doInBackground(Void... params) {
        try {
            if (filePath != null) {
                File file = new File(filePath);
                if (file.exists()) {
                    publishProgress();
                    file = new Compressor.Builder(this.compressImgWeakRef.get()).setMaxWidth(1080).setMaxHeight(1920).setQuality(75).setCompressFormat(Bitmap.CompressFormat.JPEG).build().compressToFile(file);
                    compressPath = file.getPath();
                    /**
                     * @param type  1 for licence back,
                     *              2 for licence front,
                     *              3 for insurance
                     *              4 for registeration
                     *              5 for carriage
                     */
                    if (type == 1) docType = "license_back";
                    else if (type == 2) docType = "license_front";
                    else if (type == 3) docType = "insurance";
                    else if (type == 4) docType = "rc";
                    else if (type == 5) docType = "permit";
                    else if (type == 6) {
                        docType = "profile_image";
                        uploadType = "image";
                    } else if (type == 7) {
                        docType = "document";
                        uploadType = "document";
                    } else {
                        docType = "image";
                        uploadType = "image";
                    }


                    requestBody = uploadImgParam(compressPath, docType, uploadType);
                }
            }
        } catch (OutOfMemoryError | Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    /**
     * called after the WS return the response.
     */
    @Override
    protected void onPostExecute(Void value) {
        if (compressImgWeakRef != null && compressImgWeakRef.get() != null && requestBody != null) {
            imageListener.onImageCompress(compressPath, requestBody);
        } else {
            imageListener.onImageCompress(compressPath, null);
        }
    }


    /**
     * To upload image
     * @param imagePath path of the image
     * @param docType type  of the document
     * @param uploadType typr of upload
     * @return returns of the type request body
     * @throws IOException
     */


    public RequestBody uploadImgParam(String imagePath, String docType, String uploadType) throws IOException {
        MultipartBody.Builder multipartBody = new MultipartBody.Builder();
        multipartBody.setType(MultipartBody.FORM);
        File file = null;
        try {


            file = new File(imagePath);
            String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
            multipartBody.addFormDataPart(uploadType, "IMG_" + timeStamp + ".jpg", RequestBody.create(MediaType.parse("image/png"), file));
            multipartBody.addFormDataPart("token", sessionManager.getAccessToken());

            multipartBody.addFormDataPart("document_type", docType);

        } catch (Exception e) {
            e.printStackTrace();
        }
        RequestBody formBody = multipartBody.build();
        return formBody;
    }
}
