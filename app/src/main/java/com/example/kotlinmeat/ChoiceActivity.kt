package com.example.kotlinmeat

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.kotlinmeat.databinding.ActivityChoiceBinding


class ChoiceActivity : AppCompatActivity() {

    lateinit var binding: ActivityChoiceBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityChoiceBinding.inflate(layoutInflater)
        setContentView(binding.root)


        val button = findViewById<Button>(R.id.button_register)
        val button2 = findViewById<Button>(R.id.button_login)
        val button3 = findViewById<Button>(R.id.button_test)
        button.setOnClickListener{
            val intent = Intent(this,RegisterActivity::class.java)
            startActivity(intent)
        }
        button2.setOnClickListener{
            val intent = Intent(this,LoginActivity::class.java)
            startActivity(intent)
        }
        button3.setOnClickListener{
            val intent = Intent(this,MainScreenActivity::class.java)
            startActivity(intent)
        }
        Toast.makeText(this, "Welcome", Toast.LENGTH_SHORT).show()

    }
}
