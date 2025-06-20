package com.example.pointgrapher.presentation.screen.result.ui

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.pointgrapher.presentation.screen.result.model.PointViewData
import com.patrykandpatrick.vico.compose.cartesian.CartesianChartHost
import com.patrykandpatrick.vico.compose.cartesian.axis.rememberBottom
import com.patrykandpatrick.vico.compose.cartesian.axis.rememberStart
import com.patrykandpatrick.vico.compose.cartesian.layer.rememberLineCartesianLayer
import com.patrykandpatrick.vico.compose.cartesian.rememberCartesianChart
import com.patrykandpatrick.vico.compose.cartesian.rememberVicoZoomState
import com.patrykandpatrick.vico.core.cartesian.axis.HorizontalAxis
import com.patrykandpatrick.vico.core.cartesian.axis.VerticalAxis
import com.patrykandpatrick.vico.core.cartesian.data.CartesianChartModelProducer
import com.patrykandpatrick.vico.core.cartesian.data.lineSeries

@Composable
internal fun ResultSuccessState(
    modifier: Modifier = Modifier,
    points: PointViewData
) {
    Column(
        modifier = modifier.fillMaxSize(),
        content = {
            ScrollableTable(
                modifier = Modifier.weight(1f),
                points = points
            )
            Graph(
                modifier = Modifier.weight(1f),
                points = points
            )
        },
    )
}


@Composable
private fun ScrollableTable(
    points: PointViewData,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier.horizontalScroll(rememberScrollState()),
        content = {
            Column {
                points.x.forEachIndexed { index, point ->
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text("X: ${point.toDouble()}")
                        Text("Y: ${points.y[index]}")
                    }
                }
            }
        }
    )
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
        modifier = modifier,
        chart = rememberCartesianChart(
            rememberLineCartesianLayer(),
            startAxis = VerticalAxis.rememberStart(),
            bottomAxis = HorizontalAxis.rememberBottom(),
        ),
        zoomState = rememberVicoZoomState(),
        modelProducer = modelProducer,
    )
}