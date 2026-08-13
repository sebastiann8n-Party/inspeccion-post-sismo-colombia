package com.example.ui.form

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.data.ColombiaLocations
import com.example.data.FotoEntity
import com.example.data.InspeccionConFotos
import com.example.data.InspeccionEntity
import com.example.data.InspeccionRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.UUID

data class FormUiState(
    val id: String = UUID.randomUUID().toString(),
    val fechaHora: Long = System.currentTimeMillis(),
    // Identificación
    val departamento: String = "Cundinamarca",
    val municipio: String = "Bogotá D.C.",
    val localidad: String = "",
    val barrio: String = "",
    val codigoBarrio: String = "",
    val tipoVia: String = "Carrera",
    val direccion: String = "",
    val idCatastralManzana: String = "",
    val idCatastralPredio: String = "",
    val idCatastralConstruccion: String = "",
    // Estructura & Edificación
    val nombreEdificacion: String = "",
    val usoPredominante: String = "",
    val usoPlantaBaja: String = "",
    val niveles: String = "",
    val sotanos: String = "",
    val sistemaEstructural: String = "",
    val sistemaPlantaBaja: String = "",
    val tipoEntrepiso: String = "",
    val anioConstruccion: String = "",
    val frente: String = "",
    val fondo: String = "",
    // Daños y Estado de la Edificación
    val colapsoEstado: String = "1. No", // 1. No, 2. Parcial, 3. Total
    val desviacionInclinacion: String = "2. No", // 1. Sí, 2. No, 3. No se pudo determinar
    val fallaCimentacion: String = "2. No", // 1. Sí, 2. No, 3. No se pudo determinar
    // Daños en Elementos Arquitectónicos (1. Ninguno, 2. Leve, 3. Moderado, 4. Fuerte, 5. Severo)
    val danoFachadasAntepechos: String = "1. Ninguno",
    val danoMurosDivisorios: String = "1. Ninguno",
    val danoCieloRasosLuminarias: String = "1. Ninguno",
    val danoCubierta: String = "1. Ninguno",
    val danoEscaleras: String = "1. Ninguno",
    val instalacionesAcueducto: Boolean = false,
    val instalacionesAlcantarillado: Boolean = false,
    val instalacionesEnergia: Boolean = false,
    val instalacionesGas: Boolean = false,
    val danoInstalaciones: String = "1. Ninguno",
    val danoTanquesElevados: String = "1. Ninguno",
    // Problemas Geotécnicos (1. No, 2. Puntual, 3. General)
    val fallaTaludMovimientos: String = "1. No",
    val asentamientoLicuacion: String = "1. No",
    // Elementos Estructurales
    val danosColumnas: String = "1. Ninguno",
    val danosVigas: String = "1. Ninguno",
    val danosMuros: String = "1. Ninguno",
    val danosNudos: String = "1. Ninguno",
    val huboColapso: Boolean = false,
    val porcentajeDanoGlobal: Int = 0,
    val danosArquitectonicos: String = "",
    val danosGeotecnicos: String = "",
    val habitabilidad: String = "", // VERDE, AMARILLO, NARANJA, ROJO
    // Recomendaciones y Acciones
    val accionesSeleccionadas: Set<String> = emptySet(),
    val lugaresMedidasSeguridad: String = "",
    val intervencionesSeleccionadas: Set<String> = emptySet(),
    // Condiciones Pre-existentes
    val calidadConstruccion: String = "Buena",
    val posicionManzana: String = "Intermedia",
    val configPlanta: String = "Buena",
    val configAltura: String = "Buena",
    val configEstructural: String = "Buena",
    val indiciosDanosAnteriores: Boolean = false,
    val tipoReparacion: String = "Ninguna",
    // Ocupantes
    val huboMuertosOheridos: String = "No",
    val numFallecidos: String = "0",
    val numHeridos: String = "0",
    val numDesaparecidos: String = "0",
    val nombresDesaparecidos: String = "",
    // Ocupación
    val estaHabitada: Boolean = true,
    val unidadesExistentes: String = "",
    val unidadesNoHabitables: String = "",
    // Fotos
    val fotos: List<FotoEntity> = emptyList(),
    // Recomendaciones y Servicios
    val recomendaciones: String = "",
    val serviciosDesconectar: String = "",
    // Contacto
    val nombreContacto: String = "",
    val telefonoContacto: String = "",
    val comentarios: String = "",
    // Inspectores / Comisión
    val codigoComision: String = "",
    val numeroEvaluadores: String = "1",
    val inspectorLider: String = "",
    val comision: String = "",
    val esPrueba: Boolean = false, // Modo prueba / simulación
    val firmaInspector: String = "", // Base64 o ruta local
    // Georeferenciación
    val latitud: Double = 0.0,
    val longitud: Double = 0.0,
    val barrioId: String? = null,
    val direccionNormalizada: String = ""
)

class FormViewModel(private val repository: InspeccionRepository) : ViewModel() {

    private val _uiState = MutableStateFlow(FormUiState())
    val uiState: StateFlow<FormUiState> = _uiState.asStateFlow()

    fun updateField(field: String, value: String) {
        _uiState.update { currentState ->
            when (field) {
                "departamento" -> {
                    val deptInfo = ColombiaLocations.findDepartamento(value)
                    val newLat = if (deptInfo != null) deptInfo.latitudCentroide else currentState.latitud
                    val newLon = if (deptInfo != null) deptInfo.longitudCentroide else currentState.longitud
                    currentState.copy(
                        departamento = value,
                        latitud = newLat,
                        longitud = newLon
                    )
                }
                "municipio" -> currentState.copy(municipio = value)
                "localidad" -> currentState.copy(localidad = value)
                "barrio" -> currentState.copy(barrio = value)
                "codigoBarrio" -> currentState.copy(codigoBarrio = value)
                "tipoVia" -> currentState.copy(tipoVia = value)
                "direccion" -> currentState.copy(direccion = value)
                "idCatastralManzana" -> currentState.copy(idCatastralManzana = value)
                "idCatastralPredio" -> currentState.copy(idCatastralPredio = value)
                "idCatastralConstruccion" -> currentState.copy(idCatastralConstruccion = value)
                "nombreEdificacion" -> currentState.copy(nombreEdificacion = value)
                "usoPredominante" -> {
                    val newPlantaBaja = if (currentState.usoPlantaBaja.isBlank()) value else currentState.usoPlantaBaja
                    currentState.copy(usoPredominante = value, usoPlantaBaja = newPlantaBaja)
                }
                "usoPlantaBaja" -> currentState.copy(usoPlantaBaja = value)
                "niveles" -> currentState.copy(niveles = value)
                "sotanos" -> currentState.copy(sotanos = value)
                "sistemaEstructural" -> {
                    val newPlantaBaja = if (currentState.sistemaPlantaBaja.isBlank()) value else currentState.sistemaPlantaBaja
                    currentState.copy(sistemaEstructural = value, sistemaPlantaBaja = newPlantaBaja)
                }
                "sistemaPlantaBaja" -> currentState.copy(sistemaPlantaBaja = value)
                "tipoEntrepiso" -> currentState.copy(tipoEntrepiso = value)
                "anioConstruccion" -> currentState.copy(anioConstruccion = value)
                "frente" -> currentState.copy(frente = value)
                "fondo" -> currentState.copy(fondo = value)
                "colapsoEstado" -> {
                    val isTotal = value.contains("3. Total")
                    val isParcial = value.contains("2. Parcial")
                    val newPct = when {
                        isTotal -> 100
                        isParcial -> if (currentState.porcentajeDanoGlobal == 0 || currentState.porcentajeDanoGlobal == 100) 50 else currentState.porcentajeDanoGlobal
                        else -> currentState.porcentajeDanoGlobal
                    }
                    val habitability = when {
                        newPct >= 75 -> "ROJO"
                        newPct >= 50 -> "NARANJA"
                        newPct >= 25 -> "AMARILLO"
                        else -> "VERDE"
                    }
                    currentState.copy(
                        colapsoEstado = value,
                        huboColapso = isTotal,
                        porcentajeDanoGlobal = newPct,
                        habitabilidad = habitability
                    )
                }
                "porcentajeDanoGlobal" -> {
                    val pct = value.toIntOrNull()?.coerceIn(0, 100) ?: currentState.porcentajeDanoGlobal
                    val habitability = when {
                        pct >= 75 -> "ROJO"
                        pct >= 50 -> "NARANJA"
                        pct >= 25 -> "AMARILLO"
                        else -> "VERDE"
                    }
                    currentState.copy(
                        porcentajeDanoGlobal = pct,
                        habitabilidad = habitability
                    )
                }
                "desviacionInclinacion" -> currentState.copy(desviacionInclinacion = value)
                "fallaCimentacion" -> currentState.copy(fallaCimentacion = value)
                "danoFachadasAntepechos" -> currentState.copy(danoFachadasAntepechos = value)
                "danoMurosDivisorios" -> currentState.copy(danoMurosDivisorios = value)
                "danoCieloRasosLuminarias" -> currentState.copy(danoCieloRasosLuminarias = value)
                "danoCubierta" -> currentState.copy(danoCubierta = value)
                "danoEscaleras" -> currentState.copy(danoEscaleras = value)
                "danoInstalaciones" -> currentState.copy(danoInstalaciones = value)
                "danoTanquesElevados" -> currentState.copy(danoTanquesElevados = value)
                "fallaTaludMovimientos" -> currentState.copy(fallaTaludMovimientos = value)
                "asentamientoLicuacion" -> currentState.copy(asentamientoLicuacion = value)
                "danosArquitectonicos" -> currentState.copy(danosArquitectonicos = value)
                "danosGeotecnicos" -> currentState.copy(danosGeotecnicos = value)
                "habitabilidad" -> currentState.copy(habitabilidad = value)
                "lugaresMedidasSeguridad" -> currentState.copy(lugaresMedidasSeguridad = value)
                "calidadConstruccion" -> currentState.copy(calidadConstruccion = value)
                "posicionManzana" -> currentState.copy(posicionManzana = value)
                "configPlanta" -> currentState.copy(configPlanta = value)
                "configAltura" -> currentState.copy(configAltura = value)
                "configEstructural" -> currentState.copy(configEstructural = value)
                "tipoReparacion" -> currentState.copy(tipoReparacion = value)
                "numFallecidos" -> currentState.copy(numFallecidos = value)
                "numHeridos" -> currentState.copy(numHeridos = value)
                "numDesaparecidos" -> currentState.copy(numDesaparecidos = value)
                "nombresDesaparecidos" -> currentState.copy(nombresDesaparecidos = value)
                "unidadesExistentes" -> currentState.copy(unidadesExistentes = value)
                "unidadesNoHabitables" -> currentState.copy(unidadesNoHabitables = value)
                "nombreContacto" -> currentState.copy(nombreContacto = value)
                "telefonoContacto" -> currentState.copy(telefonoContacto = value)
                "comentarios" -> currentState.copy(comentarios = value)
                "codigoComision" -> currentState.copy(codigoComision = value)
                "numeroEvaluadores" -> currentState.copy(numeroEvaluadores = value)
                "recomendaciones" -> currentState.copy(recomendaciones = value)
                "serviciosDesconectar" -> currentState.copy(serviciosDesconectar = value)
                "inspectorLider" -> currentState.copy(inspectorLider = value)
                "comision" -> currentState.copy(comision = value)
                "esPrueba" -> currentState.copy(esPrueba = value.toBoolean())
                "firmaInspector" -> currentState.copy(firmaInspector = value)
                else -> currentState
            }
        }
    }
    
    fun addFoto(uri: String, observacion: String) {
        val nuevaFoto = FotoEntity(
            inspeccionId = _uiState.value.id,
            uri = uri,
            observacion = observacion
        )
        _uiState.update { currentState ->
            currentState.copy(fotos = currentState.fotos + nuevaFoto)
        }
    }

    fun updateFotoObservacion(fotoId: String, nuevaObservacion: String) {
        _uiState.update { currentState ->
            val fotosActualizadas = currentState.fotos.map { foto ->
                if (foto.id == fotoId) foto.copy(observacion = nuevaObservacion) else foto
            }
            currentState.copy(fotos = fotosActualizadas)
        }
    }

    fun removeFoto(fotoId: String) {
        _uiState.update { currentState ->
            currentState.copy(fotos = currentState.fotos.filterNot { it.id == fotoId })
        }
    }
    
    fun loadInspeccion(inspeccionId: String) {
        viewModelScope.launch {
            val ins = repository.getInspeccionConFotosById(inspeccionId)
            if (ins != null) {
                _uiState.update {
                    FormUiState(
                        id = ins.inspeccion.id,
                        fechaHora = ins.inspeccion.fechaHora,
                        departamento = ins.inspeccion.departamento,
                        municipio = ins.inspeccion.municipio,
                        localidad = ins.inspeccion.localidad,
                        barrio = ins.inspeccion.barrio,
                        codigoBarrio = ins.inspeccion.codigoBarrio,
                        tipoVia = ins.inspeccion.tipoVia,
                        direccion = ins.inspeccion.direccion,
                        idCatastralManzana = ins.inspeccion.idCatastralManzana,
                        idCatastralPredio = ins.inspeccion.idCatastralPredio,
                        idCatastralConstruccion = ins.inspeccion.idCatastralConstruccion,
                        nombreEdificacion = ins.inspeccion.nombreEdificacion,
                        usoPredominante = ins.inspeccion.usoPredominante,
                        usoPlantaBaja = ins.inspeccion.usoPlantaBaja,
                        niveles = ins.inspeccion.niveles.toString(),
                        sotanos = ins.inspeccion.sotanos.toString(),
                        sistemaEstructural = ins.inspeccion.sistemaEstructural,
                        sistemaPlantaBaja = ins.inspeccion.sistemaPlantaBaja,
                        tipoEntrepiso = ins.inspeccion.tipoEntrepiso,
                        anioConstruccion = ins.inspeccion.anioConstruccion.toString(),
                        frente = ins.inspeccion.frente.toString(),
                        fondo = ins.inspeccion.fondo.toString(),
                        colapsoEstado = ins.inspeccion.colapsoEstado,
                        desviacionInclinacion = ins.inspeccion.desviacionInclinacion,
                        fallaCimentacion = ins.inspeccion.fallaCimentacion,
                        danoFachadasAntepechos = ins.inspeccion.danoFachadasAntepechos,
                        danoMurosDivisorios = ins.inspeccion.danoMurosDivisorios,
                        danoCieloRasosLuminarias = ins.inspeccion.danoCieloRasosLuminarias,
                        danoCubierta = ins.inspeccion.danoCubierta,
                        danoEscaleras = ins.inspeccion.danoEscaleras,
                        instalacionesAcueducto = ins.inspeccion.instalacionesAcueducto,
                        instalacionesAlcantarillado = ins.inspeccion.instalacionesAlcantarillado,
                        instalacionesEnergia = ins.inspeccion.instalacionesEnergia,
                        instalacionesGas = ins.inspeccion.instalacionesGas,
                        danoInstalaciones = ins.inspeccion.danoInstalaciones,
                        danoTanquesElevados = ins.inspeccion.danoTanquesElevados,
                        fallaTaludMovimientos = ins.inspeccion.fallaTaludMovimientos,
                        asentamientoLicuacion = ins.inspeccion.asentamientoLicuacion,
                        danosColumnas = ins.inspeccion.danosColumnas,
                        danosVigas = ins.inspeccion.danosVigas,
                        danosMuros = ins.inspeccion.danosMuros,
                        danosNudos = ins.inspeccion.danosNudos,
                        huboColapso = ins.inspeccion.huboColapso,
                        porcentajeDanoGlobal = ins.inspeccion.porcentajeDanoGlobal,
                        danosArquitectonicos = ins.inspeccion.danosArquitectonicos,
                        danosGeotecnicos = ins.inspeccion.danosGeotecnicos,
                        habitabilidad = ins.inspeccion.habitabilidad,
                        accionesSeleccionadas = ins.inspeccion.accionesSeguridad.split(",").filter { it.isNotBlank() }.toSet(),
                        lugaresMedidasSeguridad = ins.inspeccion.lugaresMedidasSeguridad,
                        intervencionesSeleccionadas = ins.inspeccion.intervencionesRequeridas.split(",").filter { it.isNotBlank() }.toSet(),
                        calidadConstruccion = ins.inspeccion.calidadConstruccion,
                        posicionManzana = ins.inspeccion.posicionManzana,
                        configPlanta = ins.inspeccion.configPlanta,
                        configAltura = ins.inspeccion.configAltura,
                        configEstructural = ins.inspeccion.configEstructural,
                        indiciosDanosAnteriores = ins.inspeccion.indiciosDanosAnteriores,
                        tipoReparacion = ins.inspeccion.tipoReparacion,
                        huboMuertosOheridos = ins.inspeccion.huboMuertosOheridos,
                        numFallecidos = ins.inspeccion.numFallecidos,
                        numHeridos = ins.inspeccion.numHeridos,
                        numDesaparecidos = ins.inspeccion.numDesaparecidos,
                        nombresDesaparecidos = ins.inspeccion.nombresDesaparecidos,
                        estaHabitada = ins.inspeccion.estaHabitada,
                        unidadesExistentes = ins.inspeccion.unidadesExistentes,
                        unidadesNoHabitables = ins.inspeccion.unidadesNoHabitables,
                        fotos = ins.fotos,
                        recomendaciones = ins.inspeccion.recomendaciones,
                        serviciosDesconectar = ins.inspeccion.serviciosDesconectar,
                        nombreContacto = ins.inspeccion.nombreContacto,
                        telefonoContacto = ins.inspeccion.telefonoContacto,
                        comentarios = ins.inspeccion.comentarios,
                        codigoComision = ins.inspeccion.codigoComision,
                        numeroEvaluadores = ins.inspeccion.numeroEvaluadores,
                        inspectorLider = ins.inspeccion.inspectorLider,
                        comision = ins.inspeccion.comision,
                        esPrueba = ins.inspeccion.esPrueba,
                        firmaInspector = ins.inspeccion.firmaInspector,
                        latitud = ins.inspeccion.latitud,
                        longitud = ins.inspeccion.longitud,
                        barrioId = ins.inspeccion.barrioId,
                        direccionNormalizada = ins.inspeccion.direccionNormalizada
                    )
                }
            }
        }
    }

    fun updateLocationAndBarrio(
        lat: Double,
        lon: Double,
        barrioId: String?,
        barrioNombre: String?,
        barrioCodigo: String?,
        localidadNombre: String?,
        direccionFormatted: String?
    ) {
        _uiState.update { state ->
            val newBarrio = if (!barrioNombre.isNullOrBlank()) barrioNombre else state.barrio
            val newLocalidad = if (!localidadNombre.isNullOrBlank()) localidadNombre else state.localidad
            val newAddressFormatted = if (!direccionFormatted.isNullOrBlank()) direccionFormatted else state.direccionNormalizada
            state.copy(
                latitud = lat,
                longitud = lon,
                barrioId = barrioId ?: state.barrioId,
                barrio = newBarrio,
                codigoBarrio = if (!barrioCodigo.isNullOrBlank()) barrioCodigo else state.codigoBarrio,
                localidad = newLocalidad,
                direccionNormalizada = newAddressFormatted,
                direccion = if (state.direccion.isBlank()) newAddressFormatted else state.direccion
            )
        }
    }
    
    fun updateDamageField(field: String, value: String) {
        _uiState.update { currentState ->
            val nextState = when (field) {
                "danosColumnas" -> currentState.copy(danosColumnas = value)
                "danosVigas" -> currentState.copy(danosVigas = value)
                "danosMuros" -> currentState.copy(danosMuros = value)
                "danosNudos" -> currentState.copy(danosNudos = value)
                else -> currentState
            }
            recalculateDamage(nextState)
        }
    }
    
    fun toggleInstalacion(servicio: String) {
        _uiState.update { currentState ->
            when (servicio) {
                "Acueducto" -> currentState.copy(instalacionesAcueducto = !currentState.instalacionesAcueducto)
                "Alcantarillado" -> currentState.copy(instalacionesAlcantarillado = !currentState.instalacionesAlcantarillado)
                "Energía" -> currentState.copy(instalacionesEnergia = !currentState.instalacionesEnergia)
                "Gas" -> currentState.copy(instalacionesGas = !currentState.instalacionesGas)
                else -> currentState
            }
        }
    }

    fun toggleAccion(accion: String) {
        _uiState.update { currentState ->
            val set = currentState.accionesSeleccionadas.toMutableSet()
            if (set.contains(accion)) set.remove(accion) else set.add(accion)
            currentState.copy(accionesSeleccionadas = set)
        }
    }

    fun toggleIntervencion(entidad: String) {
        _uiState.update { currentState ->
            val set = currentState.intervencionesSeleccionadas.toMutableSet()
            if (set.contains(entidad)) set.remove(entidad) else set.add(entidad)
            currentState.copy(intervencionesSeleccionadas = set)
        }
    }

    fun updateHuboMuertosOheridos(respuesta: String) {
        _uiState.update { currentState ->
            val limpiarNumeros = respuesta.equals("No", ignoreCase = true)
            currentState.copy(
                huboMuertosOheridos = respuesta,
                numFallecidos = if (limpiarNumeros) "0" else currentState.numFallecidos,
                numHeridos = if (limpiarNumeros) "0" else currentState.numHeridos,
                numDesaparecidos = if (limpiarNumeros) "0" else currentState.numDesaparecidos,
                nombresDesaparecidos = if (limpiarNumeros) "" else currentState.nombresDesaparecidos
            )
        }
    }

    fun toggleIndiciosDanosAnteriores() {
        _uiState.update { currentState ->
            currentState.copy(indiciosDanosAnteriores = !currentState.indiciosDanosAnteriores)
        }
    }

    fun toggleEstaHabitada() {
        _uiState.update { currentState ->
            currentState.copy(estaHabitada = !currentState.estaHabitada)
        }
    }

    fun updateEsPrueba(esPrueba: Boolean) {
        _uiState.update { currentState ->
            currentState.copy(esPrueba = esPrueba)
        }
    }

    fun updatePorcentajeDanoGlobal(porcentaje: Int) {
        updateField("porcentajeDanoGlobal", porcentaje.toString())
    }

    fun updateColapso(huboColapso: Boolean) {
        _uiState.update { currentState ->
            val colapsoStr = if (huboColapso) "3. Total" else "1. No"
            val pct = if (huboColapso) 100 else currentState.porcentajeDanoGlobal
            val habitability = if (huboColapso) "ROJO" else when {
                pct >= 75 -> "ROJO"
                pct >= 50 -> "NARANJA"
                pct >= 25 -> "AMARILLO"
                else -> "VERDE"
            }
            currentState.copy(
                huboColapso = huboColapso,
                colapsoEstado = colapsoStr,
                porcentajeDanoGlobal = pct,
                habitabilidad = habitability
            )
        }
    }

    private fun recalculateDamage(state: FormUiState): FormUiState {
        if (state.colapsoEstado.contains("3. Total")) {
            return state.copy(porcentajeDanoGlobal = 100, habitabilidad = "ROJO")
        }
        val score = fun(d: String) = when {
            d.contains("Severo", ignoreCase = true) || d == "SEVERO" -> 100
            d.contains("Fuerte", ignoreCase = true) || d == "FUERTE" -> 75
            d.contains("Moderado", ignoreCase = true) || d == "MODERADO" -> 50
            d.contains("Leve", ignoreCase = true) || d == "LEVE" -> 25
            else -> 0
        }
        val avg = (score(state.danosColumnas) + score(state.danosVigas) + score(state.danosMuros) + score(state.danosNudos) + score(state.danoFachadasAntepechos) + score(state.danoCubierta)) / 6
        val newPct = if (state.porcentajeDanoGlobal == 0 && avg > 0) avg else state.porcentajeDanoGlobal
        val habitability = when {
            newPct >= 75 -> "ROJO"
            newPct >= 50 -> "NARANJA"
            newPct >= 25 -> "AMARILLO"
            else -> "VERDE"
        }
        return state.copy(porcentajeDanoGlobal = newPct, habitabilidad = habitability)
    }

    fun saveForm(isBorrador: Boolean = false) {
        val state = _uiState.value
        val inspeccionConFotos = state.toInspeccionConFotos(isBorrador)

        viewModelScope.launch {
            repository.insertInspeccion(inspeccionConFotos.inspeccion)
            repository.deleteFotosByInspeccionId(state.id)
            state.fotos.forEach {
                repository.insertFoto(it)
            }
        }
    }
}

fun FormUiState.toInspeccionConFotos(isBorrador: Boolean = false): InspeccionConFotos {
    return InspeccionConFotos(
        inspeccion = InspeccionEntity(
            id = id,
            fechaHora = fechaHora,
            estado = if (isBorrador) "BORRADOR" else "COMPLETADO",
            departamento = departamento,
            municipio = municipio,
            localidad = if (localidad.isBlank()) "No Aplica" else localidad,
            barrio = barrio,
            codigoBarrio = codigoBarrio,
            tipoVia = tipoVia,
            direccion = direccion,
            idCatastralManzana = idCatastralManzana,
            idCatastralPredio = idCatastralPredio,
            idCatastralConstruccion = idCatastralConstruccion,
            nombreEdificacion = nombreEdificacion,
            usoPredominante = usoPredominante,
            usoPlantaBaja = usoPlantaBaja,
            niveles = niveles.toIntOrNull() ?: 0,
            sotanos = sotanos.toIntOrNull() ?: 0,
            sistemaEstructural = sistemaEstructural,
            sistemaPlantaBaja = sistemaPlantaBaja,
            tipoEntrepiso = tipoEntrepiso,
            anioConstruccion = anioConstruccion.toIntOrNull() ?: 0,
            frente = frente.toFloatOrNull() ?: 0f,
            fondo = fondo.toFloatOrNull() ?: 0f,
            colapsoEstado = colapsoEstado,
            desviacionInclinacion = desviacionInclinacion,
            fallaCimentacion = fallaCimentacion,
            danoFachadasAntepechos = danoFachadasAntepechos,
            danoMurosDivisorios = danoMurosDivisorios,
            danoCieloRasosLuminarias = danoCieloRasosLuminarias,
            danoCubierta = danoCubierta,
            danoEscaleras = danoEscaleras,
            instalacionesAcueducto = instalacionesAcueducto,
            instalacionesAlcantarillado = instalacionesAlcantarillado,
            instalacionesEnergia = instalacionesEnergia,
            instalacionesGas = instalacionesGas,
            danoInstalaciones = danoInstalaciones,
            danoTanquesElevados = danoTanquesElevados,
            fallaTaludMovimientos = fallaTaludMovimientos,
            asentamientoLicuacion = asentamientoLicuacion,
            danosColumnas = danosColumnas,
            danosVigas = danosVigas,
            danosMuros = danosMuros,
            danosNudos = danosNudos,
            huboColapso = huboColapso,
            porcentajeDanoGlobal = porcentajeDanoGlobal,
            danosArquitectonicos = danosArquitectonicos,
            danosGeotecnicos = danosGeotecnicos,
            habitabilidad = habitabilidad,
            accionesSeguridad = accionesSeleccionadas.joinToString(","),
            lugaresMedidasSeguridad = lugaresMedidasSeguridad,
            intervencionesRequeridas = intervencionesSeleccionadas.joinToString(","),
            calidadConstruccion = calidadConstruccion,
            posicionManzana = posicionManzana,
            configPlanta = configPlanta,
            configAltura = configAltura,
            configEstructural = configEstructural,
            indiciosDanosAnteriores = indiciosDanosAnteriores,
            tipoReparacion = tipoReparacion,
            huboMuertosOheridos = huboMuertosOheridos,
            numFallecidos = numFallecidos,
            numHeridos = numHeridos,
            numDesaparecidos = numDesaparecidos,
            nombresDesaparecidos = nombresDesaparecidos,
            estaHabitada = estaHabitada,
            unidadesExistentes = unidadesExistentes,
            unidadesNoHabitables = unidadesNoHabitables,
            recomendaciones = recomendaciones,
            serviciosDesconectar = serviciosDesconectar,
            nombreContacto = nombreContacto,
            telefonoContacto = telefonoContacto,
            comentarios = comentarios,
            codigoComision = codigoComision,
            numeroEvaluadores = numeroEvaluadores,
            inspectorLider = inspectorLider,
            comision = comision,
            esPrueba = esPrueba,
            firmaInspector = firmaInspector,
            latitud = latitud,
            longitud = longitud,
            barrioId = barrioId,
            direccionNormalizada = direccionNormalizada
        ),
        fotos = fotos
    )
}

class FormViewModelFactory(private val repository: InspeccionRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(FormViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return FormViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
