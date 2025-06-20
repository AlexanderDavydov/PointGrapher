package com.example.pointgrapher.presentation.screen.main

import com.example.pointgrapher.presentation.screen.main.viewdata.MainScreenErrorTypeViewData
import com.example.pointgrapher.presentation.screen.main.viewdata.MainScreenErrorTypeViewData.EmptyNumber
import com.example.pointgrapher.presentation.screen.main.viewdata.MainScreenErrorTypeViewData.NegativeNumber
import com.example.pointgrapher.presentation.screen.main.viewdata.MainScreenErrorTypeViewData.None
import com.example.pointgrapher.presentation.screen.main.viewdata.MainScreenErrorTypeViewData.RequestedIncorrectnessError

data class MainScreenState(
    val requiredPointNumber: String = "10",
    val isLoading: Boolean = false,
    val errorTypeViewData: MainScreenErrorTypeViewData = None,
) {
    val isError: Boolean = errorTypeViewData != None
    val isInputError: Boolean =
        errorTypeViewData == EmptyNumber
                || errorTypeViewData == NegativeNumber
                || errorTypeViewData == RequestedIncorrectnessError
}