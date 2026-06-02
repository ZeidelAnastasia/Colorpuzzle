package com.example.colorpuzzle

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.colorpuzzle.ui.GameScreen
import com.example.colorpuzzle.viewmodel.GameViewModel

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {

            val vm: GameViewModel = viewModel()

            GameScreen(vm)
        }
    }
}