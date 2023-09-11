package com.pg.testgraph.core.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

abstract class MviViewModel<E : IEvent, S : IState, F : IEffect>(initialState: S) : ViewModel() {
    private val _state: MutableStateFlow<S> = MutableStateFlow(initialState)
    val state = _state.asStateFlow()

    private val _effect: Channel<F> = Channel()
    val effect = _effect.receiveAsFlow()

    private val _event: MutableSharedFlow<E> = MutableSharedFlow()
    val event = _event.asSharedFlow()

    init {
        subscribeEvents()
    }

    private fun subscribeEvents() {
        viewModelScope.launch {
            event.collect { handleEvent(it) }
        }
    }

    abstract fun handleEvent(event: E)

    protected fun sendEffect(effect: F) {
        viewModelScope.launch { _effect.send(effect) }
    }

    protected fun sendState(state: S) {
        viewModelScope.launch { _state.emit(state) }
    }

    fun setEvent(event: E) {
        viewModelScope.launch { _event.emit(event) }
    }
}