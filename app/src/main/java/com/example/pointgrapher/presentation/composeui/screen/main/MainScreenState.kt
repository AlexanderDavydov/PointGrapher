package com.example.pointgrapher.presentation.composeui.screen.main

import com.example.pointgrapher.presentation.composeui.screen.main.viewdata.BatchInfoViewData
import com.example.pointgrapher.presentation.composeui.screen.main.viewdata.MainScreenErrorTypeViewData
import com.example.pointgrapher.presentation.composeui.screen.main.viewdata.MainScreenErrorTypeViewData.EmptyNumber
import com.example.pointgrapher.presentation.composeui.screen.main.viewdata.MainScreenErrorTypeViewData.NegativeNumber
import com.example.pointgrapher.presentation.composeui.screen.main.viewdata.MainScreenErrorTypeViewData.None
import com.example.pointgrapher.presentation.composeui.screen.main.viewdata.MainScreenErrorTypeViewData.RequestedIncorrectnessError

data class MainScreenState(
    val requiredPointNumber: String = "10",
    val isLoading: Boolean = false,
    val errorTypeViewData: MainScreenErrorTypeViewData = None,
    val batches: List<BatchInfoViewData> = emptyList()
) {
    val isError: Boolean = errorTypeViewData != None
    val isInputError: Boolean =
        errorTypeViewData == EmptyNumber
                || errorTypeViewData == NegativeNumber
                || errorTypeViewData == RequestedIncorrectnessError
}