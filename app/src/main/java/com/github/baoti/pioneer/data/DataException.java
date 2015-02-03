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

package com.github.baoti.pioneer.data;

import com.github.baoti.pioneer.biz.exception.BizException;

/**
 * Created by liuyedong on 2015/1/1.
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
