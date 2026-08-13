package com.example.util

import android.annotation.SuppressLint
import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import androidx.core.content.ContextCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withTimeoutOrNull
import org.osmdroid.util.GeoPoint
import kotlin.coroutines.resume

object LocationHelper {

    fun hasLocationPermission(context: Context): Boolean {
        val fineLocation = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

        val coarseLocation = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

        return fineLocation || coarseLocation
    }

    fun isGpsEnabled(context: Context): Boolean {
        val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as? LocationManager
        return locationManager?.isProviderEnabled(LocationManager.GPS_PROVIDER) == true ||
               locationManager?.isProviderEnabled(LocationManager.NETWORK_PROVIDER) == true
    }

    suspend fun getCurrentLocation(context: Context): Result<GeoPoint> {
        if (!hasLocationPermission(context)) {
            return Result.failure(SecurityException("Permisos de ubicación no concedidos."))
        }
        if (!isGpsEnabled(context)) {
            return Result.failure(IllegalStateException("El GPS está desactivado en el dispositivo."))
        }

        val geoPoint = withTimeoutOrNull(10000) {
            suspendCancellableCoroutine<GeoPoint?> { continuation ->
                var resumed = false
                fun safeResume(point: GeoPoint?) {
                    if (!resumed) {
                        resumed = true
                        if (continuation.isActive) {
                            continuation.resume(point)
                        }
                    }
                }

                try {
                    val fusedClient = LocationServices.getFusedLocationProviderClient(context)
                    fusedClient.getCurrentLocation(Priority.PRIORITY_HIGH_ACCURACY, null)
                        .addOnSuccessListener { location: Location? ->
                            if (location != null) {
                                safeResume(GeoPoint(location.latitude, location.longitude))
                            } else {
                                fetchLocationFromManager(context) { geo ->
                                    safeResume(geo)
                                }
                            }
                        }
                        .addOnFailureListener {
                            fetchLocationFromManager(context) { geo ->
                                safeResume(geo)
                            }
                        }
                } catch (e: Exception) {
                    fetchLocationFromManager(context) { geo ->
                        safeResume(geo)
                    }
                }
            }
        }

        return if (geoPoint != null) {
            Result.success(geoPoint)
        } else {
            Result.failure(Exception("No se pudo obtener la ubicación. Tiempo de espera agotado."))
        }
    }

    @SuppressLint("MissingPermission")
    fun getCurrentLocationFlow(context: Context): Flow<GeoPoint?> = callbackFlow {
        if (!hasLocationPermission(context)) {
            trySend(null)
            close()
            return@callbackFlow
        }

        var fusedClient: FusedLocationProviderClient? = null
        try {
            fusedClient = LocationServices.getFusedLocationProviderClient(context)
            fusedClient.getCurrentLocation(Priority.PRIORITY_HIGH_ACCURACY, null)
                .addOnSuccessListener { location: Location? ->
                    if (location != null) {
                        trySend(GeoPoint(location.latitude, location.longitude))
                    } else {
                        fetchLocationFromManager(context) { geo ->
                            trySend(geo)
                        }
                    }
                }
                .addOnFailureListener {
                    fetchLocationFromManager(context) { geo ->
                        trySend(geo)
                    }
                }
        } catch (e: Exception) {
            fetchLocationFromManager(context) { geo ->
                trySend(geo)
            }
        }

        awaitClose { }
    }

    @SuppressLint("MissingPermission")
    private fun fetchLocationFromManager(context: Context, onResult: (GeoPoint?) -> Unit) {
        val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as? LocationManager
        if (locationManager == null) {
            onResult(null)
            return
        }

        try {
            val gpsLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)
            val networkLocation = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)

            val bestLocation = gpsLocation ?: networkLocation
            if (bestLocation != null) {
                onResult(GeoPoint(bestLocation.latitude, bestLocation.longitude))
            } else {
                val listener = object : LocationListener {
                    override fun onLocationChanged(location: Location) {
                        onResult(GeoPoint(location.latitude, location.longitude))
                        locationManager.removeUpdates(this)
                    }
                    @Deprecated("Deprecated in Java")
                    override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {}
                    override fun onProviderEnabled(provider: String) {}
                    override fun onProviderDisabled(provider: String) {}
                }

                if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                    locationManager.requestSingleUpdate(LocationManager.GPS_PROVIDER, listener, null)
                } else if (locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
                    locationManager.requestSingleUpdate(LocationManager.NETWORK_PROVIDER, listener, null)
                } else {
                    onResult(null)
                }
            }
        } catch (e: SecurityException) {
            onResult(null)
        }
    }
}
