package com.example.data.repository

import com.example.data.local.FitnessDao
import com.example.data.model.Routine
import com.example.data.model.PhysicalMetric
import com.example.data.model.WorkoutLog
import kotlinx.coroutines.flow.Flow

class FitnessRepository(private val fitnessDao: FitnessDao) {

    val allRoutines: Flow<List<Routine>> = fitnessDao.getAllRoutines()
    val allMetrics: Flow<List<PhysicalMetric>> = fitnessDao.getAllMetrics()
    val allWorkoutLogs: Flow<List<WorkoutLog>> = fitnessDao.getAllWorkoutLogs()

    suspend fun getRoutineById(id: Int): Routine? = fitnessDao.getRoutineById(id)

    suspend fun insertRoutine(routine: Routine): Long = fitnessDao.insertRoutine(routine)

    suspend fun deleteRoutine(routine: Routine) = fitnessDao.deleteRoutine(routine)

    suspend fun insertMetric(metric: PhysicalMetric): Long = fitnessDao.insertMetric(metric)

    suspend fun deleteMetric(metric: PhysicalMetric) = fitnessDao.deleteMetric(metric)

    suspend fun insertWorkoutLog(log: WorkoutLog): Long = fitnessDao.insertWorkoutLog(log)

    suspend fun deleteWorkoutLog(log: WorkoutLog) = fitnessDao.deleteWorkoutLog(log)
}
