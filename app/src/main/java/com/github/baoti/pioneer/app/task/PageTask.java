package com.github.baoti.pioneer.app.task;

import com.github.baoti.pioneer.biz.ResourcePage;
import com.github.baoti.pioneer.biz.exception.BizException;
import com.github.baoti.pioneer.biz.interactor.DeferredInteractor;
import com.github.baoti.pioneer.biz.interactor.PageInteractor;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Created by Administrator on 2015/1/1.
 */
public class PageTask<E> implements Tasks.SafeTask<Collection<E>> {
    private Tasks.LifecycleListener listener;
    private Task task;
    private List<E> resources;

    public void setLifecycleListener(Tasks.LifecycleListener listener) {
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

    public boolean isRefreshing() {
        return isRunning() && isFirstPage();
    }

    public void refresh(PageInteractor<E> interactor) {
        cancel(true);
        task = new Task(true, interactor);
        task.executeOnDefaultThreadPool();
    }

    public boolean hasNextPage() {
        return !(task != null && task.getResult() != null) || task.getResult().hasNext();
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
                return LoadState.REFRESHING;
            } else {
                return LoadState.LOADING_NEXT;
            }
        }
        if (task.getResult() == null) {
            // failed, retry
            if (!task.isFirst) {
                cancel(true);
                task = task.recreate();
                task.executeOnDefaultThreadPool();
                return LoadState.LOADING_NEXT;
            } else {
                return LoadState.OTHER;
            }
        }
        final ResourcePage<E> page = task.getResult();
        if (!page.hasNext()) {
            return LoadState.NO_NEXT;
        }
        cancel(true);
        task = new Task(false, new DeferredInteractor<ResourcePage<E>>() {
            @Override
            public ResourcePage<E> interact() throws BizException {
                return page.next();
            }
        });
        task.executeOnDefaultThreadPool();
        return LoadState.LOADING_NEXT;
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
        storeResources(task);
        if (listener != null) {
            listener.onStopped(this);
        }
    }

    private void storeResources(Task task) {
        if (task.getResult() != null) {
            if (resources == null) {
                resources = new ArrayList<>();
            }
            if (task.isFirst) {
                resources.clear();
            }
            resources.addAll(task.getResult().getResources());
        }
    }

    public enum LoadState {
        /** refreshing first page */
        REFRESHING,
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
