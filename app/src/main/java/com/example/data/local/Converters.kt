package com.example.data.local

import androidx.room.TypeConverter
import com.example.data.model.Exercise
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory

class Converters {
    private val moshi = Moshi.Builder().addLast(KotlinJsonAdapterFactory()).build()
    private val exerciseListType = Types.newParameterizedType(List::class.java, Exercise::class.java)
    private val exerciseListAdapter = moshi.adapter<List<Exercise>>(exerciseListType)

    @TypeConverter
    fun fromExerciseList(exercises: List<Exercise>?): String? {
        return exercises?.let { exerciseListAdapter.toJson(it) }
    }

    @TypeConverter
    fun toExerciseList(json: String?): List<Exercise>? {
        return json?.let { exerciseListAdapter.fromJson(it) }
    }
}
