package com.example.pointgrapher.presentation

import com.example.pointgrapher.domain.model.Point

data class PointScreenState(
    val points: List<Point> = emptyList(),
    val requeredPointNumber: String = "10",
    val isLoading: Boolean = false,
    val errorTypeViewData: ErrorTypeViewData = ErrorTypeViewData.None
) {
    val isError: Boolean = errorTypeViewData != ErrorTypeViewData.None

    enum class ErrorTypeViewData {
        None,
        EmptyNumber,
        NegativeNumber,
        RequstError,
        Uncpecified
    }
}
