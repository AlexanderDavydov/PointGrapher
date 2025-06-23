package com.example.pointgrapher.presentation.screen.main.ui

import android.content.res.Configuration
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.displayCutoutPadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.pointgrapher.R
import com.example.pointgrapher.presentation.screen.main.MainScreenState
import com.example.pointgrapher.presentation.screen.main.MainViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun MainScreen(
    viewModel: MainViewModel = hiltViewModel(),
    onOpenResultScreen: (String) -> Unit
) {
    val state by viewModel.state.collectAsState()

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text(text = stringResource(R.string.app_name)) }
            )
        },
        content = { paddingValues ->
            val configuration = LocalConfiguration.current
            if (configuration.orientation == Configuration.ORIENTATION_LANDSCAPE) {
                MainScreenContentLandscape(
                    state = state,
                    modifier = Modifier.padding(paddingValues),
                    onPointNumberChanged = viewModel::onPointNumberChanged,
                    onRequestClicked = viewModel::request,
                    onBatchClicked = viewModel::onBatchClicked
                )
            } else {
                MainScreenContentPortrait(
                    state = state,
                    modifier = Modifier.padding(paddingValues),
                    onPointNumberChanged = viewModel::onPointNumberChanged,
                    onRequestClicked = viewModel::request,
                    onBatchClicked = viewModel::onBatchClicked
                )
            }
        }
    )

    LaunchedEffect(Unit) {
        launch {
            viewModel.navigation
                .collect { onOpenResultScreen(it.batchId) }
        }
    }
}

@Composable
private fun MainScreenContentPortrait(
    state: MainScreenState,
    onPointNumberChanged: (String) -> Unit,
    onRequestClicked: () -> Unit,
    onBatchClicked: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        content = {
            MainContentPortrait(state, onPointNumberChanged, onRequestClicked)
            if (state.batches.isNotEmpty()) {
                Spacer(modifier = Modifier.height(16.dp))
                MainScreenBatchList(
                    batches = state.batches,
                    onBatchClicked = onBatchClicked
                )
            }
        }
    )
}

@Composable
private fun MainContentPortrait(
    state: MainScreenState,
    onPointNumberChanged: (String) -> Unit,
    onRequestClicked: () -> Unit
) {
    Spacer(modifier = Modifier.height(16.dp))
    Text(
        modifier = Modifier.padding(horizontal = 16.dp),
        text = stringResource(R.string.main_screen_explanation),
        style = MaterialTheme.typography.titleMedium
    )
    Spacer(modifier = Modifier.height(16.dp))
    MainScreenTextField(
        modifier = Modifier
            .padding(horizontal = 16.dp)
            .fillMaxWidth(),
        state = state,
        onPointNumberChanged = onPointNumberChanged
    )
    MainScreenErrorSection(
        modifier = Modifier.fillMaxWidth(),
        state = state
    )
    MainScreenGoButton(
        modifier = Modifier
            .padding(horizontal = 16.dp)
            .fillMaxWidth(),
        state = state,
        onClick = onRequestClicked,
    )
}

@Composable
private fun MainScreenContentLandscape(
    state: MainScreenState,
    onPointNumberChanged: (String) -> Unit,
    onRequestClicked: () -> Unit,
    onBatchClicked: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .displayCutoutPadding()
            .fillMaxSize(),
        content = {
            MainContentLandscapeLeft(
                modifier = Modifier.weight(0.5f),
                state = state,
                onBatchClicked = onBatchClicked
            )
            MainContentLandscapeRight(
                modifier = Modifier.weight(0.5f),
                state = state,
                onPointNumberChanged = onPointNumberChanged,
                onRequestClicked = onRequestClicked
            )
        }
    )
}

@Composable
private fun MainContentLandscapeLeft(
    state: MainScreenState,
    onBatchClicked: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
    ) {
        Text(
            modifier = Modifier.padding(horizontal = 16.dp),
            text = stringResource(R.string.main_screen_explanation),
            style = MaterialTheme.typography.titleMedium
        )
        Spacer(modifier = Modifier.height(16.dp))
        if (state.batches.isNotEmpty()) {
            MainScreenBatchList(
                batches = state.batches,
                onBatchClicked = onBatchClicked
            )
        }
    }
}

@Composable
private fun MainContentLandscapeRight(
    state: MainScreenState,
    onPointNumberChanged: (String) -> Unit,
    onRequestClicked: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        MainScreenTextField(
            modifier = Modifier
                .padding(horizontal = 16.dp)
                .fillMaxWidth(),
            state = state,
            onPointNumberChanged = onPointNumberChanged
        )
        MainScreenErrorSection(
            modifier = Modifier.fillMaxWidth(),
            state = state
        )
        MainScreenGoButton(
            modifier = Modifier
                .padding(horizontal = 16.dp)
                .fillMaxWidth(),
            state = state,
            onClick = onRequestClicked,
        )
    }
}