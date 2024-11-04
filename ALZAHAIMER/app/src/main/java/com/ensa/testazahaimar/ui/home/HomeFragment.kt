package com.ensa.testazahaimar.ui.home

import androidx.appcompat.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.ensa.testazahaimar.databinding.FragmentHomeBinding
import com.ensa.testazahaimar.ui.AddTaskActivity
import com.ensa.testazahaimar.ui.adapter.TaskAdapter
import com.ensa.testazahaimar.ui.model.Task
import com.google.android.material.snackbar.Snackbar
import org.json.JSONArray
import org.json.JSONObject

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private lateinit var recyclerView: RecyclerView
    private lateinit var taskAdapter: TaskAdapter
    private val tasks = mutableListOf<Task>()
    private lateinit var handler: Handler
    private lateinit var runnable: Runnable

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val homeViewModel = ViewModelProvider(this).get(HomeViewModel::class.java)

        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root

        setupRecyclerView() // Initialize RecyclerView
        setupItemTouchHelper() // Setup swipe and drag functionality
        setupFabAddListener() // Setup the Floating Action Button listener

        // Initialize handler and runnable for periodic fetching
        handler = Handler(Looper.getMainLooper())
        runnable = Runnable {
            fetchTasks()
            handler.postDelayed(runnable, 1000) // Fetch every second (1000 ms)
        }

        return root
    }

    private fun setupRecyclerView() {
        recyclerView = binding.recyclerView
        taskAdapter = TaskAdapter(requireContext(), tasks)
        recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerView.adapter = taskAdapter
    }

    private fun setupFabAddListener() {
        binding.fabAdd.setOnClickListener {
            val intent = Intent(context, AddTaskActivity::class.java)
            startActivity(intent)
        }
    }

    private fun setupItemTouchHelper() {
        val itemTouchHelperCallback = object : ItemTouchHelper.SimpleCallback(
            ItemTouchHelper.UP or ItemTouchHelper.DOWN,
            ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT
        ) {
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                val fromPosition = viewHolder.adapterPosition
                val toPosition = target.adapterPosition

                // Swap the items in the list
                tasks.add(toPosition, tasks.removeAt(fromPosition))
                taskAdapter.notifyItemMoved(fromPosition, toPosition)

                return true
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val position = viewHolder.adapterPosition
                val task = tasks[position]

                if (direction == ItemTouchHelper.LEFT) {
                    // Gérer le swipe à gauche (suppression de la tâche)
                    val alertDialog = AlertDialog.Builder(requireContext())
                        .setTitle("Delete Task")
                        .setMessage("Do you want to delete this task '${task.title}'?")
                        .setPositiveButton("Yes") { _, _ ->
                            deleteTask(task.id) {
                                tasks.removeAt(position)
                                taskAdapter.notifyItemRemoved(position)

                                // Afficher un Snackbar avec une option UNDO
                                Snackbar.make(
                                    binding.recyclerView,
                                    "Task '${task.title}' deleted by User: [Username]",
                                    Snackbar.LENGTH_LONG
                                ).setAction("UNDO") {
                                    tasks.add(position, task)
                                    taskAdapter.notifyItemInserted(position)
                                }.show()
                            }
                        }
                        .setNegativeButton("No") { dialog, _ ->
                            taskAdapter.notifyItemChanged(position)
                            dialog.dismiss()
                        }
                        .create()

                    alertDialog.show()
                } else if (direction == ItemTouchHelper.RIGHT) {
                    // Gérer le swipe à droite (marquer la tâche comme complétée/incomplète)
                    val isComplete = !task.isCompleted // Inverser l'état actuel
                    updateTaskStatus(task.id, isComplete) {
                        task.isCompleted = isComplete
                        taskAdapter.notifyItemChanged(position)

                        // Afficher un message de confirmation
                        Snackbar.make(
                            binding.recyclerView,
                            "Task '${task.title}' marked as ${if (isComplete) "completed" else "incomplete"}",
                            Snackbar.LENGTH_SHORT
                        ).show()
                    }
                }
            }
        }

        ItemTouchHelper(itemTouchHelperCallback).attachToRecyclerView(recyclerView)
    }
    private fun updateTaskStatus(taskId: String, isComplete: Boolean, onSuccess: () -> Unit) {
        val url = "http://10.0.2.2:3000/api/tasks/$taskId"

        // Créer un objet JSON contenant le nouvel état de la tâche
        val jsonBody = JSONObject()
        jsonBody.put("isComplete", isComplete)

        val jsonObjectRequest = JsonObjectRequest(
            Request.Method.PUT,
            url,
            jsonBody,
            Response.Listener {
                // Le statut de la tâche a été mis à jour avec succès
                onSuccess()
                Log.d("HomeFragment", "Task status updated successfully")
            },
            Response.ErrorListener { error ->
                Log.e("HomeFragment", "Error updating task status: ${error.message}")
            }
        )

        // Ajouter la requête à la RequestQueue
        Volley.newRequestQueue(requireContext()).add(jsonObjectRequest)
    }


    private fun deleteTask(taskId: String, onSuccess: () -> Unit) {
        // Show a loading indicator (consider implementing a ProgressDialog or Spinner here)
        val url = "http://10.0.2.2:3000/api/tasks/$taskId"

        val jsonObjectRequest = JsonObjectRequest(
            Request.Method.DELETE,
            url,
            null,
            Response.Listener {
                // Task deleted successfully
                onSuccess()
                Log.d("HomeFragment", "Task deleted successfully")
                // Hide loading indicator (implement this in your UI)
            },
            Response.ErrorListener { error ->
                Log.e("HomeFragment", "Error deleting task: ${error.message}")
                // Hide loading indicator (implement this in your UI)
            }
        )

        // Add the request to the RequestQueue
        Volley.newRequestQueue(requireContext()).add(jsonObjectRequest)
    }

    private fun fetchTasks() {
        // URL of the API
        val url = "http://10.0.2.2:3000/api/tasks/67181aeacbdc8432337a4f78/tasks"

        // Create a Volley request to fetch tasks
        val jsonArrayRequest = JsonArrayRequest(
            Request.Method.GET,
            url,
            null,
            Response.Listener { response ->
                // Process the JSON response
                parseTasks(response)
            },
            Response.ErrorListener { error ->
                Log.e("HomeFragment", "Error: ${error.message}")
            }
        )

        // Add the request to the RequestQueue
        Volley.newRequestQueue(requireContext()).add(jsonArrayRequest)
    }

    private fun parseTasks(response: JSONArray) {
        tasks.clear() // Clear the list before adding new tasks
        for (i in 0 until response.length()) {
            val taskJson: JSONObject = response.getJSONObject(i)
            // Assuming your Task model has a constructor that takes these attributes
            val task = Task(
                id = taskJson.getString("_id"),
                title = taskJson.getString("title"),
                description = taskJson.getString("description"),
                date = taskJson.getString("date")
            )
            tasks.add(task) // Add the task to the list
        }
        taskAdapter.notifyDataSetChanged() // Notify the adapter that the data has changed
    }

    override fun onResume() {
        super.onResume()
        handler.post(runnable) // Start the periodic fetching when the fragment is resumed
    }

    override fun onPause() {
        super.onPause()
        handler.removeCallbacks(runnable) // Stop the periodic fetching when the fragment is paused
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
