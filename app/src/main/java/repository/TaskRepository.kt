package com.example.group4midtermactivity.repository

import com.example.group4midtermactivity.data.SubjectWithCount
import com.example.group4midtermactivity.data.Task
import com.example.group4midtermactivity.data.TaskDao
import com.example.group4midtermactivity.data.TaskDatabase
import kotlinx.coroutines.flow.Flow

class TaskRepository(private val taskDao: TaskDao) {

    // Create
    suspend fun insertTask(task: Task): Long {
        return taskDao.insertTask(task)
    }

    // Read - All tasks
    fun getAllTasks(): Flow<List<Task>> {
        return taskDao.getAllTasks()
    }

    // Read - Pending tasks
    fun getPendingTasks(): Flow<List<Task>> {
        return taskDao.getPendingTasks()
    }

    // Read - Completed tasks
    fun getCompletedTasks(): Flow<List<Task>> {
        return taskDao.getCompletedTasks()
    }

    // Read - Tasks by subject
    fun getTasksBySubject(subject: String): Flow<List<Task>> {
        return taskDao.getTasksBySubject(subject)
    }

    // Read - Subjects with counts
    fun getSubjectsWithCount(): Flow<List<SubjectWithCount>> {
        return taskDao.getSubjectsWithCount()
    }

    // Update
    suspend fun updateTask(task: Task) {
        taskDao.updateTask(task)
    }

    // Update - Mark as completed
    suspend fun markTaskCompleted(taskId: Int, completedDate: String) {
        taskDao.markTaskCompleted(taskId, completedDate)
    }

    // Delete
    suspend fun deleteTask(task: Task) {
        taskDao.deleteTask(task)
    }

    // Delete - Clear all completed tasks
    suspend fun clearCompletedTasks() {
        taskDao.clearCompletedTasks()
    }

    // Get task by ID
    suspend fun getTaskById(taskId: Int): Task? {
        return taskDao.getTaskById(taskId)
    }

    companion object {
        @Volatile
        private var INSTANCE: TaskRepository? = null

        fun getInstance(context: android.content.Context): TaskRepository {
            return INSTANCE ?: synchronized(this) {
                val database = TaskDatabase.getDatabase(context)
                val instance = TaskRepository(database.taskDao())
                INSTANCE = instance
                instance
            }
        }
    }
}