package com.ensa.testazahaimar

import android.content.Context
import android.util.Log
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.VolleyError
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import org.json.JSONObject

class LocationRepository(private val context: Context) {

    private val requestQueue: RequestQueue by lazy {
        Volley.newRequestQueue(context)
    }

    // Récupérer la localisation actuelle
    fun getCurrentLocation(userId: String, callback: (latitude: Double?, longitude: Double?) -> Unit) {
        val url = "http://10.0.2.2:3000/api/locations/$userId/current"

        val jsonObjectRequest = JsonObjectRequest(Request.Method.GET, url, null,
            Response.Listener<JSONObject> { response ->
                val latitude = response.getDouble("latitude")
                val longitude = response.getDouble("longitude")
                callback(latitude, longitude)
            },
            Response.ErrorListener { error: VolleyError ->
                Log.e("VolleyError", "Error fetching current location: ${error.message}")
                callback(null, null)
            })

        requestQueue.add(jsonObjectRequest)
    }

    // Récupérer la localisation de la maison
    fun getHomeLocation(userId: String, callback: (latitude: Double?, longitude: Double?) -> Unit) {
        val url = "http://10.0.2.2:3000/api/locations/$userId/home"

        val jsonObjectRequest = JsonObjectRequest(Request.Method.GET, url, null,
            Response.Listener<JSONObject> { response ->
                val latitude = response.getDouble("latitude")
                val longitude = response.getDouble("longitude")
                callback(latitude, longitude)
            },
            Response.ErrorListener { error: VolleyError ->
                Log.e("VolleyError", "Error fetching home location: ${error.message}")
                callback(null, null)
            })

        requestQueue.add(jsonObjectRequest)
    }

    // Récupérer la localisation de l'hôpital
    fun getHospitalLocation(userId: String, callback: (latitude: Double?, longitude: Double?) -> Unit) {
        val url = "http://10.0.2.2:3000/api/locations/$userId/hospital"

        val jsonObjectRequest = JsonObjectRequest(Request.Method.GET, url, null,
            Response.Listener<JSONObject> { response ->
                val latitude = response.getDouble("latitude")
                val longitude = response.getDouble("longitude")
                callback(latitude, longitude)
            },
            Response.ErrorListener { error: VolleyError ->
                Log.e("VolleyError", "Error fetching hospital location: ${error.message}")
                callback(null, null)
            })

        requestQueue.add(jsonObjectRequest)
    }
}
