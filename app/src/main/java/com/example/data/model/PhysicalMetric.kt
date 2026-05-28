package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "physical_metrics")
data class PhysicalMetric(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val timestamp: Long = System.currentTimeMillis(),
    val weightKg: Double,
    val bodyFatPercentage: Double = 0.0,
    val muscleMassKg: Double = 0.0,
    val chestCm: Double = 0.0,
    val waistCm: Double = 0.0,
    val bicepCm: Double = 0.0,
    val thighCm: Double = 0.0,
    val notes: String = ""
)
