package com.example.data

import androidx.room.Embedded
import androidx.room.Relation

data class InspeccionConFotos(
    @Embedded val inspeccion: InspeccionEntity,
    @Relation(
        parentColumn = "id",
        entityColumn = "inspeccionId"
    )
    val fotos: List<FotoEntity>
)
