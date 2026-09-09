package com.example.group4midtermactivity

import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.group4midtermactivity.data.Task
import com.example.group4midtermactivity.viewmodel.TaskViewModel
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.MaterialAutoCompleteTextView
import com.google.android.material.textfield.TextInputEditText
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class AddTaskActivity : AppCompatActivity() {

    private lateinit var viewModel: TaskViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_task)

        viewModel = TaskViewModel(application)

        // --- Subject Dropdown Setup ---
        val subjects = arrayOf(
            "Integrative Programming",
            "Arts Appreciation",
            "Philippine History",
            "Events Driven Programming",
            "Mobile Programming",
            "Science Technology and Society",
            "System Integration",
            "Computer Networking"
        )
        val adapter = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, subjects)
        val autoComplete = findViewById<MaterialAutoCompleteTextView>(R.id.autoCompleteSubject)
        autoComplete.setAdapter(adapter)

        // Set default subject
        if (autoComplete.text.isEmpty()) {
            autoComplete.setText(subjects[0], false)
        }

        // Get references to input fields
        val etTaskName = findViewById<TextInputEditText>(R.id.etTaskName)
        val etDescription = findViewById<TextInputEditText>(R.id.etDescription)
        val etDeadline = findViewById<TextInputEditText>(R.id.etDeadline)
        val btnAddTask = findViewById<MaterialButton>(R.id.btn_add_task)

        // --- Calendar / Date Picker ---
        val dateFormat = SimpleDateFormat("MMM d, yyyy", Locale.US)
        val today = Calendar.getInstance().time
        etDeadline.setText(dateFormat.format(today))

        etDeadline.setOnClickListener {
            showDatePicker(etDeadline)
        }

        // --- Add Task Button Click ---
        btnAddTask.setOnClickListener {
            val title = etTaskName.text.toString().trim()
            val subject = autoComplete.text.toString().trim()
            val description = etDescription.text.toString().trim()
            val deadline = etDeadline.text.toString().trim()

            if (title.isEmpty()) {
                etTaskName.error = "Task name is required"
                return@setOnClickListener
            }

            if (deadline.isEmpty()) {
                etDeadline.error = "Deadline is required"
                return@setOnClickListener
            }

            val task = Task(
                title = title,
                subject = subject,
                description = description,
                deadline = deadline,
                isCompleted = false
            )

            lifecycleScope.launch {
                try {
                    val id = viewModel.addTask(task)
                    if (id > 0) {
                        Toast.makeText(this@AddTaskActivity, "✅ Task added successfully!", Toast.LENGTH_SHORT).show()
                        finish()
                    } else {
                        Toast.makeText(this@AddTaskActivity, "Failed to add task", Toast.LENGTH_SHORT).show()
                    }
                } catch (e: Exception) {
                    Toast.makeText(this@AddTaskActivity, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
                    e.printStackTrace()
                }
            }
        }
    }

    private fun showDatePicker(etDeadline: TextInputEditText) {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog = android.app.DatePickerDialog(
            this,
            { _, selectedYear, selectedMonth, selectedDay ->
                val selectedCalendar = Calendar.getInstance()
                selectedCalendar.set(selectedYear, selectedMonth, selectedDay)
                val dateFormat = SimpleDateFormat("MMM d, yyyy", Locale.US)
                etDeadline.setText(dateFormat.format(selectedCalendar.time))
            },
            year, month, day
        )

        datePickerDialog.show()
    }
}