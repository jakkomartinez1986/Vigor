package com.example.data.model

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class Exercise(
    val id: String = java.util.UUID.randomUUID().toString(),
    val name: String,
    val sets: Int = 3,
    val reps: Int = 10,
    val weightKg: Double = 0.0,
    val durationMinutes: Int = 0,
    val category: String = "Fuerza", // Fuerza, Cardio, Flexibilidad
    val isCompleted: Boolean = false
)
