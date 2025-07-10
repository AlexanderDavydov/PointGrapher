package com.example.pointgrapher.presentation.main

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pointgrapher.domain.exception.BatchNotFoundException
import com.example.pointgrapher.domain.exception.NetworkError
import com.example.pointgrapher.domain.model.BatchInfo
import com.example.pointgrapher.domain.model.ValidationResult
import com.example.pointgrapher.domain.model.ValidationResult.Failure
import com.example.pointgrapher.domain.usecase.ClearUIFrameworkUseCase
import com.example.pointgrapher.domain.usecase.DeletePointBatchUseCase
import com.example.pointgrapher.domain.usecase.ObserveBatchesUseCase
import com.example.pointgrapher.domain.usecase.RequestPointsBatchUseCase
import com.example.pointgrapher.domain.usecase.ValidatePointCountUseCase
import com.example.pointgrapher.presentation.main.viewdata.BatchInfoViewData
import com.example.pointgrapher.presentation.main.viewdata.MainScreenErrorTypeViewData
import com.example.pointgrapher.presentation.main.viewdata.MainScreenErrorTypeViewData.ValidationError
import com.example.pointgrapher.presentation.main.viewdata.ValidationErrorTypeViewData
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
    private val clearUIFrameworkUseCase: ClearUIFrameworkUseCase,
    private val deletePointBatchUseCase: DeletePointBatchUseCase,
    private val requestPointsBatchUseCase: RequestPointsBatchUseCase,
    private val validatePointCountUseCase: ValidatePointCountUseCase,
    private val savedStateHandle: SavedStateHandle,
    observeBatchesUseCase: ObserveBatchesUseCase,
) : ViewModel() {

    private var savedInputText: String
        get() = savedStateHandle.get<String>("input_text") ?: initialPointsValue
        set(value) = savedStateHandle.set("input_text", value)

    private val _state = MutableStateFlow(MainScreenState(savedInputText))
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
        savedInputText = newValue

        val validationResult = validatePointCountUseCase(newValue)

        _state.update {
            it.copy(
                requiredPointNumber = newValue,
                errorTypeViewData = when (validationResult) {
                    is ValidationResult.Success -> MainScreenErrorTypeViewData.None
                    Failure.Empty -> ValidationError(ValidationErrorTypeViewData.Empty)
                    Failure.MustBeNumber -> ValidationError(ValidationErrorTypeViewData.MustBeNumber)
                    Failure.InvalidFormat -> ValidationError(ValidationErrorTypeViewData.InvalidFormat)
                    Failure.LowValue -> ValidationError(ValidationErrorTypeViewData.TooLowValue)
                    Failure.HighValue -> ValidationError(ValidationErrorTypeViewData.TooHighValue)
                }
            )
        }
    }

    fun requestPoints() {
        val validationResult = validatePointCountUseCase(state.value.requiredPointNumber)
        when (validationResult) {
            is ValidationResult.Success -> handleRequestValidationSuccess(validationResult)
            is Failure -> handleRequestValidationFailure(validationResult)
        }
    }

    fun onBatchClicked(batchId: String) {
        _navigation.tryEmit(MainScreenNavigation.GoToResult(batchId))
    }

    fun onBatchDeleted(batchId: String) {
        viewModelScope.launch {
            try {
                deletePointBatchUseCase(batchId)
            } catch (e: Exception) {
                val logMessage = if (e is BatchNotFoundException) {
                    "Batch $batchId was already deleted"
                } else {
                    "Error while deleting batch $batchId"
                }
                Timber.i(logMessage)
            }
        }
    }

    private fun handleRequestValidationSuccess(validationResult: ValidationResult.Success) {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            try {
                val batchId = requestPointsBatchUseCase(validationResult.data)
                _state.update {
                    it.copy(
                        isLoading = false,
                        errorTypeViewData = MainScreenErrorTypeViewData.None
                    )
                }
                _navigation.tryEmit(MainScreenNavigation.GoToResult(batchId))
            } catch (e: Exception) {
                val errorTypeViewData = extractErrorTypeViewData(e)
                _state.update {
                    it.copy(
                        errorTypeViewData = errorTypeViewData,
                        isLoading = false
                    )
                }

                val logMessage =
                    "Error while requesting ${state.value.requiredPointNumber} points"
                Timber.e(e, logMessage)
            }
        }
    }

    private fun handleRequestValidationFailure(failure: Failure) {
        _state.update {
            it.copy(
                isLoading = false,
                errorTypeViewData = when (failure) {
                    Failure.Empty -> ValidationError(ValidationErrorTypeViewData.Empty)
                    Failure.MustBeNumber -> ValidationError(ValidationErrorTypeViewData.MustBeNumber)
                    Failure.InvalidFormat -> ValidationError(ValidationErrorTypeViewData.InvalidFormat)
                    Failure.LowValue -> ValidationError(ValidationErrorTypeViewData.TooLowValue)
                    Failure.HighValue -> ValidationError(ValidationErrorTypeViewData.TooHighValue)
                }
            )
        }
    }

    private fun extractErrorTypeViewData(e: Exception): MainScreenErrorTypeViewData {
        return when (e) {
            is NetworkError -> extractNetworkError(e)
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

    fun onOpenOnboarding() {
        clearUIFrameworkUseCase()
        _navigation.tryEmit(MainScreenNavigation.GoToOnboarding)
    }
}

private const val initialPointsValue = "10"