package com.example.pointgrapher.presentation.result.composeui

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.pointgrapher.R
import com.example.pointgrapher.presentation.result.ResultState
import com.example.pointgrapher.presentation.result.ResultUINotification
import com.example.pointgrapher.presentation.result.ResultViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun ResultScreen(
    batchId: String,
    onBack: () -> Unit,
    viewModel: ResultViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val captureState = remember { mutableStateOf(false) }

    LaunchedEffect(batchId) {
        viewModel.onBatchIdChanged(batchId)
    }

    LaunchedEffect(Unit) {
        viewModel.notification.collect { notification ->
            snackbarHostState.showSnackbar(
                message = when (notification) {
                    is ResultUINotification.ChartSaved -> "Chart Saved to ${notification.path}"
                    is ResultUINotification.Error -> notification.message
                },
                withDismissAction = notification is ResultUINotification.Error,
                duration = when (notification) {
                    is ResultUINotification.ChartSaved -> SnackbarDuration.Long
                    is ResultUINotification.Error -> SnackbarDuration.Indefinite
                }
            )
        }
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("${stringResource(R.string.result_screen_title)} (Compose)") },
                navigationIcon = {
                    IconButton(
                        onClick = onBack,
                        content = {
                            Icon(
                                imageVector = Icons.AutoMirrored.Default.ArrowBack,
                                contentDescription = "Go Back"
                            )
                        }
                    )
                },
                actions = {
                    IconButton(
                        onClick = {
                            captureState.value = true
                        },
                        content = {
                            Icon(
                                painter = painterResource(R.drawable.ic_save),
                                contentDescription = "Save Chart"
                            )
                        }
                    )
                }
            )
        },
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState)
        },
        content = {
            ResultScreenState(
                state = state,
                modifier = Modifier.padding(it),
                captureState = captureState,
                onChartCapture = {
                    captureState.value = false
                    when (it) {
                        is ResultCaptureChartEvent.Error -> viewModel.onSaveChartError(it.e)
                        is ResultCaptureChartEvent.Success -> viewModel.saveChartImage(
                            bitmap = it.bitmap,
                            chartType = "compose_chart"
                        )
                    }
                }
            )
        }
    )
}

@Composable
private fun ResultScreenState(
    state: ResultState,
    modifier: Modifier = Modifier,
    captureState: MutableState<Boolean>,
    onChartCapture: (ResultCaptureChartEvent) -> Unit,
) {
    when (state) {
        is ResultState.Success -> ResultSuccessState(
            modifier = modifier,
            points = state.points,
            captureState = captureState,
            onChartCapture = onChartCapture
        )

        is ResultState.Loading -> ResultLoadingState(modifier = modifier)
        is ResultState.Error -> ResultErrorState(modifier = modifier)
    }
}