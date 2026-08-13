package com.example.data

import kotlinx.coroutines.flow.Flow

class InspeccionRepository(private val inspeccionDao: InspeccionDao) {

    val allInspecciones: Flow<List<InspeccionEntity>> = inspeccionDao.getAllInspecciones()
    val allInspeccionesConFotos: Flow<List<InspeccionConFotos>> = inspeccionDao.getAllInspeccionesConFotos()

    fun getInspeccionesGeoreferenciadas(): Flow<List<InspeccionConFotos>> = inspeccionDao.getInspeccionesGeoreferenciadas()

    fun getInspeccionesConFotosByBarrio(barrioId: String): Flow<List<InspeccionConFotos>> = inspeccionDao.getInspeccionesConFotosByBarrio(barrioId)

    suspend fun getInspeccionById(id: String): InspeccionEntity? {
        return inspeccionDao.getInspeccionById(id)
    }
    
    suspend fun getInspeccionConFotosById(id: String): InspeccionConFotos? {
        return inspeccionDao.getInspeccionConFotosById(id)
    }

    suspend fun insertInspeccion(inspeccion: InspeccionEntity) {
        inspeccionDao.insertInspeccion(inspeccion)
    }

    suspend fun updateEstadoInspeccion(id: String, estado: String) {
        val inspeccion = getInspeccionById(id)
        if (inspeccion != null) {
            inspeccionDao.insertInspeccion(inspeccion.copy(estado = estado))
        }
    }

    suspend fun insertFoto(foto: FotoEntity) {
        inspeccionDao.insertFoto(foto)
    }

    suspend fun deleteFotoById(id: String) {
        inspeccionDao.deleteFotoById(id)
    }

    suspend fun deleteFotosByInspeccionId(inspeccionId: String) {
        inspeccionDao.deleteFotosByInspeccionId(inspeccionId)
    }
    
    suspend fun deleteInspeccionById(id: String) {
        inspeccionDao.deleteInspeccionById(id)
    }
}
