package com.example.opsc7311_part2_groupa

import android.content.ContentValues
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.example.opsc7311_part2_groupa.databinding.ActivityTaskBinding

class Task : AppCompatActivity() {
    private lateinit var binding: ActivityTaskBinding
    private val tasks = mutableListOf<String>()
    private lateinit var adapter: ArrayAdapter<String>
    private lateinit var db: DBClass

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTaskBinding.inflate(layoutInflater)
        setContentView(binding.root)

        db = DBClass(applicationContext)

        val toolbar: Toolbar = binding.toolbar
        setSupportActionBar(toolbar)

        adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, tasks)
        binding.TaskList.adapter = adapter

        binding.addButton.setOnClickListener {
            val taskName = binding.addTaskName.text.toString()
            val subTaskName = binding.addSubTaskName.text.toString()
            val taskDate = binding.addTaskDate.text.toString()
            val taskHours = binding.addTaskWorkHours.text.toString().toFloatOrNull()

            if (taskName.isNotEmpty() && subTaskName.isNotEmpty() && taskDate.isNotEmpty() && taskHours != null) {
                val data = ContentValues()
                data.put(DBClass.TASK_NAME, taskName)
                data.put(DBClass.SUB_TASK_NAME, subTaskName)
                data.put(DBClass.TASK_DATE, taskDate)
                data.put(DBClass.TASK_HOURS, taskHours)

                val dbw = db.writableDatabase
                val rs: Long = dbw.insert(DBClass.TABLE_TASKS, null, data)
                if (rs != -1L) {
                    tasks.add("Task: $taskName, Sub-Task: $subTaskName, Date: $taskDate, Hours: $taskHours")
                    adapter.notifyDataSetChanged()
                } else {
                    Toast.makeText(this, "Failed to add task", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(this, "Please fill in all fields correctly", Toast.LENGTH_SHORT).show()
            }
        }
        displayTasks()
    }

    private fun displayTasks() {
        val dbr = db.readableDatabase
        val query = "SELECT * FROM ${DBClass.TABLE_TASKS}"
        val cursor = dbr.rawQuery(query, null)

        if (cursor.moveToFirst()) {
            val taskNameIndex = cursor.getColumnIndex(DBClass.TASK_NAME)
            val subTaskNameIndex = cursor.getColumnIndex(DBClass.SUB_TASK_NAME)
            val taskDateIndex = cursor.getColumnIndex(DBClass.TASK_DATE)
            val taskHoursIndex = cursor.getColumnIndex(DBClass.TASK_HOURS)

            if (taskNameIndex != -1 && subTaskNameIndex != -1 && taskDateIndex != -1 && taskHoursIndex != -1) {
                do {
                    val taskNameStr = cursor.getString(taskNameIndex)
                    val subTaskNameStr = cursor.getString(subTaskNameIndex)
                    val taskDateStr = cursor.getString(taskDateIndex)
                    val taskHoursStr = cursor.getFloat(taskHoursIndex)
                    tasks.add("Task: $taskNameStr, Sub-Task: $subTaskNameStr, Date: $taskDateStr, Hours: $taskHoursStr")
                } while (cursor.moveToNext())
            }
        }
        cursor.close()
        adapter.notifyDataSetChanged()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.task_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return onMenuItemSelected(item)
    }

    private fun onMenuItemSelected(item: MenuItem): Boolean {
        val dbw = db.writableDatabase
        return when (item.itemId) {
            R.id.menu_item1 -> {
                startActivity(Intent(this, TimeEntry::class.java))
                true
            }
            R.id.menu_item2 -> {
                startActivity(Intent(this, Homepage::class.java))
                true
            }
            R.id.menu_item3 -> {
                startActivity(Intent(this, TotalHours::class.java))
                true
            }
            R.id.menu_item4 -> {
                startActivity(Intent(this, Entries::class.java))
                true
            }
            R.id.menu_item5 -> {
                startActivity(Intent(this, Goal::class.java))
                true
            }
            R.id.menu_item6 -> {
                startActivity(Intent(this, Graphs::class.java))
                true
            }
            R.id.menu_item7 -> {
                dbw.execSQL("DROP TABLE IF EXISTS user_logged")
                dbw.execSQL("CREATE TABLE user_logged (email TEXT PRIMARY KEY)")
                startActivity(Intent(this, Login::class.java))
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}