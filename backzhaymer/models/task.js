const mongoose = require('mongoose');

const taskSchema = new mongoose.Schema({
    userId: { type: mongoose.Schema.Types.ObjectId, ref: 'User', required: true }, // Référence à l'utilisateur
    title: { type: String, required: true }, // Nouveau champ pour le titre
    description: { type: String, required: true },
    isCompleted: { type: Boolean, default: false },
    date: { type: String, required: true }, // Date pour la tâche
}, { timestamps: true }); // Ajoute les champs createdAt et updatedAt

module.exports = mongoose.model('Task', taskSchema);
