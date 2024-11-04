package com.ensa.testazahaimar

import android.app.AlertDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.ensa.testazahaimar.MainActivity
import com.ensa.testazahaimar.R
import com.ensa.testazahaimar.ui.ResponsableActivity
import org.json.JSONObject

class Authentification : AppCompatActivity() {
    private lateinit var etEmail: EditText
    private lateinit var etPassword: EditText
    private lateinit var tvForgotPassword: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_authentification)

        etEmail = findViewById(R.id.etEmail)
        etPassword = findViewById(R.id.etPassword)
        val loginButton = findViewById<Button>(R.id.btnLogin)
        tvForgotPassword = findViewById(R.id.tvForgotPassword)

        loginButton.setOnClickListener {
            authenticateUser()
        }

        tvForgotPassword.setOnClickListener {
            showForgotPasswordDialog()
        }
    }

    private fun authenticateUser() {
        val email = etEmail.text.toString()
        val password = etPassword.text.toString()

        val url = "http://10.0.2.2:3000/authenticate"

        val requestBody = JSONObject()
        requestBody.put("email", email)
        requestBody.put("mtp", password)

        val jsonObjectRequest = JsonObjectRequest(
            Request.Method.POST, url, requestBody,
            { response ->
                val role = response.getString("role")
                // Redirect based on role
                val intent = if (role == "malade") {
                    Intent(this, MainActivity::class.java)
                } else {
                    Intent(this, ResponsableActivity::class.java)
                }
                startActivity(intent)
                finish() // Close the current activity
            },
            { error ->
                Toast.makeText(this, "Erreur d'authentification ", Toast.LENGTH_SHORT).show()
            }
        )

        // Add the request to the queue
        Volley.newRequestQueue(this).add(jsonObjectRequest)
    }

    private fun showForgotPasswordDialog() {
        val url = "http://10.0.2.2:3000/api/users/6716e0faaf34c36c3c4cfe99"

        // Make the network request to fetch user data
        val jsonObjectRequest = JsonObjectRequest(
            Request.Method.GET, url, null,
            { response ->
                val phoneNumber = response.getString("phoneNumber")
                showDialogWithPhoneNumber(phoneNumber)
            },
            { error ->
                Toast.makeText(this, "Failed to fetch phone number", Toast.LENGTH_SHORT).show()
            }
        )

        // Add the request to the Volley request queue
        Volley.newRequestQueue(this).add(jsonObjectRequest)
    }

    private fun showDialogWithPhoneNumber(phoneNumber: String) {
        // Inflate the custom layout
        val dialogView = layoutInflater.inflate(R.layout.dialog_forgot_password, null)
        val tvPhoneNumber: TextView = dialogView.findViewById(R.id.tvPhoneNumber)
        val btnCall: Button = dialogView.findViewById(R.id.btnCall)

        // Set the phone number
        tvPhoneNumber.text = phoneNumber

        // Build and display the dialog
        val dialogBuilder = AlertDialog.Builder(this)
        dialogBuilder.setView(dialogView)
            .setCancelable(false)
            .setTitle("Forgot Password")

        // Create the dialog
        val alertDialog = dialogBuilder.create()

        // Set up the call button action
        btnCall.setOnClickListener {
            // Place the call using an intent
            val intent = Intent(Intent.ACTION_DIAL)
            intent.data = Uri.parse("tel:$phoneNumber")
            startActivity(intent)
            alertDialog.dismiss()
        }

        alertDialog.show()
    }
}
