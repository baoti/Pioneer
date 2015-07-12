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

package com.github.baoti.pioneer.ui.splash;

import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.github.baoti.android.presenter.Presenter;
import com.github.baoti.pioneer.app.task.SafeAsyncTask;
import com.github.baoti.pioneer.biz.interactor.AppInteractor;
import com.github.baoti.pioneer.event.app.AppInitializeReportEvent;
import com.github.baoti.pioneer.misc.util.Texts;
import com.github.baoti.pioneer.ui.ForUi;
import com.squareup.otto.Subscribe;

import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import timber.log.Timber;

/**
 * Created by liuyedong on 14-12-23.
 */
public class SplashPresenter extends Presenter<ISplashView> {
    private static final long SPLASH_TIME = TimeUnit.SECONDS.toMillis(1);

    private static final String STATE_HIDE_BUTTON = "Splash-HideButton";

    private InitializeTask task;
    private final AppInteractor appInteractor;
    private final Handler uiHandler;

    private String status;
    private long counter;

    private boolean hideButton;
    private boolean hasNavigated;

    private boolean hideButtonInBundle;

    private long entryTime;

    /**
     * 未处理的启动Main界面的操作，当初始化完成后，将不为null，即可以启动了。
     */
    private Runnable launchMainPending;

    @Inject
    public SplashPresenter(AppInteractor interactor, @ForUi Handler handler) {
        appInteractor = interactor;
        uiHandler = handler;
    }

    @Override
    protected void onLoad(@Nullable Bundle savedInstanceState, boolean reusing) {
        super.onLoad(savedInstanceState, reusing);
        if (savedInstanceState != null) {
            hideButtonInBundle = savedInstanceState.getBoolean(STATE_HIDE_BUTTON, false);
            if (hideButtonInBundle) {
                getView().hideRetainInBundle();
            }
        } else {
            hideButtonInBundle = false;
            entryTime = System.currentTimeMillis();
        }
        if (task == null) {
            task = new InitializeTask();
            task.executeOnDefaultThreadPool();
        } else {
            showStatus(status, false);
        }
        if (hideButton) {
            getView().hideRetainInPresenter();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        autoLaunchMain();
    }

    @Override
    protected void onPause() {
        pauseAutoLaunchMain();
        super.onPause();
    }

    @Override
    protected void onSave(@NonNull Bundle outState) {
        outState.putBoolean(STATE_HIDE_BUTTON, hideButtonInBundle);
    }

    @Override
    protected void onClose() {
        cancelAutoLaunchMain();
        task.cancel(true);
        task = null;
        super.onClose();
    }

    private void cancelAutoLaunchMain() {
        uiHandler.removeCallbacks(launchMainPending);
        launchMainPending = null;
    }

    private void autoLaunchMain() {
        if (launchMainPending != null) {
            uiHandler.removeCallbacks(launchMainPending);
            long passingTime = System.currentTimeMillis() - entryTime;
            long expTime = SPLASH_TIME - passingTime;
            Timber.v("expTime: %s", expTime);
            uiHandler.postDelayed(launchMainPending, expTime);
        }
    }

    private void pauseAutoLaunchMain() {
        uiHandler.removeCallbacks(launchMainPending);
    }

    public void onGoToMainClicked() {
        navigateToMain();
    }

    private void navigateToMain() {
        if (!hasView()) {
            return;
        }
        hasNavigated = true;
        cancelAutoLaunchMain();
        getView().getFlow().navigateToMain(false);
    }

    private void showStatus(String status, boolean update) {
        if (!hasView()) {
            return;
        }
        if (update) {
            counter++;
        }
        this.status = status;
        getView().showStatus(String.format(
                "(%d)\n%s\n%s",
                counter,
                task == null ? "null" : Texts.filledStr((int) task.countSeconds(), '.'),
                status));
    }

    private void handleInitializeFinished(Boolean success) {
        if (hasNavigated) {
            Timber.i("Has navigated, do nothing");
        } else if (success) {
            if (launchMainPending == null) {
                launchMainPending = new Runnable() {
                    @Override
                    public void run() {
                        navigateToMain();
                    }
                };
                autoLaunchMain();
            }
        } else {
            // post an Exit Event
        }
    }

    public void onRetainInPresenterClicked() {
        hideButton = true;
        getView().hideRetainInPresenter();
    }

    public void onRetainInBundleClicked() {
        hideButtonInBundle = true;
        getView().hideRetainInBundle();
    }

    class InitializeTask extends SafeAsyncTask<Void, String, Void> {

        private long startTime;

        public InitializeTask() {
            super(true);
        }

        @Override
        protected void onStart() {
            startTime = SystemClock.uptimeMillis();
            showStatus("正在初始化应用", true);
        }

        long countSeconds() {
            return (SystemClock.uptimeMillis() - startTime) / 1000;
        }

        @Override
        protected Void doTask(Void... params) throws Exception {
            appInteractor.initialize();
            return null;
        }

        @Override
        protected void onException(Exception exception) {
            Timber.e(exception, "Unable initialize app");
            handleInitializeFinished(false);
        }

        @Override
        protected void onCancel() {
            Timber.i("onCancel");
        }

        @Override
        protected void onSuccess(Void aVoid) {
            handleInitializeFinished(true);
        }

        @Override
        protected void onProgressUpdate(String... values) {
            showStatus(values[0], true);
        }

        @Subscribe
        public void onAppInitialize(AppInitializeReportEvent event) {
            publishProgress(event.progress);
        }
    }
}
