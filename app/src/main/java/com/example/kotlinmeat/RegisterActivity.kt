package com.example.kotlinmeat

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.kotlinmeat.databinding.ActivityMainBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.PropertyName

class User{

     private lateinit var Name: String
    private lateinit var Id: String
    private lateinit var Email: String
     //lateinit var Birthdate: String
     private lateinit var CurrentLocation: HashMap<String,Double>
    private lateinit var Surename: String
    private  lateinit var ImageLink: String
    private  lateinit var Info: String
    private lateinit var Nickname: String
    private lateinit var Phone: String
    @PropertyName("Name")
    public fun getName(): String
    {
        return Name;
    }
    @PropertyName("Name")
    public fun setName(name: String)
    {
        Name = name
    }
    @PropertyName("Id")
    public fun getId(): String
    {
        return Id;
    }
    @PropertyName("Id")
    public fun setId(id: String)
    {
        Id = id
    }
    @PropertyName("Email")
    public fun getEmail(): String
    {
        return Email;
    }
    @PropertyName("Email")
    public fun setEmail(email: String)
    {
        Email = email
    }
    @PropertyName("CurrentLocation")
    public fun getCurrentLocation(): HashMap<String,Double>
    {
        return CurrentLocation;
    }
    @PropertyName("CurrentLocation")
    public fun setCurrentLocation(currentlocation: HashMap<String,Double>)
    {
        CurrentLocation = currentlocation
    }
    @PropertyName("Surename")
    public fun getSurename(): String
    {
        return Surename
    }
    @PropertyName("Surename")
    public fun setSurename(surename: String)
    {
        Surename = surename
    }
    @PropertyName("ImageLink")
    public fun getImageLink(): String
    {
        return ImageLink;
    }
    @PropertyName("ImageLink")
    public fun setImageLink(imagelink: String)
    {
        ImageLink = imagelink
    }
    @PropertyName("Info")
    public fun getInfo(): String
    {
        return Info;
    }
    @PropertyName("Info")
    public fun setInfo(info: String)
    {
        Info = info
    }
    @PropertyName("Nickname")
    public fun getNickname(): String
    {
        return Nickname;
    }
    @PropertyName("Nickname")
    public fun setNickname(nickname: String)
    {
        Nickname = nickname
    }
    @PropertyName("Phone")
    public fun getPhone(): String
    {
        return Phone;
    }
    @PropertyName("Phone")
    public fun setPhone(phone: String)
    {
        Phone = phone
    }
}

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
            val nickname = binding.usernameReg.text.toString()
            val email = binding.emailReg.text.toString()
            val password  = binding.passwordReg.text.toString()
            if(email.isEmpty() || password.isEmpty() || nickname.isEmpty()) {
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
                    val intent = Intent(this@RegisterActivity, NewuserActivity::class.java)
                    intent.putExtra("Email",email)
                    intent.putExtra("Password",password)
                    intent.putExtra("id",it.result?.user?.uid)
                    intent.putExtra("nickname", nickname)
                    startActivity(intent)
                }
                .addOnFailureListener {
                    Log.d("Main","Failed to create user: ${it.message}")
                    Toast.makeText(this,"Failed to create user: ${it.message}",Toast.LENGTH_SHORT).show()
                }
        }


}

