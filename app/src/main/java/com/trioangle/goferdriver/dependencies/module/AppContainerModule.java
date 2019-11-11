package com.trioangle.goferdriver.dependencies.module;
/**
 * @package com.trioangle.com.trioangle.goferdriver
 * @subpackage dependencies.module
 * @category AppContainerModule
 * @author Trioangle Product Team
 * @version 1.0
 **/

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;

import com.trioangle.goferdriver.configs.SessionManager;
import com.trioangle.goferdriver.helper.Constants;
import com.trioangle.goferdriver.helper.CustomDialog;
import com.trioangle.goferdriver.helper.RunTimePermission;
import com.trioangle.goferdriver.model.JsonResponse;

import net.hockeyapp.android.utils.ImageUtils;

import java.util.ArrayList;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

/*****************************************************************
 App Container Module
 ****************************************************************/
@Module(includes = com.trioangle.goferdriver.dependencies.module.ApplicationModule.class)
public class AppContainerModule {
    @Provides
    @Singleton
    public SharedPreferences providesSharedPreferences(Application application) {
        return application.getSharedPreferences(Constants.APP_NAME, Context.MODE_PRIVATE);
    }


    @Provides
    @Singleton
    public Context providesContext(Application application) {
        return application.getApplicationContext();
    }

    @Provides
    @Singleton
    public SessionManager providesSessionManager() {
        return new SessionManager();
    }

    @Provides
    @Singleton
    public ArrayList<String> providesStringArrayList() {
        return new ArrayList<>();
    }

    @Provides
    @Singleton
    public JsonResponse providesJsonResponse() {
        return new JsonResponse();
    }

    @Provides
    @Singleton
    public RunTimePermission providesRunTimePermission() {
        return new RunTimePermission();
    }

    @Provides
    @Singleton
    CustomDialog providesCustomDialog() {
        return new CustomDialog();
    }

    @Provides
    @Singleton
    ImageUtils providesImageUtils() {
        return new ImageUtils();
    }


}
