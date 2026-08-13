package com.example.util

import android.content.Context
import com.example.data.InspeccionConFotos
import java.io.File
import java.io.FileWriter

object CsvExporter {

    fun generateCsv(context: Context, inspeccionList: List<InspeccionConFotos>): File {
        val csvDir = File(context.filesDir, "exportaciones_csv")
        if (!csvDir.exists()) csvDir.mkdirs()
        val file = File(csvDir, "inspecciones_export.csv")

        FileWriter(file).use { writer ->
            writer.append("ID,Fecha,Localidad,Barrio,Direccion,Uso,Niveles,Habitabilidad,DanoGlobal,Lider,FirmaCapturada\n")
            for (insConFotos in inspeccionList) {
                val ins = insConFotos.inspeccion
                writer.append("\"${ins.id}\",")
                writer.append("\"${ins.fechaHora}\",")
                writer.append("\"${ins.localidad}\",")
                writer.append("\"${ins.barrio}\",")
                writer.append("\"${ins.tipoVia} ${ins.direccion}\",")
                writer.append("\"${ins.usoPredominante}\",")
                writer.append("\"${ins.niveles}\",")
                writer.append("\"${ins.habitabilidad}\",")
                writer.append("\"${ins.porcentajeDanoGlobal}%\",")
                writer.append("\"${ins.inspectorLider}\",")
                writer.append("\"${ins.firmaInspector.isNotBlank()}\"\n")
            }
        }
        return file
    }
}
