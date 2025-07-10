package com.example.pointgrapher.presentation.result.composeui

import android.content.res.Configuration
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.displayCutoutPadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.dp
import com.example.pointgrapher.presentation.result.model.PointViewData

@Composable
internal fun ResultSuccessState(
    modifier: Modifier = Modifier,
    points: PointViewData,
    captureState: MutableState<Boolean>,
    onChartCapture: (ResultCaptureChartEvent) -> Unit,
) {
    val configuration = LocalConfiguration.current
    val isLandscape = configuration.orientation == Configuration.ORIENTATION_LANDSCAPE

    if (isLandscape) {
        Row(
            modifier = modifier
                .displayCutoutPadding()
                .fillMaxSize(),
            content = {
                ResultScrollableTable(
                    modifier = Modifier.weight(1f),
                    points = points,
                )
                ResultGraph(
                    modifier = Modifier.weight(1f),
                    points = points,
                    captureState = captureState,
                    onChartCapture = onChartCapture
                )
            }
        )
    } else {
        Column(
            modifier = modifier.fillMaxSize(),
            content = {
                ResultScrollableTable(
                    modifier = Modifier.weight(1f),
                    points = points
                )
                Spacer(modifier = Modifier.height(16.dp))
                ResultGraph(
                    points = points,
                    modifier = Modifier.weight(1f),
                    captureState = captureState,
                    onChartCapture = onChartCapture,
                )
            }
        )
    }
}