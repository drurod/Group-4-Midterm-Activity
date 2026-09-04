package com.example.group4midtermactivity

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.floatingactionbutton.FloatingActionButton

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dashboard)

        // --- 1. FAB Button: Opens Add Task Screen ---
        val fab = findViewById<FloatingActionButton>(R.id.fab_add)
        fab.setOnClickListener {
            startActivity(Intent(this, AddTaskActivity::class.java))
        }

        // --- 2. Bottom Navigation: Handles tab clicks ---
        val bottomNav = findViewById<BottomNavigationView>(R.id.bottom_navigation)
        bottomNav.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> {
                    // Already on Dashboard (Home), so do nothing
                    true
                }
                R.id.nav_tasks -> {
                    // Navigate to Task List screen
                    startActivity(Intent(this, TaskListActivity::class.java))
                    true
                }
                R.id.nav_notes -> {
                    // Navigate to Subjects screen (acting as "Notes")
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
    }
}