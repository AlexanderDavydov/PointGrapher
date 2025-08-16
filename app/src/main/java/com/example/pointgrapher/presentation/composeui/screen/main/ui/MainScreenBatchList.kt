package com.example.pointgrapher.presentation.composeui.screen.main.ui

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxState
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.Text
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import com.example.pointgrapher.R
import com.example.pointgrapher.presentation.composeui.screen.main.viewdata.BatchInfoViewData
import java.text.SimpleDateFormat
import java.util.Date

@Composable
internal fun MainScreenBatchList(
    batches: List<BatchInfoViewData>,
    onBatchClicked: (String) -> Unit,
    onBatchDeleted: (String) -> Unit,
) {
    Text(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        text = stringResource(R.string.main_screen_batch_list_title),
        style = MaterialTheme.typography.titleMedium
    )
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp),
        content = {
            items(
                items = batches,
                key = { it.id }
            ) { batch ->
                BatchInfoListItem(
                    modifier = Modifier.animateItem(),
                    batchInfoViewData = batch,
                    onClicked = { onBatchClicked(batch.id) },
                    onDeleted = { onBatchDeleted(batch.id) }
                )
            }
        }
    )
}

@Composable
private fun BatchInfoListItem(
    batchInfoViewData: BatchInfoViewData,
    onClicked: () -> Unit,
    onDeleted: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val dismissState = rememberSwipeToDismissBoxState(
        confirmValueChange = {
            it == SwipeToDismissBoxValue.EndToStart
        }
    )

    LaunchedEffect(dismissState.currentValue) {
        if (dismissState.currentValue == SwipeToDismissBoxValue.EndToStart) {
            onDeleted()
        }
    }

    SwipeToDismissBox(
        modifier = modifier,
        state = dismissState,
        backgroundContent = { SwipeDeleteBackground(dismissState) },
        enableDismissFromStartToEnd = false,
        content = {
            Column(
                modifier = Modifier
                    .clickable(onClick = onClicked, role = Role.Button)
                    .fillMaxWidth(),
                content = {
                    Text(
                        modifier = Modifier.padding(horizontal = 16.dp),
                        text = "Batch of ${batchInfoViewData.numberOfPoints} items",
                        style = MaterialTheme.typography.titleMedium
                    )
                    Text(
                        modifier = Modifier.padding(horizontal = 16.dp),
                        text = "Created: ${formatTimestamp(batchInfoViewData.time)}",
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Text(
                        modifier = Modifier.padding(horizontal = 16.dp),
                        text = "ID: ${batchInfoViewData.id}",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            )
        }
    )
}

@Composable
private fun SwipeDeleteBackground(
    dismissState: SwipeToDismissBoxState
) {
    val backgroundColor by animateColorAsState(
        targetValue = if (dismissState.targetValue == SwipeToDismissBoxValue.EndToStart) {
            MaterialTheme.colorScheme.error
        } else {
            Color.Transparent
        }
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundColor)
            .padding(horizontal = 20.dp),
        contentAlignment = Alignment.CenterEnd,
        content = {
            if (dismissState.targetValue == SwipeToDismissBoxValue.EndToStart) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Delete",
                    tint = MaterialTheme.colorScheme.onError
                )
            }
        }
    )
}

@Composable
private fun formatTimestamp(date: Date): String {
    val locale = LocalConfiguration.current.locales.get(0)
    val format = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", locale)
    return format.format(date)
}

