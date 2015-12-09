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

package com.github.baoti.pioneer.data.api;

import com.github.baoti.pioneer.data.api.internal.FakeNewsApiImpl;
import com.squareup.okhttp.OkHttpClient;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import retrofit2.GsonConverterFactory;
import retrofit2.Retrofit;

/**
 * Created by liuyedong on 14-12-19.
 */
@Module(
        library = true,
        complete = false
)
public class ApiModule {
    public static final String PRODUCTION_API_URL = "http://api.github.com/3/";

    @Provides
    @Singleton
    Retrofit provideRetrofit(OkHttpClient client) {
        return new Retrofit.Builder() //
                .client(client) //
                .baseUrl(PRODUCTION_API_URL) //
                .addConverterFactory(GsonConverterFactory.create())
                .build();
    }

    @Provides
    @Singleton
    AccountApi provideUserApi(Retrofit retrofit) {
        return retrofit.create(AccountApi.class);
    }

    @Provides
    @Singleton
    NewsApi provideNewsApi() {
        return new FakeNewsApiImpl();
    }
}
