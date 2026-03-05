package com.lain.soft.claramobilechallenge.ui.util

import androidx.annotation.StringRes
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource

sealed class UiText {
    class StringResource(
        @param:StringRes val id: Int,
        vararg val args: Any,
    ) : UiText()

    @Composable
    fun asString(): String =
        when (this) {
            is StringResource -> stringResource(id, *args)
        }
}
