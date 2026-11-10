package com.example.fittracker.data.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

/** Пользователь. */
@Entity(tableName = "users")
data class User(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val goal: Goal = Goal.MAINTENANCE
)

/** Упражнение из каталога. */
@Entity(tableName = "exercises")
data class Exercise(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val description: String = "",
    val category: ExerciseCategory = ExerciseCategory.STRENGTH,
    val muscleGroup: String = "",
    /** Сложность от 1 до 5. */
    val difficulty: Int = 1,
    val animationUrl: String = "",
    val videoUrl: String = ""
)

/** Тренировка пользователя. Время хранится в миллисекундах (epoch). */
@Entity(
    tableName = "workouts",
    foreignKeys = [
        ForeignKey(
            entity = User::class,
            parentColumns = ["id"],
            childColumns = ["userId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("userId")]
)
data class Workout(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val userId: Long,
    /** Начало дня тренировки. */
    val date: Long,
    val startTime: Long,
    val endTime: Long,
    val durationSeconds: Long,
    val notes: String = ""
)

/** Упражнение в составе тренировки. */
@Entity(
    tableName = "workout_exercises",
    foreignKeys = [
        ForeignKey(
            entity = Workout::class,
            parentColumns = ["id"],
            childColumns = ["workoutId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = Exercise::class,
            parentColumns = ["id"],
            childColumns = ["exerciseId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("workoutId"), Index("exerciseId")]
)
data class WorkoutExercise(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val workoutId: Long,
    val exerciseId: Long,
    val orderIndex: Int
)

/** Подход (Set). Назван WorkoutSet, чтобы не конфликтовать с kotlin.collections.Set. */
@Entity(
    tableName = "sets",
    foreignKeys = [
        ForeignKey(
            entity = WorkoutExercise::class,
            parentColumns = ["id"],
            childColumns = ["workoutExerciseId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("workoutExerciseId")]
)
data class WorkoutSet(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val workoutExerciseId: Long,
    val setNumber: Int,
    val reps: Int = 0,
    val weightKg: Double = 0.0,
    /** Для кардио. */
    val durationSeconds: Int = 0,
    val distanceMeters: Int = 0
)
