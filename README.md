# PhotoCatalog 📸

A modern, **Offline-First** Android application built with **Jetpack Compose** and **Clean Architecture**. It demonstrates robust data synchronization, efficient list rendering, and resilient error handling.

![Kotlin](https://img.shields.io/badge/Kotlin-1.9.0-blue?logo=kotlin)
![Jetpack Compose](https://img.shields.io/badge/UI-Jetpack%20Compose-green?logo=android)
![Architecture](https://img.shields.io/badge/Arch-Clean%20%2B%20MVVM-orange)
![Status](https://img.shields.io/badge/Status-Release%20Candidate-success)

## 📱 Features

- **Infinite Grid Catalog:** Efficient rendering of thousands of items using **Paging 3** with a responsive Grid layout (`LazyVerticalGrid`).
- **Offline-First Architecture:** Full functionality without internet access. **Room Database** acts as the Single Source of Truth (SSOT).
- **Smart Synchronization:**
  - **Reactive Banner:** Real-time feedback on network status ("Offline", "Syncing", "Online", "Updated just now").
  - **Background Sync:** Periodic data freshness checks using **WorkManager**.
  - **Visual Feedback:** Minimized loading flicker with optimistic updates and minimum visual loading times.
- **Interactive Detail View:**
  - **Zoom & Pan:** Native pinch-to-zoom gesture support for high-quality image inspection.
  - **Navigation:** Seamless transitions using Compose Navigation.
- **Resilience:**
  - Robust error handling for network failures (401, 404, Timeout).
  - **Image Sanitization:** Automatic detection and correction of SVG URLs to PNG for optimal Android rendering.

## 🏗️ Architecture

The project follows strict **Clean Architecture** principles, distributed across three Gradle modules to ensure separation of concerns and scalability:

### 1. `:app` (Presentation Layer)
- **Pattern:** MVVM (Model-View-ViewModel).
- **State Management:** `StateFlow` combined with `combine` operators for reactive UI updates.
- **UI:** 100% Jetpack Compose with Material 3 Design.
- **State Hoisting:** Components like `PhotoDetailContent` are stateless to facilitate isolated UI testing (`@ComposeTestRule`).

### 2. `:domain` (Business Logic Layer)
- Pure Kotlin module (No Android dependencies).
- **Use Cases:** Encapsulate business rules (e.g., `GetSyncStatusUseCase` determines data freshness logic).
- **Models:** Domain models independent of API or Database implementation details.

### 3. `:data` (Data Layer)
- **Repository Pattern:** Coordinates data fetching strategies.
- **Paging 3:** Implements `RemoteMediator` for seamless caching and pagination.
- **Persistence:** Room Database for local storage and DataStore for preferences.
- **Network:** Retrofit + Moshi for API communication.

## 🛠️ Tech Stack

* **Language:** [Kotlin](https://kotlinlang.org/)
* **Dependency Injection:** [Hilt](https://dagger.dev/hilt/)
* **Async:** [Coroutines](https://kotlinlang.org/docs/coroutines-overview.html) & [Flow](https://kotlinlang.org/docs/flow.html)
* **Network:** [Retrofit](https://square.github.io/retrofit/) & [OkHttp](https://square.github.io/okhttp/)
* **Serialization:** [Moshi](https://github.com/square/moshi)
* **Image Loading:** [Coil](https://coil-kt.github.io/coil/) (Optimized with custom caching policy)
* **Local Storage:** [Room](https://developer.android.com/training/data-storage/room) & [DataStore](https://developer.android.com/topic/libraries/architecture/datastore)
* **Pagination:** [Paging 3](https://developer.android.com/topic/libraries/architecture/paging/v3)
* **Background Work:** [WorkManager](https://developer.android.com/topic/libraries/architecture/workmanager)
* **Testing:** JUnit4, MockK, Turbine, Compose UI Test.

## 💡 Key Technical Decisions (ADR)

### 1. SVG to PNG On-the-fly Conversion
**Context:** The API provider (`placehold.co`) returns SVG images by default if no extension is provided. Coil requires extra heavy decoders for SVG.
**Decision:** Implemented a `sanitizeImageUrl` utility in the Data layer. It detects specific provider URLs and injects the `.png` extension before requesting.
**Result:** Faster image rendering, reduced APK size (no SVG decoder needed), and cleaner UI.

### 2. Removing "Favorites" for Stability
**Context:** Implementing a local "Favorite" feature on top of a paginated remote list (`RemoteMediator`) caused UI flickering due to full list invalidation by Room.
**Decision:** Prioritized application stability and performance over feature quantity. The feature was removed for the release candidate to ensure a rock-solid scrolling experience without visual artifacts.

### 3. Custom Coil Configuration
**Context:** Default image loaders can consume uncontrolled amounts of disk/RAM.
**Decision:** Implemented `ImageLoaderFactory` in `CatalogApplication`.
- **RAM Limit:** 25% of available memory.
- **Disk Limit:** 100MB hard limit.
- **Policy:** `respectCacheHeaders(true)` to allow server-side image updates while maintaining offline capabilities.

## 🧪 Testing Strategy

The project includes a comprehensive test suite:

* **Unit Tests (`test`):** Verify ViewModels, UseCases, and Utility logic.
    * *Tools:* MockK, Turbine.
* **UI Tests (`androidTest`):** Verify UI rendering, navigation, and component interactions.
    * *Tools:* Compose Test Rule, Espresso.

Run tests via command line:
```bash
./gradlew testDebugUnitTest
./gradlew connectedDebugAndroidTest
