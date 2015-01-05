package com.github.baoti.pioneer.ui.me;

import com.github.baoti.pioneer.entity.ImageBean;
import com.github.baoti.pioneer.ui.common.IView;

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
