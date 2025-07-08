package com.example.pointgrapher.presentation.result.xmlbased

import android.graphics.Color
import com.example.pointgrapher.presentation.result.model.PointViewData
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GraphManager @Inject constructor() {

    fun setupChart(chart: LineChart) {
        chart.apply {
            // Общие настройки
            description.isEnabled = false
            setTouchEnabled(true)
            isDragEnabled = true
            setScaleEnabled(true)
            setPinchZoom(true)

            // Настройки осей
            xAxis.apply {
                position = XAxis.XAxisPosition.BOTTOM
                setDrawGridLines(true)
                granularity = 1f
                textColor = Color.GRAY
            }

            axisLeft.apply {
                setDrawGridLines(true)
                textColor = Color.GRAY
            }

            axisRight.isEnabled = false
            legend.isEnabled = false

            // Стилизация
            setBackgroundColor(Color.WHITE)
            setBorderColor(Color.LTGRAY)
            setGridBackgroundColor(Color.WHITE)

            // Анимация
            animateX(1000)
        }
    }

    fun updateData(chart: LineChart, points: PointViewData) {
        if (points.x.isEmpty() || points.y.isEmpty()) {
            chart.clear()
            return
        }

        val entries = points.x.mapIndexed { index, x ->
            Entry(x.toFloat(), points.y[index].toFloat())
        }

        val dataSet = LineDataSet(entries, "Points").apply {
            color = Color.parseColor("#1976D2")
            setCircleColor(Color.parseColor("#1976D2"))

            lineWidth = 2f
            circleRadius = 4f
            setDrawCircleHole(false)

            valueTextSize = 0f

            mode = LineDataSet.Mode.LINEAR

            setDrawFilled(false)

            highLightColor = Color.parseColor("#42A5F5")
            setDrawHighlightIndicators(true)
        }

        chart.data = LineData(dataSet)
        chart.invalidate()
    }

    fun getChartBitmap(chart: LineChart): android.graphics.Bitmap? {
        return chart.chartBitmap
    }
}