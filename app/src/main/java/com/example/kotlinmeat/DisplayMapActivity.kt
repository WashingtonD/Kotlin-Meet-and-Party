package com.example.kotlinmeat

import android.Manifest
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.Transformations.map
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions




class DisplayMapActivity : AppCompatActivity(), OnMapReadyCallback {

    private val LOCATION_PERMISSION_REQUEST = 1
    private lateinit var map: GoogleMap


    override fun onCreate(savedInstanceState: Bundle?) {
                super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maps)
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as? SupportMapFragment
        mapFragment?.getMapAsync(this)

    }


    private fun getLocationAccess() {
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            map.isMyLocationEnabled = true
        }
        else
            ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION), LOCATION_PERMISSION_REQUEST)
    }


    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        if (requestCode == LOCATION_PERMISSION_REQUEST) {
            if (grantResults.contains(PackageManager.PERMISSION_GRANTED)) {
                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    return
                }
                map.isMyLocationEnabled = true
            }
            else {
                Toast.makeText(this, "User has not granted location access permission", Toast.LENGTH_LONG).show()
                finish()
            }
        }
    }


    override fun onMapReady(googleMap: GoogleMap) {
        map = googleMap
        getLocationAccess()
    }
//
//    private lateinit var map: GoogleMap
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity_maps)
//        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
//        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as? SupportMapFragment
//        mapFragment?.getMapAsync(this)
//    }
//
//
////    override fun onMapReady(googleMap: GoogleMap?) {
////        googleMap?.apply {
////            val UL = LatLng(51.77661101791812, 19.48708592760286)
////            addMarker(
////                    MarkerOptions()
////                            .position(UL)
////                            .title("Uniwersytet Lodzki, Wydzial Matematyki i Informatyki")
////            )
////        }
////    }
////}
//
//    override fun onMapReady(googleMap: GoogleMap) {
//
////        map = googleMap ?: return
////        googleMap.setOnMyLocationButtonClickListener(this)
////        googleMap.setOnMyLocationClickListener(this)
////
//        map = googleMap
//        map.mapType = GoogleMap.MAP_TYPE_HYBRID
//        val UL = LatLng(51.77661101791812, 19.48708592760286)
//        map.addMarker(
//                MarkerOptions()
//                        .position(UL)
//                        .title("Uniwersytet Lodzki, Wydzial Matematyki i Informatyki")
//        )
//        map.animateCamera(CameraUpdateFactory.newLatLngZoom(UL, 10f))
//    }

//    private fun enableMyLocation() {
//        if (!::map.isInitialized) return
//        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
//                == PackageManager.PERMISSION_GRANTED) {
//            map.isMyLocationEnabled = true
//        } else {
//            // Permission to access the location is missing. Show rationale and request permission
//            requestPermission(this, LOCATION_PERMISSION_REQUEST_CODE,
//                    Manifest.permission.ACCESS_FINE_LOCATION, true
//            )
//        }
//    }
//
//    override fun onMyLocationButtonClick(): Boolean {
//        Toast.makeText(this, "MyLocation button clicked", Toast.LENGTH_SHORT).show()
//        // Return false so that we don't consume the event and the default behavior still occurs
//        // (the camera animates to the user's current position).
//        return false
//    }
//
//    override fun onMyLocationClick(location: Location) {
//        Toast.makeText(this, "Current location:\n$location", Toast.LENGTH_LONG).show()
//    }
//
//    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
//        if (requestCode != LOCATION_PERMISSION_REQUEST_CODE) {
//            return
//        }
//        if (isPermissionGranted(permissions, grantResults, Manifest.permission.ACCESS_FINE_LOCATION)) {
//            // Enable the my location layer if the permission has been granted.
//            enableMyLocation()
//        } else {
//            // Permission was denied. Display an error message
//            // Display the missing permission error dialog when the fragments resume.
//            permissionDenied = true
//        }
//    }
//
//    override fun onResumeFragments() {
//        super.onResumeFragments()
//        if (permissionDenied) {
//            // Permission was not granted, display error dialog.
//            showMissingPermissionError()
//            permissionDenied = false
//        }
//    }
//
//    /**
//     * Displays a dialog with error message explaining that the location permission is missing.
//     */
//    private fun showMissingPermissionError() {
//        newInstance(true).show(supportFragmentManager, "dialog")
//    }
//
//    companion object {
//        /**
//         * Request code for location permission request.
//         *
//         * @see .onRequestPermissionsResult
//         */
//        private const val LOCATION_PERMISSION_REQUEST_CODE = 1
//    }
//}

}