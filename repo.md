# MyBest UBSI

Android application for UBSI (Universitas Bina Sarana Informatika) students to manage their academic activities, receive real-time notifications for class schedules and assignments.

## Project Information

- **Package**: `com.ubsi.mybest`
- **Version**: 1.0.0 (versionCode: 1)
- **Min SDK**: 24 (Android 7.0)
- **Target SDK**: 34 (Android 14)
- **Compile SDK**: 34

## Technology Stack

### Language & Framework
- **Kotlin** 1.9.x
- **Android SDK** 34
- **Java Version**: 11

### Architecture & Libraries
- **View Binding**: Enabled for type-safe view access
- **AndroidX Core KTX**: 1.12.0
- **AppCompat**: 1.6.1
- **Material Design Components**: 1.10.0
- **ConstraintLayout**: 2.1.4
- **Activity KTX**: 1.8.1
- **Fragment KTX**: 1.6.2
- **Lifecycle Runtime KTX**: 2.6.2
- **ViewModel KTX**: 2.6.2

## Project Structure

```
app/
├── src/main/
│   ├── java/com/ubsi/mybest/
│   │   ├── ui/
│   │   │   ├── setup/         # Initial setup & permissions
│   │   │   ├── login/         # Login functionality
│   │   │   └── main/          # Main application screen
│   │   ├── util/              # Utility classes
│   │   │   └── PreferenceManager.kt
│   │   └── service/           # Background services (planned)
│   ├── res/
│   │   ├── layout/            # XML layout files
│   │   ├── values/            # Resources (strings, colors, themes)
│   │   ├── drawable/          # Icons and graphics
│   │   └── mipmap/            # App launcher icons
│   └── AndroidManifest.xml
└── build.gradle.kts
```

## Features

### Implemented
1. **Setup Screen** (`SetupActivity`)
   - First-time launch experience
   - Request notification permissions (Android 13+)
   - Request battery optimization exclusion
   - Skip option available
   - Visual feedback for granted permissions

2. **Login Screen** (`LoginActivity`)
   - NIM (Student ID) and password authentication
   - "Remember Me" functionality with SharedPreferences
   - Input validation
   - Forgot password information
   - Mock login with loading state

3. **Preferences Management** (`PreferenceManager`)
   - Setup completion tracking
   - Remember Me functionality
   - Saved NIM storage

### Planned/In Progress
- Main application interface with navigation
- Real-time notifications for class schedules
- Assignment notifications
- Background data synchronization
- API integration for authentication
- Student dashboard features

## Permissions

The application requests the following permissions:

- **POST_NOTIFICATIONS**: For real-time notifications about class schedules and assignments (Android 13+)
- **INTERNET**: For API communication and data synchronization
- **REQUEST_IGNORE_BATTERY_OPTIMIZATIONS**: To ensure background app operation for data sync

## Build Configuration

### Gradle
- **Gradle Version**: 8.2
- **Android Gradle Plugin**: Latest stable
- **Kotlin Plugin**: Latest stable

### Build Types
- **Debug**: Default configuration
- **Release**: ProGuard disabled (minifyEnabled: false)

## Setup & Installation

1. Clone the repository
2. Open in Android Studio (Arctic Fox or newer)
3. Sync Gradle dependencies
4. Run on device or emulator (API 24+)

## Development Status

This is an active development project. The current implementation includes:
- ✅ Initial setup flow
- ✅ Permission handling
- ✅ Login UI and validation
- ✅ Preference management
- 🚧 Main application interface (in progress)
- 🚧 API integration (planned)
- 🚧 Background services (planned)

## Language

The application is localized in **Indonesian (Bahasa Indonesia)**.

## Theme

Uses Material Design 3 with a custom theme: `Theme.MyBestUBSI`
- Light theme support
- NoActionBar variants for setup and login screens
