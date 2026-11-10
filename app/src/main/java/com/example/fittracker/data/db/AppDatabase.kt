package com.example.fittracker.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.fittracker.data.model.Exercise
import com.example.fittracker.data.model.User
import com.example.fittracker.data.model.Workout
import com.example.fittracker.data.model.WorkoutExercise
import com.example.fittracker.data.model.WorkoutSet

@Database(
    entities = [
        User::class,
        Exercise::class,
        Workout::class,
        WorkoutExercise::class,
        WorkoutSet::class
    ],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun exerciseDao(): ExerciseDao
    abstract fun workoutDao(): WorkoutDao
    abstract fun workoutExerciseDao(): WorkoutExerciseDao
    abstract fun workoutSetDao(): WorkoutSetDao

    companion object {
        @Volatile
        private var instance: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase =
            instance ?: synchronized(this) {
                instance ?: Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "fittracker.db"
                ).build().also { instance = it }
            }
    }
}
