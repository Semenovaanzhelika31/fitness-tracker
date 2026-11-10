package com.example.fittracker.data.model

import androidx.room.Embedded
import androidx.room.Relation

/** Упражнение тренировки вместе с данными упражнения из каталога. */
data class WorkoutExerciseWithExercise(
    @Embedded val workoutExercise: WorkoutExercise,
    @Relation(parentColumn = "exerciseId", entityColumn = "id")
    val exercise: Exercise
)
