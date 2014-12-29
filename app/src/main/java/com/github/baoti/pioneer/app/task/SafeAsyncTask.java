package com.github.baoti.pioneer.app.task;

import android.annotation.TargetApi;
import android.os.AsyncTask;
import android.os.Build;

import com.github.baoti.pioneer.BusProvider;
import com.squareup.otto.Bus;

/**
 * 自动注册到 APP_BUS 的 AsyncTask , 可用于处理后台任务, 并且将后台任务发出的事件 通过 publishProgress 通知 UI.
 *
 * Created by liuyedong on 14-12-19.
 */
public abstract class SafeAsyncTask<Params, Progress, Result>
        extends AsyncTask<Params, Progress, SafeAsyncTask.ResultOrException<Result>> {
    private final boolean subscribe;
    private final LifecycleListener lifecycleListener;

    /**
     * 用于订阅通知
     */
    protected final Bus appBus;

    private boolean stopped;

    public SafeAsyncTask() {
        this(false,null);
    }

    public SafeAsyncTask(boolean subscribe, LifecycleListener listener) {
        this.subscribe = subscribe;
        appBus = BusProvider.APP_BUS;
        lifecycleListener = listener;
    }

    @SafeVarargs
    public final void executeOnDefaultThreadPool(Params... params) {
        Tasks.executeOnDefaultThreadPool(this, params);
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
    protected final ResultOrException doInBackground(Params... params) {
        try {
            if (subscribe) {
                appBus.register(this);
            }
            try {
                Result result = doTask(params);
                return new ResultOrException(result, null);
            } finally {
                if (subscribe) {
                    appBus.unregister(this);
                }
            }
        } catch (Exception e){
            return new ResultOrException(null, e);
        }
    }

    @Override
    protected final void onPreExecute() {
        super.onPreExecute();
        if (lifecycleListener != null) {
            lifecycleListener.onStarted(this);
        }
        onStart();
    }

    private void onStopped() {
        stopped = true;
        if (lifecycleListener != null) {
            lifecycleListener.onStopped(this);
        }
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    @Override
    protected final void onCancelled(ResultOrException<Result> e) {
        super.onCancelled(e);
    }

    @Override
    protected final void onCancelled() {
        super.onCancelled();
        if (stopped) {
            return;
        }
        onStopped();
        onCancel();
    }

    @Override
    protected final void onPostExecute(ResultOrException<Result> e) {
        super.onPostExecute(e);
        if (stopped) {
            return;
        }
        onStopped();
        if (e.exception != null) {
            onException(e.exception);
        } else {
            onSuccess(e.result);
        }
    }

    static class ResultOrException<Result> {
        final Result result;
        final Exception exception;

        private ResultOrException(Result result, Exception exception) {
            this.result = result;
            this.exception = exception;
        }
    }

    public interface LifecycleListener {
        void onStarted(SafeAsyncTask task);
        void onStopped(SafeAsyncTask task);
    }
}
