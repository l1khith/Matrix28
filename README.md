# Matrix28 — 28-Day Fixed Calendar & Productivity App

**Matrix28** is a modern Kotlin Multiplatform (KMP) and Compose Multiplatform application designed around the 28-day fixed calendar model. It combines calendar planning, task management, Eisenhower Matrix prioritization, habit tracking, and home screen widgets into a unified offline-first experience for Android and iOS.

---

## 🌟 Key Features

### 📅 Fixed 28-Day Calendar
- **13 Months × 28 Days**: Structured 52-week calendar grid where every month starts on the same day.
- **Calendar Sync**: Synchronize tasks and events with native device calendars.
- **Year Day Handling**: Dedicated accounting for the 365th annual day.

### 📋 Task & Eisenhower Matrix Management
- **Prioritization**: Categorize tasks using Eisenhower Matrix quadrants (Urgent/Important).
- **Reminders & Alarms**: Schedule exact notifications using Android `AlarmManager`.
- **Recurring Tasks**: Daily, weekly, monthly, or custom interval task repetition.

### 🔥 Habit Tracker
- **Daily & Weekly Habits**: Track streak progress, completion history, and target frequencies.
- **Habit Reminders**: Custom notification schedules per habit.

### 📱 Android Home Screen Widget
- **Glance AppWidget**: View and check off today's tasks directly from the home screen (`TodayTaskWidget`).

### 🔒 Security & Customization
- **Biometric Security**: Protect sensitive tasks with biometric authentication.
- **Pro Tier & Testing Mode**: In-app purchase integration with RevenueCat, plus a dev-mode toggle for closed testing.
- **Custom Theme Engine**: Modern dark/light UI design system built with Compose Multiplatform.

---

## 🏗 Architecture & Project Structure

```
Matrix28/
├── androidApp/                               # Android application module
│   └── src/main/
│       ├── kotlin/com/l1khith/matrix28/     # MainActivity, MatrixApplication, Glance Widget
│       └── AndroidManifest.xml
├── shared/                                   # Kotlin Multiplatform shared module
│   └── src/
│       ├── commonMain/kotlin/com/l1khith/matrix28/
│       │   ├── billing/                      # Subscription & RevenueCat manager
│       │   ├── data/                         # Room Database & Entities (Tasks, Habits)
│       │   ├── repository/                   # User Preferences (DataStore) & Repositories
│       │   ├── ui/                           # Compose Multiplatform UI components & screens
│       │   ├── utils/                        # Fixed calendar math & platform helpers
│       │   └── viewmodel/                    # Shared ViewModels
│       ├── androidMain/                       # Android-specific implementations (Alarms, Boot)
│       └── iosMain/                           # iOS-specific implementations
```

---

## 🛠 Tech Stack

- **Language**: Kotlin 2.0+
- **UI Framework**: Compose Multiplatform (Material 3)
- **Architecture**: MVI / MVVM with Shared ViewModels
- **Database**: Room Database (KMP Bundled SQLite)
- **Preferences**: Jetpack DataStore Preferences
- **Widgets**: Jetpack Glance (Android Home Screen Widget)
- **Subscriptions**: RevenueCat KMP SDK
- **Concurrency**: Kotlinx Coroutines & Flow

---

## 🚀 Getting Started

### Prerequisites
- **Android Studio**: Ladybug (2024.2.1+) or higher
- **JDK**: OpenJDK 17 or higher
- **Android SDK**: `compileSdk = 35`, `minSdk = 26`
- **Xcode**: 15+ (for iOS builds)

### Building the Project

#### Android
```bash
# Debug build
./gradlew :androidApp:assembleDebug

# Run unit tests
./gradlew :shared:testDebugUnitTest
```

#### iOS
Open `iosApp` (or the XCode workspace generated for the shared KMP module) in Xcode or build via Gradle:
```bash
./gradlew :shared:embedAndSignAppleFrameworkForXcode
```

---

## 🧪 Dev Mode / Closed Testing Toggle

For closed testing without active Play Store billing:
1. Go to the **Profile** screen.
2. Rapidly tap the **Subscription Status** card **5 times**.
3. Pro mode toggles on/off locally with a feedback banner.

---

## 📄 License

Copyright © 2026 Matrix28 Team. All rights reserved.