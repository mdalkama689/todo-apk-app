package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DeleteSweep
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.PlaylistAdd
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.components.AddEditTaskSheet
import com.example.ui.components.EmptyState
import com.example.ui.components.FilterBar
import com.example.ui.components.TaskItemCard
import com.example.ui.components.TaskSummaryHeader
import com.example.ui.viewmodel.TaskViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
  viewModel: TaskViewModel,
  modifier: Modifier = Modifier
) {
  val uiState by viewModel.uiState.collectAsStateWithLifecycle()
  val snackbarHostState = remember { SnackbarHostState() }
  val scope = rememberCoroutineScope()

  var isSearchActive by remember { mutableStateOf(false) }
  var showMenu by remember { mutableStateOf(false) }
  var showClearCompletedDialog by remember { mutableStateOf(false) }

  Scaffold(
    modifier = modifier.fillMaxSize(),
    topBar = {
      TopAppBar(
        title = {
          if (isSearchActive) {
            OutlinedTextField(
              value = uiState.searchQuery,
              onValueChange = { viewModel.setSearchQuery(it) },
              placeholder = { Text("Search tasks...") },
              singleLine = true,
              shape = RoundedCornerShape(12.dp),
              colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                focusedBorderColor = MaterialTheme.colorScheme.primary,
                unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
              ),
              trailingIcon = {
                if (uiState.searchQuery.isNotEmpty()) {
                  IconButton(onClick = { viewModel.setSearchQuery("") }) {
                    Icon(
                      imageVector = Icons.Default.Close,
                      contentDescription = "Clear search",
                      modifier = Modifier.size(18.dp)
                    )
                  }
                }
              },
              modifier = Modifier
                .fillMaxWidth()
                .padding(end = 8.dp)
                .testTag("search_text_field")
            )
          } else {
            Text(
              text = "TaskFlow",
              style = MaterialTheme.typography.titleLarge,
              fontWeight = FontWeight.Bold,
              color = MaterialTheme.colorScheme.onBackground
            )
          }
        },
        actions = {
          IconButton(
            onClick = {
              isSearchActive = !isSearchActive
              if (!isSearchActive) {
                viewModel.setSearchQuery("")
              }
            },
            modifier = Modifier.testTag("search_toggle_button")
          ) {
            Icon(
              imageVector = if (isSearchActive) Icons.Default.Close else Icons.Default.Search,
              contentDescription = if (isSearchActive) "Close Search" else "Search Tasks",
              tint = MaterialTheme.colorScheme.onSurface
            )
          }

          Box {
            IconButton(
              onClick = { showMenu = true },
              modifier = Modifier.testTag("more_options_button")
            ) {
              Icon(
                imageVector = Icons.Default.MoreVert,
                contentDescription = "More Options",
                tint = MaterialTheme.colorScheme.onSurface
              )
            }

            DropdownMenu(
              expanded = showMenu,
              onDismissRequest = { showMenu = false }
            ) {
              DropdownMenuItem(
                text = { Text("Clear Completed") },
                leadingIcon = {
                  Icon(
                    imageVector = Icons.Default.DeleteSweep,
                    contentDescription = null
                  )
                },
                enabled = uiState.completedCount > 0,
                onClick = {
                  showMenu = false
                  showClearCompletedDialog = true
                },
                modifier = Modifier.testTag("menu_clear_completed")
              )

              DropdownMenuItem(
                text = { Text("Load Sample Tasks") },
                leadingIcon = {
                  Icon(
                    imageVector = Icons.Default.PlaylistAdd,
                    contentDescription = null
                  )
                },
                onClick = {
                  showMenu = false
                  viewModel.resetSampleTasks()
                  scope.launch {
                    snackbarHostState.showSnackbar("Sample tasks loaded")
                  }
                },
                modifier = Modifier.testTag("menu_load_samples")
              )
            }
          }
        },
        colors = TopAppBarDefaults.topAppBarColors(
          containerColor = MaterialTheme.colorScheme.background
        )
      )
    },
    floatingActionButton = {
      FloatingActionButton(
        onClick = { viewModel.openAddSheet() },
        containerColor = MaterialTheme.colorScheme.primary,
        contentColor = MaterialTheme.colorScheme.onPrimary,
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier.testTag("add_task_fab")
      ) {
        Icon(
          imageVector = Icons.Default.Add,
          contentDescription = "Add New Task",
          modifier = Modifier.size(24.dp)
        )
      }
    },
    snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
  ) { innerPadding ->
    LazyColumn(
      modifier = Modifier
        .fillMaxSize()
        .padding(innerPadding),
      contentPadding = PaddingValues(bottom = 88.dp)
    ) {
      // Header with summary and stats (only shown when not searching)
      if (uiState.searchQuery.isBlank()) {
        item(key = "header") {
          TaskSummaryHeader(uiState = uiState)
        }
      }

      // Filter tabs and category chips
      item(key = "filters") {
        FilterBar(
          selectedFilter = uiState.selectedFilter,
          onFilterSelected = { viewModel.setFilter(it) },
          selectedCategory = uiState.selectedCategory,
          onCategorySelected = { viewModel.setCategory(it) },
          selectedSort = uiState.selectedSort,
          onSortSelected = { viewModel.setSort(it) }
        )
        Spacer(modifier = Modifier.height(8.dp))
      }

      // Task list or empty state
      if (uiState.filteredTasks.isEmpty()) {
        item(key = "empty_state") {
          EmptyState(
            filter = uiState.selectedFilter,
            searchQuery = uiState.searchQuery,
            onAddTask = { viewModel.openAddSheet() },
            onResetSamples = if (uiState.tasks.isEmpty()) {
              { viewModel.resetSampleTasks() }
            } else null
          )
        }
      } else {
        items(
          items = uiState.filteredTasks,
          key = { it.id }
        ) { task ->
          Box(
            modifier = Modifier
              .padding(horizontal = 16.dp, vertical = 5.dp)
              .animateItem()
          ) {
            TaskItemCard(
              task = task,
              onToggleComplete = {
                viewModel.toggleTaskCompletion(task)
              },
              onClick = {
                viewModel.openEditSheet(task)
              },
              onDelete = {
                viewModel.deleteTask(task)
                scope.launch {
                  val result = snackbarHostState.showSnackbar(
                    message = "\"${task.title}\" deleted",
                    actionLabel = "Undo",
                    duration = SnackbarDuration.Short
                  )
                  if (result == SnackbarResult.ActionPerformed) {
                    viewModel.undoDelete()
                  }
                }
              }
            )
          }
        }
      }
    }
  }

  // Add / Edit Modal Bottom Sheet
  if (uiState.isAddSheetVisible) {
    AddEditTaskSheet(
      task = uiState.editingTask,
      onDismiss = { viewModel.closeSheet() },
      onSave = { id, title, description, priority, category, dueDateMillis ->
        viewModel.saveTask(
          id = id,
          title = title,
          description = description,
          priority = priority,
          category = category,
          dueDateMillis = dueDateMillis
        )
      }
    )
  }

  // Clear completed tasks dialog
  if (showClearCompletedDialog) {
    AlertDialog(
      onDismissRequest = { showClearCompletedDialog = false },
      title = { Text("Clear Completed Tasks?") },
      text = { Text("This will permanently remove ${uiState.completedCount} completed task(s).") },
      confirmButton = {
        TextButton(
          onClick = {
            viewModel.clearCompletedTasks()
            showClearCompletedDialog = false
            scope.launch {
              snackbarHostState.showSnackbar("Completed tasks cleared")
            }
          },
          modifier = Modifier.testTag("confirm_clear_completed")
        ) {
          Text("Clear", color = MaterialTheme.colorScheme.error)
        }
      },
      dismissButton = {
        TextButton(onClick = { showClearCompletedDialog = false }) {
          Text("Cancel")
        }
      }
    )
  }
}
