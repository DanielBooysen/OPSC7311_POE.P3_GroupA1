package com.example.opsc7311_part2_groupa

import android.content.ContentValues
import android.content.DialogInterface
import android.content.Intent
import android.net.Uri
import android.database.sqlite.SQLiteDatabase
import android.os.Bundle
import android.text.InputType
import android.view.Menu
import android.view.MenuItem
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ListView
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast

import androidx.appcompat.app.AlertDialog

import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar

class Time_entry : AppCompatActivity() {
    private var hours = arrayOf(0,1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18,19,20,21,22,23)
    private var minutes = arrayOf(0,1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18,19,20,21,22,23,24,25,26,27,28,29,30,31,32,33,34,35,36,37,38,39,40,41,42,43,44,45,46,47,48,49,50,51,52,53,54,55,56,57,58,59)
    private var selectedImageUri: Uri? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_time_entry)

        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        //Initialize database for database opperations
        val dbhelp = DBClass(applicationContext)
        var db = dbhelp.writableDatabase
        val dbr = dbhelp.readableDatabase
        //Get all saved gategories to display for user selection
        val getCategoriesQuery = "SELECT * FROM categories"
        val catResult = dbr.rawQuery(getCategoriesQuery, null)

        val categories = mutableListOf<String>("Select option")

        //saved
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

        //Creates a popup window to input a new category and save it to the database
        val categoryButton = findViewById<Button>(R.id.addCategory)
        categoryButton.setOnClickListener {
            val builder: android.app.AlertDialog.Builder = android.app.AlertDialog.Builder(this)
            builder.setTitle("Enter the category!")

            val input = EditText(this)
            input.setInputType(InputType.TYPE_CLASS_TEXT)
            builder.setView(input)

            builder.setPositiveButton("OK", DialogInterface.OnClickListener { dialog, which ->
                categories.add(input.getText().toString())
                val data = ContentValues()
                data.put("category", input.getText().toString())
                db.insert("categories", null, data)
            })
            builder.setNegativeButton("Cancel",
                DialogInterface.OnClickListener { dialog, which -> dialog.cancel() })

            builder.show()
        }

        //Get all saved time entries
        val queryEntries = "SELECT category, time FROM entries"
        val entrResult = dbr.rawQuery(queryEntries, null)

        val entriesList = mutableListOf<Pair<String, String>>()
        val entryList = findViewById<ListView>(R.id.timeEntryList)

        //Get all saved time entries and display them to the user
        if (entrResult != null && entrResult.moveToFirst()) {
            val categoryIndex = entrResult.getColumnIndex("category")
            val timeEntryIndex = entrResult.getColumnIndex("time_entry")

            if (categoryIndex != -1 && timeEntryIndex != -1) {
                do {
                    val category = entrResult.getString(categoryIndex)
                    val timeEntry = entrResult.getString(timeEntryIndex)
                    entriesList.add(Pair(category, timeEntry))
                } while (entrResult.moveToNext())
            } else {
                Toast.makeText(this, "Entries empty", Toast.LENGTH_SHORT).show()
            }
            entrResult.close()
        } else {
            Toast.makeText(this, "Entries empty", Toast.LENGTH_SHORT).show()
        }

        //Adapter to display the entries
        val adapter = ArrayAdapter<Pair<String, String>>(
            this,
            android.R.layout.simple_list_item_1,
            entriesList
        )

        entryList.adapter = adapter

        val categoriesSpinner = findViewById<Spinner>(R.id.categoryPicker)

        //Add categories to the spinner for display
        val categoryAdapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, categories)
        categoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        categoriesSpinner.adapter = categoryAdapter

        val startHourSpinner = findViewById<Spinner>(R.id.hourStart)
        val endHourSpinner = findViewById<Spinner>(R.id.hourEnd)
        val startMinuteSpinner = findViewById<Spinner>(R.id.minuteStart)
        val endMinuteSpinner = findViewById<Spinner>(R.id.minuteEnd)

        //Adapters to dislpay the time entry options for users
        val hourAdapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, hours)
        val minuteAdapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, minutes)
        startHourSpinner.adapter = hourAdapter
        endHourSpinner.adapter = hourAdapter
        startMinuteSpinner.adapter = minuteAdapter
        endMinuteSpinner.adapter = minuteAdapter

        val workTimeDisplay = findViewById<TextView>(R.id.workTimeView)
        val descriptionView = findViewById<TextView>(R.id.descriptionView)
        val dateView = findViewById<TextView>(R.id.dateView)

        //calculates the time spent on a category and saves it to the database
        val submitEntry = findViewById<Button>(R.id.submitTimeEntry)
        submitEntry.setOnClickListener {
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

            workTimeDisplay.text = "$workTime"

            val data = ContentValues()
            data.put("time", workTime)
            data.put("category", categoryChosen)
            data.put("description", description)
            data.put("date", date)
            val insert: Long = db.insert("entries", null, data)

            if(!insert.equals(-1)){
                val ad = AlertDialog.Builder(this)
                ad.setTitle("Message")
                ad.setMessage("Success")
                ad.setPositiveButton("Ok", null)
                ad.show()
            }

            setContentView(R.layout.item_timesheet_entry)

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
        when (item.itemId) {
            R.id.menu_item8 -> {
                startActivity(Intent(this, Homepage::class.java))
                return true
            }
            R.id.menu_item10 -> {
                startActivity(Intent(this, Goal::class.java))
                return true
            }
            R.id.menu_item11 -> {
                startActivity(Intent(this, Login::class.java))
                return true
            }
            R.id.menu_item12 -> {
                startActivity(Intent(this, ListView::class.java))
                return true
            }
            else -> return super.onOptionsItemSelected(item)
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

}



