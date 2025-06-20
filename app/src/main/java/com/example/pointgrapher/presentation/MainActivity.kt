package com.example.pointgrapher.presentation

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.pointgrapher.presentation.points.ui.PointMainScreen
import com.example.pointgrapher.presentation.theme.PointGrapherTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            PointGrapherTheme {
                PointMainScreen()
            }
        }
    }
}