package com.example.util

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.graphics.Typeface
import android.graphics.pdf.PdfDocument
import android.net.Uri
import android.util.Base64
import androidx.core.content.FileProvider
import com.example.data.FotoEntity
import com.example.data.InspeccionConFotos
import com.example.data.InspeccionEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class PdfExportService(private val context: Context) {

    // Medidas estándar A4 en puntos (1/72 pulgada)
    private val A4_WIDTH = 595
    private val A4_HEIGHT = 842

    suspend fun generarReporteInspeccion(inspeccionConFotos: InspeccionConFotos): File =
        withContext(Dispatchers.IO) {
            val pdfDocument = PdfDocument()
            val ins = inspeccionConFotos.inspeccion
            val fotos = inspeccionConFotos.fotos

            // Estilos de texto y pinceles
            val titlePaint = Paint().apply {
                color = Color.BLACK
                textSize = 12f
                typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            }
            val subtitlePaint = Paint().apply {
                color = Color.DKGRAY
                textSize = 10f
                typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            }
            val textPaint = Paint().apply {
                color = Color.BLACK
                textSize = 8f
                typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
            }
            val labelPaint = Paint().apply {
                color = Color.BLACK
                textSize = 8f
                typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            }
            val linePaint = Paint().apply {
                color = Color.GRAY
                strokeWidth = 0.8f
                style = Paint.Style.STROKE
            }
            val borderPaint = Paint().apply {
                color = Color.rgb(180, 180, 180)
                strokeWidth = 0.7f
                style = Paint.Style.STROKE
            }
            val bannerBgPaint = Paint().apply {
                color = Color.rgb(25, 118, 210) // Azul Primario M3
                style = Paint.Style.FILL
            }
            val bannerTextPaint = Paint().apply {
                color = Color.WHITE
                textSize = 9f
                typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            }
            val tableHeaderBgPaint = Paint().apply {
                color = Color.rgb(238, 238, 238) // Gris Claro
                style = Paint.Style.FILL
            }
            val tableHeaderPaint = Paint().apply {
                color = Color.BLACK
                textSize = 7.5f
                typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            }
            val markPaint = Paint().apply {
                color = Color.rgb(211, 47, 47) // Rojo "X" para marcas visibles
                textSize = 9f
                typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            }

            val sdf = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
            val leftX = 30f
            val contentWidth = 535f

            // Total de páginas estimado
            val hasDesaparecidos = (ins.numDesaparecidos.toIntOrNull() ?: 0) > 0 || ins.nombresDesaparecidos.isNotBlank()
            val photoPages = if (fotos.isNotEmpty()) (fotos.size + 1) / 2 else 0
            val totalPages = 2 + (if (hasDesaparecidos) 1 else 0) + photoPages

            // =========================================================
            // PÁGINA 1: IDENTIFICACIÓN, EDIFICACIÓN Y MATRICES DE DAÑO
            // =========================================================
            val pageInfo1 = PdfDocument.PageInfo.Builder(A4_WIDTH, A4_HEIGHT, 1).create()
            val page1 = pdfDocument.startPage(pageInfo1)
            val canvas1 = page1.canvas
            var y1 = 25f

            // Encabezado principal
            if (ins.esPrueba) {
                val testBgPaint = Paint().apply {
                    color = Color.rgb(211, 47, 47) // Rojo Alerta
                    style = Paint.Style.FILL
                }
                val testTextPaint = Paint().apply {
                    color = Color.WHITE
                    textSize = 10f
                    typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
                }
                canvas1.drawRect(leftX, y1, leftX + contentWidth, y1 + 18f, testBgPaint)
                canvas1.drawText("*** REGISTRO DE PRUEBA / SIMULACIÓN — DOCUMENTO NO OFICIAL ***", leftX + 10f, y1 + 12f, testTextPaint)
                y1 += 22f
            }

            canvas1.drawText("FORMULARIO ÚNICO PARA INSPECCIÓN DE EDIFICACIONES POST-SISMO", leftX, y1, titlePaint)
            y1 += 14f
            canvas1.drawText("EVALUACIÓN DE DAÑOS Y HABITABILIDAD EN EDIFICACIONES", leftX, y1, subtitlePaint)
            y1 += 12f
            canvas1.drawText(
                "Fecha: ${sdf.format(Date(ins.fechaHora))}  |  ID: ${ins.id.take(8)}  |  Estado: ${ins.estado}",
                leftX, y1, textPaint
            )
            y1 += 8f
            canvas1.drawLine(leftX, y1, leftX + contentWidth, y1, linePaint)
            y1 += 10f

            // --- SECCIÓN 1: IDENTIFICACIÓN DE LA EDIFICACIÓN ---
            drawSectionBanner(canvas1, leftX, y1, "1. IDENTIFICACIÓN DE LA EDIFICACIÓN", bannerBgPaint, bannerTextPaint, contentWidth)
            y1 += 20f

            drawLabeledTextPair(canvas1, leftX, y1, "Localidad:", ins.localidad.ifBlank { "N/A" }, labelPaint, textPaint)
            drawLabeledTextPair(canvas1, leftX + 220f, y1, "Barrio:", "${ins.barrio.ifBlank { "N/A" }} (Cód: ${ins.codigoBarrio.ifBlank { "N/A" }})", labelPaint, textPaint)
            y1 += 17f

            drawLabeledTextPair(canvas1, leftX, y1, "Dirección:", "${ins.tipoVia} ${ins.direccion}".ifBlank { "N/A" }, labelPaint, textPaint)
            drawLabeledTextPair(canvas1, leftX + 220f, y1, "Normalizada:", ins.direccionNormalizada.ifBlank { "N/A" }, labelPaint, textPaint)
            y1 += 17f

            drawLabeledTextPair(canvas1, leftX, y1, "Cédula Catastral:", "Manzana: ${ins.idCatastralManzana.ifBlank { "---" }}  |  Predio: ${ins.idCatastralPredio.ifBlank { "---" }}  |  Construcción: ${ins.idCatastralConstruccion.ifBlank { "---" }}", labelPaint, textPaint)
            y1 += 17f

            drawLabeledTextPair(canvas1, leftX, y1, "Coordenadas GPS:", "Lat: ${ins.latitud}  |  Lon: ${ins.longitud}", labelPaint, textPaint)
            y1 += 18f
            canvas1.drawLine(leftX, y1, leftX + contentWidth, y1, linePaint)
            y1 += 12f

            // --- SECCIÓN 2: DESCRIPCIÓN DE LA EDIFICACIÓN Y ESTRUCTURA ---
            drawSectionBanner(canvas1, leftX, y1, "2. DESCRIPCIÓN DE LA EDIFICACIÓN Y ESTRUCTURA", bannerBgPaint, bannerTextPaint, contentWidth)
            y1 += 20f

            drawLabeledTextPair(canvas1, leftX, y1, "Nombre / Edificio:", ins.nombreEdificacion.ifBlank { "N/A" }, labelPaint, textPaint)
            y1 += 17f

            drawLabeledTextPair(canvas1, leftX, y1, "Uso Predominante:", ins.usoPredominante.ifBlank { "N/A" }, labelPaint, textPaint)
            drawLabeledTextPair(canvas1, leftX + 250f, y1, "Uso Planta Baja:", ins.usoPlantaBaja.ifBlank { "N/A" }, labelPaint, textPaint)
            y1 += 17f

            drawLabeledTextPair(canvas1, leftX, y1, "Niveles Terreno:", "${ins.niveles}", labelPaint, textPaint)
            drawLabeledTextPair(canvas1, leftX + 130f, y1, "Sótanos:", "${ins.sotanos}", labelPaint, textPaint)
            drawLabeledTextPair(canvas1, leftX + 220f, y1, "Total Pisos:", "${ins.niveles + ins.sotanos}", labelPaint, textPaint)
            drawLabeledTextPair(canvas1, leftX + 330f, y1, "Año Construcción:", if (ins.anioConstruccion > 0) "${ins.anioConstruccion}" else "N/A", labelPaint, textPaint)
            y1 += 17f

            // TAREA CRÍTICA 1: IMPRIMIR DIMENSIONES FÍSICAS (Frente y Fondo)
            val frenteStr = if (ins.frente > 0f) "${ins.frente} m" else "[ ______ ] m"
            val fondoStr = if (ins.fondo > 0f) "${ins.fondo} m" else "[ ______ ] m"
            canvas1.drawText("Dimensiones Físicas:", leftX, y1, labelPaint)
            drawLabeledTextPair(canvas1, leftX + 115f, y1, "Frente (m):", frenteStr, labelPaint, textPaint)
            drawLabeledTextPair(canvas1, leftX + 280f, y1, "Fondo (m):", fondoStr, labelPaint, textPaint)
            y1 += 17f

            drawLabeledTextPair(canvas1, leftX, y1, "Sistema Estructural:", ins.sistemaEstructural.ifBlank { "N/A" }, labelPaint, textPaint)
            drawLabeledTextPair(canvas1, leftX + 260f, y1, "Sistema Planta Baja:", ins.sistemaPlantaBaja.ifBlank { "N/A" }, labelPaint, textPaint)
            y1 += 17f

            drawLabeledTextPair(canvas1, leftX, y1, "Tipo de Entrepiso:", ins.tipoEntrepiso.ifBlank { "N/A" }, labelPaint, textPaint)
            y1 += 18f
            canvas1.drawLine(leftX, y1, leftX + contentWidth, y1, linePaint)
            y1 += 12f

            // --- SECCIÓN 3: EVALUACIÓN DE DAÑOS Y HABITABILIDAD ---
            drawSectionBanner(canvas1, leftX, y1, "3. EVALUACIÓN DE DAÑOS Y HABITABILIDAD", bannerBgPaint, bannerTextPaint, contentWidth)
            y1 += 20f

            // 3.1 Estado General / Inclinación / Cimentación
            drawLabeledTextPair(canvas1, leftX, y1, "Colapso / Estado Crítico:", ins.colapsoEstado.ifBlank { "No determinado" }, labelPaint, textPaint)
            drawLabeledTextPair(canvas1, leftX + 220f, y1, "Inclinación:", ins.desviacionInclinacion.ifBlank { "No determinado" }, labelPaint, textPaint)
            drawLabeledTextPair(canvas1, leftX + 370f, y1, "Falla Cimentación:", ins.fallaCimentacion.ifBlank { "No determinado" }, labelPaint, textPaint)
            y1 += 18f

            // TAREA CRÍTICA 2: MATRICES DE EVALUACIÓN DE DAÑOS
            val damageHeaders = listOf("Elemento / Componente", "1. Ninguno", "2. Leve", "3. Moderado", "4. Fuerte", "5. Severo")
            val damageColWidths = listOf(175f, 72f, 72f, 72f, 72f, 72f)

            // 3.2 Matriz de Daños Estructurales
            canvas1.drawText("3.2 DAÑOS EN ELEMENTOS ESTRUCTURALES", leftX, y1, labelPaint)
            y1 += 12f
            drawTableHeader(canvas1, leftX, y1, damageColWidths, damageHeaders, tableHeaderBgPaint, tableHeaderPaint, borderPaint, rowHeight = 16f)
            y1 += 16f

            val structuralRows = listOf(
                "Columnas / Muros Portantes" to getDamageColumnIndex(ins.danosColumnas),
                "Vigas" to getDamageColumnIndex(ins.danosVigas),
                "Muros de Carga" to getDamageColumnIndex(ins.danosMuros),
                "Nudos / Conexiones" to getDamageColumnIndex(ins.danosNudos)
            )
            for ((label, colIdx) in structuralRows) {
                drawTableRowWithChoice(canvas1, leftX, y1, damageColWidths, label, colIdx, textPaint, markPaint, borderPaint, rowHeight = 15f)
                y1 += 15f
            }
            y1 += 8f

            // 3.3 Matriz de Daños Arquitectónicos
            canvas1.drawText("3.3 DAÑOS EN ELEMENTOS ARQUITECTÓNICOS E INSTALACIONES", leftX, y1, labelPaint)
            y1 += 12f
            drawTableHeader(canvas1, leftX, y1, damageColWidths, damageHeaders, tableHeaderBgPaint, tableHeaderPaint, borderPaint, rowHeight = 16f)
            y1 += 16f

            val archServicesDetail = mutableListOf<String>()
            if (ins.instalacionesAcueducto) archServicesDetail.add("Acueducto")
            if (ins.instalacionesAlcantarillado) archServicesDetail.add("Alcantarillado")
            if (ins.instalacionesEnergia) archServicesDetail.add("Energía")
            if (ins.instalacionesGas) archServicesDetail.add("Gas")
            val servicesText = if (archServicesDetail.isNotEmpty()) " (${archServicesDetail.joinToString()})" else ""

            val archRows = listOf(
                "Fachadas y antepechos" to getDamageColumnIndex(ins.danoFachadasAntepechos),
                "Muros divisorios o particiones" to getDamageColumnIndex(ins.danoMurosDivisorios),
                "Cielo rasos y luminarias" to getDamageColumnIndex(ins.danoCieloRasosLuminarias),
                "Cubierta" to getDamageColumnIndex(ins.danoCubierta),
                "Escaleras" to getDamageColumnIndex(ins.danoEscaleras),
                "Instalaciones$servicesText" to getDamageColumnIndex(ins.danoInstalaciones),
                "Tanques elevados" to getDamageColumnIndex(ins.danoTanquesElevados)
            )
            for ((label, colIdx) in archRows) {
                drawTableRowWithChoice(canvas1, leftX, y1, damageColWidths, label, colIdx, textPaint, markPaint, borderPaint, rowHeight = 15f)
                y1 += 15f
            }
            y1 += 8f

            // 3.4 Matriz de Problemas Geotécnicos
            val geoHeaders = listOf("Problema Geotécnico", "1. No", "2. Puntual", "3. General")
            val geoColWidths = listOf(235f, 100f, 100f, 100f)

            canvas1.drawText("3.4 PROBLEMAS GEOTÉCNICOS", leftX, y1, labelPaint)
            y1 += 12f
            drawTableHeader(canvas1, leftX, y1, geoColWidths, geoHeaders, tableHeaderBgPaint, tableHeaderPaint, borderPaint, rowHeight = 16f)
            y1 += 16f

            val geoRows = listOf(
                "Falla en talud / Movimientos en masa" to getGeotechnicalColumnIndex(ins.fallaTaludMovimientos),
                "Asentamiento, subsidencia o licuación" to getGeotechnicalColumnIndex(ins.asentamientoLicuacion)
            )
            for ((label, colIdx) in geoRows) {
                drawTableRowWithChoice(canvas1, leftX, y1, geoColWidths, label, colIdx, textPaint, markPaint, borderPaint, rowHeight = 15f)
                y1 += 15f
            }
            y1 += 12f

            // 3.5 Clasificación de Habitabilidad y Daño Global
            val colorHabitabilidad = when (ins.habitabilidad.uppercase()) {
                "HABITABLE", "VERDE" -> Color.rgb(46, 125, 50)
                "USO RESTRINGIDO", "AMARILLO" -> Color.rgb(245, 124, 0)
                "NO HABITABLE", "NARANJA", "ROJO" -> Color.rgb(198, 40, 40)
                else -> Color.DKGRAY
            }
            val badgePaint = Paint().apply {
                color = colorHabitabilidad
                style = Paint.Style.FILL
            }
            val whiteText = Paint().apply {
                color = Color.WHITE
                textSize = 9.5f
                typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            }

            canvas1.drawRect(leftX, y1, leftX + 220f, y1 + 20f, badgePaint)
            val habText = "CLASIFICACIÓN: ${ins.habitabilidad.ifBlank { "NO EVALUADO" }}"
            canvas1.drawText(habText, leftX + 10f, y1 + 14f, whiteText)

            canvas1.drawText("Porcentaje Daño Global Estimado:", leftX + 240f, y1 + 14f, labelPaint)
            canvas1.drawText("${ins.porcentajeDanoGlobal}%", leftX + 410f, y1 + 14f, titlePaint)
            y1 += 28f

            // Cuadro Esquema / Anexo Foto
            val boxPaint = Paint().apply {
                color = Color.LTGRAY
                style = Paint.Style.STROKE
                strokeWidth = 0.8f
            }
            val boxHeight = 70f
            canvas1.drawRect(leftX, y1, leftX + contentWidth, y1 + boxHeight, boxPaint)
            canvas1.drawText(
                "REGISTRO VISUAL Y FOTOGRÁFICO DE LA EDIFICACIÓN",
                leftX + 130f, y1 + 28f, labelPaint
            )
            canvas1.drawText(
                "Ver Anexo Fotográfico adjunto (${fotos.size} Fotografías registradas)",
                leftX + 140f, y1 + 46f, textPaint
            )

            // Pie de Página 1
            canvas1.drawText("Página 1 de $totalPages", A4_WIDTH - 100f, A4_HEIGHT - 25f, textPaint)
            pdfDocument.finishPage(page1)


            // =========================================================
            // PÁGINA 2: RECOMENDACIONES, PRE-EXISTENTES Y OCUPACIÓN
            // =========================================================
            val pageInfo2 = PdfDocument.PageInfo.Builder(A4_WIDTH, A4_HEIGHT, 2).create()
            val page2 = pdfDocument.startPage(pageInfo2)
            val canvas2 = page2.canvas
            var y2 = 25f

            // --- SECCIÓN 4: RECOMENDACIONES Y MEDIDAS DE SEGURIDAD ---
            drawSectionBanner(canvas2, leftX, y2, "4. RECOMENDACIONES Y MEDIDAS DE SEGURIDAD", bannerBgPaint, bannerTextPaint, contentWidth)
            y2 += 18f

            // TAREA CRÍTICA 3: CHECKLIST DE MEDIDAS DE SEGURIDAD (Cuadrícula de Verificación)
            canvas2.drawText("4.1 Cuadrícula de Medidas de Seguridad Recomendadas", leftX, y2, labelPaint)
            y2 += 10f

            val safetyMeasures = listOf(
                "Restringir paso de peatones" to isActionChecked(ins.accionesSeguridad, listOf("peaton", "peatones", "paso")),
                "Restringir tráfico vehicular" to isActionChecked(ins.accionesSeguridad, listOf("vehicul", "tráfico", "trafico")),
                "Apuntalar edificación" to isActionChecked(ins.accionesSeguridad, listOf("apuntalar", "apuntalamiento")),
                "Demoler elementos en peligro" to isActionChecked(ins.accionesSeguridad, listOf("demoler", "demolicion", "demolición")),
                "Evacuar parcialmente edificación" to isActionChecked(ins.accionesSeguridad, listOf("parcial", "parcialmente")),
                "Evacuar totalmente edificación" to isActionChecked(ins.accionesSeguridad, listOf("total", "totalmente")),
                "Evacuar edificaciones vecinas" to isActionChecked(ins.accionesSeguridad, listOf("vecina", "vecinas")),
                "Desconectar Energía / Gas / Agua" to isActionChecked(ins.accionesSeguridad, listOf("desconectar", "servicios", "energía", "gas", "agua")),
                "Manejo de sustancias peligrosas" to isActionChecked(ins.accionesSeguridad, listOf("sustancia", "peligrosa")),
                "Visita especializada: Estructurales" to isActionChecked(ins.accionesSeguridad, listOf("estructural")),
                "Visita especializada: Geotécnicos" to isActionChecked(ins.accionesSeguridad, listOf("geotecnic", "geotécnic")),
                "Visita especializada: Serv. Públicos" to isActionChecked(ins.accionesSeguridad, listOf("público", "publico", "servicios"))
            )

            // Dibujar en cuadrícula de 2 columnas x 6 filas
            val col1X = leftX
            val col2X = leftX + 270f
            var rowY = y2

            for (i in safetyMeasures.indices step 2) {
                val (label1, checked1) = safetyMeasures[i]
                drawCheckboxItem(canvas2, col1X, rowY, label1, checked1, labelPaint, textPaint, markPaint)

                if (i + 1 < safetyMeasures.size) {
                    val (label2, checked2) = safetyMeasures[i + 1]
                    drawCheckboxItem(canvas2, col2X, rowY, label2, checked2, labelPaint, textPaint, markPaint)
                }
                rowY += 14f
            }
            y2 = rowY + 6f

            canvas2.drawText("Lugares que requieren medidas de seguridad:", leftX, y2, labelPaint)
            y2 += 11f
            y2 = drawMultilineText(
                canvas2,
                ins.lugaresMedidasSeguridad.ifBlank { "_____ / Ninguno especificado" },
                leftX,
                y2,
                contentWidth,
                textPaint
            )
            y2 += 6f

            // Checklist de Intervención Requerida
            canvas2.drawText("4.2 Intervención Entidades Requerida:", leftX, y2, labelPaint)
            y2 += 12f

            val interventions = listOf(
                "Planeación / Control Físico" to isActionChecked(ins.intervencionesRequeridas, listOf("planeacion", "planeación", "control")),
                "Policía / Ejército" to isActionChecked(ins.intervencionesRequeridas, listOf("policia", "policía", "ejército", "ejercito")),
                "Tránsito" to isActionChecked(ins.intervencionesRequeridas, listOf("transito", "tránsito")),
                "Bomberos / Rescate" to isActionChecked(ins.intervencionesRequeridas, listOf("bombero", "bomberos", "rescate"))
            )

            var intX = leftX
            for ((intLabel, intChecked) in interventions) {
                drawCheckboxItem(canvas2, intX, y2, intLabel, intChecked, labelPaint, textPaint, markPaint)
                intX += 132f
            }
            y2 += 18f
            canvas2.drawLine(leftX, y2, leftX + contentWidth, y2, linePaint)
            y2 += 10f


            // TAREA CRÍTICA 4: CONDICIONES PRE-EXISTENTES Y OCUPACIÓN
            drawSectionBanner(canvas2, leftX, y2, "5. CONDICIONES PRE-EXISTENTES Y OCUPACIÓN", bannerBgPaint, bannerTextPaint, contentWidth)
            y2 += 20f

            val preHeaders = listOf("Condición Pre-existente / Parámetro", "1. Buena", "2. Regular", "3. Mala")
            val preColWidths = listOf(235f, 100f, 100f, 100f)

            canvas2.drawText("5.1 Matriz de Evaluaciones Pre-existentes", leftX, y2, labelPaint)
            y2 += 12f
            drawTableHeader(canvas2, leftX, y2, preColWidths, preHeaders, tableHeaderBgPaint, tableHeaderPaint, borderPaint, rowHeight = 16f)
            y2 += 16f

            val preRows = listOf(
                "Calidad de la Construcción" to getEvaluationColumnIndex(ins.calidadConstruccion),
                "Configuración en Planta" to getEvaluationColumnIndex(ins.configPlanta),
                "Configuración en Altura" to getEvaluationColumnIndex(ins.configAltura),
                "Configuración Estructural" to getEvaluationColumnIndex(ins.configEstructural)
            )
            for ((label, colIdx) in preRows) {
                drawTableRowWithChoice(canvas2, leftX, y2, preColWidths, label, colIdx, textPaint, markPaint, borderPaint, rowHeight = 15f)
                y2 += 15f
            }
            y2 += 8f

            drawLabeledTextPair(canvas2, leftX, y2, "Posición en Manzana:", ins.posicionManzana.ifBlank { "N/A" }, labelPaint, textPaint)
            drawLabeledTextPair(canvas2, leftX + 260f, y2, "Indicios Daños Anteriores:", if (ins.indiciosDanosAnteriores) "SÍ" else "NO", labelPaint, textPaint)
            y2 += 17f

            drawLabeledTextPair(canvas2, leftX, y2, "Tipo de Reparación Previa:", ins.tipoReparacion.ifBlank { "Ninguna" }, labelPaint, textPaint)
            y2 += 18f

            // 5.2 Efecto en Ocupantes
            canvas2.drawText("5.2 Efecto en Ocupantes y Víctimas", leftX, y2, labelPaint)
            y2 += 14f
            drawLabeledTextPair(canvas2, leftX, y2, "Hubo Muertos / Heridos / Desaparecidos:", ins.huboMuertosOheridos.ifBlank { "No determinado" }, labelPaint, textPaint)
            y2 += 16f
            val nF = if (ins.numFallecidos.isNotBlank()) ins.numFallecidos else "0"
            val nH = if (ins.numHeridos.isNotBlank()) ins.numHeridos else "0"
            val nD = if (ins.numDesaparecidos.isNotBlank()) ins.numDesaparecidos else "0"
            drawLabeledTextPair(canvas2, leftX, y2, "N° Fallecidos:", nF, labelPaint, textPaint)
            drawLabeledTextPair(canvas2, leftX + 160f, y2, "N° Heridos:", nH, labelPaint, textPaint)
            drawLabeledTextPair(canvas2, leftX + 320f, y2, "N° Desaparecidos:", nD, labelPaint, textPaint)
            y2 += 18f

            // 5.3 Ocupación / Unidades Residenciales y Comerciales
            canvas2.drawText("5.3 Ocupación y Unidades Habitacionales", leftX, y2, labelPaint)
            y2 += 14f
            drawLabeledTextPair(canvas2, leftX, y2, "Edificación Habitada Actualmente:", if (ins.estaHabitada) "SÍ" else "NO", labelPaint, textPaint)
            y2 += 17f

            val unidExist = if (ins.unidadesExistentes.isNotBlank()) ins.unidadesExistentes else "_____"
            val unidNoHab = if (ins.unidadesNoHabitables.isNotBlank()) ins.unidadesNoHabitables else "_____"
            drawLabeledTextPair(canvas2, leftX, y2, "Número de Unidades Existentes (Residenciales/Comerciales):", unidExist, labelPaint, textPaint)
            y2 += 17f

            drawLabeledTextPair(canvas2, leftX, y2, "Número de Unidades No Habitables:", unidNoHab, labelPaint, textPaint)
            y2 += 20f
            canvas2.drawLine(leftX, y2, leftX + contentWidth, y2, linePaint)
            y2 += 12f


            // --- SECCIÓN 6: PERSONA DE CONTACTO, COMENTARIOS E INSPECTORES ---
            drawSectionBanner(canvas2, leftX, y2, "6. PERSONA DE CONTACTO, COMENTARIOS E INSPECTORES", bannerBgPaint, bannerTextPaint, contentWidth)
            y2 += 20f

            drawLabeledTextPair(canvas2, leftX, y2, "Persona para Contacto:", ins.nombreContacto.ifBlank { "N/A" }, labelPaint, textPaint)
            drawLabeledTextPair(canvas2, leftX + 280f, y2, "Teléfono:", ins.telefonoContacto.ifBlank { "N/A" }, labelPaint, textPaint)
            y2 += 17f

            canvas2.drawText("Comentarios y Observaciones Adicionales:", leftX, y2, labelPaint)
            y2 += 14f
            y2 = drawMultilineText(
                canvas2,
                ins.comentarios.ifBlank { "Sin observaciones adicionales registradas." },
                leftX,
                y2,
                contentWidth,
                textPaint
            )
            y2 += 16f

            drawLabeledTextPair(canvas2, leftX, y2, "Código Comisión:", ins.codigoComision.ifBlank { "N/A" }, labelPaint, textPaint)
            drawLabeledTextPair(canvas2, leftX + 180f, y2, "N° Evaluadores:", "${ins.numeroEvaluadores}", labelPaint, textPaint)
            drawLabeledTextPair(canvas2, leftX + 320f, y2, "Inspector Líder:", ins.inspectorLider.ifBlank { "N/A" }, labelPaint, textPaint)
            y2 += 24f

            // Firma Digital
            canvas2.drawText("Firma Inspector Líder:", leftX, y2 + 10f, labelPaint)
            if (ins.firmaInspector.isNotBlank()) {
                val signatureBitmap = decodeBase64ToBitmap(ins.firmaInspector)
                if (signatureBitmap != null) {
                    val scaledSig = Bitmap.createScaledBitmap(signatureBitmap, 160, 50, true)
                    canvas2.drawBitmap(scaledSig, leftX + 120f, y2 - 10f, null)
                } else {
                    canvas2.drawLine(leftX + 120f, y2 + 15f, leftX + 280f, y2 + 15f, linePaint)
                    canvas2.drawText("(Firmado Digitalmente)", leftX + 140f, y2 + 10f, textPaint)
                }
            } else {
                canvas2.drawLine(leftX + 120f, y2 + 15f, leftX + 280f, y2 + 15f, linePaint)
                canvas2.drawText("(Pendiente de Firma)", leftX + 140f, y2 + 10f, textPaint)
            }

            // Pie de Página 2
            canvas2.drawText("Página 2 de $totalPages", A4_WIDTH - 100f, A4_HEIGHT - 25f, textPaint)
            pdfDocument.finishPage(page2)


            // =========================================================
            // ANEXOS DINÁMICOS (Desaparecidos y Fotos)
            // =========================================================
            var currentAnnexPage = 3
            if (hasDesaparecidos) {
                currentAnnexPage = crearAnexoDesaparecidos(
                    pdfDocument = pdfDocument,
                    ins = ins,
                    startPageNum = currentAnnexPage,
                    totalPages = totalPages,
                    titlePaint = titlePaint,
                    labelPaint = labelPaint,
                    textPaint = textPaint,
                    bannerBgPaint = bannerBgPaint,
                    bannerTextPaint = bannerTextPaint,
                    linePaint = linePaint
                )
            }

            if (fotos.isNotEmpty()) {
                crearAnexoFotografico(
                    pdfDocument = pdfDocument,
                    fotos = fotos,
                    startPageNumber = currentAnnexPage,
                    totalPages = totalPages,
                    titlePaint = titlePaint,
                    textPaint = textPaint
                )
            }

            // =========================================================
            // GUARDAR PDF
            // =========================================================
            val pdfDir = File(context.filesDir, "reportes_pdf")
            if (!pdfDir.exists()) pdfDir.mkdirs()

            val file = File(pdfDir, "Inspeccion_${ins.id.take(8)}.pdf")
            FileOutputStream(file).use { outputStream ->
                pdfDocument.writeTo(outputStream)
            }
            pdfDocument.close()

            return@withContext file
        }

    // --- FUNCIONES AUXILIARES DE DIBUJO Y TABLAS ---

    private fun drawLabeledTextPair(
        canvas: Canvas,
        x: Float,
        y: Float,
        label: String,
        value: String,
        labelPaint: Paint,
        textPaint: Paint,
        minOffset: Float = 0f
    ): Float {
        canvas.drawText(label, x, y, labelPaint)
        val labelWidth = labelPaint.measureText(label)
        val gap = 6f
        val offset = Math.max(labelWidth + gap, minOffset)
        val valueX = x + offset
        canvas.drawText(value, valueX, y, textPaint)
        return valueX + textPaint.measureText(value)
    }

    private fun drawSectionBanner(
        canvas: Canvas,
        x: Float,
        y: Float,
        title: String,
        bgPaint: Paint,
        textPaint: Paint,
        width: Float
    ) {
        val bannerHeight = 14f
        canvas.drawRect(x, y, x + width, y + bannerHeight, bgPaint)
        canvas.drawText(title, x + 6f, y + 10.5f, textPaint)
    }

    private fun drawTableHeader(
        canvas: Canvas,
        x: Float,
        y: Float,
        columnsWidths: List<Float>,
        headers: List<String>,
        paintHeaderBg: Paint,
        paintTextHeader: Paint,
        paintBorder: Paint,
        rowHeight: Float = 14f
    ) {
        val totalWidth = columnsWidths.sum()
        canvas.drawRect(x, y, x + totalWidth, y + rowHeight, paintHeaderBg)

        var currentX = x
        for (i in headers.indices) {
            val colWidth = columnsWidths[i]
            canvas.drawRect(currentX, y, currentX + colWidth, y + rowHeight, paintBorder)
            val text = headers[i]
            val textWidth = paintTextHeader.measureText(text)
            val xText = if (i == 0) currentX + 4f else currentX + (colWidth - textWidth) / 2f
            canvas.drawText(text, xText, y + rowHeight - 3.5f, paintTextHeader)
            currentX += colWidth
        }
    }

    private fun drawTableRowWithChoice(
        canvas: Canvas,
        x: Float,
        y: Float,
        columnsWidths: List<Float>,
        label: String,
        selectedIndex: Int, // -1 si ninguna seleccionada
        paintText: Paint,
        paintMark: Paint,
        paintBorder: Paint,
        rowHeight: Float = 13f
    ) {
        var currentX = x
        for (i in columnsWidths.indices) {
            val colWidth = columnsWidths[i]
            canvas.drawRect(currentX, y, currentX + colWidth, y + rowHeight, paintBorder)
            if (i == 0) {
                canvas.drawText(label, currentX + 4f, y + rowHeight - 3f, paintText)
            } else {
                if (i - 1 == selectedIndex) {
                    val mark = "X"
                    val markWidth = paintMark.measureText(mark)
                    canvas.drawText(mark, currentX + (colWidth - markWidth) / 2f, y + rowHeight - 2.5f, paintMark)
                }
            }
            currentX += colWidth
        }
    }

    private fun drawCheckboxItem(
        canvas: Canvas,
        x: Float,
        y: Float,
        label: String,
        isChecked: Boolean,
        labelPaint: Paint,
        textPaint: Paint,
        markPaint: Paint
    ) {
        val boxWidth = 9f
        val boxHeight = 9f
        val boxPaint = Paint().apply {
            color = Color.BLACK
            style = Paint.Style.STROKE
            strokeWidth = 0.8f
        }
        canvas.drawRect(x, y - 7f, x + boxWidth, y - 7f + boxHeight, boxPaint)
        if (isChecked) {
            canvas.drawText("X", x + 1.5f, y, markPaint)
        }
        canvas.drawText(label, x + boxWidth + 4f, y, textPaint)
    }

    private fun getDamageColumnIndex(value: String?): Int {
        if (value.isNullOrBlank()) return -1
        val valLower = value.lowercase()
        return when {
            valLower.startsWith("1") || valLower.contains("ninguno") -> 0
            valLower.startsWith("2") || valLower.contains("leve") -> 1
            valLower.startsWith("3") || valLower.contains("moderado") -> 2
            valLower.startsWith("4") || valLower.contains("fuerte") -> 3
            valLower.startsWith("5") || valLower.contains("severo") -> 4
            else -> -1
        }
    }

    private fun getGeotechnicalColumnIndex(value: String?): Int {
        if (value.isNullOrBlank()) return -1
        val valLower = value.lowercase()
        return when {
            valLower.startsWith("1") || valLower.contains("no") -> 0
            valLower.startsWith("2") || valLower.contains("puntual") -> 1
            valLower.startsWith("3") || valLower.contains("general") -> 2
            else -> -1
        }
    }

    private fun getEvaluationColumnIndex(value: String?): Int {
        if (value.isNullOrBlank()) return -1
        val valLower = value.lowercase()
        return when {
            valLower.contains("buena") || valLower.startsWith("1") -> 0
            valLower.contains("regular") || valLower.startsWith("2") -> 1
            valLower.contains("mala") || valLower.startsWith("3") -> 2
            else -> -1
        }
    }

    private fun isActionChecked(source: String?, keywords: List<String>): Boolean {
        if (source.isNullOrBlank()) return false
        val lower = source.lowercase()
        return keywords.any { lower.contains(it.lowercase()) }
    }

    private fun crearAnexoDesaparecidos(
        pdfDocument: PdfDocument,
        ins: InspeccionEntity,
        startPageNum: Int,
        totalPages: Int,
        titlePaint: Paint,
        labelPaint: Paint,
        textPaint: Paint,
        bannerBgPaint: Paint,
        bannerTextPaint: Paint,
        linePaint: Paint
    ): Int {
        val nombresList = ins.nombresDesaparecidos.split("\n").filter { it.isNotBlank() }
        val count = ins.numDesaparecidos.toIntOrNull() ?: nombresList.size

        var pageNumber = startPageNum
        val pageInfo = PdfDocument.PageInfo.Builder(A4_WIDTH, A4_HEIGHT, pageNumber).create()
        val page = pdfDocument.startPage(pageInfo)
        var canvas = page.canvas

        drawSectionBanner(canvas, 30f, 30f, "ANEXO: LISTADO DE PERSONAS DESAPARECIDAS", bannerBgPaint, bannerTextPaint, A4_WIDTH - 60f)
        var y = 60f

        val locInfo = if (ins.direccion.isNotBlank()) ins.direccion else if (ins.nombreEdificacion.isNotBlank()) ins.nombreEdificacion else "N/A"
        canvas.drawText("Ubicación / Edificación: $locInfo", 30f, y, labelPaint)
        y += 15f
        canvas.drawText("Total de Personas Desaparecidas Reportadas: $count", 30f, y, labelPaint)
        y += 20f

        canvas.drawLine(30f, y, A4_WIDTH - 30f, y, linePaint)
        y += 18f

        if (nombresList.isEmpty()) {
            canvas.drawText("No se registraron nombres individuales para las $count personas desaparecidas.", 35f, y, textPaint)
            y += 20f
        } else {
            nombresList.forEachIndexed { idx, name ->
                canvas.drawText("${idx + 1}. $name", 35f, y, textPaint)
                y += 16f
                if (y > A4_HEIGHT - 60f) {
                    canvas.drawText("Página $pageNumber de $totalPages", A4_WIDTH - 100f, A4_HEIGHT - 25f, textPaint)
                    pdfDocument.finishPage(page)
                    pageNumber++
                    val newPageInfo = PdfDocument.PageInfo.Builder(A4_WIDTH, A4_HEIGHT, pageNumber).create()
                    val newPage = pdfDocument.startPage(newPageInfo)
                    canvas = newPage.canvas
                    y = 40f
                }
            }
        }

        canvas.drawText("Página $pageNumber de $totalPages", A4_WIDTH - 100f, A4_HEIGHT - 25f, textPaint)
        pdfDocument.finishPage(page)
        return pageNumber + 1
    }

    private fun crearAnexoFotografico(
        pdfDocument: PdfDocument,
        fotos: List<FotoEntity>,
        startPageNumber: Int,
        totalPages: Int,
        titlePaint: Paint,
        textPaint: Paint
    ) {
        val fotosPorPagina = 2
        val chunks = fotos.chunked(fotosPorPagina)
        var pageNumber = startPageNumber

        for (chunk in chunks) {
            val pageInfo = PdfDocument.PageInfo.Builder(A4_WIDTH, A4_HEIGHT, pageNumber).create()
            val page = pdfDocument.startPage(pageInfo)
            val canvas = page.canvas

            canvas.drawText("ANEXO FOTOGRÁFICO DE REGISTRO VISUAL", 30f, 35f, titlePaint)
            var currentY = 65f

            chunk.forEachIndexed { index, foto ->
                val numeroFotoGlobal = ((pageNumber - startPageNumber) * fotosPorPagina) + index + 1

                canvas.drawText("Fotografía N° $numeroFotoGlobal", 30f, currentY, titlePaint)
                currentY += 15f

                val bitmap = loadAndScaleBitmap(foto.uri, maxWidth = 480, maxHeight = 250)
                if (bitmap != null) {
                    val xPos = (A4_WIDTH - bitmap.width) / 2f
                    canvas.drawBitmap(bitmap, xPos, currentY, null)
                    currentY += bitmap.height + 15f
                } else {
                    canvas.drawText("[Imagen no disponible o no accesible]", 30f, currentY, textPaint)
                    currentY += 35f
                }

                currentY = drawMultilineText(
                    canvas,
                    "Observación: ${foto.observacion.ifBlank { "Sin descripción grabada." }}",
                    30f,
                    currentY,
                    A4_WIDTH - 60f,
                    textPaint
                )
                currentY += 25f
            }

            canvas.drawText("Página $pageNumber de $totalPages", A4_WIDTH - 100f, A4_HEIGHT - 25f, textPaint)

            pdfDocument.finishPage(page)
            pageNumber++
        }
    }

    private fun loadAndScaleBitmap(uriString: String, maxWidth: Int, maxHeight: Int): Bitmap? {
        return try {
            val uri = Uri.parse(uriString)
            val inputStream = context.contentResolver.openInputStream(uri)
                ?: File(uri.path ?: "").inputStream()
            val originalBitmap = BitmapFactory.decodeStream(inputStream)
            inputStream.close()

            if (originalBitmap == null) return null

            val ratio = Math.min(
                maxWidth.toFloat() / originalBitmap.width,
                maxHeight.toFloat() / originalBitmap.height
            )
            val width = Math.round(ratio * originalBitmap.width)
            val height = Math.round(ratio * originalBitmap.height)

            Bitmap.createScaledBitmap(originalBitmap, width, height, true)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    private fun decodeBase64ToBitmap(base64Str: String): Bitmap? {
        return try {
            val cleanBase64 = if (base64Str.contains(",")) base64Str.substringAfter(",") else base64Str
            val decodedBytes = Base64.decode(cleanBase64, Base64.DEFAULT)
            BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.size)
        } catch (e: Exception) {
            null
        }
    }

    private fun drawMultilineText(
        canvas: Canvas,
        text: String,
        x: Float,
        y: Float,
        maxWidth: Float,
        paint: Paint
    ): Float {
        var currentY = y
        val words = text.split(" ")
        var line = ""

        for (word in words) {
            val testLine = if (line.isEmpty()) word else "$line $word"
            val testWidth = paint.measureText(testLine)
            if (testWidth > maxWidth) {
                canvas.drawText(line, x, currentY, paint)
                line = word
                currentY += paint.textSize + 7.5f // Espaciado ampliado +4pt para legibilidad editorial
            } else {
                line = testLine
            }
        }
        if (line.isNotEmpty()) {
            canvas.drawText(line, x, currentY, paint)
            currentY += paint.textSize + 7.5f // Espaciado ampliado +4pt para legibilidad editorial
        }
        return currentY
    }

    companion object {
        fun compartirPdf(context: Context, pdfFile: File) {
            val uri: Uri = FileProvider.getUriForFile(
                context,
                "${context.packageName}.fileprovider",
                pdfFile
            )

            val intent = Intent(Intent.ACTION_SEND).apply {
                type = "application/pdf"
                putExtra(Intent.EXTRA_STREAM, uri)
                putExtra(Intent.EXTRA_SUBJECT, "Reporte de Inspección Oficial - Formulario Post-Sismo")
                putExtra(Intent.EXTRA_TEXT, "Se adjunta el reporte oficial consolidado de inspección de edificación.")
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }

            context.startActivity(Intent.createChooser(intent, "Compartir Reporte PDF"))
        }
    }
}
