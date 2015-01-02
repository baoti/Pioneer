package com.github.baoti.pioneer.biz.exception;

import java.util.NoSuchElementException;

/**
 * Created by liuyedong on 14-12-31.
 */
public class NoSuchPageException extends NoSuchElementException {

    /**
     * Cause exception
     */
    protected final BizException cause;

    /**
     * Create no such page exception
     *
     * @param cause
     */
    public NoSuchPageException(BizException cause) {
        this.cause = cause;
    }

    @Override
    public String getMessage() {
        return cause != null ? cause.getMessage() : super.getMessage();
    }

    @Override
    public BizException getCause() {
        return cause;
    }
}
