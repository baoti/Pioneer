package com.github.baoti.pioneer.data.api;

import com.github.baoti.pioneer.biz.exception.BizException;

/**
 * Created by liuyedong on 2014/12/27.
 */
public class ApiException extends BizException {
    private final ApiResponse response;

    public ApiException(ApiResponse response) {
        this(response, response.message);
    }

    public ApiException(ApiResponse response, String msg) {
        super(msg);
        this.response = response;
    }

    public ApiResponse getResponse() {
        return response;
    }
}
