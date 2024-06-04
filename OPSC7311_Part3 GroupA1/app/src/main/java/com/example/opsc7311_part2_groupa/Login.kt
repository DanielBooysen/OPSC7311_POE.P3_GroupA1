package com.example.opsc7311_part2_groupa

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.opsc7311_part2_groupa.databinding.ActivityLoginBinding



class Login : AppCompatActivity() {
    private lateinit var bind : ActivityLoginBinding
    @SuppressLint("Range")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        bind= ActivityLoginBinding.inflate(layoutInflater)
        setContentView(bind.root)
        val dbhelp=DBClass(applicationContext)
        val db=dbhelp.readableDatabase
        bind.login.setOnClickListener{
            val email = bind.email.text.toString()
            val password = bind.editTextTextPassword.text.toString()
            val query= "SELECT * FROM user WHERE email='$email' AND password='$password'"
            val rs=db.rawQuery(query,null)
            if(rs.moveToFirst()){
                val email = rs.getString(rs.getColumnIndex("email"))
                rs.close()
                startActivity(Intent(this,Homepage::class.java).putExtra("email", email))
            }else{
                var ad = AlertDialog.Builder(this)
                ad.setTitle("Message")
                ad.setMessage("Email or password is incorrect!")
                ad.setPositiveButton("Ok", null)
                ad.show()
            }
        }
        bind.regisLink.setOnClickListener {
            val intent = Intent(this, Main::class.java)
            startActivity(intent)
        }
    }
}