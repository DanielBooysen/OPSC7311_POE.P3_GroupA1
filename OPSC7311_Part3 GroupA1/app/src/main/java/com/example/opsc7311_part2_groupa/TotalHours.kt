package com.example.opsc7311_part2_groupa

import android.app.DatePickerDialog
import android.content.Intent
import android.database.sqlite.SQLiteDatabase
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ListView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import java.text.SimpleDateFormat
import java.util.*

class TotalHours : AppCompatActivity() {
    private lateinit var db: SQLiteDatabase
    private lateinit var startDate: EditText
    private lateinit var endDate: EditText
    private lateinit var calculateButton: Button
    private lateinit var listView: ListView
    private lateinit var adapter: ArrayAdapter<String>
    private val categoryDisplayList = mutableListOf<String>()
    private val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.US)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_total_hours)

        db = DBClass(applicationContext).readableDatabase

        startDate = findViewById(R.id.startDate)
        endDate = findViewById(R.id.endDate)
        calculateButton = findViewById(R.id.calculateButton)
        listView = findViewById(R.id.total_hours)

        startDate.setOnClickListener { showDatePickerDialog(startDate) }
        endDate.setOnClickListener { showDatePickerDialog(endDate) }

        calculateButton.setOnClickListener { calculateTotalHours() }

        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, categoryDisplayList)
        listView.adapter = adapter
    }

    private fun showDatePickerDialog(editText: EditText) {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog = DatePickerDialog(this, { _, selectedYear, selectedMonth, selectedDay ->
            val selectedDate = Calendar.getInstance()
            selectedDate.set(selectedYear, selectedMonth, selectedDay)
            editText.setText(dateFormat.format(selectedDate.time))
        }, year, month, day)

        datePickerDialog.show()
    }

    private fun calculateTotalHours() {
        val start = startDate.text.toString()
        val end = endDate.text.toString()

        if (start.isEmpty() || end.isEmpty()) {
            Toast.makeText(this, "Please select both start and end dates", Toast.LENGTH_SHORT).show()
            return
        }

        val categories = getCategories()
        val userEmail = getLoggedInUserEmail()
        categoryDisplayList.clear()

        val categoryTimeMap = mutableMapOf<String, Int>()

        for (category in categories) {
            val entryQuery = "SELECT time FROM entries WHERE category = ? AND email = ? AND date BETWEEN ? AND ?"
            val entryCursor = db.rawQuery(entryQuery, arrayOf(category, userEmail, start, end))

            var totalMinutes = 0
            if (entryCursor.moveToFirst()) {
                val index = entryCursor.getColumnIndex("time")
                do {
                    val timeEntry = entryCursor.getString(index)
                    val parts = timeEntry.split(":")
                    val hours = parts[0].toInt()
                    val minutes = parts[1].toInt()
                    totalMinutes += hours * 60 + minutes
                } while (entryCursor.moveToNext())
            }
            entryCursor.close()

            if (totalMinutes > 0) {
                categoryTimeMap[category] = totalMinutes
            }
        }

        for ((category, totalMinutes) in categoryTimeMap) {
            val hours = totalMinutes / 60
            val minutes = totalMinutes % 60
            val displayTime = String.format("%02d:%02d", hours, minutes)
            val display = "$category: $displayTime"
            categoryDisplayList.add(display)
        }

        adapter.notifyDataSetChanged()
    }

    private fun getCategories(): List<String> {
        val query = "SELECT category FROM categories"
        val categoryCursor = db.rawQuery(query, null)
        val categories = mutableListOf<String>()

        if (categoryCursor.moveToFirst()) {
            val index = categoryCursor.getColumnIndex("category")
            if (index != -1) {
                do {
                    val category = categoryCursor.getString(index)
                    categories.add(category)
                } while (categoryCursor.moveToNext())
            }
        }
        categoryCursor.close()
        return categories
    }

    private fun getLoggedInUserEmail(): String {
        val query = "SELECT email FROM user_logged"
        val userCursor = db.rawQuery(query, null)
        var email = ""
        if (userCursor.moveToFirst()) {
            val index = userCursor.getColumnIndex("email")
            email = userCursor.getString(index)
        }
        userCursor.close()
        return email
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.total_hoursmenu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return onMenuItemSelected(item)
    }

    private fun onMenuItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.menu_item1 -> {
                startActivity(Intent(this, TimeEntry::class.java))
                true
            }
            R.id.menu_item2 -> {
                startActivity(Intent(this, Entries::class.java))
                true
            }
            R.id.menu_item3 -> {
                startActivity(Intent(this, Goal::class.java))
                true
            }
            R.id.menu_item4 -> {
                startActivity(Intent(this, Homepage::class.java))
                true
            }
            R.id.menu_item5 -> {
                db.execSQL("DROP TABLE IF EXISTS user_logged")
                db.execSQL("CREATE TABLE user_logged (email TEXT PRIMARY KEY)")
                startActivity(Intent(this, Login::class.java))
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}