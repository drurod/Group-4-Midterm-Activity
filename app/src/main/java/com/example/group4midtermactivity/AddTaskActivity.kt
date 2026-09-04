package com.example.group4midtermactivity

import android.os.Bundle
import android.widget.ArrayAdapter
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.textfield.MaterialAutoCompleteTextView

class AddTaskActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_task)

        // This makes the Subject dropdown work
        val subjects = arrayOf("Programming", "Math", "Science", "History", "English")
        val adapter = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, subjects)
        val autoComplete = findViewById<MaterialAutoCompleteTextView>(R.id.autoCompleteSubject)
        autoComplete.setAdapter(adapter)
    }
}