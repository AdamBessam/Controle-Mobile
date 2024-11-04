const mongoose = require('mongoose');

const userSchema = new mongoose.Schema({
    name: { type: String, required: true },
    mtp: { type: String, required: true },
    surname: { type: String, required: true },
    dateOfBirth: { type: Date, required: true },
    phoneNumber: { type: String, required: true },
    email: { type: String, required: true, unique: true },
    homeLocation: { type: mongoose.Schema.Types.ObjectId, ref: 'Location',required:false },
    hospitalLocation: { type: mongoose.Schema.Types.ObjectId, ref: 'Location' ,required:false},
    currentLocation: [{ type: mongoose.Schema.Types.ObjectId, ref: 'Location' ,required:false}],
    role: {type: String,enum: ['malade', 'responsable'], required: true},
});

module.exports = mongoose.model('User', userSchema);
