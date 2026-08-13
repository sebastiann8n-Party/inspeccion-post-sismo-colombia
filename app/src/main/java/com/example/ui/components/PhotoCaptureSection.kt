package com.example.ui.components

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material.icons.filled.AddPhotoAlternate
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.core.content.FileProvider
import coil.compose.AsyncImage
import com.example.data.FotoEntity
import java.io.File

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun PhotoCaptureSection(
    fotos: List<FotoEntity>,
    onPhotoTaken: (String, String) -> Unit,
    onUpdateFotoObservacion: (String, String) -> Unit = { _, _ -> },
    onRemoveFoto: (String) -> Unit = {}
) {
    val context = LocalContext.current
    var tempCameraUri by remember { mutableStateOf<Uri?>(null) }
    
    // Estado para diálogo de nueva foto capturada/seleccionada
    var pendingPhotoUri by remember { mutableStateOf<Uri?>(null) }
    var pendingObservation by remember { mutableStateOf("") }

    // Estado para editar la observación de una foto existente
    var editingFoto by remember { mutableStateOf<FotoEntity?>(null) }
    var editingObservation by remember { mutableStateOf("") }

    // Estado para confirmación de eliminación
    var photoToDelete by remember { mutableStateOf<FotoEntity?>(null) }

    val presetTags = listOf(
        "Grieta en Muro",
        "Falla en Columna",
        "Daño en Fachada",
        "Colapso Parcial",
        "Desprendimiento de Pañete",
        "Falla en Viga",
        "Daño en Escalera",
        "Falla Geotécnica"
    )

    val takePictureLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture(),
        onResult = { success ->
            if (success) {
                tempCameraUri?.let { uri ->
                    pendingPhotoUri = uri
                    pendingObservation = ""
                }
            }
        }
    )

    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia(),
        onResult = { uri ->
            uri?.let {
                pendingPhotoUri = it
                pendingObservation = ""
            }
        }
    )

    Column(modifier = Modifier.fillMaxWidth()) {
        Card(
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = "Capturar o Seleccionar Evidencia Fotográfica",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Añada fotografías con descripciones detalladas de los daños observados para el reporte oficial.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Button(
                        onClick = {
                            val file = File(context.filesDir, "foto_${System.currentTimeMillis()}.jpg")
                            val uri = FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", file)
                            tempCameraUri = uri
                            takePictureLauncher.launch(uri)
                        },
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(Icons.Filled.CameraAlt, contentDescription = "Tomar Foto con Cámara")
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Cámara")
                    }

                    OutlinedButton(
                        onClick = {
                            photoPickerLauncher.launch(
                                androidx.activity.result.PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                            )
                        },
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(Icons.Filled.PhotoLibrary, contentDescription = "Seleccionar de Galería")
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Galería")
                    }
                }
            }
        }

        // --- GALERÍA DE FOTOS REGISTRADAS ---
        if (fotos.isNotEmpty()) {
            Text(
                text = "Fotografías de la Inspección (${fotos.size})",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(top = 20.dp, bottom = 8.dp)
            )

            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                fotos.forEachIndexed { index, foto ->
                    Card(
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            // Miniatura de la fotografía
                            AsyncImage(
                                model = foto.uri,
                                contentDescription = "Fotografía ${index + 1}",
                                contentScale = ContentScale.Crop,
                                modifier = Modifier
                                    .size(90.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(MaterialTheme.colorScheme.surfaceVariant)
                            )

                            // Información de la foto y descripción
                            Column(
                                modifier = Modifier.weight(1f),
                                verticalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Text(
                                    text = "Fotografía #${index + 1}",
                                    style = MaterialTheme.typography.labelLarge,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.primary
                                )
                                Text(
                                    text = if (foto.observacion.isNotBlank()) foto.observacion else "Sin descripción añadida.",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = if (foto.observacion.isNotBlank()) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }

                            // Botones de edición y eliminación
                            Column(
                                verticalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                IconButton(
                                    onClick = {
                                        editingFoto = foto
                                        editingObservation = foto.observacion
                                    }
                                ) {
                                    Icon(
                                        Icons.Filled.Edit,
                                        contentDescription = "Editar descripción",
                                        tint = MaterialTheme.colorScheme.primary
                                    )
                                }
                                IconButton(
                                    onClick = {
                                        photoToDelete = foto
                                    }
                                ) {
                                    Icon(
                                        Icons.Filled.Delete,
                                        contentDescription = "Eliminar foto",
                                        tint = MaterialTheme.colorScheme.error
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // --- DIÁLOGO 1: COMENTAR/DESCRIBIR FOTO RECIÉN TOMADA O SELECCIONADA ---
    pendingPhotoUri?.let { uri ->
        AlertDialog(
            onDismissRequest = {
                pendingPhotoUri = null
            },
            icon = {
                Icon(Icons.Filled.AddPhotoAlternate, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
            },
            title = {
                Text("Describir Fotografía", fontWeight = FontWeight.Bold)
            },
            text = {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Vista previa de la imagen recién seleccionada
                    AsyncImage(
                        model = uri,
                        contentDescription = "Vista previa",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(180.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(MaterialTheme.colorScheme.surfaceVariant)
                    )

                    Text(
                        text = "Escriba una descripción o seleccione etiquetas de daño:",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    // Campo de texto para comentario/observación
                    OutlinedTextField(
                        value = pendingObservation,
                        onValueChange = { pendingObservation = it },
                        label = { Text("Descripción / Observaciones") },
                        placeholder = { Text("Ej: Grieta diagonal en muro de fachada norte") },
                        modifier = Modifier.fillMaxWidth(),
                        minLines = 2
                    )

                    // Etiquetas rápidas
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        presetTags.forEach { tag ->
                            SuggestionChip(
                                onClick = {
                                    pendingObservation = if (pendingObservation.isBlank()) tag else "$pendingObservation - $tag"
                                },
                                label = { Text(tag, style = MaterialTheme.typography.labelSmall) }
                            )
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val obsFinal = pendingObservation.ifBlank { "Sin observación" }
                        onPhotoTaken(uri.toString(), obsFinal)
                        pendingPhotoUri = null
                    }
                ) {
                    Text("Guardar Foto")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        pendingPhotoUri = null
                    }
                ) {
                    Text("Cancelar")
                }
            }
        )
    }

    // --- DIÁLOGO 2: EDITAR DESCRIPCIÓN DE FOTO EXISTENTE ---
    editingFoto?.let { foto ->
        AlertDialog(
            onDismissRequest = { editingFoto = null },
            icon = {
                Icon(Icons.Filled.Edit, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
            },
            title = {
                Text("Editar Descripción", fontWeight = FontWeight.Bold)
            },
            text = {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    AsyncImage(
                        model = foto.uri,
                        contentDescription = "Vista previa",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(160.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(MaterialTheme.colorScheme.surfaceVariant)
                    )

                    OutlinedTextField(
                        value = editingObservation,
                        onValueChange = { editingObservation = it },
                        label = { Text("Descripción de la Fotografía") },
                        modifier = Modifier.fillMaxWidth(),
                        minLines = 2
                    )

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        presetTags.forEach { tag ->
                            SuggestionChip(
                                onClick = {
                                    editingObservation = if (editingObservation.isBlank()) tag else "$editingObservation - $tag"
                                },
                                label = { Text(tag, style = MaterialTheme.typography.labelSmall) }
                            )
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        onUpdateFotoObservacion(foto.id, editingObservation)
                        editingFoto = null
                    }
                ) {
                    Text("Actualizar")
                }
            },
            dismissButton = {
                TextButton(onClick = { editingFoto = null }) {
                    Text("Cancelar")
                }
            }
        )
    }

    // --- DIÁLOGO 3: CONFIRMACIÓN DE ELIMINACIÓN DE FOTO ---
    photoToDelete?.let { foto ->
        AlertDialog(
            onDismissRequest = { photoToDelete = null },
            title = { Text("¿Eliminar Fotografía?") },
            text = { Text("Esta foto y su descripción serán removidas de la inspección.") },
            confirmButton = {
                Button(
                    onClick = {
                        onRemoveFoto(foto.id)
                        photoToDelete = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                ) {
                    Text("Eliminar")
                }
            },
            dismissButton = {
                TextButton(onClick = { photoToDelete = null }) {
                    Text("Cancelar")
                }
            }
        )
    }
}
