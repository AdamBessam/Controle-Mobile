const Task = require('../models/task'); // Assurez-vous que le chemin est correct

// Récupérer toutes les tâches d'un utilisateur
exports.getTasksByUser = async (req, res) => {
    try {
        const tasks = await Task.find({ userId: req.params.userId });
        res.json(tasks);
    } catch (error) {
        res.status(500).json({ message: 'Erreur lors de la récupération des tâches', error });
    }
};

// Créer une nouvelle tâche
exports.createTask = async (req, res) => {
    const { title, description, date } = req.body; // Inclure le titre

    const task = new Task({
        userId: req.params.userId,
        title, // Ajouter le titre
        description,
        date,
    });

    try {
        const savedTask = await task.save();
        res.status(201).json(savedTask);
    } catch (error) {
        res.status(400).json({ message: 'Erreur lors de la création de la tâche', error });
    }
};

// Mettre à jour une tâche
exports.updateTask = async (req, res) => {
    try {
        const updatedTask = await Task.findByIdAndUpdate(req.params.id, req.body, { new: true });
        if (!updatedTask) {
            return res.status(404).json({ message: 'Tâche non trouvée' });
        }
        res.json(updatedTask);
    } catch (error) {
        res.status(400).json({ message: 'Erreur lors de la mise à jour de la tâche', error });
    }
};

// Supprimer une tâche
exports.deleteTask = async (req, res) => {
    try {
        const deletedTask = await Task.findByIdAndDelete(req.params.id);
        if (!deletedTask) {
            return res.status(404).json({ message: 'Tâche non trouvée' });
        }
        res.status(204).send();
    } catch (error) {
        res.status(500).json({ message: 'Erreur lors de la suppression de la tâche', error });
    }
};
// Récupérer une tâche par son ID
exports.getTaskById = async (req, res) => {
    try {
        const task = await Task.findById(req.params.id);
        if (!task) {
            return res.status(404).json({ message: 'Tâche non trouvée' });
        }
        res.json(task);
    } catch (error) {
        res.status(500).json({ message: 'Erreur lors de la récupération de la tâche', error });
    }
};
