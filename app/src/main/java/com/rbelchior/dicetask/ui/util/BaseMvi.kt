package com.rbelchior.dicetask.ui.util

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

/**
 * A Reducer class that converts Events emitted by the Presenter into state objects read by the View.
 * Main functions:
 *  sendEvent(UiEvent) - Describes a change to the UI as directed by the presenter. UIEvent contains the delta required for the Reducer to emit the correct state.
 *  val state - A flowable of the current emitted state of the reducer.
 */
abstract class Reducer<S : UiState, E : UiEvent>(initialVal: S) {

    private val _state: MutableStateFlow<S> by lazy {
        MutableStateFlow(initialVal)
    }

    val state: StateFlow<S>
        get() = _state

    val currentValue: S
        get() = state.value

    fun sendEvent(event: E) {
        reduce(_state.value, event)
    }

    protected fun setState(newState: S) {
        _state.value = (newState)
    }

    abstract fun reduce(oldState: S, event: E)
}

interface UiState

interface UiEvent
