package com.example.opsc7311_part2_groupa

import android.content.ContentValues
import android.content.DialogInterface
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.CountDownTimer
import android.text.InputType
import android.view.Menu
import android.view.MenuItem
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import android.database.sqlite.SQLiteDatabase

class TimeEntry : AppCompatActivity() {
    private var hours = arrayOf(0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23)
    private var minutes = arrayOf(0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25, 26, 27, 28, 29, 30, 31, 32, 33, 34, 35, 36, 37, 38, 39, 40, 41, 42, 43, 44, 45, 46, 47, 48, 49, 50, 51, 52, 53, 54, 55, 56, 57, 58, 59)
    private var selectedImageUri: Uri? = null
    private var timerRunning = false
    private var timer: CountDownTimer? = null
    private var elapsedTime: Long = 0
    private lateinit var dbhelp: DBClass
    private lateinit var dbw: SQLiteDatabase
    private lateinit var dbr: SQLiteDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_time_entry)

        dbhelp = DBClass(applicationContext)
        dbw = dbhelp.writableDatabase
        dbr = dbhelp.readableDatabase

        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        val startStopButton = findViewById<Button>(R.id.startStopButton)

        startStopButton.setOnClickListener {
            if (timerRunning) {
                stopTimer()
            } else {
                startTimer()
            }
        }

        // Get all saved categories to display for user selection
        val getCategoriesQuery = "SELECT * FROM categories"
        val catResult = dbr.rawQuery(getCategoriesQuery, null)

        val categories = mutableListOf<String>("Select option")

        if (catResult != null && catResult.moveToFirst()) {
            val categoryIndex = catResult.getColumnIndex("category")
            if (categoryIndex != -1) {
                do {
                    val category = catResult.getString(categoryIndex)
                    categories.add(category)
                } while (catResult.moveToNext())
            } else {
                Toast.makeText(this, "Categories empty", Toast.LENGTH_SHORT).show()
            }
            catResult.close()
        } else {
            Toast.makeText(this, "Categories empty", Toast.LENGTH_SHORT).show()
        }

        // Creates a popup window to input a new category and save it to the database
        val categoryButton = findViewById<Button>(R.id.addCategory)
        categoryButton.setOnClickListener {
            val builder: android.app.AlertDialog.Builder = android.app.AlertDialog.Builder(this)
            builder.setTitle("Enter the category!")

            val input = EditText(this)
            input.inputType = InputType.TYPE_CLASS_TEXT
            builder.setView(input)

            builder.setPositiveButton("OK") { dialog, which ->
                categories.add(input.text.toString())
                val data = ContentValues()
                data.put("category", input.text.toString())
                dbw.insert("categories", null, data)
            }
            builder.setNegativeButton("Cancel") { dialog, which -> dialog.cancel() }

            builder.show()
        }

        val categoriesSpinner = findViewById<Spinner>(R.id.categoryPicker)

        // Add categories to the spinner for display
        val categoryAdapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, categories)
        categoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        categoriesSpinner.adapter = categoryAdapter

        val startHourSpinner = findViewById<Spinner>(R.id.hourStart)
        val endHourSpinner = findViewById<Spinner>(R.id.hourEnd)
        val startMinuteSpinner = findViewById<Spinner>(R.id.minuteStart)
        val endMinuteSpinner = findViewById<Spinner>(R.id.minuteEnd)

        // Adapters to display the time entry options for users
        val hourAdapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, hours)
        val minuteAdapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, minutes)
        startHourSpinner.adapter = hourAdapter
        endHourSpinner.adapter = hourAdapter
        startMinuteSpinner.adapter = minuteAdapter
        endMinuteSpinner.adapter = minuteAdapter

        val workTimeDisplay = findViewById<TextView>(R.id.workTimeView)
        val descriptionView = findViewById<TextView>(R.id.descriptionView)
        val dateView = findViewById<TextView>(R.id.dateView)

        // Calculates the time spent on a category and saves it to the database
        val submitEntry = findViewById<Button>(R.id.submitTimeEntry)
        submitEntry.setOnClickListener {

            if (workTimeDisplay.text == "@string/work_time") {
                val startHour = startHourSpinner.selectedItem.toString().toInt()
                val endHour = endHourSpinner.selectedItem.toString().toInt()
                val startMinute = startMinuteSpinner.selectedItem.toString().toInt()
                val endMinute = endMinuteSpinner.selectedItem.toString().toInt()
                val categoryChosen = categoriesSpinner.selectedItem.toString()
                val date = dateView.text.toString()
                val description = descriptionView.text.toString()

                val startTime = startHour * 60 + startMinute
                val endTime = endHour * 60 + endMinute
                val totalTime = endTime - startTime

                val workHour = totalTime / 60
                val workMinute = totalTime % 60

                val workTime = "$workHour:$workMinute"

                workTimeDisplay.text = workTime

                val query1 = "SELECT email FROM user_logged"
                val userCursor = dbr.rawQuery(query1, null)
                var email = ""
                if (userCursor.moveToFirst()) {
                    val index = userCursor.getColumnIndex("email")
                    email = userCursor.getString(index)
                }
                userCursor.close()

                val data = ContentValues()
                data.put("time", workTime)
                data.put("category", categoryChosen)
                data.put("email", email)
                data.put("description", description)
                data.put("date", date)
                val insert: Long = dbw.insert("entries", null, data)

                if (insert != -1L) {
                    val ad = AlertDialog.Builder(this)
                    ad.setTitle("Message")
                    ad.setMessage("Success")
                    ad.setPositiveButton("Ok", null)
                    ad.show()
                }

                setContentView(R.layout.item_timesheet_entry)
            } else {
                val categoryChosen = categoriesSpinner.selectedItem.toString()
                val date = dateView.text.toString()
                val description = descriptionView.text.toString()
                val workTime = workTimeDisplay.text.toString()

                val query1 = "SELECT email FROM user_logged"
                val userCursor = dbr.rawQuery(query1, null)
                var email = ""
                if (userCursor.moveToFirst()) {
                    val index = userCursor.getColumnIndex("email")
                    email = userCursor.getString(index)
                }
                userCursor.close()

                val data = ContentValues()
                data.put("time", workTime)
                data.put("category", categoryChosen)
                data.put("email", email)
                data.put("description", description)
                data.put("date", date)
                val insert: Long = dbw.insert("entries", null, data)

                if (insert != -1L) {
                    val ad = AlertDialog.Builder(this)
                    ad.setTitle("Message")
                    ad.setMessage("Success")
                    ad.setPositiveButton("Ok", null)
                    ad.show()
                }

                setContentView(R.layout.item_timesheet_entry)
            }
        }

        // Photo button click listener
        val photoButton = findViewById<Button>(R.id.photoButton)
        photoButton.setOnClickListener {
            val galleryIntent = Intent(Intent.ACTION_PICK)
            galleryIntent.type = "image/*"
            startActivityForResult(galleryIntent, GALLERY_REQUEST_CODE)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.time_entrymenu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return onMenuItemSelected(item)
    }

    private fun onMenuItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.menu_item1 -> {
                startActivity(Intent(this, TotalHours::class.java))
                true
            }
            R.id.menu_item2 -> {
                startActivity(Intent(this, Goal::class.java))
                true
            }
            R.id.menu_item3 -> {
                startActivity(Intent(this, Homepage::class.java))
                true
            }
            R.id.menu_item4 -> {
                startActivity(Intent(this, Entries::class.java))
                true
            }
            R.id.menu_item5 -> {
                dbw.execSQL("DROP TABLE IF EXISTS user_logged")
                dbw.execSQL("CREATE TABLE user_logged (email TEXT PRIMARY KEY)")
                startActivity(Intent(this, Login::class.java))
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == GALLERY_REQUEST_CODE && resultCode == RESULT_OK && data != null) {
            selectedImageUri = data.data
        }
    }

    companion object {
        private const val GALLERY_REQUEST_CODE = 1001
    }

    private fun startTimer() {
        timer = object : CountDownTimer(Long.MAX_VALUE, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                elapsedTime = Long.MAX_VALUE - millisUntilFinished
                updateTimerDisplay()
            }

            override fun onFinish() {
                // Timer finished
            }
        }

        timer?.start()
        timerRunning = true
    }

    private fun stopTimer() {
        timer?.cancel()
        timerRunning = false

        val hours = (elapsedTime / 60000) / 60
        val minutes = (elapsedTime / 60000) % 60

        val workTimeView = findViewById<TextView>(R.id.workTimeView)
        workTimeView.text = String.format("%02d:%02d", hours, minutes)
    }

    private fun updateTimerDisplay() {
        val hours = elapsedTime / 3600000
        val minutes = (elapsedTime % 3600000) / 60000
        val seconds = (elapsedTime % 60000) / 1000

        val timerTextView = findViewById<TextView>(R.id.timerTextView)
        timerTextView.text = String.format("%02d:%02d:%02d", hours, minutes, seconds)
    }

    override fun onStop() {
        super.onStop()
        stopTimer()
    }
}