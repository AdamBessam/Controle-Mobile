package com.ensa.testazahaimar.ui.resp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.ensa.testazahaimar.R
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import org.json.JSONObject

class EditHomeLoation : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private var selectedLocation: LatLng? = null
    val userId = "67181aeacbdc8432337a4f78"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_home_loation)

        // Initialize the map
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        val buttonSave: Button = findViewById(R.id.buttonSave)
        buttonSave.setOnClickListener {
            saveLocation()
        }
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        // Enable zoom controls and zoom gestures
        mMap.uiSettings.isZoomControlsEnabled = true  // Adds zoom in/out buttons
        mMap.uiSettings.isZoomGesturesEnabled = true  // Enables pinch-to-zoom gesture

        // Set default location (e.g., city center) for initial camera position

        // Add a marker and move the camera when the user clicks on the map
        mMap.setOnMapClickListener { latLng ->
            selectedLocation = latLng
            mMap.clear() // Clear previous markers
            mMap.addMarker(MarkerOptions().position(latLng).title("Selected Location"))
            mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng))
        }
    }

    private fun saveLocation() {
        selectedLocation?.let { latLng ->
            // API URL for updating the home location
            val url = "http://10.0.2.2:3000/api/locations/$userId/home-location" // Make sure the URL is correct

            // Create the JSON object
            val jsonBody = JSONObject().apply {
                put("latitude", latLng.latitude) // Directly add latitude and longitude
                put("longitude", latLng.longitude)
            }

            // Create a new request queue
            val queue = Volley.newRequestQueue(this)

            // Create a JsonObjectRequest
            val jsonObjectRequest = JsonObjectRequest(
                Request.Method.PUT, url, jsonBody,
                { response ->
                    // Handle response
                    Toast.makeText(this, "Location updated successfully!", Toast.LENGTH_SHORT).show()
                },
                { error ->
                    // Handle error
                    Toast.makeText(this, "Error: ${error.message}", Toast.LENGTH_SHORT).show()
                }
            )

            // Add the request to the RequestQueue
            queue.add(jsonObjectRequest)
        } ?: Toast.makeText(this, "Please select a location!", Toast.LENGTH_SHORT).show()
    }
}
