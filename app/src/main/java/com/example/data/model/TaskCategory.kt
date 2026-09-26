package com.example.data.model

enum class TaskCategory(val label: String) {
  PERSONAL("Personal"),
  WORK("Work"),
  SHOPPING("Shopping"),
  HEALTH("Health"),
  STUDY("Study"),
  FINANCE("Finance"),
  OTHER("Other");

  companion object {
    fun fromName(name: String?): TaskCategory {
      return entries.find { it.name.equals(name, ignoreCase = true) } ?: OTHER
    }
  }
}
