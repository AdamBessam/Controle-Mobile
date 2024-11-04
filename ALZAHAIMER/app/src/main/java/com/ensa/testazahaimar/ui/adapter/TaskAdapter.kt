package com.ensa.testazahaimar.ui.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.ensa.testazahaimar.R
import com.ensa.testazahaimar.ui.model.Task

class TaskAdapter(
    private val context: Context,
    private val tasks: List<Task>
) : RecyclerView.Adapter<TaskAdapter.TaskViewHolder>() {

    class TaskViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val taskTitle: TextView = view.findViewById(R.id.tvTaskTitle)
        val taskDate: TextView = view.findViewById(R.id.tvTaskDate)
        val taskStatus: TextView = view.findViewById(R.id.tvTaskStatus)

        fun bind(task: Task, context: Context) {
            taskTitle.text = task.title
            taskDate.text = task.date
            taskStatus.text = if (task.isCompleted) "Complète" else "Incomplète"

            // Change the color of the status text based on completion state
            taskStatus.setTextColor(
                if (task.isCompleted) {
                    ContextCompat.getColor(context, R.color.green) // Use green if completed
                } else {
                    ContextCompat.getColor(context, R.color.red) // Use red if not completed
                }
            )

            // Handle click event to show dialog
            itemView.setOnClickListener {
                showTaskDetailsDialog(task, context)
            }
        }

        private fun showTaskDetailsDialog(task: Task, context: Context) {
            // Inflate the dialog layout
            val dialogView = LayoutInflater.from(context).inflate(R.layout.activity_details, null)

            // Initialize dialog components
            val titleTextView: TextView = dialogView.findViewById(R.id.tvTaskDetailTitle)
            val dateTextView: TextView = dialogView.findViewById(R.id.tvTaskDetailDate)
            val statusTextView: TextView = dialogView.findViewById(R.id.chipTaskStatus)
            val descriptionTextView: TextView = dialogView.findViewById(R.id.tvTaskDetailDescription)
            val closeButton: Button = dialogView.findViewById(R.id.btnClose)

            // Set the values in the dialog
            titleTextView.text = task.title
            dateTextView.text = task.date
            statusTextView.text = if (task.isCompleted) "Complète" else "Incomplète"
            descriptionTextView.text = task.description

            // Change the color of the status text in the dialog
            statusTextView.setTextColor(
                if (task.isCompleted) {
                    ContextCompat.getColor(context, R.color.green) // Use green if completed
                } else {
                    ContextCompat.getColor(context, R.color.red) // Use red if not completed
                }
            )

            // Create and show the dialog
            val dialogBuilder = AlertDialog.Builder(context)
                .setView(dialogView)
                .setCancelable(true)

            val dialog = dialogBuilder.create()

            // Close button functionality
            closeButton.setOnClickListener {
                dialog.dismiss() // Close the dialog
            }

            dialog.show() // Display the dialog
        }
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_task, parent, false)
        return TaskViewHolder(view)
    }

    override fun onBindViewHolder(holder: TaskViewHolder, position: Int) {
        val task = tasks[position]
        holder.bind(task, context)
    }

    override fun getItemCount(): Int = tasks.size
}
