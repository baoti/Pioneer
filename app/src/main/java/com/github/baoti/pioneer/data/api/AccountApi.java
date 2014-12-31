package com.github.baoti.pioneer.data.api;

import com.github.baoti.pioneer.entity.Account;

import retrofit.http.POST;
import retrofit.http.Query;

/**
 * Created by liuyedong on 14-12-18.
 */
public interface AccountApi {
    @POST("/account/login")
    ApiResponse<Account> login(@Query("account") String accountId, @Query("pwd") String password)
            throws ApiException;
}
