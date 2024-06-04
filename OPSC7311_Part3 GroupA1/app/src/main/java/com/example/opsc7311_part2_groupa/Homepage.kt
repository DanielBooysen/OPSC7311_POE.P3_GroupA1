package com.example.opsc7311_part2_groupa

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import androidx.appcompat.app.AppCompatActivity
import com.example.opsc7311_part2_groupa.databinding.ActivityHomepageBinding


class Homepage : AppCompatActivity() {
    private lateinit var bind: ActivityHomepageBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        bind= ActivityHomepageBinding.inflate(layoutInflater)
        setContentView(bind.root)

        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        val value=intent.getStringExtra("username")
        bind.username.text=value

        bind.logout.setOnClickListener {
            startActivity(Intent(this,Login::class.java))
        }
    }
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return true
    }
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menu_item1 -> {
                startActivity(Intent(this, Time_entry::class.java))
                return true
            }
            R.id.menu_item2 -> {
                startActivity(Intent(this, Goal::class.java))
                return true
            }
            R.id.menu_item3 -> {
                startActivity(Intent(this, Login::class.java))
                return true
            }

        }
        return super.onOptionsItemSelected(item)
    }
}