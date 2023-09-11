package com.pg.testgraph.features.feature_graph.presentation

import android.graphics.Typeface
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import co.yml.charts.axis.AxisData
import co.yml.charts.common.extensions.formatToSinglePrecision
import co.yml.charts.ui.linechart.LineChart
import co.yml.charts.ui.linechart.model.GridLines
import co.yml.charts.ui.linechart.model.IntersectionPoint
import co.yml.charts.ui.linechart.model.Line
import co.yml.charts.ui.linechart.model.LineChartData
import co.yml.charts.ui.linechart.model.LinePlotData
import co.yml.charts.ui.linechart.model.LineStyle
import co.yml.charts.ui.linechart.model.LineType
import co.yml.charts.ui.linechart.model.SelectionHighlightPoint
import co.yml.charts.ui.linechart.model.SelectionHighlightPopUp
import co.yml.charts.ui.linechart.model.ShadowUnderLine
import com.pg.testgraph.R
import com.pg.testgraph.core.domain.Point
import com.pg.testgraph.core.presentation.ui.navigation.Screen
import com.pg.testgraph.features.feature_graph.presentation.GraphContract.Effect
import com.pg.testgraph.features.feature_graph.presentation.GraphContract.Event
import com.pg.testgraph.features.feature_graph.presentation.GraphContract.State
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest

@Composable
fun GraphScreen(
    navController: NavController,
    stateFlow: StateFlow<State>,
    effectFlow: Flow<Effect>,
    setEvent: (Event) -> Unit,
) {
    val context = LocalContext.current
    val state by stateFlow.collectAsState()

    BackHandler {
        navController.navigate(Screen.MainScreen.route) {
            popUpTo(Screen.MainScreen.route) { inclusive = true }
            launchSingleTop = true
        }
    }

    SideEffect {
        setEvent(Event.OnViewReady)
    }

    LaunchedEffect(true) {
        effectFlow.collectLatest {
            handleEffect(navController = navController, effect = it)
        }
    }

    when (state) {
        is State.Loading -> LoadingContent()
        is State.Content -> {
            GraphScreenContent((state as State.Content).points) {
                setEvent(Event.OnSavePointsToFile(context))
            }
        }
    }
}

@Composable
fun LoadingContent() {
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier.fillMaxSize()
    ) {
        CircularProgressIndicator()
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GraphScreenContent(
    points: List<Point>,
    doOnSaveDataClick: () -> Unit,
) {
    var lineType by remember { mutableStateOf(LineTypeSetting.STRAIGHT) }
    var isMenuExpanded by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {},
                actions = {
                    IconButton(onClick = { isMenuExpanded = true }) {
                        Icon(imageVector = Icons.Default.MoreVert, contentDescription = null)
                    }

                    DropdownMenu(
                        expanded = isMenuExpanded,
                        onDismissRequest = { isMenuExpanded = false }
                    ) {
                        DropdownMenuItem(
                            onClick = {
                                lineType = LineTypeSetting.STRAIGHT
                                isMenuExpanded = false
                            },
                            text = {
                                Text(text = stringResource(R.string.straight_line))
                            }
                        )

                        DropdownMenuItem(
                            onClick = {
                                lineType = LineTypeSetting.SMOOTH
                                isMenuExpanded = false
                            },
                            text = {
                                Text(text = stringResource(R.string.smooth_line))
                            }
                        )

                        DropdownMenuItem(
                            onClick = {
                                doOnSaveDataClick()
                                isMenuExpanded = false
                            },
                            text = {
                                Text(text = stringResource(R.string.save_points_to_file))
                            }
                        )
                    }
                }
            )
        },
        content = { paddings ->
            Column(
                modifier = Modifier
                    .padding(paddings)
                    .fillMaxSize()
            ) {
                Spacer(modifier = Modifier.height(16.dp))
                PointsTable(points = points)
                Spacer(modifier = Modifier.height(16.dp))
                PointsLineChart(points = points, lineType = lineType.toLineType())
            }
        }
    )
}

enum class LineTypeSetting {
    STRAIGHT, SMOOTH;

    fun toLineType(): LineType = when (this) {
        STRAIGHT -> LineType.Straight()
        SMOOTH -> LineType.SmoothCurve()
    }
}

private fun handleEffect(navController: NavController, effect: Effect) {
    when (effect) {
        is Effect.SaveToFileSuccess -> {
            navController.navigate(Screen.DialogSaveFileSuccess.route)
        }

        is Effect.SaveToFileError -> {
            navController.navigate(Screen.DialogSaveFileError.route)
        }
    }
}

@Composable
fun PointsTable(points: List<Point>) {
    LazyColumn(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight(0.5f)
    ) {
        items(points) { point ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(text = stringResource(R.string.point_x, point.x))
                Text(text = stringResource(R.string.point_y, point.y))
            }
            Divider(color = Color.Gray)
        }
    }
}

@Composable
fun PointsLineChart(points: List<Point>, lineType: LineType = LineType.SmoothCurve()) {
    val steps = points.size
    val chartPoints = points.map { point ->
        co.yml.charts.common.model.Point(
            x = point.x.toFloat(),
            y = point.y.toFloat()
        )
    }

    val xAxisData = AxisData.Builder()
        .axisStepSize(30.dp)
        .backgroundColor(Color.White)
        .steps(steps)
        .typeFace(Typeface.DEFAULT_BOLD)
        .labelAndAxisLinePadding(20.dp)
        .build()

    val yAxisData = AxisData.Builder()
        .axisStepSize(30.dp)
        .backgroundColor(Color.White)
        .labelAndAxisLinePadding(8.dp)
        .steps(steps)
        .typeFace(Typeface.DEFAULT_BOLD)
        .labelData { i ->
            val yMin = chartPoints.minOf { it.y }
            val yMax = chartPoints.maxOf { it.y }
            val yScale = (yMax - yMin) / steps
            ((i * yScale) + yMin).formatToSinglePrecision()
        }
        .labelAndAxisLinePadding(20.dp)
        .build()

    val lineChartData = LineChartData(
        linePlotData = LinePlotData(
            lines = listOf(
                Line(
                    dataPoints = chartPoints,
                    LineStyle().copy(lineType = lineType),
                    IntersectionPoint(),
                    SelectionHighlightPoint(),
                    ShadowUnderLine(),
                    SelectionHighlightPopUp()
                )
            ),
        ),
        xAxisData = xAxisData,
        yAxisData = yAxisData,
        gridLines = GridLines(),
        backgroundColor = Color.White
    )

    LineChart(
        modifier = Modifier.fillMaxSize(),
        lineChartData = lineChartData
    )
}