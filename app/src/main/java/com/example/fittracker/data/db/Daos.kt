package com.example.fittracker.data.db

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.example.fittracker.data.model.Exercise
import com.example.fittracker.data.model.User
import com.example.fittracker.data.model.Workout
import com.example.fittracker.data.model.WorkoutExercise
import com.example.fittracker.data.model.WorkoutExerciseWithExercise
import com.example.fittracker.data.model.WorkoutSet
import kotlinx.coroutines.flow.Flow

@Dao
interface UserDao {
    @Insert
    suspend fun insert(user: User): Long

    @Update
    suspend fun update(user: User)

    @Delete
    suspend fun delete(user: User)

    @Query("SELECT * FROM users ORDER BY name")
    fun observeAll(): Flow<List<User>>
}

@Dao
interface ExerciseDao {
    @Insert
    suspend fun insert(exercise: Exercise): Long

    @Update
    suspend fun update(exercise: Exercise)

    @Delete
    suspend fun delete(exercise: Exercise)

    @Query("SELECT * FROM exercises ORDER BY name")
    fun observeAll(): Flow<List<Exercise>>
}

@Dao
interface WorkoutDao {
    @Insert
    suspend fun insert(workout: Workout): Long

    @Update
    suspend fun update(workout: Workout)

    @Delete
    suspend fun delete(workout: Workout)

    @Query("SELECT * FROM workouts WHERE userId = :userId ORDER BY startTime DESC")
    fun observeByUser(userId: Long): Flow<List<Workout>>

    @Query("SELECT * FROM workouts WHERE id = :id")
    fun observeById(id: Long): Flow<Workout?>
}

@Dao
interface WorkoutExerciseDao {
    @Insert
    suspend fun insert(item: WorkoutExercise): Long

    @Update
    suspend fun update(item: WorkoutExercise)

    @Delete
    suspend fun delete(item: WorkoutExercise)

    @Transaction
    @Query("SELECT * FROM workout_exercises WHERE workoutId = :workoutId ORDER BY orderIndex")
    fun observeByWorkout(workoutId: Long): Flow<List<WorkoutExerciseWithExercise>>
}

@Dao
interface WorkoutSetDao {
    @Insert
    suspend fun insert(set: WorkoutSet): Long

    @Update
    suspend fun update(set: WorkoutSet)

    @Delete
    suspend fun delete(set: WorkoutSet)

    @Query(
        "SELECT sets.* FROM sets " +
            "INNER JOIN workout_exercises ON sets.workoutExerciseId = workout_exercises.id " +
            "WHERE workout_exercises.workoutId = :workoutId " +
            "ORDER BY sets.setNumber"
    )
    fun observeByWorkout(workoutId: Long): Flow<List<WorkoutSet>>
}
