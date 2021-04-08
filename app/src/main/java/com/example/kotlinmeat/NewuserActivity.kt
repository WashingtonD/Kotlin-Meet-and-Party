package com.example.kotlinmeat

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.example.kotlinmeat.databinding.ActivityNewuserBinding
import com.squareup.picasso.Picasso


class NewuserActivity: AppCompatActivity() {

    lateinit var binding: ActivityNewuserBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNewuserBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Setting first picture
        binding.photoSelectionButtonNewuser.setOnClickListener {
            Log.d("NewUA","Trying to set up new photo")

            val intent = Intent(Intent.ACTION_PICK)
            intent.type = "image/*"
            startActivityForResult(intent,0)
        }//Another attempt to change photo
        binding.profileImage.setOnClickListener{
            Log.d("NewUA","Another attempt to change pic")

            val intent = Intent(Intent.ACTION_PICK)
            intent.type = "image/*"
            startActivityForResult(intent,2)
        }



    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode == 0 && resultCode == Activity.RESULT_OK && data != null)
        {
            Log.d("NewUA","Photo was selected")
            binding.photoSelectionButtonNewuser.visibility = View.GONE
            Picasso.with(this).load(data.data).fit().centerInside().into(binding.profileImage)
        }
    }
}

