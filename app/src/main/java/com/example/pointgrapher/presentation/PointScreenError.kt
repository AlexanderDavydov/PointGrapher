package com.example.pointgrapher.presentation

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.keyframes
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.pointgrapher.presentation.PointScreenState.ErrorTypeViewData

@Composable
internal fun PointScreenError(
    errorTypeViewData: ErrorTypeViewData,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center, content = {
            Column(
                modifier = Modifier.align(Alignment.Center),
                horizontalAlignment = Alignment.CenterHorizontally,
                content = {
                    Text(text = "😢", style = TextStyle(fontSize = 52.sp))
                    Spacer(Modifier.height(16.dp))



                    val prefix = "Error ocured while reqiest points: "
                    val cause = when (errorTypeViewData) {
                        ErrorTypeViewData.None -> ""
                        ErrorTypeViewData.EmptyNumber -> "Request points number can't be empty"
                        ErrorTypeViewData.NegativeNumber -> "Request points number must be positive"
                        ErrorTypeViewData.RequstError -> "Something happened during request"
                        ErrorTypeViewData.Uncpecified -> "Something went wrong"
                    }
                    Text(
                        text = buildAnnotatedString {
                            appendLine(prefix)
                            appendLine()
                            addStyle(
                                style = SpanStyle(fontSize = 16.sp),
                                start = 0,
                                end = prefix.length
                            )
                            appendLine(cause)

                        },
                        style = TextStyle(fontSize = 18.sp),
                        textAlign = TextAlign.Center
                    )
                }
            )
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.BottomCenter),
                contentAlignment = Alignment.Center,
                content = {
                    Text(text = "please try again")
                    BounceArrowIcon(
                        Modifier
                            .align(Alignment.CenterEnd)
                            .padding(horizontal = 24.dp)
                    )
                }
            )
        }
    )
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