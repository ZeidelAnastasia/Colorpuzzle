package com.example.colorpuzzle.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.colorpuzzle.model.Tile
import com.example.colorpuzzle.viewmodel.GameViewModel

@Composable
fun GameScreen(vm: GameViewModel) {

    var showWinDialog by remember {
        mutableStateOf(false)
    }

    LaunchedEffect(vm.tiles) {

        if (vm.isSolved()) {
            showWinDialog = true
        }
    }

    Column(
        modifier = Modifier.fillMaxSize()
    ) {

        Text(
            text = "Готово: ${vm.progress()}%",
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(24.dp)
        )
        BoxWithConstraints(
            modifier = Modifier.weight(1f)
        ) {

            val boardSize =
                minOf(maxWidth, maxHeight)

            LazyVerticalGrid(
                columns = GridCells.Fixed(7),
                horizontalArrangement = Arrangement.spacedBy(0.dp),
                verticalArrangement = Arrangement.spacedBy(0.dp),
                modifier = Modifier
                    .size(boardSize)
                    .align(Alignment.Center)
            ) {

                items(vm.tiles) { tile ->

                    TileView(
                        tile = tile,
                        onClick = {
                            vm.selectTile(tile)
                        }
                    )
                }
            }
        }

        Button(
            onClick = {
                vm.restart()
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(64.dp)
                .padding(8.dp)
        ) {
            Text("Новый уровень")
        }
    }

    if (showWinDialog) {

        AlertDialog(
            onDismissRequest = {
                showWinDialog = false
            },

            title = {
                Text("Победа")
            },

            text = {
                Text("Градиент собран правильно!")
            },

            confirmButton = {
                Button(
                    onClick = {
                        showWinDialog = false
                    }
                ) {
                    Text("OK")
                }
            }
        )
    }
}

@Composable
private fun TileView(
    tile: Tile,
    onClick: () -> Unit
) {

    Box(
        modifier = Modifier
            .aspectRatio(1f)
            .clickable(
                enabled = !tile.locked
            ) {
                onClick()
            }
            .drawWithCache {

                val lightColor = Color.White.copy(alpha = 0.12f)
                val darkColor = Color.Black.copy(alpha = 0.12f)

                val diagonalGradient = Brush.linearGradient(
                    colors = listOf(
                        darkColor,
                        Color.Transparent,
                        lightColor
                    ),
                    start = Offset(
                        0f,
                        size.height
                    ),
                    end = Offset(
                        size.width,
                        0f
                    )
                )

                onDrawBehind {

                    drawRect(
                        color = tile.color
                    )

                    drawRect(
                        brush = diagonalGradient
                    )

                    drawLine(
                        color = Color.White.copy(alpha = 0.10f),
                        start = Offset(size.width, 0f),
                        end = Offset(0f, 0f),
                        strokeWidth = 2f
                    )

                    drawLine(
                        color = Color.White.copy(alpha = 0.08f),
                        start = Offset(size.width, 0f),
                        end = Offset(size.width, size.height),
                        strokeWidth = 2f
                    )

                    drawLine(
                        color = Color.Black.copy(alpha = 0.08f),
                        start = Offset(0f, size.height),
                        end = Offset(size.width, size.height),
                        strokeWidth = 2f
                    )

                    drawLine(
                        color = Color.Black.copy(alpha = 0.10f),
                        start = Offset(0f, 0f),
                        end = Offset(0f, size.height),
                        strokeWidth = 2f
                    )
                }
            }
    ) {

        if (tile.locked) {

            Box(
                modifier = Modifier
                    .size(7.dp)
                    .align(Alignment.Center)
                    .background(
                        color = Color.Black.copy(alpha = 0.75f),
                        shape = CircleShape
                    )
            )
        }
    }
}