const express = require('express');
const router = express.Router();
const taskController = require('../controllers/taskController'); // Assurez-vous que le chemin est correct

// Récupérer toutes les tâches d'un utilisateur
router.get('/:userId/tasks', taskController.getTasksByUser);

// Créer une nouvelle tâche
router.post('/:userId/tasks', taskController.createTask);

// Mettre à jour une tâche
router.put('/tasks/:id', taskController.updateTask);

// Supprimer une tâche
router.delete('/:id', taskController.deleteTask);

router.get('/tasks/:id', taskController.getTaskById);

module.exports = router;
