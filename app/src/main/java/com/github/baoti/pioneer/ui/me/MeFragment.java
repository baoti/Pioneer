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

import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.github.baoti.android.presenter.FragmentView;
import com.github.baoti.pioneer.AppMain;
import com.github.baoti.pioneer.R;
import com.github.baoti.pioneer.entity.ImageBean;
import com.github.baoti.pioneer.misc.picasso.PicassoHelper;
import com.github.baoti.pioneer.ui.Navigator;
import com.github.baoti.pioneer.ui.common.image.chooser.ImageChooserFragment;
import com.github.baoti.pioneer.ui.login.LoginDialogFragment;
import com.squareup.picasso.Picasso;

import javax.inject.Inject;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import dagger.Lazy;

/**
 * Created by liuyedong on 14-12-26.
 */
public class MeFragment extends FragmentView<IMeView, MePresenter> implements IMeView,
        ImageChooserFragment.OnImageChooserListener {

    public static MeFragment newInstance() {
        return new MeFragment();
    }

    private static final int CHOOSE_AVATAR = 0;

    @Inject
    Lazy<MePresenter> presenterLazy;

    @Inject
    Picasso picasso;

    @InjectView(R.id.app_toolbar)
    Toolbar toolbar;

    @InjectView(R.id.cv_account_info)
    CardView accountInfo;

    @InjectView(R.id.iv_avatar)
    ImageView avatar;

    @InjectView(R.id.tv_account)
    TextView accountId;

    @InjectView(R.id.tv_name)
    TextView name;

    @InjectView(R.id.tv_following)
    TextView following;

    @InjectView(R.id.tv_followers)
    TextView followers;

    @InjectView(R.id.btn_sign_in)
    Button signIn;

    @InjectView(R.id.btn_sign_out)
    Button signOut;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_me, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        ButterKnife.inject(this, view);
        AppMain.component().ui().injectMeFragment(this);
        setupToolbar();
        super.onViewCreated(view, savedInstanceState);
    }

    private void setupToolbar() {
        toolbar.setTitle("Me");
        Navigator.setupToolbarNavigation(this, toolbar);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (getActivity() instanceof ActionBarActivity) {
            ActionBar actionBar = ((ActionBarActivity) getActivity()).getSupportActionBar();
            actionBar.setSubtitle(R.string.title_me);
        } else {
            getActivity().setTitle(R.string.title_me);
        }
    }

    @Override
    protected MePresenter createPresenter(IMeView view) {
        return presenterLazy.get();
    }

    @Override
    public void showAccountId(String accountId) {
        this.accountId.setText(accountId);
    }

    @Override
    public void showName(String name) {
        this.name.setText(name);
    }

    @Override
    public void showFollowers(String followers) {
        this.followers.setText(followers);
    }

    @Override
    public void showFollowing(String following) {
        this.following.setText(following);
    }

    @Override
    public void showAvatar(ImageBean avatar) {
        PicassoHelper.load(Picasso.with(getActivity()), avatar)
                .placeholder(R.drawable.avatar)
                .error(R.drawable.avatar)
                .into(this.avatar);
    }

    @Override
    public void showSignIn() {
        signIn.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideSignIn() {
        signIn.setVisibility(View.GONE);
    }

    @Override
    public void showAccountInfo() {
        accountInfo.setVisibility(View.VISIBLE);
        signOut.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideAccountInfo() {
        accountInfo.setVisibility(View.GONE);
        signOut.setVisibility(View.GONE);
    }

    @Override
    public void navigateToLogin() {
        LoginDialogFragment.newInstance().show(getFragmentManager(), null);
    }

    @OnClick(R.id.btn_go_home)
    void onGoHomeClicked() {
        Navigator.upToMain(getActivity());
    }

    @OnClick(R.id.btn_close_app)
    void onCloseAppClicked() {
        Navigator.closeApp(getActivity());
    }

    @OnClick(R.id.btn_sign_in)
    void onSignInClicked() {
        getPresenter().onSignInClicked();
    }

    @OnClick(R.id.btn_sign_out)
    void onSignOutClicked() {
        getPresenter().onSignOutClicked();
    }

    @OnClick(R.id.tr_avatar)
    void onAvatarClicked() {
        ImageChooserFragment.showDialog(getFragmentManager(), true)
                .setTargetFragment(this, CHOOSE_AVATAR);
    }

    @Override
    public void onImageChose(int requestCode, @Nullable Uri image) {
        switch (requestCode) {
            case CHOOSE_AVATAR:
                getPresenter().onAvatarChanged(image);
                picasso.load(image).into(avatar);
                break;
        }
    }

    @Override
    public void onImageCancelled(int requestCode) {

    }
}
