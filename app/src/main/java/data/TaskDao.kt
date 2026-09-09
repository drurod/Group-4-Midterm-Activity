package com.example.group4midtermactivity.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface TaskDao {

    // Create
    @Insert
    suspend fun insertTask(task: Task): Long

    // Read - All tasks
    @Query("SELECT * FROM tasks ORDER BY deadline ASC")
    fun getAllTasks(): Flow<List<Task>>

    // Read - Pending tasks (not completed)
    @Query("SELECT * FROM tasks WHERE isCompleted = 0 ORDER BY deadline ASC")
    fun getPendingTasks(): Flow<List<Task>>

    // Read - Completed tasks
    @Query("SELECT * FROM tasks WHERE isCompleted = 1 ORDER BY completedDate DESC")
    fun getCompletedTasks(): Flow<List<Task>>

    // Read - Tasks by subject
    @Query("SELECT * FROM tasks WHERE subject = :subject AND isCompleted = 0 ORDER BY deadline ASC")
    fun getTasksBySubject(subject: String): Flow<List<Task>>

    // Read - Get all unique subjects with task counts
    @Query("SELECT subject, COUNT(*) as taskCount FROM tasks WHERE isCompleted = 0 GROUP BY subject")
    fun getSubjectsWithCount(): Flow<List<SubjectWithCount>>

    // Update
    @Update
    suspend fun updateTask(task: Task)

    // Update - Mark as completed
    @Query("UPDATE tasks SET isCompleted = 1, completedDate = :completedDate WHERE id = :taskId")
    suspend fun markTaskCompleted(taskId: Int, completedDate: String)

    // Delete
    @Delete
    suspend fun deleteTask(task: Task)

    // Delete - Clear all completed tasks
    @Query("DELETE FROM tasks WHERE isCompleted = 1")
    suspend fun clearCompletedTasks()

    // Get task by ID
    @Query("SELECT * FROM tasks WHERE id = :taskId")
    suspend fun getTaskById(taskId: Int): Task?
}

// Data class for subject counts
data class SubjectWithCount(
    val subject: String,
    val taskCount: Int
)