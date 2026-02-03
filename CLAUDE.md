# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

This is an Android game remake based on a Polish cartoon "Kapitan Dupa" from the TV series "Kapitan Bomba". The game is a simple clicker-style game with stages that progress based on player performance.

**Legal Context**: This is a quotation-based recreation for Android. All graphical and audio assets are based on Git Produkcja's intellectual property. The repository can be removed if legal or ethical concerns arise.

## Build & Development

### Requirements
- Android Studio Bumblebee or later
- Minimum API: 32 (Android 12)
- Compiled with SDK 35 (Android 15)
- Target SDK: 35
- Kotlin 1.8.10
- Gradle 8.5

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
- Supports API 32-35

### Recent Updates (v1.2)
- Upgraded to API 35 (Android 15)
- Fixed Handler deprecations (now uses Looper.getMainLooper())
- Implemented proper MediaPlayer resource management to prevent memory leaks
- Replaced deprecated onBackPressed() with OnBackPressedDispatcher
- Updated dependencies to API 35-compatible versions
- Changed app icon to use piotrek_1.png (created in all densities)
- Implemented proper pause/resume handling for all activities
- Game now pauses when window becomes inactive (minimized)

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
   - **Dynamic audio system**: AudioFile data class defines all 13 audio resources with durations
   - Random audio selection each stage (excludes previous audio to prevent consecutive repeats)
   - Stage durations vary from 5s to 14s based on randomly selected audio file length + 1s margin
   - Point thresholds calculated dynamically during gameplay: stage² × (audio_duration + 1)
   - Cumulative threshold tracking with stage-by-stage validation
   - Game over triggered if player fails to meet stage requirements
   - All 13 rypanie_* audio samples available for selection
   - Button disabled initially, enabled when game starts
   - Game automatically ends after stage 10 completes
   - Visual feedback through ImageView animations (dupa, piotrek, fiut1-10, lotos)

3. **GameOver** (app/src/main/java/com/example/kapitandupa/GameOver.kt)
   - Plays game over audio sequence
   - Restart button enabled after 12 seconds
   - Returns to GameActivity on restart

### Key Game Mechanics
- **Scoring**: Points = clicks × current_stage (each click in stage N gives N points)
- **Stage System**: 10 progressive stages (fiut1-10) with dynamic durations and thresholds
  - Each stage randomly selects from all 13 audio files (cannot repeat consecutively)
  - Stage duration = selected audio file length + 1 second margin
  - Required hits per second increases with each stage (1 hit/sec in stage 1, up to 10 hits/sec in stage 10)
  - Point thresholds calculated dynamically: stage² × audio_duration
  - Cumulative thresholds tracked throughout gameplay
  - Game automatically ends after completing stage 10 (regardless of score)
  - Thresholds vary per playthrough based on random audio selection
- **Audio System**:
  - All 13 rypanie_* audio files available for random selection each stage
  - Each stage randomly picks one (excluding the previous stage's audio)
  - Audio durations range from 4s to 13s
  - Dedicated start/gameover sounds
- **Visual Feedback**: Two-frame animation on button press (50ms duration)
- **Button State**: SHOT button disabled (greyed out) until ready=true after start audio completes
- **Timing**: 10 second intro wait, variable stage delays based on audio files, 12 second restart delay

### Resource Structure
- **res/raw/**: Audio files (intro, start, gameover, lowscore, 13 rypanie_* variants)
- **res/drawable/ & drawable-v24/**: Game sprites (dupa_1/2, piotrek_1/2, lotos_1/2, fiut images)
- **res/layout/**: Three activity layouts (activity_main, activity_game, activity_game_over)
- All activities locked to landscape orientation in AndroidManifest.xml

### Important Implementation Details
- Back button on all activities returns to home screen (not previous activity)
- MediaPlayer instances created per audio play (not reused)
- Handler-based timing for delays and recursive game loop
- Activity lifecycle properly handles game state:
  - `playing` boolean prevents audio after destroy
  - `isPaused` flag pauses game when activity loses focus
  - All MediaPlayers pause in onPause() and resume in onResume()
  - GameActivity tracks audio state to properly resume after pause
  - MainActivity saves/restores video position on pause/resume
- Stage debugging: Set `stage = 10` in GameActivity.onCreate for game over testing

## Audio File Durations

All durations have a ±1 second margin.

### Gameplay Sounds (rypanie_*)
- rypanie_dopalacze.mp3 - 6 sec
- rypanie_jakbabe.mp3 - 6 sec
- rypanie_kawalerze.mp3 - 6 sec
- rypanie_kolba.mp3 - 13 sec
- rypanie_laser.mp3 - 9 sec
- rypanie_maaaa.mp3 - 9 sec
- rypanie_maaaa2.mp3 - 10 sec
- rypanie_nie.mp3 - 5 sec
- rypanie_nieczuje.mp3 - 4 sec
- rypanie_obrotowa.mp3 - 4 sec
- rypanie_piana.mp3 - 11 sec
- rypanie_torpedy.mp3 - 10 sec
- rypanie_trututututu.mp3 - 12 sec

### Game State Sounds
- start.mp3 - 9 sec
- country.mp3 - 13 sec
- gameover.mp3 - 4 sec
- highscore.mp3 - 7 sec
- lowscore.mp3 - 8 sec
