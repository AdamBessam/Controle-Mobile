const express = require('express');
const router = express.Router();
const { saveHomeLocation,updateHospitalLocationn,updateHomeLocation,updateHospitalLocation,findLocationById, updateLocationById,saveHospitalLocation, saveCurrentLocation ,updateCurrentLocation,getHospitalLocation,getHomeLocation,getCurrentLocation} = require('../controllers/locationController');

router.post('/:userId/home', saveHomeLocation);
router.post('/:userId/hospital', saveHospitalLocation);
router.post('/:userId/current', saveCurrentLocation); 
router.put('/:userId/current', updateCurrentLocation);
router.get('/:userId/current', getCurrentLocation);
router.get('/:userId/home', getHomeLocation);
router.get('/:userId/hospital', getHospitalLocation);
router.put('/:userId/home-location', updateHomeLocation);
router.put('/:locationId', updateLocationById);

router.get('/:locationId', findLocationById);

router.put('/:userId/hospital-location',updateHospitalLocation);
router.put('/:id/hospital',updateHospitalLocationn);





module.exports = router;
