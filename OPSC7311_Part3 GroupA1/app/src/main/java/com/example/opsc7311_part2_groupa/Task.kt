package com.example.opsc7311_part2_groupa

import android.content.ContentValues
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ListView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar

class Task : AppCompatActivity() {

    private lateinit var db: DBClass
    private lateinit var taskName: EditText
    private lateinit var subTaskName: EditText
    private lateinit var taskDate: EditText
    private lateinit var taskHours: EditText
    private lateinit var addButton: Button
    private lateinit var taskListView: ListView
    private lateinit var taskListAdapter: ArrayAdapter<String>
    private val taskList = mutableListOf<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_task)

        db = DBClass(applicationContext)
        taskName = findViewById(R.id.addTaskName)
        subTaskName = findViewById(R.id.addSubTaskName)
        taskDate = findViewById(R.id.addTaskDate)
        taskHours = findViewById(R.id.addTaskWorkHours)
        addButton = findViewById(R.id.addButton)
        taskListView = findViewById(R.id.TaskList) // Updated to match the new ID

        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        taskListAdapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, taskList)
        taskListView.adapter = taskListAdapter

        addButton.setOnClickListener {
            val taskNameStr = taskName.text.toString()
            val subTaskNameStr = subTaskName.text.toString()
            val taskDateStr = taskDate.text.toString()
            val taskHoursStr = taskHours.text.toString().toFloatOrNull()

            if (taskNameStr.isEmpty() || subTaskNameStr.isEmpty() || taskDateStr.isEmpty() || taskHoursStr == null) {
                Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val contentValues = ContentValues().apply {
                put(DBClass.TASK_NAME, taskNameStr)
                put(DBClass.SUB_TASK_NAME, subTaskNameStr)
                put(DBClass.TASK_DATE, taskDateStr)
                put(DBClass.TASK_HOURS, taskHoursStr)
            }

            val dbw = db.writableDatabase
            val result = dbw.insert(DBClass.TABLE_TASKS, null, contentValues)

            if (result != -1L) {
                Toast.makeText(this, "Task added successfully", Toast.LENGTH_SHORT).show()
                taskList.add("Task: $taskNameStr, Subtask: $subTaskNameStr, Date: $taskDateStr, Hours: $taskHoursStr")
                taskListAdapter.notifyDataSetChanged()
                clearFields()
            } else {
                Toast.makeText(this, "Failed to add task", Toast.LENGTH_SHORT).show()
            }
        }

        loadTasks()
    }

    private fun clearFields() {
        taskName.text.clear()
        subTaskName.text.clear()
        taskDate.text.clear()
        taskHours.text.clear()
    }

    private fun loadTasks() {
        val dbR = db.readableDatabase
        val cursor = dbR.rawQuery("SELECT * FROM ${DBClass.TABLE_TASKS}", null)
        if (cursor.moveToFirst()) {
            do {
                val taskNameStr = cursor.getString(cursor.getColumnIndex(DBClass.TASK_NAME))
                val subTaskNameStr = cursor.getString(cursor.getColumnIndex(DBClass.SUB_TASK_NAME))
                val taskDateStr = cursor.getString(cursor.getColumnIndex(DBClass.TASK_DATE))
                val taskHoursStr = cursor.getFloat(cursor.getColumnIndex(DBClass.TASK_HOURS))

                taskList.add("Task: $taskNameStr, Subtask: $subTaskNameStr, Date: $taskDateStr, Hours: $taskHoursStr")
            } while (cursor.moveToNext())
        }
        cursor.close()
        taskListAdapter.notifyDataSetChanged()
    }
}