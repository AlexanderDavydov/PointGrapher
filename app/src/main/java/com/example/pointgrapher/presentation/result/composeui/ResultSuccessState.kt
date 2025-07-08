package com.example.pointgrapher.presentation.result.composeui

import android.content.res.Configuration
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.displayCutoutPadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.dp
import com.example.pointgrapher.presentation.result.model.PointViewData
import com.patrykandpatrick.vico.compose.cartesian.CartesianChartHost
import com.patrykandpatrick.vico.compose.cartesian.axis.rememberBottom
import com.patrykandpatrick.vico.compose.cartesian.axis.rememberStart
import com.patrykandpatrick.vico.compose.cartesian.layer.rememberLineCartesianLayer
import com.patrykandpatrick.vico.compose.cartesian.rememberCartesianChart
import com.patrykandpatrick.vico.compose.cartesian.rememberVicoZoomState
import com.patrykandpatrick.vico.core.cartesian.Zoom
import com.patrykandpatrick.vico.core.cartesian.axis.HorizontalAxis
import com.patrykandpatrick.vico.core.cartesian.axis.VerticalAxis
import com.patrykandpatrick.vico.core.cartesian.data.CartesianChartModelProducer
import com.patrykandpatrick.vico.core.cartesian.data.lineSeries

@Composable
internal fun ResultSuccessState(
    modifier: Modifier = Modifier,
    points: PointViewData
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
                    points = points
                )
                Graph(
                    modifier = Modifier.weight(1f),
                    points = points
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
                Graph(
                    modifier = Modifier.weight(1f),
                    points = points
                )
            }
        )
    }
}


@Composable
private fun Graph(
    points: PointViewData,
    modifier: Modifier = Modifier,
) {
    val modelProducer = remember { CartesianChartModelProducer() }

    LaunchedEffect(points) {
        if (points != PointViewData.Empty) {
            modelProducer.runTransaction {
                lineSeries { series(x = points.x, y = points.y) }
            }
        }
    }

    CartesianChartHost(
        modifier = modifier.fillMaxSize(),
        chart = rememberCartesianChart(
            rememberLineCartesianLayer(),
            startAxis = VerticalAxis.rememberStart(),
            bottomAxis = HorizontalAxis.rememberBottom(),
        ),
        zoomState = rememberVicoZoomState(initialZoom = Zoom.Content),
        modelProducer = modelProducer,
    )
}