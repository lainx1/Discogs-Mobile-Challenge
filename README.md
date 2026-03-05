# MobileChallenge (Discogs)

Android app built for the mobile challenge using **Kotlin + Clean Architecture + MVI + Hilt + Retrofit + Paging + Jetpack Compose**.

## 1) Setup and Project Configuration

### Prerequisites
- **Android Studio**: recent stable version (Koala/Ladybug or newer).
- **JDK**: **17** for Gradle/AGP runtime (project compiles Java 11 bytecode).
- **Gradle**: use wrapper included in project (`gradle-9.2.1`).
- **Android SDK**:
  - `minSdk = 24` (Android 7.0)
  - `targetSdk = 36`

### Local configuration (`local.properties`)
1. Copy values from example: [local.properties.example](https://github.com/lainx1/Discogs-Mobile-Challenge/blob/master/local.properties.example)
2. Create/update your `local.properties` file in project root

3. local.properties values are injected into `BuildConfig` from `app/build.gradle.kts`.

### Build and run (debug)
```bash
./gradlew :app:assembleDebug
./gradlew :app:installDebug
```
Or run from Android Studio using the `app` configuration.

### Testing

#### Unit tests
```bash
./gradlew :app:testDebugUnitTest
```

#### Instrumented tests (Compose UI tests)
```bash
./gradlew :app:connectedDebugAndroidTest
```
Requires an emulator/device connected and unlocked.

### Static analysis

#### ktlint
```bash
./gradlew :app:ktlintCheck
./gradlew :app:ktlintFormat
```
- `ktlintCheck`: reports style violations.
- `ktlintFormat`: auto-formats fixable style issues.

#### detekt
```bash
./gradlew :app:detekt
```
Configured in:
- `app/build.gradle.kts`
- `config/detekt/detekt.yml`

Ktlint and detekt reports are generated under `app/build/reports/`

Current behavior:
- `ignoreFailures = true` and high `maxIssues` -> detekt findings are currently **informational/non-blocking**.
- `ktlintCheck` should be treated as **blocking** for style quality (especially in CI), while `ktlintFormat` is the quick fix step.
- Detekt-driven cleanup/fixes were applied in this commit: [ea54e18](https://github.com/lainx1/Discogs-Mobile-Challenge/commit/ea54e18bd4ec720856e125a481187a964c92d598).


### Common setup pitfalls
- Missing/invalid [local.properties](https://github.com/lainx1/Discogs-Mobile-Challenge/blob/master/local.properties.example) values will cause network setup/runtime errors and API auth failures.
- Running instrumented tests without a booted device/emulator fails immediately.
- JDK mismatch (not using JDK 17 for Gradle runtime) may break sync/build.
- If formatting seems not applied, run `:app:ktlintFormat` (not only `ktlintCheck`).

---

## 2) Brief Development Process

### Step 1: Search
- Implemented artist search screen with debounced query handling.
- Paging used for large result sets.
- Search state modeled with MVI (`Idle/Loading/Success/Error`) to keep rendering deterministic.

### Step 2: Artist detail
- Added detail screen driven by artist ID route argument.
- ViewModel handles loading/retry/error mapping and one-shot navigation effects.
- Detail state modeled with MVI (`Loading/Success/Error`) to keep rendering deterministic.

### Step 3: Albums/Releases + filters
- Implemented releases search by artist name using Discogs search endpoint.
- Added filters (`year`, `genre`, `label`) and reload behavior on filter changes.
- Added local sorting strategy by year (details below).
- Albums/Releases state modeled with MVI (`Loading/Success/Error`) to keep rendering deterministic.

### Key decisions and trade-offs
- Used `search` endpoint for filter support, sacrificing server-side year sorting.
- Chose local per-page sorting for predictable UI ordering with current endpoint limits.
- Kept MVI/effects approach for testable state transitions and navigation side effects.

---

## 3) Architecture and Rationale

### Stack
- **Clean Architecture**
- **MVI state management**
- **Hilt** for DI
- **Retrofit + OkHttp** for networking
- **Paging 3**
- **Jetpack Compose**

### Layers and responsibilities
- **UI layer** (`ui/...`)
  - Compose screens, UI components, ViewModels, UI state/events/effects.
- **Domain layer** (`domain/...`)
  - Use cases + domain models + repository interfaces.
- **Data layer** (`data/...`)
  - Repository implementations, Retrofit API, DTOs, mappers, paging sources, interceptors.

### Dependency direction
- `ui -> domain -> data`
- Domain is framework-light and depends on abstractions.
- Data implements domain repository contracts.

### Why this architecture
- **Maintainability**: clear boundaries and smaller change impact.
- **Testability**: ViewModels/use cases/paging/interceptors are unit-test friendly.
- **Scalability**: features can grow without collapsing concerns into UI classes.

---

## 4) API Constraints and Sorting Strategy

Discogs API docs: https://www.discogs.com/developers/#

### Constraints
- The chosen search-based releases flow uses `GET /database/search` with filters (`artist`, `year`, `genre`, `label`).
- This endpoint supports required filters but does **not** provide the needed year ordering behavior for this use case.

### Why `/artists/{artist_id}/releases{?sort,sort_order}` was not used
- It supports sorting options, but does not support the full required filter set (`year`, `genre`, `label`) needed by the assignment flow.

### Implemented partial solution (local sorting by year)
- In `ArtistReleasesPagingSource`, each fetched page is sorted locally:
  - Primary: `year` descending
  - Secondary: `title` ascending
  - Tertiary: `id` ascending

### Limitations
- Sorting is **per page**, not globally across all pages from server pagination.
- If older/newer items are split across pages, global order may not be perfect until more pages load.

---

## 5) Additional Sections

### Feature list (assignment-aligned)
- Artist search screen with debounced input.
- Artist detail screen.
- Artist releases screen with filters (`year`, `genre`, `label`).
- Loading/empty/error/success UI states.
- Compose navigation between the 3 screens.
- Unit tests for core layers and basic Compose instrumented UI tests.

### Error handling notes
- Network/interceptor layer maps timeout/no-internet/auth-like failures to domain-friendly errors.
- ViewModels map throwables to user-facing messages via `ErrorMessageMapper`.
- UI exposes retry paths in error states and uses one-shot effects for navigation.

### Suggested future improvements
- Improve static code analyzer tools configuration
- Improve release ordering with a backend-assisted/global merge strategy if strict sort is mandatory.
- Expand UI test coverage for filters/paging edge cases.
