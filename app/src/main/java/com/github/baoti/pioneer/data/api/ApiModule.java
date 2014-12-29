package com.github.baoti.pioneer.data.api;

import com.squareup.okhttp.OkHttpClient;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import retrofit.Endpoint;
import retrofit.Endpoints;
import retrofit.RestAdapter;
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
    RestAdapter provideRestAdapter(Endpoint endpoint, Client client) {
        return new RestAdapter.Builder() //
                .setClient(client) //
                .setEndpoint(endpoint) //
                .build();
    }

    @Provides
    @Singleton
    AccountApi provideUserApi(RestAdapter restAdapter) {
        return restAdapter.create(AccountApi.class);
    }
}
