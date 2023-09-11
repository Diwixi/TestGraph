package com.pg.testgraph.features.feature_welcome.presentation

import com.pg.testgraph.core.domain.Point
import com.pg.testgraph.core.presentation.IEffect
import com.pg.testgraph.core.presentation.IEvent
import com.pg.testgraph.core.presentation.IState

@Suppress("unused")
class WelcomeContract {
    sealed class State : IState {
        object Nothing : State()
        object Loading : State()
        data class Error(val errorModel: ErrorModel) : State()
    }

    sealed class Effect : IEffect {
        data class OpenGraphScreen(val points: List<Point>) : Effect()
    }

    sealed class Event : IEvent {
        data class OnFetchPoints(val count: Int) : Event()
        object RefreshNavigation : Event()
    }

    data class ErrorModel(val message: String)
}