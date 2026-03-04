package com.lain.soft.claramobilechallenge.ui.activity

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.lain.soft.claramobilechallenge.ui.screen.MainScreen
import com.lain.soft.claramobilechallenge.ui.theme.ClaraMobileChallengeTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ClaraMobileChallengeTheme {
                MainScreen()
            }
        }
    }
}
