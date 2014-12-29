package com.github.baoti.pioneer.app.notification;

import android.content.Context;
import android.text.TextUtils;
import android.widget.Toast;

import com.github.baoti.pioneer.app.ForApp;

import javax.inject.Inject;
import javax.inject.Singleton;

/**
 * Created by liuyedong on 14-12-22.
 */
@Singleton
public class Toaster {
    private final Context context;

    @Inject
    public Toaster(
            @ForApp Context context) {
        this.context = context;
    }

    public void show(CharSequence msg) {
        if (!TextUtils.isEmpty(msg)) {
            Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
        }
    }

    public void show(int textId) {
        Toast.makeText(context, textId, Toast.LENGTH_SHORT).show();
    }
}
