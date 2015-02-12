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

import com.github.baoti.pioneer.biz.ResourcePage;
import com.github.baoti.pioneer.biz.exception.BizException;

import java.util.Collection;

/**
 * Created by liuyedong on 2015/1/1.
 */
public interface PageInteractor<E> extends DeferredInteractor<ResourcePage<E>> {

    public abstract class Simple<E> implements PageInteractor<E> {

        private final int pageStart;
        protected final int pageSize;

        public Simple(int pageStart, int pageSize) {
            this.pageStart = pageStart;
            this.pageSize = pageSize;
        }

        @Override
        public ResourcePage<E> interact() throws BizException {
            return createPage(pageStart, loadResources(pageStart));
        }

        protected abstract Collection<E> loadResources(int pageIndex) throws BizException;

        protected boolean hasNextPage(int pageIndex, Collection<E> resources) {
            return !resources.isEmpty() && (pageSize <= 0 || resources.size() >= pageSize);
        }

        protected ResourcePage<E> nextPage(int pageIndex) throws BizException {
            return createPage(pageIndex + 1, loadResources(pageIndex + 1));
        }

        protected ResourcePage<E> createPage(int pageIndex, Collection<E> resources) {
            return new Page(pageIndex, resources);
        }

        class Page implements ResourcePage<E> {
            private final int pageIndex;
            private final Collection<E> resources;

            public Page(int pageIndex, Collection<E> resources) {
                this.pageIndex = pageIndex;
                this.resources = resources;
            }

            @Override
            public Collection<E> getResources() {
                return resources;
            }

            @Override
            public boolean hasNext() {
                return hasNextPage(pageIndex, resources);
            }

            @Override
            public ResourcePage<E> next() throws BizException {
                return nextPage(pageIndex);
            }
        }
    }
}
