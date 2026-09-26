package com.example.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.data.model.Task
import com.example.data.model.TaskCategory
import com.example.data.model.TaskPriority
import com.example.data.repository.TaskRepository
import com.example.util.DateUtils
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

enum class TaskFilter(val label: String) {
  ALL("All"),
  PENDING("To Do"),
  TODAY("Today"),
  UPCOMING("Upcoming"),
  COMPLETED("Done")
}

enum class TaskSort(val label: String) {
  DUE_DATE("Due Date"),
  PRIORITY("Priority"),
  NEWEST("Newest"),
  TITLE("Alphabetical")
}

data class TaskUiState(
  val tasks: List<Task> = emptyList(),
  val filteredTasks: List<Task> = emptyList(),
  val searchQuery: String = "",
  val selectedFilter: TaskFilter = TaskFilter.ALL,
  val selectedCategory: TaskCategory? = null,
  val selectedSort: TaskSort = TaskSort.DUE_DATE,
  val totalCount: Int = 0,
  val completedCount: Int = 0,
  val pendingCount: Int = 0,
  val todayCount: Int = 0,
  val highPriorityCount: Int = 0,
  val progress: Float = 0f,
  val editingTask: Task? = null,
  val isAddSheetVisible: Boolean = false,
  val lastDeletedTask: Task? = null
)

class TaskViewModel(private val repository: TaskRepository) : ViewModel() {

  private val _searchQuery = MutableStateFlow("")
  private val _selectedFilter = MutableStateFlow(TaskFilter.ALL)
  private val _selectedCategory = MutableStateFlow<TaskCategory?>(null)
  private val _selectedSort = MutableStateFlow(TaskSort.DUE_DATE)
  private val _editingTask = MutableStateFlow<Task?>(null)
  private val _isAddSheetVisible = MutableStateFlow(false)
  private val _lastDeletedTask = MutableStateFlow<Task?>(null)

  val uiState: StateFlow<TaskUiState> = combine(
    repository.allTasks,
    _searchQuery,
    _selectedFilter,
    _selectedCategory,
    _selectedSort,
    _editingTask,
    _isAddSheetVisible,
    _lastDeletedTask
  ) { args: Array<Any?> ->
    @Suppress("UNCHECKED_CAST")
    val allTasks = args[0] as List<Task>
    val search = args[1] as String
    val filter = args[2] as TaskFilter
    val category = args[3] as TaskCategory?
    val sort = args[4] as TaskSort
    val editingTask = args[5] as Task?
    val isAddSheet = args[6] as Boolean
    val lastDeleted = args[7] as Task?

    val total = allTasks.size
    val completed = allTasks.count { it.isCompleted }
    val pending = total - completed
    val todayTasks = allTasks.count { !it.isCompleted && it.dueDateMillis != null && DateUtils.isToday(it.dueDateMillis) }
    val highPriority = allTasks.count { !it.isCompleted && it.priority == TaskPriority.HIGH }
    val progress = if (total > 0) completed.toFloat() / total.toFloat() else 0f

    // Filtering
    val filtered = allTasks.filter { task ->
      // Search
      val matchesSearch = search.isBlank() ||
          task.title.contains(search, ignoreCase = true) ||
          task.description.contains(search, ignoreCase = true)

      // Category
      val matchesCategory = category == null || task.category == category

      // Tab Filter
      val matchesFilter = when (filter) {
        TaskFilter.ALL -> true
        TaskFilter.PENDING -> !task.isCompleted
        TaskFilter.COMPLETED -> task.isCompleted
        TaskFilter.TODAY -> !task.isCompleted && task.dueDateMillis != null && DateUtils.isToday(task.dueDateMillis)
        TaskFilter.UPCOMING -> !task.isCompleted && task.dueDateMillis != null && task.dueDateMillis > DateUtils.getTodayEnd()
      }

      matchesSearch && matchesCategory && matchesFilter
    }.sortedWith { a, b ->
      // Always keep uncompleted tasks above completed tasks within any sort
      if (a.isCompleted != b.isCompleted) {
        if (a.isCompleted) 1 else -1
      } else {
        when (sort) {
          TaskSort.DUE_DATE -> {
            // Null due dates go last
            val dateA = a.dueDateMillis ?: Long.MAX_VALUE
            val dateB = b.dueDateMillis ?: Long.MAX_VALUE
            dateA.compareTo(dateB)
          }
          TaskSort.PRIORITY -> {
            // HIGH (3) > MEDIUM (2) > LOW (1)
            b.priority.level.compareTo(a.priority.level)
          }
          TaskSort.NEWEST -> {
            b.createdAtMillis.compareTo(a.createdAtMillis)
          }
          TaskSort.TITLE -> {
            a.title.compareTo(b.title, ignoreCase = true)
          }
        }
      }
    }

    TaskUiState(
      tasks = allTasks,
      filteredTasks = filtered,
      searchQuery = search,
      selectedFilter = filter,
      selectedCategory = category,
      selectedSort = sort,
      totalCount = total,
      completedCount = completed,
      pendingCount = pending,
      todayCount = todayTasks,
      highPriorityCount = highPriority,
      progress = progress,
      editingTask = editingTask,
      isAddSheetVisible = isAddSheet,
      lastDeletedTask = lastDeleted
    )
  }.stateIn(
    scope = viewModelScope,
    started = SharingStarted.WhileSubscribed(5000),
    initialValue = TaskUiState()
  )

  fun setSearchQuery(query: String) {
    _searchQuery.value = query
  }

  fun setFilter(filter: TaskFilter) {
    _selectedFilter.value = filter
  }

  fun setCategory(category: TaskCategory?) {
    _selectedCategory.value = category
  }

  fun setSort(sort: TaskSort) {
    _selectedSort.value = sort
  }

  fun openAddSheet() {
    _editingTask.value = null
    _isAddSheetVisible.value = true
  }

  fun openEditSheet(task: Task) {
    _editingTask.value = task
    _isAddSheetVisible.value = true
  }

  fun closeSheet() {
    _isAddSheetVisible.value = false
    _editingTask.value = null
  }

  fun toggleTaskCompletion(task: Task) {
    viewModelScope.launch {
      repository.setTaskCompleted(task.id, !task.isCompleted)
    }
  }

  fun saveTask(
    id: Long = 0,
    title: String,
    description: String,
    priority: TaskPriority,
    category: TaskCategory,
    dueDateMillis: Long?
  ) {
    viewModelScope.launch {
      if (id == 0L) {
        val newTask = Task(
          title = title.trim(),
          description = description.trim(),
          priority = priority,
          category = category,
          dueDateMillis = dueDateMillis,
          createdAtMillis = System.currentTimeMillis()
        )
        repository.insertTask(newTask)
      } else {
        val existing = repository.getTaskById(id)
        if (existing != null) {
          val updated = existing.copy(
            title = title.trim(),
            description = description.trim(),
            priority = priority,
            category = category,
            dueDateMillis = dueDateMillis
          )
          repository.updateTask(updated)
        }
      }
      closeSheet()
    }
  }

  fun deleteTask(task: Task) {
    viewModelScope.launch {
      _lastDeletedTask.value = task
      repository.deleteTask(task)
    }
  }

  fun undoDelete() {
    val taskToRestore = _lastDeletedTask.value ?: return
    viewModelScope.launch {
      repository.insertTask(taskToRestore)
      _lastDeletedTask.value = null
    }
  }

  fun clearCompletedTasks() {
    viewModelScope.launch {
      repository.deleteCompletedTasks()
    }
  }

  fun resetSampleTasks() {
    viewModelScope.launch {
      val now = System.currentTimeMillis()
      val dayMillis = 24 * 60 * 60 * 1000L
      val samples = listOf(
        Task(
          title = "Organize desk & clean workspace 🧹",
          description = "Declutter notebook, cables, and setup lighting.",
          priority = TaskPriority.MEDIUM,
          category = TaskCategory.PERSONAL,
          dueDateMillis = now,
          createdAtMillis = now
        ),
        Task(
          title = "Prepare weekly presentation slides 📊",
          description = "Include key metrics and customer feedback.",
          priority = TaskPriority.HIGH,
          category = TaskCategory.WORK,
          dueDateMillis = now + dayMillis,
          createdAtMillis = now
        ),
        Task(
          title = "Grocery list for healthy meal prep 🥑",
          description = "Spinach, chicken breast, quinoa, olive oil.",
          priority = TaskPriority.LOW,
          category = TaskCategory.SHOPPING,
          dueDateMillis = now + (2 * dayMillis),
          createdAtMillis = now
        )
      )
      repository.insertAll(samples)
    }
  }
}

class TaskViewModelFactory(private val repository: TaskRepository) : ViewModelProvider.Factory {
  override fun <T : ViewModel> create(modelClass: Class<T>): T {
    if (modelClass.isAssignableFrom(TaskViewModel::class.java)) {
      @Suppress("UNCHECKED_CAST")
      return TaskViewModel(repository) as T
    }
    throw IllegalArgumentException("Unknown ViewModel class")
  }
}
