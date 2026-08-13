package com.example.ui.map

import com.example.data.BarrioEntity
import com.example.data.InspeccionConFotos
import com.example.util.ExportFormat
import org.osmdroid.util.GeoPoint
import java.io.File

enum class MapDataTypeFilter { ALL, REAL_ONLY, TEST_ONLY }

data class MapState(
    val currentLocation: GeoPoint? = null,
    val selectedLocation: GeoPoint? = null,
    val detectedBarrio: BarrioEntity? = null,
    val direccionNormalizada: String = "",
    val localidad: String = "",
    val isManualSelection: Boolean = false,
    val allBarrios: List<BarrioEntity> = emptyList(),
    val selectedBarrioFilter: BarrioEntity? = null,
    val dataTypeFilter: MapDataTypeFilter = MapDataTypeFilter.REAL_ONLY,
    val inspecciones: List<InspeccionConFotos> = emptyList(),
    val rawInspeccionesList: List<InspeccionConFotos> = emptyList(),
    val isLoading: Boolean = false,
    // HeatMap and Export State
    val showHeatMap: Boolean = false,
    val showExportDialog: Boolean = false,
    val selectedExportFormat: ExportFormat = ExportFormat.CSV,
    val isExporting: Boolean = false,
    val exportProgress: Float = 0f,
    val lastExportedFile: File? = null
)
