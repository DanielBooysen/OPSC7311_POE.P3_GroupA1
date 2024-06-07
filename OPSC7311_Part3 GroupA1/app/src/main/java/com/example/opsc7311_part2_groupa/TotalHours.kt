package com.example.opsc7311_part2_groupa


import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.LinearLayout
import android.widget.ListView
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class TotalHours : AppCompatActivity() {
    val db = DBClass(applicationContext).readableDatabase
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_total_hours)
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        // Fetch categories from the database
        val query = ("SELECT category FROM categories")
        val categoryCursor = db.rawQuery(query, null)
        val categories: MutableList<String> = mutableListOf()
        if(categoryCursor.moveToFirst()){
            val index = categoryCursor.getColumnIndex("category")
            if(index != -1){
                do {
                    val category = categoryCursor.getString(index)
                    categories.add(category)
                }while(categoryCursor.moveToNext())
            }
        }
        categoryCursor.close()

        val query1 = ("SELECT email FROM user_logged")
        val userCursor = db.rawQuery(query1, null)
        var email: String = ""
        if(userCursor.moveToFirst()){
            val index = userCursor.getColumnIndex("email")
            email = userCursor.getString(index)
        }
        userCursor.close()

        val categoryTimeMap = mutableMapOf<String, Int>()

        for(category in categories){
            val entryQuery = "SELECT time FROM entries WHERE category = ? AND email = ?"
            val entryCursor = db.rawQuery(entryQuery, arrayOf(category, email))

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

            categoryTimeMap[category] = totalMinutes
        }

        val categoryDisplayList = mutableListOf<String>()

        for ((category, totalMinutes) in categoryTimeMap) {
            val hours = totalMinutes / 60
            val minutes = totalMinutes % 60
            val displayTime = "$hours:$minutes"
            val display = "$category: $displayTime"
            categoryDisplayList.add(display)
        }

        val listView: ListView = findViewById(R.id.total_hours)
        val adapter = CategoryAdapter(this, categoryDisplayList)
        listView.adapter = adapter
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
                val query = "DROP TABLE IF EXISTS user_logged"
                val query1 = "CREATE TABLE user_logged (email TEXT PRIMARY KEY)"
                db.rawQuery(query, null)
                db.rawQuery(query1, null)
                startActivity(Intent(this, Login::class.java))
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}