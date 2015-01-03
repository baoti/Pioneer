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
