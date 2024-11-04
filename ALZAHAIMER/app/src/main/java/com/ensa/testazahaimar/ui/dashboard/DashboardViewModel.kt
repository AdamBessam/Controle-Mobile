package com.ensa.testazahaimar.ui.dashboard

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class DashboardViewModel : ViewModel() {

    // LiveData to hold the current location
    private val _location = MutableLiveData<String>().apply {
        value = "Location not available"
    }
    val location: LiveData<String> = _location

    // Method to update location
    fun updateLocation(latitude: Double, longitude: Double) {
        _location.value = "Current Location: \nLatitude: $latitude\nLongitude: $longitude"
    }
}
