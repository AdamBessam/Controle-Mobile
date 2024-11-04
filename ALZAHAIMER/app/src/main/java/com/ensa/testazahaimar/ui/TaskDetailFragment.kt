package com.ensa.testazahaimar.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.ensa.testazahaimar.R
import com.ensa.testazahaimar.ui.model.Task

class TaskDetailFragment(private val task: Task) : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_task_detail, container, false)

        val titleTextView: TextView = view.findViewById(R.id.tvTaskDetailTitle)
        val dateTextView: TextView = view.findViewById(R.id.tvTaskDetailDate)
        val statusTextView: TextView = view.findViewById(R.id.tvTaskDetailStatus)
        val closeButton: Button = view.findViewById(R.id.btnClose)

        // Set task details to the views
        titleTextView.text = task.title
        dateTextView.text = task.date
        statusTextView.text = if (task.isCompleted) "Complète" else "Incomplète"

        // Close button functionality
        closeButton.setOnClickListener {
            requireActivity().supportFragmentManager.popBackStack() // Navigate back
        }

        return view
    }
}
