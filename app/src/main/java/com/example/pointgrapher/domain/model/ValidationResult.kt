package com.example.pointgrapher.domain.model

sealed class ValidationResult {

    data class Success(val data: Int) : ValidationResult()
    sealed class Failure : ValidationResult() {
        data object Empty : Failure()
        data object MustBeNumber : Failure()
        data object InvalidFormat : Failure()
        data object LowValue : Failure()
        data object HighValue : Failure()
    }
}