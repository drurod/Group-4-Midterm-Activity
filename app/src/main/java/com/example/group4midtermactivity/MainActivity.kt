package com.example.group4midtermactivity

import android.content.Intent
import android.os.Bundle
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.example.group4midtermactivity.data.Task
import com.example.group4midtermactivity.viewmodel.TaskViewModel
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class MainActivity : AppCompatActivity() {

    private lateinit var viewModel: TaskViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dashboard)

        viewModel = TaskViewModel(application)

        // --- FAB Button ---
        val fab = findViewById<FloatingActionButton>(R.id.fab_add)
        fab.setOnClickListener {
            startActivity(Intent(this, AddTaskActivity::class.java))
        }

        // --- Bottom Navigation ---
        val bottomNav = findViewById<BottomNavigationView>(R.id.bottom_navigation)
        bottomNav.menu.findItem(R.id.nav_home).isChecked = true

        bottomNav.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> true
                R.id.nav_tasks -> {
                    startActivity(Intent(this, TaskListActivity::class.java))
                    true
                }
                R.id.nav_notes -> {
                    startActivity(Intent(this, SubjectsActivity::class.java))
                    true
                }
                R.id.nav_done -> {
                    startActivity(Intent(this, CompletedTasksActivity::class.java))
                    true
                }
                else -> false
            }
        }

        // --- Load Dashboard Data ---
        loadDashboardData()
    }

    private fun loadDashboardData() {
        lifecycleScope.launch {
            viewModel.allTasks.collect { tasks ->
                // Update counts
                val pendingCount = tasks.count { !it.isCompleted }
                val overdueCount = tasks.count { !it.isCompleted && isTaskOverdue(it.deadline) }
                val completedCount = tasks.count { it.isCompleted }

                findViewById<TextView>(R.id.tv_upcoming_count).text = pendingCount.toString()
                findViewById<TextView>(R.id.tv_overdue_count).text = overdueCount.toString()
                findViewById<TextView>(R.id.tv_done_count).text = completedCount.toString()

                // Update deadlines list (show pending tasks)
                val container = findViewById<LinearLayout>(R.id.deadline_container)
                container.removeAllViews()

                val pendingTasks = tasks.filter { !it.isCompleted }.sortedBy { it.deadline }

                if (pendingTasks.isEmpty()) {
                    val emptyView = TextView(this@MainActivity).apply {
                        text = "🎉 No upcoming deadlines!\nAll caught up!"
                        textSize = 16f
                        setPadding(16, 32, 16, 32)
                        gravity = android.view.Gravity.CENTER
                        setTextColor(ContextCompat.getColor(context, android.R.color.darker_gray))
                    }
                    container.addView(emptyView)
                } else {
                    pendingTasks.forEach { task ->
                        val taskView = createDeadlineItemView(task)
                        container.addView(taskView)
                    }
                }
            }
        }
    }

    private fun createDeadlineItemView(task: Task): LinearLayout {
        return LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            setPadding(12, 12, 12, 12)
            setBackgroundResource(android.R.drawable.btn_default)
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply { bottomMargin = 4 }
            gravity = android.view.Gravity.CENTER_VERTICAL

            val statusView = TextView(context).apply {
                text = getDeadlineStatus(task.deadline)
                textSize = 14f
                setTypeface(null, android.graphics.Typeface.BOLD)
                layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f)
            }
            addView(statusView)

            val titleView = TextView(context).apply {
                text = task.title
                textSize = 14f
                layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 2f)
                setPadding(8, 0, 8, 0)
                setTextColor(ContextCompat.getColor(context, android.R.color.black))
            }
            addView(titleView)

            val dateView = TextView(context).apply {
                text = task.deadline
                textSize = 14f
                setTextColor(ContextCompat.getColor(context, android.R.color.darker_gray))
            }
            addView(dateView)

            setOnClickListener {
                val intent = Intent(context, TaskDetailsActivity::class.java)
                intent.putExtra("task_id", task.id)
                context.startActivity(intent)
            }
        }
    }

    private fun getDeadlineStatus(deadlineStr: String): String {
        return try {
            val format = SimpleDateFormat("MMM d, yyyy", Locale.US)
            val deadlineDate = format.parse(deadlineStr)
            val today = Calendar.getInstance().apply {
                set(Calendar.HOUR_OF_DAY, 0)
                set(Calendar.MINUTE, 0)
                set(Calendar.SECOND, 0)
                set(Calendar.MILLISECOND, 0)
            }.time

            val diff = deadlineDate.time - today.time
            val days = diff / (24 * 60 * 60 * 1000)

            when {
                days < 0 -> "🔴 OVERDUE"
                days == 0L -> "🔴 DUE TODAY"
                days == 1L -> "🟠 DUE TOMORROW"
                days in 2..3 -> "🟡 IN $days DAYS"
                else -> "📅 Due in $days days"
            }
        } catch (e: Exception) {
            "📅 ${deadlineStr}"
        }
    }

    private fun isTaskOverdue(deadlineStr: String): Boolean {
        return try {
            val format = SimpleDateFormat("MMM d, yyyy", Locale.US)
            val deadlineDate = format.parse(deadlineStr)
            val today = Calendar.getInstance().apply {
                set(Calendar.HOUR_OF_DAY, 0)
                set(Calendar.MINUTE, 0)
                set(Calendar.SECOND, 0)
                set(Calendar.MILLISECOND, 0)
            }.time
            deadlineDate.before(today)
        } catch (e: Exception) {
            false
        }
    }
}