package com.example.opsc7311_part2_groupa

import android.content.Intent
import android.database.sqlite.SQLiteDatabase
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.widget.ImageView
import android.widget.PopupMenu
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.example.opsc7311_part2_groupa.databinding.ActivityHomepageBinding

class Homepage : AppCompatActivity() {
    private lateinit var bind: ActivityHomepageBinding
    private lateinit var db: SQLiteDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        bind = ActivityHomepageBinding.inflate(layoutInflater)
        setContentView(bind.root)

        // Move database initialization to a background thread
        Thread {
            db = DBClass(applicationContext).readableDatabase
            runOnUiThread {
                setupUI()
            }
        }.start()
    }

    private fun setupUI() {
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        val query = "SELECT email FROM user_logged"
        val value = db.rawQuery(query, null)
        if (value.moveToFirst()) {
            bind.username.setText(value.getString(0))
        }
        value.close()

        bind.logout.setOnClickListener {
            db.execSQL("DROP TABLE IF EXISTS user_logged")
            db.execSQL("CREATE TABLE user_logged (email TEXT PRIMARY KEY)")
            startActivity(Intent(this, Login::class.java))
        }

        val logo: ImageView = findViewById(R.id.logo)
        logo.setOnClickListener { view -> showPopupMenu(view) }
    }

    private fun showPopupMenu(view: View) {
        val popupMenu = PopupMenu(this, view)
        val inflater: MenuInflater = popupMenu.menuInflater
        inflater.inflate(R.menu.main_menu, popupMenu.menu)
        popupMenu.setOnMenuItemClickListener { item ->
            onMenuItemSelected(item)
        }
        popupMenu.show()
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
                startActivity(Intent(this, TotalHours::class.java))
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

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return onMenuItemSelected(item)
    }
}
