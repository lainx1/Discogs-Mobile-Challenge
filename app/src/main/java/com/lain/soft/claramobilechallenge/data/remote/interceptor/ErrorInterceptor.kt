package com.lain.soft.claramobilechallenge.data.remote.interceptor

import com.lain.soft.claramobilechallenge.domain.model.NetworkErrorType
import com.lain.soft.claramobilechallenge.domain.model.NetworkException
import okhttp3.Interceptor
import okhttp3.Response
import java.io.IOException
import java.net.SocketTimeoutException
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ErrorInterceptor @Inject constructor() : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        
        val response = try {
            chain.proceed(request)
        } catch (e: Exception) {
            throw when (e) {
                is SocketTimeoutException -> NetworkException(NetworkErrorType.Timeout)
                is IOException -> NetworkException(NetworkErrorType.NoInternet)
                else -> NetworkException(NetworkErrorType.Unknown)
            }
        }

        if (!response.isSuccessful) {
            val networkErrorType = getNetworkErrorType(response.code)
            throw NetworkException(networkErrorType)
        }

        return response
    }

    private fun getNetworkErrorType(code: Int): NetworkErrorType {
        return when (code) {
            400 -> NetworkErrorType.BadRequest
            401 -> NetworkErrorType.Unauthorized
            403 -> NetworkErrorType.Forbidden
            404 -> NetworkErrorType.NotFound
            in 500..599 -> NetworkErrorType.ServerError
            else -> NetworkErrorType.Unknown
        }
    }
}
