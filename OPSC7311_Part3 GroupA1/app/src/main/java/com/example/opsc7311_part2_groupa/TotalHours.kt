package com.example.opsc7311_part2_groupa


import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.LinearLayout
import android.widget.ListView
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class TotalHours : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_total_hours)
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        ViewCompat.setOnApplyWindowInsetsListener(window.decorView.rootView) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        // Get start and end dates from intent or wherever they're set
        val startDate = intent.getStringExtra("START_DATE")
        val endDate = intent.getStringExtra("END_DATE")

        // Fetch total hours data from the database
        val totalHoursByCategory = DBClass(this).getTotalHoursByCategory(startDate, endDate)

        // Get the LinearLayout to dynamically add TextViews
        val layout = findViewById<LinearLayout>(R.id.linearLayout)

        // Loop through the retrieved data and dynamically add TextViews for each category
        for ((category, totalHours) in totalHoursByCategory) {
            val textView = TextView(this)
            textView.layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
            textView.text = "$category: $totalHours hours"
            layout.addView(textView)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.total_hoursmenu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menu_item13 -> {
                startActivity(Intent(this, TimeEntry::class.java))
                return true
            }

            R.id.menu_item14 -> {
                startActivity(Intent(this, Goal::class.java))
                return true
            }

            R.id.menu_item15 -> {
                startActivity(Intent(this, Homepage::class.java))
                return true
            }

            R.id.menu_item16 -> {
                startActivity(Intent(this, ListView::class.java))
                return true
            }

            else -> return super.onOptionsItemSelected(item)
        }
    }
}