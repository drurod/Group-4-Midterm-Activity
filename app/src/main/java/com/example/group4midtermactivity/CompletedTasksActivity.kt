package com.example.group4midtermactivity

import android.content.Intent
import android.graphics.Typeface
import android.os.Bundle
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.example.group4midtermactivity.data.Task
import com.example.group4midtermactivity.viewmodel.TaskViewModel
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.coroutines.launch

class CompletedTasksActivity : AppCompatActivity() {

    private lateinit var viewModel: TaskViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_completed_tasks)

        viewModel = TaskViewModel(application)

        // --- Bottom Navigation ---
        val bottomNav = findViewById<BottomNavigationView>(R.id.bottom_navigation)
        // ✅ Highlight Done tab
        bottomNav.menu.findItem(R.id.nav_done).isChecked = true

        bottomNav.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> {
                    startActivity(Intent(this, MainActivity::class.java))
                    finish()
                    true
                }
                R.id.nav_tasks -> {
                    startActivity(Intent(this, TaskListActivity::class.java))
                    finish()
                    true
                }
                R.id.nav_notes -> {
                    startActivity(Intent(this, SubjectsActivity::class.java))
                    finish()
                    true
                }
                R.id.nav_done -> true
                else -> false
            }
        }

        // --- Clear All Button ---
        val btnClearAll = findViewById<TextView>(R.id.btn_clear_all)
        btnClearAll.setOnClickListener {
            AlertDialog.Builder(this)
                .setTitle("Clear all completed tasks?")
                .setMessage("This action cannot be undone.")
                .setPositiveButton("Clear All") { _, _ ->
                    lifecycleScope.launch {
                        viewModel.clearCompletedTasks()
                        Toast.makeText(
                            this@CompletedTasksActivity,
                            "All completed tasks cleared!",
                            Toast.LENGTH_SHORT
                        ).show()
                        loadCompletedTasks()
                    }
                }
                .setNegativeButton("Cancel", null)
                .show()
        }

        // --- Load Completed Tasks ---
        loadCompletedTasks()
    }

    private fun loadCompletedTasks() {
        lifecycleScope.launch {
            viewModel.completedTasks.collect { tasks ->
                val taskContainer = findViewById<LinearLayout>(R.id.completed_task_container)
                taskContainer.removeAllViews()

                if (tasks.isEmpty()) {
                    val emptyView = TextView(this@CompletedTasksActivity).apply {
                        text = "✅ No completed tasks yet\nComplete a task to see it here!"
                        textSize = 18f
                        setPadding(16, 32, 16, 32)
                        gravity = android.view.Gravity.CENTER
                        setTextColor(ContextCompat.getColor(context, android.R.color.darker_gray))
                    }
                    taskContainer.addView(emptyView)
                } else {
                    tasks.forEach { task ->
                        val taskView = createCompletedTaskView(task)
                        taskContainer.addView(taskView)
                    }
                }
            }
        }
    }

    private fun createCompletedTaskView(task: Task): LinearLayout {
        return LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(16, 16, 16, 16)
            setBackgroundColor(ContextCompat.getColor(context, android.R.color.white))
            val borderDrawable = android.graphics.drawable.GradientDrawable().apply {
                setColor(ContextCompat.getColor(context, android.R.color.white))
                setStroke(1, ContextCompat.getColor(context, android.R.color.darker_gray))
                cornerRadius = 8f
            }
            background = borderDrawable
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                bottomMargin = 12
            }

            isClickable = true
            isFocusable = true

            setOnClickListener {
                val intent = Intent(context, TaskDetailsActivity::class.java)
                intent.putExtra("task_id", task.id)
                context.startActivity(intent)
            }

            val titleRow = LinearLayout(context).apply {
                orientation = LinearLayout.HORIZONTAL
                layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                )
            }

            val checkView = TextView(context).apply {
                text = "✅"
                textSize = 22f
                setPadding(0, 0, 12, 0)
            }
            titleRow.addView(checkView)

            val titleView = TextView(context).apply {
                text = task.title
                textSize = 18f
                setTypeface(null, Typeface.BOLD)
                setTextColor(ContextCompat.getColor(context, android.R.color.black))
                layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f)
            }
            titleRow.addView(titleView)

            val dateView = TextView(context).apply {
                text = "📅 ${task.deadline}"
                textSize = 14f
                setTextColor(ContextCompat.getColor(context, android.R.color.darker_gray))
            }
            titleRow.addView(dateView)

            addView(titleRow)

            val completedView = TextView(context).apply {
                text = "(Completed on ${task.completedDate ?: "Unknown"})"
                textSize = 14f
                setTextColor(ContextCompat.getColor(context, android.R.color.holo_green_dark))
                setPadding(36, 4, 0, 0)
            }
            addView(completedView)
        }
    }
}