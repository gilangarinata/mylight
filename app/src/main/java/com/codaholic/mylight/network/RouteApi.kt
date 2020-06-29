package com.codaholic.mylight.network

import io.reactivex.Observable
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.*
import java.util.*

interface RouteApi {
    @GET
    fun setRouteGet(@Url endpoint: String?, @QueryMap field: HashMap<String?, String?>?): Observable<Response<ResponseBody?>?>?

    @DELETE
    fun setRouteDelete(@Url endpoint: String?, @QueryMap field: HashMap<String?, String?>?): Observable<Response<ResponseBody?>?>?

    @FormUrlEncoded
    @POST
    fun setRoutePost(@Url url: String?, @FieldMap a: HashMap<String?, String?>?): Observable<Response<ResponseBody?>?>?

    @Multipart
    @POST
    fun setRoutePostMultipath(@Url url: String?, @PartMap file: Map<String?, RequestBody?>?): Observable<Response<ResponseBody?>?>?

    @Streaming
    @GET
    fun setRouteStreaming(@Url fileUrl: String?): Observable<Response<ResponseBody?>?>?
}