package com.example.pomodoro.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.wear.compose.material.*
import com.example.pomodoro.R
import com.example.pomodoro.data.PomodoroState
import com.example.pomodoro.data.TimerMode
import com.example.pomodoro.data.TimerPhase
import com.example.pomodoro.ui.theme.StatusColors

/**
 * Main timer display with circular progress
 */
@Composable
fun TimerDisplay(
    state: PomodoroState,
    onToggle: () -> Unit,
    onReset: () -> Unit,
    modifier: Modifier = Modifier
) {
    val statusColor = when {
        state.powerSaveMode -> Color.White.copy(alpha = 0.5f)  // Dimmed in power save
        !state.isRunning -> StatusColors.Paused
        state.mode == TimerMode.DEEP_WORK -> StatusColors.DeepWork
        state.phase == TimerPhase.FOCUS -> StatusColors.Focus
        state.phase == TimerPhase.SHORT_BREAK -> StatusColors.ShortBreak
        state.phase == TimerPhase.LONG_BREAK -> StatusColors.LongBreak
        else -> StatusColors.Focus
    }
    
    Box(
        modifier = modifier
            .fillMaxSize()
            .then(
                if (state.powerSaveMode) {
                    Modifier.background(Color.Black)  // Pure black for AMOLED
                } else {
                    Modifier.background(
                        Brush.radialGradient(
                            colors = listOf(
                                statusColor.copy(alpha = 0.15f),
                                Color.Transparent
                            )
                        )
                    )
                }
            ),
        contentAlignment = Alignment.Center
    ) {
        // Circular progress indicator - HIDDEN in power save mode
        if (!state.powerSaveMode) {
            CircularProgressIndicator(
                progress = state.progressFraction,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(8.dp),
                startAngle = 270f,
                endAngle = 270f,
                indicatorColor = statusColor,
                trackColor = statusColor.copy(alpha = 0.2f),
                strokeWidth = 6.dp
            )
        }
        
        // Center content
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Phase/Mode indicator
            Text(
                text = state.phaseDisplayName,
                fontSize = 14.sp,
                color = statusColor,
                fontWeight = FontWeight.Medium,
                textAlign = TextAlign.Center
            )
            
            Spacer(modifier = Modifier.height(4.dp))
            
            // Timer display
            Text(
                text = state.timeRemainingFormatted,
                fontSize = 40.sp,
                color = Color.White,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            // Session counter (only in Pomodoro mode and NOT power save)
            if (state.mode == TimerMode.POMODORO && !state.powerSaveMode) {
                Text(
                    text = "Session ${state.completedFocusSessions + 1}/4",
                    fontSize = 11.sp,
                    color = Color.White.copy(alpha = 0.6f),
                    textAlign = TextAlign.Center
                )
                
                Spacer(modifier = Modifier.height(8.dp))
            }
            
            // Control buttons
            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Reset button
                CompactButton(
                    onClick = onReset,
                    colors = ButtonDefaults.secondaryButtonColors(
                        backgroundColor = Color.White.copy(alpha = 0.1f)
                    )
                ) {
                    Icon(
                        painter = painterResource(R.drawable.ic_refresh),
                        contentDescription = "Reset",
                        modifier = Modifier.size(20.dp),
                        tint = Color.White.copy(alpha = 0.8f)
                    )
                }
                
                // Play/Pause button
                Button(
                    onClick = onToggle,
                    colors = ButtonDefaults.buttonColors(
                        backgroundColor = statusColor
                    ),
                    modifier = Modifier.size(48.dp)
                ) {
                    Icon(
                        painter = painterResource(
                            if (state.isRunning) R.drawable.ic_pause else R.drawable.ic_play
                        ),
                        contentDescription = if (state.isRunning) "Pause" else "Start",
                        modifier = Modifier.size(24.dp),
                        tint = Color.White
                    )
                }
            }
        }
    }
}
