package com.github.baoti.pioneer.app.task;

import android.content.Context;
import android.widget.Toast;

/**
 * 用于测试 Task 泄漏 与 View 泄漏
 *
 * Created by sean on 2014/12/24.
 */
public class ReportTask extends SafeAsyncTask<String, String, String> {
    private final Context context;
    private final String tag;
    private final long duration;

    public ReportTask(Context context, String tag, long duration) {
        this.context = context.getApplicationContext();
        this.tag = tag;
        this.duration = duration;
    }

    @Override
    protected String doTask(String... params) {
        int count = 0;
        long sleepTime = Math.max(3000, duration);
        while (!isCancelled()) {
            publishProgress(String.format("%s[%d]: I'm alive.", tag, ++count));
            try {
                Thread.sleep(sleepTime);
            } catch (InterruptedException e) {
                break;
            }
        }
        return String.format("%s[%d]: I'm going to die", tag, ++count);
    }

    @Override
    protected void onProgressUpdate(String... values) {
        Toast.makeText(context, values[0], Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onSuccess(String s) {
        Toast.makeText(context, s, Toast.LENGTH_SHORT).show();
    }
}
