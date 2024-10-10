package org.example.project

import AndroidStepCounter
import App
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import steps.domain.view.AppContext

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            App()
        }
    }
    override fun onPause() {
        super.onPause()
        val stepcounter = AndroidStepCounter(applicationContext)
        stepcounter.stopCounting()
    }
}

@Preview
@Composable
fun AppAndroidPreview() {
    App()
}