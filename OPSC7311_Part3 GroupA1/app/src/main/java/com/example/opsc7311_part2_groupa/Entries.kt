package com.example.opsc7311_part2_groupa

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.ListView
import androidx.appcompat.widget.Toolbar

class Entries : AppCompatActivity() {
    private val db by lazy { DBClass(applicationContext).readableDatabase }

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_entries)

        Log.d("ENTRY_DEBUG", "onCreate: Activity started")

        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        Log.d("ENTRY_DEBUG", "onCreate: Toolbar set")

        val entries = getTimesheetEntriesFromDatabase()

        val listView: ListView = findViewById(R.id.entries_list_view)
        val adapter = EntryAdapter(this, entries)
        listView.adapter = adapter

        Log.d("ENTRY_DEBUG", "onCreate: ListView adapter set")
    }

    @SuppressLint("Range")
    private fun getTimesheetEntriesFromDatabase(): List<TimeSheetEntry> {
        val entries = mutableListOf<TimeSheetEntry>()

        Log.d("ENTRY_DEBUG", "getTimesheetEntriesFromDatabase: Fetching email")

        val query1 = ("SELECT email FROM user_logged")
        val userCursor = db.rawQuery(query1, null)
        var email: String = ""
        if(userCursor.moveToFirst()){
            val index = userCursor.getColumnIndex("email")
            email = userCursor.getString(index)
        }
        userCursor.close()

        Log.d("ENTRY_DEBUG", "getTimesheetEntriesFromDatabase: Retrieved email $email")

        val query = "SELECT * FROM entries WHERE email = '$email'"
        db.rawQuery(query, null).use { cursor ->
            while (cursor.moveToNext()) {
                val time = cursor.getString(cursor.getColumnIndex(DBClass.TIME_ENTRY))
                val category = cursor.getString(cursor.getColumnIndex(DBClass.CATEGORY_ENTRY))
                val description = cursor.getString(cursor.getColumnIndex(DBClass.DESCRIPTION_ENTRY))
                val date = cursor.getString(cursor.getColumnIndex(DBClass.DATE_ENTRY))

                val entry = TimeSheetEntry(time, category, description, date)
                entries.add(entry)

                Log.d("ENTRY_DEBUG", "getTimesheetEntriesFromDatabase: Retrieved entry $entry")
            }
        }

        // Log the number of retrieved entries
        Log.d("ENTRY_DEBUG", "getTimesheetEntriesFromDatabase: Number of entries: ${entries.size}")

        return entries
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.entry_menu, menu)
        Log.d("ENTRY_DEBUG", "onCreateOptionsMenu: Menu created")
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        Log.d("ENTRY_DEBUG", "onOptionsItemSelected: Menu item selected ${item.itemId}")
        return onMenuItemSelected(item)
    }

    private fun onMenuItemSelected(item: MenuItem): Boolean {
        Log.d("ENTRY_DEBUG", "onMenuItemSelected: Handling menu item ${item.itemId}")
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
                startActivity(Intent(this, TimeEntry::class.java))
                true
            }
            R.id.menu_item5 -> {
                startActivity(Intent(this, Graphs::class.java))
                true
            }
            R.id.menu_item6 -> {
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
