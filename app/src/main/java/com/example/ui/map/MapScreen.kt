package com.example.ui.map

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color as AndroidColor
import android.graphics.drawable.Drawable
import android.graphics.drawable.GradientDrawable
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.Layers
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import com.example.ui.components.GpsActivationButton
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.BarrioEntity
import com.example.data.InspeccionConFotos
import com.example.util.LocationHelper
import com.example.util.MapExporter
import kotlinx.coroutines.launch
import org.osmdroid.config.Configuration
import org.osmdroid.events.MapEventsReceiver
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.MapEventsOverlay
import org.osmdroid.views.overlay.Marker
import java.io.File
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MapScreen(
    viewModel: MapViewModel,
    isSelectionMode: Boolean = false,
    initialLat: Double = 0.0,
    initialLon: Double = 0.0,
    onBack: () -> Unit,
    onConfirmLocation: ((lat: Double, lon: Double, barrioId: String?, barrioNombre: String, codigoBarrio: String, localidad: String, direccion: String) -> Unit)? = null,
    onSelectInspeccion: ((id: String) -> Unit)? = null
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    var selectedInspeccion by remember { mutableStateOf<InspeccionConFotos?>(null) }
    var expandedBarrioMenu by remember { mutableStateOf(false) }

    // Configure osmdroid user agent and tile cache path
    LaunchedEffect(Unit) {
        Configuration.getInstance().userAgentValue = context.packageName
        val tileCache = File(context.cacheDir, "osmdroid/tiles")
        if (!tileCache.exists()) tileCache.mkdirs()
        Configuration.getInstance().osmdroidTileCache = tileCache

        if (initialLat != 0.0 && initialLon != 0.0) {
            val initialGeo = GeoPoint(initialLat, initialLon)
            viewModel.selectLocation(context, initialGeo, isManual = false)
        }
    }

    // Permission launcher for GPS location
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val granted = permissions.values.any { it }
        if (granted) {
            LocationHelper.getCurrentLocationFlow(context)
        }
    }

    LaunchedEffect(Unit) {
        if (!LocationHelper.hasLocationPermission(context)) {
            permissionLauncher.launch(
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )
            )
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            if (isSelectionMode) "Seleccionar Ubicación" else "Mapa e Inspecciones por Barrio",
                            fontWeight = FontWeight.Bold,
                            style = MaterialTheme.typography.titleMedium
                        )
                        if (!isSelectionMode) {
                            val barrioName = uiState.selectedBarrioFilter?.nombre ?: "Todas las Zonas"
                            Text(
                                text = "$barrioName (${uiState.inspecciones.size} Puntos)",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
                    }
                },
                actions = {
                    if (!isSelectionMode) {
                        // Heatmap toggle button
                        FilledIconToggleButton(
                            checked = uiState.showHeatMap,
                            onCheckedChange = { viewModel.toggleHeatMap() },
                            colors = IconButtonDefaults.filledIconToggleButtonColors(
                                checkedContainerColor = MaterialTheme.colorScheme.errorContainer,
                                checkedContentColor = MaterialTheme.colorScheme.onErrorContainer
                            )
                        ) {
                            Icon(Icons.Default.Layers, contentDescription = "Mapa de Calor")
                        }

                        // Export Malla button
                        IconButton(
                            onClick = { viewModel.setShowExportDialog(true) },
                            enabled = uiState.inspecciones.isNotEmpty()
                        ) {
                            Icon(Icons.Default.Download, contentDescription = "Exportar Puntos")
                        }

                        // Barrio folder filter dropdown
                        Box {
                            IconButton(onClick = { expandedBarrioMenu = true }) {
                                BadgedBox(
                                    badge = {
                                        if (uiState.selectedBarrioFilter != null) {
                                            Badge { Text("1") }
                                        }
                                    }
                                ) {
                                    Icon(Icons.Default.Folder, contentDescription = "Filtrar por Barrio")
                                }
                            }
                            DropdownMenu(
                                expanded = expandedBarrioMenu,
                                onDismissRequest = { expandedBarrioMenu = false }
                            ) {
                                DropdownMenuItem(
                                    text = { Text("📂 Todas las Inspecciones") },
                                    onClick = {
                                        viewModel.setBarrioFilter(null)
                                        expandedBarrioMenu = false
                                    },
                                    leadingIcon = {
                                        if (uiState.selectedBarrioFilter == null) {
                                            Icon(Icons.Default.Check, contentDescription = null)
                                        }
                                    }
                                )
                                HorizontalDivider()
                                uiState.allBarrios.forEach { barrio ->
                                    DropdownMenuItem(
                                        text = { Text("${barrio.nombre} (${barrio.localidad})") },
                                        onClick = {
                                            viewModel.setBarrioFilter(barrio)
                                            expandedBarrioMenu = false
                                        },
                                        leadingIcon = {
                                            if (uiState.selectedBarrioFilter?.id == barrio.id) {
                                                Icon(Icons.Default.Check, contentDescription = null)
                                            }
                                        }
                                    )
                                }
                            }
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        },
        floatingActionButton = {
            Column(
                verticalArrangement = Arrangement.spacedBy(10.dp),
                horizontalAlignment = Alignment.End
            ) {
                if (!isSelectionMode && uiState.inspecciones.isNotEmpty()) {
                    ExtendedFloatingActionButton(
                        onClick = { viewModel.setShowExportDialog(true) },
                        icon = { Icon(Icons.Default.Share, contentDescription = null) },
                        text = { Text("Exportar Malla") },
                        containerColor = MaterialTheme.colorScheme.secondaryContainer,
                        contentColor = MaterialTheme.colorScheme.onSecondaryContainer
                    )
                }

                GpsActivationButton(
                    onLocationObtained = { geo ->
                        viewModel.setCurrentLocation(geo)
                        viewModel.selectLocation(context, geo, isManual = false)
                    },
                    isFab = true
                )
            }
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // OSMMapView with overlays
            OSMMapViewContainer(
                uiState = uiState,
                isSelectionMode = isSelectionMode,
                onLocationSelected = { geoPoint ->
                    viewModel.selectLocation(context, geoPoint, isManual = true)
                },
                onInspeccionSelected = { inspeccion ->
                    selectedInspeccion = inspeccion
                }
            )

            // Floating Data Type Filter (Reales vs Pruebas) in Explorer mode
            if (!isSelectionMode) {
                val realCount = remember(uiState.rawInspeccionesList) { uiState.rawInspeccionesList.count { !it.inspeccion.esPrueba } }
                val testCount = remember(uiState.rawInspeccionesList) { uiState.rawInspeccionesList.count { it.inspeccion.esPrueba } }

                Surface(
                    modifier = Modifier
                        .align(Alignment.TopCenter)
                        .padding(top = 12.dp),
                    shape = RoundedCornerShape(24.dp),
                    color = MaterialTheme.colorScheme.surface.copy(alpha = 0.95f),
                    shadowElevation = 6.dp
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        FilterChip(
                            selected = uiState.dataTypeFilter == MapDataTypeFilter.REAL_ONLY,
                            onClick = { viewModel.setDataTypeFilter(MapDataTypeFilter.REAL_ONLY) },
                            label = { Text("📋 Reales ($realCount)", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold) }
                        )
                        FilterChip(
                            selected = uiState.dataTypeFilter == MapDataTypeFilter.TEST_ONLY,
                            onClick = { viewModel.setDataTypeFilter(MapDataTypeFilter.TEST_ONLY) },
                            label = { Text("🧪 Pruebas ($testCount)", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold) }
                        )
                        FilterChip(
                            selected = uiState.dataTypeFilter == MapDataTypeFilter.ALL,
                            onClick = { viewModel.setDataTypeFilter(MapDataTypeFilter.ALL) },
                            label = { Text("Todas (${uiState.rawInspeccionesList.size})", style = MaterialTheme.typography.labelMedium) }
                        )
                    }
                }
            }

            // Instruction Banner in Selection Mode
            if (isSelectionMode) {
                Surface(
                    modifier = Modifier
                        .align(Alignment.TopCenter)
                        .padding(top = 12.dp, start = 16.dp, end = 16.dp),
                    shape = RoundedCornerShape(24.dp),
                    color = MaterialTheme.colorScheme.primaryContainer,
                    contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                    tonalElevation = 6.dp
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(Icons.Default.Place, contentDescription = null, modifier = Modifier.size(18.dp))
                        Text(
                            text = "Toca cualquier punto del mapa para ubicar la edificación",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            // Active Barrio Folder Filter Badge & Heatmap status badge
            if (!isSelectionMode) {
                Column(
                    modifier = Modifier
                        .align(Alignment.TopCenter)
                        .padding(top = 12.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    if (uiState.selectedBarrioFilter != null) {
                        Surface(
                            shape = RoundedCornerShape(24.dp),
                            color = MaterialTheme.colorScheme.primary,
                            contentColor = MaterialTheme.colorScheme.onPrimary,
                            tonalElevation = 6.dp
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Icon(Icons.Default.Folder, contentDescription = null, modifier = Modifier.size(18.dp))
                                Text(
                                    text = "Barrio: ${uiState.selectedBarrioFilter?.nombre}",
                                    style = MaterialTheme.typography.labelLarge,
                                    fontWeight = FontWeight.Bold
                                )
                                TextButton(
                                    onClick = { viewModel.setBarrioFilter(null) },
                                    colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colorScheme.onPrimary)
                                ) {
                                    Text("Limpiar", style = MaterialTheme.typography.labelSmall)
                                }
                            }
                        }
                    }

                    if (uiState.showHeatMap) {
                        Surface(
                            shape = RoundedCornerShape(20.dp),
                            color = MaterialTheme.colorScheme.errorContainer,
                            contentColor = MaterialTheme.colorScheme.onErrorContainer,
                            tonalElevation = 6.dp
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 14.dp, vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Icon(Icons.Default.Layers, contentDescription = null, modifier = Modifier.size(16.dp))
                                Text(
                                    text = "🔥 Mapa de Calor / Gravedad Activo",
                                    style = MaterialTheme.typography.labelMedium,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }
            }

            // Bottom Location Card in Selection Mode
            if (isSelectionMode) {
                AnimatedVisibility(
                    visible = uiState.selectedLocation != null,
                    enter = slideInVertically { it } + fadeIn(),
                    exit = slideOutVertically { it } + fadeOut(),
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .fillMaxWidth()
                        .navigationBarsPadding()
                        .padding(16.dp)
                ) {
                    Card(
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Icon(
                                    Icons.Default.LocationOn,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.primary
                                )
                                Text(
                                    "Ubicación de la Edificación",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold
                                )
                            }

                            if (uiState.isLoading) {
                                LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
                            }

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = uiState.direccionNormalizada.ifBlank { "Coordenadas seleccionadas" },
                                        style = MaterialTheme.typography.bodyMedium,
                                        fontWeight = FontWeight.Medium,
                                        maxLines = 2,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                    val latStr = String.format(Locale.US, "%.5f", uiState.selectedLocation?.latitude ?: 0.0)
                                    val lonStr = String.format(Locale.US, "%.5f", uiState.selectedLocation?.longitude ?: 0.0)
                                    Text(
                                        text = "Lat: $latStr | Lon: $lonStr",
                                        style = MaterialTheme.typography.labelMedium,
                                        color = MaterialTheme.colorScheme.outline
                                    )
                                }

                                uiState.detectedBarrio?.let { barrio ->
                                    Surface(
                                        shape = RoundedCornerShape(12.dp),
                                        color = MaterialTheme.colorScheme.secondaryContainer,
                                        contentColor = MaterialTheme.colorScheme.onSecondaryContainer,
                                        modifier = Modifier.padding(start = 8.dp)
                                    ) {
                                        Text(
                                            text = barrio.nombre,
                                            style = MaterialTheme.typography.labelLarge,
                                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                }
                            }

                            Button(
                                onClick = {
                                    val loc = uiState.selectedLocation
                                    if (loc != null) {
                                        val barrio = uiState.detectedBarrio
                                        onConfirmLocation?.invoke(
                                            loc.latitude,
                                            loc.longitude,
                                            barrio?.id,
                                            barrio?.nombre ?: "",
                                            barrio?.id ?: "",
                                            uiState.localidad,
                                            uiState.direccionNormalizada
                                        )
                                    }
                                },
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Icon(Icons.Default.Check, contentDescription = null)
                                Spacer(Modifier.width(8.dp))
                                Text("Confirmar Ubicación")
                            }
                        }
                    }
                }
            }

            // Bottom Selected Inspection Details Card in Explorer Mode
            if (!isSelectionMode && selectedInspeccion != null) {
                val ins = selectedInspeccion!!.inspeccion
                val habitabilityColor = when (ins.habitabilidad.uppercase()) {
                    "HABITABLE", "VERDE" -> Color(0xFF2E7D32)
                    "USO RESTRINGIDO", "AMARILLO" -> Color(0xFFF57C00)
                    "NO HABITABLE", "NARANJA" -> Color(0xFFE65100)
                    "EN COLAPSO", "PELIGRO", "ROJO" -> Color(0xFFC62828)
                    else -> Color.Gray
                }

                Card(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .fillMaxWidth()
                        .navigationBarsPadding()
                        .padding(16.dp),
                    shape = RoundedCornerShape(20.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        if (ins.esPrueba) {
                            Surface(
                                shape = RoundedCornerShape(4.dp),
                                color = MaterialTheme.colorScheme.tertiary,
                                modifier = Modifier.padding(bottom = 4.dp)
                            ) {
                                Text(
                                    text = "🧪 REGISTRO DE PRUEBA / SIMULACIÓN",
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onTertiary,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                                )
                            }
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = ins.direccion.ifBlank { "Sin Dirección" },
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "Barrio: ${ins.barrio.ifBlank { "Desconocido" }} (${ins.localidad})",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = habitabilityColor,
                                contentColor = Color.White
                            ) {
                                Text(
                                    text = ins.habitabilidad.ifBlank { "EVALUADO" },
                                    style = MaterialTheme.typography.labelMedium,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                                )
                            }
                        }

                        HorizontalDivider()

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Código: ${ins.id.take(8).uppercase()} | Daño: ${ins.porcentajeDanoGlobal}%",
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.outline
                            )
                            Button(
                                onClick = { onSelectInspeccion?.invoke(ins.id) },
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Icon(Icons.Default.Visibility, contentDescription = null, modifier = Modifier.size(18.dp))
                                Spacer(Modifier.width(6.dp))
                                Text("Ver Detalle")
                            }
                        }
                    }
                }
            }

            // Export Dialog
            if (uiState.showExportDialog) {
                ExportDialog(
                    barrioNombre = uiState.selectedBarrioFilter?.nombre,
                    count = uiState.inspecciones.size,
                    selectedFormat = uiState.selectedExportFormat,
                    isExporting = uiState.isExporting,
                    exportProgress = uiState.exportProgress,
                    onFormatSelected = { viewModel.setExportFormat(it) },
                    onConfirmExport = {
                        viewModel.exportMalla(context) { file, format ->
                            MapExporter.shareExportedFile(context, file, format)
                        }
                    },
                    onDismiss = { viewModel.setShowExportDialog(false) }
                )
            }
        }
    }
}

@SuppressLint("ClickableViewAccessibility")
@Composable
private fun OSMMapViewContainer(
    uiState: MapState,
    isSelectionMode: Boolean,
    onLocationSelected: (GeoPoint) -> Unit,
    onInspeccionSelected: (InspeccionConFotos) -> Unit
) {
    val context = LocalContext.current

    AndroidView(
        factory = { ctx ->
            MapView(ctx).apply {
                setTileSource(TileSourceFactory.MAPNIK)
                setMultiTouchControls(true)
                zoomController.setVisibility(org.osmdroid.views.CustomZoomButtonsController.Visibility.ALWAYS)

                val startGeo = uiState.selectedLocation ?: GeoPoint(4.6512, -74.0589)
                controller.setZoom(13.0)
                controller.setCenter(startGeo)

                // Map tap and long-press listener for marker placement
                val eventsOverlay = MapEventsOverlay(object : MapEventsReceiver {
                    override fun singleTapConfirmedHelper(p: GeoPoint?): Boolean {
                        p?.let { point ->
                            onLocationSelected(point)
                        }
                        return true
                    }

                    override fun longPressHelper(p: GeoPoint?): Boolean {
                        p?.let { point ->
                            onLocationSelected(point)
                        }
                        return true
                    }
                })
                overlays.add(eventsOverlay)
            }
        },
        update = { mapView ->
            mapView.overlays.removeAll { it is Marker || it is HeatMapOverlay }

            // 0. HeatMap Overlay if active
            if (uiState.showHeatMap && !isSelectionMode) {
                val heatMapOverlay = HeatMapOverlay(uiState.inspecciones)
                mapView.overlays.add(0, heatMapOverlay)
            }

            // 1. Current Inspector Location Marker (Blue)
            uiState.currentLocation?.let { currentGeo ->
                val currentMarker = Marker(mapView).apply {
                    position = currentGeo
                    title = "Mi Ubicación"
                    setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
                }
                mapView.overlays.add(currentMarker)
            }

            // 2. Selected Location Red Marker
            uiState.selectedLocation?.let { selectedGeo ->
                val selectedMarker = Marker(mapView).apply {
                    position = selectedGeo
                    title = "Ubicación de la Edificación"
                    isDraggable = true
                    setOnMarkerDragListener(object : Marker.OnMarkerDragListener {
                        override fun onMarkerDrag(marker: Marker?) {}
                        override fun onMarkerDragStart(marker: Marker?) {}
                        override fun onMarkerDragEnd(marker: Marker?) {
                            marker?.position?.let { newPos ->
                                onLocationSelected(newPos)
                            }
                        }
                    })
                    setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
                }
                mapView.overlays.add(selectedMarker)

                if (uiState.isManualSelection) {
                    mapView.controller.animateTo(selectedGeo)
                }
            }

            // 3. Inspections Markers (Colored by Habitability) in Explorer mode
            if (!isSelectionMode) {
                uiState.inspecciones.forEach { inspeccionConFotos ->
                    val ins = inspeccionConFotos.inspeccion
                    if (ins.latitud != 0.0 && ins.longitud != 0.0) {
                        val pos = GeoPoint(ins.latitud, ins.longitud)

                        val colorInt = when (ins.habitabilidad.uppercase()) {
                            "HABITABLE", "VERDE" -> AndroidColor.rgb(76, 175, 80)
                            "USO RESTRINGIDO", "AMARILLO" -> AndroidColor.rgb(255, 193, 7)
                            "NO HABITABLE", "NARANJA" -> AndroidColor.rgb(255, 152, 0)
                            "EN COLAPSO", "PELIGRO", "ROJO" -> AndroidColor.rgb(244, 67, 54)
                            else -> AndroidColor.rgb(158, 158, 158)
                        }

                        val marker = Marker(mapView).apply {
                            position = pos
                            title = ins.direccion.ifBlank { "Inspección" }
                            snippet = "Habitabilidad: ${ins.habitabilidad} | Barrio: ${ins.barrio} | Daño: ${ins.porcentajeDanoGlobal}%"
                            icon = createColoredMarkerDrawable(colorInt)
                            setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_CENTER)
                            setOnMarkerClickListener { m, _ ->
                                onInspeccionSelected(inspeccionConFotos)
                                m.showInfoWindow()
                                true
                            }
                        }
                        mapView.overlays.add(marker)
                    }
                }
            }

            mapView.invalidate()
        },
        modifier = Modifier.fillMaxSize()
    )
}

private fun createColoredMarkerDrawable(colorInt: Int): Drawable {
    return GradientDrawable().apply {
        shape = GradientDrawable.OVAL
        setColor(colorInt)
        setStroke(4, AndroidColor.WHITE)
        setSize(48, 48)
    }
}
