package com.example.pointgrapher.presentation.result.composeui

import android.graphics.Bitmap
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.Canvas
import androidx.compose.ui.graphics.drawscope.draw
import androidx.core.graphics.createBitmap
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
internal fun ResultGraph(
    points: PointViewData,
    modifier: Modifier = Modifier,
    onChartCapture: (ResultCaptureChartEvent) -> Unit,
    captureState: MutableState<Boolean>,
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
        modifier = modifier
            .fillMaxSize()
            .captureAsBitmap(
                shouldCapture = captureState.value,
                onBitmapCaptured = { onChartCapture(ResultCaptureChartEvent.Success(it)) },
                onCaptureError = { onChartCapture(ResultCaptureChartEvent.Error(it)) }
            ),
        chart = rememberCartesianChart(
            rememberLineCartesianLayer(),
            startAxis = VerticalAxis.rememberStart(),
            bottomAxis = HorizontalAxis.rememberBottom(),
        ),
        zoomState = rememberVicoZoomState(initialZoom = Zoom.Content),
        modelProducer = modelProducer,
    )
}

fun Modifier.captureAsBitmap(
    shouldCapture: Boolean,
    onBitmapCaptured: (Bitmap) -> Unit,
    onCaptureError: (Exception) -> Unit = {}
) = then(
    Modifier.drawWithContent {
        val width = size.width.toInt()
        val height = size.height.toInt()

        if (shouldCapture) {
            try {
                if (width <= 0 || height <= 0) {
                    onCaptureError(IllegalArgumentException("Invalid dimensions: ${width}x${height}"))
                } else if (width * height > 4096 * 4096) {
                    onCaptureError(IllegalArgumentException("Image too large"))
                } else {
                    val picture = android.graphics.Picture()
                    val recordingCanvas = picture.beginRecording(width, height)
                    val composeCanvas = Canvas(recordingCanvas)

                    draw(
                        density = this,
                        layoutDirection = layoutDirection,
                        canvas = composeCanvas,
                        size = size
                    ) {
                        this@drawWithContent.drawContent()
                    }

                    picture.endRecording()

                    val bitmap = createBitmap(width, height)
                    val canvas = android.graphics.Canvas(bitmap)
                    canvas.drawColor(android.graphics.Color.WHITE)
                    canvas.drawPicture(picture)

                    onBitmapCaptured(bitmap)
                }
            } catch (e: Exception) {
                onCaptureError(e)
            }
        }

        drawContent()
    }
)
