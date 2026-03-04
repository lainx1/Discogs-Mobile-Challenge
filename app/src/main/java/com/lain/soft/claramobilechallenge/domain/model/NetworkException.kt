package com.lain.soft.claramobilechallenge.domain.model

import java.io.IOException

open class AppException : Exception()
class NetworkException(val networkErrorType: NetworkErrorType) : IOException()

sealed interface NetworkErrorType {
    object NoInternet : NetworkErrorType
    object Timeout : NetworkErrorType
    object BadRequest : NetworkErrorType
    object Unauthorized : NetworkErrorType
    object Forbidden : NetworkErrorType
    object NotFound : NetworkErrorType
    object ServerError : NetworkErrorType
    object Unknown : NetworkErrorType
}
