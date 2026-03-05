package com.lain.soft.claramobilechallenge.data.remote.interceptor

import okhttp3.Request
import org.junit.Assert.assertEquals
import org.junit.Test

class AuthInterceptorTest {

    @Test
    fun intercept_addsAuthorizationHeader() {
        val interceptor = AuthInterceptor()
        val initialRequest = Request.Builder()
            .url("https://api.discogs.com/database/search")
            .build()
        val chain = TestInterceptorChain(initialRequest = initialRequest)

        interceptor.intercept(chain)

        val header = chain.proceededRequest?.header("Authorization").orEmpty()
        assertEquals(true, header.startsWith("Discogs token="))
    }
}
