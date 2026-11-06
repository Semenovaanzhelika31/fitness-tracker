# FitTracker — трекер спортивных упражнений

Android-приложение для спортсменов (Kotlin, Jetpack Compose).

## Задание №1. Пользовательский интерфейс и навигация

Реализован каркас приложения и навигация между экранами-заглушками.

### Архитектура

- Single-Activity + Jetpack Compose (Material 3)
- MVVM: у каждого экрана свой `ViewModel` с `StateFlow<UiState>`
- Navigation Compose, нижняя панель (`NavigationBar`)
- Version Catalog (`gradle/libs.versions.toml`)

```
com.example.fittracker
├── MainActivity.kt
└── ui
    ├── FitTrackerApp.kt          # Scaffold + BottomNavigation
    ├── navigation
    │   ├── Screen.kt             # маршруты, иконки, заголовки
    │   └── AppNavHost.kt         # граф навигации
    ├── components
    │   └── PlaceholderScreen.kt  # общий макет заглушки
    ├── screens
    │   ├── exercises             # список упражнений
    │   ├── workout               # тренировка (таймер)
    │   └── history               # история тренировок
    └── theme
        └── Theme.kt
```

### Экраны (заглушки)

| Экран | Будущий функционал |
|---|---|
| Упражнения | список упражнений с описанием из сети |
| Тренировка | анимированный таймер обратного отсчёта, подходы |
| История | история тренировок |

### Планируется в следующих заданиях

Сущности `User`, `Exercise`, `Workout`, `WorkoutExercise`, `Set` (Room), сеть (Retrofit), уведомления-напоминания, медиа (анимации, видео, фотоотчёты).

## Сборка

Откройте папку в Android Studio (Ladybug или новее, JDK 17), дождитесь синхронизации Gradle и запустите конфигурацию `app`.
