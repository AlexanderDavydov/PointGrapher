package com.example.pointgrapher.presentation.points.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.pointgrapher.R
import com.example.pointgrapher.presentation.points.model.PointScreenState
import com.example.pointgrapher.presentation.points.PointViewModel

@Composable
fun PointMainScreen(
    viewModel: PointViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()

    Scaffold(
        bottomBar = {
            if (state.isLoading.not()) {
                PointBottomBar(
                    state = state,
                    onPointNumberChanged = viewModel::onPointNumberChanged,
                    onRequestClicked = viewModel::request
                )
            }
        },
        content = { paddingValues ->
            when {
                state.isLoading -> LoadingContent(Modifier.padding(paddingValues))
                state.isError -> PointScreenError(
                    modifier = Modifier.padding(paddingValues),
                    errorTypeViewData = state.errorTypeViewData
                )

                else -> SuccessContent(
                    points = state.points,
                    modifier = Modifier.padding(paddingValues)
                )
            }
        }
    )
}

@Composable
private fun PointBottomBar(
    state: PointScreenState,
    onPointNumberChanged: (String) -> Unit,
    onRequestClicked: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .navigationBarsPadding()
            .padding(top = 16.dp, bottom = 8.dp),
        content = {
            val textState = remember(state.requiredPointNumber) {
                mutableStateOf(state.requiredPointNumber)
            }

            TextField(
                modifier = Modifier
                    .padding(horizontal = 16.dp)
                    .fillMaxWidth()
                    .weight(1f),
                value = textState.value,
                label = { Text(text = "Points number") },
                isError = state.isInputError,
                onValueChange = { onPointNumberChanged(it) },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
            )

            val buttonTextRes = if (state.isError) R.string.go_again else R.string.go
            Button(
                modifier = Modifier.padding(horizontal = 16.dp),
                onClick = onRequestClicked,
                content = { Text(text = stringResource(buttonTextRes)) }
            )
        }
    )
}

@Composable
private fun LoadingContent(modifier: Modifier) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center,
        content = { CircularProgressIndicator() }
    )
}