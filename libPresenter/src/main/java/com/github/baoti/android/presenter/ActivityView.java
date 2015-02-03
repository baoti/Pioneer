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
import android.support.v4.app.FragmentActivity;

/**
 * Base Activity working with {@link Presenter}
 *
 * Created by liuyedong on 14-12-24.
 */
public abstract class ActivityView<V extends IView, P extends Presenter<V>>
        extends FragmentActivity implements IView {
    private static class RetainInstance {
        final Presenter presenter;
        final Object other;
        final Boolean retainPresenter;

        private RetainInstance(Presenter presenter, Object other, Boolean retainPresenter) {
            this.presenter = presenter;
            this.other = other;
            this.retainPresenter = retainPresenter;
        }
    }

    private static final RetainInstance RETAIN_NULL = new RetainInstance(null, null, null);

    private boolean retainPresenter;

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

    protected final void setRetainPresenter(boolean retain) {
        retainPresenter = retain;
    }

    @Override
    public final Object getLastCustomNonConfigurationInstance() {
        RetainInstance instance = (RetainInstance) super.getLastCustomNonConfigurationInstance();
        if (instance == null) {
            return RETAIN_NULL;
        }
        return instance;
    }

    /**
     * Replace {@link #getLastCustomNonConfigurationInstance}
     * @return Retained instance by {@link #onRetainNonConfigInstance}
     * @see #onRetainNonConfigInstance()
     */
    @SuppressWarnings("UnusedDeclaration")
    protected Object getLastNonConfigInstance() {
        RetainInstance instance = (RetainInstance) getLastCustomNonConfigurationInstance();
        return instance != null ? instance.other : null;
    }

    /**
     * Replace {@link #onRetainCustomNonConfigurationInstance}
     * @return Will be retained
     * @see #getLastNonConfigInstance()
     */
    protected Object onRetainNonConfigInstance() {
        return null;
    }

    @Override
    public final Object onRetainCustomNonConfigurationInstance() {
        Object other = onRetainNonConfigInstance();
        if (retainPresenter || other != null) {
            return new RetainInstance(presenter, other, retainPresenter);
        } else {
            return null;
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        RetainInstance instance = (RetainInstance) getLastCustomNonConfigurationInstance();
        //noinspection unchecked
        presenter = (P) instance.presenter;
        if (viewDelegate == null) {
            viewDelegate = createViewDelegate();
        }
        if (presenter == null) {
            presenter = createPresenter(viewDelegate);
        }
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        if (getPresenter() != null) {
            //noinspection unchecked
            getPresenter().takeView(viewDelegate);
            getPresenter().load(savedInstanceState);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (getPresenter() != null) {
            getPresenter().resume();
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        if (getPresenter() != null) {
            getPresenter().save(outState);
        }

        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onPause() {
        if (getPresenter() != null) {
            getPresenter().pause();
        }

        super.onPause();
    }

    @Override
    protected void onDestroy() {
        if (getPresenter() != null) {
            //noinspection unchecked
            getPresenter().dropView(viewDelegate);
            if (!retainPresenter || isFinishing()) {
                getPresenter().close();
            }
        }

        super.onDestroy();
    }
}
