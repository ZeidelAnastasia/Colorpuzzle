package com.example.colorpuzzle.model

import androidx.compose.ui.graphics.Color

data class Tile(
    val id: Int,
    val correctIndex: Int,
    var currentIndex: Int,
    val color: Color,
    val locked: Boolean = false
)