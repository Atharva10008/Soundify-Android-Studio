# Soundify

Soundify is an Android application that leverages on-device machine learning to perform real-time object detection and provide spoken descriptions of detected objects. It features a modern, user-friendly interface, Firebase authentication, and a robust navigation structure. This README provides a comprehensive overview, setup instructions, and contribution guidelines for developers and users.

---

## Table of Contents
- [Overview](#overview)
- [Features](#features)
- [Screenshots](#screenshots)
- [APK Download](#apk-download)
- [Architecture](#architecture)
- [Machine Learning Model](#machine-learning-model)
- [Firebase Integration](#firebase-integration)
- [Navigation Flow](#navigation-flow)
- [UI & Theming](#ui--theming)
- [Setup & Installation](#setup--installation)
- [Usage](#usage)
- [Project Structure](#project-structure)
- [Contributing](#contributing)
- [License](#license)

---

## Overview

**Soundify** transforms your device into a smart assistant that can detect objects in images using a TensorFlow Lite model and describe them using text-to-speech. The app is designed for accessibility, learning, and fun, making it easy for anyone to explore the world through their camera or gallery images.

---

## Features

- **Object Detection:**
  - Detects 80+ common objects using a pre-trained SSD MobileNet V1 model.
  - Draws bounding boxes and labels on detected objects in images.
  - Confidence thresholding for reliable results.
- **Text-to-Speech:**
  - Reads out detected object names and Wikipedia summaries for accessibility.
- **Image Input:**
  - Capture photos using the camera or select images from the gallery.
- **Authentication:**
  - Secure login and signup using Firebase Authentication.
  - Password reset functionality.
- **User Dashboard:**
  - Personalized dashboard with user info and navigation drawer.
- **Onboarding/Walkthrough:**
  - Modern onboarding screens to introduce app features.
- **Material Design:**
  - Clean, responsive UI with custom theming and branding.
- **Offline Support:**
  - Core object detection works offline (no network required for inference).
- **Wikipedia Integration:**
  - Tap detected objects to fetch and read Wikipedia summaries.

---

## Screenshots

<div align="center">
  <img src="https://github.com/user-attachments/assets/0861c7ff-2533-4e3e-8a0e-3ca62a81b86e" alt="splash" width="250" style="margin:8px;" />
  <img src="https://github.com/user-attachments/assets/e994751b-6975-41de-a495-7c47d5b8540d" alt="walkthrough" width="250" style="margin:8px;" />
  <img src="https://github.com/user-attachments/assets/03e268e3-d04e-4460-8967-c5e0b707cbc6" alt="login" width="250" style="margin:8px;" />
  <img src="https://github.com/user-attachments/assets/e10577ea-8aa4-4f56-8212-583a09e1be6a" alt="signup" width="250" style="margin:8px;" />
  <img src="https://github.com/user-attachments/assets/fec09a69-e8d1-4089-aee0-ea49743f6361" alt="dashboard" width="250" style="margin:8px;" />
  <img src="https://github.com/user-attachments/assets/d59cbeda-d540-4e73-967b-06beca1de713" alt="object-detection" width="250" style="margin:8px;" />
  <img src="https://github.com/user-attachments/assets/5051b799-d22a-44b0-a1db-7642f96d5f42" alt="gallery" width="250" style="margin:8px;" />
  <img src="https://github.com/user-attachments/assets/4d520369-05d8-419b-9d3d-a50b28f2775e" alt="slideshow" width="250" style="margin:8px;" />
</div>

---

## APK Download

You can download the latest APK release of Soundify here:

**[⬇️ Download APK](https://github.com/Atharva10008/Soundify-Android-Studio/releases/download/untagged-97385987541e93c3554c/app-debug.apk)** <!-- Replace # with your actual APK link or GitHub release URL -->

- After downloading, transfer the APK to your Android device and install it.
- Make sure to enable installation from unknown sources in your device settings if prompted.

---

## Architecture

- **Kotlin + MVVM:**
  - Written in Kotlin, using Android best practices.
- **Navigation Component:**
  - Fragment-based navigation with a single-activity architecture.
- **Firebase:**
  - Authentication, Firestore, and Storage integration.
- **TensorFlow Lite:**
  - On-device ML inference for object detection.
- **Text-to-Speech:**
  - Android TTS API for accessibility.

### Main Components
- `MainActivity.kt`: Hosts the navigation and initializes Firebase.
- `MainFragment.kt`: Core object detection UI and logic.
- `LoginFragment.kt` / `SignupFragment.kt`: Authentication flows.
- `WalkthroughFragment.kt`: Onboarding experience.
- `MyApplicationFragment.kt`: User dashboard and navigation drawer.
- `ml/`: ML model integration and data classes.

---

## Machine Learning Model

- **Model:** SSD MobileNet V1 (TensorFlow Lite)
- **File:** `ssd_mobilenet_v1_1_metadata_1.tflite`
- **Labels:** 80+ classes (see `assets/labels.txt`)
- **Integration:**
  - Uses TensorFlow Lite Support Library for image preprocessing and inference.
  - Custom Kotlin interface for model input/output.
- **Usage:**
  - Loads model and labels at runtime.
  - Processes images and returns bounding boxes, class labels, and confidence scores.

---

## Firebase Integration

- **Authentication:** Email/password login, signup, and password reset.
- **Firestore & Storage:** Ready for user data and image storage (expandable).
- **Configuration:**
  - `google-services.json` included.
  - API keys and project info in `strings.xml`.

---

## Navigation Flow

- **Splash Screen** → **Walkthrough** → **Login/Signup** → **Dashboard** → **Object Detection**
- Navigation handled via Android Navigation Component (`nav_graph.xml`).
- Drawer navigation for dashboard and account actions.

---

## UI & Theming

- **Material Components:** Modern, accessible UI.
- **Custom Colors:**
  - Primary: `#E58D2E`
  - Background: `#DAE2EB`
  - Accent: `#6200EE`
- **Branding:**
  - Custom logo and icons in `res/drawable/`.
- **Dark/Light Support:** Based on Material DayNight theme.

---

## Setup & Installation

1. **Clone the repository:**
   ```bash
   git clone https://github.com/yourusername/Soundify-Android-Studio.git
   cd Soundify-Android-Studio
   ```
2. **Open in Android Studio.**
3. **Configure Firebase:**
   - Ensure `google-services.json` is present in `app/`.
   - Update Firebase project settings if needed.
4. **Build the project:**
   - Minimum SDK: 24
   - Target SDK: 34
   - Kotlin JVM Target: 17
5. **Run on device or emulator.**

---

## Usage

- **Onboarding:** Follow the walkthrough screens on first launch.
- **Authentication:** Sign up or log in with your email.
- **Object Detection:**
  - Use the dashboard to access object detection.
  - Take a picture or select from gallery.
  - View detected objects and tap for more info.
  - Use Play/Pause to hear descriptions.
- **Logout:** Use the navigation drawer or dashboard button.

---

## Project Structure

```
Soundify-Android-Studio/
├── app/
│   ├── src/main/
│   │   ├── java/com/example/soundify/
│   │   │   ├── MainActivity.kt
│   │   │   ├── ml/ (ML integration)
│   │   │   └── ui/ (Fragments: auth, main, walkthrough, dashboard)
│   │   ├── res/ (layouts, drawables, values)
│   │   ├── assets/ (labels.txt)
│   │   └── ml/ (tflite model)
│   ├── build.gradle
│   └── ...
├── build.gradle
├── settings.gradle
└── ...
```

---

## Contributing

Contributions are welcome! Please open issues or pull requests for bug fixes, features, or improvements.

1. Fork the repository.
2. Create a new branch for your feature or fix.
3. Commit your changes with clear messages.
4. Open a pull request describing your changes.

---

## License

This project is licensed under the MIT License. See [LICENSE](LICENSE) for details.
