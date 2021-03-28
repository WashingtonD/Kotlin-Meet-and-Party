package com.example.kotlinmeat

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.kotlinmeat.databinding.ActivityNewuserBinding

class NewuserActivity: AppCompatActivity() {

    lateinit var binding: ActivityNewuserBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNewuserBinding.inflate(layoutInflater)
        setContentView(binding.root)



    }
}