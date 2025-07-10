package com.example.pointgrapher.presentation.composeui

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.navigation3.runtime.entry
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.ui.NavDisplay
import com.example.pointgrapher.presentation.composeui.theme.PointGrapherTheme
import com.example.pointgrapher.presentation.main.composebased.MainScreen
import com.example.pointgrapher.presentation.onboarding.OnboardingActivity
import com.example.pointgrapher.presentation.result.composeui.ResultScreen
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainComposeActivity : ComponentActivity() {
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
                                },
                                onGoToOnboarding = {
                                    navigateToOnboarding()
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

    private fun navigateToOnboarding() {
        val onboardingIntent =
            Intent(
                this@MainComposeActivity,
                OnboardingActivity::class.java
            ).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            }
        startActivity(onboardingIntent)
        finish()
    }
}