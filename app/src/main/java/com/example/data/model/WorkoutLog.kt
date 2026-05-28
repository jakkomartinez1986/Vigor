package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "workout_logs")
data class WorkoutLog(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val routineId: Int,
    val routineName: String,
    val timestamp: Long = System.currentTimeMillis(),
    val durationMinutes: Int,
    val exercises: List<Exercise> = emptyList(),
    val feedback: String = "¡Excelente sesión!"
)
