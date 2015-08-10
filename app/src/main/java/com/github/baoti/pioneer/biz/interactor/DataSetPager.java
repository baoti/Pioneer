/*
 * Copyright (c) 2015 Sean Liu.
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

import com.github.baoti.pioneer.biz.DataSet;
import com.github.baoti.pioneer.biz.ResourcePage;
import com.github.baoti.pioneer.biz.exception.BizException;
import com.github.baoti.pioneer.biz.exception.NoSuchPageException;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Generic resource pager for elements that can be paged
 *
 * @param <E>
 */
public abstract class DataSetPager<E> {

    public static final int FIRST_PAGE = 1;

    public static final DataSetPager EMPTY = new DataSetPager() {
        @Override
        protected ResourcePage first() throws BizException {
            return ResourcePage.EMPTY;
        }
    };

    @SuppressWarnings("unchecked")
    public static <E> DataSetPager<E> empty() {
        return EMPTY;
    }

    public static <E> DataSetPager<E> from(final PageInteractor<E> interactor) {
        return new DataSetPager<E>() {
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
    protected final List<E> resources = new ArrayList<>();

    /**
     * Are more pages available?
     */
    protected boolean hasMore;

    protected Page current;

    /**
     * Reset the number of the next page to be requested from {@link #next()}
     * and clear all stored state
     *
     * @return this pager
     */
    public synchronized DataSetPager<E> reset() {
        page = FIRST_PAGE;
        return clear();
    }

    /**
     * Clear all stored resources and have the next call to {@link #next()} load
     * all previously loaded pages
     *
     * @return this pager
     */
    public synchronized DataSetPager<E> clear() {
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
    public DataSet<E> next() throws BizException {
        boolean emptyPage = false;
        ResourcePage<E> it;
        int pageCount;
        synchronized (this) {
            it = current;
            pageCount = count;
        }
        List<E> added = new ArrayList<>();
        boolean isNew = false;
        for (int i = 0; i < pageCount; i++) {
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
        synchronized (this) {
            int offset = resources.size();
            resources.addAll(added);
            current = new Page(isNew, offset, added, it);
            // Set page to count value if first call after call to reset()
            if (count > 1) {
                this.page = count;
                count = 1;
            }

            this.page++;
            hasMore = current.hasNext() && !emptyPage;
            return current;
        }
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

    public interface Changed {
        boolean isNew();

        int start();

        int count();
    }

    private final class Page implements DataSet<E>, ResourcePage<E>, Changed {

        private final boolean isNew;
        private final int offset;
        private final Collection<E> resources;
        private final ResourcePage<E> delegate;

        public Page(boolean isNew, int offset, Collection<E> resources, ResourcePage<E> delegate) {
            this.isNew = isNew;
            this.offset = offset;
            this.resources = resources;
            this.delegate = delegate;
        }

        @Override
        public boolean isNew() {
            return isNew;
        }

        @Override
        public int start() {
            return offset;
        }

        @Override
        public int count() {
            return resources.size();
        }

        @Override
        public Collection<E> getResources() {
            return resources;
        }

        @Override
        public boolean hasNext() {
            return delegate.hasNext();
        }

        @Override
        public ResourcePage<E> next() throws NoSuchPageException, BizException {
            return delegate.next();
        }

        @Override
        public Collection<E> loadedResources() {
            return DataSetPager.this.resources;
        }

        @Override
        public ResourcePage<E> currentPage() {
            return this;
        }
    }
}
