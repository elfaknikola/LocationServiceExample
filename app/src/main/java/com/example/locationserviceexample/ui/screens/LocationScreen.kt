package com.example.locationserviceexample.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.locationserviceexample.model.LocationUpdate

@Composable
fun LocationScreen(
    location: LocationUpdate?,
    hasLocationPermission: Boolean,
    hasNotificationPermission: Boolean,
    isServiceRunning: Boolean,
    onRequestPermissions: () -> Unit,
    onStartService: () -> Unit,
    onStopService: () -> Unit
) {
    val hasAllPermissions =
        hasLocationPermission && hasNotificationPermission

    val isRequestPermissionsEnabled =
        !hasAllPermissions

    val isStartServiceEnabled =
        hasAllPermissions && !isServiceRunning

    val isStopServiceEnabled =
        isServiceRunning

    val locationText =
        if (location == null) {
            "No location yet"
        } else {
            "Lat: ${location.latitude}\nLng: ${location.longitude}"
        }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = locationText)

        Text(
            text = "Location permission: $hasLocationPermission",
            modifier = Modifier.padding(top = 16.dp)
        )

        Text(
            text = "Notification permission: $hasNotificationPermission",
            modifier = Modifier.padding(top = 4.dp)
        )

        Text(
            text = "Service running: $isServiceRunning",
            modifier = Modifier.padding(top = 4.dp)
        )

        Button(
            onClick = onRequestPermissions,
            enabled = isRequestPermissionsEnabled,
            modifier = Modifier.padding(top = 24.dp)
        ) {
            Text("Request permissions")
        }

        Button(
            onClick = onStartService,
            enabled = isStartServiceEnabled,
            modifier = Modifier.padding(top = 8.dp)
        ) {
            Text("Start location service")
        }

        Button(
            onClick = onStopService,
            enabled = isStopServiceEnabled,
            modifier = Modifier.padding(top = 8.dp)
        ) {
            Text("Stop location service")
        }
    }
}
