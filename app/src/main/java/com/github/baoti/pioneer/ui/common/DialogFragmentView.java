package com.github.baoti.pioneer.ui.common;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.View;

/**
 * Created by liuyedong on 14-12-24.
 */
public abstract class DialogFragmentView<V extends IView, P extends Presenter<V>>
        extends DialogFragment implements IView {

    private P presenter;
    private V viewDelegate;

    @SuppressWarnings({"UnusedParameters", "unchecked"})
    protected V createViewDelegate() {
        return (V) this;
    }

    public final V getViewDelegate() {
        return viewDelegate;
    }

    @SuppressWarnings("unchecked")
    protected P createPresenter(V view) {
        return (P) view.getPresenter();
    }

    public final P getPresenter() {
        return presenter;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (viewDelegate == null) {
            viewDelegate = createViewDelegate();
        }
        if (presenter == null) {
            presenter = createPresenter(viewDelegate);
        }
        if (getPresenter() != null) {
            //noinspection unchecked
            getPresenter().takeView(viewDelegate);
            getPresenter().load(savedInstanceState);
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        if (getPresenter() != null) {
            getPresenter().resume();
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        if (getPresenter() != null) {
            getPresenter().save(outState);
        }

        super.onSaveInstanceState(outState);
    }

    @Override
    public void onPause() {
        if (getPresenter() != null) {
            getPresenter().pause();
        }

        super.onPause();
    }

    @Override
    public void onDestroyView() {
        if (getPresenter() != null) {
            //noinspection unchecked
            getPresenter().dropView(viewDelegate);
        }

        super.onDestroyView();
    }

    @Override
    public void onDestroy() {
        if (getPresenter() != null) {
            getPresenter().close();
        }

        super.onDestroy();
    }
}
