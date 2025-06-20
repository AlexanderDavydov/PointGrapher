package com.example.pointgrapher.presentation

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable

sealed interface NavScreen : NavKey {
    @Serializable
    data object Main : NavScreen
    @Serializable
    data class Result(val batchId: String) : NavScreen
}