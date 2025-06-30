package com.example.pointgrapher.presentation.composeui.screen.main.viewdata

import androidx.compose.runtime.Immutable
import java.util.Date

@Immutable
data class BatchInfoViewData(
    val id: String,
    val numberOfPoints: Int,
    val time: Date
)