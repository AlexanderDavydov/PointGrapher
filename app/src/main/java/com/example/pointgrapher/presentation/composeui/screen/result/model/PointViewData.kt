package com.example.pointgrapher.presentation.composeui.screen.result.model

data class PointViewData(
    val x: List<Number>,
    val y: List<Number>
) {
    companion object {
        val Empty = PointViewData(emptyList(), emptyList())
    }
}