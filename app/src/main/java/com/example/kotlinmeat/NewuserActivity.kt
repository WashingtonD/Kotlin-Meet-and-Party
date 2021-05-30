package com.example.kotlinmeat

import android.app.Activity
import android.content.Intent
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
            u.setEmail(intent.getStringExtra("Email")!!)
            u.setSurname("")
            //u.Birthdate = ""
            u.setCurrentLocation(ArrayList<Double>())
            u.setId(intent.getStringExtra("id")!!)
            u.setName(binding.editTextTextPersonName.text.toString())
            Log.d("NewUA","User: ${u.getEmail()} and with id: ${u.getId()} with name ${u.getName()} was trying to register")
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
            user.setEmail(intent.getStringExtra("Email")!!)
            user.setSurname(binding.editTextSurname.text.toString())
            //user.Birthdate = ""
            user.setCurrentLocation(ArrayList<Double>())
            (user.getCurrentLocation()).add(1.54)
            user.getCurrentLocation().add(3.32)
            user.setId(intent.getStringExtra("id")!!)
            user.setName(binding.editTextTextPersonName.text.toString())
            user.setImageLink(profileImageUrl)
            user.setNickname(intent.getStringExtra("nickname")!!)
            user.setPhone(binding.editTextPhone.text.toString())
            user.setInfo(binding.editTextDescription.text.toString())
            Log.d("NewUA","${user.getEmail()} || ${user.getSurname()} || ${user.getNickname()}"+ /*|| ${user.Birthdate}*/ "|| ${user.getName()} ||${user.getId()} ||  ${user.getImageLink()} ")
            ref.setValue(user)
                    .addOnSuccessListener {
                        Log.d("NewUA","User saved to Firebase database")
                        val intent = Intent(this,MainScreenActivity::class.java)
                        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
                        intent.putExtra("Image_Link",user.getImageLink())
                        intent.putExtra("email",user.getEmail())
                        intent.putExtra("nickname",user.getNickname())
                        intent.putExtra("id",user.getId())
                        intent.putExtra("surname",user.getSurname())
                        intent.putExtra("phone",user.getPhone())
                        intent.putExtra("description",user.getInfo())
                        startActivity(intent)
                    }
        }

    }

