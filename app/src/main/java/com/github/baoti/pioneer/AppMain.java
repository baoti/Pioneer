/*
 * Copyright (c) 2014-2015 Sean Liu.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.github.baoti.pioneer;

import android.app.Application;

import timber.log.Timber;

/**
 * Created by liuyedong on 14-12-18.
 */
public class AppMain extends Application {

    private static AppMain app;

    public static AppMain app() {
        return app;
    }

    private AppMainComponent component;

    @Override
    public void onCreate() {
        app = this;

        super.onCreate();

        component = DaggerAppMainComponent.builder().appMainModule(new AppMainModule(this)).build();

        if (BuildConfig.DEBUG) {
            Timber.plant(new Timber.DebugTree());
        }
    }

    public static AppMainComponent component() {
        return app().component;
    }
}
