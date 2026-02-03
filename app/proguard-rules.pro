# Pomodoro Deep Work App - Optimized ProGuard Rules

# === AGGRESSIVE OPTIMIZATION ===
-optimizationpasses 5
-allowaccessmodification
-repackageclasses ''
-dontpreverify

# === REMOVE LOGGING IN RELEASE ===
-assumenosideeffects class android.util.Log {
    public static int v(...);
    public static int d(...);
    public static int i(...);
}

# === KEEP ONLY WHAT'S NEEDED ===

# Keep ViewModel (required for lifecycle)
-keep class * extends androidx.lifecycle.ViewModel { <init>(...); }

# Keep data classes (Kotlin serialization)
-keepclassmembers class com.example.pomodoro.data.** {
    public <fields>;
    public <methods>;
}

# Keep enums
-keepclassmembers enum * {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}

# ExoPlayer - minimal keeps
-keep class androidx.media3.exoplayer.** { *; }
-dontwarn androidx.media3.**

# Compose - let R8 optimize aggressively
-dontwarn androidx.compose.**

# Media session
-keep class android.media.session.MediaController { *; }
-keep class android.media.session.MediaSession { *; }

# DataStore
-keepclassmembers class * extends com.google.protobuf.GeneratedMessageLite { *; }

# Kotlin coroutines
-dontwarn kotlinx.coroutines.**
