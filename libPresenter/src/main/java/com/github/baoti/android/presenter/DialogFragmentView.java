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
package com.github.baoti.android.presenter;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.View;

/**
 * Base DialogFragment working with {@link Presenter}
 *
 * Created by liuyedong on 14-12-24.
 */
public abstract class DialogFragmentView<V extends IView, P extends Presenter<V>>
        extends DialogFragment implements IView {

    private P presenter;
    private V viewDelegate;

    @SuppressWarnings("unchecked")
    protected V createViewDelegate() {
        return (V) this;
    }

    @SuppressWarnings("UnusedDeclaration")
    public final V getViewDelegate() {
        return viewDelegate;
    }

    @SuppressWarnings("unchecked")
    protected P createPresenter(V view) {
        return (P) view.getPresenter();
    }

    @Override
    public final P getPresenter() {
        return presenter;
    }

    @Override
    public void onPresenterTaken(Presenter presenter) {}

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
