package com.example.group4midtermactivity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.button.MaterialButton

class TaskDetailsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_task_details)

        // Optional: Handle Mark as Completed button click
        val btnMarkCompleted = findViewById<MaterialButton>(R.id.btn_mark_completed)
        btnMarkCompleted.setOnClickListener {
            // TODO: Update task status and go back
            finish() // Closes activity and returns to previous screen
        }
    }
}