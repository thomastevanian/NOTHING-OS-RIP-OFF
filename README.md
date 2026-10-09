# Dotline Launcher (Nothing OS 5.0 Visual Recreation)

A complete, pixel-accurate Nothing OS (Android 17 / Nothing OS 5.0 era) visual recreation for Samsung Galaxy and Android devices, crafted entirely with Kotlin and Jetpack Compose.

---

## Features

- **Radical Dot-Matrix Design System**:
  - Doto display typography and Space Mono numeric/caption fonts bundled locally.
  - Pure `#000000` AMOLED dark canvas with signature `#D71921` red micro-accents.
  - Flat 24dp rounded cards, dot page indicators, dotted separators, and dot-matrix clock.
  - High-performance one-shot transitions (<200ms) with no bounce or wobble.
- **Home Launcher Core**:
  - Registered with `ACTION_MAIN`, `CATEGORY_HOME`, and `CATEGORY_DEFAULT`.
  - 5x6 responsive grid with customizable 4x5 to 6x7 density.
  - Unpadded 4-app minimal dock.
  - Long-press edit mode with dotted matrix alignment overlay.
  - 2x2 miniature icon folder preview tiles opening into rounded dialogs.
  - Configurable gestures (swipe up for drawer, swipe down for status bar, double tap to lock).
- **Monochrome Icon Engine**:
  - Built-in custom vector silhouettes for top 60+ apps (Phone, Messages, Camera, Gallery, Chrome, Gmail, Spotify, Maps, YouTube, Netflix, Discord, etc.).
  - Automatic high-fidelity white silhouette extraction with alpha thresholding for all other installed apps.
  - Cached in memory and disk to eliminate scroll stutter.
- **Built-in Compose Widgets**:
  - Dot-Matrix Digital Clock & Date
  - Battery Level with circular dot ring and charging indicator
  - Live Weather with Open-Meteo free API (no API key needed, cached 30 min)
  - Upcoming Calendar Events
  - Dual World Clock (London & Tokyo)
  - Quick Notes scratchpad tile
- **Home Screen AppWidget Providers**:
  - Native `AppWidgetProvider` RemoteViews for Clock, Battery, Weather, and Date usable directly on Samsung One UI Home or other launchers.
- **Procedural Wallpaper Studio**:
  - 100% procedural on-device canvas wallpaper generation (Dot Grid, Dot-Matrix Gradient, Concentric Rings, Halftone Waves, Minimal Black + Red Dot).
  - 24 built-in presets + custom size and spacing controls.
  - Instant application to Home, Lock, or Both via `WallpaperManager`.
- **Sound Studio**:
  - Synthesizes clean electronic tones directly with `AudioTrack` PCM:
    - 8 Ringtones
    - 12 Notification pips and chimes
    - 6 Alarm pulses
    - 6 Micro UI clicks
  - Instant audio preview and direct export to `MediaStore` (Ringtones, Notifications, Alarms).
- **Samsung One UI Setup Guide ("Finish the Look")**:
  - Step-by-step optimization with deep links into Samsung settings.
  - Transparent documentation of One UI system boundaries.
- **Backup & Restore**:
  - Full layout and configuration export/import via JSON.
- **In-App Crash Log Diagnostics**:
  - Settings > About > Crash Logs captures uncaught exceptions for hassle-free debugging.

---

## How to Download and Install on Samsung Galaxy S21

1. Go to the **GitHub repository's Releases page**:  
   `https://github.com/<user>/<repo>/releases`
2. Download the latest `Dotline.apk` from the Assets list under the newest release tag.
3. On your Samsung Galaxy, open the downloaded APK from your browser or **My Files**.
4. If prompted with *"For your security, your phone is not allowed to install unknown apps from this source"*, tap **Settings** and toggle **Allow from this source**.
5. Tap **Install** and then **Open**.

---

## How to Set Dotline as Default Launcher

1. Launch Dotline. On first run, tap through the 3 onboarding screens to the final step and tap **Set Default & Enter**.
2. Select **Dotline** and choose **Always**.
3. **Manual Method via Samsung Settings**:
   - Go to **Settings** > **Apps**.
   - Tap **Choose default apps** (at the top).
   - Tap **Home app**.
   - Select **Dotline**.

---

## How to Switch Back to Samsung One UI Home

1. Open **Settings** on your phone.
2. Navigate to **Apps** > **Choose default apps** > **Home app**.
3. Tap **One UI Home**.
4. Press the Home button to return to Samsung's default launcher.

---

## What Samsung One UI Always Controls

Because Android separates home launchers from the system framework, Samsung One UI maintains exclusive control over:
- **Notification Shade & Quick Settings Panel**: The pulldown shade and toggle layout are system-level and cannot be altered by third-party launchers.
- **Samsung System Settings App**: Menus and subpages inside Samsung Settings.
- **Lock Screen Framework**: The lock screen wallpaper can be set by Dotline, but lock screen clock styles and always-on-display are handled by One UI.
- **Physical Glyph Hardware**: Nothing's physical rear LED Glyph hardware is exclusive to Nothing Phone hardware and does not exist on Samsung Galaxy phones.

---

## Build and CI Workflow

The project is built automatically using GitHub Actions (`.github/workflows/build.yml`):
- Runs `./gradlew assembleDebug` on Ubuntu runners.
- Publishes installable `Dotline.apk` directly to GitHub Releases on every push to `main`.
