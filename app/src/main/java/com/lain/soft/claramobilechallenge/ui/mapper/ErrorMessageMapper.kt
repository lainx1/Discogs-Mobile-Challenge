package com.lain.soft.claramobilechallenge.ui.mapper

import com.lain.soft.claramobilechallenge.R
import com.lain.soft.claramobilechallenge.domain.model.AppException
import com.lain.soft.claramobilechallenge.domain.model.NetworkErrorType
import com.lain.soft.claramobilechallenge.domain.model.NetworkException
import com.lain.soft.claramobilechallenge.ui.util.UiText
import javax.inject.Inject

class ErrorMessageMapper @Inject constructor() {
    fun map(throwable: Throwable): UiText {
        return when (throwable) {
            is NetworkException -> mapNetworkError(throwable.networkErrorType)
            is AppException -> mapAppErrors(throwable)
            else -> UiText.StringResource(R.string.app_generic_unknown_error_text)
        }
    }

    private fun mapNetworkError(errorType: NetworkErrorType): UiText {
        return when (errorType) {
            NetworkErrorType.NoInternet -> UiText.StringResource(R.string.error_no_internet)
            NetworkErrorType.Timeout -> UiText.StringResource(R.string.error_timeout)
            NetworkErrorType.BadRequest -> UiText.StringResource(R.string.error_bad_request)
            NetworkErrorType.Unauthorized -> UiText.StringResource(R.string.error_unauthorized)
            NetworkErrorType.Forbidden -> UiText.StringResource(R.string.error_forbidden)
            NetworkErrorType.NotFound -> UiText.StringResource(R.string.error_not_found)
            NetworkErrorType.ServerError -> UiText.StringResource(R.string.error_server)
            NetworkErrorType.Unknown -> UiText.StringResource(R.string.app_generic_unknown_error_text)
        }
    }

    private fun mapAppErrors(appException: AppException): UiText {
        return when (appException) {
            else -> UiText.StringResource(R.string.app_generic_unknown_error_text)
        }
    }
}
