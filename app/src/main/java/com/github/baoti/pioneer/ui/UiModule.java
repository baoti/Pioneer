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

package com.github.baoti.pioneer.ui;

import android.os.Handler;
import android.os.Looper;

import com.github.baoti.pioneer.BusProvider;
import com.squareup.otto.Bus;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

/**
 * Created by liuyedong on 14-12-18.
 */
@Module(library = true)
public class UiModule {
    @Provides
    @Singleton
    @ForUi
    public Bus provideUiBus() {
        return BusProvider.UI_BUS;
    }

    @Provides
    @Singleton
    @ForUi
    public Handler provideUiHandler() {
        return new Handler(Looper.getMainLooper());
    }

}
