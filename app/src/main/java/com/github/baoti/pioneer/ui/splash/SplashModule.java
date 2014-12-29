package com.github.baoti.pioneer.ui.splash;

import com.github.baoti.pioneer.AppMainModule;

import dagger.Module;

/**
 * Created by liuyedong on 14-12-19.
 */
@Module(
        addsTo = AppMainModule.class,
        injects = {
                SplashActivity.class
        }
)
public class SplashModule {
}
