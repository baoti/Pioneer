package com.github.baoti.pioneer.biz.interactor;

import com.github.baoti.pioneer.biz.exception.BizException;

/**
 * Created by liuyedong on 14-12-23.
 */
public interface DeferredInteractor<V> {
    V interact() throws BizException;
}
