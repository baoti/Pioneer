package com.github.baoti.pioneer;

import android.app.Application;

import com.github.baoti.pioneer.app.AppModule;
import com.github.baoti.pioneer.biz.BizModule;
import com.github.baoti.pioneer.data.DataModule;
import com.github.baoti.pioneer.ui.UiModule;

import dagger.Module;
import dagger.Provides;

/**
 * Created by liuyedong on 14-12-22.
 */
@Module(
        library = true,
        injects = {},
        includes = {
                AppModule.class,
                UiModule.class,
                BizModule.class,
                DataModule.class
        }
)
public class AppMainModule {
    private final AppMain appMain;

    public AppMainModule(AppMain appMain) {
        this.appMain = appMain;
    }

    @Provides
    public Application provideApplication() {
        return appMain;
    }
}
