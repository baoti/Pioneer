package com.github.baoti.pioneer.ui.news;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.github.baoti.pioneer.R;
import com.github.baoti.pioneer.entity.News;
import com.github.baoti.pioneer.ui.common.base.BaseContainerActivity;
import com.github.baoti.pioneer.ui.common.holder.OnViewHolderClickListener;
import com.github.baoti.pioneer.ui.common.search.BaseSearchActivity;
import com.github.baoti.pioneer.ui.news.detail.NewsDetailFragment;
import com.github.baoti.pioneer.ui.news.edit.NewsEditFragment;
import com.github.baoti.pioneer.ui.news.list.NewsListFragment;
import com.github.baoti.pioneer.ui.news.list.NewsListPresenter;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by liuyedong on 2015/1/2.
 */
public class NewsActivity extends BaseSearchActivity implements OnViewHolderClickListener<News> {
    public static final String EXTRA_NEWS = "news";     // type: News
    public static final String EXTRA_EDITABLE = "editable";     // type: boolean

    public static final int RESULT_PICKED = RESULT_FIRST_USER;

    private static final String TAG_FRAG_LIST = "frag_list";
    private static final String TAG_FRAG_DETAIL = "frag_detail";
    private static final String TAG_FRAG_EDIT = "frag_edit";

    private static Intent withAction(Context context, String action) {
        Intent intent = new Intent(context, NewsActivity.class);
        intent.setAction(action);
        return intent;
    }

    public static Intent actionView(Context context) {
        return withAction(context, Intent.ACTION_VIEW);
    }

    public static Intent actionView(Context context, News news) {
        return withAction(context, Intent.ACTION_VIEW)
                .putExtra(EXTRA_EDITABLE, true)
                .putExtra(EXTRA_NEWS, news);
    }

    public static Intent actionEdit(Context context) {
        return withAction(context, Intent.ACTION_EDIT)
                .putExtra(EXTRA_EDITABLE, true);
    }

    public static Intent actionEdit(Context context, News news) {
        return withAction(context, Intent.ACTION_EDIT)
                .putExtra(EXTRA_EDITABLE, true)
                .putExtra(EXTRA_NEWS, news);
    }

    public static Intent actionPick(Context context) {
        return withAction(context, Intent.ACTION_PICK);
    }

    public static News pickedNews(int resultCode, Intent data) {
        if (resultCode != RESULT_PICKED) {
            return null;
        }
        return (News) data.getSerializableExtra(EXTRA_NEWS);
    }

    @InjectView(R.id.app_toolbar)
    Toolbar toolbar;

    private boolean inPickMode;
    private boolean showList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news);

        ButterKnife.inject(this);

        getSupportFragmentManager().addOnBackStackChangedListener(this);

        String action = getIntent().getAction();
        showList = !getIntent().hasExtra(EXTRA_NEWS);
        inPickMode = Intent.ACTION_PICK.equals(action);

        initContentFragment(savedInstanceState, action);
    }

    private void initContentFragment(Bundle savedInstanceState, String action) {
        if (showList) {
            NewsListFragment listFragment;
            if (savedInstanceState == null) {
                listFragment = NewsListFragment.newInstance();
                addContentFragment(listFragment, TAG_FRAG_LIST);
            } else {
                listFragment = (NewsListFragment) getSupportFragmentManager()
                        .findFragmentByTag(TAG_FRAG_LIST);
            }
            listFragment.setOnItemClickedListener(this);
            listFragment.enableInitialResources();

            setupSearch(setupToolbar(toolbar), listFragment);
        } else {
            toolbar.setVisibility(View.GONE);
            if (savedInstanceState == null) {
                if (Intent.ACTION_EDIT.equals(action)) {
                    NewsEditFragment fragment = NewsEditFragment.newInstance(
                            getIntent().getExtras());
                    addContentFragment(fragment, TAG_FRAG_EDIT);
                } else {
                    NewsDetailFragment fragment = NewsDetailFragment.newInstance(
                            getIntent().getExtras());
                    addContentFragment(fragment, TAG_FRAG_DETAIL);
                }
            }
        }
    }

    @Override
    protected void onDestroy() {
        getSupportFragmentManager().removeOnBackStackChangedListener(this);
        super.onDestroy();
    }

    @Override
    public void onBackStackChanged() {
        super.onBackStackChanged();
        toolbar.setVisibility(isListOrSearchVisible() ? View.VISIBLE : View.GONE);
    }

    private void addContentFragment(Fragment fragment, String tag) {
        getSupportFragmentManager().beginTransaction()
                .setTransition(BaseContainerActivity.TRANSITION_INIT_FRAGMENT)
                .add(getSearchFragmentContainerId(), fragment, tag)
                .commit();
    }

    @Override
    protected Fragment createSearchFragment() {
        return NewsListFragment.newInstance().setOnItemClickedListener(this);
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

    @Override
    public void onViewHolderClick(RecyclerView.ViewHolder viewHolder, News item) {
        if (inPickMode) {
            Intent data = new Intent();
            data.putExtra(EXTRA_NEWS, item);
            setResult(RESULT_PICKED, data);
            finish();
        } else {
            navigateToDetail(item);
        }
    }

    private void navigateToDetail(News item) {
        Bundle args = new Bundle(2);
        args.putSerializable(EXTRA_NEWS, item);
        if (getIntent().hasExtra(EXTRA_EDITABLE)) {
            args.putBoolean(EXTRA_EDITABLE, getIntent().getBooleanExtra(EXTRA_EDITABLE, false));
        }
        NewsDetailFragment fragment = NewsDetailFragment.newInstance(args);
        getSupportFragmentManager().beginTransaction()
                .replace(getSearchFragmentContainerId(), fragment, TAG_FRAG_DETAIL)
                .addToBackStack(null)
                .commit();
    }
}
