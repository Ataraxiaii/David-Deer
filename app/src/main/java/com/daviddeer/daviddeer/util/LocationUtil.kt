package com.daviddeer.daviddeer.util

import android.annotation.SuppressLint
import android.content.Context
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle

object LocationUtil {

    @SuppressLint("MissingPermission")
    fun getLocation(
        context: Context,
        onResult: (lat: Double, lon: Double) -> Unit
    ) {
        val locationManager =
            context.getSystemService(Context.LOCATION_SERVICE) as LocationManager

        // First try to use GPS，than use internet location
        val provider = when {
            locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) ->
                LocationManager.GPS_PROVIDER
            locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER) ->
                LocationManager.NETWORK_PROVIDER
            else -> null
        }

        provider ?: return

        val lastLocation = locationManager.getLastKnownLocation(provider)
        lastLocation?.let {
            onResult(it.latitude, it.longitude)
            return
        }

        locationManager.requestSingleUpdate(
            provider,
            object : LocationListener {
                override fun onLocationChanged(location: Location) {
                    onResult(location.latitude, location.longitude)
                }

                override fun onProviderEnabled(provider: String) {}
                override fun onProviderDisabled(provider: String) {}
                override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {}
            },
            null
        )
    }
}
