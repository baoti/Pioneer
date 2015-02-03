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

package com.github.baoti.pioneer.data.api.internal.mapper;

import com.github.baoti.pioneer.data.api.ApiResponse;

/**
 * Created by liuyedong on 14-12-29.
 */
public class Mapper {
    public interface Transformer<Src, Dst> {
        Dst transform(Src src);
    }

    public static <A, B> ApiResponse<B> transform(ApiResponse<A> a, Transformer<A, B> transformer) {
        ApiResponse<B> dst = new ApiResponse<>();
        dst.message = a.message;
        dst.total = a.total;
        dst.result = a.result;
        dst.payload = transformer.transform(a.payload);
        return dst;
    }
}
