package com.ensa.testazahaimar.ui

import android.app.TimePickerDialog
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.ensa.testazahaimar.R
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import java.util.Calendar

class AddTaskActivity : AppCompatActivity() {

    private lateinit var etTitle: TextInputEditText
    private lateinit var etDescription: TextInputEditText
    private lateinit var etTime: TextInputEditText
    private lateinit var btnAddTask: MaterialButton
    private var selectedTime: String = "00:00" // Champ pour stocker l'heure

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_task)

        // Initialiser les champs et boutons
        etTitle = findViewById(R.id.etTitle)
        etDescription = findViewById(R.id.etDescription)
        etTime = findViewById(R.id.etDate)
        btnAddTask = findViewById(R.id.btnAddTask)

        val calendar = Calendar.getInstance()

        // Ouvrir le TimePickerDialog pour sélectionner l'heure et les minutes
        etTime.setOnClickListener {
            TimePickerDialog(
                this,
                { _, hourOfDay, minute ->
                    selectedTime = String.format("%02d:%02d", hourOfDay, minute)
                    etTime.setText(selectedTime) // Afficher l'heure et la minute sélectionnées
                },
                calendar.get(Calendar.HOUR_OF_DAY),
                calendar.get(Calendar.MINUTE),
                true // true pour le format 24h
            ).show()
        }

        // Envoi des données au backend lorsque le bouton est cliqué
        btnAddTask.setOnClickListener {
            sendTaskToBackend()
        }
    }

    private fun sendTaskToBackend() {
        // Récupérer les entrées de l'utilisateur
        val title = etTitle.text.toString().trim()
        val description = etDescription.text.toString().trim()

        // Vérification des champs
        if (title.isEmpty() || description.isEmpty() || selectedTime.isEmpty()) {
            Log.e("AddTaskActivity", "Les champs ne doivent pas être vides")
            return
        }

        // Créer l'objet JSON avec juste l'heure et la minute
        val jsonObject = """{
            "title": "$title",
            "description": "$description",
            "date": "$selectedTime"
        }"""

        Log.d("AddTaskActivity", "JSON à envoyer : $jsonObject")

        // URL de l'API
        val url = "http://10.0.2.2:3000/api/tasks/67181aeacbdc8432337a4f78/tasks"

        // Créer une requête POST avec Volley
        val stringRequest = object : StringRequest(Request.Method.POST, url,
            Response.Listener { response ->
                Log.d("AddTaskActivity", "Réponse : $response")
                finish() // Fermer l'activité après un succès
            },
            Response.ErrorListener { error ->
                Log.e("AddTaskActivity", "Erreur : ${error.message}")
                Log.e("AddTaskActivity", "Code status : ${error.networkResponse?.statusCode}")
                Log.e("AddTaskActivity", "Données : ${String(error.networkResponse.data)}")
            }) {
            override fun getBody(): ByteArray {
                return jsonObject.toByteArray()
            }

            override fun getHeaders(): MutableMap<String, String> {
                val headers = HashMap<String, String>()
                headers["Content-Type"] = "application/json"
                return headers
            }
        }

        // Ajouter la requête à la RequestQueue
        Volley.newRequestQueue(this).add(stringRequest)
    }
}
