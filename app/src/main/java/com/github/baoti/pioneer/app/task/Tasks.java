package com.github.baoti.pioneer.app.task;

import android.os.AsyncTask;
import android.os.Build;

/**
 * Created by liuyedong on 14-12-26.
 */
public class Tasks {
    @SafeVarargs
    static <P, T extends AsyncTask<P, ?, ?>> void executeOnDefaultThreadPool(T task, P... params) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, params);
        } else {
            task.execute(params);
        }
    }
}
