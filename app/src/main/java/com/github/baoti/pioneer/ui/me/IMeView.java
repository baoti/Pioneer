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

import com.github.baoti.android.presenter.IView;
import com.github.baoti.pioneer.entity.ImageBean;

/**
 * Created by liuyedong on 2014/12/26.
 */
public interface IMeView extends IView {
    void showAccountId(String accountId);

    void showName(String name);

    void showFollowers(String followers);

    void showFollowing(String following);

    void showAvatar(ImageBean avatar);

    void showSignIn();

    void hideSignIn();

    void showAccountInfo();

    void hideAccountInfo();

    void navigateToLogin();
}
