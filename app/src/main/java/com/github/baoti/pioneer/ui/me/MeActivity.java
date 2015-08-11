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

package com.github.baoti.pioneer.ui.me;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.CheckResult;
import android.support.v4.app.Fragment;

import com.github.baoti.pioneer.ui.common.base.BaseContainerActivity;

/**
 * Created by liuyedong on 14-12-26.
 */
public class MeActivity extends BaseContainerActivity {
    @CheckResult
    public static Intent actionLaunch(Context context) {
        return new Intent(context, MeActivity.class);
    }

    @Override
    protected Fragment createContentFragment() {
        return MeFragment.newInstance();
    }
}
