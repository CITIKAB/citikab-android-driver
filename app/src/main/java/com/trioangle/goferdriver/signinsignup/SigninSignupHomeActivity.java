package com.trioangle.goferdriver.signinsignup;

/**
 * @package com.trioangle.goferdriver.signinsignup
 * @subpackage signinsignup model
 * @category SigninSignupHomeActivity
 * @author Trioangle Product Team
 * @version 1.5
 */

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.annotation.RequiresApi;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.ViewCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Base64;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.messaging.FirebaseMessaging;
import com.trioangle.goferdriver.R;
import com.trioangle.goferdriver.configs.SessionManager;
import com.trioangle.goferdriver.facebookAccountKit.FacebookAccountKitActivity;
import com.trioangle.goferdriver.fragments.currency.CurrencyModel;
import com.trioangle.goferdriver.fragments.language.LanguageAdapter;
import com.trioangle.goferdriver.network.AppController;
import com.trioangle.goferdriver.pushnotification.Config;
import com.trioangle.goferdriver.pushnotification.NotificationUtils;
import com.trioangle.goferdriver.util.CommonKeys;
import com.trioangle.goferdriver.util.CommonMethods;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import javax.inject.Inject;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

import static com.trioangle.goferdriver.fragments.AccountFragment.langclick;
import static com.trioangle.goferdriver.util.CommonKeys.ACTIVITY_REQUEST_CODE_START_FACEBOOK_ACCOUNT_KIT;
import static com.trioangle.goferdriver.util.CommonKeys.FACEBOOK_ACCOUNT_KIT_MESSAGE_KEY;
import static com.trioangle.goferdriver.util.CommonKeys.FACEBOOK_ACCOUNT_KIT_PHONE_NUMBER_COUNTRY_CODE_KEY;
import static com.trioangle.goferdriver.util.CommonKeys.FACEBOOK_ACCOUNT_KIT_PHONE_NUMBER_KEY;

/* ************************************************************
                SigninSignupHomeActivity
Its used to show the signin and register screen to call the function
*************************************************************** */
public class SigninSignupHomeActivity extends AppCompatActivity {

    private static final String TAG = SigninSignupHomeActivity.class.getSimpleName();
    public static android.app.AlertDialog alertDialogStores;
    public AlertDialog dialog;
    public @Inject
    CommonMethods commonMethods;
    public @Inject
    SessionManager sessionManager;
    public @InjectView(R.id.signin)
    Button signin;
    public @InjectView(R.id.signup)
    Button signup;
    public @InjectView(R.id.looking)
    TextView looking;
    public @InjectView(R.id.languagetext)
    TextView LanguageText;
    public @InjectView(R.id.language)
    TextView language;
    public @InjectView(R.id.activity_signin_signup_home)
    RelativeLayout relativeLayout;
    public String token;
    public List<CurrencyModel> languagelist;
    public LanguageAdapter LanguageAdapter;
    public RecyclerView languageView;
    protected boolean isInternetAvailable;
    private BroadcastReceiver mRegistrationBroadcastReceiver;

    @OnClick(R.id.looking)
    public void looking() {
        Intent i = new Intent(Intent.ACTION_MAIN);
        PackageManager managerclock = getPackageManager();
        i = managerclock.getLaunchIntentForPackage(getResources().getString(R.string.package_rider));
        if (i == null) {
            // Open play store package link
            i = new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + getResources().getString(R.string.package_rider)));
            //Toast.makeText(this, "No Application Name", Toast.LENGTH_LONG).show();
        } else {
            // Open rider application
            i.addCategory(Intent.CATEGORY_LAUNCHER);

        }
        startActivity(i);

    }

    @OnClick(R.id.languagetext)
    public void languagetext() {
        languagelist();
        language.setClickable(false);
        LanguageText.setClickable(false);

    }

    @OnClick(R.id.signin)
    public void signin() {
        // Redirect to signin page
        if (sessionManager.getDeviceId() != null) {
            Intent signin = new Intent(getApplicationContext(), SigninActivity.class);
            startActivity(signin);
            overridePendingTransition(R.anim.ub__slide_in_right, R.anim.ub__slide_out_left);
            finish();
        } else {
            String refreshedToken = FirebaseInstanceId.getInstance().getToken();
            if (refreshedToken != null) {
                sessionManager.setDeviceId(refreshedToken);
                Intent intent = new Intent(getApplicationContext(), SigninActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.ub__slide_in_right, R.anim.ub__slide_out_left);
            } else {
                dialogfunction("Unable to get Device Id. Please try again later...");
            }
        }
    }

    @OnClick(R.id.signup)
    public void signUp() {
        //initAccountKitEmailFlow();
        // move to register activity

openFacebookAccountKit();

    }


    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signin_signup_home);

        ButterKnife.inject(this);
        AppController.getAppComponent().inject(this);

        AppEventsLogger.activateApp(this);
        FacebookSdk.sdkInitialize(this);


        dialog = commonMethods.getAlertDialog(this);

        getFbKeyHash(getApplicationContext().getResources().getString(R.string.package_driver));


        isInternetAvailable = commonMethods.isOnline(this);

        if (!isInternetAvailable) {
            dialogfunction(getResources().getString(R.string.turnoninternet));
        }

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);


        sessionManager.setType("driver");
        sessionManager.setDeviceType("2");

        //getLocalIpAddress();
        //getDeviceipWiFiData();
        //getIPFromWeb();

        final boolean isAttachedToWindow = ViewCompat.isAttachedToWindow(relativeLayout);

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            relativeLayout.setVisibility(View.VISIBLE);
            relativeLayout.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        if (isAttachedToWindow) {
                                            doCircularReveal();
                                        }
                                    }
                                }
            );
        }
        setLocale();

        /*
         *  Get notification message from broadcast
         */
        mRegistrationBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {

                // checking for type intent filter
                if (intent.getAction().equals(Config.REGISTRATION_COMPLETE)) {
                    // FCM successfully registered
                    // now subscribe to `global` topic to receive app wide notifications
                    FirebaseMessaging.getInstance().subscribeToTopic(Config.TOPIC_GLOBAL);

                    displayFirebaseRegId();

                }
            }
        };

        displayFirebaseRegId();
    }




    //Create FB KeyHash
    public void getFbKeyHash(String packageName) {

        PackageInfo info;
        try {
            info = getPackageManager().getPackageInfo(packageName, PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md;
                md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                String something = new String(Base64.encode(md.digest(), 0));
                //String something = new String(Base64.encodeBytes(md.digest()));
                System.out.println("hash key value" + something);
                Log.e("hash key", something);
            }
        } catch (PackageManager.NameNotFoundException e1) {
            Log.e("name not found", e1.toString());
        } catch (NoSuchAlgorithmException e) {
            Log.e("no such an algorithm", e.toString());
        } catch (Exception e) {
            Log.e("exception", e.toString());
        }

    }




    /*
     *  Get FCM ID
     */
    private void displayFirebaseRegId() {
        token = FirebaseInstanceId.getInstance().getToken();

        CommonMethods.DebuggableLogE(TAG, "Firebase reg id: " + token);

        if (!TextUtils.isEmpty(token)) {
            sessionManager.setDeviceId(token);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        String lan = sessionManager.getLanguage();

        if (lan != null) {
            language.setText(lan);
        }
        // register FCM registration complete receiver
        LocalBroadcastManager.getInstance(this).registerReceiver(mRegistrationBroadcastReceiver,
                new IntentFilter(Config.REGISTRATION_COMPLETE));

        // register new push message receiver
        // by doing this, the activity will be notified each time a new message arrives
        LocalBroadcastManager.getInstance(this).registerReceiver(mRegistrationBroadcastReceiver,
                new IntentFilter(Config.PUSH_NOTIFICATION));

        // clear the notification area when the app is opened
        NotificationUtils.clearNotifications(getApplicationContext());
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    public void onBackPressed() {
        finishAffinity();
        super.onBackPressed();       // bye

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            relativeLayout.setVisibility(View.VISIBLE);
            relativeLayout.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        doExitReveal();
                                    }
                                }
            );
        }
    }


    /**
     * Exit revel animation
     */
    public void doExitReveal() {


        // get the center for the clipping circle
        int centerX = (relativeLayout.getLeft() + relativeLayout.getRight()) / 2;
        int centerY = (relativeLayout.getTop() + relativeLayout.getBottom()) / 2;

        // get the initial radius for the clipping circle
        int initialRadius = relativeLayout.getWidth();

        // create the animation (the final radius is zero)
        Animator anim =
                null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            anim = ViewAnimationUtils.createCircularReveal(relativeLayout,
                    centerX, centerY, initialRadius, 0);
        }
        anim.setDuration(1000);
        // make the view invisible when the animation is done
        anim.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                relativeLayout.setVisibility(View.GONE);
            }
        });

        // start the animation
        anim.start();

    }

    /*
     *  Animate home page
     */
    private void doCircularReveal() {

        // get the center for the clipping circle
        int centerX = (relativeLayout.getLeft() + relativeLayout.getRight()) / 2;
        int centerY = (relativeLayout.getTop() + relativeLayout.getBottom()) / 2;

        int startRadius = 0;
        // get the final radius for the clipping circle
        int endRadius = Math.max(relativeLayout.getWidth(), relativeLayout.getHeight());

        // create the animator for this view (the start radius is zero)
        Animator anim =
                null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            anim = ViewAnimationUtils.createCircularReveal(relativeLayout,
                    centerX, centerY, startRadius, endRadius);
        }
        anim.setDuration(1500);
        // make the view invisible when the animation is done
        anim.addListener(new AnimatorListenerAdapter() {
            public void onAnimationStart(Animator animation) {

            }

            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
            }
        });


        anim.start();
    }

    public void dialogfunction(String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(message)
                .setCancelable(false)
                .setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();
                    }
                });

        AlertDialog alert = builder.create();
        alert.show();
    }


    public void languagelist() {

        languageView = new RecyclerView(this);
        languagelist = new ArrayList<>();
        loadlang();

        LanguageAdapter = new LanguageAdapter(this, languagelist);
        languageView.setLayoutManager(new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.VERTICAL, false));
        languageView.setAdapter(LanguageAdapter);

        LayoutInflater inflater = getLayoutInflater();
        View view = inflater.inflate(R.layout.currency_header, null);
        TextView T = (TextView) view.findViewById(R.id.header);
        T.setText(getString(R.string.selectlanguage));
        alertDialogStores = new android.app.AlertDialog.Builder(SigninSignupHomeActivity.this)
                .setCustomTitle(view)
                .setView(languageView)
                .setCancelable(true)
                .show();
        language.setClickable(true);
        LanguageText.setClickable(true);

        alertDialogStores.setOnDismissListener(new DialogInterface.OnDismissListener() {

            @Override
            public void onDismiss(DialogInterface dialog) {
                // TODO Auto-generated method stub
                if (langclick) {
                    langclick = false;
                    String langocde = sessionManager.getLanguageCode();
                    String lang = sessionManager.getLanguage();
                    language.setText(lang);
                    //new UpdateLanguage().execute();
                    setLocale(langocde);
                    recreate();
                    Intent intent = new Intent(SigninSignupHomeActivity.this, SigninSignupHomeActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                }
                LanguageText.setClickable(true);

            }
        });
    }

    public void loadlang() {

        String[] languages;
        String[] langCode;
        languages = getResources().getStringArray(R.array.language);
        langCode = getResources().getStringArray(R.array.languageCode);
        for (int i = 0; i < languages.length; i++) {
            CurrencyModel listdata = new CurrencyModel();
            listdata.setCurrencyName(languages[i]);
            listdata.setCurrencySymbol(langCode[i]);
            languagelist.add(listdata);

        }
    }

    public void setLocale(String lang) {
        Locale myLocale = new Locale(lang);
        Resources res = getResources();
        DisplayMetrics dm = res.getDisplayMetrics();
        Configuration conf = res.getConfiguration();
        conf.setLocale(myLocale);
        conf.locale = myLocale;
        res.updateConfiguration(conf, dm);

    }


    public void setLocale() {
        String lang = sessionManager.getLanguage();
        if (!lang.equals("")) {
            String langC = sessionManager.getLanguageCode();
            Locale locale = new Locale(langC);
            Locale.setDefault(locale);
            Configuration config = new Configuration();
            config.locale = locale;
            SigninSignupHomeActivity.this.getResources().updateConfiguration(config, SigninSignupHomeActivity.this.getResources().getDisplayMetrics());
        } else {
            sessionManager.setLanguage("English");
            sessionManager.setLanguageCode("en");
            setLocale();
            recreate();
            Intent intent = new Intent(SigninSignupHomeActivity.this, SigninSignupHomeActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        }


    }


    public void openFacebookAccountKit(){

        FacebookAccountKitActivity.openFacebookAccountKitActivity(this);
    }

    public void openRegisterActivity(String phoneNumber, String countryCode){
        Intent signin = new Intent(getApplicationContext(), Register.class);
        signin.putExtra(FACEBOOK_ACCOUNT_KIT_PHONE_NUMBER_KEY,phoneNumber);
        signin.putExtra(FACEBOOK_ACCOUNT_KIT_PHONE_NUMBER_COUNTRY_CODE_KEY,countryCode);
        startActivity(signin);
        overridePendingTransition(R.anim.ub__slide_in_right, R.anim.ub__slide_out_left);
        finish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == ACTIVITY_REQUEST_CODE_START_FACEBOOK_ACCOUNT_KIT && resultCode == RESULT_OK){
            /*if(resultCode == CommonKeys.FACEBOOK_ACCOUNT_KIT_RESULT_NEW_USER){
                openRegisterActivity(data.getStringExtra(FACEBOOK_ACCOUNT_KIT_PHONE_NUMBER_KEY),data.getStringExtra(FACEBOOK_ACCOUNT_KIT_PHONE_NUMBER_COUNTRY_CODE_KEY));
            }else if (resultCode == CommonKeys.FACEBOOK_ACCOUNT_KIT_RESULT_OLD_USER){
                commonMethods.showMessage(this, dialog, data.getStringExtra(FACEBOOK_ACCOUNT_KIT_MESSAGE_KEY));

            }*/

            openRegisterActivity(data.getStringExtra(FACEBOOK_ACCOUNT_KIT_PHONE_NUMBER_KEY),data.getStringExtra(FACEBOOK_ACCOUNT_KIT_PHONE_NUMBER_COUNTRY_CODE_KEY));
        }
    }
}
