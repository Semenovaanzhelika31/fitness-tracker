package com.example.fittracker.data.model

/** Цель пользователя. Room сохраняет enum как строку (имя константы). */
enum class Goal(val title: String) {
    WEIGHT_LOSS("Похудение"),
    MUSCLE_GAIN("Набор массы"),
    MAINTENANCE("Поддержка")
}

/** Категория упражнения. */
enum class ExerciseCategory(val title: String) {
    STRENGTH("Силовое"),
    CARDIO("Кардио"),
    STRETCHING("Растяжка")
}
