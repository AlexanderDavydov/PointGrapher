package com.example.pointgrapher.presentation.points.model

data class PointScreenState(
    val points: PointViewData = PointViewData.Empty,
    val requiredPointNumber: String = "10",
    val isLoading: Boolean = false,
    val errorTypeViewData: ErrorTypeViewData = ErrorTypeViewData.None
) {
    val isError: Boolean = errorTypeViewData != ErrorTypeViewData.None
    val isInputError: Boolean =
        errorTypeViewData == ErrorTypeViewData.EmptyNumber
                || errorTypeViewData == ErrorTypeViewData.NegativeNumber
                || errorTypeViewData == ErrorTypeViewData.RequestedIncorrectnessError

    data class PointViewData(
        val x: Collection<Number>,
        val y: Collection<Number>
    ) {
        companion object {
            val Empty = PointViewData(emptyList(), emptyList())
        }
    }


    enum class ErrorTypeViewData {
        None,
        EmptyNumber,
        NegativeNumber,
        RequestedIncorrectnessError,
        RequestError,
        Unspecified
    }
}
