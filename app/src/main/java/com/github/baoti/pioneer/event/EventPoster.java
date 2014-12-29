package com.github.baoti.pioneer.event;

import android.os.Handler;
import android.os.Looper;

import com.github.baoti.pioneer.BusProvider;
import com.github.baoti.pioneer.app.ForApp;
import com.github.baoti.pioneer.ui.ForUi;

import java.util.concurrent.Executor;

import javax.inject.Inject;
import javax.inject.Singleton;

/**
 * Created by liuyedong on 2014/12/27.
 */
@Singleton
public class EventPoster {
    private final Handler uiHandler;
    private final Executor appExecutor;

    @Inject
    public EventPoster(@ForUi Handler uiHandler, @ForApp Executor appExecutor) {
        this.uiHandler = uiHandler;
        this.appExecutor = appExecutor;
    }

    public static boolean isMainThread() {
        return Looper.myLooper() == Looper.getMainLooper();
    }

    public void postOnUi(final Object event) {
        if (isMainThread()) {
            BusProvider.UI_BUS.post(event);
        } else {
            uiHandler.post(new Runnable() {
                @Override
                public void run() {
                    BusProvider.UI_BUS.post(event);
                }
            });
        }
    }

    public void postOnApp(final Object event) {
        if (isMainThread()) {
            appExecutor.execute(new Runnable() {
                @Override
                public void run() {
                    BusProvider.APP_BUS.post(event);
                }
            });
        } else {
            BusProvider.APP_BUS.post(event);
        }
    }

    public void postOnBoth(Object event) {
        postOnUi(event);
        postOnApp(event);
    }
}
