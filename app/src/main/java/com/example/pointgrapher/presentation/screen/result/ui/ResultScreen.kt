package com.example.pointgrapher.presentation.screen.result.ui

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.pointgrapher.R
import com.example.pointgrapher.presentation.screen.result.ResultState
import com.example.pointgrapher.presentation.screen.result.ResultViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun ResultScreen(
    viewModel: ResultViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsState()

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text(stringResource(R.string.result_screen_title)) },
                navigationIcon = {
                    IconButton(
                        onClick = viewModel::onBackClicked,
                        content = {
                            Icon(
                                imageVector = Icons.AutoMirrored.Default.ArrowBack,
                                contentDescription = "Localized description"
                            )
                        }
                    )
                }
            )
        },
        content = {
            ResultScreenState(
                state = state,
                modifier = Modifier.padding(it)
            )
        }
    )
}

@Composable
private fun ResultScreenState(
    state: ResultState,
    modifier: Modifier = Modifier
) {
    when (state) {
        is ResultState.Success -> ResultSuccessState(
            modifier = modifier,
            points = state.points
        )

        is ResultState.Loading -> ResultLoadingState(modifier = modifier)
        is ResultState.Error -> ResultErrorState(modifier = modifier)
    }
}