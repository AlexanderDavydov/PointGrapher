package com.example.pointgrapher.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pointgrapher.domain.usecase.GetPointsUsecase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PointViewModel @Inject constructor(
    private val getPointsUsecase: GetPointsUsecase
) : ViewModel() {

    private val _state = MutableStateFlow<PointScreenState>(PointScreenState.Content(emptyList()))
    val state = _state.asStateFlow()

    fun request() {
        viewModelScope.launch {
            _state.value = PointScreenState.Loading
            try {
                val points = getPointsUsecase(10)
                _state.value = PointScreenState.Content(points)
            } catch (e: Exception) {
                _state.value = PointScreenState.Error
            }
        }
    }

}
