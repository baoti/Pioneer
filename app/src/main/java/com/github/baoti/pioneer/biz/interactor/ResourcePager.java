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

package com.github.baoti.pioneer.biz.interactor;

import android.support.annotation.WorkerThread;

import com.github.baoti.pioneer.biz.ResourcePage;
import com.github.baoti.pioneer.biz.exception.BizException;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Generic resource pager for elements that can be paged
 *
 * @param <E>
 */
public abstract class ResourcePager<E> {

    public static final int FIRST_PAGE = 1;

    public static final ResourcePager EMPTY = new ResourcePager() {
        @Override
        protected ResourcePage first() throws BizException {
            return ResourcePage.EMPTY;
        }
    };

    @SuppressWarnings("unchecked")
    public static <E> ResourcePager<E> empty() {
        return EMPTY;
    }

    public static <E> ResourcePager<E> from(final PageInteractor<E> interactor) {
        return new ResourcePager<E>() {
            @Override
            public ResourcePage<E> first() throws BizException {
                return interactor.interact();
            }
        };
    }

    /**
     * Next page to request
     */
    protected int page = FIRST_PAGE;

    /**
     * Number of pages to request
     */
    protected int count = 1;

    /**
     * All resources retrieved
     */
    protected final List<E> resources = new ArrayList<E>();

    /**
     * Are more pages available?
     */
    protected boolean hasMore;

    protected ResourcePage<E> current;

    private Changed lastChanged;

    /**
     * Reset the number of the next page to be requested from {@link #next()}
     * and clear all stored state
     *
     * @return this pager
     */
    public synchronized ResourcePager<E> reset() {
        page = FIRST_PAGE;
        return clear();
    }

    /**
     * Clear all stored resources and have the next call to {@link #next()} load
     * all previously loaded pages
     *
     * @return this pager
     */
    public synchronized ResourcePager<E> clear() {
        count = Math.max(1, page - 1);
        page = FIRST_PAGE;
        current = null;
        resources.clear();
        hasMore = true;
        return this;
    }

    public boolean hasLoadedResources() {
        return !resources.isEmpty();
    }

    /**
     * Get number of resources loaded into this pager
     *
     * @return number of resources
     */
    public int size() {
        return resources.size();
    }

    /**
     * Get resources
     *
     * @return resources
     */
    public List<E> getResources() {
        return resources;
    }

    /**
     * Get the next page of resources
     *
     * @return true if more pages
     * @throws BizException
     */
    @WorkerThread
    public synchronized boolean next() throws BizException {
        boolean emptyPage = false;
        ResourcePage<E> it = current;
        List<E> added = new ArrayList<>();
        boolean isNew = false;
        for (int i = 0; i < count; i++) {
            if (it == null) {
                isNew = true;
                it = first();
            } else if (it.hasNext()) {
                it = it.next();
            } else {
                break;
            }
            Collection<E> resourcePage = it.getResources();
            emptyPage = resourcePage.isEmpty();
            if (emptyPage) {
                break;
            }
            for (E resource : resourcePage) {
                resource = register(resource);
                if (resource == null)
                    continue;
                added.add(resource);
            }
        }
        saveLastChanged(isNew, resources.size(), added.size());
        resources.addAll(added);
        current = it;
        // Set page to count value if first call after call to reset()
        if (count > 1) {
            page = count;
            count = 1;
        }

        page++;
        hasMore = current.hasNext() && !emptyPage;
        return hasMore;
    }

    /**
     * Are more pages available to request?
     *
     * @return true if the last call to {@link #next()} returned true, false
     * otherwise
     */
    public boolean hasMore() {
        return hasMore;
    }

    /**
     * Callback to register a fetched resource before it is stored in this pager
     * <p/>
     * Sub-classes may override
     *
     * @param resource
     * @return resource
     */
    protected E register(final E resource) {
        return resource;
    }

    protected abstract ResourcePage<E> first() throws BizException;

    /**
     * Next page to request
     */
    public int getNextPage() {
        return page;
    }

    public Changed getLastChanged() {
        return lastChanged;
    }

    private void saveLastChanged(final boolean isNew, final int start, final int count) {
        lastChanged = new Changed() {
            @Override
            public boolean isNew() {
                return isNew;
            }

            @Override
            public int start() {
                return start;
            }

            @Override
            public int count() {
                return count;
            }
        };
    }

    public interface Changed {
        boolean isNew();

        int start();

        int count();
    }
}
