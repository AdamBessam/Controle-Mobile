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

class EditHospitalLocation : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private var selectedLocation: LatLng? = null
    val userId = "67181aeacbdc8432337a4f78" // Remplacez par l'ID utilisateur correct

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_hospital_location)

        // Initialisation de la carte
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        val buttonSave: Button = findViewById(R.id.buttonSavee)
        buttonSave.setOnClickListener {
            saveLocation()
        }
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        // Activer les contrôles de zoom et les gestes de zoom
        mMap.uiSettings.isZoomControlsEnabled = true  // Ajoute les boutons de zoom
        mMap.uiSettings.isZoomGesturesEnabled = true  // Active le geste de pincement pour zoomer

        // Position par défaut (exemple : centre-ville de Los Angeles)
        val defaultLocation = LatLng(34.0522, -118.2437)
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(defaultLocation, 10f))

        // Ajouter un marqueur et déplacer la caméra lorsque l'utilisateur clique sur la carte
        mMap.setOnMapClickListener { latLng ->
            selectedLocation = latLng
            mMap.clear() // Effacer les anciens marqueurs
            mMap.addMarker(MarkerOptions().position(latLng).title("Selected Hospital Location"))
            mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng))
        }
    }

    private fun saveLocation() {
        selectedLocation?.let { latLng ->
            // URL de l'API pour mettre à jour la localisation de l'hôpital
            val url = "http://10.0.2.2:3000/api/locations/$userId/hospital-location"

            // Création de l'objet JSON
            val jsonBody = JSONObject().apply {
                put("latitude", latLng.latitude)
                put("longitude", latLng.longitude)
            }

            // Création d'une nouvelle request queue
            val queue = Volley.newRequestQueue(this)

            // Création d'un JsonObjectRequest
            val jsonObjectRequest = JsonObjectRequest(
                Request.Method.PUT, url, jsonBody,
                Response.Listener { response ->
                    // Gestion de la réponse
                    Toast.makeText(this, "Hospital location updated successfully!", Toast.LENGTH_SHORT).show()
                },
                Response.ErrorListener { error ->
                    // Gestion de l'erreur
                    Toast.makeText(this, "Error: ${error.message}", Toast.LENGTH_SHORT).show()
                }
            )

            // Ajouter la requête à la RequestQueue
            queue.add(jsonObjectRequest)
        } ?: Toast.makeText(this, "Please select a location!", Toast.LENGTH_SHORT).show()
    }
}
