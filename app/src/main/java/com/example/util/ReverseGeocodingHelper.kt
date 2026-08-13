package com.example.util

import android.content.Context
import android.location.Address
import android.location.Geocoder
import com.example.data.BarrioEntity
import com.example.data.BarrioRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.Locale

data class GeocodingResult(
    val barrio: BarrioEntity?,
    val localidad: String,
    val direccionNormalizada: String
)

object ReverseGeocodingHelper {

    suspend fun reverseGeocode(
        context: Context,
        lat: Double,
        lon: Double,
        barrioRepository: BarrioRepository
    ): GeocodingResult = withContext(Dispatchers.IO) {
        val nearestBarrio = barrioRepository.getBarrioByCoordinates(lat, lon)
        var localidad = nearestBarrio?.localidad ?: ""
        var formattedAddress = ""

        try {
            if (Geocoder.isPresent()) {
                val geocoder = Geocoder(context, Locale.getDefault())
                @Suppress("DEPRECATION")
                val addresses: List<Address>? = geocoder.getFromLocation(lat, lon, 1)
                if (!addresses.isNullOrEmpty()) {
                    val address = addresses[0]
                    val thoroughfare = address.thoroughfare ?: ""
                    val subThoroughfare = address.subThoroughfare ?: ""
                    val subLocality = address.subLocality ?: ""
                    val locality = address.locality ?: ""

                    if (localidad.isBlank() && subLocality.isNotBlank()) {
                        localidad = subLocality
                    } else if (localidad.isBlank() && locality.isNotBlank()) {
                        localidad = locality
                    }

                    formattedAddress = when {
                        thoroughfare.isNotBlank() && subThoroughfare.isNotBlank() -> "$thoroughfare #$subThoroughfare"
                        thoroughfare.isNotBlank() -> thoroughfare
                        address.getAddressLine(0) != null -> address.getAddressLine(0)
                        else -> ""
                    }
                }
            }
        } catch (e: Exception) {
            // Offline fallback
        }

        if (formattedAddress.isBlank()) {
            val barrioNombre = nearestBarrio?.nombre ?: "Ubicación Georeferenciada"
            val locNombre = if (localidad.isNotBlank()) localidad else "Bogotá D.C."
            formattedAddress = "$barrioNombre, $locNombre (${String.format(Locale.US, "%.4f, %.4f", lat, lon)})"
        }

        GeocodingResult(
            barrio = nearestBarrio,
            localidad = if (localidad.isNotBlank()) localidad else (nearestBarrio?.localidad ?: "Bogotá D.C."),
            direccionNormalizada = formattedAddress
        )
    }
}
