package com.github.baoti.pioneer.ui.news;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;

import com.github.baoti.pioneer.R;
import com.github.baoti.pioneer.ui.common.search.BaseSearchActivity;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by liuyedong on 2015/1/2.
 */
public class NewsListActivity extends BaseSearchActivity {

    public static Intent intentToLaunch(Context context) {
        return new Intent(context, NewsListActivity.class);
    }

    @InjectView(R.id.app_toolbar)
    Toolbar toolbar;

    private NewsListFragment listFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news_list);

        ButterKnife.inject(this);

        listFragment = (NewsListFragment) getSupportFragmentManager().findFragmentById(R.id.frag_news_list);
        listFragment.enableInitialResources();

        setupSearch(setupToolbar(toolbar), listFragment);
    }

    @Override
    protected Fragment createSearchFragment() {
        return NewsListFragment.newInstance();
    }

    @Override
    protected boolean onQueryTextSubmit(Fragment searchFragment, String s) {
        ((NewsListPresenter) ((NewsListFragment) searchFragment).getPresenter()).refreshWithKeyword(s);
        return true;
    }

    @Override
    protected void onQueryTextChange(Fragment searchFragment, String s) {
        ((NewsListFragment) searchFragment).getPresenter().clearRefreshInteractor();
    }
}
