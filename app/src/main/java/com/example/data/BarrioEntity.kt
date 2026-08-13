package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "barrios")
data class BarrioEntity(
    @PrimaryKey val id: String,          // Código único (ej. "11001-001")
    val nombre: String,                  // Nombre del barrio
    val localidad: String,               // Localidad
    val departamento: String = "Bogotá D.C.",
    val latitudCentroide: Double = 0.0,
    val longitudCentroide: Double = 0.0,
    val poligonoWkt: String? = null
)
