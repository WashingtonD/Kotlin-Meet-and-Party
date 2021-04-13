package com.example.kotlinmeat

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.example.kotlinmeat.databinding.ActivityMainScreenBinding

class MainScreenActivity: AppCompatActivity() {
    lateinit var binding: ActivityMainScreenBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val swipeRefreshLayout = findViewById<SwipeRefreshLayout>(R.id.refreshLayout)
        val textView = findViewById<TextView>(R.id.tv1)

        // Refresh function for the layout
        swipeRefreshLayout.setOnRefreshListener{

            textView.text = "Refreshed"

            swipeRefreshLayout.isRefreshing = false
        }

        val button = findViewById<Button>(R.id.button_profile)

        button.setOnClickListener {
            val intent = Intent(this, ProfileActivity::class.java)
            startActivity(intent)
        }

    }
}
