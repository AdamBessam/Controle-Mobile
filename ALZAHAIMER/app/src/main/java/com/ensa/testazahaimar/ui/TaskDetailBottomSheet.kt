package com.ensa.testazahaimar.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import com.ensa.testazahaimar.R
import com.ensa.testazahaimar.ui.model.Task
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class TaskDetailBottomSheet(private val task: Task) : BottomSheetDialogFragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.bottom_sheet_task_details, container, false)

        val tvTaskTitle = view.findViewById<TextView>(R.id.tvTaskTitle)
        val tvTaskDate = view.findViewById<TextView>(R.id.tvTaskDate)
        val tvTaskStatus = view.findViewById<TextView>(R.id.tvTaskStatus)
        val btnClose = view.findViewById<Button>(R.id.btnClose)

        // Afficher les détails de la tâche
        tvTaskTitle.text = task.title
        tvTaskDate.text = task.date
        tvTaskStatus.text = if (task.isCompleted) "Complète" else "Incomplète"

        // Configurer le bouton de fermeture
        btnClose.setOnClickListener {
            dismiss() // Ferme le bottom sheet
        }

        return view
    }
}
