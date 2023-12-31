package com.pg.testgraph.features.feature_welcome.presentation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.core.text.isDigitsOnly
import androidx.navigation.NavController
import com.pg.testgraph.R
import com.pg.testgraph.core.presentation.ui.navigation.Screen
import com.pg.testgraph.features.feature_welcome.presentation.WelcomeContract.Effect
import com.pg.testgraph.features.feature_welcome.presentation.WelcomeContract.ErrorModel
import com.pg.testgraph.features.feature_welcome.presentation.WelcomeContract.ErrorModel.SocketTimeout
import com.pg.testgraph.features.feature_welcome.presentation.WelcomeContract.ErrorModel.Unknown
import com.pg.testgraph.features.feature_welcome.presentation.WelcomeContract.Event
import com.pg.testgraph.features.feature_welcome.presentation.WelcomeContract.State
import com.pg.testgraph.features.feature_welcome.presentation.WelcomeContract.State.Error
import com.pg.testgraph.features.feature_welcome.presentation.WelcomeContract.State.Loading
import com.pg.testgraph.features.feature_welcome.presentation.WelcomeContract.State.Nothing
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest

@Composable
fun WelcomeScreen(
    navController: NavController,
    stateFlow: StateFlow<State>,
    effectFlow: Flow<Effect>,
    setEvent: (Event) -> Unit,
) {
    val state by stateFlow.collectAsState()

    LaunchedEffect(true) {
        effectFlow.collectLatest {
            handleEffect(effect = it, navController = navController)
        }
    }

    when (state) {
        is Loading -> LoadingContent()
        is Error -> ErrorContent(error = (state as Error).errorModel, doOnTryAgain = {
            setEvent(Event.RefreshNavigation)
        }, doOnGenerate = {
            setEvent(Event.OnFetchOwnPoints(it))
        })

        is Nothing -> {
            WelcomeScreenContent { count ->
                setEvent(Event.OnFetchPoints(count))
            }
        }
    }
}

@Composable
fun ErrorContent(error: ErrorModel, doOnTryAgain: () -> Unit, doOnGenerate: (Int) -> Unit) {
    when (error) {
        is Unknown -> UnknownErrorScreen(doOnTryAgain)

        is SocketTimeout -> {
            SocketTimeoutErrorScreen(error.count, doOnTryAgain, doOnGenerate)
        }
    }
}

@Composable
fun SocketTimeoutErrorScreen(count: Int, doOnTryAgain: () -> Unit, doOnGenerate: (Int) -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp), contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.wrapContentSize()
        ) {
            Text(text = stringResource(R.string.server_not_responding))
            Spacer(modifier = Modifier.height(16.dp))
            Button(onClick = doOnTryAgain, content = {
                Text(text = stringResource(R.string.try_again))
            })
            Spacer(modifier = Modifier.height(16.dp))
            Button(onClick = { doOnGenerate(count) }, content = {
                Text(text = stringResource(R.string.generate_points))
            })
        }
    }
}

@Composable
fun UnknownErrorScreen(doAgainClick: () -> Unit) {
    Box(
        modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.wrapContentSize()
        ) {
            Text(text = stringResource(R.string.something_went_wrong))
            Spacer(modifier = Modifier.height(16.dp))
            Button(onClick = doAgainClick, content = {
                Text(text = stringResource(R.string.try_again))
            })
        }
    }
}

@Composable
fun LoadingContent() {
    Box(
        contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()
    ) {
        CircularProgressIndicator()
    }
}

fun String.isValidNegativeNumber(): Boolean {
    return this == "-" || this.all { char -> char.isDigit() } || (this.startsWith("-") && this.drop(
        1
    ).all { char -> char.isDigit() })
}

fun String.isValidPositiveNumber(): Boolean {
    return this.all { char -> char.isDigit() } && this.toInt() <= 1000 && this.toInt() > 0
}

@Composable
fun WelcomeScreenContent(doOnRunButtonClick: (Int) -> Unit) {

    var pointCount by remember { mutableStateOf("") }
    val isError by remember {
        derivedStateOf {
            pointCount.isNotEmpty() && (!pointCount.isValidNegativeNumber() || !pointCount.isValidPositiveNumber())
        }
    }
    val isActiveButton by remember {
        derivedStateOf {
            pointCount.isNotEmpty() && pointCount.isDigitsOnly() && pointCount.toInt() <= 1000 && pointCount.toInt() > 0
        }
    }
    val labelText by remember {
        derivedStateOf {
            when {
                pointCount.isEmpty() -> {
                    R.string.hint_points
                }

                pointCount.length == 1 && pointCount.first() == '-' -> {
                    R.string.hint_points
                }

                pointCount.toInt() >= 1001 -> {
                    R.string.hint_popints_error
                }

                pointCount.toInt() == 0 -> {
                    R.string.number_points_cannot_be_zero
                }

                pointCount.toInt() < 0 -> {
                    R.string.number_points_cannot_be_negative
                }

                else -> {
                    R.string.hint_points
                }
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = stringResource(R.string.welcome))

        OutlinedTextField(value = pointCount,
            isError = isError,
            onValueChange = {
                if (it.isDigit() && it.length <= 5) {
                    pointCount = it
                }
            },
            label = { Text(stringResource(labelText)) },
            keyboardOptions = KeyboardOptions.Default.copy(
                keyboardType = KeyboardType.Number, imeAction = ImeAction.Done
            ),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(onClick = { doOnRunButtonClick(pointCount.toInt()) },
            enabled = isActiveButton,
            content = {
                Text(text = stringResource(R.string.start))
            })
    }
}

private fun handleEffect(effect: Effect, navController: NavController) {
    when (effect) {
        is Effect.OpenGraphScreen -> navController.navigate(Screen.GraphScreen.route)
    }
}

private fun String.isDigit(): Boolean {
    return this == "-" || this.all { char -> char.isDigit() } || (this.startsWith("-") && this.drop(
        1
    ).all { char -> char.isDigit() })
}
