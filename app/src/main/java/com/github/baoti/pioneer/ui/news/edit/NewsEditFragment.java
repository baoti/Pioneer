/*
 * Copyright (c) 2014-2016 Sean Liu.
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

package com.github.baoti.pioneer.ui.news.edit;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.github.baoti.android.presenter.FragmentView;
import com.github.baoti.android.presenter.IView;
import com.github.baoti.android.presenter.Presenter;
import com.github.baoti.pioneer.AppMain;
import com.github.baoti.pioneer.R;
import com.github.baoti.pioneer.app.notification.Toaster;
import com.github.baoti.pioneer.entity.News;
import com.github.baoti.pioneer.ui.Navigator;
import com.github.baoti.pioneer.ui.news.NewsActivity;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by liuyedong on 15-1-6.
 */
public class NewsEditFragment extends FragmentView<IView, Presenter<IView>> implements Toolbar.OnMenuItemClickListener {

    public static NewsEditFragment newInstance(Bundle extras) {
        NewsEditFragment fragment = new NewsEditFragment();
        fragment.setArguments(extras);
        return fragment;
    }

    @Inject
    Toaster toaster;

    @BindView(R.id.app_toolbar)
    Toolbar toolbar;

    @BindView(R.id.et_news_content)
    EditText contentInput;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AppMain.component().ui().injectNewsEditFragment(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_news_edit, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        ButterKnife.bind(this, view);
        Navigator.setupToolbarNavigation(this, toolbar);
        toolbar.inflateMenu(R.menu.save);
        toolbar.setOnMenuItemClickListener(this);
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        News news = (News) getArguments().getSerializable(NewsActivity.EXTRA_NEWS);
        toolbar.setTitle(news.getTitle());
        contentInput.setText(news.getContent());
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public boolean onMenuItemClick(MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case R.id.action_save:
                onSaveClicked();
                return true;
        }
        return false;
    }

    private void onSaveClicked() {
        toaster.show("News's content changed to: " + contentInput.getText());
        // TODO: post news changed event
        getActivity().onBackPressed();
    }
}
