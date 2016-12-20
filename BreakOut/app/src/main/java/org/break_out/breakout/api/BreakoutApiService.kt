package org.break_out.breakout.api

import java.io.IOException

import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Response
import retrofit2.Retrofit
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory

/**
 * Created by florianschmidt on 20/12/2016.
 */

class BreakoutApiService {

    private fun createBreakoutClient(): BreakoutClient {
        return Retrofit.Builder()
                .baseUrl("https://backend.break-out.org") // TODO: Fetch this from config!
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .build()
                .create(BreakoutClient::class.java)
    }

    private fun test() {
        val httpClient = OkHttpClient.Builder()
        httpClient.addInterceptor { }
    }
}
