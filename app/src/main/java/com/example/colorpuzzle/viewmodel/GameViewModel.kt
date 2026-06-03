package com.example.colorpuzzle.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.example.colorpuzzle.model.ColorGenerator
import com.example.colorpuzzle.model.Tile
import kotlin.random.Random

class GameViewModel : ViewModel() {

    companion object {
        const val GRID_SIZE = 9
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

    fun restart() {
        tiles = generateLevel()
        selectedTile = null
    }

    fun isSolved(): Boolean {
        return tiles.all { it.correctIndex == it.currentIndex }
    }

    fun progress(): Int {
        val correct = tiles.count { it.correctIndex == it.currentIndex }
        return correct * 100 / tiles.size
    }

    private fun generateLevel(): List<Tile> {
        val total = GRID_SIZE * GRID_SIZE
        val lockedPositions = generateLockedPositions()
        val colors = ColorGenerator().generateColors()

        val movablePositions = (0 until total)
            .filter { it !in lockedPositions }
            .shuffled()

        var movableIndex = 0

        return colors.mapIndexed { index, color ->
            val locked = index in lockedPositions
            Tile(
                id = index,
                correctIndex = index,
                currentIndex = if (locked) index else movablePositions[movableIndex++],
                color = color,
                locked = locked
            )
        }.sortedBy { it.currentIndex }
    }

    private fun generateLockedPositions(): Set<Int> {
        val total = GRID_SIZE * GRID_SIZE
        val lockedPositions = mutableSetOf<Int>()

        // Углы
        lockedPositions.add(0)
        lockedPositions.add(GRID_SIZE - 1)
        lockedPositions.add(total - GRID_SIZE)
        lockedPositions.add(total - 1)

        // 4-й ряд
//        for (col in 0 until GRID_SIZE) {
//            lockedPositions.add(3 * GRID_SIZE + col)
//        }

        // 4-й столбец
//        for (row in 0 until GRID_SIZE) {
//            lockedPositions.add(row * GRID_SIZE + 3)
//        }

        return lockedPositions
    }
}