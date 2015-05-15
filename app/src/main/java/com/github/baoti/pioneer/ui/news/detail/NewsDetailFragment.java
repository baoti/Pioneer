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

package com.github.baoti.pioneer.ui.news.detail;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.github.baoti.android.presenter.FragmentView;
import com.github.baoti.android.presenter.IView;
import com.github.baoti.android.presenter.Presenter;
import com.github.baoti.pioneer.R;
import com.github.baoti.pioneer.entity.News;
import com.github.baoti.pioneer.ui.Navigator;
import com.github.baoti.pioneer.ui.news.NewsActivity;
import com.github.baoti.pioneer.ui.news.edit.NewsEditFragment;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by liuyedong on 15-1-6.
 */
public class NewsDetailFragment extends FragmentView<IView, Presenter<IView>> implements Toolbar.OnMenuItemClickListener {

    public static NewsDetailFragment newInstance(Bundle args) {
        NewsDetailFragment fragment = new NewsDetailFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Bind(R.id.app_toolbar)
    Toolbar toolbar;

    @Bind(R.id.tv_news_content)
    TextView newsContent;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_news_detail, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        ButterKnife.bind(this, view);
        Navigator.setupToolbarNavigation(this, toolbar);
        toolbar.setOnMenuItemClickListener(this);
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        News news = (News) getArguments().getSerializable(NewsActivity.EXTRA_NEWS);
        boolean editable = getArguments().getBoolean(NewsActivity.EXTRA_EDITABLE);
        toolbar.setTitle(news.getTitle());
        if (editable) {
            toolbar.inflateMenu(R.menu.edit);
        }
        newsContent.setText(news.getContent());
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public boolean onMenuItemClick(MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case R.id.action_edit:
                navigateToEdit();
                return true;
        }
        return false;
    }

    private void navigateToEdit() {
        String tag = null;
        getFragmentManager().beginTransaction()
                .replace(getId(), NewsEditFragment.newInstance(getArguments()), tag)
                .addToBackStack(null)
                .commit();
    }
}
