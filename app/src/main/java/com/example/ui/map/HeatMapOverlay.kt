package com.example.ui.map

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Point
import android.graphics.RadialGradient
import android.graphics.Shader
import com.example.data.InspeccionConFotos
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Overlay

class HeatMapOverlay(
    private val inspecciones: List<InspeccionConFotos>
) : Overlay() {

    private val tempPoint = Point()

    override fun draw(canvas: Canvas, mapView: MapView, shadow: Boolean) {
        if (shadow) return
        if (inspecciones.isEmpty()) return

        val projection = mapView.projection
        val mapWidth = mapView.width
        val mapHeight = mapView.height

        for (item in inspecciones) {
            val ins = item.inspeccion
            if (ins.latitud == 0.0 || ins.longitud == 0.0) continue

            val geoPoint = GeoPoint(ins.latitud, ins.longitud)
            projection.toPixels(geoPoint, tempPoint)

            // Check if point is within viewport bounds (with margin)
            if (tempPoint.x < -100 || tempPoint.x > mapWidth + 100 || tempPoint.y < -100 || tempPoint.y > mapHeight + 100) {
                continue
            }

            // Severity weight: percentage of damage global (0..100)
            val damage = ins.porcentajeDanoGlobal.coerceIn(5, 100)
            val weightFactor = damage / 100f

            // Radius scales with map zoom level
            val baseRadius = (35f * (mapView.zoomLevelDouble / 13.0)).toFloat().coerceIn(25f, 90f)
            val radius = baseRadius * (0.8f + 0.4f * weightFactor)

            // Dynamic color gradient based on habitability / severity
            val (centerColor, edgeColor) = when (ins.habitabilidad.uppercase()) {
                "EN COLAPSO", "PELIGRO", "ROJO" -> Pair(Color.argb(180, 244, 67, 54), Color.argb(0, 244, 67, 54))
                "NO HABITABLE", "NARANJA" -> Pair(Color.argb(170, 255, 152, 0), Color.argb(0, 255, 152, 0))
                "USO RESTRINGIDO", "AMARILLO" -> Pair(Color.argb(150, 255, 193, 7), Color.argb(0, 255, 193, 7))
                "HABITABLE", "VERDE" -> Pair(Color.argb(130, 76, 175, 80), Color.argb(0, 76, 175, 80))
                else -> Pair(Color.argb(140, 33, 150, 243), Color.argb(0, 33, 150, 243))
            }

            val paint = Paint().apply {
                isAntiAlias = true
                shader = RadialGradient(
                    tempPoint.x.toFloat(),
                    tempPoint.y.toFloat(),
                    radius,
                    intByteArrayOf(centerColor, edgeColor),
                    floatArrayOf(0.0f, 1.0f),
                    Shader.TileMode.CLAMP
                )
            }

            canvas.drawCircle(tempPoint.x.toFloat(), tempPoint.y.toFloat(), radius, paint)
        }
    }

    private fun intByteArrayOf(c1: Int, c2: Int): IntArray {
        return intArrayOf(c1, c2)
    }
}
