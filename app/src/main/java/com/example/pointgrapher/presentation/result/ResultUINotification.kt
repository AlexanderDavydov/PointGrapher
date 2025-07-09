package com.example.pointgrapher.presentation.result

sealed interface ResultUINotification {
    data class ChartSaved(val path: String) : ResultUINotification
    data class Error(val message: String) : ResultUINotification
}