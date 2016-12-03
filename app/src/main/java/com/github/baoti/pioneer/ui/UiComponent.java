/*
 * Copyright (c) 2014-2016 Sean Liu.
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

package com.github.baoti.pioneer.ui;

import com.github.baoti.pioneer.ui.login.LoginViewDelegate;
import com.github.baoti.pioneer.ui.main.MainFragment;
import com.github.baoti.pioneer.ui.me.MeFragment;
import com.github.baoti.pioneer.ui.news.edit.NewsEditFragment;
import com.github.baoti.pioneer.ui.news.list.NewsListFragment;
import com.github.baoti.pioneer.ui.splash.SplashActivity;

import dagger.Subcomponent;

/**
 * Created by liuyedong on 15-5-15.
 */
@Subcomponent
public interface UiComponent {

    void injectMainFragment(MainFragment fragment);

    void injectLoginViewDelegate(LoginViewDelegate loginViewDelegate);

    void injectMeFragment(MeFragment fragment);

    void injectSplashActivity(SplashActivity splashActivity);

    void injectNewsEditFragment(NewsEditFragment newsEditFragment);

    void injectNewsListFragment(NewsListFragment newsListFragment);
}
