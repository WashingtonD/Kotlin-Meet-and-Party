package com.example.kotlinmeat

import android.content.Intent
import android.graphics.Point
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.example.kotlinmeat.databinding.ActivityMainScreenBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.mikepenz.materialdrawer.AccountHeader
import com.mikepenz.materialdrawer.AccountHeaderBuilder
import com.mikepenz.materialdrawer.Drawer
import com.mikepenz.materialdrawer.DrawerBuilder
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem
import com.mikepenz.materialdrawer.model.ProfileDrawerItem
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem


class MainScreenActivity: AppCompatActivity() {
    lateinit var binding: ActivityMainScreenBinding
    lateinit var mDrawer:Drawer
    lateinit var mHeader:AccountHeader
    lateinit var mToolbar:Toolbar


    var user = User()



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)

        user.birthdate = ""
        user.email = ""
        user.currentLocation = Point(1,0)
        user.id = FirebaseAuth.getInstance().uid.toString()
        user.imageLink = ""
        user.name = ""
        user.surename = ""
        initUser()


        initfields()
        initFunc()
        val swipeRefreshLayout = findViewById<SwipeRefreshLayout>(R.id.refreshLayout)
        val textView = findViewById<TextView>(R.id.tv1)

        // Refresh function for the layout
        swipeRefreshLayout.setOnRefreshListener{

            textView.text = "Refreshed"

            swipeRefreshLayout.isRefreshing = false
        }

        val button = findViewById<Button>(R.id.button_profile)

        button.setOnClickListener {
            val intent = Intent(this, ProfileActivity::class.java)
            startActivity(intent)
        }


    }

    private fun initFunc() {
        setSupportActionBar(mToolbar)
        createHeader()
        createDrawer()
    }

    private fun createDrawer() {
        mDrawer = DrawerBuilder()
                .withActivity(this)
                .withToolbar(mToolbar)
                .withActionBarDrawerToggle(true)
                .withSelectedItem(-1)
                .withAccountHeader(mHeader)
                .addDrawerItems(
                         PrimaryDrawerItem().withIdentifier(100)
                                 .withIconTintingEnabled(true)
                                 .withName("My messages")
                                 .withSelectable(false),
                        PrimaryDrawerItem().withIdentifier(200)
                            .withIconTintingEnabled(true)
                            .withName("LogOut")
                            .withSelectable(false)
                            .withOnDrawerItemClickListener(object: Drawer.OnDrawerItemClickListener{
                                override fun onItemClick(
                                    view: View?,
                                    position: Int,
                                    drawerItem: IDrawerItem<*>
                                ): Boolean {
                                    FirebaseAuth.getInstance().signOut()
                                    val intent = Intent(this@MainScreenActivity,ChoiceActivity::class.java)
                                    intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
                                    startActivity(intent)
                                    return false
                                }
                            }

                )).build()
    }

    private fun createHeader() {
        val uid = FirebaseAuth.getInstance().uid

        val ref = FirebaseDatabase.getInstance().getReference("/users/$uid")
        mHeader = AccountHeaderBuilder()
                .withActivity(this)
                .withHeaderBackground(R.drawable.header)
                .addProfiles(
                        ProfileDrawerItem().withName(FirebaseAuth.getInstance().uid)
                                .withEmail(user.email)
                            //.withIcon(user.imageLink)
                ).build()
    }

    private fun initfields() {
        mToolbar = binding.mainToolbar
    }

    private fun initUser()
    {
        val uid = FirebaseAuth.getInstance().uid
        val ref = FirebaseDatabase.getInstance().getReference("users/$uid/imageLink")
        //ref.child("users").child(FirebaseAuth.getInstance().uid.toString())
            .addListenerForSingleValueEvent(object : ValueEventListener{
                override fun onCancelled(error: DatabaseError) {
                    Log.d("MainActivity", "Cannot download image")
                }

                override fun onDataChange(snapshot: DataSnapshot) {
                    var pic = snapshot.getValue().toString()
                    Log.d("MainActivity", "Adress: $pic")
                    user.imageLink = pic
                }
            })
    }


}

