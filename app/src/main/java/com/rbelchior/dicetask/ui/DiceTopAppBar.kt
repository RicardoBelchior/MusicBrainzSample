package com.rbelchior.dicetask.ui

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.*
import androidx.compose.runtime.Composable

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DiceTopAppBar(
    onIconClick: () -> Unit,
    onNavigationClick: () -> Unit,
) {
    CenterAlignedTopAppBar(
        title = { Text("Dice Task") },
        navigationIcon = {
            IconButton(onIconClick) {
                Icon(
                    imageVector = Icons.Filled.Menu,
                    contentDescription = "Navigation icon"
                )
            }
        },
        actions = {
            IconButton(onNavigationClick) {
                Icon(
                    imageVector = Icons.Filled.Info,
                    contentDescription = "Information icon"
                )
            }
        }
    )
}