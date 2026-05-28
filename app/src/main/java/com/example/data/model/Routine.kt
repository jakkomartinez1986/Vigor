package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "routines")
data class Routine(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val description: String,
    val category: String = "Fuerza", // Fuerza, Cardio, Resistencia, Flexibilidad, Enfoque Local
    val exercises: List<Exercise> = emptyList(),
    val dayOfWeek: String = "" // e.g. "Lunes, Miércoles, Viernes" or "Día de Pierna"
)
