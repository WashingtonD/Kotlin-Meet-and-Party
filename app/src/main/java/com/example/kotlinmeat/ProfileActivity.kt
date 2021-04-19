package com.example.kotlinmeat

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.core.net.toUri
import com.example.kotlinmeat.databinding.ActivityProfileBinding
import com.squareup.picasso.Picasso

class ProfileActivity: AppCompatActivity() {

    lateinit var binding: ActivityProfileBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        Picasso.with(this).load(intent.getStringExtra("image")?.toUri()).fit().centerInside().into(binding.imageView)
        val button = findViewById<Button>(R.id.button_logout)
        val button2 = findViewById<Button>(R.id.button_edit)
        val button3 = findViewById<Button>(R.id.button_profile_back)
        button.setOnClickListener{
            val intent = Intent(this,RegisterActivity::class.java)
            startActivity(intent)
        }
        button2.setOnClickListener{
            val intent = Intent(this,EditProfileActivity::class.java)
            startActivity(intent)
        }
        button3.setOnClickListener{
            val intent = Intent(this,MainScreenActivity::class.java)
            startActivity(intent)

        }
    }

    /*interface  FirebaseCallback{
        fun OnCallback(list: MutableList<String>)
    }

    private fun readFriebaseData(firebaseCallBack: FirebaseCallback)
    {
        val uid = FirebaseAuth.getInstance().uid
        val ref = FirebaseDatabase.getInstance().getReference("users/$uid")
        ref.addListenerForSingleValueEvent(object: ValueEventListener{
            override fun onCancelled(error: DatabaseError) {

            }

            override fun onDataChange(snapshot: DataSnapshot) {
                val list = ArrayList<String>()
                val image = snapshot.child("imageLink")
            }
        })*/
}
