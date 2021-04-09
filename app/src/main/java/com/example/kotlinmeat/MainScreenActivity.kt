package com.example.kotlinmeat

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.kotlinmeat.databinding.ActivityMainScreenBinding

class MainScreenActivity: AppCompatActivity() {
    lateinit var binding: ActivityMainScreenBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }
}

