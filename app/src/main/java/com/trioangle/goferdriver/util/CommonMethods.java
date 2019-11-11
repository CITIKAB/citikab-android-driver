package com.trioangle.goferdriver.util;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityOptions;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.trioangle.goferdriver.MainActivity;
import com.trioangle.goferdriver.R;
import com.trioangle.goferdriver.custompalette.FontCache;
import com.trioangle.goferdriver.firebaseChat.FirebaseChatNotificationService;
import com.trioangle.goferdriver.helper.Constants;
import com.trioangle.goferdriver.helper.CustomDialog;
import com.trioangle.goferdriver.network.AppController;
import com.trioangle.goferdriver.signinsignup.SigninSignupHomeActivity;

import org.json.JSONObject;

import java.io.File;

/**
 * Created by trioangle on 9/7/18.
 */

public class CommonMethods {

    public static CustomDialog progressDialog;

    public CommonMethods() {
        AppController.getAppComponent().inject(this);
    }

    public static void gotoMainActivityFromChatActivity(Activity mActivity) {
        Intent mainActivityIntent = new Intent(mActivity, MainActivity.class);
        mainActivityIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        mActivity.startActivity(mainActivityIntent);

    }

    public Object getJsonValue(String jsonString, String key, Object object) {
        Object objct = object;
        try {
            JSONObject jsonObject = new JSONObject(jsonString);
            if (jsonObject.has(key)) objct = jsonObject.get(key);
        } catch (Exception e) {
            e.printStackTrace();
            return new Object();
        }
        return objct;
    }


    public void showProgressDialog(AppCompatActivity mActivity, CustomDialog customDialog) {
        if (mActivity == null || customDialog == null || (customDialog.getDialog() != null && customDialog.getDialog().isShowing()))
            return;
        progressDialog = new CustomDialog(true);
        progressDialog.show(mActivity.getSupportFragmentManager(), "");
    }

    public void hideProgressDialog() {
        if (progressDialog == null || progressDialog.getDialog() == null || !progressDialog.getDialog().isShowing())
            return;
        progressDialog.dismissAllowingStateLoss();
        progressDialog = null;
    }

    //Show Dialog with message
    public void showMessage(Context context, AlertDialog dialog, String msg) {
        if (context != null && dialog != null && !((Activity) context).isFinishing()) {
            dialog.setMessage(msg);
            dialog.show();
        }
    }

    //Create and Get Dialog
    public AlertDialog getAlertDialog(Context context) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setCancelable(true);
        builder.setPositiveButton(context.getResources().getString(R.string.ok), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        AlertDialog dialog = builder.create();
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE); //before
        return dialog;
    }


    public File cameraFilePath(Context context) {
        return new File(getDefaultCameraPath(context), context.getResources().getString(R.string.app_name) + System.currentTimeMillis() + ".png");
    }

    public String getDefaultCameraPath(Context context) {
        File path = new File(Environment.getExternalStorageDirectory() + "/" + context.getResources().getString(R.string.app_name));
        if (path.exists()) {
            File test1 = new File(path, "Camera/");
            if (test1.exists()) {
                path = test1;
            } else {
                File test2 = new File(path, "100MEDIA/");
                if (test2.exists()) {
                    path = test2;
                } else {
                    File test3 = new File(path, "100ANDRO/");
                    if (test3.exists()) {
                        path = test3;
                    } else {
                        test1.mkdirs();
                        path = test1;
                    }
                }
            }
        } else {
            path = new File(path, "Camera/");
            path.mkdirs();
        }
        return path.getPath();
    }

    public void refreshGallery(Context context, File file) {
        try {
            Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
            Uri contentUri = Uri.fromFile(file); //out is your file you saved/deleted/moved/copied
            mediaScanIntent.setData(contentUri);
            context.sendBroadcast(mediaScanIntent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    ////////////////// CAMERA ///////////////////////////////////
    public File getDefaultFileName(Context context) {
        File imageFile;
        Boolean isSDPresent = Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);
        if (isSDPresent) { // External storage path
            imageFile = new File(Environment.getExternalStorageDirectory() + File.separator + Constants.FILE_NAME + System.currentTimeMillis() + ".png");
        } else {  // Internal storage path
            imageFile = new File(context.getFilesDir() + File.separator + Constants.FILE_NAME + System.currentTimeMillis() + ".png");
        }
        return imageFile;
    }


    public boolean isOnline(Context context) {
        if (context == null) return false;
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = connectivityManager.getActiveNetworkInfo();
        return (netInfo != null && netInfo.isConnected());
    }

    public static String getAppVersionNameFromGradle(Context context) {
        String versionName;
        try {
            versionName = AppController.getContext().getPackageManager().getPackageInfo(AppController.getContext().getPackageName(), 0).versionName;
        } catch (PackageManager.NameNotFoundException e) {
            versionName = "0.0";
            e.printStackTrace();
        }
        return versionName;
    }

    public static String getAppPackageName() {
        String packageName;
        try {
            packageName = AppController.getContext().getPackageName();
        } catch (Exception e) {
            packageName = "";
            e.printStackTrace();
        }
        return packageName;
    }

    public static void DebuggableLogE(String tag, @Nullable String message) {
        if (CommonKeys.isLoggable) {
            Log.e(tag, message);
        }
    }

    public static void DebuggableLogE(String tag, @Nullable String message, Throwable tr) {
        if (CommonKeys.isLoggable) {
            Log.e(tag, message, tr);
        }
    }

    public static void DebuggableLogI(String tag, @Nullable String message) {
        if (CommonKeys.isLoggable) {
            Log.i(tag, message);
        }
    }

    public static void DebuggableLogD(String tag, @Nullable String message) {
        if (CommonKeys.isLoggable) {
            Log.d(tag, message);
        }
    }

    public static void DebuggableLogV(String tag, @Nullable String message) {
        if (CommonKeys.isLoggable) {
            Log.v(tag, message);
        }
    }

    public static void DebuggableToast(Context mContext, @Nullable String message) {
        if (CommonKeys.isLoggable) {
            Toast.makeText(mContext, message, Toast.LENGTH_SHORT).show();
        }
    }

    public static void showUserMessage(View view, Context mContext, @Nullable String message) {
        Snackbar snackbar = Snackbar.make(view, message, Snackbar.LENGTH_LONG);
        snackbar.show();
    }

    public static void showUserMessage(@Nullable String message) {
        try {
            if (!TextUtils.isEmpty(message)) {
                Toast.makeText(AppController.getContext(), message, Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void showServerInternalErrorMessage(Context context){
        showUserMessage(context.getResources().getString(R.string.internal_server_error));
    }

    public static void startFirebaseChatListenerService(Context mContext) {
        mContext.startService(new Intent(mContext, FirebaseChatNotificationService.class));
    }

    public static void stopFirebaseChatListenerService(Context mContext) {
        mContext.stopService(new Intent(mContext, FirebaseChatNotificationService.class));
    }

    public static boolean isMyBackgroundServiceRunning(Class<?> serviceClass, Context mContext) {
        ActivityManager manager = (ActivityManager) mContext.getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    public void snackBar(String message, String buttonmessage, boolean buttonvisible, int duration, EditText edt, TextView txt, Resources getRes, Activity ctx) {
        // Create the Snackbar
        Snackbar snackbar;
        RelativeLayout snackbar_background;
        TextView snack_button;
        TextView snack_message;
        // Snack bar visible duration
        if (duration == 1) snackbar = Snackbar.make(edt, "", Snackbar.LENGTH_INDEFINITE);
        else if (duration == 2)
            snackbar = Snackbar.make(edt, "", Snackbar.LENGTH_LONG);
        else snackbar = Snackbar.make(edt, "", Snackbar.LENGTH_SHORT);

        // Get the Snackbar's layout view
        Snackbar.SnackbarLayout layout = (Snackbar.SnackbarLayout) snackbar.getView();
        // Hide the text
        TextView textView = (TextView) layout.findViewById(android.support.design.R.id.snackbar_text);
        textView.setVisibility(View.INVISIBLE);


        // Inflate our custom view
        View snackView = ctx.getLayoutInflater().inflate(R.layout.snackbar, null);
        // Configure the view

        snackbar_background = (RelativeLayout) snackView.findViewById(R.id.snackbar);
        snack_button = (TextView) snackView.findViewById(R.id.snack_button);
        snack_message = (TextView) snackView.findViewById(R.id.snackbar_text);

        snackbar_background.setBackgroundColor(getRes.getColor(R.color.white)); // Background Color

        if (buttonvisible) // set Right side button visible or gone
            snack_button.setVisibility(View.VISIBLE);
        else snack_button.setVisibility(View.GONE);

        snack_button.setTextColor(getRes.getColor(R.color.app_background)); // set right side button text color
        snack_button.setText(buttonmessage); // set right side button text
        snack_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.out.println("onclikedmsg" + message);
                if (message.equals(getRes.getString(R.string.invalidelogin))) {
                    edt.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                    edt.setSelection(edt.length());
                    txt.setText(R.string.hide);
                } else if (message.equals(getRes.getString(R.string.emailalreadyexits))) {
                    System.out.println("oncliked");
                    Intent login = new Intent(ctx, SigninSignupHomeActivity.class);
                    login.putExtra("email", edt.getText().toString());
                    Bundle bndlanimation = ActivityOptions.makeCustomAnimation(ctx, R.anim.trans_left_in, R.anim.trans_left_out).toBundle();
                    ctx.startActivity(login, bndlanimation);
                }
            }
        });

        snack_message.setTextColor(getRes.getColor(R.color.app_background)); // set left side main message text color
        snack_message.setText(message);  // set left side main message text

// Add the view to the Snackbar's layout
        layout.addView(snackView, 0);
// Show the Snackbar
        View snackBarView = snackbar.getView();
        snackBarView.setBackgroundColor(getRes.getColor(R.color.white));
        snackbar.show();
        System.out.println("Snack bar ended");

    }

    public static TextView getCustomHeaderForAlert(Context mContext, String title) {
        TextView headerFontTextView = new TextView(mContext);
        headerFontTextView.setPadding(50, 50, 50, 10);
        headerFontTextView.setTextColor(mContext.getResources().getColor(R.color.ub__black));
        headerFontTextView.setText(title);
        headerFontTextView.setTextSize(20);
        headerFontTextView.setTypeface(FontCache.getTypeface(mContext.getResources().getString(R.string.fonts_UBERMedium), mContext), Typeface.BOLD);
        headerFontTextView.setGravity(Gravity.CENTER);


        return headerFontTextView;
    }

    public static void openPlayStore(Context context){
        final String appPackageName = getAppPackageName(); // getPackageName() from Context or Activity object
        try {
            Intent playstoreIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName));
            playstoreIntent.setPackage("com.android.vending");
            context.startActivity(playstoreIntent);
        } catch (android.content.ActivityNotFoundException anfe) {
            context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName)));
        }
    }

}

