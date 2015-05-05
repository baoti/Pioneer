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

package com.github.baoti.pioneer.app.loader;

import android.content.Context;

import com.github.baoti.pioneer.biz.exception.BizException;
import com.github.baoti.pioneer.biz.interactor.ResourcePager;

import java.util.List;

/**
 * 分页数据加载器
 * Created by liuyedong on 15-4-20.
 */
public class PageLoader<E> extends ThrowableLoader<List<E>> {

    private final ResourcePager<E> pager;
    private boolean isFirstPage;
    private boolean loading;
    private boolean failed;

    /**
     * Create loader for context and seeded with initial data
     *
     * @param context
     * @param data
     */
    public PageLoader(Context context, ResourcePager<E> pager, List<E> data) {
        super(context, data);
        this.pager = pager;
        isFirstPage = pager.getNextPage() == ResourcePager.FIRST_PAGE;
    }

    @Override
    public List<E> loadData() throws BizException {
        isFirstPage = pager.getNextPage() == ResourcePager.FIRST_PAGE;
        try {
            pager.next();
        } catch (BizException e) {
            failed = true;
            throw e;
        }
        failed = false;
        return pager.getResources();
    }

    @Override
    protected void onForceLoad() {
        loading = true;
        onLoadingChanged();
        super.onForceLoad();
    }

    @Override
    public void onCanceled(List<E> data) {
        super.onCanceled(data);
        loading = false;
        onLoadingChanged();
    }

    @Override
    public void commitContentChanged() {
        super.commitContentChanged();
        loading = false;
        onLoadingChanged();
    }

    protected void onLoadingChanged() {

    }

    public ResourcePager<E> getPager() {
        return pager;
    }

    public boolean hasNextPage() {
        return pager.hasMore();
    }

    public boolean isFirstPage() {
        return isFirstPage;
    }

    public boolean isLoading() {
        return loading;
    }

    public boolean isFailed() {
        return failed;
    }

    public boolean isLoadingNextPage() {
        return loading && !isFirstPage;
    }

    public boolean isFailedToLoadNextPage() {
        return failed && !isFirstPage;
    }
}
