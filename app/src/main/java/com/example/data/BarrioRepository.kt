package com.example.data

import com.example.util.HaversineUtil
import kotlinx.coroutines.flow.Flow

class BarrioRepository(private val barrioDao: BarrioDao) {

    val allBarrios: Flow<List<BarrioEntity>> = barrioDao.getAllBarrios()

    suspend fun getBarrioById(id: String): BarrioEntity? = barrioDao.getBarrioById(id)

    fun getBarriosByLocalidad(localidad: String): Flow<List<BarrioEntity>> =
        barrioDao.getBarriosByLocalidad(localidad)

    suspend fun getBarrioByCoordinates(lat: Double, lon: Double, maxDistanceKm: Double = 3.5): BarrioEntity? {
        val barrios = barrioDao.getAllBarriosList()
        if (barrios.isEmpty()) return null

        var closest: BarrioEntity? = null
        var minDistance = Double.MAX_VALUE

        for (barrio in barrios) {
            val dist = HaversineUtil.distanceKm(lat, lon, barrio.latitudCentroide, barrio.longitudCentroide)
            if (dist < minDistance) {
                minDistance = dist
                closest = barrio
            }
        }

        return if (minDistance <= maxDistanceKm) closest else null
    }
}
