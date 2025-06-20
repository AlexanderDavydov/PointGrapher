package com.example.pointgrapher.presentation.screen.main.ui

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.keyframes
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material3.Button
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.pointgrapher.R
import com.example.pointgrapher.presentation.screen.main.MainScreenState
import com.example.pointgrapher.presentation.screen.main.MainViewModel
import com.example.pointgrapher.presentation.screen.main.viewdata.MainScreenErrorTypeViewData
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
            MainScreenContent(
                state = state,
                modifier = Modifier.padding(paddingValues),
                onPointNumberChanged = viewModel::onPointNumberChanged,
                onRequestClicked = viewModel::request,
            )
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
private fun MainScreenContent(
    state: MainScreenState,
    onPointNumberChanged: (String) -> Unit,
    onRequestClicked: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        content = {

            Spacer(modifier = Modifier.height(16.dp))
            Text(
                modifier = Modifier.padding(horizontal = 16.dp),
                text = stringResource(R.string.main_screen_explanation),
                style = MaterialTheme.typography.titleMedium
            )
            Spacer(modifier = Modifier.height(64.dp))
            TextField(
                modifier = Modifier
                    .padding(horizontal = 16.dp)
                    .fillMaxWidth(),
                value = state.requiredPointNumber,
                onValueChange = onPointNumberChanged,
                prefix = { Text(text = "Request: ") },
                suffix = { Text(text = "points") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                isError = state.isInputError,
                supportingText = { Text(text = getErrorString(state.errorTypeViewData)) }
            )
            Spacer(modifier = Modifier.height(26.dp))

            AnimatedContent(
                targetState = state.isError,
                modifier = Modifier.fillMaxWidth(),
            ) {
                if (it) {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        content = {
                            Text(
                                modifier = Modifier.padding(horizontal = 24.dp),
                                text = "Error occurred while request points",
                                style = MaterialTheme.typography.titleMedium,
                                textAlign = TextAlign.Center
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(text = "please try again")
                            Spacer(modifier = Modifier.height(8.dp))
                            BounceArrowIcon()
                            Spacer(modifier = Modifier.height(16.dp))
                        }
                    )
                }
            }

            Button(
                modifier = Modifier
                    .padding(horizontal = 16.dp)
                    .fillMaxWidth(),
                onClick = onRequestClicked,
                content = {
                    if (state.isLoading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(24.dp),
                            color = MaterialTheme.colorScheme.onSecondary,
                            strokeWidth = 2.dp,
                        )
                    } else {
                        Text(
                            text = stringResource(
                                if (state.isError) {
                                    R.string.main_screen_button_go_again
                                } else {
                                    R.string.main_screen_button_go
                                }
                            )
                        )
                    }
                }
            )
        }
    )
}

@Composable
private fun getErrorString(errorTypeViewData: MainScreenErrorTypeViewData) =
    when (errorTypeViewData) {
        MainScreenErrorTypeViewData.None -> ""
        MainScreenErrorTypeViewData.EmptyNumber -> "Request points number can't be empty"
        MainScreenErrorTypeViewData.NegativeNumber -> "Request points number must be positive"
        MainScreenErrorTypeViewData.RequestError -> "Something happened during request"
        MainScreenErrorTypeViewData.RequestedIncorrectnessError -> "Server returned incorrect points number error. Value either too big or too small."
        MainScreenErrorTypeViewData.Unspecified -> "Something went wrong"
    }

@Composable
private fun BounceArrowIcon(
    modifier: Modifier = Modifier,
) {
    val infiniteTransition = rememberInfiniteTransition(label = "bounce")
    val scale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = keyframes {
                durationMillis = 1000
                1f at 0 using LinearEasing
                1.2f at 200 using FastOutSlowInEasing
                0.9f at 400 using LinearEasing
                1.1f at 600 using FastOutSlowInEasing
                1f at 800 using LinearEasing
            },
            repeatMode = RepeatMode.Restart
        ),
        label = "bounce_scale"
    )

    Icon(
        imageVector = Icons.AutoMirrored.Filled.ArrowForward,
        contentDescription = null,
        modifier = modifier
            .rotate(90f)
            .scale(scale)
            .size(48.dp)
    )
}
