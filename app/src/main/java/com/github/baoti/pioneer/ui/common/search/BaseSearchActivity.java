package com.github.baoti.pioneer.ui.common.search;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.github.baoti.pioneer.R;
import com.github.baoti.pioneer.ui.Navigator;

/**
 * Created by liuyedong on 2015/1/2.
 */
public abstract class BaseSearchActivity extends FragmentActivity implements FragmentManager.OnBackStackChangedListener, SearchView.OnQueryTextListener {
    private static final String TAG_FRAG_SEARCH = "frag_search";

    private Toolbar toolbar;
    private SearchView searchView;
    private Fragment listFragment;
    private Fragment searchFragment;

    @Override
    protected void onDestroy() {
        getSupportFragmentManager().removeOnBackStackChangedListener(this);
        super.onDestroy();
    }

    protected SearchView setupToolbar(Toolbar toolbar) {
        this.toolbar = toolbar;
        Navigator.setupToolbarNavigation(this, toolbar);
        toolbar.inflateMenu(R.menu.search_view);
        return (SearchView) toolbar.findViewById(R.id.action_search);
    }

    protected void setupSearch(SearchView searchView, Fragment listFragment) {
        this.searchView = searchView;
        this.listFragment = listFragment;
        searchFragment = getSearchFragment();

        getSupportFragmentManager().addOnBackStackChangedListener(this);

        setupSearchView();
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
            searchFragment = createSearchFragment();
        }
        getSupportFragmentManager().beginTransaction()
//                .hide(listFragment)
//                .add(getSearchFragmentContainerId(), searchFragment, TAG_FRAG_SEARCH)
                .replace(getSearchFragmentContainerId(), searchFragment, TAG_FRAG_SEARCH)
                .addToBackStack(null)
                .commit();
    }

    protected int getSearchFragmentContainerId() {
        return R.id.frag_container;
    }

    protected Fragment getSearchFragment() {
        return getSupportFragmentManager().findFragmentByTag(TAG_FRAG_SEARCH);
    }

    protected abstract Fragment createSearchFragment();

    protected boolean isListOrSearchVisible() {
        if (listFragment == null) {
            return false;
        }
        if (listFragment.isVisible()) {
            return true;
        }
        Fragment searchFragment = getSearchFragment();
        return searchFragment != null && searchFragment.isVisible();
    }

    @Override
    public void onBackStackChanged() {
        if (searchView == null) {
            return;
        }
        searchFragment = getSearchFragment();
        if (searchFragment == null || searchFragment.isHidden()) {
            searchView.onActionViewCollapsed();
        }
        if (toolbar != null) {
            Navigator.setupToolbarNavigation(this, toolbar);
        }
    }

    @Override
    public boolean onQueryTextSubmit(String s) {
        if (searchFragment != null && searchFragment.isVisible()) {
            searchView.clearFocus();
            return onQueryTextSubmit(searchFragment, s);
        }
        return false;
    }

    protected abstract boolean onQueryTextSubmit(Fragment searchFragment, String s);

    @Override
    public boolean onQueryTextChange(String s) {
        if (searchFragment != null && searchFragment.isVisible()) {
            onQueryTextChange(searchFragment, s);
        }
        return false;
    }

    protected abstract void onQueryTextChange(Fragment searchFragment, String s);
}
