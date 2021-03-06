package com.antonio.samir.meteoritelandingsspots.util

import android.location.Location
import android.location.LocationListener
import kotlinx.coroutines.flow.Flow

interface GPSTrackerInterface : LocationListener {

    val isLocationAuthorized: Boolean

    val location: Flow<Location?>

    suspend fun startLocationService()

    fun isLocationServiceStarted(): Boolean

    fun isGPSEnabled(): Boolean

    suspend fun stopUpdates()

    val needAuthorization: Flow<Boolean>
}
