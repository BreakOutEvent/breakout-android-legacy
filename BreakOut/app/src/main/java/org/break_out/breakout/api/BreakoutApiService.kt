package org.break_out.breakout.api

import android.content.Context
import com.facebook.stetho.okhttp3.StethoInterceptor
import java.io.IOException

import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Response
import org.break_out.breakout.manager.UserManager
import retrofit2.Retrofit
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory

/**
 * Created by florianschmidt on 20/12/2016.
 */

class BreakoutApiService {

    private val context: Context
    private val userManager: UserManager

    // TODO: Remove dependency on context
    constructor(c: Context) {
        this.context = c
        this.userManager = UserManager.getInstance(this.context)
    }

    public fun createBreakoutClient(): BreakoutClient {
        return Retrofit.Builder()
                .baseUrl("https://backend.break-out.org") // TODO: Fetch this from config!
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .client(createOkHttpClient())
                .build()
                .create(BreakoutClient::class.java)
    }

    private fun createOkHttpClient(): OkHttpClient {

        val accessToken = userManager.currentUser.accessToken

        return OkHttpClient.Builder()
                .addInterceptor { requestChain ->
                    val originalRequest = requestChain.request()
                    val request = originalRequest.newBuilder()
                            .header("User-Agent", "Breakout")
                            .build()

                    return@addInterceptor requestChain.proceed(request)
                }
                .addNetworkInterceptor(StethoInterceptor())
                .build()
    }
}
