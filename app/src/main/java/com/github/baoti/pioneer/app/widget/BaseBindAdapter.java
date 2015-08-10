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

package com.github.baoti.pioneer.app.widget;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

/**
 * An implementation of {@link BaseAdapter} which uses the create/bind pattern for its views.
 */
public abstract class BaseBindAdapter<T> extends BaseAdapter {
    private final Context context;
    private final LayoutInflater inflater;

    public BaseBindAdapter(Context context) {
        this.context = context;
        this.inflater = LayoutInflater.from(context);
    }

    /**
     * Returns the context associated with this array adapter. The context is used
     * to create views from the resource passed to the constructor.
     *
     * @return The Context associated with this adapter.
     */
    public Context getContext() {
        return context;
    }

    public LayoutInflater getInflater() {
        return inflater;
    }

    @Override
    public abstract T getItem(int position);

    @Override
    public final View getView(int position, View view, ViewGroup container) {
        boolean created = false;
        if (view == null) {
            created = true;
            view = createView(inflater, position, container);
            if (view == null) {
                throw new IllegalStateException("onCreateView result must not be null.");
            }
        }
        bindView(getItem(position), position, view, created);
        return view;
    }

    public final View createView(LayoutInflater inflater, int position, ViewGroup container) {
        View view = onCreateView(inflater, position, container);
        onPostCreateView(view, position);
        return view;
    }

    public final void bindView(T item, int position, View view, boolean created) {
        onBindView(item, position, view, created);
        onPostBindView(view, position, created);
    }

    /**
     * Create a new instance of a view for the specified position.
     */
    protected abstract View onCreateView(LayoutInflater inflater, int position, ViewGroup container);

    /**
     * Bind the data for the specified {@code position} to the view.
     */
    protected abstract void onBindView(T item, int position, View view, boolean created);

    protected void onPostCreateView(View view, int position) {

    }

    protected void onPostBindView(View view, int position, boolean created) {

    }

    @Override
    public final View getDropDownView(int position, View view, ViewGroup container) {
        boolean created = false;
        if (view == null) {
            created = true;
            view = createDropDownView(inflater, position, container);
            if (view == null) {
                throw new IllegalStateException("onCreateDropDownView result must not be null.");
            }
        }
        bindDropDownView(getItem(position), position, view, created);
        return view;
    }

    public final View createDropDownView(LayoutInflater inflater, int position, ViewGroup container) {
        View view = onCreateDropDownView(inflater, position, container);
        onPostCreateDropDownView(view, position);
        return view;
    }

    public final void bindDropDownView(T item, int position, View view, boolean created) {
        onBindDropDownView(item, position, view, created);
        onPostBindDropDownView(view, position, created);
    }

    /**
     * Create a new instance of a drop-down view for the specified position.
     */
    protected View onCreateDropDownView(LayoutInflater inflater, int position, ViewGroup container) {
        return onCreateView(inflater, position, container);
    }

    /**
     * Bind the data for the specified {@code position} to the drop-down view.
     */
    protected void onBindDropDownView(T item, int position, View view, boolean created) {
        onBindView(item, position, view, created);
    }

    protected void onPostCreateDropDownView(View view, int position) {
        onPostCreateView(view, position);
    }

    protected void onPostBindDropDownView(View view, int position, boolean created) {
        onPostBindView(view, position, created);
    }
}
