package com.example.pointgrapher.presentation.screen.result

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pointgrapher.domain.model.PointBatch
import com.example.pointgrapher.domain.usecase.GetPointsBatchUseCase
import com.example.pointgrapher.presentation.screen.result.model.PointViewData
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ResultViewModel @Inject constructor(
    private val getPointsBatchUseCase: GetPointsBatchUseCase
) : ViewModel() {

    private val _state = MutableStateFlow<ResultState>(ResultState.Loading)
    val state = _state.asStateFlow()

    fun onBatchIdChanged(batchId: String) {
        _state.value = ResultState.Loading
        viewModelScope.launch {
            try {
                val points = getPointsBatchUseCase(batchId)
                _state.value = ResultState.Success(points.mapToViewData())
            } catch (e: Exception) {
                _state.value = ResultState.Error
            }
        }
    }

    private fun PointBatch.mapToViewData(): PointViewData {
        val sorted = x.zip(y).sortedBy { it.first }
        return PointViewData(
            x = sorted.map { it.first },
            y = sorted.map { it.second }
        )
    }
}
