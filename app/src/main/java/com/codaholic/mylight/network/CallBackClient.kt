package com.codaholic.mylight.network

interface CallBackClient {
    fun loading()
    fun success(message: String?, idsuccess: Int)
    fun failed(message: String?)
    fun errorConnection(t: Throwable?)
    fun error(t: Throwable?)
}