package com.lain.soft.claramobilechallenge.ui.activity

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.lain.soft.claramobilechallenge.ui.navigation.AppNavHost
import com.lain.soft.claramobilechallenge.ui.theme.ClaraMobileChallengeTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ClaraMobileChallengeTheme {
                AppNavHost()
            }
        }
    }
}
