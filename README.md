# Weather MVVM

A sample Android weather app used as a hands-on refresher for MVVM architecture, RxJava, Dagger, Room, and Retrofit.

## Purpose

This project exists to practice and reinforce core Android architecture like MVVM and library concepts.

**What the app does:**
1. User selects a city from a predefined list of 10 cities.
2. App requests weather for that city.
3. Shows a loading state.
4. Shows weather data on success.
5. Shows an error state on failure, with a retry action.
6. Maintains a small recent-searches list.
7. Caches results locally: checks cache first (fresh → use cached; stale/missing → hit network and cache the result); falls back to stale cache on network failure if available.
8. Includes a fake/forced-state repository for testing — able to force Success, Error, Empty, and Slow-response scenarios independent of the real network.

**Architectural focus:**
- **Multiple independent observable properties** on the ViewModel (not a single unified state object) — intentional, to feel the tradeoff MVVM has vs. MVI's single state.
- **One-time events** (e.g. a Snackbar on API failure) modeled separately from persistent state via an `Event<T>` wrapper around `LiveData`, so they don't re-fire on rotation/recreation. Persistent error *state* (retry UI) is kept separate from the transient *event* (Snackbar).

This app has a sibling MVI project exploring the same domain with Coroutines/Flow, `StateFlow`, and sealed `Intent`/`UiState` classes — Compose, Hilt, Coroutines/Flow, and MVI are intentionally out of scope here.

## Tech Stack

| Concern | Choice |
|---|---|
| Language | Kotlin |
| UI | XML layouts + ViewBinding (not DataBinding) |
| Architecture | MVVM |
| Async / Reactive | RxJava (`Single`, `Observable`, `Flowable`, `Maybe`, `Completable`) |
| State exposure | LiveData |
| Dependency Injection | Dagger — hand-built component/modules/factory (no Hilt) |
| Networking | Retrofit |
| Weather API | [Open-Meteo](https://api.open-meteo.com/v1/forecast) — free, no API key required |
| City input | Predefined list of 10 cities (name + lat/lon); no free-text search or geocoding |
| Local cache | Room, with RxJava-typed DAO methods (`Flowable`, `Maybe`, `Completable`) |
| Testing | JUnit + MockK; Room in-memory DB for DAO integration tests; Espresso for UI integration tests |

## Project Structure

```
com.dmariani.weathermvvm
│
├── data
│   ├── local
│   │   ├── WeatherDao.kt
│   │   ├── WeatherEntity.kt
│   │   └── WeatherDatabase.kt
│   │
│   ├── remote
│   │   ├── WeatherApi.kt
│   │   └── WeatherResponse.kt
│   │
│   └── repository
│       └── WeatherRepositoryImpl.kt
│
├── domain
│   ├── model
│   │   ├── Weather.kt
│   │   └── City.kt
│   │
│   └── repository
│       └── WeatherRepository.kt
│
├── di
│   ├── AppComponent.kt
│   ├── AppModule.kt
│   ├── NetworkModule.kt
│   ├── DatabaseModule.kt
│   └── ViewModelModule.kt
│
├── ui
│   ├── main
│   │   ├── MainActivity.kt
│   │   ├── WeatherViewModel.kt
│   │   └── Event.kt
│   │
│   └── common
│       └── ViewModelFactory.kt
│
└── WeatherApp.kt
```

**Layering rule:** `domain` has no Android or reactive-library dependencies — it's pure Kotlin (model classes + repository interface only). `data` depends on `domain` and implements the repository interface. `ui` depends on `domain` only (never imports from `data` directly).

## Build Sequence

1. Domain models + repository interface
2. Gradle setup (RxJava, Retrofit, Room, Dagger, MockK)
3. Remote layer (`WeatherApi`, `WeatherResponse`)
4. Local layer (Room: `WeatherEntity`, `WeatherDao`, `WeatherDatabase`)
5. Mappers (Response/Entity → domain `Weather`)
6. `WeatherRepositoryImpl` (cache orchestration)
7. Fake/forced-state repository
8. Dagger wiring (modules, component, factory)
9. `Event<T>` wrapper
10. `WeatherViewModel`
11. UI (`MainActivity`, layouts, ViewBinding)
12. Tests, layer by layer
