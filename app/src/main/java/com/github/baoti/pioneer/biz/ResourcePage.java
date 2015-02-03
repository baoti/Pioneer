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

package com.github.baoti.pioneer.biz;

import com.github.baoti.pioneer.biz.exception.BizException;
import com.github.baoti.pioneer.biz.exception.NoSuchPageException;

import java.util.Collection;

/**
 * Created by liuyedong on 2015/1/1.
 */
public interface ResourcePage<E> {
    Collection<E> getResources();
    boolean hasNext();
    ResourcePage<E> next() throws NoSuchPageException, BizException;

    public static abstract class Simple<E> implements ResourcePage<E> {
        private final Collection<E> resources;
        private final int pageSize;

        public Simple(Collection<E> resources) {
            this(resources, -1);
        }

        public Simple(Collection<E> resources, int pageSize) {
            this.resources = resources;
            this.pageSize = pageSize;
        }

        @Override
        public Collection<E> getResources() {
            return resources;
        }

        @Override
        public boolean hasNext() {
            return !resources.isEmpty() && (pageSize <= 0 || resources.size() >= pageSize);
        }
    }
}
