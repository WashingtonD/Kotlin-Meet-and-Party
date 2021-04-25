package com.example.kotlinmeat

import android.content.Intent
import android.graphics.Point
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.net.toUri
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
import com.mikepenz.materialdrawer.model.interfaces.IProfile
import com.mikepenz.materialdrawer.util.AbstractDrawerImageLoader
import com.mikepenz.materialdrawer.util.DrawerImageLoader
import com.squareup.picasso.Picasso


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

        //map


        user.birthdate = ""
        user.email = ""
        user.currentLocation = Point(1,0)
        user.id = FirebaseAuth.getInstance().uid.toString()
        user.imageLink = ""
        user.name = ""
        user.surename = ""
        user.nickname = ""
        initUser()
        Log.d("AfterInit","Username: ${user.name}, Link: ${user.imageLink}")

        DrawerImageLoader.init(object : AbstractDrawerImageLoader() {
            override fun set(imageView: ImageView, uri: Uri, placeholder: Drawable) {
                Picasso.with(imageView.context).load(uri).placeholder(placeholder).into(imageView)
            }

            override fun cancel(imageView: ImageView) {
                Picasso.with(imageView.context).cancelRequest(imageView)
            }

            /*
            override fun set(imageView: ImageView, uri: Uri, placeholder: Drawable, tag: String?) {
                super.set(imageView, uri, placeholder, tag)
            }

            override fun placeholder(ctx: Context): Drawable {
                return super.placeholder(ctx)
            }

            override fun placeholder(ctx: Context, tag: String?): Drawable {
                return super.placeholder(ctx, tag)
            }
            */
        })












        initfields()
        initFunc()
/*        val swipeRefreshLayout = findViewById<SwipeRefreshLayout>(R.id.refreshLayout)
        val textView = findViewById<TextView>(R.id.tv1)

        // Refresh function for the layout
        swipeRefreshLayout.setOnRefreshListener{

            textView.text = "Refreshed"

            swipeRefreshLayout.isRefreshing = false
        }*/
        /* val button = findViewById<Button>(R.id.button_profile)

         button.setOnClickListener {
             val intent = Intent(this, ProfileActivity::class.java)
             startActivity(intent)
         }*/


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

                    //Map drawer
                        PrimaryDrawerItem().withIdentifier(300)
                            .withIconTintingEnabled(true)
                            .withName("Map")
                            .withSelectable(false)
                            .withOnDrawerItemClickListener(object: Drawer.OnDrawerItemClickListener{
                                override fun onItemClick(
                                    view: View?,
                                    position: Int,
                                    drawerItem: IDrawerItem<*>
                                ): Boolean {
                                    val intent = Intent(this@MainScreenActivity,DisplayMapActivity::class.java)
//                                    intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
                                    startActivity(intent)
                                    return false
                                }
                            } ),
                    //Logout drawer
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

    private fun initUser()
    {
        /*val uid = FirebaseAuth.getInstance().uid
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
                })*/
    }



    private fun createHeader() {
        var name: String = ""
        update_profile()

        val intent = Intent(this,ProfileActivity::class.java)
        mHeader = AccountHeaderBuilder()
                .withActivity(this)
                .withHeaderBackground(R.drawable.header)
                .withOnAccountHeaderProfileImageListener(object: AccountHeader.OnAccountHeaderProfileImageListener{
                    override fun onProfileImageClick(view: View, profile: IProfile<*>, current: Boolean): Boolean {
                        intent.putExtra("image",user.imageLink)
                        intent.putExtra("nickname", user.nickname)
                        intent.putExtra("name",user.name)
                        intent.putExtra("surname",user.surename)
                        intent.putExtra("description",user.info)
                        intent.putExtra("phone",user.phone)
                        //intent.putExtra("")
                      // var profilePic = user.imageLink
                        startActivity(intent)
                        update_profile()
                        return false
                    }

                    override fun onProfileImageLongClick(view: View, profile: IProfile<*>, current: Boolean): Boolean {
                      return false
                    }
                }).addProfiles(
                        ProfileDrawerItem()
                                .withIdentifier(228)
                                 .withName(name)
                                .withEmail(user.email)
                                //.withIcon(user.imageLink)
         ).build()
    }

    private fun initfields() {
        mToolbar = binding.mainToolbar
    }

interface FirebaseCallBack{
    fun onCallBack(list: MutableList<String>)
}

fun readFirebaseData(firebaseCallBack: FirebaseCallBack)
{
    val uid = FirebaseAuth.getInstance().uid
    val ref = FirebaseDatabase.getInstance().getReference("users/$uid")
    ref.addListenerForSingleValueEvent(object: ValueEventListener {
        override fun onCancelled(error: DatabaseError) {
            Log.d("Autho","Something went wrong")
        }
        override fun onDataChange(snapshot: DataSnapshot) {
         val list = ArrayList<String>()
            /*for(ds in snapshot.children)
            {
                val user = ds.getValue(User::class.java)
                list.add(user!!)
            }*/
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
            Log.d("Autho","List: $list")
           // val lii = listOf(user,email,image)
            //Log.d("Autho","Lii: ${lii.elementAt(0)},${lii.elementAt(1)},${lii.elementAt(2)}")
            firebaseCallBack.onCallBack(list)
        }
    })
}

private fun update_profile()
{
    val uid = FirebaseAuth.getInstance().uid
    //Log.d("Header","Name: $name, Link: ${user.imageLink}")
    val ref = FirebaseDatabase.getInstance().getReference("/users/$uid")
    readFirebaseData(object: FirebaseCallBack{
        override fun onCallBack(list: MutableList<String>) {
            // for(ds in list)
            // {
            user.name = list.elementAt(0)
            user.email = list.elementAt(1)
            user.imageLink = list.elementAt(2)
            user.surename = list.elementAt(3)
            user.phone = list.elementAt(4)
            user.info = list.elementAt(5)
            user.nickname = list.elementAt(6)
            Log.d("Autho","User.name = ${user.name}")
            // }
            mHeader.removeProfileByIdentifier(228)
            val prof = ProfileDrawerItem().withName(user.name)
                    .withEmail(user.email)
                    .withIdentifier(228)
                    .withIcon(user.imageLink.toUri())
            mHeader.addProfiles(prof)
        }
    })
}



}

