package com.example.ui.map

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.data.BarrioEntity
import com.example.data.BarrioRepository
import com.example.data.InspeccionConFotos
import com.example.data.InspeccionRepository
import com.example.util.ExportFormat
import com.example.util.MapExporter
import com.example.util.ReverseGeocodingHelper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.osmdroid.util.GeoPoint
import java.io.File

class MapViewModel(
    private val inspeccionRepository: InspeccionRepository,
    private val barrioRepository: BarrioRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(MapState())
    val uiState: StateFlow<MapState> = _uiState.asStateFlow()

    init {
        loadBarrios()
        loadInspecciones()
    }

    private fun loadBarrios() {
        viewModelScope.launch {
            barrioRepository.allBarrios.collect { list ->
                _uiState.update { it.copy(allBarrios = list) }
            }
        }
    }

    private fun loadInspecciones() {
        viewModelScope.launch {
            inspeccionRepository.getInspeccionesGeoreferenciadas().collect { list ->
                _uiState.update { state ->
                    val filtered = applyFilters(list, state.selectedBarrioFilter, state.dataTypeFilter)
                    state.copy(rawInspeccionesList = list, inspecciones = filtered)
                }
            }
        }
    }

    private fun applyFilters(
        list: List<InspeccionConFotos>,
        barrio: BarrioEntity?,
        dataTypeFilter: MapDataTypeFilter
    ): List<InspeccionConFotos> {
        return list.filter { item ->
            val matchesBarrio = if (barrio != null) {
                item.inspeccion.barrioId == barrio.id ||
                        item.inspeccion.barrio.equals(barrio.nombre, ignoreCase = true)
            } else true

            val matchesType = when (dataTypeFilter) {
                MapDataTypeFilter.ALL -> true
                MapDataTypeFilter.REAL_ONLY -> !item.inspeccion.esPrueba
                MapDataTypeFilter.TEST_ONLY -> item.inspeccion.esPrueba
            }

            matchesBarrio && matchesType
        }
    }

    fun setBarrioFilter(barrio: BarrioEntity?) {
        _uiState.update { state ->
            val filtered = applyFilters(state.rawInspeccionesList, barrio, state.dataTypeFilter)
            state.copy(selectedBarrioFilter = barrio, inspecciones = filtered)
        }
    }

    fun setDataTypeFilter(filter: MapDataTypeFilter) {
        _uiState.update { state ->
            val filtered = applyFilters(state.rawInspeccionesList, state.selectedBarrioFilter, filter)
            state.copy(dataTypeFilter = filter, inspecciones = filtered)
        }
    }

    fun setCurrentLocation(geoPoint: GeoPoint) {
        _uiState.update { it.copy(currentLocation = geoPoint) }
    }

    fun selectLocation(context: Context, geoPoint: GeoPoint, isManual: Boolean = true) {
        _uiState.update { it.copy(selectedLocation = geoPoint, isManualSelection = isManual, isLoading = true) }
        viewModelScope.launch {
            val result = ReverseGeocodingHelper.reverseGeocode(
                context = context,
                lat = geoPoint.latitude,
                lon = geoPoint.longitude,
                barrioRepository = barrioRepository
            )
            _uiState.update {
                it.copy(
                    detectedBarrio = result.barrio,
                    localidad = result.localidad,
                    direccionNormalizada = result.direccionNormalizada,
                    isLoading = false
                )
            }
        }
    }

    fun toggleHeatMap() {
        _uiState.update { it.copy(showHeatMap = !it.showHeatMap) }
    }

    fun setShowExportDialog(show: Boolean) {
        _uiState.update { it.copy(showExportDialog = show) }
    }

    fun setExportFormat(format: ExportFormat) {
        _uiState.update { it.copy(selectedExportFormat = format) }
    }

    fun exportMalla(context: Context, onSuccess: (File, ExportFormat) -> Unit) {
        val currentState = _uiState.value
        val list = currentState.inspecciones
        val format = currentState.selectedExportFormat
        val barrioNombre = currentState.selectedBarrioFilter?.nombre

        if (list.isEmpty()) return

        viewModelScope.launch {
            _uiState.update { it.copy(isExporting = true, exportProgress = 0.2f) }
            delay(150)
            _uiState.update { it.copy(exportProgress = 0.6f) }

            val file = withContext(Dispatchers.IO) {
                MapExporter.exportInspecciones(
                    context = context,
                    inspecciones = list,
                    barrioNombre = barrioNombre,
                    format = format
                )
            }

            _uiState.update {
                it.copy(
                    isExporting = false,
                    exportProgress = 1.0f,
                    showExportDialog = false,
                    lastExportedFile = file
                )
            }

            onSuccess(file, format)
        }
    }
}

class MapViewModelFactory(
    private val inspeccionRepository: InspeccionRepository,
    private val barrioRepository: BarrioRepository
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MapViewModel::class.java)) {
            return MapViewModel(inspeccionRepository, barrioRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
