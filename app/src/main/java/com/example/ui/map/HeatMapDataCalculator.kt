package com.example.ui.map

import android.graphics.Color
import com.example.data.InspeccionConFotos

class HeatMapDataCalculator {

    data class WeightedPoint(
        val latitud: Double,
        val longitud: Double,
        val weight: Double // 0.0 (habitable) a 1.0 (colapso)
    )

    fun calculateWeightedPoints(inspecciones: List<InspeccionConFotos>): List<WeightedPoint> {
        return inspecciones.map { item ->
            val weight = when (item.inspeccion.habitabilidad.uppercase()) {
                "HABITABLE", "VERDE" -> 0.1
                "USO RESTRINGIDO", "AMARILLO" -> 0.4
                "NO HABITABLE", "NARANJA" -> 0.7
                "EN COLAPSO", "PELIGRO", "ROJO" -> 1.0
                else -> 0.0
            }
            WeightedPoint(
                latitud = item.inspeccion.latitud,
                longitud = item.inspeccion.longitud,
                weight = weight
            )
        }
    }

    fun interpolateColor(weight: Double): Int {
        // Gradiente de verde (0.0) a rojo (1.0)
        val red = (255 * weight).toInt().coerceIn(0, 255)
        val green = (255 * (1 - weight)).toInt().coerceIn(0, 255)
        return Color.rgb(red, green, 0)
    }
}
