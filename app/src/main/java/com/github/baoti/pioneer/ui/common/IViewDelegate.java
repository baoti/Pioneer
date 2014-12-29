package com.github.baoti.pioneer.ui.common;

/**
 * Created by liuyedong on 2014/12/28.
 */
public interface IViewDelegate<D extends IView> extends IView {
    D getDelegator();
}
