package com.example.pointgrapher.presentation.screen.main.ui

import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.pointgrapher.R
import com.example.pointgrapher.presentation.screen.main.MainScreenState

@Composable
internal fun MainScreenGoButton(
    state: MainScreenState,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Button(
        modifier = modifier,
        onClick = onClick,
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