package com.example.kotlinmeat

import android.app.Activity
import android.content.Intent
import android.graphics.Point
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.example.kotlinmeat.databinding.ActivityNewuserBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
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
        binding.CompleteReg.setOnClickListener {
            Log.d("NewUA","Final registration attempt")
            val u = User()
            val intent = intent
            //u.passw = intent.getStringExtra("Password")!!
            u.email = intent.getStringExtra("Email")!!
            u.surename = ""
            u.birthdate = ""
            u.currentLocation = Point(1,0)
            u.id = intent.getStringExtra("id")!!
            u.name =  binding.editTextTextPersonName.text.toString()
            Log.d("NewUA","User: ${u.email} and with id: ${u.id} with name ${u.name} was trying to register")
            uploadImageToFirebase()
        }



    }

    var selectedPhotoUri: Uri? = null

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode == 0 && resultCode == Activity.RESULT_OK && data != null)
        {
            selectedPhotoUri = data.data
            Log.d("NewUA","Photo was selected")
            binding.photoSelectionButtonNewuser.visibility = View.GONE
            Picasso.with(this).load(data.data).fit().centerInside().into(binding.profileImage)
        }
        }

        private fun uploadImageToFirebase()
        {
            if(selectedPhotoUri == null) {
                Log.d("NewUA","Photo URI is Null")
                return
            }
            val filename = intent.getStringExtra("id")!!
            val ref = FirebaseStorage.getInstance().getReference("/images/$filename")
            ref.putFile(selectedPhotoUri!!)
                    .addOnSuccessListener {
                        Log.d("NewUA","Successfully uploaded images ${it.metadata?.path}")


                    //accessing to the Image location on storage
                        ref.downloadUrl.addOnSuccessListener {
                            Log.d("NewUA","File location: $it")

                            saveUserToFirebaseDatabase(it.toString())
                        }

                    }

                    .addOnFailureListener {
                        Log.d("NewUA","Failed to upload image")
                    }
        }

        private fun saveUserToFirebaseDatabase(profileImageUrl: String)
        {
            val uid = FirebaseAuth.getInstance().uid ?: ""
            val ref = FirebaseDatabase.getInstance().getReference("/users/$uid")

            val user = User()
            //u.passw = intent.getStringExtra("Password")!!
            user.email = intent.getStringExtra("Email")!!
            user.surename = ""
            user.birthdate = ""
            user.currentLocation = Point(1,0)
            user.id = intent.getStringExtra("id")!!
            user.name =  binding.editTextTextPersonName.text.toString()
            user.imageLink = profileImageUrl
            Log.d("NewUA","${user.email} || ${user.surename} || ${user.birthdate} || ${user.currentLocation} || ${user.name} ||${user.id} ||  ${user.imageLink} ")
            ref.setValue(user)
                    .addOnSuccessListener {
                        Log.d("NewUA","User saved to Firebase database")
                    }
        }

    }

