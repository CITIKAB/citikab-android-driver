package com.trioangle.goferdriver.dependencies.module;
/**
 * @package com.trioangle.gofereats
 * @subpackage dependencies.module
 * @category ApplicationModule
 * @author Trioangle Product Team
 * @version 1.0
 **/

import android.app.Application;

import com.trioangle.goferdriver.util.CommonMethods;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

/*****************************************************************
 Application Module
 ****************************************************************/
@Module
public class ApplicationModule {
    private final Application application;

    public ApplicationModule(Application application) {
        this.application = application;
    }

    @Provides
    @Singleton
    public Application application() {
        return application;
    }

    @Provides
    @Singleton
    public CommonMethods providesCommonMethods() {
        return new CommonMethods();
    }

   /* @Provides
    @Singleton
    public JsonResponse providesJsonResponse() {
        return new JsonResponse();
    }*/
}
