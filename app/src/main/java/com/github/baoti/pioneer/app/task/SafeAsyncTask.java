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

package com.github.baoti.pioneer.app.task;

import android.annotation.TargetApi;
import android.os.Build;

import com.github.baoti.pioneer.BusProvider;
import com.github.baoti.pioneer.app.task.compat.AsyncTask;
import com.squareup.otto.Bus;

import timber.log.Timber;

/**
 * 自动注册到 APP_BUS 的 AsyncTask , 可用于处理后台任务, 并且将后台任务发出的事件 通过 publishProgress 通知 UI.
 *
 * Created by liuyedong on 14-12-19.
 */
public abstract class SafeAsyncTask<Params, Progress, Result>
        extends AsyncTask<Params, Progress, Tasks.ResultOrException<Result>>
        implements Tasks.SafeTask<Result> {
    private final boolean subscribe;
    private Tasks.LifecycleListener lifecycleListener;

    /**
     * 用于订阅通知
     */
    protected final Bus appBus;

    /**
     * 是否执行了 onStopped 回调
     */
    private boolean stopped;

    /**
     * 是否正在运行
     */
    private boolean running;
    /**
     * 缓存运行结果
     */
    private Tasks.ResultOrException<Result> resultOrException;

    public SafeAsyncTask() {
        this(false);
    }

    public SafeAsyncTask(boolean subscribe) {
        this.subscribe = subscribe;
        appBus = BusProvider.APP_BUS;
    }

    public <Task extends SafeAsyncTask> Task setLifecycleListener(
            Tasks.LifecycleListener lifecycleListener) {
        this.lifecycleListener = lifecycleListener;
        //noinspection unchecked
        return (Task) this;
    }

    @SafeVarargs
    public final void executeOnDefaultThreadPool(Params... params) {
        Tasks.executeOnDefaultThreadPool(this, params);
    }

    @Override
    public boolean isRunning() {
        return running;
    }

    @Override
    public boolean hasResultOrException() {
        return resultOrException != null;
    }

    @Override
    public Exception getException() {
        return resultOrException != null ? resultOrException.exception : null;
    }

    @Override
    public Result getResult() {
        return resultOrException != null ? resultOrException.result : null;
    }

    protected void onStart() {
    }

    @SuppressWarnings("unchecked")
    protected abstract Result doTask(Params... params) throws Exception;

    protected void onCancel() {
    }

    @SuppressWarnings("UnusedParameters")
    protected void onException(Exception exception) {
    }

    @SuppressWarnings("UnusedParameters")
    protected void onSuccess(Result result) {
    }

    @SuppressWarnings("unchecked")
    @Override
    protected final Tasks.ResultOrException doInBackground(Params... params) {
        try {
            if (subscribe) {
                appBus.register(this);
            }
            try {
                Timber.v("doTask [%s]", this);
                Result result = doTask(params);
                return new Tasks.ResultOrException(result, null);
            } finally {
                if (subscribe) {
                    appBus.unregister(this);
                }
            }
        } catch (Exception e){
            return new Tasks.ResultOrException(null, e);
        }
    }

    @Override
    protected final void onPreExecute() {
        super.onPreExecute();
        Timber.v("onStart [%s]", this);
        running = true;
        if (lifecycleListener != null) {
            lifecycleListener.onStarted(this);
        }
        onStart();
    }

    private void onStopped() {
        stopped = true;
        running = false;
        if (lifecycleListener != null) {
            lifecycleListener.onStopped(this);
        }
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    @Override
    protected final void onCancelled(Tasks.ResultOrException<Result> e) {
        super.onCancelled(e);
    }

    @Override
    protected final void onCancelled() {
        super.onCancelled();
        if (stopped) {
            return;
        }
        Timber.v("onCancel [%s]", this);
        resultOrException = null;
        onStopped();
        onCancel();
    }

    @Override
    protected final void onPostExecute(Tasks.ResultOrException<Result> e) {
        super.onPostExecute(e);
        if (stopped) {
            return;
        }
        if (isCancelled()) {
            onCancelled();
            return;
        }
        resultOrException = e;
        onStopped();
        if (e.exception != null) {
            Timber.v("onException [%s] - [%s]", this, e.exception);
            onException(e.exception);
        } else {
            Timber.v("onSuccess [%s] - [%s]", this, e.result);
            onSuccess(e.result);
        }
    }
}
