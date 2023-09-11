package com.pg.testgraph

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.pg.testgraph.ui.navigation.AppNavigator
import com.pg.testgraph.ui.theme.TestGraphsTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            TestGraphsTheme {
                AppNavigator()
            }
        }
    }
}