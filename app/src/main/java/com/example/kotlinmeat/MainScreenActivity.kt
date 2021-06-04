package com.example.kotlinmeat

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.drawable.Drawable
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.View
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.net.toUri
import androidx.recyclerview.widget.DividerItemDecoration
import com.example.kotlinmeat.databinding.ActivityMainScreenBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
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
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.ViewHolder
import java.util.*
import kotlin.collections.HashMap






class MainScreenActivity: AppCompatActivity() {
    lateinit var binding: ActivityMainScreenBinding
    lateinit var mDrawer: Drawer
    lateinit var mHeader: AccountHeader
    lateinit var mToolbar: androidx.appcompat.widget.Toolbar





    companion object{
    var user = User()
}


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)
        verifyUserIsLoggedIn()
        getLocation()
        binding.recyclerviewMainScreen.adapter = adapter
        binding.recyclerviewMainScreen.addItemDecoration(DividerItemDecoration(this,DividerItemDecoration.VERTICAL))

       //Setting item click listener for message row
        adapter.setOnItemClickListener { item, view ->
            val intent = Intent(this,ChatLogActivity::class.java)

            val row = item as MainScreenMessageRow
            intent.putExtra(NewMessageActivity.USER_KEY,row.chatPartnerUser!!.getName())
            intent.putExtra("id",row.chatPartnerUser!!.getId())
            intent.putExtra("image",row.chatPartnerUser!!.getImageLink())
            startActivity(intent)
        }





        listenForLastestMessages()



        //user.Birthdate = ""
        user.setEmail("")
        user.setCurrentLocation(ArrayList<Double>())
        user.setId(FirebaseAuth.getInstance().uid.toString())
        user.setImageLink("")
        user.setName("")
        user.setSurname("")
        user.setNickname("")
        initUser()
        Log.d("AfterInit","Username: ${user.getName()}, Link: ${user.getImageLink()}")







        initloader()
        initfields()
        initFunc()
        //update_profile()
    }

    val MainScreenmessagesMap = HashMap<String,ChatMessage>()
    private fun refreshRecyclerViewMessages(){
        adapter.clear()
        MainScreenmessagesMap.values.forEach{
            adapter.add(MainScreenMessageRow(it))
        }
    }

    private fun initloader()
    {
        DrawerImageLoader.init(object: AbstractDrawerImageLoader(){
            override fun set(imageView: ImageView, uri: Uri, placeholder: Drawable) {
                Picasso.with(imageView.context).load(uri).into(imageView)
            }
        })
    }



    private fun listenForLastestMessages() {
        val fromId = FirebaseAuth.getInstance().uid
        val ref = FirebaseDatabase.getInstance().getReference("/latest-messages/$fromId")
        ref.addChildEventListener(object: ChildEventListener{
            override fun onCancelled(error: DatabaseError) {
            }
            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                val chatMessage = snapshot.getValue(ChatMessage::class.java)    ?: return
                MainScreenmessagesMap[snapshot.key!!] = chatMessage
                refreshRecyclerViewMessages()
            }
            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {
                val chatMessage = snapshot.getValue(ChatMessage::class.java)    ?: return
                MainScreenmessagesMap[snapshot.key!!] = chatMessage
                refreshRecyclerViewMessages()
            }
            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {
            }
            override fun onChildRemoved(snapshot: DataSnapshot) {
            }
        })
    }



    val adapter= GroupAdapter<ViewHolder>()


    private fun verifyUserIsLoggedIn()
    {
        val id = FirebaseAuth.getInstance().uid
        if(id == null)
        {
            val intent = Intent(this, ChoiceActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
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
                        /* PrimaryDrawerItem().withIdentifier(100)
                                 .withIconTintingEnabled(true)
                                 .withName("My messages")
                                 .withSelectable(false)
                             ,*/

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
                        intent.putExtra("image",user.getImageLink())
                        intent.putExtra("nickname", user.getNickname())
                        intent.putExtra("name",user.getName())
                        intent.putExtra("surname",user.getSurname())
                        intent.putExtra("description",user.getInfo())
                        intent.putExtra("phone",user.getPhone())
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
                                .withEmail(user.getEmail())
                                //.withIcon(user.getImageLink().toUri())
         ).build()
    }

    private fun initfields() {
        mToolbar = binding.mainToolbar
    }

interface FirebaseCallBack{
    fun onCallBack(list: ArrayList<String>)
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
            val user = snapshot.child("Name").getValue(String::class.java)
            list.add(user!!)
            val email = snapshot.child("Email").getValue(String::class.java)
            list.add(email!!)
            val image = snapshot.child("ImageLink").getValue(String::class.java)
            list.add(image!!)
            val surname = snapshot.child("Surname").getValue(String::class.java)
            list.add(surname!!)
            val phone = snapshot.child("Phone").getValue(String::class.java)
            list.add(phone!!)
            val description = snapshot.child("Info").getValue(String::class.java)
            list.add(description!!)
            val nickname = snapshot.child("Nickname").getValue(String::class.java)
            list.add(nickname!!)
            Log.d("Autho","List: $list")
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
        override fun onCallBack(list: ArrayList<String>) {

            user.setName(list.elementAt(0))
            user.setEmail(list.elementAt(1))
            user.setImageLink(list.elementAt(2))
            user.setSurname(list.elementAt(3))
            user.setPhone(list.elementAt(4))
            user.setInfo(list.elementAt(5))
            user.setNickname(list.elementAt(6))
            Log.d("Autho","User.imageLink = ${user.getImageLink()}")
            val uri = list.elementAt(2).toUri()
            mHeader.removeProfileByIdentifier(228)
            val prof = ProfileDrawerItem().withName(user.getName())
                    .withEmail(user.getEmail())
                    .withIdentifier(228)
                    .withIcon(uri)
            mHeader.addProfiles(prof)
        }
    })

}

    lateinit var locationManager: LocationManager
    var locationGps: Location? = null
    var locationNetwork: Location? = null
    var finalLocation: Location? = null
    private fun getLocation()
    {
        locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        var hasGps = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
        var hasNetwork = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
        if(hasGps || hasNetwork){
            if(ActivityCompat.checkSelfPermission(this,android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this,android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED)
            {
                ActivityCompat.requestPermissions(this,arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION,android.Manifest.permission.ACCESS_COARSE_LOCATION),100)
            }
            if(hasGps)
            {
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,5000,0F, object:
                    LocationListener {
                    override fun onLocationChanged(location: Location) {
                        locationGps = location
                    }
                })
                val localGpsLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)
                if(localGpsLocation != null)
                    locationGps = localGpsLocation
            }
            if(hasNetwork)
            {
                locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,5000,0F,object:
                    LocationListener {
                    override fun onLocationChanged(location: Location) {
                        locationNetwork = location

                    }

                })

                val localNetworkLocation = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)
                if(localNetworkLocation != null)
                    locationNetwork = localNetworkLocation
            }
            if(locationGps != null && locationNetwork != null)
            {
                if(locationGps!!.accuracy > locationNetwork!!.accuracy)
                {
                    finalLocation = locationGps as Location
                }
                else
                {
                    finalLocation = locationNetwork as Location
                }
            }
            val list = ArrayList<Double>()
            list.add(1.54)
            list.add(3.32)
            if(finalLocation != null)
            {
                list[0] = (finalLocation as Location).latitude
                list[1] = (finalLocation as Location).longitude
            }
            else
            {
                if(locationGps != null)
                {
                    list[0] = (locationGps as Location).latitude
                    list[1] = (locationGps as Location).longitude
                }
                else
                {
                    list[0] = (locationNetwork as Location).latitude
                    list[1] = (locationNetwork as Location).longitude
                }
            }
            val uid = FirebaseAuth.getInstance().uid
            val ref = FirebaseDatabase.getInstance().getReference("users/$uid/CurrentLocation")
            if(list.count() == 2)
                ref.setValue(list)
        }
        else{
            startActivity(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS))
        }

    }
}




