package com.github.baoti.pioneer.ui.news;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;

import com.github.baoti.pioneer.AppMain;
import com.github.baoti.pioneer.entity.News;
import com.github.baoti.pioneer.ui.common.FragmentView;
import com.github.baoti.pioneer.ui.common.Presenter;

import java.util.Collection;

import javax.inject.Inject;

import dagger.Lazy;

/**
 * Created by Administrator on 2015/1/2.
 */
public class NewsListFragment extends FragmentView<INewsListView, NewsListPresenter> implements INewsListView {

    public static NewsListFragment newInstance() {
        return new NewsListFragment();
    }

    @Inject
    Lazy<NewsListPresenter> presenterLazy;

    private SwipeRefreshLayout swipeRefreshLayout;
    private RecyclerView recyclerView;
    private LinearLayoutManager layoutManager;
    private NewsListAdapter adapter;

    private boolean swipeRefreshEnabled;

    @Override
    protected NewsListPresenter createPresenter(INewsListView view) {
        return presenterLazy.get();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        swipeRefreshLayout = new SwipeRefreshLayout(getActivity());
        recyclerView = new RecyclerView(getActivity());
        layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);
        swipeRefreshLayout.addView(recyclerView);
        return swipeRefreshLayout;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
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
                updateSwipeRefreshLayoutEnabled();
            }
        });
        swipeRefreshLayout.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                getPresenter().onSwipeRefreshPrepared();
            }
        });

        setRetainInstance(true);
        AppMain.globalGraph().plus(new NewsModule()).inject(this);
        super.onViewCreated(view, savedInstanceState);
    }

    private void updateSwipeRefreshLayoutEnabled() {
        boolean enableRefresh = layoutManager.findFirstCompletelyVisibleItemPosition() == 0;
        swipeRefreshLayout.setEnabled(swipeRefreshEnabled && enableRefresh);
    }

    @Override
    public void onPresenterTaken(Presenter presenter) {
        swipeRefreshLayout.setOnRefreshListener(getPresenter());
        adapter = new NewsListAdapter(LayoutInflater.from(getActivity()), getPresenter());
        recyclerView.setAdapter(adapter);
    }

    @Override
    public void showNewsList(Collection<News> resources) {
        adapter.changeItems(resources);
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
    public void enableSwipeRefreshing() {
        swipeRefreshEnabled = true;
        updateSwipeRefreshLayoutEnabled();
    }

    @Override
    public void disableSwipeRefreshing() {
        swipeRefreshEnabled = false;
        updateSwipeRefreshLayoutEnabled();
    }

    public void enableInitialResources() {
        getPresenter().enableInitialResources();
    }
}
