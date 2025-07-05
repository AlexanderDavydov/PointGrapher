package com.example.pointgrapher.presentation.composeui.screen.main.ui

import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import com.example.pointgrapher.R
import com.example.pointgrapher.presentation.composeui.screen.main.MainScreenState
import com.example.pointgrapher.presentation.composeui.screen.main.viewdata.MainScreenErrorTypeViewData
import com.example.pointgrapher.presentation.composeui.screen.main.viewdata.ValidationErrorTypeViewData

@Composable
internal fun MainScreenTextField(
    state: MainScreenState,
    onPointNumberChanged: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    TextField(
        modifier = modifier,
        value = state.requiredPointNumber,
        onValueChange = onPointNumberChanged,
        prefix = { Text(text = stringResource(R.string.main_screen_text_field_prefix)) },
        suffix = { Text(text = stringResource(R.string.main_screen_text_field_suffix)) },
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
        isError = state.isInputError,
        supportingText = { Text(text = getErrorString(state.errorTypeViewData)) }
    )
}

@Composable
private fun getErrorString(errorTypeViewData: MainScreenErrorTypeViewData): String {
    val id = when (errorTypeViewData) {
        MainScreenErrorTypeViewData.None -> return ""
        is MainScreenErrorTypeViewData.ValidationError -> getValidationErrorString(errorTypeViewData.type)
        MainScreenErrorTypeViewData.RequestError -> R.string.main_screen_error_something_happened_during_request
        MainScreenErrorTypeViewData.RequestedIncorrectnessError -> R.string.main_screen_error_server_incorrect_points
        MainScreenErrorTypeViewData.Unspecified -> R.string.main_screen_error_general
    }
    return stringResource(id)
}

private fun getValidationErrorString(type: ValidationErrorTypeViewData): Int {
    return when (type) {
        ValidationErrorTypeViewData.Empty -> R.string.main_screen_error_empty_points_number
        ValidationErrorTypeViewData.InvalidFormat -> R.string.main_screen_error_invalid_format
        ValidationErrorTypeViewData.MustBeNumber -> R.string.main_screen_error_must_be_number
        ValidationErrorTypeViewData.TooHighValue -> R.string.main_screen_error_too_high_value
        ValidationErrorTypeViewData.TooLowValue -> R.string.main_screen_error_too_low_value
    }
}