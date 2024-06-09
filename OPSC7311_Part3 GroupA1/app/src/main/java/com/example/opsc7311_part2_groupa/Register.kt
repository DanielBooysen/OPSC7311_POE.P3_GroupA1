package com.example.opsc7311_part2_groupa

import android.content.ContentValues
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.opsc7311_part2_groupa.databinding.ActivityMainBinding

class Register : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Log that the activity has started
        Log.d("REGISTER_DEBUG", "onCreate: Activity started")

        val dbhelp = DBClass(applicationContext)
        val db = dbhelp.writableDatabase

        binding.rRegister.setOnClickListener {
            Log.d("REGISTER_DEBUG", "onClick: Register button clicked")

            val username = binding.username.text.toString()
            val email = binding.email.text.toString()
            val password = binding.Password.text.toString()

            if (username.isNotEmpty() && email.isNotEmpty() && password.isNotEmpty()) {
                Log.d("REGISTER_DEBUG", "All fields are filled")

                val cursor = db.rawQuery("SELECT * FROM user WHERE email = ?", arrayOf(email))
                if (cursor.moveToFirst()) {
                    Log.d("REGISTER_DEBUG", "Email already registered")
                    val ad = AlertDialog.Builder(this)
                    ad.setTitle("Message")
                    ad.setMessage("An error occurred: Email already registered")
                    ad.setPositiveButton("Ok", null)
                    ad.show()
                } else {
                    Log.d("REGISTER_DEBUG", "Email not registered")

                    val data = ContentValues().apply {
                        put("username", username)
                        put("email", email)
                        put("password", password)
                    }
                    val rs: Long = db.insert("user", null, data)
                    if (rs != -1L) {
                        Log.d("REGISTER_DEBUG", "User registered successfully")

                        val query1 = "INSERT INTO user_logged (email) VALUES(?)"
                        db.execSQL(query1, arrayOf(email))
                        db.close()
                        val intent = Intent(this, Homepage::class.java)
                        startActivity(intent)
                    } else {
                        Log.d("REGISTER_DEBUG", "Failed to register user")

                        val ad = AlertDialog.Builder(this)
                        ad.setTitle("Message")
                        ad.setMessage("An error occurred: User not registered")
                        ad.setPositiveButton("Ok", null)
                        ad.show()
                    }
                }
                cursor.close()
            } else {
                Log.d("REGISTER_DEBUG", "Not all fields are filled")
                Toast.makeText(this, "All fields are required", Toast.LENGTH_SHORT).show()
            }
        }

        binding.login.setOnClickListener {
            Log.d("REGISTER_DEBUG", "onClick: Login button clicked")
            val intent = Intent(this, Login::class.java)
            startActivity(intent)
        }
    }
}
