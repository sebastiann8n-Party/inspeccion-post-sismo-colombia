package com.example.ui.form

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.*
import androidx.compose.material3.MenuAnchorType
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Slider
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.SistemaEstructural
import com.example.data.TipoEntrepiso
import com.example.data.UsoPredominanteOption
import androidx.compose.foundation.clickable
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Warning

import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.ui.platform.LocalContext
import com.example.ui.components.GpsActivationButton
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Step1Identificacion(
    uiState: FormUiState,
    onUpdate: (String, String) -> Unit,
    onOpenMapSelection: () -> Unit = {}
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val deptList = com.example.data.ColombiaLocations.DEPARTAMENTOS
    val localidades = listOf(
        "No Aplica",
        "Usaquén", "Chapinero", "Santa Fe", "San Cristóbal", "Usme", "Tunjuelito", 
        "Bosa", "Kennedy", "Fontibón", "Engativá", "Suba", "Barrios Unidos", 
        "Teusaquillo", "Los Mártires", "Antonio Nariño", "Puente Aranda", 
        "La Candelaria", "Rafael Uribe Uribe", "Ciudad Bolívar", "Sumapaz",
        "Comuna 1", "Comuna 2", "Comuna 3", "Comuna 4", "Comuna 5", "Centro Cabecera"
    )
    var expandedDept by remember { mutableStateOf(false) }
    var expandedLocalidad by remember { mutableStateOf(false) }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item { 
            Text(
                "Localización y Catastro", 
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onBackground
            ) 
        }

        // Card 0: MODO DE REGISTRO (Aislamiento de Datos de Prueba)
        item {
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = if (uiState.esPrueba) 
                        MaterialTheme.colorScheme.tertiaryContainer 
                    else 
                        MaterialTheme.colorScheme.surfaceVariant
                ),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = androidx.compose.ui.Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                "Modo de Registro de Evaluación",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                if (uiState.esPrueba)
                                    "🧪 REGISTRO DE PRUEBA / CAPACITACIÓN"
                                else
                                    "📋 REGISTRO OFICIAL Y REAL",
                                style = MaterialTheme.typography.bodySmall,
                                fontWeight = FontWeight.SemiBold,
                                color = if (uiState.esPrueba) MaterialTheme.colorScheme.onTertiaryContainer else MaterialTheme.colorScheme.primary
                            )
                        }
                        Switch(
                            checked = uiState.esPrueba,
                            onCheckedChange = { onUpdate("esPrueba", it.toString()) }
                        )
                    }
                    Text(
                        text = if (uiState.esPrueba)
                            "⚠️ Esta evaluación se marcará como PRUEBA. Quedará aislada automáticamente de los reportes oficiales y la malla de puntos real."
                        else
                            "✅ Registro de campo real para consolidación oficial de gestión de riesgo.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }

        // Card 1: LOCALIZACIÓN Y CATASTRO
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        "Localización General",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )

                    // Buttons for Map Georeferencing & GPS Auto-guiding
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedButton(
                            onClick = onOpenMapSelection,
                            modifier = Modifier.weight(1f),
                            colors = ButtonDefaults.outlinedButtonColors(
                                containerColor = MaterialTheme.colorScheme.surface
                            )
                        ) {
                            Icon(Icons.Default.Place, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                            Spacer(Modifier.width(4.dp))
                            Text(
                                if (uiState.latitud != 0.0) "📍 Ver en Mapa" else "📍 Mapa Offline",
                                fontWeight = FontWeight.Bold,
                                style = MaterialTheme.typography.labelMedium
                            )
                        }

                        GpsActivationButton(
                            onLocationObtained = { geo ->
                                onUpdate("latitud", geo.latitude.toString())
                                onUpdate("longitud", geo.longitude.toString())
                            },
                            modifier = Modifier.weight(1f),
                            buttonText = "Activar GPS",
                            isOutlined = true
                        )
                    }

                    if (uiState.latitud != 0.0 && uiState.longitud != 0.0) {
                        Surface(
                            shape = androidx.compose.foundation.shape.RoundedCornerShape(8.dp),
                            color = MaterialTheme.colorScheme.primaryContainer,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Text(
                                    "Georreferenciación Registrada:",
                                    style = MaterialTheme.typography.labelMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onPrimaryContainer
                                )
                                Text(
                                    "Lat: ${uiState.latitud} | Lon: ${uiState.longitud}",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onPrimaryContainer
                                )
                                if (uiState.direccionNormalizada.isNotBlank()) {
                                    Text(
                                        "Dirección: ${uiState.direccionNormalizada}",
                                        style = MaterialTheme.typography.bodySmall,
                                        fontWeight = FontWeight.Medium,
                                        color = MaterialTheme.colorScheme.onPrimaryContainer
                                    )
                                }
                            }
                        }
                    }

                    // Departamento (32 Departamentos de Colombia) y Ciudad / Municipio (Ingreso libre)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Box(modifier = Modifier.weight(1f)) {
                            ExposedDropdownMenuBox(
                                expanded = expandedDept,
                                onExpandedChange = { expandedDept = it }
                            ) {
                                OutlinedTextField(
                                    value = uiState.departamento,
                                    onValueChange = { onUpdate("departamento", it) },
                                    label = { Text("Departamento") },
                                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedDept) },
                                    colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors(),
                                    modifier = Modifier.fillMaxWidth().menuAnchor(MenuAnchorType.PrimaryEditable),
                                    singleLine = true
                                )
                                ExposedDropdownMenu(
                                    expanded = expandedDept,
                                    onDismissRequest = { expandedDept = false }
                                ) {
                                    deptList.forEach { deptInfo ->
                                        DropdownMenuItem(
                                            text = { Text(deptInfo.nombre) },
                                            onClick = {
                                                onUpdate("departamento", deptInfo.nombre)
                                                expandedDept = false
                                            }
                                        )
                                    }
                                }
                            }
                        }

                        Box(modifier = Modifier.weight(1f)) {
                            OutlinedTextField(
                                value = uiState.municipio,
                                onValueChange = { onUpdate("municipio", it) },
                                label = { Text("Ciudad / Municipio") },
                                modifier = Modifier.fillMaxWidth(),
                                singleLine = true
                            )
                        }
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Box(modifier = Modifier.weight(1f)) {
                            ExposedDropdownMenuBox(
                                expanded = expandedLocalidad,
                                onExpandedChange = { expandedLocalidad = it }
                            ) {
                                OutlinedTextField(
                                    value = uiState.localidad,
                                    onValueChange = { onUpdate("localidad", it) },
                                    label = { Text("Localidad / Comuna") },
                                    placeholder = { Text("Escriba o seleccione (Ej. No Aplica)") },
                                    supportingText = { Text("Si se deja en blanco, por defecto se colocará 'No Aplica'") },
                                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedLocalidad) },
                                    colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors(),
                                    modifier = Modifier.fillMaxWidth().menuAnchor(MenuAnchorType.PrimaryEditable),
                                    singleLine = true
                                )
                                ExposedDropdownMenu(
                                    expanded = expandedLocalidad,
                                    onDismissRequest = { expandedLocalidad = false }
                                ) {
                                    localidades.forEach { localidad ->
                                        DropdownMenuItem(
                                            text = { Text(localidad) },
                                            onClick = {
                                                onUpdate("localidad", localidad)
                                                expandedLocalidad = false
                                            }
                                        )
                                    }
                                }
                            }
                        }
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedTextField(
                            value = uiState.barrio,
                            onValueChange = { onUpdate("barrio", it) },
                            label = { Text("Nombre del Barrio") },
                            modifier = Modifier.weight(2f),
                            singleLine = true
                        )
                        OutlinedTextField(
                            value = uiState.codigoBarrio,
                            onValueChange = { onUpdate("codigoBarrio", it) },
                            label = { Text("Cód. Barrio") },
                            modifier = Modifier.weight(1f),
                            singleLine = true,
                            keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(
                                keyboardType = androidx.compose.ui.text.input.KeyboardType.Number
                            )
                        )
                    }
                }
            }
        }

        // Card 2: IDENTIFICACIÓN CATASTRAL (casillas)
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        "Identificación Catastral",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        OutlinedTextField(
                            value = uiState.codigoBarrio,
                            onValueChange = { onUpdate("codigoBarrio", it) },
                            label = { Text("Barrio") },
                            modifier = Modifier.weight(1f),
                            singleLine = true,
                            keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(
                                keyboardType = androidx.compose.ui.text.input.KeyboardType.Number
                            )
                        )
                        OutlinedTextField(
                            value = uiState.idCatastralManzana,
                            onValueChange = { onUpdate("idCatastralManzana", it) },
                            label = { Text("Manzana") },
                            modifier = Modifier.weight(1f),
                            singleLine = true,
                            keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(
                                keyboardType = androidx.compose.ui.text.input.KeyboardType.Number
                            )
                        )
                        OutlinedTextField(
                            value = uiState.idCatastralPredio,
                            onValueChange = { onUpdate("idCatastralPredio", it) },
                            label = { Text("Predio") },
                            modifier = Modifier.weight(1f),
                            singleLine = true,
                            keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(
                                keyboardType = androidx.compose.ui.text.input.KeyboardType.Number
                            )
                        )
                        OutlinedTextField(
                            value = uiState.idCatastralConstruccion,
                            onValueChange = { onUpdate("idCatastralConstruccion", it) },
                            label = { Text("Construcción") },
                            modifier = Modifier.weight(1.2f),
                            singleLine = true,
                            keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(
                                keyboardType = androidx.compose.ui.text.input.KeyboardType.Number
                            )
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Step2Estructura(uiState: FormUiState, onUpdate: (String, String) -> Unit) {
    val tiposVia = listOf("Carrera", "Calle", "Transv", "Diag", "Avda", "Otro")
    
    val nivelesInt = uiState.niveles.toIntOrNull() ?: 0
    val sotanosInt = uiState.sotanos.toIntOrNull() ?: 0
    val totalPisos = nivelesInt + sotanosInt

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item { 
            Text(
                "Identificación de la Edificación", 
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onBackground
            ) 
        }

        // 1. DIRECCIÓN Y NOMBRE
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text("Dirección y Nombre", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)

                    Text("Tipo de Vía", style = MaterialTheme.typography.bodyMedium)
                    SingleChoiceSegmentedButtonRow(
                        modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState())
                    ) {
                        tiposVia.forEachIndexed { index, via ->
                            SegmentedButton(
                                selected = uiState.tipoVia == via,
                                onClick = { onUpdate("tipoVia", via) },
                                shape = SegmentedButtonDefaults.itemShape(index = index, count = tiposVia.size)
                            ) {
                                Text(via)
                            }
                        }
                    }

                    OutlinedTextField(
                        value = uiState.direccion,
                        onValueChange = { onUpdate("direccion", it) },
                        label = { Text("Número y Detalle de Dirección") },
                        placeholder = { Text("Ej: 15 # 24 - 30") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )

                    OutlinedTextField(
                        value = uiState.nombreEdificacion,
                        onValueChange = { onUpdate("nombreEdificacion", it) },
                        label = { Text("Nombre de la Edificación") },
                        placeholder = { Text("Ej: Edificio Torres del Parque") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                }
            }
        }

        // 2. USO PREDOMINANTE
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text("Uso Predominante", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)

                    // Reference Legend of the 11 Uses from the form
                    Surface(
                        color = MaterialTheme.colorScheme.surface,
                        shape = MaterialTheme.shapes.small,
                        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text("Usos normativos:", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.SemiBold)
                            Text(
                                "1. Residencial | 2. Comercial | 3. Educacional | 4. Salud\n" +
                                "5. Hotelero | 6. Oficinas | 7. Industrial | 8. Institucional\n" +
                                "9. Bodegas | 10. Estacionamientos | 11. Otros",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    UsoPredominanteDropdown(
                        label = "De la Edificación",
                        selectedItemValue = uiState.usoPredominante,
                        onItemSelected = { option ->
                            onUpdate("usoPredominante", "${option.codigo}. ${option.descripcion}")
                        }
                    )

                    UsoPredominanteDropdown(
                        label = "De la Planta Baja",
                        selectedItemValue = uiState.usoPlantaBaja,
                        onItemSelected = { option ->
                            onUpdate("usoPlantaBaja", "${option.codigo}. ${option.descripcion}")
                        }
                    )
                }
            }
        }

        // 3. NÚMERO DE PISOS Y DIMENSIONES
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Text("Número de Pisos y Dimensiones", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedTextField(
                            value = uiState.niveles,
                            onValueChange = { onUpdate("niveles", it) },
                            label = { Text("Sobre terreno") },
                            modifier = Modifier.weight(1f),
                            singleLine = true,
                            keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(
                                keyboardType = androidx.compose.ui.text.input.KeyboardType.Number
                            )
                        )

                        OutlinedTextField(
                            value = uiState.sotanos,
                            onValueChange = { onUpdate("sotanos", it) },
                            label = { Text("Sótanos") },
                            modifier = Modifier.weight(1f),
                            singleLine = true,
                            keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(
                                keyboardType = androidx.compose.ui.text.input.KeyboardType.Number
                            )
                        )

                        // Calculated total Pisos Badge
                        Surface(
                            color = MaterialTheme.colorScheme.primaryContainer,
                            shape = MaterialTheme.shapes.medium,
                            modifier = Modifier.weight(1f).height(56.dp).padding(top = 8.dp)
                        ) {
                            Column(
                                verticalArrangement = Arrangement.Center,
                                horizontalAlignment = androidx.compose.ui.Alignment.CenterHorizontally
                            ) {
                                Text("Total", style = MaterialTheme.typography.labelSmall)
                                Text(
                                    "$totalPisos",
                                    style = MaterialTheme.typography.titleLarge,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onPrimaryContainer
                                )
                            }
                        }
                    }

                    Text("Dimensiones aproximadas de la edificación", style = MaterialTheme.typography.labelMedium)

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        OutlinedTextField(
                            value = uiState.frente,
                            onValueChange = { onUpdate("frente", it) },
                            label = { Text("Frente (m)") },
                            modifier = Modifier.weight(1f),
                            singleLine = true,
                            keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(
                                keyboardType = androidx.compose.ui.text.input.KeyboardType.Decimal
                            )
                        )

                        OutlinedTextField(
                            value = uiState.fondo,
                            onValueChange = { onUpdate("fondo", it) },
                            label = { Text("Fondo (m)") },
                            modifier = Modifier.weight(1f),
                            singleLine = true,
                            keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(
                                keyboardType = androidx.compose.ui.text.input.KeyboardType.Decimal
                            )
                        )
                    }
                }
            }
        }

        // 4. SISTEMA ESTRUCTURAL
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Text(
                        text = "Sistema Estructural",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )

                    CategorizedSistemaDropdown(
                        label = "De la Edificación",
                        selectedItemValue = uiState.sistemaEstructural,
                        onItemSelected = { item ->
                            onUpdate("sistemaEstructural", "${item.codigo} - ${item.descripcion}")
                        }
                    )

                    CategorizedSistemaDropdown(
                        label = "De la Planta Baja",
                        selectedItemValue = uiState.sistemaPlantaBaja,
                        onItemSelected = { item ->
                            onUpdate("sistemaPlantaBaja", "${item.codigo} - ${item.descripcion}")
                        }
                    )
                }
            }
        }

        // 5. TIPO DE ENTREPISO Y AÑO
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Text(
                        text = "Tipo de Entrepiso y Año",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )

                    CategorizedEntrepisoDropdown(
                        label = "Seleccione el entrepiso",
                        selectedItemValue = uiState.tipoEntrepiso,
                        onItemSelected = { item ->
                            onUpdate("tipoEntrepiso", "${item.codigo} - ${item.descripcion}")
                        }
                    )

                    OutlinedTextField(
                        value = uiState.anioConstruccion,
                        onValueChange = { onUpdate("anioConstruccion", it) },
                        label = { Text("Año de Construcción") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(
                            keyboardType = androidx.compose.ui.text.input.KeyboardType.Number
                        )
                    )
                }
            }
        }
    }
}

// --- COMPONENTES AUXILIARES PARA MENÚS CATEGORIZADOS ---

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategorizedSistemaDropdown(
    label: String,
    selectedItemValue: String,
    onItemSelected: (SistemaEstructural) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    val selectedItem = SistemaEstructural.fromValue(selectedItemValue)
    val agrupados = remember { SistemaEstructural.entries.groupBy { it.categoria } }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded }
    ) {
        OutlinedTextField(
            value = selectedItem?.let { "${it.codigo} - ${it.descripcion}" } ?: selectedItemValue,
            onValueChange = {},
            readOnly = true,
            label = { Text(label) },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            modifier = Modifier.menuAnchor(MenuAnchorType.PrimaryNotEditable).fillMaxWidth(),
            colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors()
        )
        
        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier.fillMaxHeight(0.6f)
        ) {
            agrupados.forEach { (categoria, items) ->
                DropdownMenuItem(
                    text = { 
                        Text(
                            text = categoria, 
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        ) 
                    },
                    onClick = { },
                    enabled = false
                )
                items.forEach { item ->
                    DropdownMenuItem(
                        text = { Text("${item.codigo}. ${item.descripcion}") },
                        onClick = {
                            onItemSelected(item)
                            expanded = false
                        },
                        contentPadding = PaddingValues(start = 32.dp, end = 16.dp)
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategorizedEntrepisoDropdown(
    label: String,
    selectedItemValue: String,
    onItemSelected: (TipoEntrepiso) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    val selectedItem = TipoEntrepiso.fromValue(selectedItemValue)
    val agrupados = remember { TipoEntrepiso.entries.groupBy { it.categoria } }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded }
    ) {
        OutlinedTextField(
            value = selectedItem?.let { "${it.codigo} - ${it.descripcion}" } ?: selectedItemValue,
            onValueChange = {},
            readOnly = true,
            label = { Text(label) },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            modifier = Modifier.menuAnchor(MenuAnchorType.PrimaryNotEditable).fillMaxWidth(),
            colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors()
        )
        
        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            agrupados.forEach { (categoria, items) ->
                DropdownMenuItem(
                    text = { 
                        Text(
                            text = categoria, 
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.secondary
                        ) 
                    },
                    onClick = { },
                    enabled = false
                )
                items.forEach { item ->
                    DropdownMenuItem(
                        text = { Text("${item.codigo}. ${item.descripcion}") },
                        onClick = {
                            onItemSelected(item)
                            expanded = false
                        },
                        contentPadding = PaddingValues(start = 32.dp, end = 16.dp)
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UsoPredominanteDropdown(
    label: String,
    selectedItemValue: String,
    onItemSelected: (UsoPredominanteOption) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    val selectedItem = UsoPredominanteOption.fromValue(selectedItemValue)

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded }
    ) {
        OutlinedTextField(
            value = selectedItem?.let { "${it.codigo}. ${it.descripcion}" } ?: selectedItemValue,
            onValueChange = {},
            readOnly = true,
            label = { Text(label) },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            modifier = Modifier.menuAnchor(MenuAnchorType.PrimaryNotEditable).fillMaxWidth(),
            colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors()
        )
        
        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            UsoPredominanteOption.entries.forEach { option ->
                DropdownMenuItem(
                    text = { Text("${option.codigo}. ${option.descripcion}") },
                    onClick = {
                        onItemSelected(option)
                        expanded = false
                    }
                )
            }
        }
    }
}

@Composable
fun Step3Danos(
    uiState: FormUiState,
    onUpdateColapso: (Boolean) -> Unit,
    onUpdateDamageField: (String, String) -> Unit,
    onUpdateField: (String, String) -> Unit,
    onToggleInstalacion: (String) -> Unit,
    onUpdatePorcentajeDano: (Int) -> Unit = {}
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Text(
                "Estado de la Edificación",
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onBackground
            )
        }

        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = androidx.compose.ui.Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                "Daño Global Estimado",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                if (uiState.colapsoEstado.contains("3. Total")) "Colapso Total Registrado (100%)"
                                else "Ajuste libremente el porcentaje de daño global",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        Surface(
                            color = when {
                                uiState.porcentajeDanoGlobal >= 75 -> MaterialTheme.colorScheme.errorContainer
                                uiState.porcentajeDanoGlobal >= 50 -> androidx.compose.ui.graphics.Color(0xFFFFE0B2)
                                uiState.porcentajeDanoGlobal >= 25 -> androidx.compose.ui.graphics.Color(0xFFFFF9C4)
                                else -> androidx.compose.ui.graphics.Color(0xFFC8E6C9)
                            },
                            shape = androidx.compose.foundation.shape.RoundedCornerShape(12.dp)
                        ) {
                            Text(
                                text = "${uiState.porcentajeDanoGlobal}%",
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.ExtraBold,
                                color = when {
                                    uiState.porcentajeDanoGlobal >= 75 -> MaterialTheme.colorScheme.onErrorContainer
                                    uiState.porcentajeDanoGlobal >= 50 -> androidx.compose.ui.graphics.Color(0xFFE65100)
                                    uiState.porcentajeDanoGlobal >= 25 -> androidx.compose.ui.graphics.Color(0xFFF57F17)
                                    else -> androidx.compose.ui.graphics.Color(0xFF1B5E20)
                                }
                            )
                        }
                    }

                    if (!uiState.colapsoEstado.contains("3. Total")) {
                        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            Slider(
                                value = uiState.porcentajeDanoGlobal.toFloat(),
                                onValueChange = { onUpdatePorcentajeDano(it.toInt()) },
                                valueRange = 0f..100f,
                                modifier = Modifier.fillMaxWidth()
                            )
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("0% (Sin daño)", style = MaterialTheme.typography.labelSmall)
                                Text("50% (Parcial / Moderado)", style = MaterialTheme.typography.labelSmall)
                                Text("100% (Total / Severo)", style = MaterialTheme.typography.labelSmall)
                            }
                        }
                    } else {
                        LinearProgressIndicator(
                            progress = { 1f },
                            modifier = Modifier.fillMaxWidth().height(8.dp),
                            color = MaterialTheme.colorScheme.error,
                            trackColor = MaterialTheme.colorScheme.surfaceVariant,
                            strokeCap = androidx.compose.ui.graphics.StrokeCap.Round
                        )
                    }
                }
            }
        }

        // --- 1. ESTADO GENERAL DE LA EDIFICACIÓN ---
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        "Estado General de la Edificación",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        "Revisar la edificación en forma global para las condiciones señaladas y hacer aclaraciones en comentarios:",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    TripleChoiceRow(
                        label = "1. Existe colapso",
                        options = listOf("1. No", "2. Parcial", "3. Total"),
                        selectedValue = uiState.colapsoEstado,
                        onValueSelected = { option ->
                            onUpdateField("colapsoEstado", option)
                        }
                    )

                    TripleChoiceRow(
                        label = "2. Desviación o inclinación de la edificación o entrepiso",
                        options = listOf("1. Sí", "2. No", "3. No se puede determinar"),
                        selectedValue = uiState.desviacionInclinacion,
                        onValueSelected = { onUpdateField("desviacionInclinacion", it) }
                    )

                    TripleChoiceRow(
                        label = "3. Falla o asentamiento de la cimentación",
                        options = listOf("1. Sí", "2. No", "3. No se puede determinar"),
                        selectedValue = uiState.fallaCimentacion,
                        onValueSelected = { onUpdateField("fallaCimentacion", it) }
                    )
                }
            }
        }

        // --- 2. DAÑOS EN ELEMENTOS ARQUITECTÓNICOS ---
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    Text(
                        "Daños en Elementos Arquitectónicos",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        "Indique el grado de daño de cada elemento:",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    GradedDamageRow(
                        label = "4. Muros de fachadas o antepechos",
                        currentValue = uiState.danoFachadasAntepechos,
                        onValueChange = { onUpdateField("danoFachadasAntepechos", it) }
                    )

                    GradedDamageRow(
                        label = "5. Muros divisorios o particiones",
                        currentValue = uiState.danoMurosDivisorios,
                        onValueChange = { onUpdateField("danoMurosDivisorios", it) }
                    )

                    GradedDamageRow(
                        label = "6. Cielo rasos y luminarias",
                        currentValue = uiState.danoCieloRasosLuminarias,
                        onValueChange = { onUpdateField("danoCieloRasosLuminarias", it) }
                    )

                    GradedDamageRow(
                        label = "7. Cubierta",
                        currentValue = uiState.danoCubierta,
                        onValueChange = { onUpdateField("danoCubierta", it) }
                    )

                    GradedDamageRow(
                        label = "8. Escaleras",
                        currentValue = uiState.danoEscaleras,
                        onValueChange = { onUpdateField("danoEscaleras", it) }
                    )

                    // 9. Instalaciones (Servicios + Daño)
                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Text(
                            "9. Instalaciones afectadas:",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.SemiBold
                        )
                        Row(
                            modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            FilterChip(
                                selected = uiState.instalacionesAcueducto,
                                onClick = { onToggleInstalacion("Acueducto") },
                                label = { Text("Acueducto") }
                            )
                            FilterChip(
                                selected = uiState.instalacionesAlcantarillado,
                                onClick = { onToggleInstalacion("Alcantarillado") },
                                label = { Text("Alcantarillado") }
                            )
                            FilterChip(
                                selected = uiState.instalacionesEnergia,
                                onClick = { onToggleInstalacion("Energía") },
                                label = { Text("Energía") }
                            )
                            FilterChip(
                                selected = uiState.instalacionesGas,
                                onClick = { onToggleInstalacion("Gas") },
                                label = { Text("Gas") }
                            )
                        }

                        GradedDamageRow(
                            label = "Grado de daño en Instalaciones",
                            currentValue = uiState.danoInstalaciones,
                            onValueChange = { onUpdateField("danoInstalaciones", it) }
                        )
                    }

                    GradedDamageRow(
                        label = "10. Tanques elevados",
                        currentValue = uiState.danoTanquesElevados,
                        onValueChange = { onUpdateField("danoTanquesElevados", it) }
                    )
                }
            }
        }

        // --- 3. PROBLEMAS GEOTÉCNICOS ---
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        "Problemas Geotécnicos",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )

                    TripleChoiceRow(
                        label = "11. Falla en talud o movimientos en masa",
                        options = listOf("1. No", "2. Puntual", "3. General"),
                        selectedValue = uiState.fallaTaludMovimientos,
                        onValueSelected = { onUpdateField("fallaTaludMovimientos", it) }
                    )

                    TripleChoiceRow(
                        label = "12. Asentamiento, subsidencia o licuación",
                        options = listOf("1. No", "2. Puntual", "3. General"),
                        selectedValue = uiState.asentamientoLicuacion,
                        onValueSelected = { onUpdateField("asentamientoLicuacion", it) }
                    )
                }
            }
        }

        // --- 4. ELEMENTOS ESTRUCTURALES ---
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    Text(
                        "Elementos Estructurales Principalmente Afectados",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )

                    GradedDamageRow(
                        label = "Columnas",
                        currentValue = uiState.danosColumnas,
                        onValueChange = { onUpdateDamageField("danosColumnas", it) }
                    )

                    GradedDamageRow(
                        label = "Vigas",
                        currentValue = uiState.danosVigas,
                        onValueChange = { onUpdateDamageField("danosVigas", it) }
                    )

                    GradedDamageRow(
                        label = "Muros de Carga",
                        currentValue = uiState.danosMuros,
                        onValueChange = { onUpdateDamageField("danosMuros", it) }
                    )

                    GradedDamageRow(
                        label = "Nudos / Conexiones",
                        currentValue = uiState.danosNudos,
                        onValueChange = { onUpdateDamageField("danosNudos", it) }
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TripleChoiceRow(
    label: String,
    options: List<String>,
    selectedValue: String,
    onValueSelected: (String) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
        Text(label, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.SemiBold)
        SingleChoiceSegmentedButtonRow(modifier = Modifier.fillMaxWidth()) {
            options.forEachIndexed { index, option ->
                val isSelected = selectedValue.equals(option, ignoreCase = true) ||
                    (selectedValue.isNotBlank() && (
                        (option.startsWith("1.") && selectedValue.startsWith("1.")) ||
                        (option.startsWith("2.") && selectedValue.startsWith("2.")) ||
                        (option.startsWith("3.") && selectedValue.startsWith("3.")) ||
                        (option.startsWith("4.") && selectedValue.startsWith("4."))
                    ))
                SegmentedButton(
                    selected = isSelected,
                    onClick = { onValueSelected(option) },
                    shape = SegmentedButtonDefaults.itemShape(index = index, count = options.size)
                ) {
                    Text(
                        text = option,
                        style = MaterialTheme.typography.labelSmall,
                        maxLines = 2,
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                        lineHeight = 11.sp
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GradedDamageRow(
    label: String,
    currentValue: String,
    onValueChange: (String) -> Unit,
    enabled: Boolean = true
) {
    val options = listOf("1. Ninguno", "2. Leve", "3. Moderado", "4. Fuerte", "5. Severo")
    val shortLabels = listOf("Ning.", "Leve", "Mod.", "Fuerte", "Severo")

    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
        Text(
            label,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.SemiBold,
            color = if (enabled) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
        )
        SingleChoiceSegmentedButtonRow(modifier = Modifier.fillMaxWidth()) {
            options.forEachIndexed { index, option ->
                val isCritical = index >= 3
                val isSelected = currentValue == option || currentValue.startsWith("${index + 1}.") || (index == 0 && currentValue.equals("NINGUNO", ignoreCase = true))

                val colors = if (isCritical && isSelected) {
                    SegmentedButtonDefaults.colors(
                        activeContainerColor = MaterialTheme.colorScheme.errorContainer,
                        activeContentColor = MaterialTheme.colorScheme.onErrorContainer
                    )
                } else {
                    SegmentedButtonDefaults.colors()
                }

                SegmentedButton(
                    selected = isSelected,
                    onClick = { if (enabled) onValueChange(option) },
                    shape = SegmentedButtonDefaults.itemShape(index = index, count = options.size),
                    colors = colors
                ) {
                    Text(shortLabels[index], style = MaterialTheme.typography.labelSmall, maxLines = 1)
                }
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class, ExperimentalMaterial3Api::class)
@Composable
fun Step4Habitabilidad(
    uiState: FormUiState,
    onUpdateField: (String, String) -> Unit,
    onToggleAccion: (String) -> Unit,
    onToggleIntervencion: (String) -> Unit,
    onUpdateHuboMuertosOheridos: (String) -> Unit,
    onToggleIndiciosDanos: () -> Unit,
    onToggleEstaHabitada: () -> Unit
) {
    val opcionesVisitas = listOf(
        "Visita: Estructurales", "Visita: Geotécnicos", "Visita: Servicios públicos"
    )
    val opcionesMedidasSeguridad = listOf(
        "Restringir paso de peatones", "Restringir tráfico vehicular", "Apuntalar",
        "Demoler elementos en peligro", "Evacuar parcialmente edificación",
        "Evacuar totalmente edificación", "Evacuar edificaciones vecinas",
        "Desconectar Energía/Gas/Agua", "Manejo de sustancias peligrosas"
    )
    val opcionesIntervencion = listOf(
        "Planeación - Control físico", "Policía - Ejército", "Tránsito", "Bomberos / Entidades de rescate"
    )

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Text(
                "Habitabilidad, Recomendaciones y Pre-existencias",
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onBackground
            )
        }

        // --- 1. CLASIFICACIÓN DE HABITABILIDAD ---
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        "Clasificación Global de Habitabilidad",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        HabitabilityCard(
                            color = androidx.compose.ui.graphics.Color(0xFF4CAF50),
                            label = "HABITABLE",
                            sublabel = "VERDE",
                            isSelected = uiState.habitabilidad == "VERDE",
                            onClick = { onUpdateField("habitabilidad", "VERDE") },
                            modifier = Modifier.weight(1f)
                        )
                        HabitabilityCard(
                            color = androidx.compose.ui.graphics.Color(0xFFFFC107),
                            label = "USO RESTRINGIDO",
                            sublabel = "AMARILLO",
                            isSelected = uiState.habitabilidad == "AMARILLO",
                            onClick = { onUpdateField("habitabilidad", "AMARILLO") },
                            modifier = Modifier.weight(1f)
                        )
                    }
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        HabitabilityCard(
                            color = androidx.compose.ui.graphics.Color(0xFFFF9800),
                            label = "NO HABITABLE",
                            sublabel = "NARANJA",
                            isSelected = uiState.habitabilidad == "NARANJA",
                            onClick = { onUpdateField("habitabilidad", "NARANJA") },
                            modifier = Modifier.weight(1f)
                        )
                        HabitabilityCard(
                            color = androidx.compose.ui.graphics.Color(0xFFF44336),
                            label = "PELIGRO DE COLAPSO",
                            sublabel = "ROJO",
                            isSelected = uiState.habitabilidad == "ROJO",
                            onClick = { onUpdateField("habitabilidad", "ROJO") },
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }
        }

        // --- 2. RECOMENDACIONES Y ACCIONES DE SEGURIDAD ---
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        "Recomendaciones y Acciones de Seguridad",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )

                    Text("Se necesita visita especializada por aspectos:", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.SemiBold)
                    androidx.compose.foundation.layout.FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        opcionesVisitas.forEach { visita ->
                            FilterChip(
                                selected = uiState.accionesSeleccionadas.contains(visita),
                                onClick = { onToggleAccion(visita) },
                                label = { Text(visita) }
                            )
                        }
                    }

                    Text("Medidas de seguridad recomendadas:", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.SemiBold)
                    androidx.compose.foundation.layout.FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        opcionesMedidasSeguridad.forEach { medida ->
                            FilterChip(
                                selected = uiState.accionesSeleccionadas.contains(medida),
                                onClick = { onToggleAccion(medida) },
                                label = { Text(medida) }
                            )
                        }
                    }

                    OutlinedTextField(
                        value = uiState.lugaresMedidasSeguridad,
                        onValueChange = { onUpdateField("lugaresMedidasSeguridad", it) },
                        label = { Text("Especifique lugares que requieren medidas") },
                        placeholder = { Text("Ej. Fachada posterior, zona de acceso principal") },
                        modifier = Modifier.fillMaxWidth(),
                        minLines = 2
                    )

                    Text("Se recomienda intervención de:", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.SemiBold)
                    androidx.compose.foundation.layout.FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        opcionesIntervencion.forEach { entidad ->
                            FilterChip(
                                selected = uiState.intervencionesSeleccionadas.contains(entidad),
                                onClick = { onToggleIntervencion(entidad) },
                                label = { Text(entidad) }
                            )
                        }
                    }
                }
            }
        }

        // --- 3. CONDICIONES PRE-EXISTENTES ---
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    Text(
                        "Condiciones Pre-existentes de la Edificación",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )

                    EvaluacionRow(
                        label = "Calidad de la Construcción",
                        selectedValue = uiState.calidadConstruccion,
                        onSelect = { onUpdateField("calidadConstruccion", it) }
                    )

                    TripleChoiceRow(
                        label = "Posición en la Manzana",
                        options = listOf("1. Esquina", "2. Intermedia", "3. Libre por un costado", "4. Libre por dos costados"),
                        selectedValue = uiState.posicionManzana,
                        onValueSelected = { onUpdateField("posicionManzana", it) }
                    )

                    EvaluacionRow(
                        label = "Configuración en Planta",
                        selectedValue = uiState.configPlanta,
                        onSelect = { onUpdateField("configPlanta", it) }
                    )

                    EvaluacionRow(
                        label = "Configuración en Altura",
                        selectedValue = uiState.configAltura,
                        onSelect = { onUpdateField("configAltura", it) }
                    )

                    EvaluacionRow(
                        label = "Configuración Estructural",
                        selectedValue = uiState.configEstructural,
                        onSelect = { onUpdateField("configEstructural", it) }
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth().clickable { onToggleIndiciosDanos() }.padding(vertical = 4.dp),
                        verticalAlignment = androidx.compose.ui.Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Indicios de daños anteriores al evento", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.SemiBold)
                        Switch(
                            checked = uiState.indiciosDanosAnteriores,
                            onCheckedChange = { onToggleIndiciosDanos() }
                        )
                    }

                    if (uiState.indiciosDanosAnteriores) {
                        TripleChoiceRow(
                            label = "Tipo de Reparación Previa",
                            options = listOf("Total", "Parcial", "Ninguna"),
                            selectedValue = uiState.tipoReparacion,
                            onValueSelected = { onUpdateField("tipoReparacion", it) }
                        )
                    }
                }
            }
        }

        // --- 4. EFECTOS EN LOS OCUPANTES ---
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        "Efecto en los Ocupantes",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )

                    TripleChoiceRow(
                        label = "¿Hubo fallecidos o heridos?",
                        options = listOf("No", "Sí", "No se sabe"),
                        selectedValue = uiState.huboMuertosOheridos,
                        onValueSelected = { onUpdateHuboMuertosOheridos(it) }
                    )

                    if (uiState.huboMuertosOheridos == "Sí" || uiState.huboMuertosOheridos == "No se sabe") {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            OutlinedTextField(
                                value = uiState.numFallecidos,
                                onValueChange = { onUpdateField("numFallecidos", it) },
                                label = { Text("N° Fallecidos") },
                                keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(keyboardType = androidx.compose.ui.text.input.KeyboardType.Number),
                                modifier = Modifier.weight(1f)
                            )
                            OutlinedTextField(
                                value = uiState.numHeridos,
                                onValueChange = { onUpdateField("numHeridos", it) },
                                label = { Text("N° Heridos") },
                                keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(keyboardType = androidx.compose.ui.text.input.KeyboardType.Number),
                                modifier = Modifier.weight(1f)
                            )
                            OutlinedTextField(
                                value = uiState.numDesaparecidos,
                                onValueChange = { onUpdateField("numDesaparecidos", it) },
                                label = { Text("N° Desaparec.") },
                                keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(keyboardType = androidx.compose.ui.text.input.KeyboardType.Number),
                                modifier = Modifier.weight(1f)
                            )
                        }

                        val countDesaparecidos = uiState.numDesaparecidos.toIntOrNull() ?: 0
                        if (countDesaparecidos > 0) {
                            val existingList = uiState.nombresDesaparecidos.split("\n")
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(top = 4.dp),
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Text(
                                    text = "Listado de Personas Desaparecidas ($countDesaparecidos):",
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.primary
                                )
                                for (i in 0 until countDesaparecidos.coerceAtMost(50)) {
                                    val currentName = existingList.getOrElse(i) { "" }
                                    OutlinedTextField(
                                        value = currentName,
                                        onValueChange = { newName ->
                                            val newList = MutableList(countDesaparecidos) { idx -> existingList.getOrElse(idx) { "" } }
                                            newList[i] = newName
                                            onUpdateField("nombresDesaparecidos", newList.joinToString("\n"))
                                        },
                                        label = { Text("Nombre Desaparecido ${i + 1}") },
                                        placeholder = { Text("Escriba el nombre completo") },
                                        modifier = Modifier.fillMaxWidth(),
                                        singleLine = true
                                    )
                                }
                            }
                        }
                    } else {
                        Text(
                            "Sin víctimas identificadas",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Medium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }

        // --- 5. OCUPACIÓN Y UNIDADES HABITACIONALES ---
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        "Ocupación de la Edificación",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth().clickable { onToggleEstaHabitada() }.padding(vertical = 4.dp),
                        verticalAlignment = androidx.compose.ui.Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("¿Edificación habitada actualmente?", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.SemiBold)
                        Switch(
                            checked = uiState.estaHabitada,
                            onCheckedChange = { onToggleEstaHabitada() }
                        )
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        OutlinedTextField(
                            value = uiState.unidadesExistentes,
                            onValueChange = { onUpdateField("unidadesExistentes", it) },
                            label = { Text("Unidades Existentes") },
                            placeholder = { Text("Ej. 4") },
                            keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(keyboardType = androidx.compose.ui.text.input.KeyboardType.Number),
                            modifier = Modifier.weight(1f)
                        )
                        OutlinedTextField(
                            value = uiState.unidadesNoHabitables,
                            onValueChange = { onUpdateField("unidadesNoHabitables", it) },
                            label = { Text("Unidades No Habitables") },
                            placeholder = { Text("Ej. 2") },
                            keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(keyboardType = androidx.compose.ui.text.input.KeyboardType.Number),
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun EvaluacionRow(
    label: String,
    selectedValue: String,
    onSelect: (String) -> Unit
) {
    val opciones = listOf("Buena", "Regular", "Mala")
    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
        Text(label, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.SemiBold)
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
            opciones.forEach { opcion ->
                val isSelected = selectedValue.equals(opcion, ignoreCase = true)
                OutlinedButton(
                    onClick = { onSelect(opcion) },
                    colors = ButtonDefaults.outlinedButtonColors(
                        containerColor = if (isSelected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surface
                    ),
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        opcion,
                        color = if (isSelected) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurface,
                        style = MaterialTheme.typography.labelMedium
                    )
                }
            }
        }
    }
}

@Composable
fun HabitabilityCard(
    color: androidx.compose.ui.graphics.Color,
    label: String,
    sublabel: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.height(90.dp),
        onClick = onClick,
        border = androidx.compose.foundation.BorderStroke(
            width = if (isSelected) 3.dp else 1.dp,
            color = if (isSelected) color else androidx.compose.ui.graphics.Color(0xFFC3C6CF)
        ),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) color.copy(alpha = 0.12f) else androidx.compose.ui.graphics.Color.White
        ),
        shape = androidx.compose.foundation.shape.RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize().padding(8.dp),
            horizontalAlignment = androidx.compose.ui.Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = label,
                color = if (isSelected) color else androidx.compose.ui.graphics.Color(0xFF1D1B20),
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Bold,
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = sublabel,
                color = color,
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.ExtraBold
            )
        }
    }
}

@Composable
fun Step5Fotos(uiState: FormUiState, viewModel: FormViewModel) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item { Text("Registro Fotográfico", style = MaterialTheme.typography.titleLarge) }
        item {
            com.example.ui.components.PhotoCaptureSection(
                fotos = uiState.fotos,
                onPhotoTaken = { uri, obs -> viewModel.addFoto(uri, obs) },
                onUpdateFotoObservacion = { id, obs -> viewModel.updateFotoObservacion(id, obs) },
                onRemoveFoto = { id -> viewModel.removeFoto(id) }
            )
        }
    }
}

@Composable
fun Step6ResumenFirma(
    uiState: FormUiState,
    onUpdateField: (String, String) -> Unit,
    onSaveForm: (Boolean) -> Unit
) {
    val context = androidx.compose.ui.platform.LocalContext.current
    var showSuccessDialog by remember { mutableStateOf(false) }
    var pdfFileGenerated by remember { mutableStateOf<java.io.File?>(null) }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Text(
                "Cierre, Comentarios e Inspectores",
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onBackground
            )
        }

        // --- 1. PERSONA PARA CONTACTO ---
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        "Persona para Contacto",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )

                    OutlinedTextField(
                        value = uiState.nombreContacto,
                        onValueChange = { onUpdateField("nombreContacto", it) },
                        label = { Text("Nombres y Apellidos") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = uiState.telefonoContacto,
                        onValueChange = { onUpdateField("telefonoContacto", it) },
                        label = { Text("Teléfono de Contacto") },
                        keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(
                            keyboardType = androidx.compose.ui.text.input.KeyboardType.Phone
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }

        // --- 2. COMENTARIOS Y RECOMENDACIONES ---
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        "Comentarios y Observaciones Adicionales",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        "Ampliar observaciones sobre elementos con daños importantes, recomendaciones específicas o justificación de Habitabilidad.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    OutlinedTextField(
                        value = uiState.comentarios,
                        onValueChange = { onUpdateField("comentarios", it) },
                        label = { Text("Escriba sus comentarios u observaciones aquí...") },
                        modifier = Modifier.fillMaxWidth(),
                        minLines = 4
                    )
                }
            }
        }

        // --- 3. INSPECTORES Y FIRMA DIGITAL ---
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        "Información de Inspectores y Firma",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )

                    // Fecha de Inspección
                    val sdfDia = java.text.SimpleDateFormat("dd", java.util.Locale.getDefault())
                    val sdfMes = java.text.SimpleDateFormat("MM", java.util.Locale.getDefault())
                    val sdfAnio = java.text.SimpleDateFormat("yyyy", java.util.Locale.getDefault())
                    val sdfHora = java.text.SimpleDateFormat("HH:mm", java.util.Locale.getDefault())
                    val dateObj = java.util.Date(uiState.fechaHora)

                    Text(
                        "Fecha y Hora de Inspección",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedTextField(
                            value = sdfDia.format(dateObj),
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Día") },
                            modifier = Modifier.weight(1f)
                        )
                        OutlinedTextField(
                            value = sdfMes.format(dateObj),
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Mes") },
                            modifier = Modifier.weight(1f)
                        )
                        OutlinedTextField(
                            value = sdfAnio.format(dateObj),
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Año") },
                            modifier = Modifier.weight(1.2f)
                        )
                        OutlinedTextField(
                            value = sdfHora.format(dateObj),
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Hora (24:00)") },
                            modifier = Modifier.weight(1.5f)
                        )
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        OutlinedTextField(
                            value = uiState.codigoComision,
                            onValueChange = { onUpdateField("codigoComision", it) },
                            label = { Text("Cód. Comisión") },
                            modifier = Modifier.weight(1f)
                        )
                        OutlinedTextField(
                            value = uiState.numeroEvaluadores,
                            onValueChange = { onUpdateField("numeroEvaluadores", it) },
                            label = { Text("N° Evaluadores") },
                            keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(
                                keyboardType = androidx.compose.ui.text.input.KeyboardType.Number
                            ),
                            modifier = Modifier.weight(1f)
                        )
                    }

                    OutlinedTextField(
                        value = uiState.inspectorLider,
                        onValueChange = { onUpdateField("inspectorLider", it) },
                        label = { Text("Nombre del Líder de la Comisión") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        "Firma Digital del Inspector Líder",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.SemiBold
                    )

                    com.example.ui.components.SignaturePad(
                        signatureBase64 = uiState.firmaInspector,
                        onSignatureChange = { onUpdateField("firmaInspector", it) }
                    )
                }
            }
        }

        // --- BOTÓN FINAL DE CONSOLIDACIÓN Y GUARDADO ---
        item {
            val isReadyToFinalize = uiState.inspectorLider.isNotBlank() && uiState.firmaInspector.isNotBlank()

            Button(
                onClick = {
                    onSaveForm(false) // Guardar como COMPLETADO en Room

                    // Generar PDF completo
                    val inspeccionConFotos = uiState.toInspeccionConFotos(isBorrador = false)
                    pdfFileGenerated = com.example.util.PdfExporter.generatePdf(context, inspeccionConFotos)
                    showSuccessDialog = true
                },
                enabled = isReadyToFinalize,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
            ) {
                Text(
                    "Finalizar y Guardar Inspección",
                    style = MaterialTheme.typography.titleMedium
                )
            }

            if (!isReadyToFinalize) {
                Text(
                    "Debe indicar el nombre del líder y registrar la firma digital para finalizar.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }
        }
    }

    if (showSuccessDialog) {
        AlertDialog(
            onDismissRequest = { showSuccessDialog = false },
            title = { Text("Inspección Finalizada") },
            text = { Text("La inspección se ha guardado exitosamente en Room DB y se ha generado el reporte PDF oficial en Scoped Storage.") },
            confirmButton = {
                TextButton(
                    onClick = {
                        pdfFileGenerated?.let { file ->
                            val uri = androidx.core.content.FileProvider.getUriForFile(
                                context,
                                "${context.packageName}.fileprovider",
                                file
                            )
                            val shareIntent = android.content.Intent(android.content.Intent.ACTION_SEND).apply {
                                type = "application/pdf"
                                putExtra(android.content.Intent.EXTRA_STREAM, uri)
                                addFlags(android.content.Intent.FLAG_GRANT_READ_URI_PERMISSION)
                            }
                            context.startActivity(android.content.Intent.createChooser(shareIntent, "Compartir Reporte PDF"))
                        }
                        showSuccessDialog = false
                    }
                ) {
                    Text("Compartir PDF")
                }
            },
            dismissButton = {
                TextButton(onClick = { showSuccessDialog = false }) {
                    Text("Aceptar")
                }
            }
        )
    }
}
