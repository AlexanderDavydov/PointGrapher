package com.example.pointgrapher.presentation.main.ui

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.pointgrapher.R
import com.example.pointgrapher.presentation.main.MainScreenState

@Composable
internal fun MainScreenErrorSection(
    state: MainScreenState,
    modifier: Modifier = Modifier
) {
    AnimatedContent(
        targetState = state.isError,
        modifier = modifier,
    ) {
        if (it) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                content = {
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        modifier = Modifier.padding(horizontal = 24.dp),
                        text = stringResource(R.string.main_screen_error_occurred),
                        style = MaterialTheme.typography.titleMedium,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(text = stringResource(R.string.main_screen_try_again))
                    Spacer(modifier = Modifier.height(8.dp))
                    BounceArrowIcon()
                    Spacer(modifier = Modifier.height(16.dp))
                }
            )
        }
    }
}