package com.example.pointgrapher.presentation.screen.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pointgrapher.domain.exception.BatchNotFoundException
import com.example.pointgrapher.domain.exception.NetworkError
import com.example.pointgrapher.domain.model.BatchInfo
import com.example.pointgrapher.domain.usecase.DeletePointBatchUseCase
import com.example.pointgrapher.domain.usecase.ObserveBatchesUseCase
import com.example.pointgrapher.domain.usecase.RequestPointsBatchUseCase
import com.example.pointgrapher.presentation.screen.main.viewdata.BatchInfoViewData
import com.example.pointgrapher.presentation.screen.main.viewdata.MainScreenErrorTypeViewData
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import timber.log.Timber
import java.util.Date
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val requestPointsBatchUseCase: RequestPointsBatchUseCase,
    private val deletePointBatchUseCase: DeletePointBatchUseCase,
    observeBatchesUseCase: ObserveBatchesUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(MainScreenState())
    val state = _state.asStateFlow()

    private val _navigation = MutableSharedFlow<MainScreenNavigation>(
        replay = 0,
        extraBufferCapacity = 1,
        onBufferOverflow = BufferOverflow.DROP_OLDEST
    )
    val navigation = _navigation.asSharedFlow()

    init {
        viewModelScope.launch {
            observeBatchesUseCase()
                .collect { batches ->
                    _state.update { it.copy(batches = batches.toViewData()) }
                }
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

    fun requestPoints() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            try {
                val pointsNumber = doPreRequestChecks(state.value.requiredPointNumber)
                val batchId = requestPointsBatchUseCase(pointsNumber)
                _state.update {
                    it.copy(
                        isLoading = false,
                        errorTypeViewData = MainScreenErrorTypeViewData.None
                    )
                }
                _navigation.tryEmit(MainScreenNavigation(batchId))
            } catch (e: Exception) {
                val errorTypeViewData = extractErrorTypeViewData(e)
                _state.update {
                    it.copy(
                        errorTypeViewData = errorTypeViewData,
                        isLoading = false
                    )
                }

                val logMessage = "Error while requesting ${state.value.requiredPointNumber} points"
                Timber.e(e, logMessage)
            }
        }
    }

    fun onBatchClicked(batchId: String) {
        _navigation.tryEmit(MainScreenNavigation(batchId))
    }

    fun onBatchDeleted(batchId: String) {
        viewModelScope.launch {
            try {
                deletePointBatchUseCase(batchId)
            } catch (e: Exception) {
                val logMessage = if (e is BatchNotFoundException) {
                    // Batch was already deleted, no need to show anything to the user
                    "Batch $batchId was already deleted"
                } else {
                    "Error while deleting batch $batchId"
                }
                Timber.i(logMessage)
            }
        }
    }

    private fun doPreRequestChecks(number: String): Int {
        val pointNumber =
            number.toIntOrNull() ?: throw NumberFormatException("Point number must be a number")
        require(pointNumber > 0) { "Point number must be greater than 0" }
        return pointNumber
    }

    private fun extractErrorTypeViewData(e: Exception): MainScreenErrorTypeViewData {
        return when (e) {
            is NetworkError -> extractNetworkError(e)
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

    private fun List<BatchInfo>.toViewData(): List<BatchInfoViewData> =
        map { BatchInfoViewData(it.id, it.numberOfPoints, Date(it.timestamp)) }
}