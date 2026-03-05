package com.lain.soft.claramobilechallenge.data.remote.interceptor

import okhttp3.Call
import okhttp3.Connection
import okhttp3.Interceptor
import okhttp3.Protocol
import okhttp3.Request
import okhttp3.Response
import okhttp3.ResponseBody.Companion.toResponseBody
import java.io.IOException
import java.util.concurrent.TimeUnit

class TestInterceptorChain(
    private val initialRequest: Request = Request.Builder()
        .url("https://api.discogs.com")
        .build(),
    private val responseCode: Int = 200,
    private val thrownException: Exception? = null
) : Interceptor.Chain {

    var proceededRequest: Request? = null

    override fun request(): Request = initialRequest

    override fun proceed(request: Request): Response {
        proceededRequest = request
        thrownException?.let { throw it }

        return Response.Builder()
            .request(request)
            .protocol(Protocol.HTTP_1_1)
            .code(responseCode)
            .message(if (responseCode in 200..299) "OK" else "ERR")
            .body("{}".toResponseBody())
            .build()
    }

    override fun call(): Call {
        throw IOException("Not required for this test")
    }

    override fun connection(): Connection? = null

    override fun connectTimeoutMillis(): Int = 0

    override fun withConnectTimeout(timeout: Int, unit: TimeUnit): Interceptor.Chain = this

    override fun readTimeoutMillis(): Int = 0

    override fun withReadTimeout(timeout: Int, unit: TimeUnit): Interceptor.Chain = this

    override fun writeTimeoutMillis(): Int = 0

    override fun withWriteTimeout(timeout: Int, unit: TimeUnit): Interceptor.Chain = this
}
