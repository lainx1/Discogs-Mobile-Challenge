package com.lain.soft.claramobilechallenge.data.remote.interceptor

import com.lain.soft.claramobilechallenge.domain.model.NetworkErrorType
import com.lain.soft.claramobilechallenge.domain.model.NetworkException
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import java.io.IOException
import java.net.SocketTimeoutException

class ErrorInterceptorTest {

    private val interceptor = ErrorInterceptor()

    @Test
    fun intercept_timeout_mapsToTimeoutError() {
        val chain = TestInterceptorChain(thrownException = SocketTimeoutException())

        val thrown = runCatching { interceptor.intercept(chain) }.exceptionOrNull()

        assertTrue(thrown is NetworkException)
        assertEquals(NetworkErrorType.Timeout, (thrown as NetworkException).networkErrorType)
    }

    @Test
    fun intercept_ioException_mapsToNoInternetError() {
        val chain = TestInterceptorChain(thrownException = IOException())

        val thrown = runCatching { interceptor.intercept(chain) }.exceptionOrNull()

        assertTrue(thrown is NetworkException)
        assertEquals(NetworkErrorType.NoInternet, (thrown as NetworkException).networkErrorType)
    }

    @Test
    fun intercept_http401_mapsToUnauthorizedError() {
        val chain = TestInterceptorChain(responseCode = 401)

        val thrown = runCatching { interceptor.intercept(chain) }.exceptionOrNull()

        assertTrue(thrown is NetworkException)
        assertEquals(NetworkErrorType.Unauthorized, (thrown as NetworkException).networkErrorType)
    }

    @Test
    fun intercept_successfulResponse_returnsResponse() {
        val chain = TestInterceptorChain(responseCode = 200)

        val response = interceptor.intercept(chain)

        assertEquals(200, response.code)
    }
}
