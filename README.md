# ⏰ Deep Focus - Wear OS Pomodoro & Deep Work Timer

A minimal, offline Pomodoro & Deep Work timer app for **Samsung Galaxy Watch 6 Classic** and other Wear OS devices.

![Wear OS](https://img.shields.io/badge/Wear%20OS-4.0+-4285F4?style=flat&logo=wearos&logoColor=white)
![Kotlin](https://img.shields.io/badge/Kotlin-1.9+-7F52FF?style=flat&logo=kotlin&logoColor=white)
![Jetpack Compose](https://img.shields.io/badge/Jetpack%20Compose-1.5+-4285F4?style=flat&logo=jetpackcompose&logoColor=white)

## ✨ Features

### ⏱ Pomodoro Timer
- **25 minutes** focused work sessions
- **5 minutes** short breaks
- **15 minutes** long break (after 4 sessions)
- Vibration alert on phase completion
- Session counter (1/4, 2/4, ...)

### 🧠 Deep Work Mode
- **60 minutes** (1 hour) continuous focus
- Minimal UI for maximum concentration
- Same break logic as Pomodoro

### 🔋 Power Save Mode
- Pure black AMOLED background
- No background images loaded
- Minimal UI elements
- Maximum battery efficiency

### 🎨 Customization
- **3 solid color backgrounds**: Dark, Purple, Blue
- **4 preset image backgrounds**
- **Custom background** from gallery
- Swipe gestures for navigation

### 📱 UI Features
- Dark theme optimized for focus
- Large, readable timer display
- Circular progress indicator
- Swipe left/right navigation

## 🛠 Technical Specs

| Spec | Value |
|------|-------|
| Platform | Wear OS |
| Language | Kotlin |
| UI Framework | Jetpack Compose for Wear OS |
| compileSdk | 34 |
| targetSdk | 34 |
| minSdk | 30 |
| Internet Required | ❌ No |
| Permissions | Vibration only |

## 📁 Project Structure

```
app/src/main/
├── java/com/example/pomodoro/
│   ├── MainActivity.kt           # Main Compose Activity
│   ├── data/
│   │   ├── PomodoroState.kt      # Timer state & durations
│   │   └── PreferencesManager.kt # DataStore preferences
│   ├── ui/
│   │   ├── components/
│   │   │   ├── TimerDisplay.kt   # Circular timer UI
│   │   │   ├── ModeSelector.kt   # Mode selection screen
│   │   │   └── SettingsScreen.kt # Background & power settings
│   │   └── theme/
│   │       └── Theme.kt          # Colors & theme
│   └── viewmodel/
│       └── PomodoroViewModel.kt  # Timer logic & state
└── res/
    ├── drawable/                 # Icons & backgrounds
    ├── values/                   # Colors, strings, themes
    └── mipmap-anydpi-v26/        # App icon
```

## 🚀 Installation

### Prerequisites: Install ADB Platform Tools

Download and install ADB Platform Tools for your OS:

| OS | Download Link |
|----|---------------|
| Windows | [platform-tools-latest-windows.zip](https://dl.google.com/android/repository/platform-tools-latest-windows.zip) |
| macOS | [platform-tools-latest-darwin.zip](https://dl.google.com/android/repository/platform-tools-latest-darwin.zip) |
| Linux | [platform-tools-latest-linux.zip](https://dl.google.com/android/repository/platform-tools-latest-linux.zip) |

**Windows Setup:**
1. Download and extract the zip
2. Add the folder path to System Environment Variables → PATH
3. Or open terminal in the extracted folder

**Verify Installation:**
```bash
adb version
```

---

### Method 1: Install APK via Wireless Debugging (Recommended)

#### Step 1: Enable Developer Options on Watch
1. Go to **Settings** → **About watch** → **Software**
2. Tap **Software version** 5 times
3. Developer mode is now enabled

#### Step 2: Enable Wireless Debugging
1. Go to **Settings** → **Developer options**
2. Enable **ADB debugging**
3. Enable **Wireless debugging**
4. Tap on **Wireless debugging** to see IP and Port

#### Step 3: Pair Watch with PC (First time only)
1. On watch: **Wireless debugging** → **Pair new device**
2. Note the **Pairing code** and **IP:Port**
3. On PC, run:
   ```bash
   adb pair <IP>:<PAIRING_PORT>
   ```
   Example:
   ```bash
   adb pair 192.168.1.100:12345
   ```
4. Enter the pairing code when prompted

#### Step 4: Connect to Watch
```bash
adb connect <IP>:<PORT>
```
Example:
```bash
adb connect 192.168.1.100:5555
```

#### Step 5: Install APK
```bash
adb install app-debug.apk
```
Or with full path:
```bash
adb install "C:\path\to\WearOS-Pomodoro\app-debug.apk"
```

#### Useful ADB Commands
```bash
# List connected devices
adb devices

# Disconnect from watch
adb disconnect

# Uninstall app
adb uninstall com.example.pomodoro

# Restart ADB server (if issues)
adb kill-server
adb start-server
```

### Method 2: Build from Source

#### Prerequisites
- Android Studio Hedgehog or newer
- Wear OS SDK
- Galaxy Watch 6 Classic (or Wear OS emulator)

#### Build Steps
1. Clone the repository:
   ```bash
   git clone https://github.com/Drat1x/WearOS-Pomodoro.git
   ```
2. Open in Android Studio
3. Sync Gradle
4. Connect your watch via USB or WiFi
5. Run → Select Device → Your Watch

## 📱 Usage

1. **Open the app** on your watch
2. **Select mode**: Pomodoro (25 min) or Deep Work (60 min)
3. **Tap Play** to start the timer
4. **Work focused!** Vibration alerts when phase ends
5. **Swipe right** to go back to mode selection

### Settings
- Swipe to Settings from mode selector
- Choose background color or image
- Enable Power Mode for battery saving
- Pick custom background from gallery

## 📄 License

MIT License - Feel free to use and modify!

---

Made with ❤️ for focus and productivity
