package com.example.group4midtermactivity

import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.appcompat.app.AlertDialog

class CompletedTasksActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_completed_tasks)

        // --- 1. Bottom Navigation Setup ---
        val bottomNav = findViewById<BottomNavigationView>(R.id.bottom_navigation)
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
                R.id.nav_done -> {
                    // Already on Completed Tasks screen
                    true
                }
                else -> false
            }
        }

        // --- 2. "Clear all" Button ---
        val btnClearAll = findViewById<TextView>(R.id.btn_clear_all)
        btnClearAll.setOnClickListener {
            // Show a confirmation message
            Toast.makeText(this, "Clear all completed tasks?", Toast.LENGTH_SHORT).show()
            // TODO: Implement actual clear functionality when you have a database
        }

        // --- 3. Click Listeners for Completed Task Items ---
        // Clicking a completed task could show its details or allow
        val task1 = findViewById<CardView>(R.id.completed_task_1)
        task1.setOnClickListener {
            Toast.makeText(this, "Viewing: Math Quiz 1", Toast.LENGTH_SHORT).show()
            // Optional: Navigate to Task Details screen
            // startActivity(Intent(this, TaskDetailsActivity::class.java))
        }

        val task2 = findViewById<CardView>(R.id.completed_task_2)
        task2.setOnClickListener {
            Toast.makeText(this, "Viewing: Programming Lab", Toast.LENGTH_SHORT).show()
            // Optional: Navigate to Task Details screen
            // startActivity(Intent(this, TaskDetailsActivity::class.java))
            btnClearAll.setOnClickListener {
                AlertDialog.Builder(this)
                    .setTitle("Clear all completed tasks?")
                    .setMessage("This action cannot be undone.")
                    .setPositiveButton("Clear") { _, _ ->
                        // TODO: Delete all completed tasks from database
                        Toast.makeText(this, "All completed tasks cleared!", Toast.LENGTH_SHORT).show()
                    }
                    .setNegativeButton("Cancel", null)
                    .show()
            }
        }
    }
}