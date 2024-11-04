const express = require('express');
const router = express.Router();
const { createUser,getCurrentLocationById, getPatientsWithCurrentLocation,findUserById, getHomeLocation, getHospitalLocation ,getAllUsers,editHospitalLocation,editHomeLocation} = require('../controllers/userController');


router.get('/:id', findUserById);
// Route pour créer un utilisateur
router.post('/', createUser);

router.get('/:id/current-location', getCurrentLocationById);

// Route pour récupérer les patients avec leur location actuelle
router.get('/patients/current-location', getPatientsWithCurrentLocation);

// Route pour récupérer la hospitalLocation d'un utilisateur
router.get('/:id/hospital-location', getHospitalLocation);

// Route pour récupérer la homeLocation d'un utilisateur
router.get('/:id/home-location', getHomeLocation);
router.get('/users', getAllUsers);


router.put('/:userId/home-location', editHomeLocation);

// Update hospital location
//router.put('/:userId/hospital-location', editHospitalLocation);



module.exports = router;
