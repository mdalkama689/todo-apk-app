package com.example.data.repository

import com.example.data.local.TaskDao
import com.example.data.model.Task
import kotlinx.coroutines.flow.Flow

class TaskRepository(private val taskDao: TaskDao) {
  val allTasks: Flow<List<Task>> = taskDao.getAllTasks()

  suspend fun getTaskById(id: Long): Task? = taskDao.getTaskById(id)

  suspend fun insertTask(task: Task): Long = taskDao.insertTask(task)

  suspend fun updateTask(task: Task) = taskDao.updateTask(task)

  suspend fun deleteTask(task: Task) = taskDao.deleteTask(task)

  suspend fun deleteTaskById(id: Long) = taskDao.deleteTaskById(id)

  suspend fun deleteCompletedTasks() = taskDao.deleteCompletedTasks()

  suspend fun setTaskCompleted(id: Long, isCompleted: Boolean) {
    val completedAt = if (isCompleted) System.currentTimeMillis() else null
    taskDao.setTaskCompleted(id, isCompleted, completedAt)
  }

  suspend fun insertAll(tasks: List<Task>) = taskDao.insertAll(tasks)
}
