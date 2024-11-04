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
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.maps.model.PolylineOptions
import org.json.JSONObject

class HospitalLocationActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var googleMap: GoogleMap
    private var mMap: GoogleMap? = null
    private val userId = "67181aeacbdc8432337a4f78"
    private var hospitalLocation: LatLng? = null
    private var currentLocation: LatLng? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_hospital_location)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        checkLocationPermissions()
    }

    private fun checkLocationPermissions() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), LOCATION_PERMISSION_REQUEST_CODE)
        } else {
            fetchHospitalLocation()
            fetchCurrentLocation()
        }
    }

    private fun fetchHospitalLocation() {
        val url = "http://10.0.2.2:3000/api/users/$userId/hospital-location" // Ensure this URL is correct

        val jsonObjectRequest = JsonObjectRequest(
            Request.Method.GET, url, null,
            Response.Listener { response ->
                Log.d("HospitalLocationActivity", "Response: $response")
                // Parse latitude and longitude directly from the root object
                if (response.has("latitude") && response.has("longitude")) {
                    val latitude = response.getDouble("latitude")
                    val longitude = response.getDouble("longitude")
                    hospitalLocation = LatLng(latitude, longitude)
                    Log.d("HospitalLocationActivity", "Hospital location retrieved: $hospitalLocation")

                    // Check if mMap is initialized and then display the hospital location
                    mMap?.let {
                        it.addMarker(MarkerOptions().position(hospitalLocation!!).title("Hospital Location"))
                        it.moveCamera(CameraUpdateFactory.newLatLngZoom(hospitalLocation!!, 15f))
                    } ?: Log.e("HospitalLocationActivity", "mMap is null when trying to show hospital location")
                } else {
                    Log.e("HospitalLocationActivity", "Hospital location coordinates are missing")
                }
            },
            Response.ErrorListener { error: VolleyError ->
                Log.e("HospitalLocationActivity", "Error fetching hospital location: ${error.message}")
            }
        )

        Volley.newRequestQueue(this).add(jsonObjectRequest)
    }

    private fun fetchCurrentLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
                if (location != null) {
                    currentLocation = LatLng(location.latitude, location.longitude)
                    Log.d("HospitalLocationActivity", "Current location retrieved: $currentLocation")

                    mMap?.let {
                        it.addMarker(MarkerOptions().position(currentLocation!!).title("Current Location"))
                        it.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLocation!!, 15f))

                        hospitalLocation?.let { hospitalLoc ->
                            drawRoute(currentLocation!!, hospitalLoc)
                        }
                    } ?: Log.e("HospitalLocationActivity", "mMap is null when trying to show current location")
                } else {
                    Log.e("HospitalLocationActivity", "User location not available")
                }
            }
        } else {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), LOCATION_PERMISSION_REQUEST_CODE)
        }
    }

    private fun drawRoute(start: LatLng, end: LatLng) {
        val url = "https://maps.googleapis.com/maps/api/directions/json?origin=${start.latitude},${start.longitude}&destination=${end.latitude},${end.longitude}&key=AIzaSyBHG_WAZGA6kTrKeruE_97LUGC1Z75-U78"

        val jsonObjectRequest = JsonObjectRequest(
            Request.Method.GET,
            url,
            null,
            Response.Listener { response ->
                try {
                    val routes = response.getJSONArray("routes")
                    if (routes.length() > 0) {
                        val points = ArrayList<LatLng>()
                        val polyline = routes.getJSONObject(0).getJSONObject("overview_polyline")
                        val encodedString = polyline.getString("points")
                        decodePoly(encodedString, points)

                        mMap?.addPolyline(PolylineOptions().addAll(points).color(R.color.black).width(10f))
                    }
                } catch (e: Exception) {
                    Log.e("HospitalLocationActivity", "Error fetching directions: $e")
                }
            },
            Response.ErrorListener { error ->
                Log.e("HospitalLocationActivity", "Error fetching directions: $error")
            }
        )

        Volley.newRequestQueue(this).add(jsonObjectRequest)
    }

    private fun decodePoly(encoded: String, points: ArrayList<LatLng>) {
        var index = 0
        val len = encoded.length
        var lat = 0
        var lng = 0

        while (index < len) {
            var b: Int
            var shift = 0
            var result = 0
            do {
                b = encoded[index++].toInt() - 63
                result = result or (b and 0x1f shl shift)
                shift += 5
            } while (b >= 0x20)

            val dlat = if (result and 1 != 0) {
                -(result shr 1)
            } else {
                result shr 1
            }
            lat += dlat

            shift = 0
            result = 0
            do {
                b = encoded[index++].toInt() - 63
                result = result or (b and 0x1f shl shift)
                shift += 5
            } while (b >= 0x20)

            val dlng = if (result and 1 != 0) {
                -(result shr 1)
            } else {
                result shr 1
            }
            lng += dlng

            val p = LatLng((lat / 1E5), (lng / 1E5))
            points.add(p)
        }
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                LOCATION_PERMISSION_REQUEST_CODE
            )
            return
        }

        mMap?.isMyLocationEnabled = true

        // Enable zoom controls
        mMap?.uiSettings?.isZoomControlsEnabled = true
        // Enable zoom gestures
        mMap?.uiSettings?.isZoomGesturesEnabled = true

        // Fetch the current location and display it
        fetchCurrentLocation()

        mMap?.setOnMapClickListener { latLng ->
            mMap?.addMarker(MarkerOptions().position(latLng).title("Clicked Location"))
        }
    }

    companion object {
        private const val LOCATION_PERMISSION_REQUEST_CODE = 1
    }
}
