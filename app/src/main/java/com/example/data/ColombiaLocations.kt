package com.example.data

data class DepartamentoInfo(
    val nombre: String,
    val capital: String,
    val latitudCentroide: Double,
    val longitudCentroide: Double,
    val ciudadesPrincipales: List<String> = emptyList()
)

object ColombiaLocations {
    val DEPARTAMENTOS: List<DepartamentoInfo> = listOf(
        DepartamentoInfo("Amazonas", "Leticia", -4.2153, -69.9406, listOf("Leticia", "Puerto Nariño")),
        DepartamentoInfo("Antioquia", "Medellín", 6.2442, -75.5812, listOf("Medellín", "Bello", "Itagüí", "Envigado", "Apartadó", "Rionegro", "Caucasia", "Turbo")),
        DepartamentoInfo("Arauca", "Arauca", 7.0847, -70.7591, listOf("Arauca", "Tame", "Saravena", "Arauquita")),
        DepartamentoInfo("Atlántico", "Barranquilla", 10.9685, -74.7813, listOf("Barranquilla", "Soledad", "Malambo", "Puerto Colombia", "Baranoa")),
        DepartamentoInfo("Bolívar", "Cartagena de Indias", 10.3910, -75.4794, listOf("Cartagena de Indias", "Magangué", "Turbaco", "Arjona", "El Carmen de Bolívar")),
        DepartamentoInfo("Boyacá", "Tunja", 5.5353, -73.3678, listOf("Tunja", "Duitama", "Sogamoso", "Chiquinquirá", "Puerto Boyacá")),
        DepartamentoInfo("Caldas", "Manizales", 5.0689, -75.5174, listOf("Manizales", "Villamaría", "La Dorada", "Riosucio", "Neira", "Chinchiná")),
        DepartamentoInfo("Caquetá", "Florencia", 1.6144, -75.6062, listOf("Florencia", "San Vicente del Caguán", "Puerto Rico")),
        DepartamentoInfo("Casanare", "Yopal", 5.3378, -72.3959, listOf("Yopal", "Aguazul", "Pore", "Paz de Ariporo")),
        DepartamentoInfo("Cauca", "Popayán", 2.4448, -76.6147, listOf("Popayán", "Santander de Quilichao", "Puerto Tejada", "Patía")),
        DepartamentoInfo("Cesar", "Valledupar", 10.4631, -73.2532, listOf("Valledupar", "Aguachica", "Bosconia", "Codazzi")),
        DepartamentoInfo("Chocó", "Quibdó", 5.6947, -76.6611, listOf("Quibdó", "San José del Palmar", "Istmina", "Tadó", "Condoto")),
        DepartamentoInfo("Córdoba", "Montería", 8.7480, -75.8814, listOf("Montería", "Lorica", "Cereté", "Sahagún", "Montelíbano")),
        DepartamentoInfo("Cundinamarca", "Bogotá D.C.", 4.7110, -74.0721, listOf("Bogotá D.C.", "Soacha", "Chía", "Zipaquirá", "Facatativá", "Girardot", "Fusagasugá", "Mosquera")),
        DepartamentoInfo("Guainía", "Inírida", 3.8653, -67.9239, listOf("Inírida")),
        DepartamentoInfo("Guaviare", "San José del Guaviare", 2.5648, -72.6459, listOf("San José del Guaviare", "Calamar", "El Retorno")),
        DepartamentoInfo("Huila", "Neiva", 2.9273, -75.2819, listOf("Neiva", "Pitalito", "Garzón", "La Plata")),
        DepartamentoInfo("La Guajira", "Riohacha", 11.5444, -72.9072, listOf("Riohacha", "Maicao", "Uribia", "Fonseca", "Manaure")),
        DepartamentoInfo("Magdalena", "Santa Marta", 11.2408, -74.1990, listOf("Santa Marta", "Ciénaga", "Fundación", "El Banco")),
        DepartamentoInfo("Meta", "Villavicencio", 4.1420, -73.6266, listOf("Villavicencio", "Acacías", "Granada", "Puerto López")),
        DepartamentoInfo("Nariño", "Pasto", 1.2136, -77.2811, listOf("Pasto", "Tumaco", "Ipiales", "Túquerres")),
        DepartamentoInfo("Norte de Santander", "Cúcuta", 7.8939, -72.5078, listOf("Cúcuta", "Ocaña", "Pamplona", "Villa del Rosario", "Los Patios")),
        DepartamentoInfo("Putumayo", "Mocoa", 1.1478, -76.6481, listOf("Mocoa", "Puerto Asís", "Orito", "Sibundoy")),
        DepartamentoInfo("Quindío", "Armenia", 4.5339, -75.6811, listOf("Armenia", "Calarcá", "Salento", "Montenegro", "Quimbaya")),
        DepartamentoInfo("Risaralda", "Pereira", 4.8133, -75.6961, listOf("Pereira", "Dosquebradas", "La Virginia", "Santa Rosa de Cabal")),
        DepartamentoInfo("San Andrés y Providencia", "San Andrés", 12.5847, -81.7006, listOf("San Andrés", "Providencia")),
        DepartamentoInfo("Santander", "Bucaramanga", 7.1254, -73.1198, listOf("Bucaramanga", "Floridablanca", "Girón", "Piedecuesta", "Barrancabermeja", "Los Santos", "San Gil")),
        DepartamentoInfo("Sucre", "Sincelejo", 9.3047, -75.3978, listOf("Sincelejo", "Corozal", "San Onofre", "Sincé")),
        DepartamentoInfo("Tolima", "Ibagué", 4.4389, -75.2322, listOf("Ibagué", "Espinal", "Melgar", "Honda", "Mariquita")),
        DepartamentoInfo("Valle del Cauca", "Cali", 3.4516, -76.5320, listOf("Cali", "Buenaventura", "Palmira", "Tuluá", "Cartago", "Buga", "Zarzal", "Jamundí", "Yumbo")),
        DepartamentoInfo("Vaupés", "Mitú", 1.1983, -70.1733, listOf("Mitú", "Carurú")),
        DepartamentoInfo("Vichada", "Puerto Carreño", 6.1890, -67.4859, listOf("Puerto Carreño", "La Primavera", "Cumaribo"))
    )

    fun getDepartamentoNames(): List<String> = DEPARTAMENTOS.map { it.nombre }

    fun findDepartamento(nombre: String): DepartamentoInfo? =
        DEPARTAMENTOS.find { it.nombre.equals(nombre, ignoreCase = true) }
}
