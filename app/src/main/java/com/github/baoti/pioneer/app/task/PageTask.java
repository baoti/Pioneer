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

package com.github.baoti.pioneer.app.task;

import com.github.baoti.pioneer.biz.ResourcePage;
import com.github.baoti.pioneer.biz.exception.BizException;
import com.github.baoti.pioneer.biz.interactor.DeferredInteractor;
import com.github.baoti.pioneer.biz.interactor.PageInteractor;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import timber.log.Timber;

/**
 * Created by liuyedong on 2015/1/1.
 */
public class PageTask<E> implements Tasks.SafeTask<Collection<E>> {
    private LifecycleListener<E> listener;
    /**
     * 最后一次运行的 task
     */
    private Task task;
    /**
     * 最后一次成功获得的 结果
     */
    private ResourcePage<E> page;
    /**
     * 已获得的所有资源
     */
    private List<E> resources;

    private boolean failedToLoadNextPage;

    public void setLifecycleListener(LifecycleListener<E> listener) {
        this.listener = listener;
    }

    @Override
    public boolean isRunning() {
        return task != null && task.isRunning();
    }

    @Override
    public boolean hasResultOrException() {
        return task != null && task.hasResultOrException();
    }

    @Override
    public Exception getException() {
        if (task != null) {
            return task.getException();
        }
        return null;
    }

    @Override
    public Collection<E> getResult() {
        if (task != null && task.getResult() != null) {
            return task.getResult().getResources();
        }
        return null;
    }

    public boolean hasLoadedResources() {
        return resources != null;
    }

    public Collection<E> getLoadedResources() {
        return resources;
    }

    public boolean isFirstPage() {
        return task == null || task.isFirst;
    }

    public boolean isLoadingFirstPage() {
        return isRunning() && isFirstPage();
    }

    public void loadFirstPage(PageInteractor<E> interactor) {
        Timber.v("loadFirstPage");
        cancel(true);
        task = new Task(true, interactor);
        task.executeOnDefaultThreadPool();
    }

    public boolean hasNextPage() {
        return page == null || page.hasNext();
    }

    public boolean isLoadingNextPage() {
        return isRunning() && !isFirstPage();
    }

    public LoadState loadNextPage() {
        if (task == null) {
            return LoadState.OTHER;
        }
        if (task.isRunning()) {
            if (task.isFirst) {
                return LoadState.LOADING_FIRST;
            } else {
                return LoadState.LOADING_NEXT;
            }
        }
        if (page == null) {
            // fail to load first page
            return LoadState.OTHER;
        }
        if (!page.hasNext()) {
            return LoadState.NO_NEXT;
        }
        if (task.getResult() == null) {
            // failed, retry
            if (!task.isFirst) {
                Timber.v("Retry loading next");
                retry();
                return LoadState.LOADING_NEXT;
            }
        }
        Timber.v("loadNextPage");
        cancel(true);
        final ResourcePage<E> prevPage = page;
        task = new Task(false, new PageInteractor<E>() {
            @Override
            public ResourcePage<E> interact() throws BizException {
                return prevPage.next();
            }
        });
        task.executeOnDefaultThreadPool();
        return LoadState.LOADING_NEXT;
    }

    public boolean isFailedToLoadNextPage() {
        return failedToLoadNextPage;
    }

    public void retry() {
        if (task == null) {
            return;
        }
        if (task.isRunning()) {
            return;
        }
        Timber.v("retry");
        Task retryTask = task.recreate();
        cancel(true);
        task = retryTask;
        task.executeOnDefaultThreadPool();
    }

    public final boolean cancel(boolean mayInterruptIfRunning) {
        if (task != null) {
            boolean cancelled = task.cancel(mayInterruptIfRunning);
            task = null;
            return cancelled;
        }
        return true;
    }

    private void onStarted(Task task) {
        if (listener != null) {
            listener.onStarted(this);
        }
    }

    private void onStopped(Task task) {
        failedToLoadNextPage = !task.isFirst
                && task.hasResultOrException() && task.getResult() == null;
        storeResources(task);
        if (listener != null) {
            listener.onStopped(this);
        }
    }

    private void storeResources(Task task) {
        if (task.getResult() != null) {
            page = task.getResult();
            if (resources == null) {
                resources = new ArrayList<>();
            }
            int start = resources.size();
            int before = 0;
            int count = page.getResources().size();
            if (task.isFirst) {
                resources.clear();

                before = start;
                start = 0;
            }
            resources.addAll(page.getResources());
            if (listener != null) {
                listener.onPageChanged(this, start, before, count);
            }
        }
    }

    public interface LifecycleListener<E> extends Tasks.LifecycleListener {
        /**
         * 分页资源发生了改变
         * @param pageTask 分页任务
         * @param start 从 start 开始
         * @param before before 条旧数据
         * @param count 改变为新的 count 条
         */
        void onPageChanged(PageTask<E> pageTask, int start, int before, int count);
    }

    public enum LoadState {
        /** loading first page */
        LOADING_FIRST,
        /** loading next page */
        LOADING_NEXT,
        /** no next page */
        NO_NEXT,
        OTHER
    }

    private final Tasks.LifecycleListener lifecycleListener = new Tasks.LifecycleListener() {
        @Override
        public void onStarted(Tasks.SafeTask task) {
            //noinspection unchecked
            PageTask.this.onStarted((Task) task);
        }

        @Override
        public void onStopped(Tasks.SafeTask task) {
            //noinspection unchecked
            PageTask.this.onStopped((Task) task);
        }
    };

    public class Task extends InteractorTask<Void, ResourcePage<E>> {
        private boolean isFirst;

        public Task(boolean isFirst, DeferredInteractor<ResourcePage<E>> interactor) {
            super(interactor);
            setLifecycleListener(lifecycleListener);
            this.isFirst = isFirst;
        }

        public Task recreate() {
            return new Task(isFirst, deferredInteractor);
        }
    }
}
