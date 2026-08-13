package com.example.data

enum class SistemaEstructural(val codigo: Int, val descripcion: String, val categoria: String) {
    // Concreto Reforzado
    PORTICO_CONCRETO(11, "Pórtico de concreto", "Concreto Reforzado"),
    MUROS_ESTRUCTURALES(12, "Muros estructurales", "Concreto Reforzado"),
    SISTEMAS_DUALES(13, "Sistemas duales", "Concreto Reforzado"),
    PREFABRICADOS(14, "Prefabricados", "Concreto Reforzado"),
    // Mampostería
    MAMP_CONFINADA(21, "Mampostería confinada", "Mampostería"),
    MAMP_REFORZADA(22, "Mampostería reforzada", "Mampostería"),
    MAMP_NO_REFORZADA(23, "Mampostería no reforzada", "Mampostería"),
    // Acero
    ACERO_ARRIOSTRADOS(31, "Pórticos arriostrados", "Acero"),
    ACERO_NO_ARRIOSTRADOS(32, "Pórticos no arriostrados", "Acero"),
    // Madera
    MADERA_PORTICOS(41, "Pórticos y paneles", "Madera"),
    MADERA_MIXTA(42, "Pórticos en madera y paneles mixtos", "Madera"),
    // Bahareque / Tapia
    MUROS_BAHAREQUE(51, "Muros en bahareque", "Bahareque / Tapia"),
    MUROS_TAPIA(52, "Muros en tapia", "Bahareque / Tapia"),
    // Otros
    MIXTA(50, "Mixta", "Mixtas y Otros"),
    OTROS(60, "Otros", "Mixtas y Otros");

    companion object {
        fun fromValue(value: String): SistemaEstructural? {
            if (value.isBlank()) return null
            return entries.find {
                "${it.codigo} - ${it.descripcion}" == value ||
                "${it.codigo}. ${it.descripcion}" == value ||
                it.descripcion.equals(value, ignoreCase = true) ||
                it.name.equals(value, ignoreCase = true)
            }
        }
    }
}

enum class TipoEntrepiso(val codigo: Int, val descripcion: String, val categoria: String) {
    // Concreto
    PLACA_MACIZA(11, "Placa maciza", "Concreto Reforzado"),
    PLACA_ALIGERADA(12, "Placa aligerada", "Concreto Reforzado"),
    RETICULAR_CELULADO(13, "Reticular celulado", "Concreto Reforzado"),
    // Acero
    LAMINA_COLABORANTE(21, "Lámina colaborante (Steel Deck)", "Acero"),
    VIGAS_ACERO(22, "Vigas", "Acero"),
    CERCHAS(23, "Cerchas", "Acero"),
    // Madera
    VIGAS_MADERA(31, "Vigas", "Madera"),
    MIXTA_MADERA(32, "Mixta", "Madera"),
    // Otros
    OTROS(40, "Otros", "Otros");

    companion object {
        fun fromValue(value: String): TipoEntrepiso? {
            if (value.isBlank()) return null
            return entries.find {
                "${it.codigo} - ${it.descripcion}" == value ||
                "${it.codigo}. ${it.descripcion}" == value ||
                it.descripcion.equals(value, ignoreCase = true) ||
                it.name.equals(value, ignoreCase = true)
            }
        }
    }
}

enum class UsoPredominanteOption(val codigo: Int, val descripcion: String) {
    RESIDENCIAL(1, "Residencial"),
    COMERCIAL(2, "Comercial"),
    EDUCACIONAL(3, "Educacional"),
    SALUD(4, "Salud"),
    HOTELERO(5, "Hotelero"),
    OFICINAS(6, "Oficinas"),
    INDUSTRIAL(7, "Industrial"),
    INSTITUCIONAL(8, "Institucional"),
    BODEGAS(9, "Bodegas"),
    ESTACIONAMIENTOS(10, "Estacionamientos"),
    OTROS(11, "Otros");

    companion object {
        fun fromValue(value: String): UsoPredominanteOption? {
            if (value.isBlank()) return null
            return entries.find {
                "${it.codigo}. ${it.descripcion}" == value ||
                "${it.codigo} - ${it.descripcion}" == value ||
                it.descripcion.equals(value, ignoreCase = true) ||
                it.name.equals(value, ignoreCase = true)
            }
        }
    }
}
