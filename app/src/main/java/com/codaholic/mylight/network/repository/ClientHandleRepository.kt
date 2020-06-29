package com.codaholic.mylight.network.repository

import android.content.Context
import okhttp3.Cache
import okhttp3.JavaNetCookieJar
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import java.io.File
import java.net.CookieManager
import java.net.CookiePolicy
import java.util.concurrent.TimeUnit

class ClientHandleRepository {
    fun verysmall(context: Context): OkHttpClient {
        return return try {
            OkHttpClient.Builder()
                .cache(cacheData(context))
                .connectTimeout(1, TimeUnit.MINUTES)
                .readTimeout(1, TimeUnit.MINUTES)
                .writeTimeout(1, TimeUnit.MINUTES)
                .build()
        } catch (e: Exception) {
            throw RuntimeException(e)
        }
    }

    fun small(context: Context?): OkHttpClient {
        val interceptor = HttpLoggingInterceptor()
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY)
        return OkHttpClient.Builder() // .cache(cacheData(context))
            .addInterceptor(interceptor)
            .connectTimeout(10, TimeUnit.SECONDS)
            .readTimeout(10, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .build()
    }

    fun medium(context: Context, token: String): OkHttpClient {
        val cookieManager = CookieManager()
        cookieManager.setCookiePolicy(CookiePolicy.ACCEPT_ALL)

        return OkHttpClient.Builder()
            .authenticator { route, response ->
                response.request().newBuilder()
                    .header("Authorization", "Bearer $token")
                    .header("lumen_session", "AAAAAA")
                    .build()
            }
            .cache(cacheData(context))
            .cookieJar(JavaNetCookieJar(cookieManager))
            .connectTimeout(10, TimeUnit.SECONDS)
            .readTimeout(10, TimeUnit.SECONDS)
            .writeTimeout(1, TimeUnit.MINUTES)
            .build()
    }

    fun longer(context: Context?, token: String?): OkHttpClient {
        val cookieManager = CookieManager()
        cookieManager.setCookiePolicy(CookiePolicy.ACCEPT_ALL)
        return OkHttpClient.Builder()
            .authenticator { route, response ->
                response.request().newBuilder().header("Authorization", "Bearer "+token).build()
            }
            .cookieJar(JavaNetCookieJar(cookieManager))
            .connectTimeout(20, TimeUnit.SECONDS)
            .readTimeout(20, TimeUnit.SECONDS)
            .writeTimeout(2, TimeUnit.MINUTES)
            .build()
    }


    private fun cacheData(context: Context): Cache {
        val httpCacheDirectory = File(context.cacheDir, "beautycon")
        val cacheSize = 10 * 1024 * 1024 // 10 MiB

        return Cache(httpCacheDirectory, cacheSize.toLong())
    }
}