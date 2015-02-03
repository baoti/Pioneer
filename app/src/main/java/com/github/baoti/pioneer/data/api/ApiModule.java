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
import retrofit.Endpoint;
import retrofit.Endpoints;
import retrofit.ErrorHandler;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Client;
import retrofit.client.OkClient;

/**
 * Created by liuyedong on 14-12-19.
 */
@Module(
        library = true,
        complete = false
)
public class ApiModule {
    public static final String PRODUCTION_API_URL = "http://api.github.com/3/";

    @Provides @Singleton
    Endpoint provideEndpoint() {
        return Endpoints.newFixedEndpoint(PRODUCTION_API_URL);
    }

    @Provides @Singleton
    Client provideClient(OkHttpClient client) {
        return new OkClient(client);
    }

    @Provides @Singleton
    ErrorHandler provideErrorHandler() {
        return new ErrorHandler() {
            @Override
            public Throwable handleError(RetrofitError cause) {
                return new ApiException(cause);
            }
        };
    }

    @Provides @Singleton
    RestAdapter provideRestAdapter(Endpoint endpoint, Client client, ErrorHandler errorHandler) {
        return new RestAdapter.Builder() //
                .setClient(client) //
                .setEndpoint(endpoint) //
                .setErrorHandler(errorHandler)
                .build();
    }

    @Provides
    @Singleton
    AccountApi provideUserApi(RestAdapter restAdapter) {
        return restAdapter.create(AccountApi.class);
    }

    @Provides
    @Singleton
    NewsApi provideNewsApi() {
        return new FakeNewsApiImpl();
    }
}
