package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "tasks")
data class Task(
  @PrimaryKey(autoGenerate = true)
  val id: Long = 0,
  val title: String,
  val description: String = "",
  val isCompleted: Boolean = false,
  val priority: TaskPriority = TaskPriority.MEDIUM,
  val category: TaskCategory = TaskCategory.PERSONAL,
  val dueDateMillis: Long? = null,
  val createdAtMillis: Long = System.currentTimeMillis(),
  val completedAtMillis: Long? = null
)
