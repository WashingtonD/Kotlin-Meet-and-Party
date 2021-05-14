package com.example.kotlinmeat

import android.content.pm.PackageManager
import android.graphics.Point
import android.location.Location
import android.location.LocationManager
import android.os.Build
import android.os.Bundle
import android.os.Looper
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.location.*
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import kotlin.math.pow


class DisplayMapActivity : AppCompatActivity(), OnMapReadyCallback, GoogleMap.OnMarkerClickListener {

    /// Constants connected with distance calculations
    val pi = kotlin.math.PI
    val rad = 6371
    ///


    private lateinit var map: GoogleMap
    private lateinit var locationManager: LocationManager
    private var longitude = 0.0
    private var latitude = 0.0
    val requestcode = 101
    var mapFrag: SupportMapFragment? = null
    lateinit var mLocationRequest: LocationRequest
    var mLastLocation: Location? = null
    internal var mCurrentLocationMarker: Marker? = null
    internal var mFusedLocationProviderClient: FusedLocationProviderClient? = null
    private lateinit var anotherUserLoca: User
    private var MapReady = false
    private var needToDo = false
    var mNamedMarkers = mutableMapOf<String,Marker>()
    lateinit var startPosOfUser: Point


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
            position.child("currentLocation").get().addOnSuccessListener {
                //startPosOfUser = it.value as Point
                var pos = it.value as HashMap<String,Double>
                Log.d("MapActivity","${it.value}")
                var x = pos.get("x")
                var y = pos.get("y")
                var marker = map.addMarker(MarkerOptions().position(LatLng(x!!,y!!)))
                mNamedMarkers.put(uid!!,marker)
                marker.remove()
            }
                    .addOnFailureListener {
                        Log.d("checkUserLocation","Failed to get position.")
                    }
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



   /* private fun getLoc(firebaseCallBack: FirebaseCallBack) {
        if (needToDo == false) {
            needToDo = true
            val uid = FirebaseAuth.getInstance().uid
            val ref = FirebaseDatabase.getInstance().getReference("/users")
            ref.addValueEventListener((object : ValueEventListener {
                override fun onCancelled(error: DatabaseError) {
                    Log.d("MapActivity", "$error Can't get user loc in getLoc fun")
                }

                override fun onDataChange(snapshot: DataSnapshot) {
                    var list = ArrayList<User>()
                    snapshot.children.forEach {
                        val loc = it.getValue(User::class.java)
                        list.add(loc!!)
                        Log.d("MapActivity", "Got user $loc")
                    }
                    firebaseCallBack.onCallBack(list)
                }
            }))
        }
    }
    private fun update_pr()
    {
        getLoc(object: FirebaseCallBack{
            override fun onCallBack(list: ArrayList<User>) {
                for(ds in list) {
                    anotherUserLoca = ds
                    var marker = map.addMarker(MarkerOptions().position(LatLng(anotherUserLoca.currentLocation.x.toDouble(), anotherUserLoca.currentLocation.y.toDouble())))
                    mNamedMarkers.put(anotherUserLoca.id, marker)
                    Log.d("MapActivity", "User ${anotherUserLoca.id} was added to NamedMarkers")
                }
            }
        })
    }*/

    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maps)
        checkStartUserLocation()



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
                //val reference = FirebaseDatabase.getInstance().getReference("users")


               /*ref.addValueEventListener((object: ValueEventListener{
                    override fun onCancelled(error: DatabaseError) {
                        Log.d("MapActivity","$error")
                    }
                    override fun onDataChange(snapshot: DataSnapshot) {



                        val list = mutableMapOf<String,Point>()
                        snapshot.children.forEach{
                        val loc = it.child("currentLocation").getValue(Point::class.java)
                        list.put(it.key!!,loc!!)
                        Log.d("MapActivity","Got user new loc: ${loc!!.x},${loc.y}")
                       val ll = LatLng(loc.x.toDouble(),loc.y.toDouble())
                        val markeropt = MarkerOptions()
                        markeropt.position(ll)
                        markeropt.title("User from Firebase")
                        map.addMarker(markeropt)
                    }
                    }
                }))*/
                ref.addChildEventListener(object : ChildEventListener{
                    override fun onCancelled(error: DatabaseError) {
                        Log.d("MapActivity","$error and that is why user was not added to map")
                    }
                    override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                        var key = snapshot.key
                        Log.d("MapActivity","Trying to add $key user to map")
                        //var userpos  = mCurrentLocationMarker!!.position


                        var lat = snapshot.child("currentLocation/x").getValue(Int::class.java)
                        var lng = snapshot.child("currentLocation/y").getValue(Int::class.java)
                        var location = LatLng(lat!!.toDouble(),lng!!.toDouble())

                        var userpos = mNamedMarkers.getOrDefault(FirebaseAuth.getInstance().uid,null)
                        var marker = mNamedMarkers.getOrDefault(key,null)
                        if(marker == null && key != FirebaseAuth.getInstance().uid)
                        {
                            if(getDistance(userpos!!.position.latitude,userpos.position.longitude,location.latitude,location.longitude) < 30) {
                                var options = MarkerOptions().position(location).title(key)
                                var mark = map.addMarker(options)
                                mNamedMarkers.put(key!!, mark!!)
                                Log.d("MapActivity", "OnChildAdded was not appropriate")
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

                        var lat = snapshot.child("currentLocation/x").getValue(Int::class.java)
                        var lng = snapshot.child("currentLocation/y").getValue(Int::class.java)
                        var location = LatLng(lat!!.toDouble(),lng!!.toDouble())
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
                                var myLat = (mNamedMarkers.getOrDefault(FirebaseAuth.getInstance().uid,null))//!!.position.latitude
                                if(myLat != null && getDistance(myLat.position.latitude,myLat.position.longitude,marker.position.latitude,marker.position.longitude) < 1000.0) {
                                    mNamedMarkers.remove(key)
                                    marker.remove()
                                    var options = MarkerOptions().position(location).title(key)
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
            /*val id = FirebaseAuth.getInstance().uid
            val ref = FirebaseDatabase.getInstance().getReference("users/$id")
            ref.addListenerForSingleValueEvent((object: ValueEventListener{
                override fun onCancelled(error: DatabaseError) {
                    Log.d("MapActivity","Can't get user loc")
                }
                override fun onDataChange(snapshot: DataSnapshot) {
                    //val list = ArrayList<Point>()
                    val loc = snapshot.child("currentLocation").getValue(Point::class.java)
                    //list.add(loc!!)
                    Log.d("MapActivity","Got user new loc: ${loc!!.x},${loc.y}")
                    val ll = LatLng(loc.x.toDouble(),loc.y.toDouble())
                    val markeropt = MarkerOptions()
                    markeropt.position(ll)
                    markeropt.title("User from Firebase")
                    map.addMarker(markeropt)
                }
            }))*/

        }
   /*     markerSzymon = map.addMarker(
                MarkerOptions()
                        .position(Szymon)
                        .title("Szymon")
        )
        markerSzymon.tag = 0
        markerMaciek = map.addMarker(
                MarkerOptions()
                        .position(Maciek)
                        .title("Maciek")
        )
        markerMaciek.tag = 0
        markerBartek = map.addMarker(
                MarkerOptions()
                        .position(Bartek)
                        .title("Bartek")
        )
        markerBartek.tag = 0*/
        map.setOnMarkerClickListener(this)

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


















}



