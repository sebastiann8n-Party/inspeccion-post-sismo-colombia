package com.example.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import kotlinx.coroutines.flow.Flow

@Dao
interface InspeccionDao {

    @Query("SELECT * FROM inspecciones ORDER BY fechaHora DESC")
    fun getAllInspecciones(): Flow<List<InspeccionEntity>>

    @Query("SELECT * FROM inspecciones WHERE id = :id")
    suspend fun getInspeccionById(id: String): InspeccionEntity?

    @Transaction
    @Query("SELECT * FROM inspecciones WHERE id = :id")
    suspend fun getInspeccionConFotosById(id: String): InspeccionConFotos?

    @Transaction
    @Query("SELECT * FROM inspecciones ORDER BY fechaHora DESC")
    fun getAllInspeccionesConFotos(): Flow<List<InspeccionConFotos>>

    @Transaction
    @Query("SELECT * FROM inspecciones WHERE barrioId = :barrioId ORDER BY fechaHora DESC")
    fun getInspeccionesConFotosByBarrio(barrioId: String): Flow<List<InspeccionConFotos>>

    @Transaction
    @Query("SELECT * FROM inspecciones WHERE latitud != 0.0 AND longitud != 0.0 ORDER BY fechaHora DESC")
    fun getInspeccionesGeoreferenciadas(): Flow<List<InspeccionConFotos>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertInspeccion(inspeccion: InspeccionEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFoto(foto: FotoEntity)

    @Query("DELETE FROM fotos WHERE id = :id")
    suspend fun deleteFotoById(id: String)
    
    @Query("DELETE FROM fotos WHERE inspeccionId = :inspeccionId")
    suspend fun deleteFotosByInspeccionId(inspeccionId: String)

    @Query("DELETE FROM inspecciones WHERE id = :id")
    suspend fun deleteInspeccionById(id: String)
}
