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

package com.github.baoti.pioneer.misc.util;

import android.content.Intent;
import android.os.Bundle;

/**
 * Extra state for XXX.startActivity
 *
 * Created by liuyedong on 15-6-18.
 */
public class ActivityRequestState {

    public static final String SAVED_ACTIVITY_REQUEST_STATE = "app:saved-activity-request-state";

    // type: Intent
    private static final String KEY_REQUEST_INTENT = "app:ars:request-intent";
    // type: int
    private static final String KEY_REQUEST_CODE = "app:ars:request-code";
    // type: Bundle
    private static final String KEY_REQUEST_ARGS = "app:ars:request-args";

    private Intent requestIntent;
    private Bundle requestArgs;
    private int requestCode = -1;

    public Intent getRequestIntent() {
        return requestIntent;
    }

    public Bundle getRequestArgs() {
        return requestArgs;
    }

    public int getRequestCode() {
        return requestCode;
    }

    public void setRequestState(Intent intent, Bundle args, int code) {
        this.requestIntent = intent;
        this.requestArgs = args;
        this.requestCode = code;
    }

    public void onLoad(Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            Bundle bundle = savedInstanceState.getBundle(SAVED_ACTIVITY_REQUEST_STATE);
            if (bundle != null) {
                requestIntent = bundle.getParcelable(KEY_REQUEST_INTENT);
                requestArgs = bundle.getBundle(KEY_REQUEST_ARGS);
                requestCode = bundle.getInt(KEY_REQUEST_CODE, -1);
            }
        }
    }

    public void onSave(Bundle outState) {
        Bundle bundle = new Bundle(3);
        bundle.putParcelable(KEY_REQUEST_INTENT, requestIntent);
        bundle.putBundle(KEY_REQUEST_ARGS, requestArgs);
        bundle.putInt(KEY_REQUEST_CODE, requestCode);
        outState.putBundle(SAVED_ACTIVITY_REQUEST_STATE, bundle);
    }
}
