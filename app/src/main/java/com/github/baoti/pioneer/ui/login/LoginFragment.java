package com.github.baoti.pioneer.ui.login;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.baoti.android.presenter.FragmentView;
import com.github.baoti.pioneer.R;
import com.github.baoti.pioneer.ui.Navigator;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by liuyedong on 14-12-18.
 */
public class LoginFragment extends FragmentView<ILoginView, LoginPresenter>
        implements LoginViewDelegate.Delegator {
    private static LoginFragment newInstance() {
        return new LoginFragment();
    }

    @InjectView(R.id.app_toolbar)
    Toolbar appBar;

    @Override
    protected ILoginView createViewDelegate() {
        return new LoginViewDelegate(getView(), this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_login, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        ButterKnife.inject(this, view);
        Navigator.setupToolbarNavigation(this, appBar);
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void setTitle(LoginViewDelegate delegate, CharSequence title) {
        appBar.setTitle(R.string.title_login);
    }

    @Override
    public void close(LoginViewDelegate delegate) {
        getActivity().onBackPressed();
    }
}
