package com.github.baoti.pioneer.ui.me;

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

import com.github.baoti.pioneer.AppMain;
import com.github.baoti.pioneer.AppMainModule;
import com.github.baoti.pioneer.R;
import com.github.baoti.pioneer.app.Navigator;
import com.github.baoti.pioneer.entity.ImageBean;
import com.github.baoti.pioneer.misc.picasso.PicassoHelper;
import com.github.baoti.pioneer.ui.common.FragmentView;
import com.github.baoti.pioneer.ui.login.LoginDialogFragment;
import com.squareup.picasso.Picasso;

import javax.inject.Inject;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import dagger.Lazy;
import dagger.Module;

/**
 * Created by liuyedong on 14-12-26.
 */
public class MeFragment extends FragmentView<IMeView, MePresenter> implements IMeView {
    public static MeFragment newInstance() {
        return new MeFragment();
    }

    @Inject
    Lazy<MePresenter> presenterLazy;

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

    @InjectView(R.id.tv_fans)
    TextView fans;

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
        AppMain.globalGraph().plus(new MeModule()).inject(this);
        toolbar.setTitle("Me");
        super.onViewCreated(view, savedInstanceState);
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
    public void showFans(String fans) {
        this.fans.setText(fans);
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

    @Module(injects = MeFragment.class, addsTo = AppMainModule.class)
    public class MeModule {
    }
}
