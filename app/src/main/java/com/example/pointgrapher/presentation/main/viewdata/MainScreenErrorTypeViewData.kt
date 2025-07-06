package com.example.pointgrapher.presentation.main.viewdata

sealed class MainScreenErrorTypeViewData {
    data object None : MainScreenErrorTypeViewData()
    data class ValidationError(val type: ValidationErrorTypeViewData) : MainScreenErrorTypeViewData()
    data object RequestedIncorrectnessError : MainScreenErrorTypeViewData()
    data object RequestError : MainScreenErrorTypeViewData()
    data object Unspecified : MainScreenErrorTypeViewData()
}