package org.break_out.breakout.api

import android.content.Context
import com.facebook.stetho.okhttp3.StethoInterceptor
import java.io.IOException

import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Response
import okhttp3.ResponseBody
import org.break_out.breakout.manager.UserManager
import org.break_out.breakout.util.URLUtils
import retrofit2.Retrofit
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import rx.Observable
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers

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

    private fun createBreakoutClient(accessToken: String? = null): BreakoutClient {
        return Retrofit.Builder()
                .baseUrl(URLUtils.getBaseUrl(context))
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .client(createOkHttpClient(accessToken))
                .build()
                .create(BreakoutClient::class.java)
    }

    private fun createOkHttpClient(accessToken: String?): OkHttpClient {

        val builder = OkHttpClient.Builder()
        if (!accessToken.isNullOrEmpty()) {
            builder.addInterceptor(authInterceptor(accessToken!!))
        }

        builder.addNetworkInterceptor(StethoInterceptor())
                .addInterceptor(userAgentInterceptor())

        return builder.build()
    }

    private fun authInterceptor(accessToken: String) = Interceptor {
        val request = it.request().newBuilder()
                .header("Authorization", "Bearer $accessToken")
                .build()
        return@Interceptor it.proceed(request)
    }

    private fun userAgentInterceptor() = Interceptor {
        val request = it.request().newBuilder()
                .header("User-Agent", "BreakoutAndroidApp")
                .build()
        return@Interceptor it.proceed(request)
    }

    fun likePosting(postingId: Integer): Observable<ResponseBody> {
        val currentTimeInSeconds = System.currentTimeMillis() / 1000
        val like = Like(currentTimeInSeconds)
        val accessToken = UserManager.getInstance(context).currentUser.accessToken
        return createBreakoutClient(accessToken)
                .likePosting(postingId.toInt(), like)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
    }

    fun unlikePosting(postingId: Integer): Observable<ResponseBody> {
        return createBreakoutClient(UserManager.getInstance(context).currentUser.accessToken)
                .unlikePosting(postingId.toInt())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
    }

    fun getPostings(offset: Int, limit: Int): Observable<List<RemotePosting>> {
        val accessToken = UserManager.getInstance(context).currentUser.accessToken
        val userId = UserManager.getInstance(context).currentUser.remoteId
        return createBreakoutClient(accessToken)
                .getAllPostings(offset, limit, userId.toInt())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
    }

    fun getPostingById(id: Int):Observable<RemotePosting>{
        val accessToken = UserManager.getInstance(context).currentUser.accessToken
        return createBreakoutClient(accessToken)
                .getPostingById(id)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
    }
}
