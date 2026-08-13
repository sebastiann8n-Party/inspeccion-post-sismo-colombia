package com.example.utils

import android.content.Context
import com.example.data.InspeccionConFotos
import com.example.util.PdfExportService
import kotlinx.coroutines.runBlocking
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object ReportGenerator {

    fun generatePdf(context: Context, inspeccionConFotos: InspeccionConFotos): File {
        return runBlocking {
            PdfExportService(context).generarReporteInspeccion(inspeccionConFotos)
        }
    }

    fun generateCsv(context: Context, inspecciones: List<InspeccionConFotos>): File {
        val dir = File(context.filesDir, "csvs")
        if (!dir.exists()) {
            dir.mkdir()
        }
        val file = File(dir, "reporte_inspecciones.csv")
        val builder = java.lang.StringBuilder()
        
        // CSV Header
        builder.append("ID,Fecha,Departamento,Municipio,Direccion,Localidad,Habitabilidad,Inspector,TotalFotos,EsPrueba\n")
        
        val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
        
        for (inspeccionConFotos in inspecciones) {
            val ins = inspeccionConFotos.inspeccion
            val date = dateFormat.format(Date(ins.fechaHora))
            val row = listOf(
                ins.id,
                date,
                "\"${ins.departamento}\"",
                "\"${ins.municipio}\"",
                "\"${ins.direccion}\"",
                "\"${ins.localidad}\"",
                ins.habitabilidad,
                "\"${ins.inspectorLider}\"",
                inspeccionConFotos.fotos.size.toString(),
                if (ins.esPrueba) "SI" else "NO"
            )
            builder.append(row.joinToString(",")).append("\n")
        }
        
        file.writeText(builder.toString())
        return file
    }
}

