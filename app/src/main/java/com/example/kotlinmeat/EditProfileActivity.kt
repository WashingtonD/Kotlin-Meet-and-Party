package com.example.kotlinmeat

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.example.kotlinmeat.databinding.ActivityProfileEditBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.squareup.picasso.Picasso

class EditProfileActivity: AppCompatActivity() {

    private lateinit var binding: ActivityProfileEditBinding
    private var photoCheck: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfileEditBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val adress = intent.getStringExtra("image")
        Picasso.with(this).load(adress).fit().centerInside().into(binding.profilePhoto2)
        binding.NickNameEditProfile.setText(intent.getStringExtra("nickname"))
        binding.SureNameEditProfile.setText(intent.getStringExtra("surname"))
        binding.PhoneNumberEditProfile.setText(intent.getStringExtra("phone"))
        binding.editTextTextPersonNameProfile.setText(intent.getStringExtra("name"))
        binding.DescriptionEditProfile.setText(intent.getStringExtra("description"))

        var name = intent.getStringExtra("name")
        var surname = intent.getStringExtra("surname")
        var phone = intent.getStringExtra("phone")
        var description  = intent.getStringExtra("description")
        var nickname = intent.getStringExtra("nickname")


        val button = findViewById<Button>(R.id.button_save)
        val button2 = findViewById<Button>(R.id.button_edit_back)
        button.setOnClickListener{
            var user = User()
            val id = FirebaseAuth.getInstance().uid
            val ref = FirebaseDatabase.getInstance().getReference("users/$id")
            if(binding.editTextTextPersonNameProfile.text.toString() != "" || binding.editTextTextPersonNameProfile.text.toString() != name)
            {
                ref.child("name").setValue(binding.editTextTextPersonNameProfile.text.toString())
            }
            if(binding.NickNameEditProfile.text.toString() != "" && binding.NickNameEditProfile.text.toString() != nickname)
            {
                ref.child("nickname").setValue(binding.NickNameEditProfile.text.toString())
            }
            if(binding.PhoneNumberEditProfile.text.toString() != "" && binding.PhoneNumberEditProfile.text.toString() != phone)
            {
                ref.child("phone").setValue(binding.PhoneNumberEditProfile.text.toString())
            }
            if(binding.SureNameEditProfile.text.toString() != "" && binding.SureNameEditProfile.text.toString() != surname)
            {
                ref.child("surename").setValue(binding.SureNameEditProfile.text.toString())
            }
            if(binding.DescriptionEditProfile.text.toString() != "" && binding.DescriptionEditProfile.text.toString() != description)
            {
                ref.child("info").setValue(binding.DescriptionEditProfile.text.toString())
            }
            if(photoCheck) {
                uploadPhoto()
            }
            //val imageadres = intent.getStringExtra("image")
            finish()
        }
        button2.setOnClickListener{
            finish()
        }

        binding.profilePhoto2.setOnClickListener{
            Log.d("EditProfile","Trying to change photo")
            val intent = Intent(Intent.ACTION_PICK)
            intent.type = "image/*"
            startActivityForResult(intent,1)

        }
    }

    var selectedPhotoUri: Uri? = null

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode == 1 && resultCode == Activity.RESULT_OK && data != null)
        {
            selectedPhotoUri = data.data
            Log.d("EditUser","Photo was selectd")
            Picasso.with(this).load(data.data).fit().centerInside().into(binding.profilePhoto2)
            photoCheck = true
        }
    }

    fun uploadPhoto()
    {
        if(selectedPhotoUri == null)
        {
            Log.d("EditProfile","Photo URI is empty")
            return
        }
        val id = FirebaseAuth.getInstance().uid
        val ref = FirebaseStorage.getInstance().getReference("images/$id")
        ref.delete().addOnSuccessListener {
            Log.d("EditProfile","Image was successfully deleted")
        }
                .addOnFailureListener{
                    Log.d("EditProfile","Image was not deleted")
                    return@addOnFailureListener
                }
        ref.putFile(selectedPhotoUri!!)
                .addOnSuccessListener {
                    Log.d("EditUser","Photo ${it.metadata?.path} was successfully uploaded")
                }

                .addOnFailureListener {
                    Log.d("EditUser","Failed to upload image")
                }
    }
}