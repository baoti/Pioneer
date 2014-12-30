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
