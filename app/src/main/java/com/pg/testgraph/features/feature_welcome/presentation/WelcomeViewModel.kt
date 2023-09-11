package com.pg.testgraph.features.feature_welcome.presentation

import androidx.lifecycle.viewModelScope
import com.pg.testgraph.core.data.network.PointsRepository
import com.pg.testgraph.core.presentation.MviViewModel
import com.pg.testgraph.features.feature_welcome.presentation.WelcomeContract.Effect
import com.pg.testgraph.features.feature_welcome.presentation.WelcomeContract.ErrorModel
import com.pg.testgraph.features.feature_welcome.presentation.WelcomeContract.Event
import com.pg.testgraph.features.feature_welcome.presentation.WelcomeContract.State
import kotlinx.coroutines.launch

class WelcomeViewModel(private val repository: PointsRepository) :
    MviViewModel<Event, State, Effect>(State.Nothing) {

    override fun handleEvent(event: Event) {
        when (event) {
            is Event.OnFetchPoints -> fetchPoints(event.count)
            is Event.RefreshNavigation -> sendState(State.Nothing)
        }
    }

    private fun fetchPoints(count: Int) {
        viewModelScope.launch {
            sendState(State.Loading)
            repository.fetchPoints(count)
                .onSuccess { points ->
                    sendEffect(Effect.OpenGraphScreen(points))
                }
                .onFailure { error ->
                    when (error) {
                        else -> {
                            sendState(State.Error(ErrorModel(message = error.message ?: "Неизвестная ошибка")))
                        }
                    }
                }
        }
    }

}