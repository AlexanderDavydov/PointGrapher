package com.example.pointgrapher.presentation

import com.example.pointgrapher.domain.model.Point

data class PointScreenState(
    val points: List<Point> = emptyList(),
    val requeredPointNumber: String = "10",
    val isLoading: Boolean = false,
    val errorTypeViewData: ErrorTypeViewData = ErrorTypeViewData.None
) {
    val isError: Boolean = errorTypeViewData != ErrorTypeViewData.None
    val isInputError: Boolean =
        errorTypeViewData == ErrorTypeViewData.EmptyNumber
                || errorTypeViewData == ErrorTypeViewData.NegativeNumber
                || errorTypeViewData == ErrorTypeViewData.RequestedIncorrectnessError

    enum class ErrorTypeViewData {
        None,
        EmptyNumber,
        NegativeNumber,
        RequestedIncorrectnessError,
        RequstError,
        Uncpecified
    }
}
