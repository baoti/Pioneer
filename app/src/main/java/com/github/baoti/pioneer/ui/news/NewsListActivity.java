package com.github.baoti.pioneer.ui.news;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.ViewTreeObserver;

import com.github.baoti.pioneer.AppMain;
import com.github.baoti.pioneer.R;
import com.github.baoti.pioneer.entity.News;
import com.github.baoti.pioneer.ui.common.ActivityView;

import java.util.Collection;

import javax.inject.Inject;

import butterknife.ButterKnife;
import butterknife.InjectView;
import dagger.Lazy;

/**
 * Created by Administrator on 2015/1/2.
 */
public class NewsListActivity extends ActivityView<INewsListView, NewsListPresenter> implements INewsListView, SearchView.OnQueryTextListener {

    public static Intent intentToLaunch(Context context) {
        return new Intent(context, NewsListActivity.class);
    }

    @Inject
    Lazy<NewsListPresenter> presenterLazy;

    @InjectView(R.id.app_toolbar)
    Toolbar toolbar;

    @InjectView(R.id.srl_swipe_refresh)
    SwipeRefreshLayout swipeRefreshLayout;

    @InjectView(R.id.rv_list)
    RecyclerView recyclerView;

    private NewsListAdapter newsListAdapter;
    private LinearLayoutManager layoutManager;

    @Override
    protected NewsListPresenter createPresenter(INewsListView view) {
        return presenterLazy.get();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AppMain.globalGraph().plus(new NewsModule()).inject(this);
        setContentView(R.layout.app_swip_recycler_list);

        ButterKnife.inject(this);

        setupToolbar();
        setupNewsListView();
    }

    private void setupToolbar() {
        toolbar.setNavigationIcon(R.drawable.abc_ic_ab_back_mtrl_am_alpha);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        toolbar.inflateMenu(R.menu.search);
        SearchView searchView = (SearchView) toolbar.findViewById(R.id.action_search);
        searchView.setOnCloseListener(new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {
                return getPresenter().onCloseSearchView();
            }
        });
        searchView.setOnSearchClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        searchView.setOnQueryTextListener(this);
    }

    private void setupNewsListView() {
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                int lastVisibleItem = layoutManager.findLastVisibleItemPosition();
                int lastItem = layoutManager.getItemCount() - 1;
                if (lastVisibleItem >= lastItem && newState == RecyclerView.SCROLL_STATE_IDLE) {
                    getPresenter().loadNextPage();
                }
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                boolean enableRefresh = layoutManager.findFirstCompletelyVisibleItemPosition() == 0;
                swipeRefreshLayout.setEnabled(enableRefresh);
            }
        });
        recyclerView.setAdapter(new NewsListAdapter(getLayoutInflater(), null));
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getPresenter().onRefresh();
            }
        });
        swipeRefreshLayout.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                getPresenter().onSwipeRefreshPrepared();
            }
        });
    }

    @Override
    public void showNewsList(Collection<News> resources) {
        if (newsListAdapter == null) {
            newsListAdapter = new NewsListAdapter(getLayoutInflater(), getPresenter());
            recyclerView.setAdapter(newsListAdapter);
        }
        newsListAdapter.changeItems(resources);
    }

    @Override
    public void showRefreshing() {
        swipeRefreshLayout.setRefreshing(true);
    }

    @Override
    public void hideRefreshing() {
        swipeRefreshLayout.setRefreshing(false);
    }

    @Override
    public void showLoadingMore() {
        if (layoutManager.findLastVisibleItemPosition() >= layoutManager.getItemCount() - 1) {
            recyclerView.getAdapter().notifyDataSetChanged();
        }
    }

    @Override
    public boolean onQueryTextSubmit(String s) {
        return getPresenter().onQueryTextSubmit(s);
    }

    @Override
    public boolean onQueryTextChange(String s) {
        return getPresenter().onQueryTextChange(s);
    }
}
