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

### Recent Updates (v2.0 - Current Branch: API35)
- **Leaderboard System** (Feb 4, 2026):
  - Added persistent local leaderboard with SharedPreferences
  - Name entry activity with 5-letter limit
  - High score detection and celebration (plays highscore.mp3)
  - Leaderboard displays top 10 scores with names
  - New activities: NameEntryActivity, LeaderboardActivity, HighScoreActivity
  - LeaderboardManager handles data persistence and retrieval

- **Stage System Rework** (Feb 3, 2026):
  - Stages now time-based to match audio file durations
  - Dynamic scoring: stage² × (audio_duration + 1)
  - Required hits scale with stage (1 hit/sec at stage 1, up to 10 hits/sec at stage 10)
  - Cumulative threshold tracking across all stages
  - Random audio selection each stage (excludes previous to prevent repeats)
  - App icon updated to use piotrek_1.png in all densities

- **UI Improvements** (Feb 5, 2026):
  - Replaced separate character images with single merged sprite (dupa_merge_1/2.png)
  - Characters now centered horizontally with responsive scaling
  - Merged image switches between frames on button click (50ms animation)
  - Added left margin to HIT text for better spacing
  - Layout uses percentage-based constraints for all screen sizes

- **API 35 Migration** (v1.2):
  - Upgraded to API 35 (Android 15)
  - Fixed Handler deprecations (now uses Looper.getMainLooper())
  - Implemented proper MediaPlayer resource management to prevent memory leaks
  - Replaced deprecated onBackPressed() with OnBackPressedDispatcher
  - Updated dependencies to API 35-compatible versions
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
   - Visual feedback through single merged character ImageView (dupa_merge_1/2)
   - Character sprite switches frames on button click with 50ms animation

3. **GameOver** (app/src/main/java/com/example/kapitandupa/GameOver.kt)
   - Displays final score and determines if it's a high score
   - Checks if score qualifies for top 10 leaderboard
   - Plays appropriate audio: highscore.mp3 or lowscore.mp3
   - High scores transition to NameEntryActivity
   - Non-high scores show restart and leaderboard buttons after audio completes
   - Restart button returns to GameActivity
   - Leaderboard button opens LeaderboardActivity

4. **NameEntryActivity** (app/src/main/java/com/example/kapitandupa/NameEntryActivity.kt)
   - Allows player to enter name (max 5 letters, uppercase only)
   - Custom keyboard interface with A-Z buttons
   - Delete button to remove characters
   - OK button saves score with name to leaderboard
   - Transitions to HighScoreActivity on save

5. **HighScoreActivity** (app/src/main/java/com/example/kapitandupa/HighScoreActivity.kt)
   - Celebration screen for new high scores
   - Plays highscore.mp3 audio
   - Provides buttons to restart game or view full leaderboard

6. **LeaderboardActivity** (app/src/main/java/com/example/kapitandupa/LeaderboardActivity.kt)
   - Displays top 10 scores with player names
   - Loads data from LeaderboardManager
   - Shows rank, name (5 letters), and score
   - Back button returns to GameOver or HighScore screen

7. **LeaderboardManager** (app/src/main/java/com/example/kapitandupa/LeaderboardManager.kt)
   - Manages persistent storage using SharedPreferences
   - Handles adding, retrieving, and sorting high scores
   - Maintains top 10 scores only
   - HighScoreEntry data class (name: String, score: Int)

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
- **Visual Feedback**:
  - Single merged character sprite (dupa_merge_1/2.png) switches frames on button click
  - Two-frame animation with 50ms duration
  - Characters centered horizontally and scaled responsively
  - Stage indicators (fiut1-10) update per stage
- **Button State**: SHOT button disabled (greyed out) until ready=true after start audio completes
- **Timing**: 10 second intro wait, variable stage delays based on audio files, 12 second restart delay
- **UI Layout**:
  - Three info boxes at bottom: SCORE (10%), HIT progress bar with stage indicators (70%), SHOT counter (10%)
  - HIT text has 8dp left margin for spacing
  - All UI elements use percentage-based constraints for responsive scaling
  - Character image constrained to parent edges, centered horizontally, bottom-aligned to info boxes

### Resource Structure
- **res/raw/**: Audio files (intro, start, gameover, highscore, lowscore, country, 13 rypanie_* variants)
- **res/drawable/ & drawable-v24/**: Game sprites
  - Character sprites: dupa_merge_1/2.png (merged character animation frames)
  - Legacy sprites: dupa_1/2.png, piotrek_1/2.png (kept for reference)
  - Stage indicators: lotos_1/2.png, fiut1-10 images
  - UI elements: green_box_border for info panels
- **res/layout/**: Activity layouts
  - activity_main.xml - Video intro screen
  - activity_game.xml - Main game screen with merged character sprite
  - activity_game_over.xml - Game over with score display and buttons
  - activity_name_entry.xml - Custom keyboard for name input
  - activity_high_score.xml - High score celebration screen
  - activity_leaderboard.xml - Top 10 scores display
- **res/mipmap-*/**: App icon (piotrek_1.png) in all densities (hdpi, mdpi, xhdpi, xxhdpi, xxxhdpi)
- All activities locked to landscape orientation in AndroidManifest.xml

### Important Implementation Details
- **Back Navigation**: Back button on all activities returns to home screen (not previous activity)
- **Audio Management**:
  - MediaPlayer instances created per audio play (not reused)
  - Proper cleanup and release after playback
  - All MediaPlayers pause in onPause() and resume in onResume()
- **Game Loop**: Handler-based timing for delays and recursive game loop
- **Activity Lifecycle** properly handles game state:
  - `playing` boolean prevents audio after destroy
  - `isPaused` flag pauses game when activity loses focus
  - GameActivity tracks audio state to properly resume after pause
  - MainActivity saves/restores video position on pause/resume
- **Leaderboard System**:
  - Persistent storage using SharedPreferences
  - Stores top 10 scores with 5-letter names
  - High score threshold checked against 10th place score
  - JSON serialization for HighScoreEntry objects
  - Automatic sorting by score (descending)
- **Character Animation**:
  - Single ImageView (id: characters) instead of separate dupa/piotrek views
  - Switches between dupa_merge_1.png (idle) and dupa_merge_2.png (shooting)
  - 50ms frame duration for shooting animation
  - Centered horizontally with responsive scaling on all screen sizes
- **Stage Debugging**: Set `stage = 10` in GameActivity.onCreate for game over testing

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
