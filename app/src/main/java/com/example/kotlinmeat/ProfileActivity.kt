package com.example.kotlinmeat

import android.content.Intent
import android.os.Bundle
import android.text.Html
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

    lateinit var name: String
    lateinit var surname: String
    lateinit var description: String
    lateinit var nickname: String
    lateinit var phone: String



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val imageadres = intent.getStringExtra("image")
        Log.d("ProfileActivity","ImageLink: $imageadres")
        Picasso.with(this).load(imageadres).fit().centerInside().into(binding.profilePhoto)
        binding.textViewName.text = Html.fromHtml("Name: "+ "<b>" + intent.getStringExtra("name")+ "</b>")
        binding.textViewSurname.text = Html.fromHtml("Surname: "+ "<b>" + intent.getStringExtra("surname")+ "</b>")
        binding.textViewDescription.text = Html.fromHtml("Info: "+ "<b>" + intent.getStringExtra("description")+ "</b>")
        binding.textViewNickname.text = Html.fromHtml("Nickname: "+ "<b>" + intent.getStringExtra("nickname")+ "</b>")
        binding.textViewPhone.text = Html.fromHtml("Phone: " + "<b>" + intent.getStringExtra("phone")+ "</b>")
        name = intent.getStringExtra("name")!!
        surname  = intent.getStringExtra("surname")!!
        description = intent.getStringExtra("description")!!
        nickname = intent.getStringExtra("nickname")!!
        phone = intent.getStringExtra("phone")!!



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
                binding.textViewName.text =       Html.fromHtml("Name: "+ "<b>" + list.elementAt(0) + "</b>")
                binding.textViewSurname.text =    Html.fromHtml("Surname: "+ "<b>" + list.elementAt(3)+ "</b>")
                binding.textViewDescription.text =Html.fromHtml("Info: " + "<b>" + list.elementAt(5) + "</b>")
                binding.textViewNickname.text =   Html.fromHtml("Nickname: " + "<b>" + list.elementAt(6)+ "</b>")
                binding.textViewPhone.text =      Html.fromHtml("Phone: " +  "<b>" +list.elementAt(4)+ "</b>")
                name = list.elementAt(0)
                surname = list.elementAt(3)
                description = list.elementAt(5)
                nickname = list.elementAt(6)
                phone = list.elementAt(4)
                Picasso.with(this@ProfileActivity).load(list.elementAt(2)).fit().centerInside().into(binding.profilePhoto)
            }
        })

    }
}
