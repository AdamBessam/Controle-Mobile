package com.ensa.testazahaimar.ui

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Intent
import android.location.Location
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.core.app.NotificationCompat
import com.android.volley.Request
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.ensa.testazahaimar.R

import com.ensa.testazahaimar.ui.resp.EditHomeLoation
import com.ensa.testazahaimar.ui.resp.EditHospitalLocation
import com.ensa.testazahaimar.ui.resp.LocationMalade
import com.ensa.testazahaimar.ui.resp.ProfilUser
import com.ensa.testazahaimar.ui.resp.UserProfileActivity
import com.google.android.gms.maps.model.LatLng
import org.json.JSONException
class ResponsableActivity : AppCompatActivity() {
    private lateinit var currentButton: View
    private lateinit var hospitalButton: View
    private lateinit var homeButton: View
    private lateinit var name: TextView
    private val userId = "6716e0faaf34c36c3c4cfe99"
    private val userIds = "67181aeacbdc8432337a4f78"
    private var lastMaladeLocation: LatLng? = null
    private var secondLastMaladeLocation: LatLng? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_responsable)

        // Initialize UI components
        currentButton = findViewById(R.id.buttonCurrent)
        hospitalButton = findViewById(R.id.buttonHospital)
        homeButton = findViewById(R.id.buttonHome)
        name = findViewById(R.id.res)
        val cardView: CardView = findViewById(R.id.cardView)

        // Button listeners
        currentButton.setOnClickListener {
            startActivity(Intent(this, LocationMalade::class.java))
        }

        hospitalButton.setOnClickListener {
            startActivity(Intent(this, EditHospitalLocation::class.java))
        }

        homeButton.setOnClickListener {
            startActivity(Intent(this, EditHomeLoation::class.java))
        }

        cardView.setOnClickListener {
            val intent = Intent(this, ProfilUser::class.java)
            startActivity(intent)
        }

        // Fetch initial data
        fetchMaladeLocation()
        createNotificationChannel()
        fetchUserData(userId)
    }

    private var lastTimestamp: Long = 0 // Add a variable to keep track of the last timestamp

    private fun fetchMaladeLocation() {
        Log.d("ResponsableActivity", "fetchMaladeLocation() called")

        val url = "http://10.0.2.2:3000/api/users/$userIds/current-location"

        val jsonArrayRequest = JsonArrayRequest(
            Request.Method.GET, url, null,
            { response ->
                Log.d("API Response", response.toString())

                try {
                    if (response.length() > 1) {
                        // Get the two most recent locations
                        val lastLocation = response.getJSONObject(response.length() - 1)
                        val secondLastLocation = response.getJSONObject(response.length() - 2)

                        val lastLatitude = lastLocation.getDouble("latitude")
                        val lastLongitude = lastLocation.getDouble("longitude")

                        val secondLastLatitude = secondLastLocation.getDouble("latitude")
                        val secondLastLongitude = secondLastLocation.getDouble("longitude")

                        Log.d("Location", "Last location: Lat=$lastLatitude, Lng=$lastLongitude")
                        Log.d("Location", "Second last location: Lat=$secondLastLatitude, Lng=$secondLastLongitude")

                        val newLocation = LatLng(lastLatitude, lastLongitude)
                        val oldLocation = LatLng(secondLastLatitude, secondLastLongitude)

                        // Calculate the distance between the two locations
                        val distance = calculateDistance(oldLocation, newLocation)

                        // Check if the distance is greater than 50 meters
                        if (distance > 50) {
                            sendNotification("Alert: Significant Location Change", "The new location is more than 50 meters away!")
                        }

                        // Update locations for next check
                        secondLastMaladeLocation = lastMaladeLocation
                        lastMaladeLocation = newLocation
                    } else {
                        Log.e("Location", "Not enough locations found")
                    }
                } catch (e: JSONException) {
                    Log.e("Location", "JSON parsing error: ${e.message}")
                }
            },
            { error ->
                Log.e("Location", "Network error: ${error.message}")
            }
        )

        Volley.newRequestQueue(this).add(jsonArrayRequest)
    }


    private fun sendNotification(title: String, message: String) {
        val intent = Intent(this, LocationMalade::class.java)

        // Use FLAG_IMMUTABLE for Android 12+
        val pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_IMMUTABLE)

        val notificationBuilder = NotificationCompat.Builder(this, LocationMalade.CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_notification) // Replace with your icon
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
            val descriptionText = "Notifications for location alerts"
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel(LocationMalade.CHANNEL_ID, name, importance).apply {
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


        return results[0] // Distance in meters
    }

    private fun fetchUserData(userId: String) {
        val url = "http://10.0.2.2:3000/api/users/$userId"

        val jsonObjectRequest = JsonObjectRequest(
            Request.Method.GET, url, null,
            { response ->
                try {
                    val userSurname = response.getString("surname")
                    name.text = "Welcome Mr.$userSurname"
                } catch (e: JSONException) {
                    e.printStackTrace()
                }
            },
            { error ->
                error.printStackTrace()
            }
        )

        Volley.newRequestQueue(this).add(jsonObjectRequest)
    }
}
