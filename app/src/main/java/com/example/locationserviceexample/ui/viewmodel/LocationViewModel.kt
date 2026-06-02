package com.example.locationserviceexample.ui.viewmodel

import androidx.lifecycle.ViewModel
import com.example.locationserviceexample.repository.LocationRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class LocationViewModel(
    locationRepository: LocationRepository
) : ViewModel() {

    val location = locationRepository.location

    private val _hasLocationPermission = MutableStateFlow(false)
    val hasLocationPermission: StateFlow<Boolean> =
        _hasLocationPermission.asStateFlow()

    private val _hasNotificationPermission = MutableStateFlow(false)
    val hasNotificationPermission: StateFlow<Boolean> =
        _hasNotificationPermission.asStateFlow()

    private val _isServiceRunning = MutableStateFlow(false)
    val isServiceRunning: StateFlow<Boolean> =
        _isServiceRunning.asStateFlow()

    fun updatePermissionState(
        hasLocationPermission: Boolean,
        hasNotificationPermission: Boolean
    ) {
        _hasLocationPermission.value = hasLocationPermission
        _hasNotificationPermission.value = hasNotificationPermission
    }

    fun setServiceRunning(isRunning: Boolean) {
        _isServiceRunning.value = isRunning
    }
}