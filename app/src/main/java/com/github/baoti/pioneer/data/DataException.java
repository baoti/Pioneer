package com.github.baoti.pioneer.data;

import com.github.baoti.pioneer.biz.exception.BizException;

/**
 * Created by Administrator on 2015/1/1.
 */
public class DataException extends BizException {
    public DataException(String detailMessage) {
        super(detailMessage);
    }

    public DataException(Throwable e) {
        super(e);
    }

    public DataException(String detailMessage, Throwable throwable) {
        super(detailMessage, throwable);
    }
}
