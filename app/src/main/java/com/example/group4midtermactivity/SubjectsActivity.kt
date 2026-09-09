package com.example.group4midtermactivity

import android.content.Intent
import android.os.Bundle
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.lifecycle.lifecycleScope
import com.example.group4midtermactivity.viewmodel.TaskViewModel
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.coroutines.launch

class SubjectsActivity : AppCompatActivity() {

    private lateinit var viewModel: TaskViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_subjects)

        viewModel = TaskViewModel(application)

        // --- Bottom Navigation ---
        val bottomNav = findViewById<BottomNavigationView>(R.id.bottom_navigation)
        // ✅ Highlight Notes tab
        bottomNav.menu.findItem(R.id.nav_notes).isChecked = true

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
                R.id.nav_notes -> true
                R.id.nav_done -> {
                    startActivity(Intent(this, CompletedTasksActivity::class.java))
                    finish()
                    true
                }
                else -> false
            }
        }

        // --- Load Subjects ---
        loadSubjects()
    }

    private fun loadSubjects() {
        lifecycleScope.launch {
            viewModel.subjectsWithCount.collect { subjects ->
                val subjectGrid = findViewById<LinearLayout>(R.id.subject_grid)
                subjectGrid.removeAllViews()

                if (subjects.isEmpty()) {
                    val emptyView = TextView(this@SubjectsActivity).apply {
                        text = "📚 No subjects yet\nAdd tasks to see them here!"
                        textSize = 18f
                        setPadding(16, 32, 16, 32)
                        gravity = android.view.Gravity.CENTER
                    }
                    subjectGrid.addView(emptyView)
                } else {
                    // Create a GridLayout dynamically
                    val gridLayout = android.widget.GridLayout(this@SubjectsActivity).apply {
                        columnCount = 2
                        setPadding(0, 0, 0, 0)
                        layoutParams = LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.MATCH_PARENT,
                            LinearLayout.LayoutParams.WRAP_CONTENT
                        )
                    }

                    subjects.forEach { subjectWithCount ->
                        val card = createSubjectCard(subjectWithCount.subject, subjectWithCount.taskCount)
                        gridLayout.addView(card)
                    }

                    subjectGrid.addView(gridLayout)
                }
            }
        }
    }

    private fun createSubjectCard(subject: String, taskCount: Int): CardView {
        return CardView(this).apply {
            layoutParams = android.widget.GridLayout.LayoutParams().apply {
                width = 0
                height = android.widget.GridLayout.LayoutParams.WRAP_CONTENT
                columnSpec = android.widget.GridLayout.spec(
                    android.widget.GridLayout.UNDEFINED,
                    1f
                )
                rowSpec = android.widget.GridLayout.spec(
                    android.widget.GridLayout.UNDEFINED,
                    1f
                )
                setMargins(8, 8, 8, 8)
            }
            radius = 12f
            cardElevation = 6f
            setContentPadding(20, 20, 20, 20)

            val innerLayout = LinearLayout(context).apply {
                orientation = LinearLayout.VERTICAL
                gravity = android.view.Gravity.CENTER
                layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                )
                minimumHeight = 120
            }

            val subjectView = TextView(context).apply {
                text = subject
                textSize = 18f
                typeface = android.graphics.Typeface.DEFAULT_BOLD
                gravity = android.view.Gravity.CENTER
                setTextColor(resources.getColor(android.R.color.black, null))
            }
            innerLayout.addView(subjectView)

            val countView = TextView(context).apply {
                text = "($taskCount task${if (taskCount > 1) "s" else ""})"
                textSize = 14f
                gravity = android.view.Gravity.CENTER
                setTextColor(resources.getColor(android.R.color.holo_blue_dark, null))
            }
            innerLayout.addView(countView)

            addView(innerLayout)

            // Click to see tasks by this subject
            setOnClickListener {
                val intent = Intent(context, TaskListActivity::class.java)
                intent.putExtra("subject", subject)
                context.startActivity(intent)
            }
        }
    }
}