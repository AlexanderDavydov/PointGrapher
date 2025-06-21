package com.example.pointgrapher.presentation.screen.main.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import com.example.pointgrapher.R
import com.example.pointgrapher.domain.model.BatchInfo
import java.text.SimpleDateFormat
import java.util.Date

@Composable
internal fun MainScreenBatchList(
    batches: List<BatchInfo>,
    onBatchClicked: (String) -> Unit
) {
    Text(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 32.dp),
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
                    batchInfo = batch,
                    onClicked = { onBatchClicked(batch.id) }
                )
            }
        }
    )
}

@Composable
private fun BatchInfoListItem(
    batchInfo: BatchInfo,
    onClicked: () -> Unit
) {
    Column(
        modifier = Modifier
            .clickable(onClick = onClicked, role = Role.Button)
            .fillMaxWidth(),
        content = {
            Text(
                modifier = Modifier.padding(horizontal = 32.dp),
                text = "Batch of ${batchInfo.numberOfPoints} items",
                style = MaterialTheme.typography.titleMedium
            )
            Text(
                modifier = Modifier.padding(horizontal = 32.dp),
                text = "Created: ${formatTimestamp(batchInfo.timestamp)}",
                style = MaterialTheme.typography.bodyMedium
            )
            Text(
                modifier = Modifier.padding(horizontal = 32.dp),
                text = "ID: ${batchInfo.id}",
                style = MaterialTheme.typography.bodyMedium
            )
        }
    )
}

@Composable
private fun formatTimestamp(timestamp: Long): String {
    val date = Date(timestamp)
    val locale = LocalConfiguration.current.locales.get(0)
    val format = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", locale)
    return format.format(date)
}

