package com.example.colorpuzzle.model

import androidx.compose.ui.graphics.Color
import kotlin.random.Random

class ColorGenerator {

    companion object {
        private const val GRID_SIZE = 7
    }

    fun generateColors(): List<Color> {
        // Генерируем два основных цвета
        val hueLeft = Random.nextFloat() * 360f
        var hueRight = (hueLeft + 120f + Random.nextFloat() * 120f) % 360f

        // Убеждаемся, что цвета достаточно отличаются
        var hueDiff = kotlin.math.abs(hueRight - hueLeft)
        if (hueDiff < 60f) {
            hueRight = (hueRight + 120f) % 360f
        }

        // Левые углы (один цвет, разная яркость)
        val topLeft = Color.hsv(hueLeft, 0.30f, 0.95f)    // светлый
        val bottomLeft = Color.hsv(hueLeft, 0.70f, 0.45f)  // тёмный

        // Правые углы (другой цвет, разная яркость)
        val topRight = Color.hsv(hueRight, 0.30f, 0.95f)   // светлый
        val bottomRight = Color.hsv(hueRight, 0.70f, 0.45f) // тёмный

        val colors = mutableListOf<Color>()

        for (row in 0 until GRID_SIZE) {
            for (col in 0 until GRID_SIZE) {
                val x = col.toFloat() / (GRID_SIZE - 1)  // 0 = левый край, 1 = правый край
                val y = row.toFloat() / (GRID_SIZE - 1)  // 0 = верх, 1 = низ

                // Интерполяция в RGB пространстве (чистые переходы)
                val topColor = lerpRGB(topLeft, topRight, x)
                val bottomColor = lerpRGB(bottomLeft, bottomRight, x)
                val finalColor = lerpRGB(topColor, bottomColor, y)

                colors.add(finalColor)
            }
        }

        return colors
    }

    // Линейная интерполяция между цветами в RGB пространстве
    private fun lerpRGB(start: Color, end: Color, t: Float): Color {
        val r = start.red + (end.red - start.red) * t
        val g = start.green + (end.green - start.green) * t
        val b = start.blue + (end.blue - start.blue) * t
        return Color(r, g, b)
    }
}