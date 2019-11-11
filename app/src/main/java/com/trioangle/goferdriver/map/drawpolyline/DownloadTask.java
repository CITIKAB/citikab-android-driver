package com.trioangle.goferdriver.map.drawpolyline;
/**
 * @package com.trioangle.goferdriver
 * @subpackage map.drawpolyline
 * @category DownloadTask
 * @author Trioangle Product Team
 * @version 1.5
 */

import android.content.Context;
import android.os.AsyncTask;
import android.support.v7.app.AlertDialog;
import android.util.Log;

import com.trioangle.goferdriver.R;
import com.trioangle.goferdriver.network.AppController;
import com.trioangle.goferdriver.util.CommonMethods;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import javax.inject.Inject;

public class DownloadTask extends AsyncTask<String, Void, String> {

    PolylineOptionsInterface polylineOptionsInterface = null;
    Context mContext;
    public @Inject
    CommonMethods commonMethods;
    public AlertDialog dialog;

    public DownloadTask(PolylineOptionsInterface polylineOptionsInterface, Context mContext) {
        this.polylineOptionsInterface = polylineOptionsInterface;
        this.mContext = mContext;
        AppController.getAppComponent().inject(this);
        dialog = commonMethods.getAlertDialog(mContext);
    }

    @Override
    protected String doInBackground(String... url) {

        String data = "";

        try {
            if (commonMethods.isOnline(mContext)){
                data = downloadUrl(url[0]);
            }else {
                //commonMethods.showMessage(mContext, dialog, mContext.getResources().getString(R.string.no_connection));
            }


        } catch (Exception e) {
            CommonMethods.DebuggableLogD("Background Task", e.toString());
        }
        return data;
    }

    @Override
    protected void onPostExecute(String result) {
        super.onPostExecute(result);

        ParserTask parserTask = new ParserTask(this.polylineOptionsInterface,mContext);

        if (commonMethods.isOnline(mContext)){
            parserTask.execute(result);
        }else {
            //commonMethods.showMessage(mContext, dialog, mContext.getResources().getString(R.string.no_connection));
        }

    }

    /**
     * A method to download json data from url
     */
    private String downloadUrl(String strUrl) throws IOException {
        String data = "";
        InputStream iStream = null;
        HttpURLConnection urlConnection = null;
        try {
            URL url = new URL(strUrl);

            urlConnection = (HttpURLConnection) url.openConnection();

            urlConnection.connect();

            iStream = urlConnection.getInputStream();

            BufferedReader br = new BufferedReader(new InputStreamReader(iStream));

            StringBuffer sb = new StringBuffer();

            String line = "";
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }

            data = sb.toString();

            br.close();

        } catch (Exception e) {
            CommonMethods.DebuggableLogD("Exception", e.toString());
        } finally {
            iStream.close();
            urlConnection.disconnect();
        }
        return data;
    }
}