package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.data.model.Task
import com.example.data.model.TaskCategory
import com.example.data.model.TaskPriority
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(entities = [Task::class], version = 1, exportSchema = false)
@TypeConverters(Converters::class)
abstract class TaskDatabase : RoomDatabase() {
  abstract fun taskDao(): TaskDao

  companion object {
    @Volatile
    private var INSTANCE: TaskDatabase? = null

    fun getDatabase(context: Context, scope: CoroutineScope = CoroutineScope(Dispatchers.IO)): TaskDatabase {
      return INSTANCE ?: synchronized(this) {
        val instance = Room.databaseBuilder(
          context.applicationContext,
          TaskDatabase::class.java,
          "task_flow_database"
        )
          .addCallback(DatabaseCallback(scope))
          .fallbackToDestructiveMigration()
          .build()
        INSTANCE = instance
        instance
      }
    }

    private class DatabaseCallback(private val scope: CoroutineScope) : RoomDatabase.Callback() {
      override fun onCreate(db: SupportSQLiteDatabase) {
        super.onCreate(db)
        INSTANCE?.let { database ->
          scope.launch {
            populateInitialTasks(database.taskDao())
          }
        }
      }

      suspend fun populateInitialTasks(dao: TaskDao) {
        val now = System.currentTimeMillis()
        val dayMillis = 24 * 60 * 60 * 1000L
        val initialTasks = listOf(
          Task(
            title = "Welcome to TaskFlow! 👋",
            description = "Tap the circle on the left to mark tasks completed.",
            isCompleted = false,
            priority = TaskPriority.HIGH,
            category = TaskCategory.PERSONAL,
            dueDateMillis = now,
            createdAtMillis = now - 3600000L
          ),
          Task(
            title = "Review project deliverables",
            description = "Check status report and email summary to team.",
            isCompleted = false,
            priority = TaskPriority.HIGH,
            category = TaskCategory.WORK,
            dueDateMillis = now + dayMillis,
            createdAtMillis = now - 7200000L
          ),
          Task(
            title = "Buy fresh groceries & fruits 🍎",
            description = "Milk, sourdough bread, avocados, apples, greek yogurt.",
            isCompleted = false,
            priority = TaskPriority.MEDIUM,
            category = TaskCategory.SHOPPING,
            dueDateMillis = now,
            createdAtMillis = now - 10800000L
          ),
          Task(
            title = "Daily 30-minute workout or walk 🏃",
            description = "Light cardio and stretching session.",
            isCompleted = true,
            priority = TaskPriority.LOW,
            category = TaskCategory.HEALTH,
            dueDateMillis = now - dayMillis,
            createdAtMillis = now - (dayMillis + 3600000L),
            completedAtMillis = now - 3600000L
          )
        )
        dao.insertAll(initialTasks)
      }
    }
  }
}
