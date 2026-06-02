package com.example.locationserviceexample.service

import android.Manifest
import android.app.Service
import android.content.Intent
import android.content.pm.PackageManager
import android.os.IBinder
import android.util.Log
import androidx.core.app.ActivityCompat
import com.example.locationserviceexample.LocationServiceApp
import com.example.locationserviceexample.model.LocationUpdate
import com.example.locationserviceexample.repository.LocationRepository
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority

class LocationService : Service() {

    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var locationCallback: LocationCallback
    private lateinit var notificationHelper: LocationNotificationHelper
    private lateinit var locationRepository: LocationRepository

    override fun onCreate() {
        super.onCreate()

        Log.d(TAG, "LocationService created")

        locationRepository =
            (application as LocationServiceApp).locationRepository

        notificationHelper = LocationNotificationHelper(this)
        notificationHelper.createNotificationChannel()

        startForeground(
            LocationNotificationHelper.NOTIFICATION_ID,
            notificationHelper.buildNotification("Waiting for location...")
        )

        fusedLocationClient =
            LocationServices.getFusedLocationProviderClient(this)

        setupLocationCallback()
        startLocationUpdates()
    }

    private fun setupLocationCallback() {
        locationCallback = object : LocationCallback() {
            override fun onLocationResult(result: LocationResult) {
                val location = result.lastLocation ?: return

                val update = LocationUpdate(
                    latitude = location.latitude,
                    longitude = location.longitude
                )

                Log.d(TAG, "Location update: $update")

                locationRepository.updateLocation(update)

                notificationHelper.updateNotification(
                    latitude = update.latitude,
                    longitude = update.longitude
                )
            }
        }
    }

    private fun startLocationUpdates() {
        val hasFineLocationPermission =
            ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED

        val hasCoarseLocationPermission =
            ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED

        if (!hasFineLocationPermission && !hasCoarseLocationPermission) {
            Log.d(TAG, "Missing location permission. Stopping service.")
            stopSelf()
            return
        }

        val request = LocationRequest.Builder(
            Priority.PRIORITY_HIGH_ACCURACY,
            5_000L
        )
            .setMinUpdateIntervalMillis(2_000L)
            .build()

        fusedLocationClient.requestLocationUpdates(
            request,
            locationCallback,
            mainLooper
        )

        Log.d(TAG, "Location updates requested")
    }

    override fun onDestroy() {
        Log.d(TAG, "LocationService destroyed")

        if (::fusedLocationClient.isInitialized && ::locationCallback.isInitialized) {
            fusedLocationClient.removeLocationUpdates(locationCallback)
        }

        super.onDestroy()
    }

    override fun onBind(intent: Intent?): IBinder? = null

    companion object {
        private const val TAG = "LocationService"
    }
}