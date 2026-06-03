package com.example.colorpuzzle.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.colorpuzzle.ui.components.ProgressBar
import com.example.colorpuzzle.ui.components.TileView
import com.example.colorpuzzle.ui.components.WinDialog
import com.example.colorpuzzle.viewmodel.GameViewModel

@Composable
fun GameScreen(vm: GameViewModel) {
    var showWinDialog by remember { mutableStateOf(false) }

    LaunchedEffect(vm.tiles) {
        if (vm.isSolved()) {
            showWinDialog = true
        }
    }

    Column(
        modifier = Modifier.fillMaxSize().padding(top = 40.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        ProgressBar(progress = vm.progress())

        BoxWithConstraints(
            modifier = Modifier.weight(1f)
        ) {
            val boardSize = minOf(maxWidth, maxHeight)

            LazyVerticalGrid(
                columns = GridCells.Fixed(9),
                horizontalArrangement = Arrangement.spacedBy(0.dp),
                verticalArrangement = Arrangement.spacedBy(0.dp),
                modifier = Modifier
                    .size(boardSize)
                    .align(Alignment.Center)
            ) {
                items(vm.tiles) { tile ->
                    TileView(
                        tile = tile,
                        onClick = { vm.selectTile(tile) }
                    )
                }
            }
        }

        Button(
            onClick = { vm.restart() },
            modifier = Modifier
                .fillMaxWidth()
                .height(64.dp)
                .padding(8.dp)
        ) {
            Text("Новый уровень")
        }
    }

    if (showWinDialog) {
        WinDialog(
            onDismiss = { showWinDialog = false }
        )
    }
}