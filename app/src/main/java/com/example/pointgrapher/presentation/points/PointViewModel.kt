package com.example.pointgrapher.presentation.points

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pointgrapher.domain.exeption.NerworkError
import com.example.pointgrapher.domain.usecase.GetPointsUsecase
import com.example.pointgrapher.presentation.points.model.PointScreenState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PointViewModel @Inject constructor(
    private val getPointsUsecase: GetPointsUsecase
) : ViewModel() {

    private val _state = MutableStateFlow(PointScreenState())
    val state = _state.asStateFlow()

    fun request() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            try {
                val pointNumber = doPreRequestChecks(state.value.requiredPointNumber)
                val points = getPointsUsecase(pointNumber)
                _state.update {
                    it.copy(
                        points = points,
                        isLoading = false,
                        errorTypeViewData = PointScreenState.ErrorTypeViewData.None
                    )
                }
            } catch (e: Exception) {
                val errorTypeViewData = extractErrorTypeViewdata(e)
                _state.update { it.copy(errorTypeViewData = errorTypeViewData, isLoading = false) }

                val logMessage = "Error while requesting ${state.value.requiredPointNumber} points"
                Log.e(PointViewModel::class.java.name, logMessage, e)
            }
        }
    }

    private fun doPreRequestChecks(number: String): Int {
        val pointNumber =
            number.toIntOrNull() ?: throw NumberFormatException("Point number must be a number")
        require(pointNumber > 0) { "Point number must be greater than 0" }
        return pointNumber
    }

    private fun extractErrorTypeViewdata(e: Exception): PointScreenState.ErrorTypeViewData {
        return when (e) {
            is NerworkError -> extractNetworkError(e)
            is NumberFormatException -> PointScreenState.ErrorTypeViewData.EmptyNumber
            is IllegalArgumentException -> PointScreenState.ErrorTypeViewData.NegativeNumber
            else -> PointScreenState.ErrorTypeViewData.Unspecified
        }
    }

    private fun extractNetworkError(e: Exception): PointScreenState.ErrorTypeViewData {
        return if (e.cause is IllegalArgumentException) {
            PointScreenState.ErrorTypeViewData.RequestedIncorrectnessError
        } else {
            PointScreenState.ErrorTypeViewData.RequestError
        }
    }

    fun onPointNumberChanged(newValue: String) {
        val stateValue = when {
            newValue.isBlank() -> newValue
            newValue.all { char -> char.isDigit() } -> newValue
            else -> return
        }
        _state.update { it.copy(requiredPointNumber = stateValue) }
    }
}
