package com.example.ui.form

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel

import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.foundation.layout.Arrangement

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FormScreen(
    viewModel: FormViewModel,
    onBack: () -> Unit,
    onComplete: () -> Unit,
    onOpenMapSelection: () -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsState()
    var currentStep by rememberSaveable { mutableIntStateOf(0) }
    val totalSteps = 6

    Scaffold(
        topBar = {
            Column {
                TopAppBar(
                    title = { Text("Nueva Inspección", style = MaterialTheme.typography.titleLarge) },
                    navigationIcon = {
                        IconButton(onClick = {
                            if (currentStep > 0) currentStep-- else onBack()
                        }) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Atrás")
                        }
                    },
                    actions = {
                        Text(
                            text = "Paso ${currentStep + 1} de $totalSteps",
                            style = MaterialTheme.typography.labelLarge,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(end = 16.dp)
                        )
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.background,
                        titleContentColor = MaterialTheme.colorScheme.onBackground
                    )
                )
                LinearProgressIndicator(
                    progress = { (currentStep + 1) / totalSteps.toFloat() },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    color = MaterialTheme.colorScheme.primary,
                    trackColor = MaterialTheme.colorScheme.surfaceVariant,
                    strokeCap = StrokeCap.Round
                )
            }
        },
        bottomBar = {
            androidx.compose.material3.Surface(
                color = MaterialTheme.colorScheme.background,
                shadowElevation = 8.dp,
                modifier = Modifier.navigationBarsPadding()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    if (currentStep > 0) {
                        OutlinedButton(
                            onClick = { currentStep-- },
                            modifier = Modifier.weight(1f).height(48.dp)
                        ) {
                            Text("Atrás")
                        }
                    } else {
                        Spacer(modifier = Modifier.weight(1f))
                    }
                    
                    if (currentStep < totalSteps - 1) {
                        Button(
                            onClick = { currentStep++ },
                            modifier = Modifier.weight(1f).height(48.dp)
                        ) {
                            Text("Siguiente")
                        }
                    } else {
                        Button(
                            onClick = {
                                viewModel.saveForm(isBorrador = false)
                                onComplete()
                            },
                            modifier = Modifier.weight(1f).height(48.dp)
                        ) {
                            Text("Finalizar")
                        }
                    }
                }
            }
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            AnimatedContent(
                targetState = currentStep,
                transitionSpec = {
                    val slideIn = androidx.compose.animation.slideInHorizontally { width -> if (targetState > initialState) width else -width }
                    val slideOut = androidx.compose.animation.slideOutHorizontally { width -> if (targetState > initialState) -width else width }
                    (slideIn + fadeIn()).togetherWith(slideOut + fadeOut())
                }, label = "stepTransition"
            ) { targetStep ->
                when (targetStep) {
                    0 -> Step1Identificacion(uiState, viewModel::updateField, onOpenMapSelection)
                    1 -> Step2Estructura(uiState, viewModel::updateField)
                    2 -> Step3Danos(
                        uiState = uiState,
                        onUpdateColapso = viewModel::updateColapso,
                        onUpdateDamageField = viewModel::updateDamageField,
                        onUpdateField = viewModel::updateField,
                        onToggleInstalacion = viewModel::toggleInstalacion,
                        onUpdatePorcentajeDano = viewModel::updatePorcentajeDanoGlobal
                    )
                    3 -> Step4Habitabilidad(
                        uiState = uiState,
                        onUpdateField = viewModel::updateField,
                        onToggleAccion = viewModel::toggleAccion,
                        onToggleIntervencion = viewModel::toggleIntervencion,
                        onUpdateHuboMuertosOheridos = viewModel::updateHuboMuertosOheridos,
                        onToggleIndiciosDanos = viewModel::toggleIndiciosDanosAnteriores,
                        onToggleEstaHabitada = viewModel::toggleEstaHabitada
                    )
                    4 -> Step5Fotos(uiState, viewModel)
                    5 -> Step6ResumenFirma(
                        uiState = uiState,
                        onUpdateField = viewModel::updateField,
                        onSaveForm = { isBorrador -> viewModel.saveForm(isBorrador) }
                    )
                }
            }
        }
    }
}
