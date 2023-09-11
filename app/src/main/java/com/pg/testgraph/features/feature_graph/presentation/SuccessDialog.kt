package com.pg.testgraph.features.feature_graph.presentation

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavController
import com.pg.testgraph.R

@Composable
fun SuccessDialog(navController: NavController) {
    AlertDialog(
        onDismissRequest = { navController.popBackStack() },
        title = { Text(text = stringResource(R.string.success_dialog_title)) },
        text = { Text(text = stringResource(R.string.success_dialog_msg)) },
        confirmButton = {
            Button(onClick = { navController.popBackStack() }) {
                Text(stringResource(R.string.ok))
            }
        }
    )
}