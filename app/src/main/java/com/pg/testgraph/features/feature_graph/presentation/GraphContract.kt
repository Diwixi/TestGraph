package com.pg.testgraph.features.feature_graph.presentation

import android.content.Context
import com.pg.testgraph.core.domain.Point
import com.pg.testgraph.core.presentation.IEffect
import com.pg.testgraph.core.presentation.IEvent
import com.pg.testgraph.core.presentation.IState

@Suppress("unused")
class GraphContract {
    sealed class State : IState {
        object Loading : State()
        data class Content(val points: List<Point>) : State()
    }

    sealed class Effect : IEffect {
        object SaveToFileSuccess : Effect()
        object SaveToFileError : Effect()
    }

    sealed class Event : IEvent {
        object OnViewReady : Event()
        data class OnSavePointsToFile(val context: Context) : Event()
    }

    data class ErrorModel(val message: String)
}