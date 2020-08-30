package com.codaholic.mylight.network;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import okhttp3.ResponseBody;

/**
 * Created by vim on 05/02/18.
 */

public class ResponseAPI {



    public final Status status;

    @Nullable
    public final retrofit2.Response<ResponseBody> data;

    @Nullable
    public final Throwable error;

    private ResponseAPI(Status status, @Nullable retrofit2.Response<ResponseBody> data, @Nullable Throwable error) {
        this.status = status;
        this.data = data;
        this.error = error;
    }

    public static ResponseAPI loading() {
        return new ResponseAPI(Status.LOADING, null, null);
    }

    public static ResponseAPI success(@NonNull retrofit2.Response<ResponseBody> data) {
        return new ResponseAPI(Status.SUCCESS, data, null);
    }

    public static ResponseAPI error(@NonNull Throwable error) {
        return new ResponseAPI(Status.ERROR, null, error);
    }



}
