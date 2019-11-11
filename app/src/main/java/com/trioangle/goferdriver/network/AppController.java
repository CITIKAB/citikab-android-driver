package com.trioangle.goferdriver.network;

/**
 * @package com.trioangle.goferdriver.network
 * @subpackage network
 * @category AppController
 * @author Trioangle Product Team
 * @version 1.5
 */

import android.app.Application;
import android.content.Context;
import android.support.multidex.MultiDex;
import android.support.v7.app.AppCompatDelegate;
import android.util.Log;

import com.trioangle.goferdriver.R;
import com.trioangle.goferdriver.dependencies.component.AppComponent;
import com.trioangle.goferdriver.dependencies.component.DaggerAppComponent;
import com.trioangle.goferdriver.dependencies.module.ApplicationModule;
import com.trioangle.goferdriver.dependencies.module.NetworkModule;
import com.trioangle.goferdriver.util.CommonKeys;
import com.trioangle.goferdriver.util.CommonMethods;

public class AppController extends Application {

    public static final String TAG = AppController.class.getSimpleName();
    private static AppController mInstance;
    private static AppComponent appComponent;

    public static AppComponent getAppComponent() {
        CommonMethods.DebuggableLogV("non", "null" + appComponent);
        return appComponent;
    }

    public static synchronized AppController getInstance() {
        return mInstance;
    }

    public static Context getContext(){
        return mInstance;
        // or return instance.getApplicationContext();
    }

    @Override
    public void onCreate() {
        super.onCreate();
        MultiDex.install(this);
        mInstance = this;
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);

        appComponent = DaggerAppComponent.builder().applicationModule(new ApplicationModule(this)) // This also corresponds to the name of your module: %component_name%Module
                    .networkModule(new NetworkModule(CommonKeys.apiBaseUrl)).build();
    }


    /*
    * Multidex enable
    */
    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }
}