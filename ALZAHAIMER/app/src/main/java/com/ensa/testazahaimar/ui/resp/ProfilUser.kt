package com.ensa.testazahaimar.ui.resp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.EditText
import com.ensa.testazahaimar.R
import com.android.volley.Request
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import org.json.JSONException
import java.text.SimpleDateFormat
import java.util.Locale

class ProfilUser : AppCompatActivity() {

    private lateinit var firstNameInput: EditText
    private lateinit var lastNameInput: EditText
    private lateinit var emailInput: EditText
    private lateinit var phoneInput: EditText
    private lateinit var dateOfBirthInput: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_profile)

        // Bind the UI elements
        firstNameInput = findViewById(R.id.firstNameInput)
        lastNameInput = findViewById(R.id.lastNameInput)
        emailInput = findViewById(R.id.emailInput)
        phoneInput = findViewById(R.id.phoneInput)
        dateOfBirthInput = findViewById(R.id.dateOfBirthInput)

        // Fetch user data from the API
        fetchUserData("6716e0faaf34c36c3c4cfe99")  // Replace with dynamic user ID as needed
    }

    private fun fetchUserData(userId: String) {
        val url = "http://10.0.2.2:3000/api/users/$userId"  // Replace with your actual API URL

        val jsonObjectRequest = JsonObjectRequest(
            Request.Method.GET, url, null,
            { response ->
                try {
                    // Assuming your API returns these fields
                    firstNameInput.setText(response.getString("name"))
                    lastNameInput.setText(response.getString("surname"))
                    emailInput.setText(response.getString("email"))
                    phoneInput.setText(response.getString("phoneNumber"))

                    // Format date of birth
                    val dateOfBirth = response.getString("dateOfBirth")
                    val formattedDate = formatDate(dateOfBirth)
                    dateOfBirthInput.setText(formattedDate)

                } catch (e: JSONException) {
                    e.printStackTrace()
                }
            },
            { error ->
                error.printStackTrace() // Handle error here
            }
        )

        // Add the request to the RequestQueue
        Volley.newRequestQueue(this).add(jsonObjectRequest)
    }

    private fun formatDate(dateString: String): String {
        return try {
            val inputFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()) // Change according to your API format
            val outputFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()) // Change to your desired output format if needed

            val date = inputFormat.parse(dateString) // Parse the date
            outputFormat.format(date) // Format the date
        } catch (e: Exception) {
            e.printStackTrace()
            dateString // Return original if parsing fails
        }
    }
}
