package com.example.ui.home

import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.PictureAsPdf
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.InspeccionConFotos
import com.example.utils.ReportGenerator
import com.example.utils.ShareUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.*

import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.Place

import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.text.font.FontWeight

enum class DataTypeFilter { ALL, REAL_ONLY, TEST_ONLY }

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    viewModel: HomeViewModel,
    onNavigateToForm: (String?) -> Unit,
    onNavigateToMapExplorer: () -> Unit = {}
) {
    val inspecciones by viewModel.inspecciones.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    var dataTypeFilter by remember { mutableStateOf(DataTypeFilter.REAL_ONLY) }

    val filteredInspecciones = remember(inspecciones, dataTypeFilter) {
        when (dataTypeFilter) {
            DataTypeFilter.ALL -> inspecciones
            DataTypeFilter.REAL_ONLY -> inspecciones.filter { !it.inspeccion.esPrueba }
            DataTypeFilter.TEST_ONLY -> inspecciones.filter { it.inspeccion.esPrueba }
        }
    }

    val realCount = remember(inspecciones) { inspecciones.count { !it.inspeccion.esPrueba } }
    val testCount = remember(inspecciones) { inspecciones.count { it.inspeccion.esPrueba } }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Inspecciones Post-Sismo") },
                actions = {
                    IconButton(onClick = onNavigateToMapExplorer) {
                        Icon(Icons.Filled.Map, contentDescription = "Mapa y Barrios")
                    }
                    IconButton(onClick = {
                        coroutineScope.launch {
                            try {
                                val csvFile = withContext(Dispatchers.IO) {
                                    ReportGenerator.generateCsv(context, filteredInspecciones)
                                }
                                ShareUtils.shareFile(context, csvFile, "text/csv")
                            } catch (e: Exception) {
                                Toast.makeText(context, "Error exportando CSV", Toast.LENGTH_SHORT).show()
                            }
                        }
                    }) {
                        Icon(Icons.Filled.Share, contentDescription = "Exportar CSV")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { onNavigateToForm(null) },
                modifier = Modifier.navigationBarsPadding()
            ) {
                Icon(Icons.Filled.Add, contentDescription = "Nueva Inspección")
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Filter Bar to isolate Test vs Real Data
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                FilterChip(
                    selected = dataTypeFilter == DataTypeFilter.REAL_ONLY,
                    onClick = { dataTypeFilter = DataTypeFilter.REAL_ONLY },
                    label = { Text("📋 Reales ($realCount)", fontWeight = FontWeight.Bold) }
                )
                FilterChip(
                    selected = dataTypeFilter == DataTypeFilter.TEST_ONLY,
                    onClick = { dataTypeFilter = DataTypeFilter.TEST_ONLY },
                    label = { Text("🧪 Pruebas ($testCount)", fontWeight = FontWeight.Bold) }
                )
                FilterChip(
                    selected = dataTypeFilter == DataTypeFilter.ALL,
                    onClick = { dataTypeFilter = DataTypeFilter.ALL },
                    label = { Text("Todas (${inspecciones.size})") }
                )
            }

            if (filteredInspecciones.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .weight(1f),
                    contentAlignment = androidx.compose.ui.Alignment.Center
                ) {
                    Text(
                        when (dataTypeFilter) {
                            DataTypeFilter.REAL_ONLY -> "No hay evaluaciones reales registradas"
                            DataTypeFilter.TEST_ONLY -> "No hay evaluaciones de prueba registradas"
                            DataTypeFilter.ALL -> "No hay inspecciones registradas"
                        },
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .weight(1f),
                    contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 88.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    items(filteredInspecciones) { item ->
                        InspeccionCard(
                            inspeccion = item,
                            onClick = { onNavigateToForm(item.inspeccion.id) },
                            onSharePdf = {
                                coroutineScope.launch {
                                    try {
                                        val pdfFile = withContext(Dispatchers.IO) {
                                            ReportGenerator.generatePdf(context, item)
                                        }
                                        ShareUtils.shareFile(context, pdfFile, "application/pdf")
                                    } catch (e: Exception) {
                                        Toast.makeText(context, "Error generando PDF", Toast.LENGTH_SHORT).show()
                                    }
                                }
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun InspeccionCard(
    inspeccion: InspeccionConFotos,
    onClick: () -> Unit,
    onSharePdf: () -> Unit
) {
    val date = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date(inspeccion.inspeccion.fechaHora))
    val isTest = inspeccion.inspeccion.esPrueba

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        colors = CardDefaults.cardColors(
            containerColor = if (isTest) MaterialTheme.colorScheme.tertiaryContainer.copy(alpha = 0.4f) else MaterialTheme.colorScheme.surface
        )
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            if (isTest) {
                Surface(
                    shape = androidx.compose.foundation.shape.RoundedCornerShape(4.dp),
                    color = MaterialTheme.colorScheme.tertiary,
                    modifier = Modifier.padding(bottom = 8.dp)
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

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text(text = "Fecha: $date", style = MaterialTheme.typography.labelMedium)
                Text(
                    text = inspeccion.inspeccion.estado,
                    style = MaterialTheme.typography.labelMedium,
                    color = if (inspeccion.inspeccion.estado == "COMPLETADO") Color(0xFF4CAF50) else Color(0xFFFF9800)
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = inspeccion.inspeccion.direccion.ifEmpty { "Sin dirección" }, style = MaterialTheme.typography.titleMedium)
            Text(text = "Habitabilidad: ${inspeccion.inspeccion.habitabilidad.ifEmpty { "No evaluada" }}", style = MaterialTheme.typography.bodyMedium)
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                IconButton(onClick = onSharePdf) {
                    Icon(Icons.Filled.PictureAsPdf, contentDescription = "Compartir PDF")
                }
            }
        }
    }
}
