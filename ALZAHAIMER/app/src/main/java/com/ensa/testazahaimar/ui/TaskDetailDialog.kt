package com.ensa.testazahaimar.ui

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.DialogFragment
import com.ensa.testazahaimar.R
import com.ensa.testazahaimar.ui.model.Task

class TaskDetailDialog(private val task: Task) : DialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        // Create the dialog and set its style
        return super.onCreateDialog(savedInstanceState).apply {
            setStyle(STYLE_NORMAL, R.style.CustomDialog) // Custom style (if needed)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.dialog_task_detail, container, false)

        // Initialize views
        val titleTextView: TextView = view.findViewById(R.id.tvDialogTaskTitle)
        val dateTextView: TextView = view.findViewById(R.id.tvDialogTaskDate)
        val statusTextView: TextView = view.findViewById(R.id.tvDialogTaskStatus)
        val closeButton: Button = view.findViewById(R.id.btnClose)

        // Set task details to the views
        titleTextView.text = task.title
        dateTextView.text = task.date
        statusTextView.text = if (task.isCompleted) "Complète" else "Incomplète"

        // Set click listener to dismiss the dialog
        closeButton.setOnClickListener {
            dismiss()
        }

        return view
    }

    override fun onStart() {
        super.onStart()
        // Center the dialog and set its width and height
        dialog?.window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        dialog?.window?.setBackgroundDrawableResource(android.R.color.transparent) // Optional: Transparent background
    }
}
