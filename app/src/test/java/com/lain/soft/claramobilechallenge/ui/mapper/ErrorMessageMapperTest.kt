package com.lain.soft.claramobilechallenge.ui.mapper

import com.lain.soft.claramobilechallenge.R
import com.lain.soft.claramobilechallenge.domain.model.NetworkErrorType
import com.lain.soft.claramobilechallenge.domain.model.NetworkException
import com.lain.soft.claramobilechallenge.ui.util.UiText
import org.junit.Assert.assertEquals
import org.junit.Test

class ErrorMessageMapperTest {

    private val mapper = ErrorMessageMapper()

    @Test
    fun map_noInternet_returnsNoInternetMessage() {
        val result = mapper.map(NetworkException(NetworkErrorType.NoInternet))
        assertEquals(R.string.error_no_internet, (result as UiText.StringResource).id)
    }

    @Test
    fun map_timeout_returnsTimeoutMessage() {
        val result = mapper.map(NetworkException(NetworkErrorType.Timeout))
        assertEquals(R.string.error_timeout, (result as UiText.StringResource).id)
    }

    @Test
    fun map_serverError_returnsServerMessage() {
        val result = mapper.map(NetworkException(NetworkErrorType.ServerError))
        assertEquals(R.string.error_server, (result as UiText.StringResource).id)
    }

    @Test
    fun map_unknownThrowable_returnsGenericUnknownMessage() {
        val result = mapper.map(IllegalArgumentException("unexpected"))
        assertEquals(R.string.app_generic_unknown_error, (result as UiText.StringResource).id)
    }
}
