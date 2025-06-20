package com.example.pointgrapher.presentation

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.navigation3.runtime.entry
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.ui.NavDisplay
import com.example.pointgrapher.presentation.screen.main.ui.MainScreen
import com.example.pointgrapher.presentation.screen.result.ui.ResultScreen
import com.example.pointgrapher.presentation.theme.PointGrapherTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            PointGrapherTheme {
                val backStack = rememberNavBackStack(NavScreen.Main)
                NavDisplay(
                    backStack = backStack,
                    onBack = { backStack.removeAt(backStack.lastIndex) },
                    entryProvider = entryProvider {
                        entry<NavScreen.Main> {
                            MainScreen(
                                onOpenResultScreen = { batchId ->
                                    backStack.add(NavScreen.Result(batchId))
                                }
                            )
                        }
                        entry<NavScreen.Result> {
                            ResultScreen(
                                batchId = it.batchId,
                                onBack = { backStack.removeAt(backStack.lastIndex) }
                            )
                        }
                    }
                )
            }
        }
    }
}