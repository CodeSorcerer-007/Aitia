# AITIA (Αἰτία) — Native Android Developer Bug & Debugging Notebook

[![Kotlin](https://img.shields.io/badge/Kotlin-2.0.21-purple.svg)](https://kotlinlang.org)
[![Jetpack Compose](https://img.shields.io/badge/Jetpack%20Compose-Material%203-4285F4.svg)](https://developer.android.com/jetpack/compose)
[![Room](https://img.shields.io/badge/Room-2.6.1-orange.svg)](https://developer.android.com/training/data-storage/room)
[![Offline First](https://img.shields.io/badge/Privacy-100%25%20Offline-green.svg)](https://aitia.dev)
[![Android](https://img.shields.io/badge/Android-8.0%20to%2016-brightgreen.svg)](https://developer.android.com)

> **"Aitia helps developers capture what went wrong, understand why it happened, and keep a record of how it was fixed."**
> 
> *The name **Aitia (Αἰτία)** comes from the ancient Greek concept of **cause / reason** — finding the underlying cause behind software problems.*

---

## 🏛️ Product Architecture & Core Loop

```
┌─────────────────────────────────────────────────────────────┐
│                       AITIA CORE LOOP                       │
│                                                             │
│   Capture ──► Investigate ──► Fix ──► Verify ──► Learn      │
└─────────────────────────────────────────────────────────────┘
```

Aitia is a native, offline-first developer issue tracker and debugging journal designed to optimize the primary developer moment:
1. **Notice a problem** during testing.
2. **Open Aitia** and capture it in **under 30 seconds**.
3. **Continue testing** without breaking flow.
4. **Return later** to inspect logs, reproduce steps, identify the root cause (Αἰτία), record the fix, and verify resolution.

---

## ✨ Implemented Features

### ⚡ 1. Sub-30-Second Quick Capture
- Auto-focused title input with instant draft persistence in DataStore.
- **Smart Duplicate Bug Detection**: Real-time token and n-gram similarity engine flags potential existing duplicates as you type.
- Fast Type, Priority, Project, and Environment pill selectors with inline tag chips.

### 🔍 2. Developer Workbench & Issue Detail
- **Status Lifecycle Stepper**: One-tap progression bar (`Open → Investigating → Blocked → Fixed → Verified → Closed`).
- **Reproduction Steps Builder**: Step-by-step numbered instructions.
- **Expected vs Actual Card**: Side-by-side behavioral contrast.
- **Monospaced Logs & StackTrace Parser**: Local heuristic engine that auto-detects `ExceptionType`, `SourceFile.kt`, `LineNumber`, and `ErrorMessage` from pasted logs.
- **Investigation Journal**: Chronological debugging notes log with timestamps.
- **Interactive Checklists**: In-issue tasks with animated strike-through and completion progress bar.
- **Root Cause & Fix (Αἰτία)**: Dedicated fields for `Why did it happen?` and `What changed?`, plus verification device logging.
- **Evidence Attachments**: Native Android Photo Picker & File Picker integration with full-screen attachment preview.

### 🧪 3. Projects, Releases & Testing Sessions
- **Projects Management**: Active project cards with package names, versions, open defect counters, and critical bug badges.
- **Releases & Versions View**: Grouping of defects by release version (e.g. `Release v1.4.2 — 12 issues found · 9 fixed · 2 open · 1 critical`).
- **Testing Sessions**: Start exploratory QA sessions with a live floating timer; all bugs logged during the session auto-link into a Session Summary report.
- **Environment Profiles**: Reusable test device profiles (including pre-configured `OnePlus Nord 5 — Android 16 (API 36)` and `Pixel 8 — Android 15`) to auto-populate hardware metadata.

### 📊 4. Local Diagnostic Insights & Analytics
- 100% local rule-based intelligence: Crash hotspot detection, category regression risks, unresolved critical defect warnings, and average resolution time tracking.

### 🔒 5. Privacy-First & App Lock Protection
- **Zero Cloud Tracking**: All stack traces, source code snippets, and private bug notes stay 100% on-device in local SQLite/Room.
- **App Lock**: Keypad PIN & Biometric protection overlay.

### 💾 6. Full Data Portability & Export
- **JSON Backup**: Full versioned database export & import with schema integrity.
- **Markdown Reports**: Formatted defect reports for pasting into GitHub Issues, Notion, Slack, or email.
- **CSV Export**: Clean tabular data for spreadsheets.

### 🎨 7. Branding & Motion Design
- **Animated Aitia Logo**: Custom breathing scale pulse with a rotating neon gradient aura during loading and demo data seeding.
- **Visual Palette**: Linear/Raycast-inspired dark slate theme (`#0B0F17`, `#161B22`, `#21262D`, `#30363D`), Electric Blue (`#58A6FF`), Greek Purple (`#A371F7`), and OLED Pure Black mode.
- **Haptics**: Tactile vibration feedback on button presses, status transitions, and verified milestones.

### ⚡ 8. Android Home-Screen Widget
- Jetpack Glance widget with open & critical counters and a 1-tap Quick Capture shortcut.

---

## 🛠️ Technology Stack

- **Language**: Kotlin 2.0.21
- **UI Toolkit**: Jetpack Compose + Material 3 (Compose BOM 2024.11.00)
- **Architecture**: Clean Architecture + MVVM + Repository Pattern
- **Local Persistence**: Room Database 2.6.1 + KSP
- **Key-Value Storage**: Jetpack DataStore Preferences
- **Asynchrony**: Kotlin Coroutines + StateFlow
- **Navigation**: Jetpack Navigation Compose 2.8.4
- **Home Widget**: AndroidX Glance AppWidget 1.1.1
- **Security**: AndroidX Biometric 1.2.0

---

## 🚀 Building & Running

### Prerequisites
- Android Studio Ladybug (2024.2+) or Android CLI
- JDK 21
- Android SDK Platform 35 / 36

### Build Commands
```bash
# Clone the repository
git clone https://github.com/aitia/aitia-android.git
cd Aitia

# Run Unit Tests
./gradlew test

# Assemble Debug APK
./gradlew assembleDebug

# Install on connected device (e.g. OnePlus Nord 5)
./gradlew installDebug
```

---

## 📦 Instant Demo Data

Navigate to **Settings → Developer Demo Data → Seed Realistic Sample Data** to immediately explore Aitia with real projects (`WeatherApp`, `VaultKey`, `NovaPay`), crash stack traces, reproduction steps, testing sessions, and diagnostic insights.
