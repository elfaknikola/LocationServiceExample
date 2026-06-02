package com.example.locationserviceexample

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.locationserviceexample.service.LocationService
import com.example.locationserviceexample.ui.screens.LocationScreen
import com.example.locationserviceexample.ui.theme.LocationServiceExampleTheme
import com.example.locationserviceexample.ui.viewmodel.LocationViewModel

class MainActivity : ComponentActivity() {

    private lateinit var locationViewModel: LocationViewModel

    private val permissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()
        ) { permissions ->
            val hasLocationPermission =
                permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true ||
                        permissions[Manifest.permission.ACCESS_COARSE_LOCATION] == true

            val hasNotificationPermission =
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    permissions[Manifest.permission.POST_NOTIFICATIONS] == true
                } else {
                    true
                }

            locationViewModel.updatePermissionState(
                hasLocationPermission = hasLocationPermission,
                hasNotificationPermission = hasNotificationPermission
            )

            Log.d(TAG, "Location permission: $hasLocationPermission")
            Log.d(TAG, "Notification permission: $hasNotificationPermission")
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            val app = application as LocationServiceApp

            locationViewModel = viewModel(
                factory = viewModelFactory {
                    initializer {
                        LocationViewModel(app.locationRepository)
                    }
                }
            )

            val location by locationViewModel.location.collectAsState()
            val hasLocationPermission by locationViewModel.hasLocationPermission.collectAsState()
            val hasNotificationPermission by locationViewModel.hasNotificationPermission.collectAsState()
            val isServiceRunning by locationViewModel.isServiceRunning.collectAsState()

            LocationServiceExampleTheme {
                LocationScreen(
                    location = location,
                    hasLocationPermission = hasLocationPermission,
                    hasNotificationPermission = hasNotificationPermission,
                    isServiceRunning = isServiceRunning,
                    onRequestPermissions = { requestRequiredPermissions() },
                    onStartService = { startLocationService() },
                    onStopService = { stopLocationService() }
                )
            }
        }
    }

    override fun onResume() {
        super.onResume()

        if (::locationViewModel.isInitialized) {
            refreshPermissionState()
        }
    }

    private fun requestRequiredPermissions() {
        val permissions = buildList {
            add(Manifest.permission.ACCESS_FINE_LOCATION)
            add(Manifest.permission.ACCESS_COARSE_LOCATION)

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                add(Manifest.permission.POST_NOTIFICATIONS)
            }
        }

        permissionLauncher.launch(permissions.toTypedArray())
    }

    private fun refreshPermissionState() {
        val hasLocationPermission =
            ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED ||
                    ContextCompat.checkSelfPermission(
                        this,
                        Manifest.permission.ACCESS_COARSE_LOCATION
                    ) == PackageManager.PERMISSION_GRANTED

        val hasNotificationPermission =
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.POST_NOTIFICATIONS
                ) == PackageManager.PERMISSION_GRANTED
            } else {
                true
            }

        locationViewModel.updatePermissionState(
            hasLocationPermission = hasLocationPermission,
            hasNotificationPermission = hasNotificationPermission
        )
    }

    private fun startLocationService() {
        Log.d(TAG, "Starting location service")

        val intent = Intent(this, LocationService::class.java)
        ContextCompat.startForegroundService(this, intent)

        locationViewModel.setServiceRunning(true)
    }

    private fun stopLocationService() {
        Log.d(TAG, "Stopping location service")

        val intent = Intent(this, LocationService::class.java)
        stopService(intent)

        locationViewModel.setServiceRunning(false)
    }

    companion object {
        private const val TAG = "MainActivity"
    }
}