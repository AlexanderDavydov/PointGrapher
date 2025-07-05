package com.example.pointgrapher.domain.usecase

import com.example.pointgrapher.domain.model.ValidationResult
import javax.inject.Inject

class ValidatePointCountUseCase @Inject constructor() {

    operator fun invoke(input: String): ValidationResult {
        return when {
            input.isBlank() -> ValidationResult.Failure.Empty
            !input.all { it.isDigit() } -> ValidationResult.Failure.MustBeNumber
            else -> {
                val parsedValue = input.toIntOrNull()
                when {
                    parsedValue == null -> ValidationResult.Failure.InvalidFormat
                    parsedValue < MIN_POINTS -> ValidationResult.Failure.LowValue
                    parsedValue > MAX_POINTS -> ValidationResult.Failure.HighValue
                    else -> ValidationResult.Success(parsedValue)
                }
            }
        }
    }
}

private const val MIN_POINTS = 1
private const val MAX_POINTS = 1000