package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.ui.geometry.Size
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.model.Exercise
import com.example.data.model.PhysicalMetric
import com.example.data.model.Routine
import com.example.data.model.WorkoutLog
import com.example.ui.theme.*
import com.example.ui.viewmodel.FitnessViewModel
import kotlinx.coroutines.delay
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(viewModel: FitnessViewModel) {
    val routines by viewModel.routines.collectAsStateWithLifecycle()
    val metrics by viewModel.metrics.collectAsStateWithLifecycle()
    val workoutLogs by viewModel.workoutLogs.collectAsStateWithLifecycle()
    val activeRoutine by viewModel.activeRoutine.collectAsStateWithLifecycle()

    var selectedTab by remember { mutableStateOf(0) }
    var showAddRoutineDialog by remember { mutableStateOf(false) }
    var showAddMetricDialog by remember { mutableStateOf(false) }
    var successToastMessage by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(successToastMessage) {
        if (successToastMessage != null) {
            delay(2500)
            successToastMessage = null
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(12.dp)
                                .background(NeonGymGreen, CircleShape)
                        )
                        Text(
                            text = "VIGOR",
                            fontWeight = FontWeight.Black,
                            letterSpacing = 1.5.sp,
                            color = Color.White,
                            fontSize = 24.sp
                        )
                        Text(
                            text = "|  Rendimiento Físico",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Light,
                            color = LightSlate
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = DeepCarbon,
                    titleContentColor = Color.White
                )
            )
        },
        bottomBar = {
            NavigationBar(
                containerColor = DarkCharcoal,
                tonalElevation = 8.dp,
                modifier = Modifier.testTag("main_navigation_bar")
            ) {
                NavigationBarItem(
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    icon = {
                        Icon(
                            imageVector = if (selectedTab == 0) Icons.Filled.FitnessCenter else Icons.Outlined.FitnessCenter,
                            contentDescription = "Rutinas"
                        )
                    },
                    label = { Text("Rutinas", fontWeight = FontWeight.Bold) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = Color.Black,
                        selectedTextColor = NeonGymGreen,
                        indicatorColor = NeonGymGreen,
                        unselectedIconColor = LightSlate,
                        unselectedTextColor = LightSlate
                    )
                )
                NavigationBarItem(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    icon = {
                        Icon(
                            imageVector = if (selectedTab == 1) Icons.Filled.MonitorWeight else Icons.Outlined.MonitorWeight,
                            contentDescription = "Métricas"
                        )
                    },
                    label = { Text("Métricas", fontWeight = FontWeight.Bold) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = Color.Black,
                        selectedTextColor = NeonGymGreen,
                        indicatorColor = NeonGymGreen,
                        unselectedIconColor = LightSlate,
                        unselectedTextColor = LightSlate
                    )
                )
                NavigationBarItem(
                    selected = selectedTab == 2,
                    onClick = { selectedTab = 2 },
                    icon = {
                        Icon(
                            imageVector = if (selectedTab == 2) Icons.Filled.Timeline else Icons.Outlined.Timeline,
                            contentDescription = "Progreso"
                        )
                    },
                    label = { Text("Progreso", fontWeight = FontWeight.Bold) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = Color.Black,
                        selectedTextColor = NeonGymGreen,
                        indicatorColor = NeonGymGreen,
                        unselectedIconColor = LightSlate,
                        unselectedTextColor = LightSlate
                    )
                )
            }
        },
        containerColor = DeepCarbon
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // Screen Contents based on selectedTab
            AnimatedContent(
                targetState = selectedTab,
                transitionSpec = {
                    fadeIn(animationSpec = tween(220)) togetherWith fadeOut(animationSpec = tween(220))
                },
                label = "tab_animation"
            ) { tab ->
                when (tab) {
                    0 -> RoutinesTab(
                        routines = routines,
                        workoutLogs = workoutLogs,
                        onAddRoutineClick = { showAddRoutineDialog = true },
                        onStartWorkout = { viewModel.startWorkout(it) },
                        onDeleteRoutine = { viewModel.deleteRoutine(it) },
                        onImportRoutine = { viewModel.insertRoutine(it) },
                        onShowMessage = { successToastMessage = it }
                    )
                    1 -> MetricsTab(
                        metrics = metrics,
                        onAddMetricClick = { showAddMetricDialog = true },
                        onDeleteMetric = { viewModel.deleteMetric(it) }
                    )
                    2 -> ProgressTab(
                        metrics = metrics,
                        workoutLogs = workoutLogs
                    )
                }
            }

            // Success toast overlay
            AnimatedVisibility(
                visible = successToastMessage != null,
                enter = fadeIn() + slideInVertically(initialOffsetY = { it }),
                exit = fadeOut() + slideOutVertically(targetOffsetY = { it }),
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 24.dp)
            ) {
                Card(
                    colors = CardDefaults.cardColors(containerColor = BentoAccentLavender),
                    shape = RoundedCornerShape(16.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
                    modifier = Modifier.padding(horizontal = 24.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Filled.CheckCircle,
                            contentDescription = null,
                            tint = BentoOnAccentLavender
                        )
                        Text(
                            text = successToastMessage ?: "",
                            color = BentoOnAccentLavender,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                    }
                }
            }

            // Foreground Active Workout Session overlay
            activeRoutine?.let { routine ->
                ActiveWorkoutOverlay(
                    routine = routine,
                    viewModel = viewModel
                )
            }
        }
    }

    // New Routine Setup Dialog
    if (showAddRoutineDialog) {
        AddRoutineDialog(
            onDismiss = { showAddRoutineDialog = false },
            onSave = { name, desc, cat, day, exercises ->
                viewModel.insertRoutine(
                    Routine(
                        name = name,
                        description = desc,
                        category = cat,
                        dayOfWeek = day,
                        exercises = exercises
                    )
                )
                showAddRoutineDialog = false
            }
        )
    }

    // New Metric Entry Dialog
    if (showAddMetricDialog) {
        AddMetricDialog(
            onDismiss = { showAddMetricDialog = false },
            onSave = { weight, fat, muscle, chest, waist, bicep, thigh, notes ->
                viewModel.insertMetric(
                    PhysicalMetric(
                        weightKg = weight,
                        bodyFatPercentage = fat,
                        muscleMassKg = muscle,
                        chestCm = chest,
                        waistCm = waist,
                        bicepCm = bicep,
                        thighCm = thigh,
                        notes = notes
                    )
                )
                showAddMetricDialog = false
            }
        )
    }
}

// ---------------------- DATA STRUCTURES & STATIC CONTENT ----------------------
data class LibraryExercise(
    val name: String,
    val category: String, // Fuerza, Cardio, Abdomen
    val description: String,
    val instructions: List<String>,
    val muscles: String,
    val difficulty: String
)

val EXERCISE_LIBRARY = listOf(
    LibraryExercise(
        name = "Sentadilla con Barra",
        category = "Fuerza",
        description = "El rey de los ejercicios de piernas, excelente para el desarrollo global de las extremidades inferiores y el core.",
        muscles = "Cuádriceps, Glúteos mayores, Isquiotibiales, Abdomen",
        difficulty = "Intermedio",
        instructions = listOf(
            "Coloca la barra sobre los trapecios, saca pecho y mantén los pies alineados a la anchura de los hombros.",
            "Inicia el descenso empujando la cadera hacia atrás como si fueras a sentarte en una silla.",
            "Baja controladamente hasta que tus muslos estén paralelos al suelo o ligeramente por debajo (ángulo de 90° o mayor).",
            "Empuja fuertemente a través del mediopie para regresar a la posición vertical, manteniendo la columna estable."
        )
    ),
    LibraryExercise(
        name = "Press de Banca Plano",
        category = "Fuerza",
        description = "Ejercicio fundamental de empuje superior enfocado en fuerza y volumen del torso.",
        muscles = "Pectoral Mayor, Deltoides Anterior, Tríceps Braquial",
        difficulty = "Principiante",
        instructions = listOf(
            "Acuéstate boca arriba sobre el banco, mantén los pies firmes sobre el suelo y retrae las escápulas formando un arco natural en la espalda.",
            "Sujeta la barra con un agarre ligeramente más ancho que los hombros y desmóntala.",
            "Baja la barra de forma controlada hasta rozar la parte media de tu pecho (línea del pezón).",
            "Empuja la barra verticalmente hacia arriba bloqueando moderadamente los codos arriba."
        )
    ),
    LibraryExercise(
        name = "Peso Muerto Convencional",
        category = "Fuerza",
        description = "Movimiento compuesto de bisagra de cadera indispensable para la cadena posterior.",
        muscles = "Isquiotibiales, Glúteos, Erector de la Columna, Dorsales y Trapecios",
        difficulty = "Avanzado",
        instructions = listOf(
            "Coloca tus pies a la anchura de la cadera con la barra sobre la mitad de tu empeine.",
            "Inclínate flexionando las caderas y rodillas para sostener la barra con espalda recta y mirada neutra.",
            "Inhala profundamente y genera tensión en tus dorsales y abdomen.",
            "Empuja el suelo con fuerza extendiendo caderas y rodillas simultáneamente hasta estar completamente erguido."
        )
    ),
    LibraryExercise(
        name = "Dominadas en Barra (Pull-ups)",
        category = "Fuerza",
        description = "Soberbio ejercicio calisténico para expandir el ancho de la espalda y mejorar la fuerza del agarre.",
        muscles = "Dorsal Ancho, Redondo Mayor, Bíceps Braquial, Core",
        difficulty = "Avanzado",
        instructions = listOf(
            "Sujeta la barra con agarre prono (palmas hacia el frente) a una anchura superior a los hombros.",
            "Déjate colgar totalmente firme, activando ligeramente tus escápulas.",
            "Tira de tu cuerpo hacia arriba llevando los codos hacia abajo hasta que tu barra supere la barbilla.",
            "Desciende de forma lenta y controlada hasta estirar los brazos completamente."
        )
    ),
    LibraryExercise(
        name = "Plancha Abdominal Isometrica",
        category = "Abdomen",
        description = "Estabilización isométrica primordial para esculpir un core indestructible y proteger la zona lumbar.",
        muscles = "Recto Abdominal, Transverso, Oblicuos, Glúteos",
        difficulty = "Principiante",
        instructions = listOf(
            "Apóyate sobre tus antebrazos y las puntas de tus pies en el suelo.",
            "Alinea tus hombros directamente sobre tus codos.",
            "Mantén el cuerpo completamente recto de la cabeza a los talones, apretando el abdomen y los glúteos intensamente.",
            "Sostén la posición manteniendo una respiración fluida y sin dejar caer la cadera."
        )
    ),
    LibraryExercise(
        name = "Burpees Explosivos",
        category = "Cardio",
        description = "Ejercicio calisténico de cuerpo completo que eleva drásticamente el pulso cardiovascular rápidamente.",
        muscles = "Cuerpo Completo, Resistencia Anaeróbica, Pectorales, Cuádriceps",
        difficulty = "Intermedio",
        instructions = listOf(
            "Comienza de pie con los brazos a los lados.",
            "Baja en una cuclilla rápida colocando tus manos apoyadas firmemente enfrente.",
            "Patea tus pies hacia atrás para entrar en posición de flexión de pecho y realiza una lagartija.",
            "Regresa las piernas de un salto a la posición de cuclillas y salta verticalmente hacia arriba lo más alto posible alzando los brazos."
        )
    ),
    LibraryExercise(
        name = "Remo con Mancuerna",
        category = "Fuerza",
        description = "Excelente movimiento unilateral para balancear la fuerza de tracción y corregir asimetrías de espalda.",
        muscles = "Dorsal Ancho, Trapecio, Redondo Mayor, Deltoides Posterior",
        difficulty = "Principiante",
        instructions = listOf(
            "Apoya una rodilla y una mano del mismo lado sobre un banco para estabilizar el torso horizontalmente.",
            "Sostiene la mancuerna con el brazo libre completamente extendido.",
            "Tira del peso hacia arriba dirigiendo el codo hacia tu cadera, sintiendo la contracción de la espalda.",
            "Regresa a la posición de inicio sintiendo el estiramiento completo del dorsal."
        )
    ),
    LibraryExercise(
        name = "Correr en Cinta de Correr / Trote",
        category = "Cardio",
        description = "Ejercicio clásico para entrenar la resistencia aeróbica continua, ideal para acondicionamiento físico.",
        muscles = "Gastrocnemios, Isquiotibiales, Cuádriceps, Sistema Cardiorrespiratorio",
        difficulty = "Principiante",
        instructions = listOf(
            "Para comenzar, calienta a ritmo moderado durante 2 a 3 minutos.",
            "Sube la velocidad gradualmente de acuerdo a tu nivel de resistencia.",
            "Mantén la espalda erguida, la pisada estable sobre el mediopie y los brazos relajados balanceándose rítmicamente.",
            "Realiza una fase de enfriamiento reduciendo la marcha progresivamente al finalizar."
        )
    ),
    LibraryExercise(
        name = "Curl de Bíceps con Mancuerna",
        category = "Fuerza",
        description = "Ejercicio de aislamiento directo para trabajar la cabeza larga y corta de tus bíceps.",
        muscles = "Bíceps Braquial, Braquiorradial, Antebrazos",
        difficulty = "Principiante",
        instructions = listOf(
            "Párate derecho con una mancuerna en cada mano, con los codos pegados al torso.",
            "Gira las palmas hacia adelante y contrae los bíceps flexionando los codos.",
            "Sube las mancuernas de manera estricta sin balancear la cadera hasta el nivel de tus hombros.",
            "Desciende pausadamente resistiendo la gravedad para cuidar el tendón bicipital."
        )
    )
)

val PREDEFINED_PLANS = listOf(
    Routine(
        id = -100,
        name = "Iniciación Básica (Principiante)",
        description = "Plan adaptativo enfocado en aprender la técnica y acondicionamiento general del cuerpo.",
        category = "Salud general",
        dayOfWeek = "Lunes y Jueves",
        exercises = listOf(
            Exercise(name = "Sentadilla con Barra", sets = 3, reps = 12, weightKg = 0.0, category = "Fuerza"),
            Exercise(name = "Press de Banca Plano", sets = 3, reps = 10, weightKg = 20.0, category = "Fuerza"),
            Exercise(name = "Remo con Mancuerna", sets = 3, reps = 12, weightKg = 10.0, category = "Fuerza"),
            Exercise(name = "Plancha Abdominal Isometrica", sets = 3, reps = 1, weightKg = 0.0, durationMinutes = 1, category = "Abdomen")
        )
    ),
    Routine(
        id = -101,
        name = "Fuerza Extrema (Avanzado)",
        description = "Alta intensidad para atletas experimentados buscando romper récords personales de fuerza multiarticular.",
        category = "Fuerza Máxima",
        dayOfWeek = "Lunes, Miércoles, Viernes",
        exercises = listOf(
            Exercise(name = "Sentadilla con Barra", sets = 5, reps = 5, weightKg = 100.0, category = "Fuerza"),
            Exercise(name = "Press de Banca Plano", sets = 5, reps = 5, weightKg = 85.0, category = "Fuerza"),
            Exercise(name = "Peso Muerto Convencional", sets = 4, reps = 5, weightKg = 120.0, category = "Fuerza"),
            Exercise(name = "Dominadas en Barra (Pull-ups)", sets = 4, reps = 6, weightKg = 0.0, category = "Fuerza")
        )
    ),
    Routine(
        id = -102,
        name = "Quema Grasa HIIT (Pérdida de Peso)",
        description = "Circuitos metabólicos de alta frecuencia para elevar la tasa metabólica basal y potenciar el déficit calórico.",
        category = "Cardio HIIT",
        dayOfWeek = "Martes, Jueves, Sábado",
        exercises = listOf(
            Exercise(name = "Burpees Explosivos", sets = 4, reps = 15, weightKg = 0.0, durationMinutes = 1, category = "Cardio"),
            Exercise(name = "Correr en Cinta de Correr / Trote", sets = 1, reps = 1, weightKg = 0.0, durationMinutes = 15, category = "Cardio"),
            Exercise(name = "Plancha Abdominal Isometrica", sets = 3, reps = 1, weightKg = 0.0, durationMinutes = 1, category = "Abdomen")
        )
    ),
    Routine(
        id = -103,
        name = "Hipertrofia Total (Ganancia Muscular)",
        description = "Protocolo enfocado en la tensión mecánica y volumen acumulado para maximizar el crecimiento de fibras.",
        category = "Hipertrofia",
        dayOfWeek = "Martes y Viernes",
        exercises = listOf(
            Exercise(name = "Sentadilla con Barra", sets = 4, reps = 10, weightKg = 80.0, category = "Fuerza"),
            Exercise(name = "Press de Banca Plano", sets = 4, reps = 10, weightKg = 70.0, category = "Fuerza"),
            Exercise(name = "Remo con Mancuerna", sets = 3, reps = 12, weightKg = 18.0, category = "Fuerza"),
            Exercise(name = "Curl de Bíceps con Mancuerna", sets = 3, reps = 12, weightKg = 12.0, category = "Fuerza")
        )
    )
)

// --- COMPOSABLE ANIMATED GYM GRAPHIC COMPANION ---
@Composable
fun ExerciseVisualCompanion(exerciseName: String, modifier: Modifier = Modifier) {
    val infiniteTransition = rememberInfiniteTransition(label = "exercise_anim")
    val animState = infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(2200, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "loop"
    )
    val animFactor = animState.value

    Canvas(modifier = modifier.fillMaxSize().background(BentoCardBgDarkDetail)) {
        val width = size.width
        val height = size.height
        val centerX = width / 2f
        val centerY = height / 2f

        val nameLower = exerciseName.lowercase()
        when {
            nameLower.contains("sentadilla") || nameLower.contains("leg") || nameLower.contains("pierna") -> {
                val footX = centerX
                val footY = centerY + 30f
                val kneeY = centerY + 5f + (20f * animFactor)
                val kneeX = centerX + 25f - (8f * animFactor)
                val hipY = centerY - 15f + (35f * animFactor)

                val stroke = Stroke(width = 6.dp.toPx(), cap = StrokeCap.Round)
                drawLine(BentoTextSecondary, Offset(footX, footY), Offset(kneeX, kneeY), stroke.width, cap = stroke.cap)
                drawLine(BentoTextSecondary, Offset(kneeX, kneeY), Offset(centerX, hipY), stroke.width, cap = stroke.cap)

                val barY = hipY - 12f
                drawLine(BentoAccentLavender, Offset(centerX - 42f, barY), Offset(centerX + 42f, barY), 4.dp.toPx(), cap = StrokeCap.Round)
                drawCircle(BentoHighlightPurple, 10.dp.toPx(), Offset(centerX - 42f, barY))
                drawCircle(BentoHighlightPurple, 10.dp.toPx(), Offset(centerX + 42f, barY))
            }
            nameLower.contains("press") || nameLower.contains("banca") || nameLower.contains("pecho") || nameLower.contains("lagartija") || nameLower.contains("fondos") -> {
                drawLine(BentoTextSecondary.copy(alpha = 0.5f), Offset(centerX - 50f, centerY + 15f), Offset(centerX + 50f, centerY + 15f), 4.dp.toPx())
                drawLine(BentoTextSecondary.copy(alpha = 0.5f), Offset(centerX - 35f, centerY + 15f), Offset(centerX - 35f, centerY + 35f), 4.dp.toPx())
                drawLine(BentoTextSecondary.copy(alpha = 0.5f), Offset(centerX + 35f, centerY + 15f), Offset(centerX + 35f, centerY + 35f), 4.dp.toPx())

                val barY = centerY - 25f + (38f * animFactor)
                drawLine(BentoAccentLavender, Offset(centerX - 48f, barY), Offset(centerX + 48f, barY), 4.dp.toPx(), cap = StrokeCap.Round)
                
                drawRect(BentoHighlightPurple, Offset(centerX - 53f, barY - 10f), Size(7.dp.toPx(), 20.dp.toPx()))
                drawRect(BentoHighlightPurple, Offset(centerX + 46f, barY - 10f), Size(7.dp.toPx(), 20.dp.toPx()))
                
                if (animFactor > 0.85f) {
                    drawCircle(BentoAccentLavender.copy(alpha = 0.4f), 16.dp.toPx(), Offset(centerX, centerY + 13f))
                }
            }
            nameLower.contains("peso muerto") || nameLower.contains("remo") || nameLower.contains("deadlift") -> {
                drawLine(BentoTextSecondary.copy(alpha = 0.4f), Offset(centerX - 60f, centerY + 30f), Offset(centerX + 60f, centerY + 30f), 2.dp.toPx())
                
                val barY = centerY + 22f - (40f * animFactor)
                val hipY = centerY - 5f - (15f * animFactor)
                
                val shoulderY = hipY - 20f
                val shoulderX = centerX + 12f - (12f * animFactor)
                
                val stroke = Stroke(width = 5.dp.toPx(), cap = StrokeCap.Round)
                drawLine(BentoTextSecondary, Offset(centerX - 8f, centerY + 30f), Offset(centerX - 4f, hipY), stroke.width, cap = stroke.cap)
                drawLine(BentoTextSecondary, Offset(centerX - 4f, hipY), Offset(shoulderX, shoulderY), stroke.width, cap = stroke.cap)
                drawLine(BentoAccentLavender, Offset(shoulderX, shoulderY), Offset(centerX + 8f, barY), 3.dp.toPx(), cap = stroke.cap)

                drawLine(BentoHighlightPurple, Offset(centerX - 35f, barY), Offset(centerX + 35f, barY), 4.dp.toPx(), cap = StrokeCap.Round)
                drawCircle(BentoAccentLavender, 8.dp.toPx(), Offset(centerX - 35f, barY))
                drawCircle(BentoAccentLavender, 8.dp.toPx(), Offset(centerX + 35f, barY))
            }
            nameLower.contains("plancha") || nameLower.contains("core") || nameLower.contains("abdomen") || nameLower.contains("curl") -> {
                if (nameLower.contains("curl")) {
                    val elbowX = centerX - 18f
                    val elbowY = centerY + 12f
                    val shoulderX = centerX - 12f
                    val shoulderY = centerY - 20f
                    
                    val handX = centerX + 4f + (12f * animFactor)
                    val handY = centerY - 4f - (25f * animFactor)
                    
                    val stroke = Stroke(width = 6.dp.toPx(), cap = StrokeCap.Round)
                    drawLine(BentoTextSecondary, Offset(shoulderX, shoulderY), Offset(elbowX, elbowY), stroke.width, cap = stroke.cap)
                    drawLine(BentoTextSecondary, Offset(elbowX, elbowY), Offset(handX, handY), stroke.width, cap = stroke.cap)
                    
                    drawCircle(BentoAccentLavender, 7.dp.toPx(), Offset(handX, handY))
                    drawLine(BentoHighlightPurple, Offset(handX - 8f, handY - 8f), Offset(handX + 8f, handY + 8f), 3.dp.toPx())
                } else {
                    drawCircle(BentoAccentLavender.copy(alpha = 0.15f * animFactor), 30.dp.toPx() + (20.dp.toPx() * animFactor), Offset(centerX, centerY))
                    drawCircle(BentoAccentLavender.copy(alpha = 0.3f), 15.dp.toPx(), Offset(centerX, centerY))
                    
                    val stroke = Stroke(width = 5.dp.toPx(), cap = StrokeCap.Round)
                    drawLine(BentoTextSecondary.copy(alpha = 0.4f), Offset(centerX - 50f, centerY + 20f), Offset(centerX + 50f, centerY + 20f), 2.dp.toPx())
                    drawLine(BentoHighlightPurple, Offset(centerX - 40f, centerY + 12f), Offset(centerX + 40f, centerY + 12f), stroke.width, cap = stroke.cap)
                }
            }
            else -> {
                val points = 30
                val path = Path()
                for (i in 0..points) {
                    val x = (width / points.toFloat()) * i
                    val relativeX = (i.toFloat() / points.toFloat())
                    val sinValue = kotlin.math.sin((relativeX * 10.0f + animFactor * 6.0f).toDouble()).toFloat()
                    val y = centerY + (sinValue * 18f)
                    if (i == 0) {
                        path.moveTo(x, y)
                    } else {
                        path.lineTo(x, y)
                    }
                }
                drawPath(path, color = BentoAccentLavender, style = Stroke(width = 4.dp.toPx(), cap = StrokeCap.Round))
                drawCircle(BentoHighlightPurple, 10.dp.toPx() + (3.dp.toPx() * animFactor), Offset(centerX, centerY))
            }
        }
    }
}

// ---------------------- ROUTINES TAB ----------------------
@Composable
fun RoutinesTab(
    routines: List<Routine>,
    workoutLogs: List<WorkoutLog>,
    onAddRoutineClick: () -> Unit,
    onStartWorkout: (Routine) -> Unit,
    onDeleteRoutine: (Routine) -> Unit,
    onImportRoutine: (Routine) -> Unit,
    onShowMessage: (String) -> Unit
) {
    var currentSubTab by remember { mutableStateOf(0) }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // High fidelity Bento Selection Sub Navigation Bar
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(BentoCardBgDarkDetail, RoundedCornerShape(20.dp))
                    .padding(6.dp),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                val subtabs = listOf(
                    Triple(0, "Mis Rutinas", Icons.Filled.FitnessCenter),
                    Triple(1, "Biblioteca", Icons.Filled.LibraryBooks),
                    Triple(2, "Planes Pro", Icons.Filled.Explore)
                )
                subtabs.forEach { (index, title, icon) ->
                    val isSelected = currentSubTab == index
                    Row(
                        modifier = Modifier
                            .weight(1f)
                            .background(
                                if (isSelected) BentoAccentLavender else Color.Transparent,
                                RoundedCornerShape(16.dp)
                            )
                            .clickable { currentSubTab = index }
                            .padding(vertical = 10.dp),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = icon,
                            contentDescription = null,
                            tint = if (isSelected) BentoOnAccentLavender else BentoTextSecondary,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = title,
                            color = if (isSelected) BentoOnAccentLavender else BentoTextSecondary,
                            fontWeight = FontWeight.Bold,
                            fontSize = 11.sp,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
            }
        }

        when (currentSubTab) {
            0 -> {
                // --- SUBTAB 0: DASHBOARD HERO PANELS & CUSTOM ROUTINES ---
                item {
                    // Bento Welcome Header Block
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 4.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "BIENVENIDO DE NUEVO",
                                color = BentoTextSecondary,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 1.sp
                            )
                            Text(
                                text = "Hola, Atleta",
                                color = Color.White,
                                fontSize = 24.sp,
                                fontWeight = FontWeight.Black
                            )
                        }
                        Box(
                            modifier = Modifier
                                .size(48.dp)
                                .background(BentoCardBgDarkDetail, CircleShape)
                                .border(2.dp, BentoAccentLavender, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Person,
                                contentDescription = "Atleta",
                                tint = BentoAccentLavender,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                    }
                }

                item {
                    // Hero Bento Performance Card
                    val vigorScore = remember(workoutLogs, routines) {
                        val score = 85.0 + (workoutLogs.size * 2.0) + (routines.size * 0.5)
                        String.format(Locale.US, "%.1f", score.coerceAtMost(100.0))
                    }
                    Card(
                        colors = CardDefaults.cardColors(containerColor = BentoAccentLavender),
                        shape = RoundedCornerShape(28.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(150.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(20.dp),
                            verticalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.Top
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.Bolt,
                                    contentDescription = null,
                                    tint = BentoOnAccentLavender,
                                    modifier = Modifier.size(32.dp)
                                )
                                Box(
                                    modifier = Modifier
                                        .background(
                                            BentoOnAccentLavender.copy(alpha = 0.12f),
                                            RoundedCornerShape(100.dp)
                                        )
                                        .padding(horizontal = 10.dp, vertical = 4.dp)
                                ) {
                                    Text(
                                        text = "PUNTUACIÓN SEMANAL",
                                        color = BentoOnAccentLavender,
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.Bold,
                                        letterSpacing = 0.5.sp
                                    )
                                }
                            }
                            Column {
                                Text(
                                    text = vigorScore,
                                    color = BentoOnAccentLavender,
                                    fontSize = 38.sp,
                                    fontWeight = FontWeight.Black,
                                    letterSpacing = (-1).sp
                                )
                                val percentText = if (workoutLogs.isEmpty()) "Comienza tu primer entrenamiento hoy" else "+12% vs. la semana pasada"
                                Text(
                                    text = percentText,
                                    color = BentoOnAccentLavender.copy(alpha = 0.8f),
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }
                    }
                }

                item {
                    val totalDuration = remember(workoutLogs) {
                        workoutLogs.sumOf { it.durationMinutes }
                    }
                    // Side-by-Side Bento Grid Row
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        // Heart Rate Card
                        Card(
                            colors = CardDefaults.cardColors(containerColor = BentoCardBgDarkDetail),
                            shape = RoundedCornerShape(28.dp),
                            modifier = Modifier
                                .weight(1f)
                                .height(115.dp)
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(16.dp),
                                verticalArrangement = Arrangement.SpaceBetween
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.Favorite,
                                    contentDescription = null,
                                    tint = BentoAccentLavender,
                                    modifier = Modifier.size(22.dp)
                                )
                                Column {
                                    Row(verticalAlignment = Alignment.Bottom) {
                                        Text(
                                            text = "64",
                                            color = Color.White,
                                            fontSize = 24.sp,
                                            fontWeight = FontWeight.Black
                                        )
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text(
                                            text = "BPM",
                                            color = BentoTextSecondary,
                                            fontSize = 11.sp,
                                            modifier = Modifier.padding(bottom = 2.dp)
                                        )
                                    }
                                    Text(
                                        text = "EN REPOSO",
                                        color = BentoTextSecondary,
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.Bold,
                                        letterSpacing = 0.5.sp
                                    )
                                }
                            }
                        }

                        // Training Time Card
                        Card(
                            colors = CardDefaults.cardColors(containerColor = BentoCardBgDarkDetail),
                            shape = RoundedCornerShape(28.dp),
                            modifier = Modifier
                                .weight(1f)
                                .height(115.dp)
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(16.dp),
                                verticalArrangement = Arrangement.SpaceBetween
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.Timer,
                                    contentDescription = null,
                                    tint = BentoAccentLavender,
                                    modifier = Modifier.size(24.dp)
                                )
                                Column {
                                    Row(verticalAlignment = Alignment.Bottom) {
                                        Text(
                                            text = "$totalDuration",
                                            color = Color.White,
                                            fontSize = 24.sp,
                                            fontWeight = FontWeight.Black
                                        )
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text(
                                            text = "MIN",
                                            color = BentoTextSecondary,
                                            fontSize = 11.sp,
                                            modifier = Modifier.padding(bottom = 2.dp)
                                        )
                                    }
                                    Text(
                                        text = "ENTRENAMIENTO",
                                        color = BentoTextSecondary,
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.Bold,
                                        letterSpacing = 0.5.sp
                                    )
                                }
                            }
                        }
                    }
                }

                item {
                    val recommendation = remember(routines) {
                        if (routines.isNotEmpty()) routines.first() else null
                    }

                    if (recommendation != null) {
                        Card(
                            colors = CardDefaults.cardColors(containerColor = BentoHighlightPurple),
                            shape = RoundedCornerShape(28.dp),
                            modifier = Modifier.fillMaxWidth(),
                            onClick = { onStartWorkout(recommendation) }
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(40.dp)
                                            .background(BentoOnHighlightPurple, RoundedCornerShape(14.dp)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            imageVector = Icons.Filled.FitnessCenter,
                                            contentDescription = null,
                                            tint = Color.White,
                                            modifier = Modifier.size(20.dp)
                                        )
                                    }
                                    Column {
                                        Text(
                                            text = "SIGUIENTE RECOMENDADA",
                                            color = BentoOnHighlightPurple.copy(alpha = 0.7f),
                                            fontSize = 9.sp,
                                            fontWeight = FontWeight.Bold,
                                            letterSpacing = 0.5.sp
                                        )
                                        Text(
                                            text = recommendation.name,
                                            color = BentoOnHighlightPurple,
                                            fontSize = 15.sp,
                                            fontWeight = FontWeight.Black
                                        )
                                    }
                                }
                                Icon(
                                    imageVector = Icons.Filled.ChevronRight,
                                    contentDescription = "Empezar",
                                    tint = BentoOnHighlightPurple,
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                        }
                    }
                }

                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Tus Rutinas",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.ExtraBold,
                            color = Color.White
                        )
                        Button(
                            onClick = onAddRoutineClick,
                            colors = ButtonDefaults.buttonColors(containerColor = NeonGymGreen),
                            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.testTag("btn_add_routine")
                        ) {
                            Icon(imageVector = Icons.Filled.Add, contentDescription = null, tint = Color.Black)
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Nueva Rutina", color = Color.Black, fontWeight = FontWeight.Bold)
                        }
                    }
                }

                if (routines.isEmpty()) {
                    item {
                        Card(
                            colors = CardDefaults.cardColors(containerColor = BentoCardBgDefault),
                            shape = RoundedCornerShape(28.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(32.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Icon(
                                        imageVector = Icons.Outlined.FitnessCenter,
                                        contentDescription = null,
                                        tint = LightSlate,
                                        modifier = Modifier.size(56.dp)
                                    )
                                    Spacer(modifier = Modifier.height(12.dp))
                                    Text(
                                        text = "No tienes rutinas registradas",
                                        color = Color.White,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = "Usa 'Nueva Rutina' o importa un Plan de Entrenamiento Pro para comenzar.",
                                        color = LightSlate,
                                        fontSize = 12.sp,
                                        textAlign = TextAlign.Center
                                    )
                                }
                            }
                        }
                    }
                } else {
                    items(routines) { routine ->
                        RoutineCard(
                            routine = routine,
                            onStartClick = { onStartWorkout(routine) },
                            onDeleteClick = { onDeleteRoutine(routine) }
                        )
                    }
                }
            }

            1 -> {
                // --- SUBTAB 1: EXERCISE LIBRARY ---
                item {
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text(
                            text = "Biblioteca de Ejercicios",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.ExtraBold,
                            color = Color.White
                        )
                        Text(
                            text = "Perfecciona tu técnica con guías explicativas paso a paso y simuladores biomecánicos.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = BentoTextSecondary
                        )
                    }
                }

                item {
                    // Search & Category Filters State
                    var searchQuery by remember { mutableStateOf("") }
                    var selectedCategoryFilter by remember { mutableStateOf("Todos") }

                    Card(
                        colors = CardDefaults.cardColors(containerColor = BentoCardBgDefault),
                        shape = RoundedCornerShape(24.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            // Search Box
                            OutlinedTextField(
                                value = searchQuery,
                                onValueChange = { searchQuery = it },
                                label = { Text("Buscar ejercicio...") },
                                leadingIcon = {
                                    Icon(
                                        imageVector = Icons.Filled.Search,
                                        contentDescription = null,
                                        tint = BentoAccentLavender
                                    )
                                },
                                singleLine = true,
                                shape = RoundedCornerShape(16.dp),
                                modifier = Modifier.fillMaxWidth(),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = BentoAccentLavender,
                                    unfocusedBorderColor = BentoBorderGray,
                                    focusedLabelColor = BentoAccentLavender,
                                    unfocusedLabelColor = BentoTextSecondary,
                                    focusedTextColor = Color.White,
                                    unfocusedTextColor = Color.White
                                )
                            )

                            // Quick Categories
                            val categories = listOf("Todos", "Fuerza", "Cardio", "Abdomen")
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                categories.forEach { cat ->
                                    val isCatSelected = selectedCategoryFilter == cat
                                    Box(
                                        modifier = Modifier
                                            .background(
                                                if (isCatSelected) BentoAccentLavender else BentoCardBgDarkDetail,
                                                RoundedCornerShape(12.dp)
                                            )
                                            .clickable { selectedCategoryFilter = cat }
                                            .padding(horizontal = 14.dp, vertical = 8.dp)
                                    ) {
                                        Text(
                                            text = cat,
                                            color = if (isCatSelected) BentoOnAccentLavender else Color.White,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 11.sp
                                        )
                                    }
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Filtered List
                    val filteredExercises = remember(searchQuery, selectedCategoryFilter) {
                        EXERCISE_LIBRARY.filter { ex ->
                            val matchesSearch = ex.name.lowercase().contains(searchQuery.lowercase()) ||
                                    ex.muscles.lowercase().contains(searchQuery.lowercase())
                            val matchesCat = selectedCategoryFilter == "Todos" || ex.category == selectedCategoryFilter
                            matchesSearch && matchesCat
                        }
                    }

                    if (filteredExercises.isEmpty()) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(200.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "No se encontraron ejercicios.",
                                color = BentoTextSecondary,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    } else {
                        Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                            filteredExercises.forEach { ex ->
                                LibraryExerciseCard(exercise = ex)
                            }
                        }
                    }
                }
            }

            2 -> {
                // --- SUBTAB 2: PLANS PRO (PREDEFINED ROUTINES FOR DIFFERENT GOALS) ---
                item {
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text(
                            text = "Planes de Entrenamiento Pro",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.ExtraBold,
                            color = Color.White
                        )
                        Text(
                            text = "Fija tu objetivo: Principiante, Avanzado, Pérdida de peso o Ganancia muscular.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = BentoTextSecondary
                        )
                    }
                }

                PREDEFINED_PLANS.forEach { plan ->
                    item {
                        PredefinedPlanCard(
                            routine = plan,
                            onImport = {
                                val newRoutine = plan.copy(
                                    id = 0,
                                    exercises = plan.exercises.map { it.copy(id = UUID.randomUUID().toString()) }
                                )
                                onImportRoutine(newRoutine)
                                onShowMessage("¡Se ha importado '${plan.name}' con éxito!")
                            },
                            onStart = { onStartWorkout(plan) }
                        )
                    }
                }
            }
        }
    }
}

// --- CORE COMPOSABLES FOR EXERCISE LIBRARY AND PREDEFINED PLANS ---
@Composable
fun LibraryExerciseCard(exercise: LibraryExercise) {
    var isExpanded by remember { mutableStateOf(false) }

    Card(
        colors = CardDefaults.cardColors(containerColor = BentoCardBgDefault),
        shape = RoundedCornerShape(24.dp),
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, BentoBorderGray, RoundedCornerShape(24.dp))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Animated Skeletal Canvas Gym Animator
                Card(
                    modifier = Modifier
                        .size(100.dp)
                        .border(1.dp, BentoBorderGray, RoundedCornerShape(16.dp)),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    ExerciseVisualCompanion(exerciseName = exercise.name)
                }

                // Metadata columns
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        SuggestionChip(
                            onClick = {},
                            label = { Text(exercise.category, color = BentoAccentLavender, fontSize = 9.sp, fontWeight = FontWeight.Bold) },
                            colors = SuggestionChipDefaults.suggestionChipColors(containerColor = BentoCardBgDarkDetail),
                            border = null,
                            shape = RoundedCornerShape(8.dp)
                        )
                        
                        Text(
                            text = exercise.difficulty,
                            color = when (exercise.difficulty) {
                                "Principiante" -> NeonGymGreen
                                "Intermedio" -> BentoAccentLavender
                                "Avanzado" -> Color(0xFFF2B8B5) // Soft red
                                else -> Color.White
                            },
                            fontWeight = FontWeight.Bold,
                            fontSize = 11.sp
                        )
                    }

                    Text(
                        text = exercise.name,
                        color = Color.White,
                        fontWeight = FontWeight.Black,
                        fontSize = 16.sp,
                        lineHeight = 20.sp
                    )

                    Text(
                        text = "Foco: ${exercise.muscles}",
                        color = BentoTextSecondary,
                        fontSize = 11.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = exercise.description,
                color = BentoTextSecondary,
                fontSize = 13.sp,
                lineHeight = 18.sp
            )

            // Step instructions expander drawer
            AnimatedVisibility(
                visible = isExpanded,
                enter = expandVertically() + fadeIn(),
                exit = shrinkVertically() + fadeOut()
            ) {
                Column(
                    modifier = Modifier.padding(top = 12.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Divider(color = BentoBorderGray, thickness = 1.dp)
                    Text(
                        text = "INSTRUCCIONES DE EJECUCIÓN",
                        color = BentoAccentLavender,
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 10.sp,
                        letterSpacing = 0.5.sp
                    )

                    exercise.instructions.forEachIndexed { idx, step ->
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(20.dp)
                                    .background(BentoAccentLavender, CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "${idx + 1}",
                                    color = BentoOnAccentLavender,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                            Text(
                                text = step,
                                color = Color.White,
                                fontSize = 12.sp,
                                modifier = Modifier.weight(1f),
                                lineHeight = 16.sp
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Expand/Collapse actionable button
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { isExpanded = !isExpanded }
                    .padding(vertical = 4.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = if (isExpanded) "Ocultar Técnica" else "Ver Instrucciones Paso a Paso",
                    color = BentoAccentLavender,
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp
                )
                Spacer(modifier = Modifier.width(4.dp))
                Icon(
                    imageVector = if (isExpanded) Icons.Filled.ExpandLess else Icons.Filled.ExpandMore,
                    contentDescription = null,
                    tint = BentoAccentLavender,
                    modifier = Modifier.size(16.dp)
                )
            }
        }
    }
}

@Composable
fun PredefinedPlanCard(
    routine: Routine,
    onImport: () -> Unit,
    onStart: () -> Unit
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = BentoCardBgDefault),
        shape = RoundedCornerShape(24.dp),
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, BentoBorderGray, RoundedCornerShape(24.dp))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        SuggestionChip(
                            onClick = {},
                            label = { Text(routine.category, color = BentoAccentLavender, fontWeight = FontWeight.Bold, fontSize = 10.sp) },
                            colors = SuggestionChipDefaults.suggestionChipColors(containerColor = BentoCardBgDarkDetail),
                            border = null,
                            shape = RoundedCornerShape(8.dp)
                        )
                        Text(
                            text = routine.dayOfWeek,
                            color = AccentTeal,
                            fontWeight = FontWeight.Bold,
                            fontSize = 11.sp
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = routine.name,
                        color = Color.White,
                        fontWeight = FontWeight.Black,
                        fontSize = 18.sp
                    )
                }

                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .background(BentoHighlightPurple, RoundedCornerShape(10.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Filled.Stars,
                        contentDescription = null,
                        tint = BentoOnHighlightPurple,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = routine.description,
                color = BentoTextSecondary,
                fontSize = 13.sp,
                lineHeight = 18.sp
            )

            Spacer(modifier = Modifier.height(12.dp))
            Divider(color = BentoBorderGray, thickness = 1.dp)
            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "EJERCICIOS INCLUIDOS (${routine.exercises.size})",
                color = BentoTextSecondary,
                fontWeight = FontWeight.Bold,
                fontSize = 11.sp,
                letterSpacing = 0.5.sp
            )

            Spacer(modifier = Modifier.height(8.dp))

            routine.exercises.forEach { exercise ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "• ${exercise.name}",
                        color = Color.White,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = if (exercise.category == "Cardio") "${exercise.durationMinutes} min" else "${exercise.sets} series x ${exercise.reps} reps",
                        color = BentoTextSecondary,
                        fontSize = 12.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Keep Plan Option
                Button(
                    onClick = onImport,
                    colors = ButtonDefaults.buttonColors(containerColor = BentoCardBgDarkDetail),
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier
                        .weight(1.5f)
                        .height(48.dp)
                        .border(1.dp, BentoAccentLavender, RoundedCornerShape(16.dp))
                ) {
                    Icon(
                        imageVector = Icons.Filled.BookmarkAdd,
                        contentDescription = null,
                        tint = BentoAccentLavender,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Seguir Plan",
                        color = BentoAccentLavender,
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp
                    )
                }

                // Instantly Start Workout Option
                Button(
                    onClick = onStart,
                    colors = ButtonDefaults.buttonColors(containerColor = BentoAccentLavender),
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier
                        .weight(1.5f)
                        .height(48.dp)
                ) {
                    Icon(
                        imageVector = Icons.Filled.PlayArrow,
                        contentDescription = null,
                        tint = BentoOnAccentLavender,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Empezar",
                        color = BentoOnAccentLavender,
                        fontWeight = FontWeight.Black,
                        fontSize = 12.sp
                    )
                }
            }
        }
    }
}

@Composable
fun RoutineCard(
    routine: Routine,
    onStartClick: () -> Unit,
    onDeleteClick: () -> Unit
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = DarkCharcoal),
        shape = RoundedCornerShape(24.dp),
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, SlateGray, RoundedCornerShape(24.dp)),
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        SuggestionChip(
                            onClick = {},
                            label = { Text(routine.category, color = NeonGymGreen, fontWeight = FontWeight.Bold) },
                            colors = SuggestionChipDefaults.suggestionChipColors(
                                containerColor = SlateGray
                            ),
                            border = null,
                            shape = RoundedCornerShape(8.dp)
                        )
                        if (routine.dayOfWeek.isNotEmpty()) {
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = routine.dayOfWeek,
                                fontSize = 12.sp,
                                color = AccentTeal,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = routine.name,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
                IconButton(
                    onClick = onDeleteClick,
                    modifier = Modifier.testTag("delete_routine_${routine.id}")
                ) {
                    Icon(imageVector = Icons.Filled.Delete, contentDescription = "Eliminar", tint = AlertRed)
                }
            }

            if (routine.description.isNotEmpty()) {
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = routine.description,
                    color = LightSlate,
                    fontSize = 13.sp,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }

            Spacer(modifier = Modifier.height(14.dp))
            Divider(color = SlateGray, thickness = 1.dp)
            Spacer(modifier = Modifier.height(14.dp))

            // Exercise summary list
            Text(
                text = "EJERCICIOS COMPRENDIDOS (${routine.exercises.size})",
                fontSize = 11.sp,
                fontWeight = FontWeight.ExtraBold,
                color = LightSlate,
                letterSpacing = 1.sp
            )
            Spacer(modifier = Modifier.height(8.dp))

            routine.exercises.take(3).forEach { exercise ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(text = "• ${exercise.name}", color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                    Text(
                        text = if (exercise.category == "Cardio") "${exercise.durationMinutes} min" else "${exercise.sets} series x ${exercise.reps} reps",
                        color = LightSlate,
                        fontSize = 12.sp
                    )
                }
            }

            if (routine.exercises.size > 3) {
                Text(
                    text = "+ ${routine.exercises.size - 3} ejercicios más",
                    color = AccentTeal,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = onStartClick,
                colors = ButtonDefaults.buttonColors(containerColor = SlateGray),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .border(1.dp, NeonGymGreen, RoundedCornerShape(16.dp)),
                shape = RoundedCornerShape(16.dp)
            ) {
                Icon(imageVector = Icons.Filled.PlayArrow, contentDescription = null, tint = NeonGymGreen)
                Spacer(modifier = Modifier.width(6.dp))
                Text("Iniciar Entrenamiento", color = NeonGymGreen, fontWeight = FontWeight.Black)
            }
        }
    }
}

// ---------------------- METRICS TAB ----------------------
@Composable
fun MetricsTab(
    metrics: List<PhysicalMetric>,
    onAddMetricClick: () -> Unit,
    onDeleteMetric: (PhysicalMetric) -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Fisiología Corporal",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color.White
                    )
                    Text(
                        text = "Historial de métricas físicas",
                        color = LightSlate,
                        fontSize = 12.sp
                    )
                }
                Button(
                    onClick = onAddMetricClick,
                    colors = ButtonDefaults.buttonColors(containerColor = NeonGymGreen),
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.testTag("btn_add_metric")
                ) {
                    Icon(imageVector = Icons.Filled.Add, contentDescription = null, tint = Color.Black)
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Registrar Medida", color = Color.Black, fontWeight = FontWeight.Bold)
                }
            }
        }

        if (metrics.isEmpty()) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = Icons.Outlined.MonitorWeight,
                            contentDescription = null,
                            tint = LightSlate,
                            modifier = Modifier.size(56.dp)
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = "Aún no hay métricas corporales",
                            color = Color.White,
                            fontWeight = FontWeight.SemiBold
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Presiona 'Registrar Medida' para asentar tu composición física actual.",
                            color = LightSlate,
                            fontSize = 12.sp,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
        } else {
            items(metrics) { metric ->
                MetricCard(
                    metric = metric,
                    onDeleteClick = { onDeleteMetric(metric) }
                )
            }
        }
    }
}

@Composable
fun MetricCard(
    metric: PhysicalMetric,
    onDeleteClick: () -> Unit
) {
    val dateString = remember(metric.timestamp) {
        val sdf = SimpleDateFormat("dd MMMM yyyy, HH:mm", Locale.getDefault())
        sdf.format(Date(metric.timestamp))
    }

    Card(
        colors = CardDefaults.cardColors(containerColor = DarkCharcoal),
        shape = RoundedCornerShape(24.dp),
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, SlateGray, RoundedCornerShape(24.dp))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Filled.CalendarToday,
                        contentDescription = null,
                        tint = AccentTeal,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = dateString,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        fontSize = 14.sp
                    )
                }
                IconButton(
                    onClick = onDeleteClick,
                    modifier = Modifier.testTag("delete_metric_${metric.id}")
                ) {
                    Icon(imageVector = Icons.Filled.Delete, contentDescription = "Eliminar", tint = AlertRed)
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Primary Composition Measures
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Weight
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .background(SlateGray, RoundedCornerShape(16.dp))
                        .padding(12.dp)
                ) {
                    Column {
                        Text("PESO", color = LightSlate, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                        Text("${metric.weightKg} kg", color = NeonGymGreen, fontSize = 20.sp, fontWeight = FontWeight.Black)
                    }
                }

                // Body Fat
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .background(SlateGray, RoundedCornerShape(16.dp))
                        .padding(12.dp)
                ) {
                    Column {
                        Text("GRASA CORPORAL", color = LightSlate, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                        Text(
                            if (metric.bodyFatPercentage > 0) "${metric.bodyFatPercentage}%" else "--",
                            color = Color.White,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Black
                        )
                    }
                }

                // Muscle
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .background(SlateGray, RoundedCornerShape(16.dp))
                        .padding(12.dp)
                ) {
                    Column {
                        Text("MÚSCULO", color = LightSlate, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                        Text(
                            if (metric.muscleMassKg > 0) "${metric.muscleMassKg} kg" else "--",
                            color = AccentTeal,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Black
                        )
                    }
                }
            }

            // Secondary Circumference Measures
            if (metric.chestCm > 0 || metric.waistCm > 0 || metric.bicepCm > 0 || metric.thighCm > 0) {
                Spacer(modifier = Modifier.height(12.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    if (metric.chestCm > 0) {
                        MetricSmallItem("Pecho", "${metric.chestCm} cm")
                    }
                    if (metric.waistCm > 0) {
                        MetricSmallItem("Cintura", "${metric.waistCm} cm")
                    }
                    if (metric.bicepCm > 0) {
                        MetricSmallItem("Bíceps", "${metric.bicepCm} cm")
                    }
                    if (metric.thighCm > 0) {
                        MetricSmallItem("Muslo", "${metric.thighCm} cm")
                    }
                }
            }

            if (metric.notes.isNotEmpty()) {
                Spacer(modifier = Modifier.height(12.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(DeepCarbon, RoundedCornerShape(8.dp))
                        .padding(10.dp)
                ) {
                    Text(
                        text = metric.notes,
                        color = Color.White,
                        fontSize = 12.sp,
                        fontStyle = androidx.compose.ui.text.font.FontStyle.Italic
                    )
                }
            }
        }
    }
}

@Composable
fun MetricSmallItem(label: String, value: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(label.uppercase(), color = LightSlate, fontSize = 9.sp, fontWeight = FontWeight.Bold)
        Text(value, color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
    }
}

// ---------------------- PROGRESS TAB & CUSTOM LINE CHART ----------------------
@Composable
fun ProgressTab(
    metrics: List<PhysicalMetric>,
    workoutLogs: List<WorkoutLog>
) {
    var chartSelection by remember { mutableStateOf("Peso") } // "Peso", "Grasa", "Músculo"

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Column {
                Text(
                    text = "Línea de Progreso",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.ExtraBold,
                    color = Color.White
                )
                Text(
                    text = "Gráficos analíticos de evolución corporal",
                    color = LightSlate,
                    fontSize = 12.sp
                )
            }
        }

        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = DarkCharcoal),
                shape = RoundedCornerShape(24.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, SlateGray, RoundedCornerShape(24.dp))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Comparador Biométrico",
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp,
                            color = Color.White
                        )

                        // Selector chips
                        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            listOf("Peso", "Grasa", "Músculo").forEach { tag ->
                                val active = chartSelection == tag
                                Box(
                                    modifier = Modifier
                                        .background(
                                            if (active) NeonGymGreen else SlateGray,
                                            RoundedCornerShape(8.dp)
                                        )
                                        .clickable { chartSelection = tag }
                                        .padding(horizontal = 8.dp, vertical = 4.dp)
                                ) {
                                    Text(
                                        text = tag,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (active) Color.Black else Color.White
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Draw Interactive Custom Line Chart
                    CustomLineChart(
                        metrics = metrics,
                        selectedType = chartSelection
                    )
                }
            }
        }

        item {
            Text(
                text = "Historial Reciente de Entrenamientos",
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                color = Color.White
            )
        }

        if (workoutLogs.isEmpty()) {
            item {
                Card(
                    colors = CardDefaults.cardColors(containerColor = DarkCharcoal),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Completa tu primer entrenamiento para generar historial.",
                            color = LightSlate,
                            fontSize = 13.sp,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
        } else {
            items(workoutLogs) { log ->
                val logSdf = remember { SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()) }
                Card(
                    colors = CardDefaults.cardColors(containerColor = DarkCharcoal),
                    shape = RoundedCornerShape(20.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, SlateGray, RoundedCornerShape(20.dp))
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = log.routineName,
                                fontWeight = FontWeight.Bold,
                                color = Color.White,
                                fontSize = 15.sp
                            )
                            Text(
                                text = logSdf.format(Date(log.timestamp)),
                                color = AccentTeal,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Filled.Timer,
                                contentDescription = null,
                                tint = LightSlate,
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "Duración: ${log.durationMinutes} minutos",
                                color = LightSlate,
                                fontSize = 12.sp
                            )
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "Feed: \"${log.feedback}\"",
                            color = Color.White,
                            fontSize = 12.sp,
                            fontStyle = androidx.compose.ui.text.font.FontStyle.Italic
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun CustomLineChart(
    metrics: List<PhysicalMetric>,
    selectedType: String
) {
    // Sort chronological ascending
    val sortedMetrics = remember(metrics) { metrics.sortedBy { it.timestamp } }

    if (sortedMetrics.size < 2) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
                .background(DeepCarbon, RoundedCornerShape(12.dp)),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "Registra al menos 2 medidas físicas en días o momentos diferentes para graficar el progreso.",
                textAlign = TextAlign.Center,
                color = LightSlate,
                fontSize = 12.sp,
                modifier = Modifier.padding(16.dp)
            )
        }
        return
    }

    // Extract values
    val values = remember(sortedMetrics, selectedType) {
        sortedMetrics.map {
            val d = when (selectedType) {
                "Gráfico" -> it.weightKg
                "Grasa" -> it.bodyFatPercentage
                "Músculo" -> it.muscleMassKg
                else -> it.weightKg // "Peso"
            }
            d.toFloat()
        }
    }

    val dates = remember(sortedMetrics) {
        val sdf = SimpleDateFormat("dd/MM", Locale.getDefault())
        sortedMetrics.map { sdf.format(Date(it.timestamp)) }
    }

    val maxVal = remember(values) { (values.maxOrNull() ?: 1.0).toFloat() }
    val minVal = remember(values) { (values.minOrNull() ?: 0.0).toFloat() }

    // Chart Draw Box
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = "Evolución de $selectedType (Últimas mediciones)",
            color = LightSlate,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        Canvas(
            modifier = Modifier
                .fillMaxWidth()
                .height(180.dp)
                .background(DeepCarbon, RoundedCornerShape(12.dp))
                .padding(top = 16.dp, bottom = 12.dp, start = 12.dp, end = 12.dp)
                .testTag("interactive_canvas_chart")
        ) {
            val width = size.width
            val height = size.height

            val leftPadding = 35f
            val bottomPadding = 35f
            val rightPadding = 15f
            val topPadding = 15f

            val chartWidth = width - leftPadding - rightPadding
            val chartHeight = height - topPadding - bottomPadding

            val numPoints = values.size
            val valRange = if (maxVal == minVal) 10f else (maxVal - minVal)

            // Adjust min and max boundaries slightly so curve doesn't clip peak and bottom
            val displayMin = minVal - (valRange * 0.15f)
            val displayMax = maxVal + (valRange * 0.15f)
            val displayRange = displayMax - displayMin

            val primaryColor = NeonGymGreen
            val accentColor = AccentTeal

            // Draw Y-Axis lines and numbers (Divided in 3 sections)
            val gridCount = 3
            for (i in 0..gridCount) {
                val gridVal = displayMin + (displayRange / gridCount) * i
                val y = height - bottomPadding - (i.toFloat() / gridCount) * chartHeight

                // Grid lines
                drawLine(
                    color = MediumGray.copy(alpha = 0.4f),
                    start = Offset(leftPadding, y),
                    end = Offset(width - rightPadding, y),
                    strokeWidth = 1f
                )
            }

            // Map data items to coordinates on canvas
            val coordinates = values.mapIndexed { index, value ->
                val x = leftPadding + (index.toFloat() / (numPoints - 1)) * chartWidth
                val y = height - bottomPadding - ((value - displayMin) / displayRange) * chartHeight
                Offset(x, y)
            }

            // Draw Glow shadow under the line graph
            val fillPath = Path().apply {
                moveTo(leftPadding, height - bottomPadding)
                lineTo(coordinates.first().x, coordinates.first().y)
                for (i in 1 until coordinates.size) {
                    // Draw cubic/quadratic curve or simple line segments
                    val prev = coordinates[i - 1]
                    val curr = coordinates[i]
                    val controlX = (prev.x + curr.x) / 2
                    quadraticTo(controlX, prev.y, controlX, curr.y)
                    lineTo(curr.x, curr.y)
                }
                lineTo(coordinates.last().x, height - bottomPadding)
                close()
            }

            drawPath(
                path = fillPath,
                brush = Brush.verticalGradient(
                    colors = listOf(primaryColor.copy(alpha = 0.35f), Color.Transparent),
                    startY = topPadding,
                    endY = height - bottomPadding
                )
            )

            // Draw curving primary outline line
            val linePath = Path().apply {
                moveTo(coordinates.first().x, coordinates.first().y)
                for (i in 1 until coordinates.size) {
                    val prev = coordinates[i - 1]
                    val curr = coordinates[i]
                    val controlX = (prev.x + curr.x) / 2
                    quadraticTo(controlX, prev.y, controlX, curr.y)
                    lineTo(curr.x, curr.y)
                }
            }

            drawPath(
                path = linePath,
                color = primaryColor,
                style = Stroke(width = 3.dp.toPx(), cap = StrokeCap.Round, join = StrokeJoin.Round)
            )

            // Draw each individual coordinate dot
            coordinates.forEachIndexed { idx, point ->
                // Outer subtle glowing aura
                drawCircle(
                    color = accentColor.copy(alpha = 0.4f),
                    radius = 8.dp.toPx(),
                    center = point
                )
                // Main Point
                drawCircle(
                    color = primaryColor,
                    radius = 4.dp.toPx(),
                    center = point
                )
                // Dark core
                drawCircle(
                    color = DeepCarbon,
                    radius = 2.dp.toPx(),
                    center = point
                )
            }

            // Draw chronological date notches on X-Axis
            coordinates.forEachIndexed { index, point ->
                if (index == 0 || index == numPoints - 1 || numPoints < 5 || index % 2 == 0) {
                    val dateLabel = dates[index]
                    // Axis line tick line
                    drawLine(
                        color = LightSlate.copy(alpha = 0.7f),
                        start = Offset(point.x, height - bottomPadding),
                        end = Offset(point.x, height - bottomPadding + 4.dp.toPx()),
                        strokeWidth = 2f
                    )
                }
            }
        }

        // Draw helper labels below chart
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(text = "Inicio (${dates.first()})", color = LightSlate, fontSize = 10.sp)
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .background(NeonGymGreen, CircleShape)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "Línea de tendencia física",
                    color = Color.White,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold
                )
            }
            Text(text = "Último (${dates.last()})", color = AccentTeal, fontSize = 10.sp)
        }
    }
}


// ---------------------- ACTIVE SESSION OVERLAY ----------------------
@Composable
fun ActiveWorkoutOverlay(
    routine: Routine,
    viewModel: FitnessViewModel
) {
    val activeExercises by viewModel.activeExercises.collectAsStateWithLifecycle()
    val startTime by viewModel.workoutStartTime.collectAsStateWithLifecycle()

    var secondsElapsed by remember { mutableStateOf(0) }
    var userFeedback by remember { mutableStateOf("") }
    var showFinishDialog by remember { mutableStateOf(false) }

    // Start tick timer
    LaunchedEffect(startTime) {
        if (startTime > 0L) {
            while (true) {
                secondsElapsed = (((System.currentTimeMillis() - startTime) / 1000).toInt())
                delay(1000)
            }
        }
    }

    val timerString = remember(secondsElapsed) {
        val hrs = secondsElapsed / 3600
        val mins = (secondsElapsed % 3600) / 60
        val secs = secondsElapsed % 60
        if (hrs > 0) {
            String.format("%02d:%02d:%02d", hrs, mins, secs)
        } else {
            String.format("%02d:%02d", mins, secs)
        }
    }

    Surface(
        modifier = Modifier
            .fillMaxSize()
            .testTag("active_workout_screen"),
        color = DeepCarbon
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            // Timer Header Row
            Card(
                colors = CardDefaults.cardColors(containerColor = DarkCharcoal),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, NeonGymGreen, RoundedCornerShape(16.dp))
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "ENTRENO EN CURSO",
                            color = NeonGymGreen,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Black,
                            letterSpacing = 1.2.sp
                        )
                        Text(
                            text = routine.name,
                            color = Color.White,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }

                    // Pulse Timer Badge
                    Box(
                        modifier = Modifier
                            .background(SlateGray, RoundedCornerShape(12.dp))
                            .padding(horizontal = 12.dp, vertical = 6.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .padding(end = 6.dp)
                                    .size(6.dp)
                                    .background(AlertRed, CircleShape)
                            )
                            Text(
                                text = timerString,
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace,
                                fontSize = 16.sp
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Body List of customizable exercises
            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(activeExercises) { exercise ->
                    ActiveExerciseItem(
                        exercise = exercise,
                        onToggleDone = { viewModel.toggleExerciseCompletion(exercise.id) },
                        onValueChange = { sets, reps, weight, dur ->
                            viewModel.updateExerciseStats(exercise.id, sets, reps, weight, dur)
                        }
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Options buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Button(
                    onClick = { viewModel.cancelWorkout() },
                    colors = ButtonDefaults.buttonColors(containerColor = SlateGray),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .weight(1f)
                        .height(50.dp)
                ) {
                    Text("Cancelar", color = AlertRed, fontWeight = FontWeight.Bold)
                }

                Button(
                    onClick = { showFinishDialog = true },
                    colors = ButtonDefaults.buttonColors(containerColor = NeonGymGreen),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .weight(1.5f)
                        .height(50.dp)
                        .testTag("btn_complete_workout")
                ) {
                    Text("Finalizar Entrenamiento", color = Color.Black, fontWeight = FontWeight.Black)
                }
            }
        }
    }

    if (showFinishDialog) {
        Dialog(
            onDismissRequest = { showFinishDialog = false },
            properties = DialogProperties(usePlatformDefaultWidth = false)
        ) {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(32.dp),
                shape = RoundedCornerShape(20.dp),
                color = DarkCharcoal
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        imageVector = Icons.Filled.EmojiEvents,
                        contentDescription = null,
                        tint = NeonGymGreen,
                        modifier = Modifier.size(56.dp)
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "¡Excelente Trabajo!",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color.White
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Has entrenado ${timerString}. Añade unas notas rápidas para tu yo del futuro sobre cómo te sentiste.",
                        color = LightSlate,
                        fontSize = 13.sp,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    OutlinedTextField(
                        value = userFeedback,
                        onValueChange = { userFeedback = it },
                        placeholder = { Text("Ej: ¡Me sentí con mucha fuerza! Subí peso en sentadilla", fontSize = 13.sp) },
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = NeonGymGreen,
                            unfocusedBorderColor = SlateGray,
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            focusedPlaceholderColor = LightSlate,
                            unfocusedPlaceholderColor = LightSlate
                        ),
                        maxLines = 3
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        TextButton(
                            onClick = { showFinishDialog = false },
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("Atrás", color = LightSlate)
                        }

                        Button(
                            onClick = {
                                viewModel.finishWorkout(
                                    userFeedback.ifBlank { "¡Gran entrenamiento guiado!" }
                                )
                                showFinishDialog = false
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = NeonGymGreen),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.weight(1.5f)
                        ) {
                            Text("Guardar Log", color = Color.Black, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ActiveExerciseItem(
    exercise: Exercise,
    onToggleDone: () -> Unit,
    onValueChange: (Int, Int, Double, Int) -> Unit
) {
    var setsInput by remember { mutableStateOf(exercise.sets.toString()) }
    var repsInput by remember { mutableStateOf(exercise.reps.toString()) }
    var weightInput by remember { mutableStateOf(exercise.weightKg.toString()) }
    var durationInput by remember { mutableStateOf(exercise.durationMinutes.toString()) }

    // Synchronize callback values changes inside
    fun triggerChange() {
        val s = setsInput.toIntOrNull() ?: exercise.sets
        val r = repsInput.toIntOrNull() ?: exercise.reps
        val w = weightInput.toDoubleOrNull() ?: exercise.weightKg
        val d = durationInput.toIntOrNull() ?: exercise.durationMinutes
        onValueChange(s, r, w, d)
    }

    Card(
        colors = CardDefaults.cardColors(
            containerColor = if (exercise.isCompleted) SlateGray.copy(alpha = 0.5f) else DarkCharcoal
        ),
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier
            .fillMaxWidth()
            .border(
                1.dp,
                if (exercise.isCompleted) NeonGymGreen.copy(alpha = 0.5f) else SlateGray,
                RoundedCornerShape(16.dp)
            )
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f)
                ) {
                    // Completer Checkbox Circle
                    Box(
                        modifier = Modifier
                            .size(28.dp)
                            .background(
                                if (exercise.isCompleted) NeonGymGreen else Color.Transparent,
                                CircleShape
                            )
                            .border(2.dp, NeonGymGreen, CircleShape)
                            .clickable { onToggleDone() },
                        contentAlignment = Alignment.Center
                    ) {
                        if (exercise.isCompleted) {
                            Icon(
                                imageVector = Icons.Filled.Check,
                                contentDescription = null,
                                tint = Color.Black,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Column {
                        Text(
                            text = exercise.name,
                            fontWeight = FontWeight.Bold,
                            color = if (exercise.isCompleted) LightSlate else Color.White,
                            fontSize = 15.sp
                        )
                        Text(
                            text = if (exercise.category == "Cardio") "Cardio aeróbico" else "Fuerza / Hipertrofia",
                            color = LightSlate,
                            fontSize = 11.sp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Target adjustments row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                if (exercise.category != "Cardio") {
                    // Sets
                    OutlinedTextField(
                        value = setsInput,
                        onValueChange = {
                            setsInput = it
                            triggerChange()
                        },
                        label = { Text("Series", fontSize = 10.sp) },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.weight(1f),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = NeonGymGreen,
                            unfocusedBorderColor = SlateGray,
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White
                        )
                    )

                    // Reps
                    OutlinedTextField(
                        value = repsInput,
                        onValueChange = {
                            repsInput = it
                            triggerChange()
                        },
                        label = { Text("Reps", fontSize = 10.sp) },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.weight(1f),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = NeonGymGreen,
                            unfocusedBorderColor = SlateGray,
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White
                        )
                    )

                    // Weight
                    OutlinedTextField(
                        value = weightInput,
                        onValueChange = {
                            weightInput = it
                            triggerChange()
                        },
                        label = { Text("Peso (kg)", fontSize = 10.sp) },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.weight(1.2f),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = NeonGymGreen,
                            unfocusedBorderColor = SlateGray,
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White
                        )
                    )
                } else {
                    // Duration Minutes
                    OutlinedTextField(
                        value = durationInput,
                        onValueChange = {
                            durationInput = it
                            triggerChange()
                        },
                        label = { Text("Duración (min)", fontSize = 11.sp) },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = NeonGymGreen,
                            unfocusedBorderColor = SlateGray,
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White
                        )
                    )
                }
            }
        }
    }
}


// ---------------------- DIALOG: ADD NEW ROUTINE ----------------------
@Composable
fun AddRoutineDialog(
    onDismiss: () -> Unit,
    onSave: (String, String, String, String, List<Exercise>) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("Fuerza") }
    var dayOfWeek by remember { mutableStateOf("") }

    // Temporary list of exercises builder
    val exercises = remember { mutableStateListOf<Exercise>() }
    var exerciseNameInput by remember { mutableStateOf("") }
    var exerciseCategoryInput by remember { mutableStateOf("Fuerza") }
    var exerciseSetsInput by remember { mutableStateOf("4") }
    var exerciseRepsInput by remember { mutableStateOf("10") }
    var exerciseWeightInput by remember { mutableStateOf("20") }
    var exerciseDurationInput by remember { mutableStateOf("10") }

    val focusManager = LocalFocusManager.current

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .background(DeepCarbon),
            color = DeepCarbon
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(20.dp)
            ) {
                // Header of Builder
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Crear Nueva Rutina",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Black,
                        color = Color.White
                    )
                    IconButton(onClick = onDismiss) {
                        Icon(imageVector = Icons.Filled.Close, contentDescription = "Close", tint = Color.White)
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                LazyColumn(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    item {
                        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                            // Name
                            OutlinedTextField(
                                value = name,
                                onValueChange = { name = it },
                                label = { Text("Nombre de la Rutina (Ej: Super Torso)") },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("input_routine_name"),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = NeonGymGreen,
                                    unfocusedBorderColor = SlateGray,
                                    focusedTextColor = Color.White,
                                    unfocusedTextColor = Color.White
                                )
                            )

                            // Description
                            OutlinedTextField(
                                value = description,
                                onValueChange = { description = it },
                                label = { Text("Breve descripción / Enfoque") },
                                modifier = Modifier.fillMaxWidth(),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = NeonGymGreen,
                                    unfocusedBorderColor = SlateGray,
                                    focusedTextColor = Color.White,
                                    unfocusedTextColor = Color.White
                                )
                            )

                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                // Category Selector
                                Box(modifier = Modifier.weight(1f)) {
                                    OutlinedTextField(
                                        value = category,
                                        onValueChange = { category = it },
                                        label = { Text("Categoría (Ej: Fuerza)") },
                                        modifier = Modifier.fillMaxWidth(),
                                        colors = OutlinedTextFieldDefaults.colors(
                                            focusedBorderColor = NeonGymGreen,
                                            unfocusedBorderColor = SlateGray,
                                            focusedTextColor = Color.White,
                                            unfocusedTextColor = Color.White
                                        )
                                    )
                                }

                                // Days of week
                                OutlinedTextField(
                                    value = dayOfWeek,
                                    onValueChange = { dayOfWeek = it },
                                    label = { Text("Días / Frecuencia (Ej: Lunes)") },
                                    modifier = Modifier.weight(1.2f),
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedBorderColor = NeonGymGreen,
                                        unfocusedBorderColor = SlateGray,
                                        focusedTextColor = Color.White,
                                        unfocusedTextColor = Color.White
                                    )
                                )
                            }
                        }
                    }

                    item {
                        Divider(color = MediumGray)
                    }

                    // ADD EXERCISES FORM BUILDER SECTION
                    item {
                        Card(
                            colors = CardDefaults.cardColors(containerColor = DarkCharcoal),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.border(1.dp, SlateGray, RoundedCornerShape(12.dp))
                        ) {
                            Column(modifier = Modifier.padding(14.dp)) {
                                Text(
                                    text = "Añadir Ejercicio",
                                    fontWeight = FontWeight.Bold,
                                    color = NeonGymGreen,
                                    fontSize = 14.sp
                                )
                                Spacer(modifier = Modifier.height(10.dp))

                                OutlinedTextField(
                                    value = exerciseNameInput,
                                    onValueChange = { exerciseNameInput = it },
                                    label = { Text("Nombre del Ejercicio (Ej: Sentadillas)") },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .testTag("input_exercise_name"),
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedBorderColor = NeonGymGreen,
                                        unfocusedBorderColor = SlateGray,
                                        focusedTextColor = Color.White,
                                        unfocusedTextColor = Color.White
                                    )
                                )

                                Spacer(modifier = Modifier.height(10.dp))

                                // Exercise properties
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    // Row inputs
                                    OutlinedTextField(
                                        value = exerciseSetsInput,
                                        onValueChange = { exerciseSetsInput = it },
                                        label = { Text("Series", fontSize = 11.sp) },
                                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                        modifier = Modifier.weight(1f),
                                        colors = OutlinedTextFieldDefaults.colors(
                                            focusedBorderColor = NeonGymGreen,
                                            unfocusedBorderColor = SlateGray,
                                            focusedTextColor = Color.White,
                                            unfocusedTextColor = Color.White
                                        )
                                    )

                                    OutlinedTextField(
                                        value = exerciseRepsInput,
                                        onValueChange = { exerciseRepsInput = it },
                                        label = { Text("Reps", fontSize = 11.sp) },
                                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                        modifier = Modifier.weight(1f),
                                        colors = OutlinedTextFieldDefaults.colors(
                                            focusedBorderColor = NeonGymGreen,
                                            unfocusedBorderColor = SlateGray,
                                            focusedTextColor = Color.White,
                                            unfocusedTextColor = Color.White
                                        )
                                    )

                                    OutlinedTextField(
                                        value = exerciseWeightInput,
                                        onValueChange = { exerciseWeightInput = it },
                                        label = { Text("Kilos", fontSize = 11.sp) },
                                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                        modifier = Modifier.weight(1f),
                                        colors = OutlinedTextFieldDefaults.colors(
                                            focusedBorderColor = NeonGymGreen,
                                            unfocusedBorderColor = SlateGray,
                                            focusedTextColor = Color.White,
                                            unfocusedTextColor = Color.White
                                        )
                                    )
                                }

                                Spacer(modifier = Modifier.height(12.dp))

                                Button(
                                    onClick = {
                                        if (exerciseNameInput.isNotBlank()) {
                                            val sets = exerciseSetsInput.toIntOrNull() ?: 4
                                            val reps = exerciseRepsInput.toIntOrNull() ?: 10
                                            val weight = exerciseWeightInput.toDoubleOrNull() ?: 0.0
                                            val duration = exerciseDurationInput.toIntOrNull() ?: 0

                                            exercises.add(
                                                Exercise(
                                                    name = exerciseNameInput,
                                                    sets = sets,
                                                    reps = reps,
                                                    weightKg = weight,
                                                    durationMinutes = duration,
                                                    category = exerciseCategoryInput
                                                )
                                            )
                                            // Reset builder input
                                            exerciseNameInput = ""
                                            focusManager.clearFocus()
                                        }
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = SlateGray),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(44.dp)
                                        .border(1.dp, AccentTeal, RoundedCornerShape(10.dp)),
                                    shape = RoundedCornerShape(10.dp)
                                ) {
                                    Icon(imageVector = Icons.Filled.AddCircleOutline, contentDescription = null, tint = AccentTeal)
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("Agregar a la Lista", color = AccentTeal, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }

                    // Exercises currently added
                    item {
                        Text(
                            text = "EJERCICIOS AGREGADOS (${exercises.size})",
                            style = MaterialTheme.typography.bodySmall,
                            fontWeight = FontWeight.ExtraBold,
                            color = LightSlate,
                            letterSpacing = 1.sp
                        )
                    }

                    if (exercises.isEmpty()) {
                        item {
                            Text(
                                "No has agregado ningún ejercicio a esta rutina aún.",
                                fontSize = 12.sp,
                                color = LightSlate
                            )
                        }
                    } else {
                        items(exercises) { ex ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(DarkCharcoal, RoundedCornerShape(8.dp))
                                    .border(1.dp, SlateGray, RoundedCornerShape(8.dp))
                                    .padding(horizontal = 12.dp, vertical = 8.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Filled.FitnessCenter, contentDescription = null, tint = NeonGymGreen, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(ex.name, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                }
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text("${ex.sets}x${ex.reps} | ${ex.weightKg}kg", color = LightSlate, fontSize = 12.sp)
                                    Spacer(modifier = Modifier.width(8.dp))
                                    IconButton(
                                        onClick = { exercises.remove(ex) },
                                        modifier = Modifier.size(24.dp)
                                    ) {
                                        Icon(imageVector = Icons.Filled.Close, contentDescription = "Quitar", tint = AlertRed, modifier = Modifier.size(16.dp))
                                    }
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Bottom Action Save Buttons
                Button(
                    onClick = {
                        if (name.isNotBlank()) {
                            onSave(name, description, category, dayOfWeek, exercises.toList())
                        }
                    },
                    enabled = name.isNotBlank(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = NeonGymGreen,
                        disabledContainerColor = SlateGray
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                        .testTag("btn_save_routine"),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Guardar Rutina Completa", color = Color.Black, fontWeight = FontWeight.Black)
                }
            }
        }
    }
}


// ---------------------- DIALOG: ADD NEW BODY PHYS PROPS METRIC ----------------------
@Composable
fun AddMetricDialog(
    onDismiss: () -> Unit,
    onSave: (Double, Double, Double, Double, Double, Double, Double, String) -> Unit
) {
    var weightInput by remember { mutableStateOf("") }
    var fatInput by remember { mutableStateOf("") }
    var muscleInput by remember { mutableStateOf("") }
    var chestInput by remember { mutableStateOf("") }
    var waistInput by remember { mutableStateOf("") }
    var bicepInput by remember { mutableStateOf("") }
    var thighInput by remember { mutableStateOf("") }
    var notesInput by remember { mutableStateOf("") }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            color = DarkCharcoal
        ) {
            Column(
                modifier = Modifier
                    .padding(20.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Registrar Medidas",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color.White
                    )
                    IconButton(onClick = onDismiss) {
                        Icon(imageVector = Icons.Filled.Close, contentDescription = "Close", tint = Color.White)
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                LazyColumn(
                    modifier = Modifier
                        .weight(1f, fill = false)
                        .fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    // Critical items
                    item {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            // Weight
                            OutlinedTextField(
                                value = weightInput,
                                onValueChange = { weightInput = it },
                                label = { Text("Peso (kg)", fontSize = 11.sp) },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                modifier = Modifier
                                    .weight(1f)
                                    .testTag("input_metric_weight"),
                                singleLine = true,
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = NeonGymGreen,
                                    unfocusedBorderColor = SlateGray,
                                    focusedTextColor = Color.White,
                                    unfocusedTextColor = Color.White
                                )
                            )

                            // Fat
                            OutlinedTextField(
                                value = fatInput,
                                onValueChange = { fatInput = it },
                                label = { Text("Grasa %", fontSize = 11.sp) },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                modifier = Modifier.weight(1f),
                                singleLine = true,
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = NeonGymGreen,
                                    unfocusedBorderColor = SlateGray,
                                    focusedTextColor = Color.White,
                                    unfocusedTextColor = Color.White
                                )
                            )

                            // Muscle
                            OutlinedTextField(
                                value = muscleInput,
                                onValueChange = { muscleInput = it },
                                label = { Text("Músculo kg", fontSize = 11.sp) },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                modifier = Modifier.weight(1.1f),
                                singleLine = true,
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = NeonGymGreen,
                                    unfocusedBorderColor = SlateGray,
                                    focusedTextColor = Color.White,
                                    unfocusedTextColor = Color.White
                                )
                            )
                        }
                    }

                    // Tapes measurements
                    item {
                        Text(
                            "MEDICIONES DE CINTA (OPCIONALES)",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = LightSlate,
                            letterSpacing = 1.sp,
                            modifier = Modifier.padding(top = 8.dp)
                        )
                    }

                    item {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            // Chest
                            OutlinedTextField(
                                value = chestInput,
                                onValueChange = { chestInput = it },
                                label = { Text("Pecho", fontSize = 11.sp) },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                modifier = Modifier.weight(1f),
                                singleLine = true,
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = NeonGymGreen,
                                    unfocusedBorderColor = SlateGray,
                                    focusedTextColor = Color.White,
                                    unfocusedTextColor = Color.White
                                )
                            )

                            // Waist
                            OutlinedTextField(
                                value = waistInput,
                                onValueChange = { waistInput = it },
                                label = { Text("Cintura", fontSize = 11.sp) },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                modifier = Modifier.weight(1f),
                                singleLine = true,
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = NeonGymGreen,
                                    unfocusedBorderColor = SlateGray,
                                    focusedTextColor = Color.White,
                                    unfocusedTextColor = Color.White
                                )
                            )
                        }
                    }

                    item {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            // Bicep
                            OutlinedTextField(
                                value = bicepInput,
                                onValueChange = { bicepInput = it },
                                label = { Text("Bíceps", fontSize = 11.sp) },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                modifier = Modifier.weight(1f),
                                singleLine = true,
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = NeonGymGreen,
                                    unfocusedBorderColor = SlateGray,
                                    focusedTextColor = Color.White,
                                    unfocusedTextColor = Color.White
                                )
                            )

                            // Thigh
                            OutlinedTextField(
                                value = thighInput,
                                onValueChange = { thighInput = it },
                                label = { Text("Muslo", fontSize = 11.sp) },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                modifier = Modifier.weight(1f),
                                singleLine = true,
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = NeonGymGreen,
                                    unfocusedBorderColor = SlateGray,
                                    focusedTextColor = Color.White,
                                    unfocusedTextColor = Color.White
                                )
                            )
                        }
                    }

                    item {
                        OutlinedTextField(
                            value = notesInput,
                            onValueChange = { notesInput = it },
                            label = { Text("Notas rápidas (Ej. Sintiéndome más fuerte)") },
                            modifier = Modifier.fillMaxWidth(),
                            maxLines = 2,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = NeonGymGreen,
                                unfocusedBorderColor = SlateGray,
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White
                            )
                        )
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                Button(
                    onClick = {
                        val weight = weightInput.toDoubleOrNull() ?: 0.0
                        if (weight > 0.0) {
                            val fat = fatInput.toDoubleOrNull() ?: 0.0
                            val muscle = muscleInput.toDoubleOrNull() ?: 0.0
                            val chest = chestInput.toDoubleOrNull() ?: 0.0
                            val waist = waistInput.toDoubleOrNull() ?: 0.0
                            val bicep = bicepInput.toDoubleOrNull() ?: 0.0
                            val thigh = thighInput.toDoubleOrNull() ?: 0.0
                            onSave(weight, fat, muscle, chest, waist, bicep, thigh, notesInput)
                        }
                    },
                    enabled = weightInput.toDoubleOrNull() != null && (weightInput.toDoubleOrNull() ?: 0.0) > 0.0,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = NeonGymGreen,
                        disabledContainerColor = SlateGray
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .testTag("btn_save_metric"),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Asentar Registro", color = Color.Black, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}
