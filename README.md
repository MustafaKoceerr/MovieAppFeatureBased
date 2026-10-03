# Movie Discovery App
### Modern Mobile Architecture & Clean Development Practices

[![Kotlin](https://img.shields.io/badge/Kotlin-2.1.0-blue.svg)](https://kotlinlang.org)
[![Jetpack Compose](https://img.shields.io/badge/Jetpack%20Compose-BOM%202025.06.01-green.svg)](https://developer.android.com/jetpack/compose)
[![Architecture](https://img.shields.io/badge/Architecture-MAD%20+%20MVVM-orange.svg)](https://developer.android.com/topic/architecture)
[![Structure](https://img.shields.io/badge/Structure-Package%20by%20Feature-purple.svg)](https://developer.android.com/topic/modularization/patterns)

A movie discovery app built following Modern Android Development (MAD) guidance: a simple MVVM architecture with unidirectional data flow, a small number of modules, and package-by-feature organization.

---

## 📸 App Gallery

<h3>📱 Light Theme Screens</h3>

<table>
  <tr>
    <th style="width:160px;">Splash Screen</th>
    <th style="width:160px;">Welcome Screen</th>
    <th style="width:160px;">Home Screen(1)</th>
    <th style="width:160px;">Home Screen(2)</th>
  </tr>
  <tr>
    <td><img src="assets/splash_light_1.jpg" height="400"/></td>
    <td><img src="assets/welcome_light_1.jpg" height="400"/></td>
    <td><img src="assets/home_light_1.jpg" height="400"/></td>
    <td><img src="assets/home_light_2.jpg" height="400"/></td>
  </tr>
</table>

<table>
  <tr>
    <th style="width:160px;">List Screen(1)</th>
    <th style="width:160px;">List Screen(2)</th>
    <th style="width:160px;">Details Screen(1)</th>
    <th style="width:160px;">Details Screen(2)</th>
  </tr>
  <tr>
    <td><img src="assets/list_light_1.jpg" height="400"/></td>
    <td><img src="assets/list_light_2.jpg" height="400"/></td>
    <td><img src="assets/details_light_1.jpg" height="400"/></td>
    <td><img src="assets/details_light_2.jpg" height="400"/></td>
  </tr>
</table>

<table>
  <tr>
    <th style="width:160px;">Settings Screen(1)</th>
    <th style="width:160px;">Settings Screen(2)</th>
    <th style="width:160px;">Search Screen(1)</th>
    <th style="width:160px;">Search Screen(2)</th>
  </tr>
  <tr>
    <td><img src="assets/settings_light_1.jpg" height="400"/></td>
    <td><img src="assets/settings_light_2.jpg" height="400"/></td>
    <td><img src="assets/search_light_1.jpg" height="400"/></td>
    <td><img src="assets/search_light_2.jpg" height="400"/></td>
  </tr>
</table>
<br/>

<h3>📱 Dark Theme Screens</h3>

<table>
  <tr>
    <th style="width:160px;">Splash Screen</th>
    <th style="width:160px;">Welcome Screen</th>
    <th style="width:160px;">Home Screen(1)</th>
    <th style="width:160px;">Home Screen(2)</th>
  </tr>
  <tr>
    <td><img src="assets/splash_dark_1.jpg" height="400"/></td>
    <td><img src="assets/welcome_dark_1.jpg" height="400"/></td>
      <td><img src="assets/home_dark_1.jpg" height="400"/></td>
    <td><img src="assets/home_dark_2.jpg" height="400"/></td>
  </tr>
</table>

<table>
  <tr>
    <th style="width:160px;">List Screen(1)</th>
    <th style="width:160px;">List Screen(2)</th>
    <th style="width:160px;">Details Screen(1)</th>
    <th style="width:160px;">Details Screen(2)</th>
  </tr>
  <tr>
    <td><img src="assets/list_dark_1.jpg" height="400"/></td>
    <td><img src="assets/list_dark_2.jpg" height="400"/></td>
    <td><img src="assets/details_dark_1.jpg" height="400"/></td>
    <td><img src="assets/details_dark_2.jpg" height="400"/></td>
  </tr>
</table>

<table>
  <tr>
    <th style="width:160px;">Settings Screen(1)</th>
    <th style="width:160px;">Settings Screen(2)</th>
    <th style="width:160px;">Search Screen(1)</th>
    <th style="width:160px;">Search Screen(2)</th>
  </tr>
  <tr>
    <td><img src="assets/settings_dark_1.jpg" height="400"/></td>
    <td><img src="assets/settings_dark_2.jpg" height="400"/></td>
    <td><img src="assets/search_dark_1.jpg" height="400"/></td>
    <td><img src="assets/search_dark_2.jpg" height="400"/></td>
  </tr>
</table>
<br/>

<h3>📱 Other Screens</h3>
<table>
  <tr>
    <th style="width:160px;">Share Content(1)</th>
    <th style="width:160px;">Share Content(2)</th>
    <th style="width:160px;">Error Screen</th>
    <th style="width:160px;">Error Screen</th>
    <th style="width:160px;">Auth Screen</th>
  </tr>
  <tr>
    <td><img src="assets/share_whatsapp_1.jpg" height="400"/></td>
    <td><img src="assets/share_whatsapp_2.jpg" height="400"/></td>
    <td><img src="assets/error_light_1.jpg" height="400"/></td>
    <td><img src="assets/error_dark_1.jpg" height="400"/></td>
    <td><img src="assets/auth_screen.jpg" height="400"/></td>
  </tr>
</table>

<br/>
<h3>📽️ Feature Demonstrations (GIFs)</h3>
<table>
  <tr>
    <th style="width:160px;">Details Feature</th>
    <th style="width:160px;">List Feature</th>
    <th style="width:160px;">Search Feature</th>
  </tr>
  <tr>
    <td><img src="https://github.com/user-attachments/assets/67cae3d1-e0cb-4e41-b3c5-799423076095" height="400"/></td>  
    <td><img src="https://github.com/user-attachments/assets/86b4563e-dba7-4fb0-8852-6401bcfc8751" height="400"/></td>
    <td><img src="https://github.com/user-attachments/assets/39ddbe84-a505-4cce-a123-0bdfc7aeff08" height="400"/></td>
  </tr>
</table>

<br/>

---

## 🚀 Overview

Movie Discovery App allows users to explore, search, and discover movies with a beautiful, fast, and offline-first experience. The app favors simplicity: each screen has one ViewModel exposing one `UiState`, and abstractions are only added where they earn their place.

---

## ✨ Features

### User-Facing Features
- Multi-language support with instant language switching (app & API language)
- Light, dark, and system theme selection with a single tap
- Explore movies by category: Popular, Now Playing, Top Rated, Upcoming
- Powerful search for movies, series, and actors with pagination
- Offline-first experience: fast loading, caching, and pagination (Paging 3)
- Seamless login with token-based authentication (web approval flow)
- Persistent login: token stored securely, auto-login if valid
- Easy logout with a single tap
- Settings screen for theme, language, and account management
- Share movie details with friends
- Error handling with user-friendly messages
- **Smooth transitions and animations for enhanced user experience**
- **Shimmer loading animation on every screen for network data**

### Architectural Highlights
- Modern MVVM: one `StateFlow<UiState>` per screen, plain ViewModel functions for user actions
- One-shot UI events (navigate, open browser, restart) are modeled as state and consumed by the route, so they are never lost
- Stateless screens (state hoisting): `Route` composables connect ViewModel and navigation, `Screen` composables only render
- Three Gradle modules (`app`, `core-data`, `core-ui`) with package-by-feature inside `app`
- Type-safe navigation with `@Serializable` routes; screens receive plain navigation lambdas
- UseCases only where there is real logic (e.g. combining the home categories); otherwise ViewModel → Repository
- Centralized, unified database (Room)
- AppException: unified error handling, surfaced to UI via core-ui
- Offline-first data strategy for home/list screens (cache, fast load)
- Pagination in search & list screens
- Language & API key as interceptors for network requests; secrets are redacted from debug logs
- Enum-based language architecture: add a new language with a single line
- Unit tests for every ViewModel (coroutines-test, Turbine, Mockito) and the network error/log helpers
- Shimmer loading animation for all network-fetched data (user always sees a polished loading state)
- Smooth transitions between screens and UI states

---

## 🛠️ Technologies & Libraries
- **Kotlin** `2.1.0`
- **Jetpack Compose** (UI) `BOM 2025.06.01`
- **Hilt** (Dependency Injection) `2.56.2`
- **Room** (Database) `2.6.1`
- **Retrofit** (Networking) `2.11.0` & **OkHttp** `4.12.0`
- **Paging 3** (Pagination, offline-first) `3.2.1`
- **DataStore** (Preferences) `1.1.7`
- **Kotlin Coroutines & Flow** (Async, reactive) `1.9.0`
- **Coil** (Image loading) `3.2.0`
- **JUnit** `4.13.2`, **Mockito** `5.20.0`, **Turbine** `1.0.0`, **kotlinx-coroutines-test** (Testing)

---

## 🏗️ Project Structure

```
app/                      Application, MainActivity, NavHost, all features, AppDatabase
  └─ feature/
       splash/  auth/  home/  list/  details/  search/  settings/
       movies/            (shared by the movie screens: API, DTOs, entities, mappers, models)
core-data/                Resource/AppException, network, DataStore preferences, Room/Paging base classes
core-ui/                  Theme, shared Compose components, error UI
```

Inside a feature the code is split by layer only where it helps:

```
feature/details/
  data/repository/        MovieDetailsRepository
  presentation/
    screen/               MovieDetailsRoute (wires ViewModel + navigation), MovieDetailsScreen (stateless UI)
    viewmodel/            MovieDetailsViewModel + MovieDetailsUiState
    components/           screen-specific composables
```

### 📦 Module Responsibilities

- **`app`**: *Entry point, navigation graph, DI setup, every feature (screens, ViewModels, repositories, API, Room entities)*
- **`core-data`**: *Shared data foundation: `Resource`, `AppException`, Retrofit/OkHttp setup and interceptors, `safeApiCall`, DataStore preferences (theme, language, session), Room/Paging base classes*
- **`core-ui`**: *Material 3 theme, loading/shimmer and error components shared by all screens*

> The project started as a multi-module experiment (11 modules, MVI). It was later simplified: the project is small, so features live in one module and are separated by package instead.

---

## 🧩 Architectural Summary

- **MVVM + UDF**: *`UI (Screen) → ViewModel (UiState) → [UseCase, only if needed] → Repository → Retrofit / Room / DataStore`*
- **Dependency Injection**: *Hilt (`@HiltViewModel`, constructor injection, `@Provides` for Retrofit/Room)*
- **Offline-First**: *Room as the source of truth, Paging 3 `RemoteMediator` for lists*
- **Type-Safe Navigation**: *`@Serializable` routes; screens get navigation lambdas, not a `NavController`*
- **Unified Error Handling**: *`AppException`, mapped to UI by `core-ui`*
- **Testing**: *ViewModel unit tests with fake flows, Turbine and virtual time*

---

## 🚀 Getting Started

1. Get a TMDB API key and add it to `local.properties`:
   ```
   API_KEY=your_tmdb_api_key
   ```
   Without it the app builds but API calls fail.
2. Use a JDK between 17 and 23 for Gradle (Gradle 8.11 does not support newer JDKs). Android Studio's bundled JDK works.
3. Build and test:
   ```
   ./gradlew assembleDebug
   ./gradlew testDebugUnitTest
   ```

---

## 📝 Planned / Upcoming Features

- ✅ **Multi-language support** *(add new language via enum)*
- ✅ **Offline-first home/list screens**
- ✅ **Unified error handling**
- ✅ **Type-safe navigation**
- ✅ **MVVM migration with ViewModel unit tests**
- 🔜 **Profile feature** *(planned)*
- 🔜 **Push notifications** *(planned)*
- 🔜 **Favorites and rating feature for logged-in users** *(planned)*
- 🔜 **Broader test coverage (repositories, Compose UI tests)** *(planned)*
- ✅ **Per-app language via `AppCompatDelegate.setApplicationLocales`** *(no blocking read at startup)*
- 🔜 **Onboarding flow with ViewPager2** *(planned)*


---

# **🔥 Don’t Just Browse — Explore the Engine Behind the Project!**

**🚀 [Dive into the Full Technical Deep-Dive →](DETAILS.md)**

*Curious about how everything is built? Discover detailed architecture explanations, real-world code examples, and the decisions that power this project. If you want to see the structure and implementation up close, this is the place to start!*

---

## 🎥 Full App Experience (YouTube Video)

If you want to see the full experience of the application in action, check out the video below:

👉 **[Watch Full Demo on YouTube »](https://youtu.be/STPagjB_6XI?si=-vNI7OGCxxwJbAn3)**

---

## 📲 Download & Test the App

Experience the app firsthand on your Android device. You can download the latest release APK directly from the GitHub Releases page.

<p align="center">
  <a href="https://github.com/MustafaKoceerr/MovieAppFeatureBased/releases/tag/v1.0.0" target="_blank">
    <img src="https://img.shields.io/badge/Download-v1.0.0%20APK-brightgreen?style=for-the-badge&logo=android" alt="Download APK from Releases">
  </a>
</p>
