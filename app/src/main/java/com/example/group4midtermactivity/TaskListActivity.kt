package com.example.group4midtermactivity

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.bottomnavigation.BottomNavigationView

class TaskListActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_task_list)

        // --- Bottom Navigation Setup ---
        val bottomNav = findViewById<BottomNavigationView>(R.id.bottom_navigation)
        bottomNav.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> {
                    startActivity(Intent(this, MainActivity::class.java))
                    finish() // Close this activity so user doesn't get stuck in a loop
                    true
                }
                R.id.nav_tasks -> {
                    // Already on Task List screen
                    true
                }
                R.id.nav_notes -> {
                    startActivity(Intent(this, SubjectsActivity::class.java))
                    finish()
                    true
                }
                R.id.nav_done -> {
                    // Optional: Navigate to a "Done" tasks screen if you create one
                    // For now, just show a toast or do nothing
                    true
                }
                else -> false
            }
        }

        // --- Click Listeners for Task Items ---
        // When you click a task, it opens the Task Details screen
        findViewById<android.view.View>(R.id.task_item_1).setOnClickListener {
            startActivity(Intent(this, TaskDetailsActivity::class.java))
        }

        findViewById<android.view.View>(R.id.task_item_2).setOnClickListener {
            startActivity(Intent(this, TaskDetailsActivity::class.java))
        }

        findViewById<android.view.View>(R.id.task_item_3).setOnClickListener {
            startActivity(Intent(this, TaskDetailsActivity::class.java))
        }
    }
}