package com.example.pomodoro.viewmodel

import android.app.Application
import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.pomodoro.data.PomodoroState
import com.example.pomodoro.data.PomodoroState.Companion.FOCUS_DURATION_MILLIS
import com.example.pomodoro.data.PomodoroState.Companion.LONG_BREAK_DURATION_MILLIS
import com.example.pomodoro.data.PomodoroState.Companion.SESSIONS_BEFORE_LONG_BREAK
import com.example.pomodoro.data.PomodoroState.Companion.SHORT_BREAK_DURATION_MILLIS
import com.example.pomodoro.data.PreferencesManager
import com.example.pomodoro.data.TimerMode
import com.example.pomodoro.data.TimerPhase
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class PomodoroViewModel(application: Application) : AndroidViewModel(application) {
    
    private val _state = MutableStateFlow(PomodoroState())
    val state: StateFlow<PomodoroState> = _state.asStateFlow()
    
    private val preferencesManager = PreferencesManager(application)
    private val vibrator: Vibrator
    
    private var timerJob: Job? = null
    
    init {
        // Initialize vibrator
        vibrator = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val vibratorManager = application.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as VibratorManager
            vibratorManager.defaultVibrator
        } else {
            @Suppress("DEPRECATION")
            application.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        }
        
        // Load saved preferences
        viewModelScope.launch {
            val savedBgIndex = preferencesManager.backgroundIndex.first()
            val savedMode = preferencesManager.lastMode.first()
            val savedCustomBgUri = preferencesManager.customBackgroundUri.first()
            val savedPowerSave = preferencesManager.powerSaveMode.first()
            _state.value = _state.value.copy(
                selectedBackgroundIndex = savedBgIndex,
                mode = TimerMode.entries.getOrElse(savedMode) { TimerMode.POMODORO },
                customBackgroundUri = savedCustomBgUri,
                powerSaveMode = savedPowerSave
            )
        }
    }
    
    /**
     * Start or resume the timer
     */
    fun startTimer() {
        if (_state.value.isRunning) return
        
        _state.value = _state.value.copy(isRunning = true)
        
        timerJob = viewModelScope.launch {
            while (_state.value.isRunning && _state.value.timeRemainingMillis > 0) {
                delay(1000L)
                _state.value = _state.value.copy(
                    timeRemainingMillis = _state.value.timeRemainingMillis - 1000L
                )
            }
            
            if (_state.value.timeRemainingMillis <= 0) {
                onPhaseComplete()
            }
        }
    }
    
    /**
     * Pause the timer
     */
    fun pauseTimer() {
        timerJob?.cancel()
        _state.value = _state.value.copy(isRunning = false)
    }
    
    /**
     * Toggle timer state (start/pause)
     */
    fun toggleTimer() {
        if (_state.value.isRunning) {
            pauseTimer()
        } else {
            startTimer()
        }
    }
    
    /**
     * Reset the timer to initial state
     */
    fun resetTimer() {
        timerJob?.cancel()
        
        val initialTime = if (_state.value.mode == TimerMode.DEEP_WORK) {
            PomodoroState.DEEP_WORK_DURATION_MILLIS
        } else {
            FOCUS_DURATION_MILLIS
        }
        
        _state.value = _state.value.copy(
            isRunning = false,
            phase = TimerPhase.FOCUS,
            timeRemainingMillis = initialTime,
            completedFocusSessions = 0
        )
    }
    
    /**
     * Skip to next phase
     */
    fun skipPhase() {
        timerJob?.cancel()
        onPhaseComplete()
    }
    
    /**
     * Switch between Pomodoro and Deep Work modes
     */
    fun switchMode(mode: TimerMode) {
        timerJob?.cancel()
        
        val initialTime = if (mode == TimerMode.DEEP_WORK) {
            PomodoroState.DEEP_WORK_DURATION_MILLIS
        } else {
            FOCUS_DURATION_MILLIS
        }
        
        _state.value = _state.value.copy(
            mode = mode,
            isRunning = false,
            phase = TimerPhase.FOCUS,
            timeRemainingMillis = initialTime,
            completedFocusSessions = 0
        )
        
        viewModelScope.launch {
            preferencesManager.saveLastMode(mode.ordinal)
        }
    }
    
    /**
     * Change background image
     */
    fun setBackgroundIndex(index: Int) {
        _state.value = _state.value.copy(
            selectedBackgroundIndex = index,
            customBackgroundUri = null
        )
        viewModelScope.launch {
            preferencesManager.saveBackgroundIndex(index)
            preferencesManager.saveCustomBackgroundUri(null)
        }
    }
    
    /**
     * Set custom background from gallery
     */
    fun setCustomBackground(uri: String) {
        _state.value = _state.value.copy(
            selectedBackgroundIndex = -1,
            customBackgroundUri = uri
        )
        viewModelScope.launch {
            preferencesManager.saveBackgroundIndex(-1)
            preferencesManager.saveCustomBackgroundUri(uri)
        }
    }
    
    /**
     * Toggle power save mode
     */
    fun togglePowerSaveMode() {
        val newValue = !_state.value.powerSaveMode
        _state.value = _state.value.copy(powerSaveMode = newValue)
        
        viewModelScope.launch {
            preferencesManager.savePowerSaveMode(newValue)
        }
    }
    
    /**
     * Handle phase completion
     */
    private fun onPhaseComplete() {
        vibrateShort()
        
        val currentState = _state.value
        val newState = when (currentState.phase) {
            TimerPhase.FOCUS -> {
                val newSessionCount = currentState.completedFocusSessions + 1
                if (newSessionCount >= SESSIONS_BEFORE_LONG_BREAK) {
                    currentState.copy(
                        phase = TimerPhase.LONG_BREAK,
                        timeRemainingMillis = LONG_BREAK_DURATION_MILLIS,
                        completedFocusSessions = 0,
                        isRunning = false
                    )
                } else {
                    currentState.copy(
                        phase = TimerPhase.SHORT_BREAK,
                        timeRemainingMillis = SHORT_BREAK_DURATION_MILLIS,
                        completedFocusSessions = newSessionCount,
                        isRunning = false
                    )
                }
            }
            TimerPhase.SHORT_BREAK, TimerPhase.LONG_BREAK -> {
                currentState.copy(
                    phase = TimerPhase.FOCUS,
                    timeRemainingMillis = FOCUS_DURATION_MILLIS,
                    isRunning = false
                )
            }
        }
        
        _state.value = newState
    }
    
    /**
     * Short vibration for phase completion
     */
    private fun vibrateShort() {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                vibrator.vibrate(
                    VibrationEffect.createWaveform(
                        longArrayOf(0, 200, 100, 200),
                        -1
                    )
                )
            } else {
                @Suppress("DEPRECATION")
                vibrator.vibrate(longArrayOf(0, 200, 100, 200), -1)
            }
        } catch (_: Exception) {
            // Ignore vibration errors
        }
    }
    
    override fun onCleared() {
        super.onCleared()
        timerJob?.cancel()
    }
}
