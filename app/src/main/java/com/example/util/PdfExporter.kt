package com.example.util

import android.content.Context
import com.example.data.InspeccionConFotos
import kotlinx.coroutines.runBlocking
import java.io.File

object PdfExporter {

    fun generatePdf(context: Context, inspeccionConFotos: InspeccionConFotos): File {
        return runBlocking {
            PdfExportService(context).generarReporteInspeccion(inspeccionConFotos)
        }
    }
}

