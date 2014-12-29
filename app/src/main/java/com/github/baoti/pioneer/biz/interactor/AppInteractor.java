package com.github.baoti.pioneer.biz.interactor;

import com.github.baoti.pioneer.biz.exception.BizException;

/**
 * Created by liuyedong on 14-12-18.
 */
public interface AppInteractor {
    boolean isFirstLaunching();

    void initialize() throws BizException;
}
