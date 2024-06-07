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
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class Entries : AppCompatActivity() {
    val db = DBClass(applicationContext).readableDatabase
    @SuppressLint("MissingInflatedId")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_entries)
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        val entries = getTimesheetEntriesFromDatabase()

        val listView: ListView = findViewById(R.id.entries_list_view)
        val adapter = EntryAdapter(this, entries)
        listView.adapter = adapter
    }

    @SuppressLint("Range")
    private fun getTimesheetEntriesFromDatabase(): List<TimeSheetEntry> {
        val entries = mutableListOf<TimeSheetEntry>()

        val db = DBClass(applicationContext).readableDatabase
        val query1 = ("SELECT email FROM user_logged")
        val userCursor = db.rawQuery(query1, null)
        var email: String = ""
        if(userCursor.moveToFirst()){
            val index = userCursor.getColumnIndex("email")
            email = userCursor.getString(index)
        }
        userCursor.close()
        val query = "SELECT * FROM entries WHERE email = '$email'"
        db.rawQuery(query, null).use { cursor ->
            while (cursor.moveToNext()) {
                val time = cursor.getString(cursor.getColumnIndex(DBClass.TIME_ENTRY))
                val category = cursor.getString(cursor.getColumnIndex(DBClass.CATEGORY_ENTRY))
                val description = cursor.getString(cursor.getColumnIndex(DBClass.DESCRIPTION_ENTRY))
                val date = cursor.getString(cursor.getColumnIndex(DBClass.DATE_ENTRY))

                val entry = TimeSheetEntry(time, category, description, date)
                entries.add(entry)
            }
        }
        db.close()

        // Log the number of retrieved entries
        Log.d("ENTRY_DEBUG", "Number of entries: ${entries.size}")

        return entries
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.entry_menu, menu)
        return true
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
                startActivity(Intent(this, TimeEntry::class.java))
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