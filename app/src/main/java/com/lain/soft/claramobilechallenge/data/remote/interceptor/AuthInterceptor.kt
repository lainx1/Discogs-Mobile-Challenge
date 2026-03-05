package com.lain.soft.claramobilechallenge.data.remote.interceptor

import com.lain.soft.claramobilechallenge.util.Constants
import okhttp3.Interceptor
import okhttp3.Response
import javax.inject.Inject

class AuthInterceptor @Inject constructor() : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val req = chain
            .request()
            .newBuilder()
            .addHeader("Authorization", "Discogs token=${Constants.DISCOGS_API_KEY}")
            .build()
        return chain.proceed(req)
    }
}
