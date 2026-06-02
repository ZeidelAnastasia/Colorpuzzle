package com.example.colorpuzzle.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import com.example.colorpuzzle.model.Tile
import kotlin.random.Random

class GameViewModel : ViewModel() {

    companion object {
        const val GRID_SIZE = 7
    }

    var tiles by mutableStateOf(generateLevel())
        private set

    var selectedTile by mutableStateOf<Tile?>(null)

    fun selectTile(tile: Tile) {

        if (tile.locked) return

        if (selectedTile == null) {
            selectedTile = tile
            return
        }

        val first = selectedTile!!

        if (first.id != tile.id) {
            swapTiles(first, tile)
        }

        selectedTile = null
    }

    private fun swapTiles(first: Tile, second: Tile) {

        val temp = first.currentIndex

        first.currentIndex = second.currentIndex
        second.currentIndex = temp

        tiles = tiles.sortedBy { it.currentIndex }
    }

    private fun colorDistance(
        c1: FloatArray,
        c2: FloatArray
    ): Float {

        val hueDiff = minOf(
            kotlin.math.abs(c1[0] - c2[0]),
            360f - kotlin.math.abs(c1[0] - c2[0])
        )

        val satDiff =
            kotlin.math.abs(c1[1] - c2[1]) * 100f

        val valueDiff =
            kotlin.math.abs(c1[2] - c2[2]) * 150f

        return hueDiff + satDiff + valueDiff
    }

    fun restart() {
        tiles = generateLevel()
    }

    fun isSolved(): Boolean {

        return tiles.all {
            it.correctIndex == it.currentIndex
        }
    }

    fun progress(): Int {

        val correct = tiles.count {
            it.correctIndex == it.currentIndex
        }

        return correct * 100 / tiles.size
    }

    private fun generateLevel(): List<Tile> {

        val total = GRID_SIZE * GRID_SIZE

        val lockedPositions = mutableSetOf<Int>()

// углы
        lockedPositions.add(0)
        lockedPositions.add(GRID_SIZE - 1)
        lockedPositions.add(total - GRID_SIZE)
        lockedPositions.add(total - 1)

// 4-й ряд (индекс 3)
        for (col in 0 until GRID_SIZE) {
            lockedPositions.add(3 * GRID_SIZE + col)
        }

// 4-й столбец (индекс 3)
        for (row in 0 until GRID_SIZE) {
            lockedPositions.add(row * GRID_SIZE + 3)
        }

        var corners: List<FloatArray>

        do {

            corners = generateCorners()

        } while (

            colorDistance(corners[0], corners[1]) < 120f ||
            colorDistance(corners[0], corners[2]) < 120f ||
            colorDistance(corners[0], corners[3]) < 120f ||

            colorDistance(corners[1], corners[2]) < 120f ||
            colorDistance(corners[1], corners[3]) < 120f ||

            colorDistance(corners[2], corners[3]) < 120f
        )

        val topLeft = corners[0]
        val topRight = corners[1]
        val bottomLeft = corners[2]
        val bottomRight = corners[3]

        val colors = mutableListOf<Color>()

        for (row in 0 until GRID_SIZE) {

            for (col in 0 until GRID_SIZE) {

                val x = col.toFloat() / (GRID_SIZE - 1)
                val y = row.toFloat() / (GRID_SIZE - 1)

                val topHSV =
                    lerpHSV(
                        topLeft,
                        topRight,
                        x
                    )

                val bottomHSV =
                    lerpHSV(
                        bottomLeft,
                        bottomRight,
                        x
                    )

                val finalHSV =
                    lerpHSV(
                        topHSV,
                        bottomHSV,
                        y
                    )

                val adjustedValue = when {

                    finalHSV[2] < 0.3f ->
                        finalHSV[2] * 0.6f

                    finalHSV[2] > 0.7f ->
                        minOf(1f, finalHSV[2] * 1.25f)

                    else ->
                        finalHSV[2]
                }

                val adjustedSaturation = minOf(
                    1f,
                    finalHSV[1] * 1.15f
                )

                val color =
                    Color.hsv(
                        finalHSV[0],
                        adjustedSaturation,
                        adjustedValue
                    )

                colors.add(color)
            }
        }

        val movablePositions =
            (0 until total)
                .filter { it !in lockedPositions }
                .shuffled()

        var movableIndex = 0

        return colors.mapIndexed { index, color ->

            val locked = index in lockedPositions

            Tile(
                id = index,
                correctIndex = index,
                currentIndex =
                    if (locked)
                        index
                    else
                        movablePositions[movableIndex++],
                color = color,
                locked = locked
            )

        }.sortedBy { it.currentIndex }
    }

    private fun lerpHSV(
        start: FloatArray,
        end: FloatArray,
        t: Float
    ): FloatArray {

        var h1 = start[0]
        var h2 = end[0]

        if (kotlin.math.abs(h2 - h1) > 180f) {
            if (h1 > h2) {
                h2 += 360f
            } else {
                h1 += 360f
            }
        }

        val hue =
            (h1 + (h2 - h1) * t) % 360f

        val saturation =
            start[1] + (end[1] - start[1]) * t

        val value =
            start[2] + (end[2] - start[2]) * t

        return floatArrayOf(
            hue,
            saturation,
            value
        )
    }

    private fun generateCorners(): List<FloatArray> {

        val baseHue = Random.nextFloat() * 360f

        return listOf(

            // насыщенный
            floatArrayOf(
                baseHue,
                0.60f,
                0.95f
            ),

            // светлый
            floatArrayOf(
                (baseHue + 90f) % 360f,
                0.30f,
                1.0f
            ),

            // очень тёмный
            floatArrayOf(
                (baseHue + 180f) % 360f,
                0.50f,
                0.30f
            ),

            // средний
            floatArrayOf(
                (baseHue + 270f) % 360f,
                0.60f,
                0.55f
            )
        )
    }

    private fun Color.toHue(): Float {

        val r = red
        val g = green
        val b = blue

        val max = maxOf(r, g, b)
        val min = minOf(r, g, b)

        val delta = max - min

        if (delta == 0f) return 0f

        val hue = when (max) {
            r -> ((g - b) / delta) % 6f
            g -> ((b - r) / delta) + 2f
            else -> ((r - g) / delta) + 4f
        }

        return (hue * 60f + 360f) % 360f
    }
}