package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import com.example.data.local.TaskDatabase
import com.example.data.repository.TaskRepository
import com.example.ui.screens.HomeScreen
import com.example.ui.theme.TaskFlowTheme
import com.example.ui.viewmodel.TaskViewModel
import com.example.ui.viewmodel.TaskViewModelFactory

class MainActivity : ComponentActivity() {

  private val viewModel: TaskViewModel by viewModels {
    val database = TaskDatabase.getDatabase(applicationContext)
    val repository = TaskRepository(database.taskDao())
    TaskViewModelFactory(repository)
  }

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    enableEdgeToEdge()
    setContent {
      TaskFlowTheme {
        HomeScreen(viewModel = viewModel)
      }
    }
  }
}
