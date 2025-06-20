package com.example.pointgrapher.presentation.screen.result

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class ResultViewModel @Inject constructor() : ViewModel() {

    private val _state = MutableStateFlow(ResultState.Loading)
    val state = _state.asStateFlow()


    fun onBackClicked() {

    }


//    private fun List<Point>.mapToViewData(): PointScreenState.PointViewData =
//        PointScreenState.PointViewData(x = map { it.x }, y = map { it.y })
}
