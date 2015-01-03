package com.github.baoti.pioneer.ui.news;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.github.baoti.pioneer.R;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by Administrator on 2015/1/2.
 */
public class NewsListActivity extends FragmentActivity implements FragmentManager.OnBackStackChangedListener, SearchView.OnQueryTextListener {
    private static final String TAG_FRAG_SEARCH = "search_news";

    public static Intent intentToLaunch(Context context) {
        return new Intent(context, NewsListActivity.class);
    }

    @InjectView(R.id.app_toolbar)
    Toolbar toolbar;

    private SearchView searchView;

    private NewsListFragment listFragment;
    private NewsListFragment searchFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news_list);

        ButterKnife.inject(this);

        listFragment = (NewsListFragment) getSupportFragmentManager().findFragmentById(R.id.frag_news_list);
        listFragment.enableInitialResources();

        searchFragment = getSearchFragment();

        setupToolbar();

        searchView = (SearchView) toolbar.findViewById(R.id.action_search);
        setupSearchView();

        getSupportFragmentManager().addOnBackStackChangedListener(this);
    }

    private void setupToolbar() {
        toolbar.setNavigationIcon(R.drawable.abc_ic_ab_back_mtrl_am_alpha);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        toolbar.inflateMenu(R.menu.search_view);
    }

    private void setupSearchView() {
        searchView.setOnCloseListener(new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {
                onBackPressed();
                return true;
            }
        });
        searchView.setOnSearchClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openSearchFragment();
            }
        });
        searchView.setOnQueryTextListener(this);
        if (searchFragment != null) {
            searchView.onActionViewExpanded();
        }
    }

    private void openSearchFragment() {
        if (getSearchFragment() != null) {
            return;
        }
        if (searchFragment == null) {
            searchFragment = NewsListFragment.newInstance();
        }
        getSupportFragmentManager().beginTransaction()
                .hide(listFragment)
                .add(R.id.frag_container, searchFragment, TAG_FRAG_SEARCH)
                .addToBackStack(null)
                .commit();
    }

    private NewsListFragment getSearchFragment() {
        return (NewsListFragment) getSupportFragmentManager().findFragmentByTag(TAG_FRAG_SEARCH);
    }

    @Override
    public void onBackStackChanged() {
        searchFragment = getSearchFragment();
        if (searchFragment == null || searchFragment.isHidden()) {
            searchView.onActionViewCollapsed();
        }
    }

    @Override
    public boolean onQueryTextSubmit(String s) {
        searchView.clearFocus();
        if (searchFragment != null && searchFragment.isVisible()) {
            searchFragment.getPresenter().refreshWithKeyword(s);
            return true;
        }
        return false;
    }

    @Override
    public boolean onQueryTextChange(String s) {
        if (searchFragment != null && searchFragment.isVisible()) {
            searchFragment.getPresenter().clearRefreshInteractor();
        }
        return false;
    }
}
