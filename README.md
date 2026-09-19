# Product List (Android / Kotlin / Jetpack Compose / MVVM)

Fulfills the brief: fetch `GET https://dummyjson.com/products`, decode
`id`, `title`, `description`, `thumbnail`, show them in a scrolling list
with Loading / Content / Empty / Error states, a Retry action, and
local save/unsave with no duplicates — built with Kotlin, Compose,
coroutines, and a ViewModel, with UI/ViewModel/data-access responsibilities
kept in separate layers and a fakeable data source for testing.

## 1. Where each requirement is covered

| Requirement | File(s) |
|---|---|
| Kotlin, Compose, coroutines, ViewModel | `ui/productlist/ProductListViewModel.kt`, `ui/productlist/ProductListScreen.kt` |
| GET dummyjson.com/products | `data/remote/ProductApiService.kt`, `data/remote/ProductRemoteDataSource.kt`, wired to Retrofit in `ProductListApp.kt` |
| Decode id/title/description/thumbnail | `data/model/Product.kt`, `data/model/ProductsResponse.kt` |
| Scrolling list | `ProductListScreen.kt` → `ContentState` (`LazyColumn`) |
| Loading / Content / Empty / Error states | `ui/productlist/ProductListUiState.kt` (sealed interface) + the `when` in `ProductListScreen` |
| Retry after failure | `ErrorState` composable's `Button(onClick = onRetry)` → `ProductListViewModel.loadProducts()` |
| Save/unsave locally, no duplicate saves | `data/local/SavedProductsDataSource.kt` (`Map<Int, Product>` keyed by id) |
| UI / ViewModel / data-access separation | `ui/` (View) → `ProductListViewModel` (ViewModel) → `data/repository/ProductRepository` (Model) — the ViewModel never imports Retrofit or the local store directly |
| Fakeable/mockable data source | `data/remote/ProductRemoteDataSource.kt` + `data/local/SavedProductsDataSource.kt` are interfaces; `src/test/.../FakeProductRemoteDataSource.kt` and `FakeSavedProductsDataSource.kt` implement them for tests |
| JUnit tests | `src/test/.../data/repository/ProductRepositoryImplTest.kt`, `src/test/.../ui/productlist/ProductListViewModelTest.kt` |

## 2. Architecture (MVVM)

```
ProductListScreen (View, stateless Composables)
        ↑ state           ↓ events
ProductListRoute (View, stateful — collects StateFlow)
        ↑ state           ↓ calls
ProductListViewModel      (ViewModel — exposes StateFlow<ProductListUiState>)
        ↓ calls
ProductRepository          (Model — the only thing the ViewModel depends on)
        ↓ calls                         ↓ calls
ProductRemoteDataSource    SavedProductsDataSource
(Retrofit → dummyjson.com) (in-memory map, keyed by product id)
```

Every arrow points at an **interface**, not a concrete class, which is
what makes each layer replaceable and each layer above it testable in
isolation.

## 3. Running the unit tests

```bash
./gradlew test
```

## 4. Generating a test coverage report (JaCoCo)

A `jacocoTestReport` task is configured in `app/build.gradle.kts`
(JaCoCo 0.8.12), scoped to the `debug` unit test run so ViewModel and
repository coverage is measured end to end.

Run:

```bash
./gradlew jacocoTestReport
```

This runs `testDebugUnitTest` first, then produces:

- HTML report: `app/build/reports/jacoco/jacocoTestReport/html/index.html`
- XML report (for CI tools like Codecov/SonarQube): `app/build/reports/jacoco/jacocoTestReport/jacocoTestReport.xml`

Open the HTML `index.html` in a browser; drill into
`com.example.productlist.data.repository` and
`com.example.productlist.ui.productlist` to see line/branch coverage
for `ProductRepositoryImpl` and `ProductListViewModel` specifically —
those two classes are the ones exercised by the test suite in this
exercise. `data/remote` and `data/local` show lower coverage since their
*fakes* (not the Retrofit/in-memory implementations) are what the tests
exercise directly — that's expected and is the point of the fakeable
data source pattern.

For a single command that both runs tests and opens the door to CI
enforcement, you can also add a minimum-coverage gate with JaCoCo's
`violationRules` — not included here to keep the build file focused on
what the brief asked for, but a natural next step.
