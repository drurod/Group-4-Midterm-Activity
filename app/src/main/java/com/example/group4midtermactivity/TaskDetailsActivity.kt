package com.example.group4midtermactivity

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.group4midtermactivity.viewmodel.TaskViewModel
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class TaskDetailsActivity : AppCompatActivity() {

    private lateinit var viewModel: TaskViewModel
    private var taskId: Int = -1

    // Edit mode state
    private var isEditMode = false

    // View references for edit mode
    private lateinit var tvTitle: TextView
    private lateinit var tvSubject: TextView
    private lateinit var tvDescription: TextView
    private lateinit var tvDeadline: TextView
    private lateinit var tvStatus: TextView
    private lateinit var btnMarkCompleted: MaterialButton
    private lateinit var ivEdit: ImageView
    private lateinit var ivDelete: ImageView

    // Edit mode views (hidden initially)
    private lateinit var editLayout: LinearLayout
    private lateinit var etEditTitle: TextInputEditText
    private lateinit var etEditSubject: TextInputEditText
    private lateinit var etEditDescription: TextInputEditText
    private lateinit var etEditDeadline: TextInputEditText
    private lateinit var btnSaveEdit: MaterialButton
    private lateinit var btnCancelEdit: MaterialButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_task_details)

        viewModel = TaskViewModel(application)
        taskId = intent.getIntExtra("task_id", -1)

        if (taskId == -1) {
            Toast.makeText(this, "Task not found", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        // Initialize views
        initViews()

        // Load task details
        loadTaskDetails()

        // --- Edit Button ---
        ivEdit.setOnClickListener {
            if (isEditMode) {
                // If already in edit mode, don't do anything
                // Or we could cancel edit mode
            } else {
                enableEditMode()
            }
        }

        // --- Delete Button ---
        ivDelete.setOnClickListener {
            showDeleteConfirmation()
        }

        // --- Description Click ---
        val llDescription = findViewById<LinearLayout>(R.id.ll_description)
        llDescription.setOnClickListener {
            expandDescription()
        }

        // --- Mark as Completed Button ---
        btnMarkCompleted.setOnClickListener {
            lifecycleScope.launch {
                val task = viewModel.getTaskById(taskId)
                if (task != null && !task.isCompleted) {
                    val dateFormat = SimpleDateFormat("MMM d, yyyy", Locale.getDefault())
                    val completedDate = dateFormat.format(Date())
                    viewModel.markTaskCompleted(taskId, completedDate)
                    Toast.makeText(this@TaskDetailsActivity, "✅ Task completed!", Toast.LENGTH_SHORT).show()
                    finish()
                } else {
                    Toast.makeText(this@TaskDetailsActivity, "Task already completed", Toast.LENGTH_SHORT).show()
                }
            }
        }

        // --- Save Edit Button ---
        btnSaveEdit.setOnClickListener {
            saveEdit()
        }

        // --- Cancel Edit Button ---
        btnCancelEdit.setOnClickListener {
            cancelEditMode()
        }
    }

    private fun initViews() {
        // Display views
        tvTitle = findViewById(R.id.tv_task_title)
        tvSubject = findViewById(R.id.tv_subject_value)
        tvDescription = findViewById(R.id.tv_description_value)
        tvDeadline = findViewById(R.id.tv_deadline_value)
        tvStatus = findViewById(R.id.tv_status_value)
        btnMarkCompleted = findViewById(R.id.btn_mark_completed)
        ivEdit = findViewById(R.id.iv_edit)
        ivDelete = findViewById(R.id.iv_delete)

        // Edit mode views
        editLayout = findViewById(R.id.edit_layout)
        etEditTitle = findViewById(R.id.et_edit_title)
        etEditSubject = findViewById(R.id.et_edit_subject)
        etEditDescription = findViewById(R.id.et_edit_description)
        etEditDeadline = findViewById(R.id.et_edit_deadline)
        btnSaveEdit = findViewById(R.id.btn_save_edit)
        btnCancelEdit = findViewById(R.id.btn_cancel_edit)

        // Initially hide edit layout
        editLayout.visibility = View.GONE
    }

    private fun loadTaskDetails() {
        lifecycleScope.launch {
            val task = viewModel.getTaskById(taskId)
            if (task != null) {
                tvTitle.text = "📌 ${task.title}"
                tvSubject.text = task.subject
                tvDescription.text = task.description
                tvDeadline.text = task.deadline

                if (task.isCompleted) {
                    tvStatus.text = "✅ Completed"
                    tvStatus.setTextColor(resources.getColor(android.R.color.holo_green_dark, null))
                    btnMarkCompleted.isEnabled = false
                    btnMarkCompleted.text = "✅ ALREADY COMPLETED"
                } else {
                    tvStatus.text = "🟠 Pending"
                    tvStatus.setTextColor(resources.getColor(android.R.color.holo_orange_dark, null))
                }
            } else {
                Toast.makeText(this@TaskDetailsActivity, "Task not found", Toast.LENGTH_SHORT).show()
                finish()
            }
        }
    }

    private fun enableEditMode() {
        isEditMode = true

        // Hide display views
        tvTitle.visibility = View.GONE
        tvSubject.visibility = View.GONE
        tvDescription.visibility = View.GONE
        tvDeadline.visibility = View.GONE
        tvStatus.visibility = View.GONE
        btnMarkCompleted.visibility = View.GONE
        ivEdit.visibility = View.GONE

        // Show edit layout
        editLayout.visibility = View.VISIBLE

        // Pre-fill edit fields
        lifecycleScope.launch {
            val task = viewModel.getTaskById(taskId)
            if (task != null) {
                etEditTitle.setText(task.title)
                etEditSubject.setText(task.subject)
                etEditDescription.setText(task.description)
                etEditDeadline.setText(task.deadline)
            }
        }
    }

    private fun cancelEditMode() {
        isEditMode = false

        // Show display views
        tvTitle.visibility = View.VISIBLE
        tvSubject.visibility = View.VISIBLE
        tvDescription.visibility = View.VISIBLE
        tvDeadline.visibility = View.VISIBLE
        tvStatus.visibility = View.VISIBLE
        btnMarkCompleted.visibility = View.VISIBLE
        ivEdit.visibility = View.VISIBLE

        // Hide edit layout
        editLayout.visibility = View.GONE

        // Reload original data
        loadTaskDetails()
    }

    private fun saveEdit() {
        val title = etEditTitle.text.toString().trim()
        val subject = etEditSubject.text.toString().trim()
        val description = etEditDescription.text.toString().trim()
        val deadline = etEditDeadline.text.toString().trim()

        if (title.isEmpty()) {
            etEditTitle.error = "Title is required"
            return
        }

        if (deadline.isEmpty()) {
            etEditDeadline.error = "Deadline is required"
            return
        }

        lifecycleScope.launch {
            val task = viewModel.getTaskById(taskId)
            if (task != null) {
                val updatedTask = task.copy(
                    title = title,
                    subject = subject,
                    description = description,
                    deadline = deadline
                )
                viewModel.updateTask(updatedTask)
                Toast.makeText(
                    this@TaskDetailsActivity,
                    "✅ Task updated successfully!",
                    Toast.LENGTH_SHORT
                ).show()

                // Exit edit mode
                cancelEditMode()
                // Reload with updated data
                loadTaskDetails()
            }
        }
    }

    private fun showDeleteConfirmation() {
        AlertDialog.Builder(this)
            .setTitle("Delete Task")
            .setMessage("Are you sure you want to delete this task? This action cannot be undone.")
            .setPositiveButton("Delete") { _, _ ->
                lifecycleScope.launch {
                    val task = viewModel.getTaskById(taskId)
                    if (task != null) {
                        viewModel.deleteTask(task)
                        Toast.makeText(
                            this@TaskDetailsActivity,
                            "🗑️ Task deleted!",
                            Toast.LENGTH_SHORT
                        ).show()
                        finish()
                    }
                }
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun expandDescription() {
        lifecycleScope.launch {
            val task = viewModel.getTaskById(taskId)
            if (task != null) {
                AlertDialog.Builder(this@TaskDetailsActivity)
                    .setTitle("📝 Description")
                    .setMessage(task.description)
                    .setPositiveButton("Close", null)
                    .show()
            }
        }
    }
}