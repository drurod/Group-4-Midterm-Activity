package com.example.group4midtermactivity

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.PopupMenu
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.example.group4midtermactivity.data.Task
import com.example.group4midtermactivity.viewmodel.TaskViewModel
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class TaskListActivity : AppCompatActivity() {

    private lateinit var viewModel: TaskViewModel
    private val searchQuery = MutableStateFlow("")
    private var allTasks = listOf<Task>()
    private var filteredTasks = listOf<Task>()
    private var currentSort = SortOption.DEADLINE
    private var isSelectionMode = false
    private val selectedTaskIds = mutableSetOf<Int>()

    enum class SortOption {
        DEADLINE,
        STATUS
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_task_list)

        viewModel = TaskViewModel(application)

        // --- Bottom Navigation ---
        val bottomNav = findViewById<BottomNavigationView>(R.id.bottom_navigation)
        bottomNav.menu.findItem(R.id.nav_tasks).isChecked = true

        bottomNav.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> {
                    startActivity(Intent(this, MainActivity::class.java))
                    finish()
                    true
                }
                R.id.nav_tasks -> true
                R.id.nav_notes -> {
                    startActivity(Intent(this, SubjectsActivity::class.java))
                    finish()
                    true
                }
                R.id.nav_done -> {
                    startActivity(Intent(this, CompletedTasksActivity::class.java))
                    finish()
                    true
                }
                else -> false
            }
        }

        // --- Search ---
        val searchLayout = findViewById<TextInputLayout>(R.id.search_layout)
        val etSearch = findViewById<TextInputEditText>(R.id.et_search)
        val ivSearch = findViewById<ImageView>(R.id.iv_search)

        ivSearch.setOnClickListener {
            if (searchLayout.visibility == View.GONE) {
                searchLayout.visibility = View.VISIBLE
                etSearch.requestFocus()
            } else {
                searchLayout.visibility = View.GONE
                etSearch.text?.clear()
                searchQuery.value = ""
            }
        }

        etSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                searchQuery.value = s?.toString()?.trim() ?: ""
            }
        })

        // --- Sort Button ---
        val tvSort = findViewById<TextView>(R.id.tv_sort)
        tvSort.setOnClickListener { view -> showSortMenu(view) }

        // --- Select Button ---
        val tvSelect = findViewById<TextView>(R.id.tv_select)
        tvSelect.setOnClickListener { toggleSelectionMode() }

        // --- Selection Bottom Bar ---
        val tvSelectAll = findViewById<TextView>(R.id.tv_select_all)
        tvSelectAll.setOnClickListener { toggleSelectAll() }

        val tvDeleteSelected = findViewById<TextView>(R.id.tv_delete_selected)
        tvDeleteSelected.setOnClickListener { deleteSelectedTasks() }

        // --- Subject Filter ---
        val subjectFilter = intent.getStringExtra("subject")
        if (!subjectFilter.isNullOrEmpty()) {
            Toast.makeText(this, "📚 Showing tasks for: $subjectFilter", Toast.LENGTH_SHORT).show()
        }

        // --- Load Tasks ---
        loadTasks(subjectFilter)
    }

    private fun showSortMenu(view: View) {
        val popupMenu = PopupMenu(this, view)
        popupMenu.menu.add(0, 1, 0, "Sort by Deadline 📅")
        popupMenu.menu.add(0, 2, 1, "Sort by Status 🔵")

        when (currentSort) {
            SortOption.DEADLINE -> popupMenu.menu.findItem(1)?.isChecked = true
            SortOption.STATUS -> popupMenu.menu.findItem(2)?.isChecked = true
        }

        popupMenu.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                1 -> {
                    currentSort = SortOption.DEADLINE
                    updateSortButtonText("Sort by Deadline 📅")
                    applyFilter(searchQuery.value)
                    true
                }
                2 -> {
                    currentSort = SortOption.STATUS
                    updateSortButtonText("Sort by Status 🔵")
                    applyFilter(searchQuery.value)
                    true
                }
                else -> false
            }
        }
        popupMenu.show()
    }

    private fun updateSortButtonText(text: String) {
        findViewById<TextView>(R.id.tv_sort).text = text
    }

    // ==================== FIXED SORT FUNCTION ====================

    private fun sortTasks(tasks: List<Task>): List<Task> {
        return when (currentSort) {
            SortOption.DEADLINE -> tasks.sortedBy { parseDate(it.deadline) }
            SortOption.STATUS -> tasks.sortedBy { it.isCompleted } // false (pending) first
        }
    }

    // Helper to parse "MMM d, yyyy" into a Date for sorting
    private fun parseDate(dateStr: String): Date {
        return try {
            val format = SimpleDateFormat("MMM d, yyyy", Locale.US)
            format.parse(dateStr) ?: Date(0)
        } catch (e: Exception) {
            // Fallback: try a different format or return earliest date
            Date(0)
        }
    }

    // ==================== SELECTION LOGIC ====================

    private fun toggleSelectionMode() {
        isSelectionMode = !isSelectionMode
        if (!isSelectionMode) selectedTaskIds.clear()
        updateSelectionUI()
        applyFilter(searchQuery.value)
    }

    private fun toggleSelectAll() {
        if (selectedTaskIds.size == filteredTasks.size && filteredTasks.isNotEmpty()) {
            selectedTaskIds.clear()
        } else {
            filteredTasks.forEach { selectedTaskIds.add(it.id) }
        }
        updateSelectionUI()
        applyFilter(searchQuery.value)
    }

    private fun deleteSelectedTasks() {
        if (selectedTaskIds.isEmpty()) {
            Toast.makeText(this, "No tasks selected", Toast.LENGTH_SHORT).show()
            return
        }

        AlertDialog.Builder(this)
            .setTitle("Delete Selected Tasks")
            .setMessage("Are you sure you want to delete ${selectedTaskIds.size} task(s)?")
            .setPositiveButton("Delete") { _, _ ->
                lifecycleScope.launch {
                    val tasksToDelete = allTasks.filter { it.id in selectedTaskIds }
                    tasksToDelete.forEach { viewModel.deleteTask(it) }
                    selectedTaskIds.clear()
                    isSelectionMode = false
                    updateSelectionUI()
                    loadTasks(intent.getStringExtra("subject"))
                    Toast.makeText(
                        this@TaskListActivity,
                        "Deleted ${tasksToDelete.size} task(s)",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun updateSelectionUI() {
        val bottomBar = findViewById<LinearLayout>(R.id.selection_bottom_bar)
        val tvSelect = findViewById<TextView>(R.id.tv_select)

        if (isSelectionMode) {
            bottomBar.visibility = View.VISIBLE
            tvSelect.text = "Cancel"
            tvSelect.setTextColor(ContextCompat.getColor(this, android.R.color.holo_red_dark))
        } else {
            bottomBar.visibility = View.GONE
            tvSelect.text = "Select"
            tvSelect.setTextColor(ContextCompat.getColor(this, android.R.color.holo_blue_dark))
        }
        updateSelectedCount()
    }

    private fun updateSelectedCount() {
        val countView = findViewById<TextView>(R.id.tv_selected_count)
        countView.text = "${selectedTaskIds.size} selected"
        val selectAllText = if (selectedTaskIds.size == filteredTasks.size && filteredTasks.isNotEmpty()) {
            "Deselect All"
        } else {
            "Select All"
        }
        findViewById<TextView>(R.id.tv_select_all).text = selectAllText
    }

    // ==================== LOAD TASKS ====================

    private fun loadTasks(subjectFilter: String?) {
        lifecycleScope.launch {
            viewModel.allTasks.collect { tasks ->
                val filteredBySubject = if (!subjectFilter.isNullOrEmpty()) {
                    tasks.filter { it.subject == subjectFilter }
                } else {
                    tasks
                }
                allTasks = filteredBySubject
                applyFilter(searchQuery.value)
            }
        }

        lifecycleScope.launch {
            searchQuery.collect { query ->
                applyFilter(query)
            }
        }
    }

    private fun applyFilter(query: String) {
        val container = findViewById<LinearLayout>(R.id.task_container)
        container.removeAllViews()

        val filtered = if (query.isEmpty()) {
            allTasks
        } else {
            allTasks.filter { task ->
                task.title.contains(query, ignoreCase = true) ||
                        task.subject.contains(query, ignoreCase = true) ||
                        task.description.contains(query, ignoreCase = true)
            }
        }

        filteredTasks = sortTasks(filtered)

        if (filteredTasks.isEmpty()) {
            val emptyView = TextView(this).apply {
                text = if (query.isEmpty()) {
                    "🎉 No tasks yet!\nAdd one using the + button."
                } else {
                    "🔍 No tasks found for \"$query\""
                }
                textSize = 18f
                setPadding(16, 32, 16, 32)
                gravity = android.view.Gravity.CENTER
                setTextColor(ContextCompat.getColor(context, android.R.color.darker_gray))
            }
            container.addView(emptyView)
        } else {
            filteredTasks.forEach { task ->
                val taskView = createTaskItemView(task)
                container.addView(taskView)
            }
        }
        updateSelectedCount()
    }

    private fun createTaskItemView(task: Task): LinearLayout {
        return LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            setPadding(12, 12, 12, 12)
            setBackgroundResource(android.R.drawable.btn_default)
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply { bottomMargin = 8 }
            gravity = android.view.Gravity.CENTER_VERTICAL

            val statusView = TextView(context).apply {
                text = if (task.isCompleted) "🟢" else "⭕"
                textSize = 20f
                setPadding(0, 0, 12, 0)
                visibility = if (isSelectionMode) View.GONE else View.VISIBLE
            }
            addView(statusView)

            val checkBox = CheckBox(context).apply {
                visibility = if (isSelectionMode) View.VISIBLE else View.GONE
                isChecked = selectedTaskIds.contains(task.id)
                setPadding(0, 0, 12, 0)
                setOnCheckedChangeListener { _, isChecked ->
                    if (isChecked) selectedTaskIds.add(task.id) else selectedTaskIds.remove(task.id)
                    updateSelectedCount()
                    applyFilter(searchQuery.value)
                }
            }
            addView(checkBox)

            val infoContainer = LinearLayout(context).apply {
                orientation = LinearLayout.VERTICAL
                layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f)
            }

            val titleView = TextView(context).apply {
                text = task.title
                textSize = 16f
                setTextColor(ContextCompat.getColor(context, android.R.color.black))
            }
            infoContainer.addView(titleView)

            val detailsView = TextView(context).apply {
                text = "📚 ${task.subject}  📅 ${task.deadline}"
                textSize = 14f
                setTextColor(ContextCompat.getColor(context, android.R.color.darker_gray))
            }
            infoContainer.addView(detailsView)

            addView(infoContainer)

            setOnClickListener {
                if (isSelectionMode) {
                    checkBox.performClick()
                } else {
                    viewModel.selectTask(task)
                    val intent = Intent(context, TaskDetailsActivity::class.java)
                    intent.putExtra("task_id", task.id)
                    context.startActivity(intent)
                }
            }

            setOnLongClickListener {
                if (!isSelectionMode) {
                    toggleSelectionMode()
                    selectedTaskIds.add(task.id)
                    updateSelectionUI()
                    applyFilter(searchQuery.value)
                    true
                } else {
                    false
                }
            }
        }
    }
}