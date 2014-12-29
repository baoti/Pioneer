package com.github.baoti.pioneer.data.api;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by liuyedong on 2014/12/27.
 */
public class ApiResponse<Payload> {
    @Expose
    @SerializedName("result")
    public Integer result;
    @Expose
    @SerializedName("message")
    public String message = "";
    @Expose
    @SerializedName("total")
    public Integer total;

    @Expose
    @SerializedName("data")
    public Payload payload;

    public boolean resultOk() {
        return result == 0;
    }

    public int checkedResult() throws ApiException {
        if (!resultOk()) {
            throw new ApiException(this);
        }
        return result;
    }

    public Payload checkedPayload() throws ApiException {
        checkedResult();
        if (payload == null) {
            throw new ApiException(this, "No data");
        }
        return payload;
    }
}
