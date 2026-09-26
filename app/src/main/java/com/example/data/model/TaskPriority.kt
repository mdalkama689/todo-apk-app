package com.example.data.model

enum class TaskPriority(val label: String, val level: Int) {
  LOW("Low", 1),
  MEDIUM("Medium", 2),
  HIGH("High", 3);

  companion object {
    fun fromName(name: String?): TaskPriority {
      return entries.find { it.name.equals(name, ignoreCase = true) } ?: MEDIUM
    }
  }
}
