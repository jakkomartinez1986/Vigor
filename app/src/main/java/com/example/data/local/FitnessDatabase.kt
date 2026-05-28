package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.data.model.Routine
import com.example.data.model.PhysicalMetric
import com.example.data.model.WorkoutLog

@Database(
    entities = [Routine::class, PhysicalMetric::class, WorkoutLog::class],
    version = 2,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class FitnessDatabase : RoomDatabase() {

    abstract fun fitnessDao(): FitnessDao

    companion object {
        @Volatile
        private var INSTANCE: FitnessDatabase? = null

        fun getDatabase(context: Context): FitnessDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    FitnessDatabase::class.java,
                    "fitness_database"
                )
                .fallbackToDestructiveMigration()
                .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
