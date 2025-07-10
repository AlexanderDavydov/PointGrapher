package com.example.pointgrapher.presentation.result

import android.graphics.Bitmap
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pointgrapher.domain.model.PointBatch
import com.example.pointgrapher.domain.usecase.GetPointsBatchUseCase
import com.example.pointgrapher.domain.usecase.SaveChartImageUseCase
import com.example.pointgrapher.presentation.result.model.PointViewData
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class ResultViewModel @Inject constructor(
    private val getPointsBatchUseCase: GetPointsBatchUseCase,
    private val saveChartImageUseCase: SaveChartImageUseCase
) : ViewModel() {

    private val _state = MutableStateFlow<ResultState>(ResultState.Loading)
    val state = _state.asStateFlow()

    private val _notification = MutableSharedFlow<ResultUINotification>(
        replay = 0,
        extraBufferCapacity = 1,
        onBufferOverflow = BufferOverflow.DROP_OLDEST
    )
    val notification = _notification.asSharedFlow()

    fun onBatchIdChanged(batchId: String) {
        _state.value = ResultState.Loading
        viewModelScope.launch {
            try {
                val points = getPointsBatchUseCase(batchId)
                _state.value = ResultState.Success(points.mapToViewData())
            } catch (e: Exception) {
                _state.value = ResultState.Error
                Timber.e(e)
            }
        }
    }

    fun saveChartImage(bitmap: Bitmap, chartType: String) {
        viewModelScope.launch {
            try {
                val savedPath = saveChartImageUseCase(bitmap, chartType)
                _notification.emit(ResultUINotification.ChartSaved(savedPath))
            } catch (e: Exception) {
                onSaveChartError(e)
            }
        }
    }

     fun onSaveChartError(e: Exception) {
        _notification.tryEmit(ResultUINotification.Error("Failed to save chart: ${e.message}"))
    }

    private fun PointBatch.mapToViewData(): PointViewData {
        val sorted = x.zip(y).sortedBy { it.first }
        return PointViewData(
            x = sorted.map { it.first },
            y = sorted.map { it.second }
        )
    }
}
