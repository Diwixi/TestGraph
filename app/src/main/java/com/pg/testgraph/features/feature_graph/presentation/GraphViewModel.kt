package com.pg.testgraph.features.feature_graph.presentation

import android.content.Context
import androidx.lifecycle.viewModelScope
import com.pg.testgraph.core.data.network.PointsRepository
import com.pg.testgraph.core.domain.Point
import com.pg.testgraph.core.presentation.MviViewModel
import com.pg.testgraph.features.feature_graph.presentation.GraphContract.Effect
import com.pg.testgraph.features.feature_graph.presentation.GraphContract.Event
import com.pg.testgraph.features.feature_graph.presentation.GraphContract.State
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileWriter

class GraphViewModel(
    private val repository: PointsRepository
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
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val file = File(context.filesDir, "points.txt")
                file.printWriter().use { out ->
                    repository.points.forEach {
                        out.println("${it.x}, ${it.y}")
                    }
                }
                sendEffect(Effect.SaveToFileSuccess)
            } catch (e: Exception) {
                sendEffect(Effect.SaveToFileError)
            }
        }
    }
}