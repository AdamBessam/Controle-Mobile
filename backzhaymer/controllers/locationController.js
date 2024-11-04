const Location = require('../models/Location');
const User = require('../models/User');

// Enregistrer la position actuelle d'un malade
exports.saveCurrentLocation = async (req, res) => {
    const { userId } = req.params; // L'ID de l'utilisateur malade
    console.log("adambessam")
    const { latitude, longitude } = req.body; // Coordonnées

    try {
        const location = new Location({ latitude, longitude, userId });
        await location.save();

        // Mettre à jour la position actuelle de l'utilisateur malade
        await User.findByIdAndUpdate(
            userId,
            { $addToSet: { currentLocation: location._id } }, // Ajoute location._id au tableau currentLocation si ce n'est pas déjà présent
            { new: true } // Renvoie le document modifié
        );
        

        res.status(201).json({ message: "Current location saved successfully" });
    } catch (error) {
        res.status(500).json({ message: "Error saving current location", error });
    }
};

// Enregistrer la localisation de la maison d'un malade
exports.saveHomeLocation = async (req, res) => {
    const { userId } = req.params;
    const { latitude, longitude } = req.body;

    try {
        const location = new Location({ latitude, longitude, userId });
        await location.save();
        await User.findByIdAndUpdate(userId, { homeLocation: location._id });

        res.status(201).json({ message: "Home location saved successfully" });
    } catch (error) {
        res.status(500).json({ message: "Error saving home location", error });
    }
};

// Enregistrer la localisation de l'hôpital d'un malade
exports.saveHospitalLocation = async (req, res) => {
    const { userId } = req.params;
    const { latitude, longitude } = req.body;

    try {
        const location = new Location({ latitude, longitude, userId });
        await location.save();
        await User.findByIdAndUpdate(userId, { hospitalLocation: location._id });

        res.status(201).json({ message: "Hospital location saved successfully" });
    } catch (error) {
        res.status(500).json({ message: "Error saving hospital location", error });
    }
};
// Mettre à jour la position actuelle d'un malade
exports.updateCurrentLocation = async (req, res) => {
    const { userId } = req.params; // L'ID de l'utilisateur malade
    const { latitude, longitude } = req.body; // Coordonnées

    try {
        // Rechercher la localisation actuelle existante
        const existingLocation = await Location.findOne({ userId });

        if (existingLocation) {
            // Si une localisation existe déjà, mettez à jour les coordonnées
            existingLocation.latitude = latitude;
            existingLocation.longitude = longitude;
            await existingLocation.save();

            // Optionnel: Mettre à jour également l'ID de la localisation dans l'utilisateur
            await User.findByIdAndUpdate(userId, { currentLocation: existingLocation._id });

            return res.status(200).json({ message: "Current location updated successfully" });
        } else {
            // Si aucune localisation n'existe, créez-en une nouvelle
            const location = new Location({ latitude, longitude, userId });
            await location.save();

            // Mettre à jour la position actuelle de l'utilisateur malade
            await User.findByIdAndUpdate(userId, { currentLocation: location._id });

            return res.status(201).json({ message: "Current location saved successfully" });
        }
    } catch (error) {
        res.status(500).json({ message: "Error updating current location", error });
    }
};
// Récupérer la localisation actuelle d'un malade
exports.getCurrentLocation = async (req, res) => {
    const { userId } = req.params; // L'ID de l'utilisateur malade

    try {
        const user = await User.findById(userId).populate('currentLocation');
        
        if (!user || !user.currentLocation) {
            return res.status(404).json({ message: "Current location not found" });
        }

        res.status(200).json({ currentLocation: user.currentLocation });
    } catch (error) {
        res.status(500).json({ message: "Error retrieving current location", error });
    }
};

// Récupérer la localisation de la maison d'un malade
exports.getHomeLocation = async (req, res) => {
    const { userId } = req.params; // L'ID de l'utilisateur malade

    try {
        const user = await User.findById(userId).populate('homeLocation');
        
        if (!user || !user.homeLocation) {
            return res.status(404).json({ message: "Home location not found" });
        }

        res.status(200).json({ homeLocation: user.homeLocation });
    } catch (error) {
        res.status(500).json({ message: "Error retrieving home location", error });
    }
};

// Récupérer la localisation de l'hôpital d'un malade
exports.getHospitalLocation = async (req, res) => {
    const { userId } = req.params; // L'ID de l'utilisateur malade

    try {
        const user = await User.findById(userId).populate('hospitalLocation');
        
        if (!user || !user.hospitalLocation) {
            return res.status(404).json({ message: "Hospital location not found" });
        }

        res.status(200).json({ hospitalLocation: user.hospitalLocation });
    } catch (error) {
        res.status(500).json({ message: "Error retrieving hospital location", error });
    }
};
// Mettre à jour la localisation de la maison d'un malade
exports.updateHomeLocation = async (req, res) => {
    const { userId } = req.params; // L'ID de l'utilisateur malade
    const { latitude, longitude } = req.body; // Coordonnées

    try {
        // Rechercher la localisation de la maison existante
        const existingHomeLocation = await Location.findOne({ userId: userId, _id: { $exists: true } });

        if (existingHomeLocation) {
            // Si une localisation existe déjà, mettez à jour les coordonnées
            existingHomeLocation.latitude = latitude;
            existingHomeLocation.longitude = longitude;
            await existingHomeLocation.save();

            // Optionnel: Mettre à jour également l'ID de la localisation dans l'utilisateur
            await User.findByIdAndUpdate(userId, { homeLocation: existingHomeLocation._id });

            return res.status(200).json({ message: "Home location updated successfully" });
        } else {
            // Si aucune localisation n'existe, créez-en une nouvelle
            const location = new Location({ latitude, longitude, userId });
            await location.save();

            // Mettre à jour la localisation de la maison de l'utilisateur malade
            await User.findByIdAndUpdate(userId, { homeLocation: location._id });

            return res.status(201).json({ message: "Home location saved successfully" });
        }
    } catch (error) {
        res.status(500).json({ message: "Error updating home location", error });
    }
};
exports.updateLocationById = async (req, res) => {
    const { locationId } = req.params; // ID de la localisation à mettre à jour
    const { latitude, longitude } = req.body; // Nouvelles coordonnées

    try {
        // Rechercher la localisation existante par ID
        const existingLocation = await Location.findById(locationId);

        if (!existingLocation) {
            return res.status(404).json({ message: "Location not found" });
        }

        // Mettre à jour les coordonnées
        existingLocation.latitude = latitude;
        existingLocation.longitude = longitude;
        await existingLocation.save();

        res.status(200).json({ message: "Location updated successfully" });
    } catch (error) {
        res.status(500).json({ message: "Error updating location", error });
    }
};

// Récupérer la localisation par ID
exports.findLocationById = async (req, res) => {
    const { locationId } = req.params; // L'ID de la localisation
console.log("location id "+locationId);
    try {
        // Rechercher la localisation par ID
        const location = await Location.findById(locationId);

        if (!location) {
            return res.status(404).json({ message: "Location not found" });
        }

        res.status(200).json({ location });
    } catch (error) {
        res.status(500).json({ message: "Error retrieving location", error });
    }
};


exports.updateHospitalLocation = async (req, res) => {
    const  {userId } = req.params;
    const { latitude, longitude } = req.body;
    console.log("id de user est      "+userId);

    try {
        // D'abord, récupérer l'utilisateur pour avoir l'ID de la localisation de l'hôpital
        const user = await User.findById(userId);
        if (!user) {
            return res.status(404).json({ message: "User not found" });
        }

        let location;
        if (user.hospitalLocation) {
            // Si l'utilisateur a déjà une localisation d'hôpital, la mettre à jour
            location = await Location.findById(user.hospitalLocation);
            if (location) {
                location.latitude = latitude;
                location.longitude = longitude;
                await location.save();
            }
        }

        

        res.status(200).json({ 
            message: "Hospital location updated successfully",
            location: location
        });
    } catch (error) {
        console.error('Error in updateHospitalLocation:', error);
        res.status(500).json({ message: "Error updating hospital location", error });
    }
};
exports.updateHospitalLocationn = async (req, res) => {
    console.log("Requête URL complète : " + req.originalUrl);
    const { locationId } = req.params;  // Correction ici, récupérer l'ID de la localisation
    const { latitude, longitude } = req.body;

    console.log("ID de la localisation est : " + locationId);

    try {
        // Récupérer la localisation de l'hôpital en utilisant locationId
        let location = await Location.findById(locationId);
        
        if (!location) {
            return res.status(404).json({ message: "Location not found" });
        }

        // Mise à jour de la localisation de l'hôpital
        location.latitude = latitude;
        location.longitude = longitude;
        
        await location.save();

        res.status(200).json({ 
            message: "Hospital location updated successfully",
            location: location
        });
    } catch (error) {
        console.error('Error in updateHospitalLocation:', error);
        res.status(500).json({ message: "Error updating hospital location", error });
    }
};
