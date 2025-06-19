package com.example.pointgrapher.presentation

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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.pointgrapher.domain.model.Point

@Composable
fun PointMainScreen(
    viewModel: PointViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()

    Scaffold(
        bottomBar = {
            if (state.isLoading.not()) {
                PointBottomBar(
                    pointNumber = state.requeredPointNumber,
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
                    viewData = state.points,
                    modifier = Modifier.padding(paddingValues)
                )
            }
        }
    )
}

@Composable
private fun PointBottomBar(
    pointNumber: String,
    onPointNumberChanged: (String) -> Unit,
    onRequestClicked: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .navigationBarsPadding()
            .padding(top = 16.dp, bottom = 8.dp),
        content = {
            val textState = remember(pointNumber) { mutableStateOf(pointNumber) }
            TextField(
                modifier = Modifier.padding(start = 16.dp, end = 8.dp),
                value = textState.value,
                onValueChange = { onPointNumberChanged(it) },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
            )
            Button(
                onClick = onRequestClicked,
                content = { Text(text = "GO!") }
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

@Composable
fun SuccessContent(
    viewData: List<Point>,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "${viewData.size}",
            style = TextStyle(fontSize = 48.sp, color = Color.Yellow)
        )
    }
}