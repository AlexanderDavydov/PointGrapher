package com.example.pointgrapher.presentation.main


sealed class MainScreenNavigation {
    data class GoToResult(val batchId: String) : MainScreenNavigation()
    data object GoToOnboarding : MainScreenNavigation()
}