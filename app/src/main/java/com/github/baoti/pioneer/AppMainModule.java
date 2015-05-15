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
