package com.example.colorpuzzle.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.colorpuzzle.model.Tile

@Composable
fun TileView(
    tile: Tile,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .aspectRatio(1f)
            .clickable(enabled = !tile.locked) { onClick() }
            .drawWithCache {
                val lightColor = Color.White.copy(alpha = 0.12f)
                val darkColor = Color.Black.copy(alpha = 0.12f)

                val diagonalGradient = Brush.linearGradient(
                    colors = listOf(
                        darkColor,
                        Color.Transparent,
                        lightColor
                    ),
                    start = Offset(0f, size.height),
                    end = Offset(size.width, 0f)
                )

                onDrawBehind {
                    drawRect(color = tile.color)
                    drawRect(brush = diagonalGradient)

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