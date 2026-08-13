package com.example.util

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.core.content.FileProvider
import com.example.data.InspeccionConFotos
import java.io.File
import java.io.FileWriter
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

enum class ExportFormat(val extension: String, val mimeType: String, val label: String) {
    CSV("csv", "text/csv", "Tabla CSV (Malla de Puntos)"),
    GEOJSON("geojson", "application/geo+json", "Capa GeoJSON (GIS / QGIS)"),
    HTML_MAP("html", "text/html", "Mapa HTML Local (Visor Interactivo Offline)")
}

object MapExporter {

    fun exportInspecciones(
        context: Context,
        inspecciones: List<InspeccionConFotos>,
        barrioNombre: String?,
        format: ExportFormat
    ): File {
        val exportDir = File(context.filesDir, "exportaciones_malla")
        if (!exportDir.exists()) exportDir.mkdirs()

        val cleanBarrio = (barrioNombre ?: "General")
            .replace("[^a-zA-Z0-9_-]".toRegex(), "_")
            .lowercase(Locale.ROOT)
        val timestamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val fileName = "malla_inspecciones_${cleanBarrio}_$timestamp.${format.extension}"
        val outputFile = File(exportDir, fileName)

        when (format) {
            ExportFormat.CSV -> generateCsv(outputFile, inspecciones)
            ExportFormat.GEOJSON -> generateGeoJson(outputFile, inspecciones, barrioNombre)
            ExportFormat.HTML_MAP -> generateHtmlMap(outputFile, inspecciones, barrioNombre)
        }

        return outputFile
    }

    private fun generateCsv(file: File, list: List<InspeccionConFotos>) {
        FileWriter(file).use { writer ->
            writer.append("CODIGO_FORMULARIO,LATITUD,LONGITUD,DEPARTAMENTO,MUNICIPIO,DIRECCION,BARRIO,LOCALIDAD,HABITABILIDAD,PORCENTAJE_DANO,FECHA,INSPECTOR,ES_PRUEBA\n")
            for (item in list) {
                val ins = item.inspeccion
                val cod = ins.id.take(8).uppercase()
                val lat = String.format(Locale.US, "%.6f", ins.latitud)
                val lon = String.format(Locale.US, "%.6f", ins.longitud)
                val dept = ins.departamento.replace("\"", "'")
                val mun = ins.municipio.replace("\"", "'")
                val dir = ins.direccion.replace("\"", "'")
                val barrio = ins.barrio.replace("\"", "'")
                val loc = ins.localidad.replace("\"", "'")
                val hab = ins.habitabilidad.uppercase()
                val dano = ins.porcentajeDanoGlobal
                val fecha = ins.fechaHora
                val inspector = ins.inspectorLider.replace("\"", "'")
                val esPruebaStr = if (ins.esPrueba) "SI" else "NO"

                writer.append("\"$cod\",$lat,$lon,\"$dept\",\"$mun\",\"$dir\",\"$barrio\",\"$loc\",\"$hab\",$dano,\"$fecha\",\"$inspector\",\"$esPruebaStr\"\n")
            }
        }
    }

    private fun generateGeoJson(file: File, list: List<InspeccionConFotos>, barrioNombre: String?) {
        FileWriter(file).use { writer ->
            writer.append("{\n")
            writer.append("  \"type\": \"FeatureCollection\",\n")
            writer.append("  \"name\": \"Malla_Inspecciones_${barrioNombre ?: "General"}\",\n")
            writer.append("  \"features\": [\n")

            list.forEachIndexed { index, item ->
                val ins = item.inspeccion
                val comma = if (index < list.size - 1) "," else ""
                val cod = ins.id.take(8).uppercase()
                val hab = ins.habitabilidad.uppercase()
                val colorHex = when (hab) {
                    "HABITABLE", "VERDE" -> "#4CAF50"
                    "USO RESTRINGIDO", "AMARILLO" -> "#FFC107"
                    "NO HABITABLE", "NARANJA" -> "#FF9800"
                    "EN COLAPSO", "PELIGRO", "ROJO" -> "#F44336"
                    else -> "#9E9E9E"
                }

                writer.append("    {\n")
                writer.append("      \"type\": \"Feature\",\n")
                writer.append("      \"geometry\": {\n")
                writer.append("        \"type\": \"Point\",\n")
                writer.append("        \"coordinates\": [${ins.longitud}, ${ins.latitud}]\n")
                writer.append("      },\n")
                writer.append("      \"properties\": {\n")
                writer.append("        \"CODIGO_FORMULARIO\": \"$cod\",\n")
                writer.append("        \"DIRECCION\": \"${ins.direccion.escapeJson()}\",\n")
                writer.append("        \"BARRIO\": \"${ins.barrio.escapeJson()}\",\n")
                writer.append("        \"LOCALIDAD\": \"${ins.localidad.escapeJson()}\",\n")
                writer.append("        \"HABITABILIDAD\": \"$hab\",\n")
                writer.append("        \"PORCENTAJE_DANO\": ${ins.porcentajeDanoGlobal},\n")
                writer.append("        \"COLOR\": \"$colorHex\",\n")
                writer.append("        \"INSPECTOR\": \"${ins.inspectorLider.escapeJson()}\",\n")
                writer.append("        \"ES_PRUEBA\": ${ins.esPrueba},\n")
                writer.append("        \"FECHA\": \"${ins.fechaHora}\"\n")
                writer.append("      }\n")
                writer.append("    }$comma\n")
            }

            writer.append("  ]\n")
            writer.append("}\n")
        }
    }

    private fun generateHtmlMap(file: File, list: List<InspeccionConFotos>, barrioNombre: String?) {
        val title = "Malla de Inspecciones Post-Sismo — Barrio ${barrioNombre ?: "General"}"
        val centerLat = list.map { it.inspeccion.latitud }.filter { it != 0.0 }.average().let { if (it.isNaN()) 4.6512 else it }
        val centerLon = list.map { it.inspeccion.longitud }.filter { it != 0.0 }.average().let { if (it.isNaN()) -74.0589 else it }

        val geoJsonPoints = StringBuilder("[")
        list.forEachIndexed { index, item ->
            val ins = item.inspeccion
            val comma = if (index < list.size - 1) "," else ""
            val colorHex = when (ins.habitabilidad.uppercase()) {
                "HABITABLE", "VERDE" -> "#4CAF50"
                "USO RESTRINGIDO", "AMARILLO" -> "#FFC107"
                "NO HABITABLE", "NARANJA" -> "#FF9800"
                "EN COLAPSO", "PELIGRO", "ROJO" -> "#F44336"
                else -> "#9E9E9E"
            }
            geoJsonPoints.append("""
                {
                  "lat": ${ins.latitud},
                  "lng": ${ins.longitud},
                  "cod": "${ins.id.take(8).uppercase()}",
                  "dir": "${ins.direccion.escapeJson()}",
                  "barrio": "${ins.barrio.escapeJson()}",
                  "hab": "${ins.habitabilidad.uppercase()}",
                  "dano": ${ins.porcentajeDanoGlobal},
                  "color": "$colorHex",
                  "fecha": "${ins.fechaHora}"
                }$comma
            """.trimIndent())
        }
        geoJsonPoints.append("]")

        FileWriter(file).use { writer ->
            writer.append("""
                <!DOCTYPE html>
                <html lang="es">
                <head>
                  <meta charset="UTF-8">
                  <meta name="viewport" content="width=device-width, initial-scale=1.0">
                  <title>$title</title>
                  <link rel="stylesheet" href="https://unpkg.com/leaflet@1.9.4/dist/leaflet.css" />
                  <script src="https://unpkg.com/leaflet@1.9.4/dist/leaflet.js"></script>
                  <style>
                    body { margin: 0; padding: 0; font-family: -apple-system, BlinkMacSystemFont, "Segoe UI", Roboto, sans-serif; background: #121212; color: #fff; }
                    #header { padding: 14px 20px; background: #1f1f1f; display: flex; align-items: center; justify-content: space-between; border-bottom: 2px solid #333; }
                    #header h1 { margin: 0; font-size: 18px; color: #4fc3f7; }
                    #header .badge { background: #333; padding: 6px 12px; border-radius: 20px; font-size: 13px; font-weight: bold; }
                    #map { width: 100vw; height: calc(100vh - 58px); }
                    .legend { background: rgba(30, 30, 30, 0.9); padding: 12px; border-radius: 12px; color: #fff; font-size: 12px; line-height: 1.8; box-shadow: 0 4px 12px rgba(0,0,0,0.5); }
                    .legend i { display: inline-block; width: 14px; height: 14px; border-radius: 50%; margin-right: 8px; vertical-align: middle; }
                  </style>
                </head>
                <body>
                  <div id="header">
                    <h1>📍 $title</h1>
                    <div class="badge">Total Puntos: ${list.size}</div>
                  </div>
                  <div id="map"></div>
                  <script>
                    const points = $geoJsonPoints;
                    const map = L.map('map').setView([$centerLat, $centerLon], 14);

                    L.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', {
                      maxZoom: 19,
                      attribution: '© OpenStreetMap - Inspecciones Post-Sismo'
                    }).addTo(map);

                    points.forEach(pt => {
                      if (pt.lat !== 0 && pt.lng !== 0) {
                        const marker = L.circleMarker([pt.lat, pt.lng], {
                          radius: 10,
                          fillColor: pt.color,
                          color: '#ffffff',
                          weight: 2,
                          opacity: 1,
                          fillOpacity: 0.85
                        }).addTo(map);

                        marker.bindPopup(`
                          <div style="font-family: sans-serif; min-width: 200px;">
                            <h3 style="margin: 0 0 6px 0; color: #111;">${'$'}{pt.dir}</h3>
                            <p style="margin: 4px 0; font-size: 12px; color: #555;"><b>Código:</b> ${'$'}{pt.cod}</p>
                            <p style="margin: 4px 0; font-size: 12px; color: #555;"><b>Barrio:</b> ${'$'}{pt.barrio}</p>
                            <p style="margin: 4px 0; font-size: 12px; color: #555;"><b>Daño Global:</b> ${'$'}{pt.dano}%</p>
                            <div style="margin-top: 8px; padding: 4px 8px; background: ${'$'}{pt.color}; color: white; border-radius: 4px; font-weight: bold; text-align: center; font-size: 11px;">
                              ${'$'}{pt.hab}
                            </div>
                          </div>
                        `);
                      }
                    });

                    const legend = L.control({position: 'bottomright'});
                    legend.onAdd = function (map) {
                      const div = L.DomUtil.create('div', 'legend');
                      div.innerHTML = `
                        <b>Gravedad de Habitabilidad</b><br>
                        <i style="background: #4CAF50"></i> Habitable (Verde)<br>
                        <i style="background: #FFC107"></i> Uso Restringido (Amarillo)<br>
                        <i style="background: #FF9800"></i> No Habitable (Naranja)<br>
                        <i style="background: #F44336"></i> En Colapso (Rojo)
                      `;
                      return div;
                    };
                    legend.addTo(map);
                  </script>
                </body>
                </html>
            """.trimIndent())
        }
    }

    fun shareExportedFile(context: Context, file: File, format: ExportFormat) {
        val uri: Uri = FileProvider.getUriForFile(
            context,
            "${context.packageName}.fileprovider",
            file
        )

        val intent = Intent(Intent.ACTION_SEND).apply {
            type = format.mimeType
            putExtra(Intent.EXTRA_STREAM, uri)
            putExtra(Intent.EXTRA_SUBJECT, "Malla de Puntos de Inspecciones Post-Sismo")
            putExtra(Intent.EXTRA_TEXT, "Adjunto archivo ${format.label} generado desde la app de Inspección Post-Sismo.")
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }

        val chooser = Intent.createChooser(intent, "Compartir Malla de Puntos (${format.extension.uppercase()})")
        context.startActivity(chooser)
    }

    private fun String.escapeJson(): String {
        return this.replace("\\", "\\\\")
            .replace("\"", "\\\"")
            .replace("\n", " ")
            .replace("\r", "")
    }
}
