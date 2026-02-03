package com.example.pomodoro

import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.pomodoro.ui.components.*
import com.example.pomodoro.ui.theme.PomodoroTheme
import com.example.pomodoro.viewmodel.PomodoroViewModel
import kotlin.math.abs

/**
 * Navigation screens
 */
enum class Screen {
    MODE_SELECTOR,
    TIMER,
    SETTINGS
}

class MainActivity : ComponentActivity() {
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        setContent {
            PomodoroTheme {
                PomodoroApp()
            }
        }
    }
}

@Composable
fun PomodoroApp(
    viewModel: PomodoroViewModel = viewModel()
) {
    val state by viewModel.state.collectAsState()
    var currentScreen by remember { mutableStateOf(Screen.MODE_SELECTOR) }
    var dragOffset by remember { mutableFloatStateOf(0f) }
    val context = LocalContext.current
    
    // Image picker launcher
    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument()
    ) { uri: Uri? ->
        uri?.let {
            context.contentResolver.takePersistableUriPermission(
                it,
                android.content.Intent.FLAG_GRANT_READ_URI_PERMISSION
            )
            viewModel.setCustomBackground(it.toString())
        }
    }
    
    // Get background based on selection
    val bgOption = if (state.selectedBackgroundIndex >= 0) {
        backgroundOptions.getOrElse(state.selectedBackgroundIndex) { backgroundOptions[0] }
    } else null
    
    Box(
        modifier = Modifier
            .fillMaxSize()
            .then(
                if (bgOption?.solidColor != null) {
                    Modifier.background(bgOption.solidColor)
                } else {
                    Modifier.background(Color(0xFF0D0D0D))
                }
            )
            .pointerInput(currentScreen) {
                detectHorizontalDragGestures(
                    onDragEnd = {
                        if (abs(dragOffset) > 50) {
                            when {
                                dragOffset < 0 && currentScreen == Screen.MODE_SELECTOR -> {
                                    currentScreen = Screen.TIMER
                                }
                                dragOffset > 0 && currentScreen == Screen.TIMER -> {
                                    currentScreen = Screen.MODE_SELECTOR
                                }
                                dragOffset > 0 && currentScreen == Screen.SETTINGS -> {
                                    currentScreen = Screen.MODE_SELECTOR
                                }
                            }
                        }
                        dragOffset = 0f
                    },
                    onHorizontalDrag = { _, dragAmount ->
                        dragOffset += dragAmount
                    }
                )
            }
    ) {
        // Background image - skip in power save mode
        if (!state.powerSaveMode) {
            if (state.selectedBackgroundIndex == -1 && state.customBackgroundUri != null) {
                AsyncImage(
                    model = ImageRequest.Builder(context)
                        .data(Uri.parse(state.customBackgroundUri))
                        .crossfade(true)
                        .build(),
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize(),
                    alpha = 0.4f
                )
            } else if (bgOption?.drawableRes != null) {
                Image(
                    painter = painterResource(id = bgOption.drawableRes),
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize(),
                    alpha = 0.4f
                )
            }
        }
        
        // Screen content
        when (currentScreen) {
            Screen.MODE_SELECTOR -> {
                ModeSelector(
                    currentMode = state.mode,
                    onModeSelected = { mode ->
                        viewModel.switchMode(mode)
                        currentScreen = Screen.TIMER
                    },
                    onSettingsClick = {
                        currentScreen = Screen.SETTINGS
                    }
                )
            }
            
            Screen.TIMER -> {
                TimerDisplay(
                    state = state,
                    onToggle = { viewModel.toggleTimer() },
                    onReset = { 
                        viewModel.resetTimer()
                        currentScreen = Screen.MODE_SELECTOR
                    }
                )
            }
            
            Screen.SETTINGS -> {
                SettingsScreen(
                    selectedBackgroundIndex = state.selectedBackgroundIndex,
                    customBackgroundUri = state.customBackgroundUri,
                    powerSaveMode = state.powerSaveMode,
                    onBackgroundSelected = { index ->
                        viewModel.setBackgroundIndex(index)
                    },
                    onPickCustomBackground = {
                        imagePickerLauncher.launch(arrayOf("image/*"))
                    },
                    onTogglePowerSave = {
                        viewModel.togglePowerSaveMode()
                    },
                    onBack = {
                        currentScreen = Screen.MODE_SELECTOR
                    }
                )
            }
        }
    }
}

