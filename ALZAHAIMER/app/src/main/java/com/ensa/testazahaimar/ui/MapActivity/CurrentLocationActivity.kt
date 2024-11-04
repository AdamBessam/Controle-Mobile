package com.ensa.testazahaimar.ui.MapActivity

import android.Manifest
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.VolleyError
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.ensa.testazahaimar.R
import com.google.android.gms.location.*
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import org.json.JSONObject

class CurrentLocationActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var googleMap: GoogleMap
    private lateinit var locationCallback: LocationCallback
    private lateinit var locationRequest: LocationRequest
    private var currentMarker: MarkerOptions? = null
    private val userId = "67181aeacbdc8432337a4f78"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_current_location)

        // Initialize FusedLocationProviderClient
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        // Initialize the map
        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        // Initialize location request for continuous updates
        locationRequest = LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 10000L)
            .setWaitForAccurateLocation(false)
            .setMinUpdateDistanceMeters(10f) // Send updates only if moved 10 meters
            .build()

        // Create a location callback to handle location changes
        locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                val location: Location = locationResult.lastLocation ?: return
                val latitude = location.latitude
                val longitude = location.longitude

                // Log and update the map with new location
                Log.d("CurrentLocationActivity", "New location - Latitude: $latitude, Longitude: $longitude")
                updateUserLocation(LatLng(latitude, longitude))

                // Send new location to the server
                sendLocationToServer(latitude, longitude)
            }
        }
    }

    override fun onMapReady(googleMap: GoogleMap) {
        this.googleMap = googleMap
        googleMap.uiSettings.isZoomControlsEnabled = true
        googleMap.uiSettings.isZoomGesturesEnabled = true

        // Set a click listener on the map
        googleMap.setOnMapClickListener { latLng ->
            updateUserLocation(latLng)
        }

        // Start requesting location updates
        startLocationUpdates()
    }

    private fun startLocationUpdates() {
        // Check for location permissions
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), LOCATION_PERMISSION_REQUEST_CODE)
            return
        }

        // Request location updates
        fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, null)
    }

    private fun stopLocationUpdates() {
        fusedLocationClient.removeLocationUpdates(locationCallback)
    }

    private fun updateUserLocation(latLng: LatLng) {
        // Clear previous markers
        googleMap.clear()

        // Add marker for the new location
        currentMarker = MarkerOptions().position(latLng).title("Current Location")
        googleMap.addMarker(currentMarker!!)
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15f))
    }

    private fun sendLocationToServer(latitude: Double, longitude: Double) {
        val url = "http://10.0.2.2:3000/api/locations/$userId/current"

        val jsonObject = JSONObject().apply {
            put("latitude", latitude)
            put("longitude", longitude)
        }

        val jsonObjectRequest = JsonObjectRequest(
            Request.Method.POST, url, jsonObject,
            Response.Listener { response ->
                Log.d("CurrentLocationActivity", "Location updated successfully: $response")
            },
            Response.ErrorListener { error: VolleyError ->
                Log.e("CurrentLocationActivity", "Error updating location: ${error.message}")
            }
        )

        // Add the request to the queue
        Volley.newRequestQueue(this).add(jsonObjectRequest)
    }

    companion object {
        private const val LOCATION_PERMISSION_REQUEST_CODE = 1
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startLocationUpdates() // Start updates if permission is granted
            } else {
                Log.e("CurrentLocationActivity", "Permission denied")
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        stopLocationUpdates() // Stop updates when activity is destroyed
    }
}
