package com.example.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.model.Task
import kotlinx.coroutines.flow.Flow

@Dao
interface TaskDao {
  @Query("SELECT * FROM tasks ORDER BY isCompleted ASC, createdAtMillis DESC")
  fun getAllTasks(): Flow<List<Task>>

  @Query("SELECT * FROM tasks WHERE id = :id")
  suspend fun getTaskById(id: Long): Task?

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertTask(task: Task): Long

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertAll(tasks: List<Task>)

  @Update
  suspend fun updateTask(task: Task)

  @Delete
  suspend fun deleteTask(task: Task)

  @Query("DELETE FROM tasks WHERE id = :id")
  suspend fun deleteTaskById(id: Long)

  @Query("DELETE FROM tasks WHERE isCompleted = 1")
  suspend fun deleteCompletedTasks()

  @Query("UPDATE tasks SET isCompleted = :isCompleted, completedAtMillis = :completedAt WHERE id = :id")
  suspend fun setTaskCompleted(id: Long, isCompleted: Boolean, completedAt: Long?)
}
