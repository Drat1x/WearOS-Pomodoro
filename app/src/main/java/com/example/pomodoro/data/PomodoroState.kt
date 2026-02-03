package com.example.pomodoro.data

/**
 * Represents the current phase of the Pomodoro timer
 */
enum class TimerPhase {
    FOCUS,          // 25 minutes work session
    SHORT_BREAK,    // 5 minutes break
    LONG_BREAK      // 15 minutes break (after 4 focus sessions)
}

/**
 * Represents the timer mode
 */
enum class TimerMode {
    POMODORO,       // Standard Pomodoro with work/break cycles
    DEEP_WORK       // Continuous focus mode (same behavior, different UI)
}

/**
 * Timer state for the Pomodoro app
 */
data class PomodoroState(
    val mode: TimerMode = TimerMode.POMODORO,
    val phase: TimerPhase = TimerPhase.FOCUS,
    val isRunning: Boolean = false,
    val timeRemainingMillis: Long = FOCUS_DURATION_MILLIS,
    val completedFocusSessions: Int = 0,
    val selectedBackgroundIndex: Int = 0,
    val customBackgroundUri: String? = null,
    val powerSaveMode: Boolean = false
) {
    companion object {
        const val FOCUS_DURATION_MILLIS = 25 * 60 * 1000L        // 25 minutes
        const val DEEP_WORK_DURATION_MILLIS = 60 * 60 * 1000L    // 1 hour
        const val SHORT_BREAK_DURATION_MILLIS = 5 * 60 * 1000L   // 5 minutes
        const val LONG_BREAK_DURATION_MILLIS = 15 * 60 * 1000L   // 15 minutes
        const val SESSIONS_BEFORE_LONG_BREAK = 4
    }
    
    val timeRemainingFormatted: String
        get() {
            val totalSeconds = timeRemainingMillis / 1000
            val minutes = totalSeconds / 60
            val seconds = totalSeconds % 60
            return "%02d:%02d".format(minutes, seconds)
        }
    
    val phaseDisplayName: String
        get() = when {
            mode == TimerMode.DEEP_WORK && phase == TimerPhase.FOCUS -> "Deep Work"
            phase == TimerPhase.FOCUS -> "Focus"
            phase == TimerPhase.SHORT_BREAK -> "Break"
            phase == TimerPhase.LONG_BREAK -> "Long Break"
            else -> "Focus"
        }
    
    val progressFraction: Float
        get() {
            val totalDuration = when {
                mode == TimerMode.DEEP_WORK && phase == TimerPhase.FOCUS -> DEEP_WORK_DURATION_MILLIS
                phase == TimerPhase.FOCUS -> FOCUS_DURATION_MILLIS
                phase == TimerPhase.SHORT_BREAK -> SHORT_BREAK_DURATION_MILLIS
                phase == TimerPhase.LONG_BREAK -> LONG_BREAK_DURATION_MILLIS
                else -> FOCUS_DURATION_MILLIS
            }
            return 1f - (timeRemainingMillis.toFloat() / totalDuration.toFloat())
        }
}
