package com.ensa.testazahaimar.ui.notifications

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.fragment.app.Fragment
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.ensa.testazahaimar.R
import com.ensa.testazahaimar.databinding.FragmentNotificationsBinding
import java.text.SimpleDateFormat
import java.util.*

class NotificationsFragment : Fragment() {

    private var _binding: FragmentNotificationsBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentNotificationsBinding.inflate(inflater, container, false)
        val root: View = binding.root

        // Fetch user data
        fetchUserData()

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun fetchUserData() {
        val url = "http://10.0.2.2:3000/api/users/67181aeacbdc8432337a4f78"

        val jsonObjectRequest = JsonObjectRequest(
            Request.Method.GET, url, null,
            Response.Listener { response ->
                try {
                    // Parse the response
                    val firstName = response.getString("name")
                    val lastName = response.getString("surname")
                    val email = response.getString("email")
                    val phoneNumber = response.getString("phoneNumber")
                    val dateOfBirth = response.getString("dateOfBirth") // Fetch the dateOfBirth

                    // Format the dateOfBirth
                    val formattedDate = formatDateOfBirth(dateOfBirth)

                    // Update the UI with the fetched data
                    binding.root.findViewById<EditText>(R.id.firstNameInput).setText(firstName)
                    binding.root.findViewById<EditText>(R.id.lastNameInput).setText(lastName)
                    binding.root.findViewById<EditText>(R.id.emailInput).setText(email)
                    binding.root.findViewById<EditText>(R.id.phoneInput).setText(phoneNumber)
                    binding.root.findViewById<EditText>(R.id.dateOfBirthInput).setText(formattedDate) // Set formatted dateOfBirth

                } catch (e: Exception) {
                    Log.e("NotificationsFragment", "Error parsing user data: $e")
                }
            },
            Response.ErrorListener { error ->
                Log.e("NotificationsFragment", "Error fetching user data: $error")
            }
        )

        // Add the request to the Volley request queue
        val requestQueue = Volley.newRequestQueue(requireContext())
        requestQueue.add(jsonObjectRequest)
    }

    private fun formatDateOfBirth(dateString: String): String {
        // Assuming dateString is in the format "YYYY-MM-DD"
        val inputFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val outputFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        return try {
            val date = inputFormat.parse(dateString)
            outputFormat.format(date)
        } catch (e: Exception) {
            Log.e("NotificationsFragment", "Error formatting date: $e")
            dateString // Return original if there's an error
        }
    }
}
