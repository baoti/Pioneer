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
import android.support.annotation.Nullable;
import android.support.v4.widget.ContentLoadingProgressBar;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.github.baoti.pioneer.R;
import com.github.baoti.pioneer.ui.common.page.PagePresenter;

/**
 * Helper for showing more items are being loaded at the bottom of a list via a
 * custom footer view
 */
public class ResourceLoadingIndicator {

    private HeaderFooterListAdapter<?> adapter;

    private boolean showing;

    private final View view;

    private final ContentLoadingProgressBar progressBar;
    private final TextView textView;

    /**
     * Create indicator using given inflater
     *
     * @param context
     */
    public ResourceLoadingIndicator(final Context context) {
        this(LayoutInflater.from(context), null);
    }

    public ResourceLoadingIndicator(LayoutInflater inflater, ViewGroup parent) {
        this(inflater.inflate(R.layout.loading_item, parent, false));
    }

    public ResourceLoadingIndicator(View loadingView) {
        view = loadingView;
        progressBar = (ContentLoadingProgressBar) view.findViewById(android.R.id.progress);
        textView = (TextView) view.findViewById(android.R.id.text1);
    }

    public ResourceLoadingIndicator(Context context, int loadingMessage) {
        this(context);
        textView.setText(loadingMessage);
    }

    public View getView() {
        return view;
    }

    /**
     * Set the adapter that this indicator should be added as a footer to
     *
     * @param adapter
     * @return this indicator
     */
    public ResourceLoadingIndicator setList(
            final HeaderFooterListAdapter<?> adapter) {
        return setList(adapter, true);
    }

    public ResourceLoadingIndicator setList(
            final HeaderFooterListAdapter<?> adapter, boolean visible) {
        this.adapter = adapter;
        return setVisible(visible);
    }

    /**
     * Set visibility of entire indicator view
     *
     * @param visible
     * @return this indicator
     */
    public ResourceLoadingIndicator setVisible(final boolean visible) {
        if (showing != visible && adapter != null)
            if (visible)
                adapter.addFooter(view);
            else
                adapter.removeFooter(view);
        showing = visible;
        return this;
    }

    public ResourceLoadingIndicator setProgressVisible(boolean visible) {
        if (visible) {
//            progressBar.show();
            progressBar.setVisibility(View.VISIBLE);
        } else {
//            progressBar.hide();
            progressBar.setVisibility(View.GONE);
        }
        return this;
    }

    public ResourceLoadingIndicator setText(int textResId) {
        textView.setVisibility(View.VISIBLE);
        textView.setText(textResId);
        return this;
    }

    public ResourceLoadingIndicator setText(CharSequence text) {
        textView.setVisibility(TextUtils.isEmpty(text) ? View.INVISIBLE : View.INVISIBLE);
        textView.setText(text);
        return this;
    }

    public void hide() {
        setVisible(false);
        setProgressVisible(false);
        setText(null);
    }

    public void showLoading() {
        setVisible(true);
        setProgressVisible(true);
        setText(R.string.loading);
    }

    public void showResult(boolean hasMore) {
        setVisible(true);
        setProgressVisible(false);
        if (hasMore) {
            setText(null);
        } else {
            setText(R.string.no_more);
        }
    }

    public void showError(@Nullable Exception error) {
        setVisible(true);
        setProgressVisible(false);
        setText(R.string.failed_to_load);
    }

    public <E> void updateStatus(PagePresenter<?, E> presenter) {
        if (presenter.isLoadingNextPage()) {
            progressBar.setVisibility(View.VISIBLE);
            textView.setVisibility(View.VISIBLE);
            textView.setText(R.string.loading);
        } else if (!presenter.hasNextPage()) {
            progressBar.setVisibility(View.GONE);
            textView.setVisibility(View.VISIBLE);
            textView.setText(R.string.no_more);
        } else if (presenter.isFailedToLoadNextPage()) {
            progressBar.setVisibility(View.GONE);
            textView.setVisibility(View.VISIBLE);
            textView.setText(R.string.failed_to_load);
        } else {
            progressBar.setVisibility(View.INVISIBLE);
            textView.setVisibility(View.INVISIBLE);
        }
    }
}
