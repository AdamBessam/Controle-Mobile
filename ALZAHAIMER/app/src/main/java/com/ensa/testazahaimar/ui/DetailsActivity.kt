package com.ensa.testazahaimar.ui

import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.ensa.testazahaimar.R
import com.google.android.material.chip.Chip // Import Chip class

class DetailsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_details)

        // Retrieve data from the intent
        val taskId = intent.getStringExtra("TASK_ID")
        val taskTitle = intent.getStringExtra("TASK_TITLE")
        val taskDate = intent.getStringExtra("TASK_DATE")
        val taskIsCompleted = intent.getBooleanExtra("TASK_IS_COMPLETED", false)
        val taskDescription = intent.getStringExtra("TASK_DESCRIPTION") // Retrieve Task Description

        // Populate your views
        val titleTextView: TextView = findViewById(R.id.tvTaskDetailTitle) // Title TextView
        val dateTextView: TextView = findViewById(R.id.tvTaskDetailDate) // Date TextView
        val statusChip: Chip = findViewById(R.id.chipTaskStatus) // Updated to Chip
        val descriptionTextView: TextView = findViewById(R.id.tvTaskDetailDescription) // Description TextView

        titleTextView.text = taskTitle
        dateTextView.text = taskDate
        statusChip.text = if (taskIsCompleted) "Complète" else "Incomplète" // Set Chip text
        descriptionTextView.text = taskDescription // Set the Description text

        // Setup close button action
        val closeButton: Button = findViewById(R.id.btnClose)
        closeButton.setOnClickListener {
            finish() // Close the activity
        }
    }
}
