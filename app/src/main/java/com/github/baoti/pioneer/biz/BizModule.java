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

package com.github.baoti.pioneer.biz;

import com.github.baoti.pioneer.app.ForApp;
import com.github.baoti.pioneer.biz.interactor.AccountInteractor;
import com.github.baoti.pioneer.biz.interactor.AccountInteractorImpl;
import com.github.baoti.pioneer.biz.interactor.AppInteractor;
import com.github.baoti.pioneer.biz.interactor.AppInteractorImpl;
import com.github.baoti.pioneer.biz.interactor.NewsInteractor;
import com.github.baoti.pioneer.biz.interactor.NewsInteractorImpl;
import com.github.baoti.pioneer.data.api.AccountApi;
import com.github.baoti.pioneer.data.api.NewsApi;
import com.github.baoti.pioneer.data.prefs.AccountPrefs;
import com.github.baoti.pioneer.event.EventPoster;
import com.squareup.otto.Bus;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

/**
 * Created by liuyedong on 14-12-18.
 */
@Module(
        library = true,
        complete = false
)
public class BizModule {
    @Provides
    @Singleton
    public AppInteractor provideAppInteractor(@ForApp Bus appBus) {
        return new AppInteractorImpl(appBus);
    }

    @Provides
    @Singleton
    public AccountInteractor provideAccountInteractor(
            EventPoster eventPoster, AccountApi accountApi, AccountPrefs accountPrefs) {
        return new AccountInteractorImpl(eventPoster, accountApi, accountPrefs);
    }

    @Provides
    @Singleton
    public NewsInteractor provideNewsInteractor(NewsApi newsApi) {
        return new NewsInteractorImpl(newsApi);
    }
}
