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

package com.github.baoti.pioneer.ui.main;

import android.app.Activity;

import com.github.baoti.android.presenter.IFlow;
import com.github.baoti.android.presenter.IView;

/**
 * Created by liuyedong on 14-12-22.
 */
public interface IMainView extends IView {
    void showAccount(String accountId);

    void hideSignIn();

    void showSignOut();

    void hideAccount();

    void showSignIn();

    void hideSignOut();

    void hideStopReport();

    Activity getActivity();

    @Override
    Flow getFlow();

    interface Flow extends IFlow {

        void navigateToLogin();
    }
}
