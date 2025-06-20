package com.example.pointgrapher.presentation.screen.result

import com.example.pointgrapher.presentation.screen.result.model.PointViewData

sealed interface ResultState {
    data object Loading : ResultState
    data object Error : ResultState
    data class Success(val points: PointViewData) : ResultState
}

