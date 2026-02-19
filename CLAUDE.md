# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working in this repository.

## Project Overview

Android remake of the "Kapitan Dupa" mini-game from "Kapitan Bomba".
The app is written in Kotlin and uses a stage-based clicker loop with local leaderboard persistence.

**Legal context**: Visual/audio assets are based on third-party IP (Git Produkcja universe). Repository may be removed if legal/ethical issues arise.

## Build and Tooling

- Android Gradle Plugin: `8.1.0` (`/build.gradle`)
- Gradle wrapper: `8.5` (`/gradle/wrapper/gradle-wrapper.properties`)
- Kotlin plugin: `1.8.10`
- compileSdk: `35`
- minSdk: `32`
- targetSdk: `35`
- app version: `versionCode 2`, `versionName "2.0"`

### Common Commands

```bash
./gradlew build
./gradlew assembleDebug
./gradlew assembleRelease
./gradlew installDebug
./gradlew clean
./gradlew test
./gradlew connectedAndroidTest
./gradlew test --tests com.example.kapitandupa.ExampleUnitTest
```

## Runtime Flow

1. `MainActivity` (`/app/src/main/java/com/example/kapitandupa/MainActivity.kt`)
- Plays `res/raw/intro.mp4` in `VideoView`.
- Starts `GameActivity` after intro completion.
- Saves/restores video position in `onPause/onResume`.

2. `GameActivity` (`/app/src/main/java/com/example/kapitandupa/GameActivity.kt`)
- Plays `start.mp3`, then waits `10s` before enabling SHOT button.
- Uses 10 gameplay stages (`fiut1` to `fiut10` indicators).
- Each stage randomly selects one of 13 `rypanie_*` clips, excluding immediate repeat.
- Stage duration is `(audioDuration + 1s)`.
- Clicking SHOT adds `stage` points per click.
- Per-stage point requirement is `stage^2 * (audioDuration + 1)`.
- Tracks cumulative threshold and checks previous stage completion on each new stage.
- Ends automatically when stage increments past 10.
- Sprite animation swaps `dupa_merge_1` -> `dupa_merge_2` for `50ms` on click.
- SHOT button is disabled at launch and enabled when `ready=true`.

3. `GameOver` (`/app/src/main/java/com/example/kapitandupa/GameOver.kt`)
- Always plays `gameover.mp3`.
- If score qualifies for leaderboard: routes to `HighScoreActivity`.
- If not: waits `4s`, plays `lowscore.mp3`, then reveals restart and leaderboard buttons.

4. `HighScoreActivity` (`/app/src/main/java/com/example/kapitandupa/HighScoreActivity.kt`)
- Shows final score and blinking exclamation animation.
- Plays `highscore.mp3`.
- Auto-advances to `NameEntryActivity` after `8s`.

5. `NameEntryActivity` (`/app/src/main/java/com/example/kapitandupa/NameEntryActivity.kt`)
- Input is uppercase with max length 5.
- Sanitizes to `A-Z` only (removes `*` and non-letter characters).
- Rejects empty name and the reserved name `BOMBA`.
- Saves entry, then opens `LeaderboardActivity`.

6. `LeaderboardActivity` (`/app/src/main/java/com/example/kapitandupa/LeaderboardActivity.kt`)
- Shows top scores from local storage.
- Restart button starts `GameActivity`.
- Reset button clears leaderboard after confirmation dialog.

## Leaderboard Rules

`LeaderboardManager` (`/app/src/main/java/com/example/kapitandupa/LeaderboardManager.kt`):

- Stores entries in `SharedPreferences` as JSON via Gson.
- Entry model: `HighScoreEntry(score, name, timestamp)`.
- Sort order: score descending, then timestamp ascending (earlier wins tie).
- Keeps only top 5 entries.
- `isHighScore` is strict: if table is full, score must be `>` current lowest score.

## UI and Resources

- All activities are forced to landscape in `/app/src/main/AndroidManifest.xml`.
- Back press in every activity navigates to Android home screen (not previous activity).
- Main gameplay layout: `/app/src/main/res/layout/activity_game.xml`
- Other layouts:
- `/app/src/main/res/layout/activity_main.xml`
- `/app/src/main/res/layout/activity_game_over.xml`
- `/app/src/main/res/layout/activity_high_score.xml`
- `/app/src/main/res/layout/activity_name_entry.xml`
- `/app/src/main/res/layout/activity_leaderboard.xml`

### Key Assets

- Raw media (`/app/src/main/res/raw`):
- `intro.mp4`, `start.mp3`, `gameover.mp3`, `highscore.mp3`, `lowscore.mp3`, `country.mp3`, and 13 `rypanie_*` clips.
- Character drawables (`/app/src/main/res/drawable-v24`):
- `dupa_merge_1.png`, `dupa_merge_2.png` (active gameplay frames)
- Legacy/other assets include `dupa_1.png`, `dupa_2.png`, `piotrek_1.png`, `pioterk_2.png`, `lotos_1.png`, `lotos_2.png`, `gameover.png`.

### Audio Durations Used by GameActivity

- `rypanie_obrotowa` 4s
- `rypanie_nieczuje` 4s
- `rypanie_nie` 5s
- `rypanie_dopalacze` 6s
- `rypanie_kawalerze` 6s
- `rypanie_jakbabe` 6s
- `rypanie_laser` 9s
- `rypanie_maaaa` 9s
- `rypanie_maaaa2` 10s
- `rypanie_torpedy` 10s
- `rypanie_piana` 11s
- `rypanie_trututututu` 12s
- `rypanie_kolba` 13s

Note: gameplay delay per stage is `duration + 1s`.

## Networking/Security

- App manifest enables cleartext traffic (`android:usesCleartextTraffic="true"`).
- `/app/src/main/res/xml/network_security_config.xml` currently permits cleartext for `server.com` (and subdomains).
