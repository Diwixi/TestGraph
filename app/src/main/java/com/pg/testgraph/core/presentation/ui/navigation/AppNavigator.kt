package com.pg.testgraph.core.presentation.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.dialog
import androidx.navigation.compose.rememberNavController
import com.pg.testgraph.features.feature_graph.presentation.ErrorDialog
import com.pg.testgraph.features.feature_graph.presentation.GraphScreen
import com.pg.testgraph.features.feature_graph.presentation.GraphViewModel
import com.pg.testgraph.features.feature_graph.presentation.SuccessDialog
import com.pg.testgraph.features.feature_welcome.presentation.WelcomeScreen
import com.pg.testgraph.features.feature_welcome.presentation.WelcomeViewModel
import org.koin.androidx.compose.koinViewModel

@Composable
fun AppNavigator() {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = Screen.MainScreen.route) {
        composable(route = Screen.MainScreen.route) {
            val viewModel: WelcomeViewModel = koinViewModel()
            WelcomeScreen(
                navController = navController,
                stateFlow = viewModel.state,
                effectFlow = viewModel.effect,
                setEvent = viewModel::setEvent,
            )
        }
        composable(route = Screen.GraphScreen.route) {
            val viewModel: GraphViewModel = koinViewModel()
            GraphScreen(
                navController = navController,
                stateFlow = viewModel.state,
                effectFlow = viewModel.effect,
                setEvent = viewModel::setEvent,
            )
        }
        dialog(route = Screen.DialogSaveFileSuccess.route) {
            SuccessDialog(navController = navController)
        }
        dialog(route = Screen.DialogSaveFileError.route) {
            ErrorDialog(navController = navController)
        }
    }
}