package com.example.group4midtermactivity.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.group4midtermactivity.data.Task
import com.example.group4midtermactivity.repository.TaskRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class TaskViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = TaskRepository.getInstance(application)

    // All tasks
    val allTasks = repository.getAllTasks().stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    // Pending tasks
    val pendingTasks = repository.getPendingTasks().stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    // Completed tasks
    val completedTasks = repository.getCompletedTasks().stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    // Subjects with counts
    val subjectsWithCount = repository.getSubjectsWithCount().stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    // Selected task for detail view
    private val _selectedTask = MutableStateFlow<Task?>(null)
    val selectedTask: StateFlow<Task?> = _selectedTask.asStateFlow()

    // Add Task
    suspend fun addTask(task: Task): Long {
        return repository.insertTask(task)
    }

    // Update Task
    suspend fun updateTask(task: Task) {
        repository.updateTask(task)
    }

    // Mark Task as Completed
    suspend fun markTaskCompleted(taskId: Int, completedDate: String) {
        repository.markTaskCompleted(taskId, completedDate)
    }

    // Delete Task
    suspend fun deleteTask(task: Task) {
        repository.deleteTask(task)
    }

    // Clear all completed tasks
    suspend fun clearCompletedTasks() {
        repository.clearCompletedTasks()
    }

    // Get Task by ID
    suspend fun getTaskById(taskId: Int): Task? {
        return repository.getTaskById(taskId)
    }

    // Select a task for detail view
    fun selectTask(task: Task) {
        _selectedTask.value = task
    }

    // Clear selected task
    fun clearSelectedTask() {
        _selectedTask.value = null
    }

    // Get tasks by subject
    fun getTasksBySubject(subject: String) = repository.getTasksBySubject(subject)
}