package com.example.fittracker.data

import com.example.fittracker.data.db.AppDatabase
import com.example.fittracker.data.model.Exercise
import com.example.fittracker.data.model.User
import com.example.fittracker.data.model.Workout
import com.example.fittracker.data.model.WorkoutExercise
import com.example.fittracker.data.model.WorkoutExerciseWithExercise
import com.example.fittracker.data.model.WorkoutSet
import kotlinx.coroutines.flow.Flow

/** Единая точка доступа к данным: ViewModel не знает про DAO напрямую. */
class FitRepository(db: AppDatabase) {
    private val userDao = db.userDao()
    private val exerciseDao = db.exerciseDao()
    private val workoutDao = db.workoutDao()
    private val workoutExerciseDao = db.workoutExerciseDao()
    private val setDao = db.workoutSetDao()

    // ---- Read ----
    val users: Flow<List<User>> = userDao.observeAll()
    val exercises: Flow<List<Exercise>> = exerciseDao.observeAll()
    fun workoutsOf(userId: Long): Flow<List<Workout>> = workoutDao.observeByUser(userId)
    fun workout(id: Long): Flow<Workout?> = workoutDao.observeById(id)
    fun workoutExercises(workoutId: Long): Flow<List<WorkoutExerciseWithExercise>> =
        workoutExerciseDao.observeByWorkout(workoutId)
    fun sets(workoutId: Long): Flow<List<WorkoutSet>> = setDao.observeByWorkout(workoutId)

    // ---- Create / Update (id == 0 -> новая запись) ----
    suspend fun save(user: User): Long =
        if (user.id == 0L) userDao.insert(user) else { userDao.update(user); user.id }

    suspend fun save(exercise: Exercise): Long =
        if (exercise.id == 0L) exerciseDao.insert(exercise) else { exerciseDao.update(exercise); exercise.id }

    suspend fun save(workout: Workout): Long =
        if (workout.id == 0L) workoutDao.insert(workout) else { workoutDao.update(workout); workout.id }

    suspend fun save(item: WorkoutExercise): Long =
        if (item.id == 0L) workoutExerciseDao.insert(item) else { workoutExerciseDao.update(item); item.id }

    suspend fun save(set: WorkoutSet): Long =
        if (set.id == 0L) setDao.insert(set) else { setDao.update(set); set.id }

    // ---- Delete ----
    suspend fun delete(user: User) = userDao.delete(user)
    suspend fun delete(exercise: Exercise) = exerciseDao.delete(exercise)
    suspend fun delete(workout: Workout) = workoutDao.delete(workout)
    suspend fun delete(item: WorkoutExercise) = workoutExerciseDao.delete(item)
    suspend fun delete(set: WorkoutSet) = setDao.delete(set)
}
