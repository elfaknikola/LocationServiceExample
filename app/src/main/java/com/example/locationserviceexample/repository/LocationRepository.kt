package com.example.locationserviceexample.repository

import com.example.locationserviceexample.model.LocationUpdate
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class LocationRepository {

    private val _location = MutableStateFlow<LocationUpdate?>(null)

    val location: StateFlow<LocationUpdate?> =
        _location.asStateFlow()

    fun updateLocation(locationUpdate: LocationUpdate) {
        _location.value = locationUpdate
    }

    fun clearLocation() {
        _location.value = null
    }
}