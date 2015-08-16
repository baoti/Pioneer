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

package com.github.baoti.pioneer.ui.web;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.CheckResult;

import com.github.baoti.pioneer.ui.common.base.BaseContainerActivity;

/**
 * Created by sean on 2015/8/16.
 */
public class WebActivity extends BaseContainerActivity<WebFragment> {

    static final String EXTRA_URL = "url";

    @CheckResult
    public static Intent actionView(Context context, String url) {
        return new Intent(context, WebActivity.class)
                .putExtra(EXTRA_URL, url);
    }

    @Override
    protected WebFragment createContentFragment() {
        return new WebFragment();
    }

    @Override
    public void onBackPressed() {
        if (!contentFragment.goBack()) {
            super.onBackPressed();
        }
    }
}
