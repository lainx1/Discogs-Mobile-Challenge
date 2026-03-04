package com.lain.soft.claramobilechallenge.ui.viewmodel

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

abstract class StateMachineViewModel<T>(
    initialState: T,
) : ViewModel() {
    private val _state = MutableStateFlow(initialState)
    val state: StateFlow<T> = _state.asStateFlow()

    protected fun setState(reducer: (T) -> T) {
        _state.update(reducer)
    }
}
