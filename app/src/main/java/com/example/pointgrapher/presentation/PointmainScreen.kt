package com.example.pointgrapher.presentation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
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
            Button(
                onClick = viewModel::request,
                content = { Text(text = "Request") }
            )
        },
        content = { ScreenContent(state, it) }
    )
}

@Composable
private fun ScreenContent(
    state: PointScreenState,
    it: PaddingValues
) {
    when (state) {
        is PointScreenState.Content -> SuccessContent(
            viewData = state.points,
            modifier = Modifier.padding(it)
        )

        is PointScreenState.Error -> {

        }
        is PointScreenState.Loading -> {

        }
    }
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