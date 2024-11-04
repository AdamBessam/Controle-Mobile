package com.ensa.testazahaimar.ui.resp

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import com.android.volley.Request
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.Volley
import com.ensa.testazahaimar.R
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import org.json.JSONArray
import org.json.JSONObject

class LocationMalade : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var googleMap: GoogleMap
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private val userId = "67181aeacbdc8432337a4f78"
    private var userLocation: LatLng? = null
    private var maladeLocation: LatLng? = null
    private var userMarker: Marker? = null
    private var maladeMarker: Marker? = null

    // Variables pour stocker les dernières localisations
    private var lastMaladeLocation: LatLng? = null
    private var secondLastMaladeLocation: LatLng? = null

    companion object {
        private const val LOCATION_PERMISSION_REQUEST_CODE = 1
        private const val TAG = "LocationMalade"
        const val CHANNEL_ID = "LocationAlertChannel"

        // Fonction de calcul de distance entre deux points
        private fun calculateDistance(start: LatLng, end: LatLng): Float {
            val results = FloatArray(1)
            Location.distanceBetween(start.latitude, start.longitude, end.latitude, end.longitude, results)
            return results[0] // Distance en mètres
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_location_malade)

        // Créer le canal de notification
        createNotificationChannel()

        // Initialiser la map
        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        // Initialiser le client de localisation
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
    }

    override fun onMapReady(googleMap: GoogleMap) {
        this.googleMap = googleMap
        googleMap.uiSettings.apply {
            isZoomControlsEnabled = true
            isZoomGesturesEnabled = true
            isMyLocationButtonEnabled = true
        }

        if (checkLocationPermission()) {
            googleMap.isMyLocationEnabled = true
            // Commencer à récupérer les locations une fois que la carte est prête
            fetchMaladeLocation()
            fetchUserLocation()
        }

        // Gestionnaire de clic sur la carte
        googleMap.setOnMapClickListener { latLng ->
            updateUserLocation(latLng)
        }
    }

    private fun checkLocationPermission(): Boolean {
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                LOCATION_PERMISSION_REQUEST_CODE
            )
            return false
        }
        return true
    }

    private fun fetchMaladeLocation() {
        val url = "http://10.0.2.2:3000/api/users/$userId/current-location"

        val jsonArrayRequest = JsonArrayRequest(
            Request.Method.GET, url, null,
            { response ->
                try {
                    if (response.length() > 1) {
                        // Accéder aux deux dernières localisations
                        val lastLocation = response.getJSONObject(response.length() - 1)
                        val secondLastLocation = response.getJSONObject(response.length() - 2)

                        val lastLatitude = lastLocation.getDouble("latitude")
                        val lastLongitude = lastLocation.getDouble("longitude")
                        val secondLastLatitude = secondLastLocation.getDouble("latitude")
                        val secondLastLongitude = secondLastLocation.getDouble("longitude")

                        Log.d(TAG, "Dernière localisation malade récupérée: Lat=$lastLatitude, Lng=$lastLongitude")
                        val newLocation = LatLng(lastLatitude, lastLongitude)
                        val oldLocation = LatLng(secondLastLatitude, secondLastLongitude)

                        // Calculer la distance entre les deux dernières localisations
                        val distance = calculateDistance(oldLocation, newLocation)

                        // Vérifier si la distance est supérieure à 50 mètres


                        // Mettre à jour les localisations
                        secondLastMaladeLocation = lastMaladeLocation
                        lastMaladeLocation = newLocation
                        updateMaladeLocation(newLocation)

                    } else {
                        Log.e(TAG, "Pas assez de localisations trouvées")
                    }
                } catch (e: Exception) {
                    Log.e(TAG, "Erreur parsing JSON: ${e.message}")
                    Toast.makeText(this, "Erreur lors du traitement des données de localisation", Toast.LENGTH_SHORT).show()
                }
            },
            { error ->
                Log.e(TAG, "Erreur réseau: ${error.message}")
                Toast.makeText(this, "Erreur lors de la récupération de la localisation", Toast.LENGTH_SHORT).show()
            }
        )

        Volley.newRequestQueue(this).add(jsonArrayRequest)
    }

    private fun sendNotification(title: String, message: String) {
        val intent = Intent(this, LocationMalade::class.java)
        val pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_IMMUTABLE)

        val notificationBuilder = NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_notification) // Remplacez par votre icône
            .setContentTitle(title)
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)

        val notificationManager = getSystemService(NotificationManager::class.java)
        notificationManager.notify(1, notificationBuilder.build())
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "Location Alerts"
            val descriptionText = "Notifications pour les alertes de localisation"
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
                description = descriptionText
            }
            val notificationManager: NotificationManager = getSystemService(NotificationManager::class.java)
            notificationManager.createNotificationChannel(channel)
        }
    }

    private fun calculateDistance(oldLocation: LatLng, newLocation: LatLng): Float {
        val results = FloatArray(1)
        Location.distanceBetween(
            oldLocation.latitude,
            oldLocation.longitude,
            newLocation.latitude,
            newLocation.longitude,
            results
        )
        return results[0] // Distance en mètres
    }



    private fun fetchUserLocation() {
        if (!checkLocationPermission()) return

        fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
            location?.let {
                updateUserLocation(LatLng(it.latitude, it.longitude))
            } ?: Toast.makeText(this, "Impossible de récupérer votre localisation", Toast.LENGTH_SHORT).show()
        }.addOnFailureListener {
            Toast.makeText(this, "Erreur lors de la récupération de votre localisation", Toast.LENGTH_SHORT).show()
        }
    }

    private fun updateUserLocation(latLng: LatLng) {
        userLocation = latLng
        userMarker?.remove() // Supprimer le marqueur précédent
        userMarker = googleMap.addMarker(MarkerOptions().position(latLng).title("Votre Position"))
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15f))
    }

    private fun updateMaladeLocation(latLng: LatLng) {
        maladeLocation = latLng
        maladeMarker?.remove() // Supprimer le marqueur précédent
        maladeMarker = googleMap.addMarker(MarkerOptions().position(latLng).title("Position du Malade"))
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15f))
    }
}
