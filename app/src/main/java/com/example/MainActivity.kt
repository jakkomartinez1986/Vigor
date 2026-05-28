package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import com.example.data.local.FitnessDatabase
import com.example.data.repository.FitnessRepository
import com.example.ui.screens.MainScreen
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.viewmodel.FitnessViewModel
import com.example.ui.viewmodel.FitnessViewModelFactory

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Local database configuration
        val database = FitnessDatabase.getDatabase(applicationContext)
        val repository = FitnessRepository(database.fitnessDao())

        // ViewModel initialization via standard factory pattern
        val viewModel: FitnessViewModel by viewModels {
            FitnessViewModelFactory(repository)
        }

        setContent {
            MyApplicationTheme {
                MainScreen(viewModel = viewModel)
            }
        }
    }
}
