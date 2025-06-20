package com.example.pointgrapher.presentation.screen.main

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pointgrapher.domain.exeption.NerworkError
import com.example.pointgrapher.domain.usecase.GetPointsUseCase
import com.example.pointgrapher.presentation.screen.main.viewdata.MainScreenErrorTypeViewData
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    // TODO use another use case
    private val getPointsUseCase: GetPointsUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(MainScreenState())
    val state = _state.asStateFlow()

    fun onPointNumberChanged(newValue: String) {
        val stateValue = when {
            newValue.isBlank() -> newValue
            newValue.all { char -> char.isDigit() } -> newValue
            else -> return
        }
        _state.update { it.copy(requiredPointNumber = stateValue) }
    }

    fun request() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            try {
                val pointNumber = doPreRequestChecks(state.value.requiredPointNumber)
                val points = getPointsUseCase(pointNumber)
                _state.update {
                    it.copy(
                        isLoading = false,
                        errorTypeViewData = MainScreenErrorTypeViewData.None
                    )
                }
            } catch (e: Exception) {
                val errorTypeViewData = extractErrorTypeViewdata(e)
                _state.update { it.copy(errorTypeViewData = errorTypeViewData, isLoading = false) }

                val logMessage = "Error while requesting ${state.value.requiredPointNumber} points"
                Log.e(MainViewModel::class.java.name, logMessage, e)
            }
        }
    }

    private fun doPreRequestChecks(number: String): Int {
        val pointNumber =
            number.toIntOrNull() ?: throw NumberFormatException("Point number must be a number")
        require(pointNumber > 0) { "Point number must be greater than 0" }
        return pointNumber
    }

    private fun extractErrorTypeViewdata(e: Exception): MainScreenErrorTypeViewData {
        return when (e) {
            is NerworkError -> extractNetworkError(e)
            is NumberFormatException -> MainScreenErrorTypeViewData.EmptyNumber
            is IllegalArgumentException -> MainScreenErrorTypeViewData.NegativeNumber
            else -> MainScreenErrorTypeViewData.Unspecified
        }
    }

    private fun extractNetworkError(e: Exception): MainScreenErrorTypeViewData {
        return if (e.cause is IllegalArgumentException) {
            MainScreenErrorTypeViewData.RequestedIncorrectnessError
        } else {
            MainScreenErrorTypeViewData.RequestError
        }
    }
}
