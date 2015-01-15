package com.github.baoti.pioneer.app.task;

import android.os.Build;

import com.github.baoti.pioneer.app.task.compat.AsyncTask;

/**
 * Created by liuyedong on 14-12-26.
 */
public class Tasks {
    public interface SafeTask<Result> {

        boolean isRunning();

        boolean hasResultOrException();

        Exception getException();

        Result getResult();
    }

    public interface LifecycleListener {
        void onStarted(SafeTask task);
        void onStopped(SafeTask task);
    }

    @SafeVarargs
    static <P, T extends android.os.AsyncTask<P, ?, ?>> void executeOnDefaultThreadPool(
            T task, P... params) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            task.executeOnExecutor(android.os.AsyncTask.THREAD_POOL_EXECUTOR, params);
        } else {
            task.execute(params);
        }
    }

    @SafeVarargs
    static <P, T extends AsyncTask<P, ?, ?>> void executeOnDefaultThreadPool(T task, P... params) {
        task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, params);
    }

    static class ResultOrException<Result> {
        final Result result;
        final Exception exception;

        ResultOrException(Result result, Exception exception) {
            this.result = result;
            this.exception = exception;
        }
    }
}
