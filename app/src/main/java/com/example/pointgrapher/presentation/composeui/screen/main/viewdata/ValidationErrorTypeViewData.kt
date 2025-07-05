package com.example.pointgrapher.presentation.composeui.screen.main.viewdata

sealed class ValidationErrorTypeViewData {
    data object Empty : ValidationErrorTypeViewData()
    data object MustBeNumber : ValidationErrorTypeViewData()
    data object InvalidFormat : ValidationErrorTypeViewData()
    data object TooLowValue : ValidationErrorTypeViewData()
    data object TooHighValue : ValidationErrorTypeViewData()
}