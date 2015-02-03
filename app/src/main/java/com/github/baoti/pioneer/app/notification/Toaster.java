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
