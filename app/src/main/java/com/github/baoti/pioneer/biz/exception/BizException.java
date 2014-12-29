package com.github.baoti.pioneer.biz.exception;

/**
 * Created by liuyedong on 14-12-18.
 */
public class BizException extends Exception {
    public BizException() {
        super();
    }

    public BizException(String detailMessage) {
        super(detailMessage);
    }

    public BizException(Throwable e) {
        super(e);
    }

    public BizException(String detailMessage, Throwable throwable) {
        super(detailMessage, throwable);
    }
}
