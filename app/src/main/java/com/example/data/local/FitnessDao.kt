package com.example.data.local

import androidx.room.*
import com.example.data.model.Routine
import com.example.data.model.PhysicalMetric
import com.example.data.model.WorkoutLog
import kotlinx.coroutines.flow.Flow

@Dao
interface FitnessDao {

    // Routines
    @Query("SELECT * FROM routines ORDER BY id DESC")
    fun getAllRoutines(): Flow<List<Routine>>

    @Query("SELECT * FROM routines WHERE id = :id LIMIT 1")
    suspend fun getRoutineById(id: Int): Routine?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRoutine(routine: Routine): Long

    @Delete
    suspend fun deleteRoutine(routine: Routine)

    // Physical Metrics
    @Query("SELECT * FROM physical_metrics ORDER BY timestamp DESC")
    fun getAllMetrics(): Flow<List<PhysicalMetric>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMetric(metric: PhysicalMetric): Long

    @Delete
    suspend fun deleteMetric(metric: PhysicalMetric)

    // Workout Logs
    @Query("SELECT * FROM workout_logs ORDER BY timestamp DESC")
    fun getAllWorkoutLogs(): Flow<List<WorkoutLog>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWorkoutLog(log: WorkoutLog): Long

    @Delete
    suspend fun deleteWorkoutLog(log: WorkoutLog)
}
