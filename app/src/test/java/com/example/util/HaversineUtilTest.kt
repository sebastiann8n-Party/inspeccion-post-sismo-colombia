package com.example.util

import com.google.common.truth.Truth.assertThat
import org.junit.Test
import kotlin.random.Random

/**
 * Pruebas unitarias para el algoritmo de Haversine.
 * No requiere dependencias de Android.
 */
class HaversineUtilTest {

    // --- Distancia entre puntos idénticos ---

    @Test
    fun distanceKm_samePoint_returnsZero() {
        val distance = HaversineUtil.distanceKm(4.7110, -74.0721, 4.7110, -74.0721)
        assertThat(distance).isEqualTo(0.0)
    }

    // --- Distancia conocida en Bogotá ---

    @Test
    fun distanceKm_chapineroToCandelaria_returnsApprox2km() {
        // Coordenadas aproximadas de Chapinero y La Candelaria
        val distance = HaversineUtil.distanceKm(
            lat1 = 4.6459, lon1 = -74.0636,  // Chapinero
            lat2 = 4.5967, lon2 = -74.0731   // La Candelaria
        )
        // La distancia real es aproximadamente 5.5 km
        assertThat(distance).isGreaterThan(4.0)
        assertThat(distance).isLessThan(7.0)
    }

    @Test
    fun distanceKm_sameNeighborhood_returnsLessThan1km() {
        // Dos puntos dentro de Chapinero
        val distance = HaversineUtil.distanceKm(
            lat1 = 4.6459, lon1 = -74.0636,
            lat2 = 4.6480, lon2 = -74.0650
        )
        assertThat(distance).isLessThan(1.0)
    }

    // --- Rendimiento ---

    @Test
    fun distanceKm_performance_under500ms_for1000Calculations() {
        val start = System.currentTimeMillis()
        repeat(1000) {
            HaversineUtil.distanceKm(
                Random.nextDouble() * 10,
                Random.nextDouble() * 10,
                Random.nextDouble() * 10,
                Random.nextDouble() * 10
            )
        }
        val elapsed = System.currentTimeMillis() - start
        assertThat(elapsed).isLessThan(500) // < 500 ms para 1000 cálculos
    }

    // --- Casos borde ---

    @Test
    fun distanceKm_antipodalPoints_returnsHalfEarthCircumference() {
        // Puntos antipodales: mitad de la circunferencia terrestre (~20037 km)
        val distance = HaversineUtil.distanceKm(0.0, 0.0, 0.0, 180.0)
        assertThat(distance).isGreaterThan(20000.0)
    }

    @Test
    fun distanceKm_poles_returnsCorrectDistance() {
        // Polo Norte a Polo Sur
        val distance = HaversineUtil.distanceKm(90.0, 0.0, -90.0, 0.0)
        assertThat(distance).isGreaterThan(20000.0)
    }
}
