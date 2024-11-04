package com.ensa.testazahaimar.ui.model

import java.io.Serializable

data class Task(
    val id: String,                // Identifiant unique de la tâche

    val title: String,             // Titre de la tâche
    val description: String,       // Description de la tâche
    var isCompleted: Boolean = false, // Statut de la tâche (complétée ou non)
    val date: String                 // Date de la tâche
)
