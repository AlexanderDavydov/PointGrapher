package com.example.pointgrapher.presentation.result.composeui

import android.graphics.Bitmap

sealed class ResultCaptureChartEvent {
    data class Success(val bitmap: Bitmap) : ResultCaptureChartEvent()
    data class Error(val e: Exception) : ResultCaptureChartEvent()
}
