const express = require('express');
const mongoose = require('mongoose');
const bodyParser = require('body-parser');
const userRoutes = require('./routes/userRoutes');
const locationRoutes = require('./routes/locationRoutes');
const taskRouter=require('./routes/taskRoutes');
const User=require('./models/User');

const app = express();
const PORT = process.env.PORT || 3000;

// Middleware
app.use(bodyParser.json());
app.post('/authenticate', async (req, res) => {
    const { email, mtp } = req.body; // Changer password par mtp

    try {
        // Vérifiez si l'utilisateur existe avec l'email fourni
        const user = await User.findOne({ email }); // Assurez-vous que `User` est importé

        // Si l'utilisateur n'existe pas
        if (!user) {
            return res.status(404).json({ message: 'Utilisateur non trouvé' });
        }

        // Vérifiez le mtp
        if (mtp !== user.mtp) { // Comparer le mtp fourni avec celui de l'utilisateur
            return res.status(401).json({ message: 'MTP incorrect' });
        }

        // Si tout est bon, renvoyez le rôle de l'utilisateur
        return res.status(200).json({ role: user.role });
    } catch (error) {
        console.error(error);
        return res.status(500).json({ message: 'Erreur du serveur' });
    }
});


// Routes
app.use('/api/users', userRoutes);
app.use('/api/locations', locationRoutes);
app.use('/api/tasks',taskRouter);

// Connexion à la base de données
mongoose.connect('mongodb://localhost:27017/BackendZahaymer', { useNewUrlParser: true, useUnifiedTopology: true })
    .then(() => {
        app.listen(PORT, () => {
            console.log(`Server is running on port ${PORT}`);
        });
    })
    .catch(err => {
        console.error('Database connection error:', err);
    });
