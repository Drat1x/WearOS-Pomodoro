package com.example.pomodoro.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.wear.compose.foundation.lazy.ScalingLazyColumn
import androidx.wear.compose.material.*
import com.example.pomodoro.R
import com.example.pomodoro.data.TimerMode
import com.example.pomodoro.ui.theme.StatusColors

/**
 * Mode selection screen
 */
@Composable
fun ModeSelector(
    currentMode: TimerMode,
    onModeSelected: (TimerMode) -> Unit,
    onSettingsClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    ScalingLazyColumn(
        modifier = modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        contentPadding = PaddingValues(
            top = 32.dp,
            bottom = 32.dp
        )
    ) {
        item {
            Text(
                text = "Select Mode",
                fontSize = 16.sp,
                color = Color.White,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(bottom = 16.dp)
            )
        }
        
        item {
            ModeChip(
                label = "Pomodoro",
                description = "25/5 min cycles",
                isSelected = currentMode == TimerMode.POMODORO,
                color = StatusColors.Focus,
                onClick = { onModeSelected(TimerMode.POMODORO) }
            )
        }
        
        item {
            Spacer(modifier = Modifier.height(8.dp))
        }
        
        item {
            ModeChip(
                label = "Deep Work",
                description = "Focus mode",
                isSelected = currentMode == TimerMode.DEEP_WORK,
                color = StatusColors.DeepWork,
                onClick = { onModeSelected(TimerMode.DEEP_WORK) }
            )
        }
        
        item {
            Spacer(modifier = Modifier.height(16.dp))
        }
        
        item {
            CompactChip(
                onClick = onSettingsClick,
                label = {
                    Text(
                        text = "Settings",
                        fontSize = 12.sp
                    )
                },
                icon = {
                    Icon(
                        painter = painterResource(R.drawable.ic_settings),
                        contentDescription = "Settings",
                        modifier = Modifier.size(16.dp)
                    )
                },
                colors = ChipDefaults.secondaryChipColors()
            )
        }
    }
}

@Composable
private fun ModeChip(
    label: String,
    description: String,
    isSelected: Boolean,
    color: Color,
    onClick: () -> Unit
) {
    Chip(
        onClick = onClick,
        label = {
            Text(
                text = label,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold
            )
        },
        secondaryLabel = {
            Text(
                text = description,
                fontSize = 11.sp,
                color = Color.White.copy(alpha = 0.7f)
            )
        },
        colors = ChipDefaults.chipColors(
            backgroundColor = if (isSelected) color else color.copy(alpha = 0.3f)
        ),
        modifier = Modifier.fillMaxWidth(0.9f)
    )
}
