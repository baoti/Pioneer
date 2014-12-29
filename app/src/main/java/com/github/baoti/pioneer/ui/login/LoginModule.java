package com.github.baoti.pioneer.ui.login;

import com.github.baoti.pioneer.AppMainModule;

import dagger.Module;

/**
 * Created by liuyedong on 14-12-18.
 */
@Module(
        injects = LoginViewDelegate.class,
        addsTo = AppMainModule.class
)
public class LoginModule {
}
