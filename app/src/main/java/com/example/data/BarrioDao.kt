package com.example.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface BarrioDao {
    @Query("SELECT * FROM barrios ORDER BY nombre ASC")
    fun getAllBarrios(): Flow<List<BarrioEntity>>

    @Query("SELECT * FROM barrios ORDER BY nombre ASC")
    suspend fun getAllBarriosList(): List<BarrioEntity>

    @Query("SELECT * FROM barrios WHERE id = :id LIMIT 1")
    suspend fun getBarrioById(id: String): BarrioEntity?

    @Query("SELECT * FROM barrios WHERE localidad = :localidad ORDER BY nombre ASC")
    fun getBarriosByLocalidad(localidad: String): Flow<List<BarrioEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBarrios(barrios: List<BarrioEntity>)

    @Query("SELECT COUNT(*) FROM barrios")
    suspend fun getCount(): Int
}
