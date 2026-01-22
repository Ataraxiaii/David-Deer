# David Deer - Mythical Beast Explorer Application

[![Build Tool](https://img.shields.io/badge/Build-Gradle-blue.svg)](https://gradle.org/)
[![Language](https://img.shields.io/badge/Language-Kotlin-orange.svg)](https://developer.android.com/hl)
[![Platform](https://img.shields.io/badge/Platform-Android-green.svg)](https://www.android.com/)
[![Focus](https://img.shields.io/badge/Focus-Gaming-blueviolet.svg)](#)

**David Deer** is an innovative mobile application that seamlessly blends real-world map exploration, mythical beast capturing, and fitness tracking. Players can immerse themselves in the adventure of discovering legendary creatures while maintaining a healthy lifestyle through customized walking plans.

<p align="center">
  <img src="./images/logo.png" width="180px" />
</p>

---

## 🎮 Game Experience & Features

### 1. Start Page
Our start page changes according to the current time and weather.

<p align="center">
  <img src="./images/snow.png" width="200px" />
  <img src="./images/night.jpeg" width="200px" />
  <img src="./images/day.jpeg" width="198px" />
  <img src="./images/fog.png" width="201px" />
</p>

Also, you can choose your favourite beast on the start page.

<p align="center">
  <img src="./images/choose_favourite.jpeg" width="199px" />
  <img src="./images/favourite_yinglong.jpeg" width="198px" />
</p>

The game also provides users with detailed gaming manual.

<p align="center">
  <img src="./images/game_manual.png" width="500px" />
</p>

---

### 2. Map Exploration & Real-Time Tracking
The adventure unfolds on a dynamic map interface. By leveraging GPS technology, the system generates mythical beasts in your vicinity. The map has two modes: normal and staellite mode.

<p align="center">
  <img src="./images/normal_mode.jpeg" width="200px" />
  <img src="./images/satellite_mode.jpeg" width="198px" />
</p>

When you press the red button, the beasts are generated around you.

<p align="center">
  <img src="./images/generate_creature.jpeg" width="200px" />
</p>

The system will check the distance between you and beasts.

<p align="center">
  <img src="./images/go_capture.jpeg" width="200px" />
  <img src="./images/go_closer.jpeg" width="200px" />
</p>

---

### 3. The Capture Mechanic (AR Integration)
Once you are within range of a beast, the "Open Camera" mode activates. This feature merges the digital and physical worlds, allowing you to take a "capture photo" with the beast. A successful photo adds the creature to your permanent collection.

<p align="center">
  <img src="./images/camera_permission.jpeg" width="201px" />
  <img src="./images/camera.jpeg" width="199px" />
  <img src="./images/captured.jpeg" width="198px" />
</p>

After catching the beast, you can see the picture in your albulm.

<p align="center">
  <img src="./images/photo.jpeg" width="201px" />
  <img src="./images/photo_1.jpeg" width="200px" />
  <img src="./images/photo_2.jpeg" width="200px" />
</p>

---

### 4. Beast Archive & Encyclopedia
Knowledge is power. The **Beast Archive** serves as your personal encyclopedia, where you can browse captured creatures, search for specific species, and unlock detailed lore and statistics for every beast you encounter.

<p align="center">
  <img src="./images/archive.jpeg" width="200px" />
  <img src="./images/search_creature.jpeg" width="200px" />
</p>

There are in total three satges of beasts: locked, unlocked but not captured and already captured beast.

<p align="center">
  <img src="./images/zhuque.jpeg" width="200px" />
  <img src="./images/no_capture_creature.jpeg" width="200px" />
  <img src="./images/lock_creature.jpeg" width="200px" />
</p>

---

### 5. Walking Plans & Pedometry
David Deer turns exercise into a rewarding part of the game. The built-in **Pedometer** tracks your daily movement, while the **Walking Plan** module allows you to set, modify, and apply fitness goals that complement your exploration progress.

<p align="center">
  <img src="./images/step_1.jpeg" width="199px" />
  <img src="./images/step_2.jpeg" width="200px" />
  <img src="./images/step_3.jpeg" width="201px" />
</p>

Also, the app will show the history data. You can choose specific date as well.

<p align="center">
  <img src="./images/history_1.jpeg" width="200px" />
  <img src="./images/history_2.jpeg" width="199px" />
</p>

For the exercise plan, you can also add, edit and delete custom settings.

<p align="center">
  <img src="./images/plan_1.jpeg" width="200px" />
  <img src="./images/plan_2.jpeg" width="200px" />
  <img src="./images/plan_3.jpeg" width="200px" />
</p>

---

### 6. Two Simple Games
The app designs two simple games to help users unlock the beasts. If you pass the game successfully, the app will unlock four different beasts.

<p align="center">
  <img src="./images/game.jpeg" width="200px" />
  <img src="./images/level1.jpeg" width="200px" />
  <img src="./images/game_play.jpeg" width="200px" />
  <img src="./images/game_win.jpeg" width="200px" />
</p>

---

## 🛠️ Technical Specifications

* **Build System**: **Maven/Gradle** for dependency management.
* **Core APIs**: 
    * **Gaode Maps SDK**: For location-based creature spawning.
    * **Camera API**: For the interactive capture experience.
    * **Android Sensor Manager**: To power the real-time step counter.
    * **Weather API**: For location-based weather changing.

## 📦 Getting Started

1. **Clone** the repository.
2. Open the project in **Android Studio**.
3. **Build** the project to download necessary dependencies.
4. **Grant Permissions**: Ensure Location (GPS), Camera, and Physical Activity permissions are enabled on your device.
