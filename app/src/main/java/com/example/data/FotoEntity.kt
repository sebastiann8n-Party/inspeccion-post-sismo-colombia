package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.UUID

@Entity(tableName = "fotos")
data class FotoEntity(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    val inspeccionId: String, // FK a InspeccionEntity
    val uri: String,          // URI local del archivo
    val observacion: String = ""
)
