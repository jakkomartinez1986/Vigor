package com.example.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.data.model.Exercise
import com.example.data.model.PhysicalMetric
import com.example.data.model.Routine
import com.example.data.model.WorkoutLog
import com.example.data.repository.FitnessRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.UUID

class FitnessViewModel(private val repository: FitnessRepository) : ViewModel() {

    // Reactive streams from DB
    val routines: StateFlow<List<Routine>> = repository.allRoutines
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    val metrics: StateFlow<List<PhysicalMetric>> = repository.allMetrics
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    val workoutLogs: StateFlow<List<WorkoutLog>> = repository.allWorkoutLogs
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    // Active Workout Tracker Section
    private val _activeRoutine = MutableStateFlow<Routine?>(null)
    val activeRoutine: StateFlow<Routine?> = _activeRoutine.asStateFlow()

    private val _activeExercises = MutableStateFlow<List<Exercise>>(emptyList())
    val activeExercises: StateFlow<List<Exercise>> = _activeExercises.asStateFlow()

    private val _workoutStartTime = MutableStateFlow<Long>(0L)
    val workoutStartTime: StateFlow<Long> = _workoutStartTime.asStateFlow()

    init {
        // Automatically insert demo routines and metric history if the database is clean
        viewModelScope.launch {
            val list = repository.allRoutines.first()
            if (list.isEmpty()) {
                insertSampleData()
            }
        }
    }

    private suspend fun insertSampleData() {
        // Demostration Routines
        val fullBodyRoutine = Routine(
            name = "Rutina de Fuerza - Cuerpo Completo",
            description = "Enfoque en multiarticulares para máximo reclutamiento muscular.",
            category = "Fuerza",
            dayOfWeek = "Lunes, Miércoles, Viernes",
            exercises = listOf(
                Exercise(name = "Sentadillas con Barra", sets = 4, reps = 8, weightKg = 80.0, category = "Fuerza"),
                Exercise(name = "Press de Banca Plano", sets = 4, reps = 8, weightKg = 70.0, category = "Fuerza"),
                Exercise(name = "Peso Muerto Rumano", sets = 3, reps = 10, weightKg = 90.0, category = "Fuerza"),
                Exercise(name = "Press de Hombros", sets = 3, reps = 12, weightKg = 35.0, category = "Fuerza"),
                Exercise(name = "Plancha Core abdominal", sets = 3, reps = 1, weightKg = 0.0, durationMinutes = 2, category = "Flexibilidad")
            )
        )

        val torsoRoutine = Routine(
            name = "Fuerza Superior (Torso)",
            description = "Frecuencia alta para hipertrofia de empuje y tracción.",
            category = "Fuerza",
            dayOfWeek = "Martes, Jueves",
            exercises = listOf(
                Exercise(name = "Dominadas en Barra", sets = 4, reps = 8, weightKg = 0.0, category = "Fuerza"),
                Exercise(name = "Fondos de Pecho", sets = 4, reps = 10, weightKg = 10.0, category = "Fuerza"),
                Exercise(name = "Remo con Barra", sets = 3, reps = 10, weightKg = 55.0, category = "Fuerza"),
                Exercise(name = "Vuelos Laterales Mancuerna", sets = 4, reps = 15, weightKg = 10.0, category = "Fuerza")
            )
        )

        val cardioRoutine = Routine(
            name = "HIIT Quema Grasa y Core",
            description = "Mejora de resistencia cardiovascular y VO2 Max aeróbico.",
            category = "Cardio",
            dayOfWeek = "Sábado",
            exercises = listOf(
                Exercise(name = "Burpees Continuos", sets = 4, reps = 15, weightKg = 0.0, durationMinutes = 1, category = "Cardio"),
                Exercise(name = "Saltos al Cajón", sets = 3, reps = 12, weightKg = 0.0, durationMinutes = 1, category = "Cardio"),
                Exercise(name = "Mountain Climbers", sets = 4, reps = 30, weightKg = 0.0, durationMinutes = 1, category = "Cardio"),
                Exercise(name = "Correr en Cinta", sets = 1, reps = 1, weightKg = 0.0, durationMinutes = 20, category = "Cardio")
            )
        )

        repository.insertRoutine(fullBodyRoutine)
        repository.insertRoutine(torsoRoutine)
        repository.insertRoutine(cardioRoutine)

        // Progressive metrics to generate amazing comparison graphs
        val now = System.currentTimeMillis()
        val dayMillis = 24 * 60 * 60 * 1000L

        // Week 1 (21 days ago)
        repository.insertMetric(
            PhysicalMetric(
                timestamp = now - 21 * dayMillis,
                weightKg = 81.5,
                bodyFatPercentage = 19.8,
                muscleMassKg = 34.5,
                chestCm = 101.5,
                waistCm = 88.0,
                bicepCm = 36.2,
                thighCm = 56.5,
                notes = "Fase de reinicio de volumen. Rendimiento aceptable."
            )
        )

        // Week 2 (14 days ago)
        repository.insertMetric(
            PhysicalMetric(
                timestamp = now - 14 * dayMillis,
                weightKg = 80.7,
                bodyFatPercentage = 19.2,
                muscleMassKg = 34.8,
                chestCm = 101.8,
                waistCm = 87.1,
                bicepCm = 36.5,
                thighCm = 56.4,
                notes = "Incremento de peso en sentadilla. Buena recuperación."
            )
        )

        // Week 3 (7 days ago)
        repository.insertMetric(
            PhysicalMetric(
                timestamp = now - 7 * dayMillis,
                weightKg = 79.9,
                bodyFatPercentage = 18.5,
                muscleMassKg = 35.1,
                chestCm = 102.2,
                waistCm = 85.8,
                bicepCm = 36.9,
                thighCm = 56.0,
                notes = "Sintiéndome más fuerte e hidratado. Dieta estricta."
            )
        )

        // Week 4 (Today)
        repository.insertMetric(
            PhysicalMetric(
                timestamp = now,
                weightKg = 79.2,
                bodyFatPercentage = 17.9,
                muscleMassKg = 35.4,
                chestCm = 102.5,
                waistCm = 84.5,
                bicepCm = 37.2,
                thighCm = 55.8,
                notes = "Excelente progreso corporal. Peso de sentadillas superado a 85kg."
            )
        )

        // Insert some standard completed logs in history
        repository.insertWorkoutLog(
            WorkoutLog(
                routineId = 1,
                routineName = "Rutina de Fuerza - Cuerpo Completo",
                timestamp = now - 4 * dayMillis,
                durationMinutes = 45,
                feedback = "Muy buena congestión, superé peso récord en press de banca. Cansado pero excelente.",
                exercises = listOf(
                    Exercise(name = "Sentadillas con Barra", sets = 4, reps = 8, weightKg = 80.0, category = "Fuerza", isCompleted = true),
                    Exercise(name = "Press de Banca Plano", sets = 4, reps = 8, weightKg = 70.0, category = "Fuerza", isCompleted = true),
                    Exercise(name = "Peso Muerto Rumano", sets = 3, reps = 10, weightKg = 90.0, category = "Fuerza", isCompleted = true),
                    Exercise(name = "Press de Hombros", sets = 3, reps = 12, weightKg = 35.0, category = "Fuerza", isCompleted = true)
                )
            )
        )

        repository.insertWorkoutLog(
            WorkoutLog(
                routineId = 2,
                routineName = "Fuerza Superior (Torso)",
                timestamp = now - 2 * dayMillis,
                durationMinutes = 50,
                feedback = "Muy fluido, completé todas las dominadas con excelente técnica.",
                exercises = listOf(
                    Exercise(name = "Dominadas en Barra", sets = 4, reps = 8, weightKg = 0.0, category = "Fuerza", isCompleted = true),
                    Exercise(name = "Fondos de Pecho", sets = 4, reps = 10, weightKg = 10.0, category = "Fuerza", isCompleted = true),
                    Exercise(name = "Remo con Barra", sets = 3, reps = 10, weightKg = 55.0, category = "Fuerza", isCompleted = true)
                )
            )
        )
    }

    // Database Actions
    fun insertRoutine(routine: Routine) {
        viewModelScope.launch {
            repository.insertRoutine(routine)
        }
    }

    fun deleteRoutine(routine: Routine) {
        viewModelScope.launch {
            repository.deleteRoutine(routine)
        }
    }

    fun insertMetric(metric: PhysicalMetric) {
        viewModelScope.launch {
            repository.insertMetric(metric)
        }
    }

    fun deleteMetric(metric: PhysicalMetric) {
        viewModelScope.launch {
            repository.deleteMetric(metric)
        }
    }

    // Workout Tracker Logic
    fun startWorkout(routine: Routine) {
        _activeRoutine.value = routine
        _activeExercises.value = routine.exercises.map { it.copy() }
        _workoutStartTime.value = System.currentTimeMillis()
    }

    fun toggleExerciseCompletion(exerciseId: String) {
        _activeExercises.value = _activeExercises.value.map { exercise ->
            if (exercise.id == exerciseId) {
                exercise.copy(isCompleted = !exercise.isCompleted)
            } else {
                exercise
            }
        }
    }

    fun updateExerciseStats(
        exerciseId: String,
        sets: Int,
        reps: Int,
        weightKg: Double,
        durationMinutes: Int
    ) {
        _activeExercises.value = _activeExercises.value.map { exercise ->
            if (exercise.id == exerciseId) {
                exercise.copy(
                    sets = sets,
                    reps = reps,
                    weightKg = weightKg,
                    durationMinutes = durationMinutes
                )
            } else {
                exercise
            }
        }
    }

    fun finishWorkout(feedback: String) {
        val routine = _activeRoutine.value ?: return
        val startTime = _workoutStartTime.value
        val endTime = System.currentTimeMillis()
        val duration = (((endTime - startTime) / 1000) / 60).toInt().coerceAtLeast(1)

        val workoutLog = WorkoutLog(
            routineId = routine.id,
            routineName = routine.name,
            timestamp = endTime,
            durationMinutes = duration,
            exercises = _activeExercises.value,
            feedback = feedback
        )

        viewModelScope.launch {
            repository.insertWorkoutLog(workoutLog)
            _activeRoutine.value = null
            _activeExercises.value = emptyList()
            _workoutStartTime.value = 0L
        }
    }

    fun cancelWorkout() {
        _activeRoutine.value = null
        _activeExercises.value = emptyList()
        _workoutStartTime.value = 0L
    }
}

class FitnessViewModelFactory(private val repository: FitnessRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(FitnessViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return FitnessViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
