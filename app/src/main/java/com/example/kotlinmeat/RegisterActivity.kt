 package com.example.kotlinmeat

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.kotlinmeat.databinding.ActivityMainBinding
import com.google.firebase.auth.FirebaseAuth
import java.util.*


 class RegisterActivity : AppCompatActivity() {

    lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.passValidText.text = ""


        binding.registerButton.setOnClickListener{
            performreg()
        }

        binding.alreadyAccountTextview.setOnClickListener{
            Log.d("MainActivity1","Try to show login activity")

            // launch login_activity
            val intent = Intent(this,LoginActivity::class.java)
            startActivity(intent)
        }


    }

        @SuppressLint("SetTextI18n")
        private fun performreg()
        {
            val email = binding.emailReg.text.toString()
            val password  = binding.passwordReg.text.toString()
            val id = UUID.randomUUID().toString().replace("-","")
            binding.passValidText.text = id
            if(email.isEmpty() || password.isEmpty()) {
                val toas = Toast.makeText(this,"Please enter valid information in email/pw",Toast.LENGTH_SHORT).show()
                return
            }
            if(!password.matches("(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])[0-9a-zA-Z!@#\$%^&*]{8,}".toRegex()))
            {
                    Log.d("Main","Invalid password try")
                    binding.passValidText.text = "Your password must contain uppercase letter, lowercase letter, digit and be at least 8 symbols long"
                    return
            }

            Log.d( "MainActivity","Email is: $email")
            Log.d("MainActivity","Password: $password")
            FirebaseAuth.getInstance().createUserWithEmailAndPassword(email,password)
                .addOnCompleteListener {
                    if(!it.isSuccessful)  return@addOnCompleteListener
                    // else for successful reg
                    Log.d("Main","Successfuly created user with uid: ${it.result?.user?.uid}")


                }
                .addOnFailureListener {
                    Log.d("Main","Failed to create user: ${it.message}")
                    Toast.makeText(this,"Failed to create user: ${it.message}",Toast.LENGTH_SHORT).show()
                }
        }


      private fun UploadProfileImage()
      {

      }


}

