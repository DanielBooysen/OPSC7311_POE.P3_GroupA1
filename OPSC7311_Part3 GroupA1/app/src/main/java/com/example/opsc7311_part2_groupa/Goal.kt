package com.example.opsc7311_part2_groupa

import android.content.ContentValues
import android.content.Intent
import android.database.sqlite.SQLiteDatabase
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import android.view.Menu
import android.view.MenuItem
import android.widget.ArrayAdapter
import androidx.appcompat.widget.Toolbar
import com.example.opsc7311_part2_groupa.databinding.ActivityGoalBinding
import com.example.opsc7311_part2_groupa.DBClass.Companion.DATE_GOAL
import com.example.opsc7311_part2_groupa.DBClass.Companion.MAX_HOURS
import com.example.opsc7311_part2_groupa.DBClass.Companion.MIN_HOURS
import com.example.opsc7311_part2_groupa.DBClass.Companion.TABLE_GOALS
import com.example.opsc7311_part2_groupa.DBClass.Companion.KEY_MAIL

class Goal : AppCompatActivity() {
    private lateinit var binding: ActivityGoalBinding
    private val goals = mutableListOf<String>()
    private lateinit var adapter: ArrayAdapter<String>
    private lateinit var db: SQLiteDatabase
    private var loggedInEmail: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityGoalBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val toolbar: Toolbar = binding.toolbar
        setSupportActionBar(toolbar)

        val dbhelp = DBClass(applicationContext)
        db = dbhelp.writableDatabase

        // Get logged in user's email
        loggedInEmail = getLoggedInUserEmail()

        adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, goals)
        binding.GoalList.adapter = adapter

        binding.addButton.setOnClickListener {
            val date = binding.addDate.text.toString()
            val minHours = binding.addMinGoal.text.toString().toFloatOrNull()
            val maxHours = binding.editTextNumber.text.toString().toFloatOrNull()

            if (date.isNotEmpty() && minHours != null && maxHours != null && loggedInEmail != null) {
                val data = ContentValues()
                data.put(DATE_GOAL, date)
                data.put(MIN_HOURS, minHours)
                data.put(MAX_HOURS, maxHours)
                data.put(KEY_MAIL, loggedInEmail)

                val rs: Long = db.insert(TABLE_GOALS, null, data)
                if (rs != -1L) {
                    goals.add("Date: $date, Minimum Hours: $minHours, Maximum Hours: $maxHours")
                    adapter.notifyDataSetChanged()
                } else {
                    Toast.makeText(this, "Failed to add goal", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(this, "Please fill in all fields correctly", Toast.LENGTH_SHORT).show()
            }
        }
        displayGoals()
    }

    private fun displayGoals() {
        val query = "SELECT * FROM $TABLE_GOALS WHERE $KEY_MAIL = ?"
        val rs = db.rawQuery(query, arrayOf(loggedInEmail))

        if (rs.moveToFirst()) {
            val dateIndex = rs.getColumnIndex(DATE_GOAL)
            val minHoursIndex = rs.getColumnIndex(MIN_HOURS)
            val maxHoursIndex = rs.getColumnIndex(MAX_HOURS)

            if (dateIndex != -1 && minHoursIndex != -1 && maxHoursIndex != -1) {
                do {
                    val date = rs.getString(dateIndex)
                    val minHours = rs.getFloat(minHoursIndex)
                    val maxHours = rs.getFloat(maxHoursIndex)
                    goals.add("Date: $date, Minimum Hours: $minHours, Maximum Hours: $maxHours")
                } while (rs.moveToNext())
            }
        }
        rs.close()
        adapter.notifyDataSetChanged()
    }

    private fun getLoggedInUserEmail(): String? {
        val query = "SELECT email FROM user_logged"
        val rs = db.rawQuery(query, null)
        var email: String? = null
        if (rs.moveToFirst()) {
            val emailIndex = rs.getColumnIndex("email")
            email = rs.getString(emailIndex)
        }
        rs.close()
        return email
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.goal_menu, menu)
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
                startActivity(Intent(this, Graphs::class.java))
                true
            }
            R.id.menu_item6 -> {
                startActivity(Intent(this, Task::class.java))
                true
            }
            R.id.menu_item7 -> {
                db.execSQL("DROP TABLE IF EXISTS user_logged")
                db.execSQL("CREATE TABLE user_logged (email TEXT PRIMARY KEY)")
                startActivity(Intent(this, Login::class.java))
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}