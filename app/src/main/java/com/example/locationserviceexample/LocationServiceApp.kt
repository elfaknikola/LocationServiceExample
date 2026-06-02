package com.example.locationserviceexample

import android.app.Application
import com.example.locationserviceexample.repository.LocationRepository

class LocationServiceApp : Application() {

    lateinit var locationRepository: LocationRepository
        private set

    override fun onCreate() {
        super.onCreate()

        locationRepository = LocationRepository()
    }
}