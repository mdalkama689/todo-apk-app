package com.example.data.local

import androidx.room.TypeConverter
import com.example.data.model.TaskCategory
import com.example.data.model.TaskPriority

class Converters {
  @TypeConverter
  fun fromPriority(priority: TaskPriority?): String {
    return priority?.name ?: TaskPriority.MEDIUM.name
  }

  @TypeConverter
  fun toPriority(value: String?): TaskPriority {
    return TaskPriority.fromName(value)
  }

  @TypeConverter
  fun fromCategory(category: TaskCategory?): String {
    return category?.name ?: TaskCategory.OTHER.name
  }

  @TypeConverter
  fun toCategory(value: String?): TaskCategory {
    return TaskCategory.fromName(value)
  }
}
