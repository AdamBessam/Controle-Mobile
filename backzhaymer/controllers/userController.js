const User = require('../models/User');

const Location=require('../models/Location');

// Fonction pour créer un nouvel utilisateur
exports.createUser = async (req, res) => {
    try {
        const { name, surname, dateOfBirth, phoneNumber, email, homeLocation, hospitalLocation, currentLocation, role, mtp } = req.body;

        const newUser = new User({
            name,
            surname,
            dateOfBirth,
            phoneNumber,
            email,
            role,
            mtp,  // Inclure mtp ici
            homeLocation: homeLocation || null, 
            hospitalLocation: hospitalLocation || null, 
            currentLocation: currentLocation || [] 
        });

        const savedUser = await newUser.save();
        res.status(201).json(savedUser);
    } catch (error) {
        console.error('Erreur lors de la création de l\'utilisateur :', error);
        res.status(500).json({ message: 'Erreur lors de la création de l\'utilisateur.' });
    }
};

// Fonction pour récupérer les patients avec une location actuelle
exports.getPatientsWithCurrentLocation = async (req, res) => {
    try {
        const patients = await User.find({ currentLocation: { $exists: true, $ne: [] } }) // Rechercher les tableaux non vides
            .populate('currentLocation'); // Peupler avec les détails de chaque location dans le tableau

        res.status(200).json(patients);
    } catch (error) {
        console.error('Erreur lors de la récupération des patients avec l\'emplacement actuel :', error);
        res.status(500).json({ message: 'Erreur lors de la récupération des patients avec l\'emplacement actuel.' });
    }
};

// Fonction pour récupérer la home location
exports.getHomeLocation = async (req, res) => {
    try {
        const userId = req.params.id;

        const user = await User.findById(userId).populate('homeLocation');

        if (!user || !user.homeLocation) {
            return res.status(404).json({ message: 'Home location non trouvée pour cet utilisateur.' });
        }

        res.status(200).json(user.homeLocation);
    } catch (error) {
        console.error('Erreur lors de la récupération de la home location :', error);
        res.status(500).json({ message: 'Erreur lors de la récupération de la home location.' });
    }
};

// Fonction pour récupérer la hospital location
exports.getHospitalLocation = async (req, res) => {
    try {
        const userId = req.params.id;

        const user = await User.findById(userId).populate('hospitalLocation');

        if (!user || !user.hospitalLocation) {
            return res.status(404).json({ message: 'Hospital location non trouvée pour cet utilisateur.' });
        }

        res.status(200).json(user.hospitalLocation);
    } catch (error) {
        console.error('Erreur lors de la récupération de la hospital location :', error);
        res.status(500).json({ message: 'Erreur lors de la récupération de la hospital location.' });
    }
};

// Fonction pour récupérer tous les utilisateurs
exports.getAllUsers = async (req, res) => {
    try {
        const users = await User.find(); 

        if (!users || users.length === 0) {
            return res.status(404).json({ message: 'Aucun utilisateur trouvé.' });
        }

        res.status(200).json(users);
    } catch (error) {
        console.error('Erreur lors de la récupération des utilisateurs :', error);
        res.status(500).json({ message: 'Erreur lors de la récupération des utilisateurs.' });
    }
};

// Nouvelle fonction pour trouver un utilisateur par ID
exports.findUserById = async (req, res) => {
    try {
        const userId = req.params.id;
        
        const user = await User.findById(userId)
            .populate('homeLocation')
            .populate('hospitalLocation')
            .populate('currentLocation');
        
        if (!user) {
            return res.status(404).json({ message: 'Utilisateur non trouvé.' });
        }
        
        res.status(200).json(user);
    } catch (error) {
        console.error('Erreur lors de la recherche de l\'utilisateur :', error);
        res.status(500).json({ message: 'Erreur lors de la recherche de l\'utilisateur.' });
    }
};

exports.getCurrentLocationById = async (req, res) => {
    try {
        const userId = req.params.id;

        const user = await User.findById(userId).populate('currentLocation');

        if (!user) {
            return res.status(404).json({ message: 'Utilisateur non trouvé.' });
        }

        if (!user.currentLocation || user.currentLocation.length === 0) {
            return res.status(404).json({ message: 'Aucune location actuelle trouvée pour cet utilisateur.' });
        }

        res.status(200).json(user.currentLocation);
    } catch (error) {
        console.error('Erreur lors de la récupération de la current location :', error);
        res.status(500).json({ message: 'Erreur lors de la récupération de la current location.' });
    }
};


exports.editHomeLocation = async (req, res) => {
    const { userId } = req.params; // Get user ID from params
    const { homeLocationId } = req.body; // Get new home location ID from request body

    try {
        const user = await User.findByIdAndUpdate(
            userId,
            { homeLocation: homeLocationId },
            { new: true } // Return the updated user document
        );

        if (!user) {
            return res.status(404).json({ message: "User not found" });
        }

        return res.status(200).json(user);
    } catch (error) {
        console.error("Error updating home location:", error);
        return res.status(500).json({ message: "Server error" });
    }
};

// Update hospital location
exports.editHospitalLocation = async (req, res) => {
    const { userId } = req.params; // Get user ID from params
    const { hospitalLocationId } = req.body; // Get new hospital location ID from request body

    try {
        const user = await User.findByIdAndUpdate(
            userId,
            { hospitalLocation: hospitalLocationId },
            { new: true } // Return the updated user document
        );

        if (!user) {
            return res.status(404).json({ message: "User not found" });
        }

        return res.status(200).json(user);
    } catch (error) {
        console.error("Error updating hospital location:", error);
        return res.status(500).json({ message: "Server error" });
    }
};
