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
