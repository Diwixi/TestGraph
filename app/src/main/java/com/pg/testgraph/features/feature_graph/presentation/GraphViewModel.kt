package com.pg.testgraph.features.feature_graph.presentation

import android.content.Context
import androidx.lifecycle.viewModelScope
import com.pg.testgraph.core.data.network.PointsRepository
import com.pg.testgraph.core.data.network.SaveFileResult.Failure
import com.pg.testgraph.core.data.network.SaveFileResult.Success
import com.pg.testgraph.core.presentation.MviViewModel
import com.pg.testgraph.features.feature_graph.presentation.GraphContract.Effect
import com.pg.testgraph.features.feature_graph.presentation.GraphContract.Event
import com.pg.testgraph.features.feature_graph.presentation.GraphContract.State
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.launch

class GraphViewModel(
    private val repository: PointsRepository,
    private val dispatcher: CoroutineDispatcher
) : MviViewModel<Event, State, Effect>(State.Loading) {

    override fun handleEvent(event: Event) {
        when (event) {
            is Event.OnViewReady -> loadPoints()
            is Event.OnSavePointsToFile -> savePointsToFile(event.context)
        }
    }

    private fun loadPoints() {
        sendState(State.Content(repository.points))
    }

    private fun savePointsToFile(context: Context) {
        viewModelScope.launch(dispatcher) {
            repository.savePointsToFile(context) { result ->
                when (result) {
                    is Success -> {
                        sendEffect(Effect.SaveToFileSuccess)
                    }

                    is Failure -> {
                        sendEffect(Effect.SaveToFileError)
                    }
                }
            }
        }
    }
}