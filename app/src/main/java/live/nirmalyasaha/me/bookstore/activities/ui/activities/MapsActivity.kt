package live.nirmalyasaha.me.bookstore.activities.ui.activities

import android.app.Activity
import android.content.Intent
import android.content.IntentSender
import android.content.pm.PackageManager
import android.location.Location
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.app.ActivityCompat
import android.location.Address
import android.location.Geocoder
import android.util.Log
import com.google.android.gms.common.GooglePlayServicesNotAvailableException
import com.google.android.gms.common.GooglePlayServicesRepairableException
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.*
import com.google.android.gms.location.places.ui.PlacePicker

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.android.synthetic.main.activity_maps.*
import live.nirmalyasaha.me.bookstore.R
import java.io.IOException

//class MapsActivity : AppCompatActivity(), OnMapReadyCallback, GoogleMap.OnMarkerClickListener {
//
//    private lateinit var map: GoogleMap
//    private lateinit var fusedLocationClient: FusedLocationProviderClient
//    private lateinit var lastLocation: Location
//
//    private lateinit var locationCallback: LocationCallback
//    private lateinit var locationRequest: LocationRequest
//    private var locationUpdateState = false
//
//    companion object {
//        private const val LOCATION_PERMISSION_REQUEST_CODE = 1
//        private const val REQUEST_CHECK_SETTINGS = 2
//        private const val PLACE_PICKER_REQUEST = 3
//    }
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity_maps)
//        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
//        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
//        mapFragment.getMapAsync(this)
//
//        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
//
//        locationCallback = object : LocationCallback() {
//            override fun onLocationResult(p0: LocationResult) {
//                super.onLocationResult(p0)
//
//                lastLocation = p0.lastLocation
//                placeMarkerOnMap(LatLng(lastLocation.latitude, lastLocation.longitude))
//            }
//        }
//
//        createLocationRequest()
//
//        fab.setOnClickListener {
//            loadPlacePicker()
//        }
//    }
//
//    //starts location updates
//    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
//        super.onActivityResult(requestCode, resultCode, data)
//        if (requestCode == REQUEST_CHECK_SETTINGS) {
//            if (resultCode == Activity.RESULT_OK) {
//                locationUpdateState = true
//                startLocationUpdates()
//            }
//        }
//        if (requestCode == PLACE_PICKER_REQUEST) {
//            if (resultCode == RESULT_OK) {
//                val place = PlacePicker.getPlace(this, data)
//                var addressText = place.name.toString()
//                addressText += "\n" + place.address.toString()
//
//                placeMarkerOnMap(place.latLng)
//            }
//        }
//    }
//
//    //stops location updates
//    override fun onPause() {
//        super.onPause()
//        fusedLocationClient.removeLocationUpdates(locationCallback)
//    }
//
//    //restart location updates
//    public override fun onResume() {
//        super.onResume()
//        if (!locationUpdateState) {
//            startLocationUpdates()
//        }
//    }
//
//    override fun onMapReady(googleMap: GoogleMap) {
//        map = googleMap
//
//        //appears the text on click on the marker and zoom bars
//        map.getUiSettings().setZoomControlsEnabled(true)
//        map.setOnMarkerClickListener(this)
//
//        //accepts permissions
//        setUpMap()
//    }
//
//    //appears a text when click on a marker
//    override fun onMarkerClick(p0: Marker?) = false
//
//    //accepts permissions
//    private fun setUpMap() {
//        if (ActivityCompat.checkSelfPermission(
//                this,
//                android.Manifest.permission.ACCESS_FINE_LOCATION
//            ) != PackageManager.PERMISSION_GRANTED
//        ) {
//            ActivityCompat.requestPermissions(
//                this,
//                arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION),
//                LOCATION_PERMISSION_REQUEST_CODE
//            )
//            return
//        }
//
//        //getting user current location
//        map.isMyLocationEnabled = true
//        map.mapType = GoogleMap.MAP_TYPE_NORMAL
//
//        fusedLocationClient.lastLocation.addOnSuccessListener(this) { location ->
//            // Got last known location. In some rare situations this can be null.
//            if (location != null) {
//                lastLocation = location
//                val currentLatLng = LatLng(location.latitude, location.longitude)
//                placeMarkerOnMap(currentLatLng)
//                map.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, 12f))
//            }
//        }
//    }
//
//    //placing marker
//    private fun placeMarkerOnMap(location: LatLng) {
//        val markerOptions = MarkerOptions().position(location)
//
//        val titleStr = getAddress(location)  // add these two lines
//        markerOptions.title(titleStr)
//
//        map.addMarker(markerOptions)
//    }
//
//    //geocoding
//    private fun getAddress(latLng: LatLng): String {
//        // 1
//        val geocoder = Geocoder(this)
//        val addresses: List<Address>?
//        val address: Address?
//        var addressText = ""
//
//        try {
//            // 2
//            addresses = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1)
//            // 3
//            if (null != addresses && !addresses.isEmpty()) {
//                address = addresses[0]
//                for (i in 0 until address.maxAddressLineIndex) {
//                    addressText += if (i == 0) address.getAddressLine(i) else "\n" + address.getAddressLine(i)
//                }
//            }
//        } catch (e: IOException) {
//            Log.e("MapsActivity", e.localizedMessage)
//        }
//
//        return addressText
//    }
//
//    //get location updates
//    private fun startLocationUpdates() {
//        if (ActivityCompat.checkSelfPermission(this,
//                android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
//            ActivityCompat.requestPermissions(this,
//                arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION),
//                LOCATION_PERMISSION_REQUEST_CODE)
//            return
//        }
//        fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, null /* Looper */)
//    }
//
//    private fun createLocationRequest() {
//        locationRequest = LocationRequest()
//        locationRequest.interval = 10000
//        locationRequest.fastestInterval = 5000
//        locationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
//
//        val builder = LocationSettingsRequest.Builder()
//            .addLocationRequest(locationRequest)
//        val client = LocationServices.getSettingsClient(this)
//        val task = client.checkLocationSettings(builder.build())
//
//        task.addOnSuccessListener {
//            locationUpdateState = true
//            startLocationUpdates()
//        }
//        task.addOnFailureListener { e ->
//            if (e is ResolvableApiException) {
//                // Location settings are not satisfied, but this can be fixed
//                // by showing the user a dialog.
//                try {
//                    // Show the dialog by calling startResolutionForResult(),
//                    // and check the result in onActivityResult().
//                    e.startResolutionForResult(this@MapsActivity,
//                        REQUEST_CHECK_SETTINGS)
//                } catch (sendEx: IntentSender.SendIntentException) {
//                    // Ignore the error.
//                }
//            }
//        }
//    }
//
//    private fun loadPlacePicker() {
//        val builder = PlacePicker.IntentBuilder()
//
//        try {
//            startActivityForResult(builder.build(this@MapsActivity), PLACE_PICKER_REQUEST)
//        } catch (e: GooglePlayServicesRepairableException) {
//            e.printStackTrace()
//        } catch (e: GooglePlayServicesNotAvailableException) {
//            e.printStackTrace()
//        }
//    }
//}




class MapsActivity : AppCompatActivity(), OnMapReadyCallback,
    GoogleMap.OnMarkerClickListener {

    private lateinit var map: GoogleMap
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var lastLocation: Location

    private lateinit var locationCallback: LocationCallback
    private lateinit var locationRequest: LocationRequest
    private var locationUpdateState = false

    companion object {
        private const val LOCATION_PERMISSION_REQUEST_CODE = 1
        private const val REQUEST_CHECK_SETTINGS = 2
        private const val PLACE_PICKER_REQUEST = 3
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maps)
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        locationCallback = object : LocationCallback() {
            override fun onLocationResult(p0: LocationResult) {
                super.onLocationResult(p0)

                lastLocation = p0.lastLocation
                placeMarkerOnMap(LatLng(lastLocation.latitude, lastLocation.longitude))
            }
        }

        createLocationRequest()

        val fab = findViewById<FloatingActionButton>(R.id.fab)
        fab.setOnClickListener {
            loadPlacePicker()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CHECK_SETTINGS) {
            if (resultCode == Activity.RESULT_OK) {
                locationUpdateState = true
                startLocationUpdates()
            }
        }
        if (requestCode == PLACE_PICKER_REQUEST) {
            if (resultCode == RESULT_OK) {
                val place = PlacePicker.getPlace(this, data)
                var addressText = place.name.toString()
                addressText += "\n" + place.address.toString()

                placeMarkerOnMap(place.latLng)
            }
        }
    }

    override fun onPause() {
        super.onPause()
        fusedLocationClient.removeLocationUpdates(locationCallback)
    }

    public override fun onResume() {
        super.onResume()
        if (!locationUpdateState) {
            startLocationUpdates()
        }
    }

    override fun onMapReady(googleMap: GoogleMap) {
        map = googleMap

        map.uiSettings.isZoomControlsEnabled = true
        map.setOnMarkerClickListener(this)

        setUpMap()
    }

    override fun onMarkerClick(p0: Marker?) = false

    private fun setUpMap() {
        if (ActivityCompat.checkSelfPermission(this,
                android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION), LOCATION_PERMISSION_REQUEST_CODE)
            return
        }

        map.isMyLocationEnabled = true
        map.mapType = GoogleMap.MAP_TYPE_NORMAL

        fusedLocationClient.lastLocation.addOnSuccessListener(this) { location ->
            // Got last known location. In some rare situations this can be null.
            if (location != null) {
                lastLocation = location
                val currentLatLng = LatLng(location.latitude, location.longitude)
                placeMarkerOnMap(currentLatLng)
                map.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, 12f))
            }
        }
    }

    private fun placeMarkerOnMap(location: LatLng) {
        val markerOptions = MarkerOptions().position(location)

        val titleStr = getAddress(location)  // add these two lines
        markerOptions.title(titleStr)

        map.addMarker(markerOptions)
    }

    private fun getAddress(latLng: LatLng): String {
        // 1
        val geocoder = Geocoder(this)
        val addresses: List<Address>?
        val address: Address?
        var addressText = ""

        try {
            // 2
            addresses = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1)
            // 3
            if (null != addresses && !addresses.isEmpty()) {
                address = addresses[0]
                for (i in 0 until address.maxAddressLineIndex) {
                    addressText += if (i == 0) address.getAddressLine(i) else "\n" + address.getAddressLine(i)
                }
            }
        } catch (e: IOException) {
            Log.e("MapsActivity", e.localizedMessage)
        }

        return addressText
    }

    private fun startLocationUpdates() {
        if (ActivityCompat.checkSelfPermission(this,
                android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION),
                LOCATION_PERMISSION_REQUEST_CODE)
            return
        }
        fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, null /* Looper */)
    }

    private fun createLocationRequest() {
        locationRequest = LocationRequest()
        locationRequest.interval = 10000
        locationRequest.fastestInterval = 5000
        locationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY

        val builder = LocationSettingsRequest.Builder()
            .addLocationRequest(locationRequest)
        val client = LocationServices.getSettingsClient(this)
        val task = client.checkLocationSettings(builder.build())

        task.addOnSuccessListener {
            locationUpdateState = true
            startLocationUpdates()
        }
        task.addOnFailureListener { e ->
            if (e is ResolvableApiException) {
                // Location settings are not satisfied, but this can be fixed
                // by showing the user a dialog.
                try {
                    // Show the dialog by calling startResolutionForResult(),
                    // and check the result in onActivityResult().
                    e.startResolutionForResult(this@MapsActivity,
                        REQUEST_CHECK_SETTINGS)
                } catch (sendEx: IntentSender.SendIntentException) {
                    // Ignore the error.
                }
            }
        }
    }

    private fun loadPlacePicker() {
        val builder = PlacePicker.IntentBuilder()

        try {
            startActivityForResult(builder.build(this@MapsActivity), PLACE_PICKER_REQUEST)
        } catch (e: GooglePlayServicesRepairableException) {
            e.printStackTrace()
        } catch (e: GooglePlayServicesNotAvailableException) {
            e.printStackTrace()
        }
    }
}
