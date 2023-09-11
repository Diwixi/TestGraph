package com.pg.testgraph.core.presentation.ui.navigation

sealed class Screen(val route: String) {
    object MainScreen : Screen("main_screen")
    object GraphScreen : Screen("graph_screen")
    object DialogSaveFileSuccess : Screen("dialog_save_file_success")
    object DialogSaveFileError : Screen("dialog_save_file_error")
}