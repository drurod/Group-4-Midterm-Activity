package com.example.group4midtermactivity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

class SubjectsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_subjects)

        // Optional: Set up bottom navigation click listeners
        val bottomNav = findViewById<com.google.android.material.bottomnavigation.BottomNavigationView>(R.id.bottom_navigation)
        bottomNav.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> {
                    startActivity(android.content.Intent(this, MainActivity::class.java))
                    true
                }
                R.id.nav_tasks -> {
                    startActivity(android.content.Intent(this, TaskListActivity::class.java))
                    true
                }
                R.id.nav_notes -> {
                    // Already on Subjects screen
                    true
                }
                R.id.nav_done -> {
                    // Navigate to Done/Completed tasks screen (if you have one)
                    true
                }
                else -> false
            }
        }

        // Optional: Click listener for subject cards to show tasks by subject
        // You can add this later when you have a SubjectTaskListActivity
    }
}