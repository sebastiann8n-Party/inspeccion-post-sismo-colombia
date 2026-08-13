package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.UUID

@Entity(tableName = "inspecciones")
data class InspeccionEntity(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    val fechaHora: Long = System.currentTimeMillis(),
    val estado: String = "BORRADOR", // BORRADOR, COMPLETADO
    // Identificación
    val departamento: String = "Cundinamarca",
    val municipio: String = "Bogotá D.C.",
    val localidad: String = "",
    val barrio: String = "",
    val codigoBarrio: String = "",
    val tipoVia: String = "",
    val direccion: String = "",
    val idCatastralManzana: String = "",
    val idCatastralPredio: String = "",
    val idCatastralConstruccion: String = "",
    // Estructura
    val nombreEdificacion: String = "",
    val usoPredominante: String = "",
    val usoPlantaBaja: String = "",
    val niveles: Int = 0,
    val sotanos: Int = 0,
    val sistemaEstructural: String = "",
    val sistemaPlantaBaja: String = "",
    val tipoEntrepiso: String = "",
    val anioConstruccion: Int = 0,
    val frente: Float = 0f,
    val fondo: Float = 0f,
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
    val accionesSeguridad: String = "",
    val lugaresMedidasSeguridad: String = "",
    val intervencionesRequeridas: String = "",
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
    // Recomendaciones y Servicios
    val recomendaciones: String = "",
    val serviciosDesconectar: String = "",
    // Contacto
    val nombreContacto: String = "",
    val telefonoContacto: String = "",
    val comentarios: String = "",
    // Metadatos e Inspectores
    val codigoComision: String = "",
    val numeroEvaluadores: String = "1",
    val inspectorLider: String = "",
    val comision: String = "",
    val esPrueba: Boolean = false, // Marca si es una inspección de prueba/capacitación
    val firmaInspector: String = "", // Base64 o SVG de la firma
    // Geolocalización y Organización por Barrio
    val latitud: Double = 0.0,
    val longitud: Double = 0.0,
    val barrioId: String? = null,
    val direccionNormalizada: String = ""
)
