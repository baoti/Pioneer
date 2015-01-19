package com.github.baoti.pioneer.app.task;

import android.os.Build;

import com.github.baoti.pioneer.app.task.compat.AsyncTask;

/**
 * Created by liuyedong on 14-12-26.
 */
public class Tasks {
    /**
     * 储存执行异常信息的 Task
     * @param <Result>
     */
    public interface SafeTask<Result> {

        /**
         * 是否正在运行, 已取消或失败或成功, 都返回 false
         * @return
         */
        boolean isRunning();

        /**
         * 是否有了执行结果, 正在运行时返回 null, 已取消时返回 null, 失败或成功返回 true.
         * @return
         */
        boolean hasResultOrException();

        /**
         * 得到失败时的异常, 未产生异常时返回 null.
         * @return
         */
        Exception getException();

        /**
         * 得到成功时的结果, 未得到结果时返回 null.
         * @return
         */
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
