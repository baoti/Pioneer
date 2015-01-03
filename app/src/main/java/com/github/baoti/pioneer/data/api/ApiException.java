package com.github.baoti.pioneer.data.api;

import com.github.baoti.pioneer.data.DataException;

/**
 * Created by liuyedong on 2014/12/27.
 */
public class ApiException extends DataException {
    private final ApiResponse response;

    public ApiException(ApiResponse response) {
        this(response, response.message);
    }

    public ApiException(ApiResponse response, String msg) {
        super(msg);
        this.response = response;
    }

    public ApiException(Exception error) {
        super(error.getMessage(), error);
        this.response = null;
    }

    public boolean hasResponse() {
        return response != null;
    }

    public ApiResponse getResponse() {
        return response;
    }
}
