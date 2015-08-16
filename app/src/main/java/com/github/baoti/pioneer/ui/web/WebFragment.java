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

package com.github.baoti.pioneer.ui.web;

import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;

import com.github.baoti.pioneer.R;
import com.github.baoti.pioneer.ui.Navigator;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by sean on 2015/8/16.
 */
public class WebFragment extends Fragment {

    @Bind(R.id.app_toolbar)
    Toolbar toolbar;

    @Bind(R.id.web)
    WebView webView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_web, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        ButterKnife.bind(this, view);
        Navigator.setupToolbarNavigation(this, toolbar);
        configureWebView();
        String url = "file:///android_asset/hello-react/hello.html";
        if (getArguments() != null) {
            url = getArguments().getString(WebActivity.EXTRA_URL);
        }
        webView.loadUrl(url);
        super.onViewCreated(view, savedInstanceState);
    }

    private void configureWebView() {
        WebSettings settings = webView.getSettings();
        settings.setJavaScriptEnabled(true);
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.JELLY_BEAN) {
            settings.setBuiltInZoomControls(true);
            settings.setUseWideViewPort(true);
        }
    }

    boolean goBack() {
        if (webView != null && webView.canGoBack()) {
            webView.goBack();
            return true;
        }
        return false;
    }
}
