package com.example.kotlinmeat

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.example.kotlinmeat.databinding.ActivityProfileBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.squareup.picasso.Picasso

class ProfileActivity: AppCompatActivity() {

    lateinit var binding: ActivityProfileBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val imageadres = intent.getStringExtra("image")
        Log.d("ProfileActivity","ImageLink: $imageadres")
        Picasso.with(this).load(imageadres).fit().centerInside().into(binding.profilePhoto)
        binding.textViewName.text = intent.getStringExtra("name")
        binding.textViewSurname.text = intent.getStringExtra("surname")
        binding.textViewDescription.text = intent.getStringExtra("description")
        binding.textViewNickname.text = intent.getStringExtra("nickname")
        binding.textViewPhone.text = intent.getStringExtra("phone")
        var name = intent.getStringExtra("name")
        var surname  = intent.getStringExtra("surname")
        var description = intent.getStringExtra("description")
        var nickname = intent.getStringExtra("nickname")
        var phone = intent.getStringExtra("phone")



        val button = findViewById<Button>(R.id.button_logout)
        val button2 = findViewById<Button>(R.id.button_edit)
        val button3 = findViewById<Button>(R.id.button_profile_back)
        button.setOnClickListener{
            val intent = Intent(this,RegisterActivity::class.java)
            startActivity(intent)
        }
        button2.setOnClickListener{
            val intent = Intent(this,EditProfileActivity::class.java)
            intent.putExtra("image",imageadres)
            intent.putExtra("name",name)
            intent.putExtra("surname",surname)
            intent.putExtra("nickname",nickname)
            intent.putExtra("phone",phone)
            intent.putExtra("description",description)
            startActivity(intent)
            updateProfile()
        }
        button3.setOnClickListener{
            //val intent = Intent(this,MainScreenActivity::class.java)
            //startActivity(intent)
            finish()
        }
    }

    interface  FirebaseCallback{
        fun onCallback(list: MutableList<String>)
    }

    private fun readFriebaseData(firebaseCallBack: FirebaseCallback)
    {
        val uid = FirebaseAuth.getInstance().uid
        val ref = FirebaseDatabase.getInstance().getReference("users/$uid")
        ref.addListenerForSingleValueEvent(object: ValueEventListener {
            override fun onCancelled(error: DatabaseError) {

            }

            override fun onDataChange(snapshot: DataSnapshot) {
                val list = ArrayList<String>()
                val user = snapshot.child("name").getValue(String::class.java)
                list.add(user!!)
                val email = snapshot.child("email").getValue(String::class.java)
                list.add(email!!)
                val image = snapshot.child("imageLink").getValue(String::class.java)
                list.add(image!!)
                val surname = snapshot.child("surename").getValue(String::class.java)
                list.add(surname!!)
                val phone = snapshot.child("phone").getValue(String::class.java)
                list.add(phone!!)
                val description = snapshot.child("info").getValue(String::class.java)
                list.add(description!!)
                val nickname = snapshot.child("nickname").getValue(String::class.java)
                list.add(nickname!!)
                firebaseCallBack.onCallback(list)
            }
        })
    }

    private fun updateProfile()
    {
        readFriebaseData(object: FirebaseCallback{
            override fun onCallback(list: MutableList<String>){
                binding.textViewName.text = list.elementAt(0)
                binding.textViewSurname.text = list.elementAt(3)
                binding.textViewDescription.text = list.elementAt(5)
                binding.textViewNickname.text = list.elementAt(6)
                binding.textViewPhone.text = list.elementAt(4)
                Picasso.with(this@ProfileActivity).load(list.elementAt(2)).fit().centerInside().into(binding.profilePhoto)
            }
        })

    }
}
