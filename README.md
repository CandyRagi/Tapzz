<p align="center">
  <img src="docs/assets/logo.png" alt="Tapzz Logo" width="120" />
</p>

<h1 align="center">TAPZZ</h1>

<p align="center">
  <strong>NFC-Powered Social Link Sharing for Android & Wear OS</strong>
</p>

<p align="center">
  <a href="#features">Features</a> •
  <a href="#architecture">Architecture</a> •
  <a href="#tech-stack">Tech Stack</a> •
  <a href="#getting-started">Getting Started</a> •
  <a href="#project-structure">Project Structure</a> •
  <a href="#how-it-works">How It Works</a> •
  <a href="#contributing">Contributing</a> •
  <a href="#license">License</a>
</p>

<p align="center">
  <img src="https://img.shields.io/badge/Platform-Android-3DDC84?logo=android&logoColor=white" alt="Platform" />
  <img src="https://img.shields.io/badge/Wear_OS-Supported-4285F4?logo=wearos&logoColor=white" alt="Wear OS" />
  <img src="https://img.shields.io/badge/Kotlin-2.0.21-7F52FF?logo=kotlin&logoColor=white" alt="Kotlin" />
  <img src="https://img.shields.io/badge/Jetpack_Compose-UI-4285F4?logo=jetpackcompose&logoColor=white" alt="Compose" />
  <img src="https://img.shields.io/badge/Firebase-Backend-FFCA28?logo=firebase&logoColor=black" alt="Firebase" />
  <img src="https://img.shields.io/badge/NFC-HCE-009688" alt="NFC" />
  <img src="https://img.shields.io/badge/License-MIT-blue" alt="License" />
</p>

---

## Overview

**Tapzz** is a native Android application that leverages **NFC Host Card Emulation (HCE)** to enable instant, contactless sharing of social media profiles and custom links. Simply tap your phone against another NFC-enabled device — no app install required on the receiving end — and your selected social card opens directly in their browser.

The project ships with two build targets:
- **`app`** — Full-featured phone application (API 26+)
- **`wear`** — Companion Wear OS application (API 30+)

---

## Features

| Feature | Description |
|---|---|
| **Tap-to-Share** | Share any social profile via NFC with a single tap — the receiver doesn't need the app installed |
| **Custom Social Cards** | Create personalized cards with custom names, colors, images, and linked URLs |
| **Multi-Platform Support** | Supports Instagram, Snapchat, TikTok, YouTube, Facebook, Discord, WhatsApp, and Valorant links |
| **Quick Access** | Pin frequently used cards for instant one-tap sharing |
| **User Profiles** | Full profile management with avatar upload via Cloudinary |
| **Firebase Auth** | Email/password authentication with secure session management |
| **Wear OS Companion** | Trigger card sharing directly from your wrist |
| **Animated Transitions** | Smooth slide + fade navigation transitions between screens |

---

## Architecture

```
┌──────────────────────────────────────────────────────┐
│                    Presentation Layer                 │
│  ┌─────────┐  ┌──────────┐  ┌───────────────────┐   │
│  │ Screens │  │Components│  │   Theme / Design   │   │
│  └────┬────┘  └────┬─────┘  └───────────────────┘   │
│       │             │                                │
│  ┌────▼─────────────▼────┐                           │
│  │      ViewModels       │                           │
│  └────────────┬──────────┘                           │
├───────────────┼──────────────────────────────────────┤
│               │          Data Layer                  │
│  ┌────────────▼──────────┐  ┌─────────────────────┐  │
│  │     Repositories      │  │    Data Models       │  │
│  └────────────┬──────────┘  │  (Card, User)        │  │
│               │             └─────────────────────┘  │
├───────────────┼──────────────────────────────────────┤
│               │        Services Layer                │
│  ┌────────────▼──────────┐  ┌─────────────────────┐  │
│  │  Firebase Firestore   │  │  NFC HCE Service     │  │
│  │  Firebase Auth        │  │  (HostApduService)   │  │
│  │  Cloudinary CDN       │  └─────────────────────┘  │
│  └───────────────────────┘                           │
└──────────────────────────────────────────────────────┘
```

The app follows a **clean-ish MVVM** pattern:

- **Screens** — Composable UI screens (`HomeScreen`, `ProfileScreen`, `SignInScreen`, etc.)
- **Components** — Reusable UI components (`CardItem`, `CreateCardDialog`, `EditProfileDialog`, etc.)
- **ViewModels** — State management and business logic (`AuthViewModel`)
- **Repositories** — Data access abstraction (`AuthRepository`)
- **Models** — Data classes (`Card`, `User`, `TapHistoryItem`)
- **Services** — Android system services (`MyHostApduService` for NFC HCE)

---

## Tech Stack

| Layer | Technology |
|---|---|
| **Language** | Kotlin 2.0.21 |
| **UI Framework** | Jetpack Compose (BOM 2024.11.00) + Material 3 |
| **Navigation** | Jetpack Navigation Compose 2.8.4 |
| **Authentication** | Firebase Auth |
| **Database** | Cloud Firestore |
| **Image Loading** | Coil Compose 2.4.0 |
| **Media Storage** | Cloudinary CDN |
| **NFC** | Android NFC HCE (Host Card Emulation) via `HostApduService` |
| **Build System** | Gradle 8.13.1 (Kotlin DSL) with Version Catalog |
| **Min SDK** | API 26 (Android 8.0) — Phone / API 30 (Android 11) — Wear |
| **Target SDK** | API 36 (Phone) / API 34 (Wear) |

---

## Getting Started

### Prerequisites

- **Android Studio** Ladybug or later
- **JDK 17+**
- **Android SDK** with API 36 installed
- A physical Android device with **NFC support** (emulators cannot emulate HCE)

### Setup

1. **Clone the repository**

   ```bash
   git clone https://github.com/CandyRagi/Tapzz.git
   cd Tapzz
   ```

2. **Configure environment variables**

   Create a `.env` file in the project root:

   ```env
   CLOUDINARY_CLOUD_NAME=your_cloud_name
   CLOUDINARY_UPLOAD_PRESET=your_upload_preset
   FIREBASE_API_KEY=your_firebase_api_key
   ```

3. **Add Firebase configuration**

   Place your `google-services.json` files in:
   - `app/google-services.json` — for the phone app
   - `wear/google-services.json` — for the Wear OS app

4. **Build & Run**

   ```bash
   ./gradlew :app:installDebug    # Phone app
   ./gradlew :wear:installDebug   # Wear OS app
   ```

   Or open the project in Android Studio and run the desired module.

---

## Project Structure

```
Tapzz/
├── app/                                    # Phone application module
│   ├── src/main/
│   │   ├── AndroidManifest.xml             # NFC permissions & HCE service declaration
│   │   └── java/com/project/tapthehuzz/
│   │       ├── MainActivity.kt             # Entry point, navigation host, NFC lifecycle
│   │       ├── data/
│   │       │   ├── model/
│   │       │   │   ├── Card.kt             # Social card data model
│   │       │   │   └── User.kt             # User profile + tap history models
│   │       │   └── repository/
│   │       │       └── AuthRepository.kt   # Firebase Auth & Firestore operations
│   │       ├── services/
│   │       │   └── MyHostApduService.kt    # NFC HCE — NDEF emulation service
│   │       ├── userInterface/
│   │       │   ├── components/             # Reusable Compose components
│   │       │   │   ├── CardItem.kt
│   │       │   │   ├── CreateCardDialog.kt
│   │       │   │   ├── EditProfileDialog.kt
│   │       │   │   ├── EmptyStateCard.kt
│   │       │   │   └── SocialLinkDialog.kt
│   │       │   ├── screens/                # Full-screen Composables
│   │       │   │   ├── MainScreens.kt      # Home screen
│   │       │   │   ├── AllCardsScreen.kt
│   │       │   │   ├── CardTransmissionScreen.kt
│   │       │   │   ├── ProfileScreen.kt
│   │       │   │   ├── SignInScreen.kt
│   │       │   │   ├── SignUpScreen.kt
│   │       │   │   └── SplashScreen.kt
│   │       │   ├── theme/                  # Material 3 theme (Color, Type, Theme)
│   │       │   └── viewmodel/
│   │       │       └── AuthViewModel.kt
│   │       └── utils/
│   │           └── CardNfcManager.kt       # Static NFC card URL state holder
│   └── build.gradle.kts
├── wear/                                   # Wear OS companion module
│   ├── src/main/
│   └── build.gradle.kts
├── docs/                                   # GitHub Pages landing site + APK download
│   ├── assets/logo.png
│   ├── index.html
│   ├── styles.css
│   ├── script.js
│   └── TapTheHuzz.apk
├── gradle/libs.versions.toml              # Centralized dependency version catalog
├── build.gradle.kts                        # Root build configuration
├── settings.gradle.kts                     # Module includes (app, wear)
└── LICENSE                                 # MIT License
```

---

## How It Works

### NFC Host Card Emulation (HCE)

Tapzz uses Android's **Host Card Emulation** API to make your phone behave like an NFC tag — without requiring physical NFC stickers or tags.

**The flow:**

1. The user selects a social card (e.g., their Instagram profile link).
2. The selected URL is stored in `CardNfcManager.currentCardUrl`.
3. When another NFC-enabled phone taps the device, Android's NFC subsystem routes the APDU commands to `MyHostApduService`.
4. The service emulates an **NDEF Type 4 Tag**:
   - Responds to `SELECT` commands with the NDEF Application ID (`D2760000850101`)
   - Serves a Capability Container (CC) file describing the tag's structure
   - Dynamically generates an **NDEF URI record** from the selected card URL
5. The receiving device reads the NDEF record and opens the link in its default browser.

**Key technical details:**
- AID registered: `D2760000850101` (standard NDEF AID)
- APDU command handling: `SELECT`, `READ BINARY` for CC and NDEF files
- URI prefix encoding per NFC Forum URI RTD specification (e.g., `0x04` = `https://`)
- Foreground dispatch: `CardEmulation.setPreferredService()` used in `onResume()` to prevent the system "choose an app" dialog

### Authentication & Data Flow

- **Sign Up / Sign In** → Firebase Authentication (email + password)
- **Profile & Cards** → Stored and synced via Cloud Firestore
- **Profile Images** → Uploaded to Cloudinary, URL stored in Firestore

---

## Environment Variables

| Variable | Description |
|---|---|
| `CLOUDINARY_CLOUD_NAME` | Your Cloudinary cloud name for image uploads |
| `CLOUDINARY_UPLOAD_PRESET` | Unsigned upload preset configured in Cloudinary |
| `FIREBASE_API_KEY` | Firebase Web API key (used in build config) |

> **Note:** `google-services.json` files are required for Firebase SDK initialization and are not committed to version control.

---

## Contributing

Contributions are welcome! To get started:

1. Fork the repository
2. Create a feature branch: `git checkout -b feature/your-feature`
3. Commit your changes: `git commit -m "feat: add your feature"`
4. Push to the branch: `git push origin feature/your-feature`
5. Open a Pull Request

Please follow [Conventional Commits](https://www.conventionalcommits.org/) for commit messages.

---

## License

This project is licensed under the **MIT License** — see the [LICENSE](LICENSE) file for details.

```
MIT License • Copyright (c) 2025 ANSH TIWARI
```

---

<p align="center">
  <sub>Built with ❤️ using Kotlin, Jetpack Compose, and NFC</sub>
</p>
