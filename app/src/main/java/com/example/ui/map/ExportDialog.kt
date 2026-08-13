package com.example.ui.map

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.TableChart
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.util.ExportFormat

@Composable
fun ExportDialog(
    barrioNombre: String?,
    count: Int,
    selectedFormat: ExportFormat,
    isExporting: Boolean,
    exportProgress: Float,
    onFormatSelected: (ExportFormat) -> Unit,
    onConfirmExport: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = { if (!isExporting) onDismiss() },
        title = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Download,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = "Exportar Malla de Puntos",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }
        },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = if (barrioNombre != null) {
                        "Se exportarán $count inspecciones registradas en el barrio $barrioNombre."
                    } else {
                        "Se exportarán $count inspecciones registradas en la ciudad."
                    },
                    style = MaterialTheme.typography.bodyMedium
                )

                Text(
                    text = "Selecciona el formato de exportación:",
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )

                FormatOptionCard(
                    format = ExportFormat.CSV,
                    title = "Tabla CSV",
                    description = "Coordenadas, código, habitabilidad y % de daño para Excel o SIG.",
                    icon = Icons.Default.TableChart,
                    isSelected = selectedFormat == ExportFormat.CSV,
                    onClick = { onFormatSelected(ExportFormat.CSV) }
                )

                FormatOptionCard(
                    format = ExportFormat.GEOJSON,
                    title = "Capa GeoJSON",
                    description = "Formato estándar geoespacial para QGIS, ArcGIS o Google Earth.",
                    icon = Icons.Default.Code,
                    isSelected = selectedFormat == ExportFormat.GEOJSON,
                    onClick = { onFormatSelected(ExportFormat.GEOJSON) }
                )

                FormatOptionCard(
                    format = ExportFormat.HTML_MAP,
                    title = "Mapa HTML Local (Offline)",
                    description = "Archivo web interactivo autocontenido con visor de puntos y capas.",
                    icon = Icons.Default.Map,
                    isSelected = selectedFormat == ExportFormat.HTML_MAP,
                    onClick = { onFormatSelected(ExportFormat.HTML_MAP) }
                )

                AnimatedVisibility(visible = isExporting) {
                    Column(
                        modifier = Modifier.padding(top = 8.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        LinearProgressIndicator(
                            progress = { exportProgress },
                            modifier = Modifier.fillMaxWidth()
                        )
                        Text(
                            text = "Generando archivo...",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.outline
                        )
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = onConfirmExport,
                enabled = !isExporting && count > 0,
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(Icons.Default.Share, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(Modifier.width(6.dp))
                Text("Exportar y Compartir")
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss,
                enabled = !isExporting
            ) {
                Text("Cancelar")
            }
        },
        shape = RoundedCornerShape(20.dp)
    )
}

@Composable
private fun FormatOptionCard(
    format: ExportFormat,
    title: String,
    description: String,
    icon: ImageVector,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(12.dp),
        color = if (isSelected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant,
        contentColor = if (isSelected) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurfaceVariant,
        border = if (isSelected) androidx.compose.foundation.BorderStroke(2.dp, MaterialTheme.colorScheme.primary) else null,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            RadioButton(
                selected = isSelected,
                onClick = onClick
            )
            Icon(icon, contentDescription = null, modifier = Modifier.size(24.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = description,
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }
    }
}
