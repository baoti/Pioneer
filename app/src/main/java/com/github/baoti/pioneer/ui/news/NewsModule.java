package com.github.baoti.pioneer.ui.news;

import com.github.baoti.pioneer.AppMainModule;
import com.github.baoti.pioneer.ui.news.edit.NewsEditFragment;
import com.github.baoti.pioneer.ui.news.list.NewsListFragment;

import dagger.Module;

/**
 * Created by liuyedong on 2015/1/2.
 */
@Module(
        injects = {
                NewsListFragment.class,
                NewsEditFragment.class
        },
        addsTo = AppMainModule.class
)
public class NewsModule {
}
