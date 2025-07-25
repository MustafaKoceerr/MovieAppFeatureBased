# Movie Discovery App
### Modern Mobile Architecture & Clean Development Practices

[![Kotlin](https://img.shields.io/badge/Kotlin-1.9.0-blue.svg)](https://kotlinlang.org)
[![Jetpack Compose](https://img.shields.io/badge/Jetpack%20Compose-2024.02.00-green.svg)](https://developer.android.com/jetpack/compose)
[![Clean Architecture](https://img.shields.io/badge/Architecture-Clean%20+%20MVI-orange.svg)](https://developer.android.com/topic/architecture)
[![Modular Design](https://img.shields.io/badge/Design-Feature%20Based%20Modules-purple.svg)](https://developer.android.com/guide/app-bundle/play-feature-delivery)

A modern, modular, and robust movie discovery application built with Clean Architecture, best practices, and a focus on scalability and maintainability.

---

## 📸 App Gallery

<!-- Example: 2 static images + 1 gif per row, 3-4 rows. Replace with your own assets. -->

| ![](assets/app_icon.png) | ![](assets/app_icon.png) | ![](assets/auth_video_1_compressed.mp4) |
|-------------------------|-------------------------|-----------------------------------------|
| ![](assets/app_icon.png) | ![](assets/app_icon.png) | ![](assets/list_video_1_compressed.mp4) |
| ![](assets/app_icon.png) | ![](assets/app_icon.png) | ![](assets/theme_video_1_compressed.mp4) |
| ![](assets/app_icon.png) | ![](assets/app_icon.png) | ![](assets/language_video_1_compressed.mp4) |

---

## 🚀 Overview

Movie Discovery App allows users to explore, search, and discover movies with a beautiful, fast, and offline-first experience. The app is designed with a strong focus on modularity, scalability, and clean code principles.

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
- Clean Architecture with strict separation of concerns
- Modularized project structure (feature & core modules)
- Type-safe, encapsulated navigation via navigation-contracts
- Centralized, unified database (Room)
- AppException: unified error handling, surfaced to UI via core-ui
- Offline-first data strategy for home/list screens (cache, fast load)
- Pagination in search & list screens
- Language & API key as interceptors for network requests
- Enum-based language architecture: add a new language with a single line
- UI contracts: consistent, maintainable screen logic
- Best practices applied, pitfalls avoided
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
- **JUnit** `4.13.2`, **Mockito** `5.8.0`, **Espresso** `3.6.1` (Testing)
- **Modular Gradle setup**

---

## 🏗️ Project Structure

```
app/
core-domain/
core-ui/
core-network/
core-database/
core-preferences/
core-android/
feature-movies/
feature-auth/
feature-splash/
navigation-contracts/
```

### Module Responsibilities
- **app**: Application entry, DI setup, navigation host, global config
- **core-domain**: Business logic, domain models, contracts
- **core-ui**: Shared UI components, themes, error UI, UI contracts
- **core-network**: Network layer, API config, interceptors
- **core-database**: Room DB, DAOs, caching
- **core-preferences**: DataStore, user preferences (theme, language, token)
- **core-android**: Android-specific utilities, base ViewModel
- **feature-movies**: Home, list, details, search screens & logic
- **feature-auth**: Authentication (login, logout, token flow)
- **feature-splash**: Splash screen, initial loading
- **navigation-contracts**: Type-safe navigation contracts, decoupled navigation

---

## 🧩 Architectural Summary
- **Clean Architecture**: Strict separation (domain, data, presentation)
- **MVI Pattern**: Unidirectional data flow, UI contracts for all screens
- **Dependency Injection**: Hilt for scalable, testable DI
- **Modularization**: Each feature/core is a separate Gradle module
- **Offline-First**: Caching, fast load, pagination
- **Type-Safe Navigation**: navigation-contracts for decoupled, safe navigation
- **Unified Error Handling**: AppException, surfaced to UI
- **Best Practices**: Naming, file structure, code quality

---

## 📝 Planned/Upcoming Features
- [x] Multi-language support (add new language via enum)
- [x] Offline-first home/list screens
- [x] Unified error handling
- [x] Modular navigation
- [ ] Profile feature (planned)
- [ ] Push notifications (planned)
- [ ] Favorites and rating feature for logged-in users (planned)
- [ ] Comprehensive testing (planned)
- [ ] Onboarding flow with ViewPager2 (planned)

---

---

# **🔥 Don’t Just Browse — Explore the Engine Behind the Project!**

**🚀 [Dive into the Full Technical Deep-Dive →](DETAILS.md)**

*Curious about how everything is built? Discover detailed architecture explanations, real-world code examples, and the decisions that power this project. If you want to see the structure and implementation up close, this is the place to start!*

---
