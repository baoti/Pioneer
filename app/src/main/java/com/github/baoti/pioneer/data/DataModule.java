package com.github.baoti.pioneer.data;

import android.app.Application;
import android.content.Context;
import android.net.Uri;

import com.github.baoti.pioneer.BusProvider;
import com.github.baoti.pioneer.app.ForApp;
import com.github.baoti.pioneer.data.api.ApiModule;
import com.github.baoti.pioneer.data.prefs.AccountPrefs;
import com.squareup.okhttp.Cache;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.picasso.OkHttpDownloader;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.IOException;

import javax.inject.Named;
import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import timber.log.Timber;

/**
 * Created by liuyedong on 14-12-19.
 */
@Module(
        library = true,
        complete = false,
        injects = {
                DataInitializer.class
        },
        includes = {
                ApiModule.class
        }
)
public class DataModule {
    static final int DISK_CACHE_SIZE = 50 * 1024 * 1024; // 50MB

    public DataModule() {
        super();
        BusProvider.APP_BUS.register(new DataInitializer(BusProvider.APP_BUS));
    }

    @Provides
    @Singleton
    AccountPrefs provideAccountPrefs(@ForApp Context context) {
        return new AccountPrefs(context);
    }

    @Provides
    @Named("cacheDir")
    File provideCache(@ForApp Context context) {
        return context.getCacheDir();
    }

    @Provides
    @Singleton
    OkHttpClient provideOkHttpClient(Application app) {
        return createOkHttpClient(app);
    }

    @Provides
    @Singleton
    Picasso providePicasso(Application app, OkHttpClient client) {
        return new Picasso.Builder(app)
                .downloader(new OkHttpDownloader(client))
                .listener(new Picasso.Listener() {
                    @Override public void onImageLoadFailed(Picasso picasso, Uri uri, Exception e) {
                        Timber.e(e, "Failed to load image: %s", uri);
                    }
                })
                .build();
    }

    static OkHttpClient createOkHttpClient(Application app) {
        OkHttpClient client = new OkHttpClient();

        // Install an HTTP cache in the application cache directory.
        try {
            File cacheDir = new File(app.getCacheDir(), "http");
            Cache cache = new Cache(cacheDir, DISK_CACHE_SIZE);
            client.setCache(cache);
        } catch (IOException e) {
            Timber.e(e, "Unable to install disk cache.");
        }

        return client;
    }
}
