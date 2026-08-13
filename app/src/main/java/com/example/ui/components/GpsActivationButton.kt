package com.example.ui.components

import android.Manifest
import android.content.Intent
import android.provider.Settings
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.GpsFixed
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.util.LocationHelper
import kotlinx.coroutines.launch
import org.osmdroid.util.GeoPoint

@Composable
fun GpsActivationButton(
    onLocationObtained: (GeoPoint) -> Unit,
    modifier: Modifier = Modifier,
    buttonText: String = "Activar GPS",
    isOutlined: Boolean = true,
    isFab: Boolean = false
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    var isLoading by remember { mutableStateOf(false) }
    var showGpsDisabledDialog by remember { mutableStateOf(false) }
    var pendingLocationFetch by remember { mutableStateOf(false) }

    // Launcher for Location Settings
    val gpsSettingsLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) {
        if (LocationHelper.isGpsEnabled(context)) {
            pendingLocationFetch = true
        } else {
            Toast.makeText(context, "El GPS continúa desactivado.", Toast.LENGTH_SHORT).show()
        }
    }

    // Function to perform location fetch
    fun executeFetchLocation() {
        coroutineScope.launch {
            isLoading = true
            val result = LocationHelper.getCurrentLocation(context)
            isLoading = false
            result.fold(
                onSuccess = { geoPoint ->
                    Toast.makeText(context, "Ubicación obtenida correctamente", Toast.LENGTH_SHORT).show()
                    onLocationObtained(geoPoint)
                },
                onFailure = { error ->
                    Toast.makeText(
                        context,
                        error.message ?: "Error al obtener ubicación. Intenta nuevamente.",
                        Toast.LENGTH_LONG
                    ).show()
                }
            )
        }
    }

    // Reactively fetch location after returning from settings
    LaunchedEffect(pendingLocationFetch) {
        if (pendingLocationFetch) {
            pendingLocationFetch = false
            executeFetchLocation()
        }
    }

    // Launcher for Multiple Location Permissions
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val isGranted = permissions.values.any { it }
        if (isGranted) {
            if (!LocationHelper.isGpsEnabled(context)) {
                showGpsDisabledDialog = true
            } else {
                executeFetchLocation()
            }
        } else {
            Toast.makeText(
                context,
                "Permisos de ubicación denegados. Actívalos en los ajustes de la aplicación.",
                Toast.LENGTH_LONG
            ).show()
        }
    }

    fun startGpsFlow() {
        if (!LocationHelper.hasLocationPermission(context)) {
            permissionLauncher.launch(
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )
            )
            return
        }

        if (!LocationHelper.isGpsEnabled(context)) {
            showGpsDisabledDialog = true
            return
        }

        executeFetchLocation()
    }

    // GPS Disabled Alert Dialog
    if (showGpsDisabledDialog) {
        AlertDialog(
            onDismissRequest = { showGpsDisabledDialog = false },
            title = {
                Text(
                    text = "GPS Desactivado",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Text(
                    text = "El GPS del dispositivo está desactivado. ¿Deseas ir a los ajustes de ubicación para activarlo y continuar con la georreferenciación?",
                    style = MaterialTheme.typography.bodyMedium
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        showGpsDisabledDialog = false
                        try {
                            val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                            gpsSettingsLauncher.launch(intent)
                        } catch (e: Exception) {
                            Toast.makeText(context, "No se pudieron abrir los ajustes de ubicación", Toast.LENGTH_SHORT).show()
                        }
                    }
                ) {
                    Text("Ir a Ajustes")
                }
            },
            dismissButton = {
                TextButton(onClick = { showGpsDisabledDialog = false }) {
                    Text("Cancelar")
                }
            }
        )
    }

    // UI Button Render
    if (isFab) {
        FloatingActionButton(
            onClick = { startGpsFlow() },
            modifier = modifier,
            containerColor = MaterialTheme.colorScheme.primaryContainer,
            contentColor = MaterialTheme.colorScheme.onPrimaryContainer
        ) {
            if (isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(20.dp),
                    strokeWidth = 2.dp,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
            } else {
                Icon(
                    imageVector = Icons.Default.MyLocation,
                    contentDescription = "Ir a mi ubicación"
                )
            }
        }
    } else if (isOutlined) {
        OutlinedButton(
            onClick = { startGpsFlow() },
            enabled = !isLoading,
            modifier = modifier,
            colors = ButtonDefaults.outlinedButtonColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer
            )
        ) {
            if (isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(20.dp),
                    strokeWidth = 2.dp,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
                Spacer(Modifier.width(8.dp))
                Text(
                    "Obteniendo...",
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
            } else {
                Icon(
                    imageVector = Icons.Default.GpsFixed,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onPrimaryContainer,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(Modifier.width(6.dp))
                Text(
                    buttonText,
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }
        }
    } else {
        Button(
            onClick = { startGpsFlow() },
            enabled = !isLoading,
            modifier = modifier
        ) {
            if (isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(20.dp),
                    strokeWidth = 2.dp,
                    color = MaterialTheme.colorScheme.onPrimary
                )
                Spacer(Modifier.width(8.dp))
                Text("Obteniendo GPS...")
            } else {
                Icon(
                    imageVector = Icons.Default.MyLocation,
                    contentDescription = null,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(Modifier.width(8.dp))
                Text(buttonText, fontWeight = FontWeight.Bold)
            }
        }
    }
}
