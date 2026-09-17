# Android Pokedex

[![Codecov](https://codecov.io/gh/santimattius/android-basic-skeleton/branch/master/graph/badge.svg?token=HNW9TXKMQU)](https://codecov.io/gh/santimattius/android-basic-skeleton)
[![Quality Checks](https://github.com/santimattius/android-basic-skeleton/actions/workflows/main.yml/badge.svg)](https://github.com/santimattius/android-basic-skeleton/actions)
[![AGP 9.4.0](https://img.shields.io/badge/AGP-9.4.0-blue.svg)](https://developer.android.com/build/releases/gradle-plugin)
[![Kotlin 2.4.20](https://img.shields.io/badge/Kotlin-2.4.20-purple.svg)](https://kotlinlang.org/docs/whatsnew20.html)

A Pokédex Android app built on top of a production-ready Kotlin/Compose skeleton, consuming [PokéAPI](https://pokeapi.co/) to browse and inspect Pokémon. Built as a staged, spec-driven implementation with strict TDD across every layer.

## 🚀 Key Features

- **Pokémon catalog**: paginated list (Jetpack Paging 3) with a decorative per-item accent color, artwork via Coil.
- **Pokémon detail**: type-tinted header, weight/height, base stats with progress bars, capture difficulty and legendary/mythical flags aggregated from PokéAPI's detail + species endpoints.
- **Offline-first detail cache**: Room-backed 24h freshness cache — a previously viewed Pokémon stays available (and falls back silently on network failure) once cached.
- **Modern Tech Stack**: Jetpack Compose, Navigation 3, Hilt (incl. assisted injection), Coroutines/Flow, Paging 3, Room.
- **Robust Networking**: Retrofit with Gson and OkHttp integration.
- **Built-in Quality Control**: Integrated Detekt for static analysis and Jacoco for code coverage.
- **AGP 9.0+ Ready**: Configured with the latest Android Gradle Plugin defaults, including built-in Kotlin support and New DSL interfaces.
- **CI/CD Integrated**: Pre-configured GitHub Actions for automated testing and secret management.

## 🏗 Architecture & Design

The project follows modern Android development patterns:
- **Dependency Injection**: Hilt for compile-time safe DI (incl. `@AssistedInject` for the detail ViewModel, which takes the navigated Pokémon name at creation time).
- **UI Framework**: 100% Jetpack Compose for a reactive UI, navigated with Navigation 3.
- **Asynchronous Programming**: Kotlin Coroutines and Flow for seamless data handling; concurrent detail+species fetch via `coroutineScope { async }`.
- **Persistence**: Room for the offline detail cache, with `java.time` support via core library desugaring.
- **Image Loading**: Coil for efficient image loading in Compose.
- **Layering**: `data` (remote DTOs/services/repositories, local Room cache) → `domain` (pure models + `GetPokemonProfile` interactor) → `presentation` (per-screen UI models, mappers, ViewModels, Composables).

## 🗺 Implementation Stages

Built as a staged, spec-driven (SDD) implementation on top of the base skeleton, one capability per stage, each independently tested and shipped:

| Stage | Capability | What it added |
| :--- | :--- | :--- |
| 1 | `pokemon-detail` | Package rename to `com.santimattius.pokedex`; remote Pokémon detail lookup (Retrofit + PokéAPI), domain model, detail ViewModel/Composable. |
| 2 | `pokemon-offline-cache` | Room-backed local cache for Pokémon detail with a 24h freshness window and silent fallback on network failure. |
| 3 | `pokemon-profile` (UI mapping) | Pure UI mapper/model layer between domain and Compose (`PokemonUiMapper`, `PokemonTypeStyle`), decoupling the screen from raw domain types. |
| 4 | `pokemon-profile` (aggregation) | Species data + concurrent detail/species fetch (`GetPokemonProfile`), capture difficulty, legendary/mythical flag, total base stats. |
| 5 | `pokemon-catalog` | Paginated Pokémon list (Paging 3) and Navigation 3 wiring between list and detail. |
| 6 | UI polish | Weight/height display, type-tinted detail header and chips, decorative list-card accent colors, and Coil artwork rendering (previously unused). |

## 🛠 Project Structure

```text
├── app/                  # Main application module
│   ├── src/main/java/    # Source code (data/domain/presentation layers, Hilt DI, Navigation 3)
│   ├── src/test/         # Unit + Robolectric tests
│   ├── src/androidTest/  # Instrumented tests (Hilt, device-only)
│   ├── src/screenshotTest/ # Compose Preview Screenshot tests
│   └── build.gradle.kts  # App-specific build configuration (incl. Jacoco)
├── config/               # Configuration files (Detekt, etc.)
├── docs/
│   └── testing.md        # Testing strategy: analysis, plan, and what's implemented
├── gradle/               # Gradle scripts and version catalog
│   └── libs.versions.toml # Centralized dependency management
├── AGENTS.md             # Entry point for agent-facing project docs
└── plugins/              # Custom build plugins
```

## 🚦 Getting Started

### Prerequisites
- Android Studio Ladybug | 2024.2.1 or newer
- JDK 17 (configured in Gradle settings)

### Verification & Testing

Run project-wide quality checks:
```bash
./gradlew check
```

Execute unit tests (includes Robolectric-based Compose behavior tests):
```bash
./gradlew :app:testDebugUnitTest
```

Execute instrumented tests (requires a connected device or emulator):
```bash
./gradlew :app:connectedDebugAndroidTest
```

Run static analysis (Detekt):
```bash
./gradlew :app:detekt
```

### Screenshot Testing

Update reference images after an intentional UI change:
```bash
./gradlew :app:updateDebugScreenshotTest
```

Validate the current UI against the committed reference images:
```bash
./gradlew :app:validateDebugScreenshotTest
```
*Reference images live at: `app/src/screenshotTestDebug/reference/`*

### Code Coverage Reports

Generate a Jacoco coverage report from unit tests:
```bash
./gradlew :app:jacocoTestReport
```
*Report is generated at: `app/build/reports/jacoco/jacocoTestReport/html/index.html`*

See [docs/testing.md](docs/testing.md) for the full testing strategy.

## 🔐 Configuration & Secrets

### Local Secret Management
The project uses the `secrets-gradle-plugin`. To define API keys or sensitive data:

1. Add your key to `local.properties`:
   ```properties
   apiKey="your_api_key_here"
   ```
2. Access it in code:
   ```kotlin
   val apiKey = BuildConfig.apiKey
   ```

### AGP 9.0 Note
`buildConfig` is explicitly enabled in `app/build.gradle.kts` to maintain compatibility with modern plugin standards.

## 📦 Dependencies

| Category | Libraries |
| :--- | :--- |
| **UI** | Jetpack Compose (BOM), Material 3, Navigation 3, Coil |
| **DI** | Hilt (incl. assisted injection) |
| **Async** | Coroutines, Flow |
| **Networking** | Retrofit, Gson, OkHttp |
| **Persistence** | Room, core library desugaring |
| **Pagination** | Paging 3 |
| **Testing** | JUnit 4, MockK, MockWebServer, Robolectric, Hilt Testing, Compose Test, Compose Preview Screenshot Testing, Jacoco |

## 🎮 Data Source

Pokémon data is fetched live from [PokéAPI](https://pokeapi.co/) (`https://pokeapi.co/api/v2/`) — no API key required.

---
Maintainer: [Santiago Mattiauda](https://github.com/santimattius)
