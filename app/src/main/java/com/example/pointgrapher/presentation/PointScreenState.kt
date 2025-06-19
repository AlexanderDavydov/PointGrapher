package com.example.pointgrapher.presentation

import com.example.pointgrapher.domain.model.Point

sealed interface PointScreenState {
    data class Content(
        val points: List<Point>
    ) : PointScreenState
    object Loading : PointScreenState
    object Error : PointScreenState
}
