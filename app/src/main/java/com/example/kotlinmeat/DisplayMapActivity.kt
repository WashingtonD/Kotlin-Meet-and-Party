package com.example.kotlinmeat

import android.content.pm.PackageManager
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
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions


class DisplayMapActivity : AppCompatActivity(), OnMapReadyCallback {

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

    internal var mLocationCallback: LocationCallback = object : LocationCallback() {
        fun OnLocationResult(locationResult: LocationResult) {
            val locationList = locationResult.locations
            if (locationList.isEmpty()) {
                val location = locationList.last()
                Log.d("MapsActivity", "Location" + location.latitude + " " + location.longitude)
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

    //private lateinit var fusedLocationProviderClient: FusedLocationProviderClient


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maps)
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        // fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)
        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)

        mapFrag = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFrag?.getMapAsync(this)


        //getlastlocation()
        /*locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        try {
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 10L, 0f, locationListener)
        }catch (ex: SecurityException){
            Log.d("Map","Security exception, no location")
        }*/

        // val mapFragment = supportFragmentManager
        //   .findFragmentById(R.id.map) as SupportMapFragment
        // mapFragment.getMapAsync(this)

    }

    /*private val locationListener: LocationListener = object: LocationListener{
        override fun onLocationChanged(location: Location) {
            longitude = location.longitude
            latitude = location.latitude
            Log.d("MapAct","Long: $longitude, Lat: $latitude")
        }
        override fun onProviderDisabled(provider: String) {
            super.onProviderDisabled(provider)
        }
        override fun onProviderEnabled(provider: String) {
            super.onProviderEnabled(provider)
        }
    }*/

    // fun getlastlocation()
    // {


    // }


    /* override fun onMapReady(googleMap: GoogleMap) {
        map = googleMap
        //etlastlocation()
        val UL = LatLng(latitude, longitude)
        map.addMarker(
                MarkerOptions().position(UL).title("Your location $latitude || $longitude")
                        )
        map.animateCamera(CameraUpdateFactory.newLatLngZoom(UL, 10f))

    }*/

    /*override fun OnMapReady(googleMap: GoogleMap){
       map = googleMap
        map.mapType = GoogleMap.MAP_TYPE_HYBRID

        mLocationRequest = LocationRequest()
        mLocationRequest.interval = 120000
        mLocationRequest.fastestInterval = 120000
        mLocationRequest.priority = LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            if(ContextCompat.checkSelfPermission(this,android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED)
            {
                mFusedLocationProviderClient?.requestLocationUpdates(mLocationRequest,mLocationCallback, Looper.myLooper())
                map.isMyLocationEnabled = true
            }
            else
            {
                checkLocationPermission()
            }
        }
        else
        {
            mFusedLocationProviderClient?.requestLocationUpdates(mLocationRequest,mLocationCallback,Looper.myLooper())
        }
   }*/
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
                                Looper.myLooper()
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
        map.mapType = GoogleMap.MAP_TYPE_HYBRID
        mLocationRequest = LocationRequest()
        mLocationRequest.interval = 120000
        mLocationRequest.fastestInterval = 120000
        mLocationRequest.priority = LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            if(ContextCompat.checkSelfPermission(this,android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED)
            {
                mFusedLocationProviderClient?.requestLocationUpdates(mLocationRequest,mLocationCallback, Looper.myLooper())
                map.isMyLocationEnabled = true
            }
            else
            {
                checkLocationPermission()
            }
        }
        else
        {
            mFusedLocationProviderClient?.requestLocationUpdates(mLocationRequest,mLocationCallback,Looper.myLooper())
        }
    }


}