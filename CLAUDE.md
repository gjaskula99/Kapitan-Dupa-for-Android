# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

This is an Android game remake based on a Polish cartoon "Kapitan Dupa" from the TV series "Kapitan Bomba". The game is a simple clicker-style game with stages that progress based on player performance.

**Legal Context**: This is a quotation-based recreation for Android. All graphical and audio assets are based on Git Produkcja's intellectual property. The repository can be removed if legal or ethical concerns arise.

## Build & Development

### Requirements
- Android Studio Bumblebee or later
- Minimum API: 32 (Android 8.0 Oreo, though build.gradle shows minSdk 32)
- Compiled with SDK 32
- Kotlin 1.8.10
- Gradle 8.1.0

### Build Commands
```bash
# Build the project
./gradlew build

# Build debug APK
./gradlew assembleDebug

# Build release APK
./gradlew assembleRelease

# Install debug APK to connected device
./gradlew installDebug

# Clean build artifacts
./gradlew clean
```

### Testing
```bash
# Run unit tests
./gradlew test

# Run instrumented tests (requires emulator/device)
./gradlew connectedAndroidTest

# Run specific test class
./gradlew test --tests com.example.kapitandupa.ExampleUnitTest
```

### Running the App
- Tested on AVD with API 30
- Tested on physical device: Samsung Galaxy A71 (API 30)
- App runs in landscape orientation only

## Code Architecture

### Activity Flow
1. **MainActivity** (app/src/main/java/com/example/kapitandupa/MainActivity.kt)
   - Entry point that plays intro video from `res/raw/intro`
   - On video completion, transitions to GameActivity
   - Handles pause/resume to preserve video playback position

2. **GameActivity** (app/src/main/java/com/example/kapitandupa/GameActivity.kt)
   - Main game logic with 10 stages
   - Player clicks button to score points (points increment by stage number)
   - Timer-based progression system with recursive Handler callbacks
   - Stage thresholds: 20, 80, 200, 500, 1000, 2000, 4000, 8000, 10000 points
   - Game over triggered if player fails to meet stage requirements
   - 13 different audio samples played randomly during gameplay (rypanie_*)
   - Visual feedback through ImageView animations (dupa, piotrek, fiut1-10, lotos)

3. **GameOver** (app/src/main/java/com/example/kapitandupa/GameOver.kt)
   - Plays game over audio sequence
   - Restart button enabled after 12 seconds
   - Returns to GameActivity on restart

### Key Game Mechanics
- **Scoring**: Points = clicks × current_stage
- **Stage System**: 10 progressive stages (fiut1-10), each requiring higher point thresholds
- **Audio System**: 13 randomized sound effects during gameplay, plus dedicated start/gameover sounds
- **Visual Feedback**: Two-frame animation on button press (50ms duration)
- **Timing**: 10 second intro wait, 13 second stage delay, 12 second restart delay

### Resource Structure
- **res/raw/**: Audio files (intro, start, gameover, lowscore, 13 rypanie_* variants)
- **res/drawable/ & drawable-v24/**: Game sprites (dupa_1/2, piotrek_1/2, lotos_1/2, fiut images)
- **res/layout/**: Three activity layouts (activity_main, activity_game, activity_game_over)
- All activities locked to landscape orientation in AndroidManifest.xml

### Important Implementation Details
- Back button on all activities returns to home screen (not previous activity)
- MediaPlayer instances created per audio play (not reused)
- Handler-based timing for delays and recursive game loop
- Activity lifecycle properly handles game state (playing boolean prevents audio after destroy)
- Stage debugging: Set `stage = 10` in GameActivity.onCreate for game over testing
