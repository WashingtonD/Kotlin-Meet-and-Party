package com.example.kotlinmeat

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.drawable.Drawable
import android.location.Location
import android.os.Build
import android.os.Bundle
import android.os.Looper
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import coil.imageLoader
import coil.request.ImageRequest
import com.google.android.gms.location.*
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.squareup.picasso.Picasso
import kotlin.math.pow


class DisplayMapActivity : AppCompatActivity(), OnMapReadyCallback, GoogleMap.OnMarkerClickListener {


    internal inner class InfoWindowActivity: AppCompatActivity(), GoogleMap.OnInfoWindowLongClickListener
    {
        override fun onInfoWindowLongClick(p0: Marker?) {
            val needMarker = mNamedMarkers.filterValues { it == p0 }
            val key = needMarker.keys
            startAnotherUserProfileAct(key.elementAt(0))
        }
    }

    private fun startAnotherUserProfileAct(id: String)
    {
        intent = Intent(this,MapPopupActivity::class.java)
        intent.putExtra("id",id)
        startActivity(intent)
    }



    /// Constants connected with distance calculations
    val pi = kotlin.math.PI
    val rad = 6371
    ///

    private lateinit var map: GoogleMap
    val requestcode = 101
    var mapFrag: SupportMapFragment? = null
    lateinit var mLocationRequest: LocationRequest
    var mLastLocation: Location? = null
    internal var mCurrentLocationMarker: Marker? = null
    internal var mFusedLocationProviderClient: FusedLocationProviderClient? = null
    var mNamedMarkers = mutableMapOf<String,Marker>()
    var startOfUsers = mutableMapOf<String,User>()


    internal var mLocationCallback: LocationCallback = object : LocationCallback() {
        fun OnLocationResult(locationResult: LocationResult) {
            val locationList = locationResult.locations
            if (locationList.isEmpty()) {
                val location = locationList.last()
                Log.d("MapActivity", "Location" + location.latitude + " " + location.longitude)
                mLastLocation = location
                if (mCurrentLocationMarker != null) {
                    mCurrentLocationMarker?.remove()
                }
                val latLng = LatLng(location.latitude, location.longitude)
                val MarkerOptions = MarkerOptions()
                MarkerOptions.position(latLng)
                MarkerOptions.title("Current position")
                mCurrentLocationMarker = map.addMarker(MarkerOptions)

                map.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 11.0F))
            }
        }
    }

    interface FirebaseCallBack{
        fun onCallBack(list: ArrayList<User>)
    }

    private fun checkStartUserLocation()
    {
        val uid = FirebaseAuth.getInstance().uid
            var position = FirebaseDatabase.getInstance().getReference("/users/$uid")
            position.child("CurrentLocation").get().addOnSuccessListener {
                //startPosOfUser = it.value as Point
                var pos = it.value as ArrayList<Double>
                Log.d("MapActivity","${it.value}")
                var x = pos.elementAt(0)
                var y = pos.elementAt(1)
                var marker = map.addMarker(MarkerOptions().position(LatLng(x,y)))
                mNamedMarkers.put(uid!!,marker)
                marker.remove()
            }
                    .addOnFailureListener {
                        Log.d("checkUserLocation","Failed to get position.")
                    }
    }


    private fun getUsersInfo(firebaseCallBack: FirebaseCallBack)
    {
        var ref = FirebaseDatabase.getInstance().getReference("/users")
        var userpos = mNamedMarkers.getOrDefault(FirebaseAuth.getInstance().uid,null)
        ref.addValueEventListener(object: ValueEventListener{
            override fun onCancelled(error: DatabaseError) {}
            override fun onDataChange(snapshot: DataSnapshot) {
                var list = ArrayList<User>()
                for(ds in snapshot.children)
                {
                    var user = ds.getValue(User::class.java)
                    if(userpos != null) {
                        if (getDistance(
                                userpos.position.latitude,
                                userpos.position.longitude,
                                (user!!.getCurrentLocation().elementAt(0)),
                                (user.getCurrentLocation().elementAt(1))) < 1000) {
                            list.add(user)
                        }
                    }
                }
                firebaseCallBack.onCallBack(list)
            }
        })
    }

    private fun updateMassOfUsers()
    {
        getUsersInfo(object: FirebaseCallBack{
            override fun onCallBack(list: ArrayList<User>) {
                for(ds: User in list)
                {
                    startOfUsers.put(ds.getId(),ds)
                }
            }
        })
    }



    private fun getDistance(myLatitude: Double, myLongitude: Double, anotherLatitude: Double, anotherLongitude: Double): Double
    {
        var phi1 = (myLatitude*pi)/180
        var phi2 = (anotherLatitude*pi)/180
        var lambda1 = (myLongitude*pi)/180
        var lambda2 = (anotherLongitude*pi)/180

        var cu1 = kotlin.math.cos(phi1)
        var cb2 = kotlin.math.cos(phi2)
        var su1 = kotlin.math.sin(phi1)
        var sb2 = kotlin.math.sin(phi2)
        var delta = lambda2-lambda1
        var cdelta = kotlin.math.cos(delta)
        var sdelta = kotlin.math.sin(delta)

        var y = kotlin.math.sqrt((cb2*sdelta).pow(2)+(cu1*sb2-su1*cb2*cdelta).pow(2))
        var x = su1*sb2+cu1*cb2*cdelta
        var ad = kotlin.math.atan2(y,x)
        var dist = ad*rad

        return dist
    }

    

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maps)


        checkStartUserLocation()
        updateMassOfUsers()


        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)
        mapFrag = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFrag?.getMapAsync(this)




    }


    private fun checkLocationPermission(){
        if(ActivityCompat.checkSelfPermission(this,android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED)
        {
            if(ActivityCompat.shouldShowRequestPermissionRationale(this,android.Manifest.permission.ACCESS_FINE_LOCATION))
            {
                AlertDialog.Builder(this)
                        .setTitle(("Location Permission Needed"))
                        .setMessage(("This app needs the Location permission, please accept to use location functionality"))
                        .setPositiveButton("OK"){_,_ ->
                            ActivityCompat.requestPermissions(
                                    this,
                                    arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION),
                                    requestcode
                            )
                        }
                        .create()
                        .show()
            }
            else
            {
                ActivityCompat.requestPermissions(
                        this,
                        arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION),
                        MY_PERMISSIONS_REQUEST_LOCATION
                )
            }
        }
    }
    fun ActivityCompat.OnRequestPermissionsResultCallback(requestCode: Int,
                                                          permissions: Array<String>, grantResults: IntArray){
        when(requestCode){
            MY_PERMISSIONS_REQUEST_LOCATION -> {
                if(grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                {
                    if(ContextCompat.checkSelfPermission(this@DisplayMapActivity,android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED)
                    {
                        mFusedLocationProviderClient?.requestLocationUpdates(
                                mLocationRequest,
                                mLocationCallback,
                                Looper.myLooper()!!
                        )
                        map.isMyLocationEnabled = true
                    }
                }
                else{
                    Toast.makeText(this@DisplayMapActivity, "permission denied", Toast.LENGTH_LONG).show()
                }
                return
            }
        }

    }
    companion object{
        val MY_PERMISSIONS_REQUEST_LOCATION = 99
    }


    override fun onMapReady(googleMap: GoogleMap?) {
        map = googleMap!!
        map.mapType = GoogleMap.MAP_TYPE_NORMAL
        mLocationRequest = LocationRequest()
        mLocationRequest.interval = 120000
        mLocationRequest.fastestInterval = 120000
        mLocationRequest.priority = LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY





        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            if(ContextCompat.checkSelfPermission(this,android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED)
            {
                mFusedLocationProviderClient?.requestLocationUpdates(mLocationRequest,mLocationCallback, Looper.myLooper()!!)
                map.isMyLocationEnabled = true
                val id = FirebaseAuth.getInstance().uid
                val ref = FirebaseDatabase.getInstance().getReference("/users")

                ref.addChildEventListener(object : ChildEventListener{
                    override fun onCancelled(error: DatabaseError) {
                        Log.d("MapActivity","$error and that is why user was not added to map")
                    }
                    override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                        var key = snapshot.key
                        Log.d("MapActivity","Trying to add $key user to map")
                        //var userpos  = mCurrentLocationMarker!!.position

                        var name = snapshot.child("Name").getValue(String::class.java)+ " " + snapshot.child("Surname").getValue(String::class.java)
                        var desc = snapshot.child("Info").getValue(String::class.java)
                        var lat = snapshot.child("CurrentLocation/0").getValue(Double::class.java)
                        var lng = snapshot.child("CurrentLocation/1").getValue(Double::class.java)
                        var location = LatLng(lat!!,lng!!)
                        var image = snapshot.child("ImageLink").getValue(String::class.java)
                        var userpos = mNamedMarkers.getOrDefault(FirebaseAuth.getInstance().uid,null)
                        var marker = mNamedMarkers.getOrDefault(key,null)
                        if(marker == null && key != FirebaseAuth.getInstance().uid)
                        {
                            if(getDistance(userpos!!.position.latitude,userpos.position.longitude,location.latitude,location.longitude) < 51230) {
                                var im: Bitmap
                                var options = MarkerOptions().position(location).title(name).snippet(desc+"+"+image)
                                val request  = ImageRequest.Builder(this@DisplayMapActivity)
                                    .data(image).target {
                                        result ->
                                        options.icon(getIconFromDrawable(result))
                                    }.build()
                                //options.icon(getIconFromDrawable(R.drawable.ic_baseline_account_circle_24))
                                var mark = map.addMarker(options)
                                mNamedMarkers.put(key!!, mark!!)
                                val disposable = imageLoader.enqueue(request)
                            }

                        }
                        else
                        {
                            Log.d("MapActivity","NewUser: $key Added")
                        }
                    }
                    override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {
                        var key = snapshot.key
                        Log.d("MapActivity","Location for $key user is updated")

                        var lat = snapshot.child("CurrentLocation/0").getValue(Double::class.java)
                        var lng = snapshot.child("CurrentLocation/1").getValue(Double::class.java)
                        var location = LatLng(lat!!,lng!!)
                        var marker = mNamedMarkers.getOrDefault(key,null)
                        if(marker == null)
                        {
                            var options = MarkerOptions().position(location).title(key)
                            var mark = map.addMarker(options)
                            mNamedMarkers.put(key!!,mark!!)
                        }
                        else
                        {
                            if(marker.position != location)
                            {
                                var name = snapshot.child("Name").getValue(String::class.java) + " " + snapshot.child("Surname").getValue(String::class.java)
                                var desc = snapshot.child("Info").getValue(String::class.java)
                                var myLat = (mNamedMarkers.getOrDefault(FirebaseAuth.getInstance().uid,null))//!!.position.latitude
                                if(myLat != null && getDistance(myLat.position.latitude,myLat.position.longitude,marker.position.latitude,marker.position.longitude) < 51000.0) {
                                    mNamedMarkers.remove(key)
                                    marker.remove()
                                    var options = MarkerOptions().position(location).title(name).snippet(desc)
                                    var mark = map.addMarker(options)
                                    mNamedMarkers.put(key!!, mark!!)
                                }
                            }
                            Log.d("MapActivity","OnChildChanged Good")
                        }


                    }
                    override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {
                        Log.d("MapActivity","Priority for ${snapshot.key} user was changed")
                    }
                    override fun onChildRemoved(snapshot: DataSnapshot) {
                        val key  = snapshot.key
                        val marker = mNamedMarkers.getOrDefault(key,null)
                        if(marker != null)
                        {
                            marker.remove()
                        }

                     Log.d("MapActivity","User $key was removed")
                    }
                })


            }
            else
            {
                checkLocationPermission()
            }
        }
        else
        {
            mFusedLocationProviderClient?.requestLocationUpdates(mLocationRequest,mLocationCallback,Looper.myLooper()!!)
        }
        map.setOnMarkerClickListener(this)
        map.setInfoWindowAdapter(CustomInfoWindowForGoogleMap(this))
        map.setOnInfoWindowLongClickListener(InfoWindowActivity())
    }
    override fun onMarkerClick(marker: Marker): Boolean{

        val clickCount = marker.tag as? Int

        clickCount?.let {
            val newClickCount = it + 1
            marker.tag = newClickCount
            Toast.makeText(
                    this,
                    "${marker.title} has been clicked $newClickCount times.",
                    Toast.LENGTH_SHORT
            ).show()
        }
        return false
    }

    var target: com.squareup.picasso.Target = object: com.squareup.picasso.Target{
        override fun onBitmapFailed(errorDrawable: Drawable?) {}
        override fun onBitmapLoaded(
            bitmap: Bitmap?,
            from: Picasso.LoadedFrom?
        ) {}
        override fun onPrepareLoad(placeHolderDrawable: Drawable?) {}
    }


    private fun getIconFromDrawable(drawable: Drawable): BitmapDescriptor
    {
        var bitmap = Bitmap.createBitmap(drawable.intrinsicWidth,drawable.intrinsicHeight,Bitmap.Config.ARGB_8888)
        var canvas = Canvas()
        canvas.setBitmap(bitmap)
        drawable.setBounds(0,0,drawable.intrinsicWidth,drawable.intrinsicHeight)
        drawable.draw(canvas)
        return BitmapDescriptorFactory.fromBitmap(bitmap)
    }

}


class CustomInfoWindowForGoogleMap(context: Context):GoogleMap.InfoWindowAdapter {
    var mContext = context
    var mWindow = (context as Activity).layoutInflater.inflate(R.layout.fragment_diary_map, null)

    private fun rendowWindowText(marker: Marker, view: View) {
        val tvTitle = view.findViewById<TextView>(R.id.title)
        val tvSnippet = view.findViewById<TextView>(R.id.snippet)
        var position = marker.snippet.indexOf("+")
        var desc = marker.snippet.substring(0,position)
        var imagelink = marker.snippet.substring(position+1)
        Log.d("InfoWindow",imagelink)
        Picasso.with(mContext).load(imagelink).fit().centerInside().into(view.findViewById(R.id.contextMenuImage) as ImageView)
        tvTitle.text = marker.title
        tvSnippet.text = desc
    }

    override fun getInfoContents(p0: Marker?): View {
        rendowWindowText(p0!!, mWindow)
        return mWindow
    }

    override fun getInfoWindow(p0: Marker?): View {
        rendowWindowText(p0!!, mWindow)
        return mWindow
    }


}