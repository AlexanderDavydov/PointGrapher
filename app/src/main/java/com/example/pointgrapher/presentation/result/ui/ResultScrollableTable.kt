package com.example.pointgrapher.presentation.result.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.displayCutoutPadding
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.pointgrapher.presentation.result.model.PointViewData

@Composable
internal fun ResultScrollableTable(
    points: PointViewData,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier) {
        TwoColumnTableRow(colum1Text = "X", colum2Text = "Y")
        LazyColumn(
            content = {
                items(points.x.size) {
                    TwoColumnTableRow(
                        colum1Text = "${points.x[it].toDouble()}",
                        colum2Text = "${points.y[it]}",
                        isPrimaryColor = it % 2 == 0
                    )
                }
            }
        )
    }
}

@Composable
private fun TwoColumnTableRow(
    colum1Text: String,
    colum2Text: String,
    modifier: Modifier = Modifier,
    isPrimaryColor: Boolean = false,
) {
    val rowAccentColor = if (isPrimaryColor) {
        MaterialTheme.colorScheme.onPrimary
    } else {
        MaterialTheme.colorScheme.onSecondary
    }

    val rowAccentSecondaryColor = if (isPrimaryColor) {
        MaterialTheme.colorScheme.onSecondary
    } else {
        MaterialTheme.colorScheme.onPrimary
    }

    Row(
        modifier = modifier.background(rowAccentColor),
        horizontalArrangement = Arrangement.Center,
        content = {
            Text(
                modifier = Modifier
                    .border(
                        width = 1.dp,
                        color = rowAccentSecondaryColor
                    )
                    .weight(0.5f)
                    .fillMaxWidth(),
                text = colum1Text,
                textAlign = TextAlign.Center

            )
            Text(
                modifier = Modifier
                    .border(
                        width = 1.dp,
                        color = rowAccentSecondaryColor
                    )
                    .weight(0.5f)
                    .fillMaxWidth(),
                text = colum2Text,
                textAlign = TextAlign.Center
            )
        })
}