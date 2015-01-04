package com.github.baoti.pioneer.ui.login;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.baoti.pioneer.R;
import com.github.baoti.pioneer.ui.Navigator;
import com.github.baoti.pioneer.ui.common.DialogFragmentView;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by liuyedong on 2014/12/26.
 */
public class LoginDialogFragment extends DialogFragmentView<ILoginView, LoginPresenter>
        implements LoginViewDelegate.Delegator {
    public static LoginDialogFragment newInstance() {
        return new LoginDialogFragment();
    }

//    @Optional
    @InjectView(R.id.app_toolbar)
    Toolbar appBar;

    @Override
    protected ILoginView createViewDelegate() {
        return new LoginViewDelegate(getView(), this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.dialog_fragment_login, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        ButterKnife.inject(this, view);
        Navigator.setupToolbarNavigation(this, appBar);
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void setTitle(LoginViewDelegate delegate, CharSequence title) {
        if (getShowsDialog()) {
            appBar.setVisibility(View.GONE);
            getDialog().setTitle(title);
        } else {
            appBar.setVisibility(View.VISIBLE);
            appBar.setTitle(title);
        }
    }

    @Override
    public void close(LoginViewDelegate delegate) {
        if (getShowsDialog()) {
            dismiss();
        } else {
            getActivity().onBackPressed();
        }
    }
}
