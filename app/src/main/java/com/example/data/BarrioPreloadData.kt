package com.example.data

object BarrioPreloadData {
    fun getInitialBarrios(): List<BarrioEntity> {
        return listOf(
            // USAQUÉN (Lat: ~4.71 - 4.76, Lon: ~-74.03)
            BarrioEntity("11001-001", "Usaquén Centro", "Usaquén", latitudCentroide = 4.6961, longitudCentroide = -74.0306),
            BarrioEntity("11001-002", "Santa Bárbara", "Usaquén", latitudCentroide = 4.6934, longitudCentroide = -74.0381),
            BarrioEntity("11001-003", "Cedritos", "Usaquén", latitudCentroide = 4.7212, longitudCentroide = -74.0354),
            BarrioEntity("11001-004", "San Cristóbal Norte", "Usaquén", latitudCentroide = 4.7432, longitudCentroide = -74.0289),
            BarrioEntity("11001-005", "Verbenal", "Usaquén", latitudCentroide = 4.7589, longitudCentroide = -74.0298),

            // CHAPINERO (Lat: ~4.64 - 4.68, Lon: ~-74.05)
            BarrioEntity("11001-006", "Chapinero Central", "Chapinero", latitudCentroide = 4.6436, longitudCentroide = -74.0622),
            BarrioEntity("11001-007", "El Chicó", "Chapinero", latitudCentroide = 4.6781, longitudCentroide = -74.0531),
            BarrioEntity("11001-008", "Rosales", "Chapinero", latitudCentroide = 4.6512, longitudCentroide = -74.0520),
            BarrioEntity("11001-009", "Pardo Rubio / Marly", "Chapinero", latitudCentroide = 4.6360, longitudCentroide = -74.0645),
            BarrioEntity("11001-010", "Antiguo Country", "Chapinero", latitudCentroide = 4.6678, longitudCentroide = -74.0589),

            // SANTA FE & LA CANDELARIA (Lat: ~4.59 - 4.60, Lon: ~-74.07)
            BarrioEntity("11001-011", "La Candelaria", "La Candelaria", latitudCentroide = 4.5969, longitudCentroide = -74.0728),
            BarrioEntity("11001-012", "Las Nieves", "Santa Fe", latitudCentroide = 4.6056, longitudCentroide = -74.0712),
            BarrioEntity("11001-013", "San Diego / Macarena", "Santa Fe", latitudCentroide = 4.6134, longitudCentroide = -74.0682),
            BarrioEntity("11001-014", "Las Cruces", "Santa Fe", latitudCentroide = 4.5889, longitudCentroide = -74.0789),

            // TEUSAQUILLO (Lat: ~4.62 - 4.65, Lon: ~-74.08)
            BarrioEntity("11001-015", "Teusaquillo", "Teusaquillo", latitudCentroide = 4.6298, longitudCentroide = -74.0789),
            BarrioEntity("11001-016", "Park Way / La Soledad", "Teusaquillo", latitudCentroide = 4.6321, longitudCentroide = -74.0745),
            BarrioEntity("11001-017", "Galerías", "Teusaquillo", latitudCentroide = 4.6423, longitudCentroide = -74.0732),
            BarrioEntity("11001-018", "Quinta Paredes / Salitre Sur", "Teusaquillo", latitudCentroide = 4.6367, longitudCentroide = -74.0912),

            // KENNEDY (Lat: ~4.61 - 4.63, Lon: ~-74.15)
            BarrioEntity("11001-019", "Ciudad Kennedy Central", "Kennedy", latitudCentroide = 4.6189, longitudCentroide = -74.1523),
            BarrioEntity("11001-020", "Patio Bonito", "Kennedy", latitudCentroide = 4.6312, longitudCentroide = -74.1689),
            BarrioEntity("11001-021", "Castilla", "Kennedy", latitudCentroide = 4.6412, longitudCentroide = -74.1389),
            BarrioEntity("11001-022", "Tintal Norte", "Kennedy", latitudCentroide = 4.6489, longitudCentroide = -74.1578),
            BarrioEntity("11001-023", "Marsella", "Kennedy", latitudCentroide = 4.6289, longitudCentroide = -74.1289),

            // SUBA (Lat: ~4.73 - 4.77, Lon: ~-74.08)
            BarrioEntity("11001-024", "Suba Centro", "Suba", latitudCentroide = 4.7456, longitudCentroide = -74.0834),
            BarrioEntity("11001-025", "Niza", "Suba", latitudCentroide = 4.7089, longitudCentroide = -74.0723),
            BarrioEntity("11001-026", "El Rincón", "Suba", latitudCentroide = 4.7389, longitudCentroide = -74.0989),
            BarrioEntity("11001-027", "Tibabuyes", "Suba", latitudCentroide = 4.7567, longitudCentroide = -74.1123),
            BarrioEntity("11001-028", "Prado Veraniego", "Suba", latitudCentroide = 4.7312, longitudCentroide = -74.0567),

            // ENGATIVÁ (Lat: ~4.68 - 4.71, Lon: ~-74.11)
            BarrioEntity("11001-029", "Normandía", "Engativá", latitudCentroide = 4.6689, longitudCentroide = -74.1089),
            BarrioEntity("11001-030", "Garcés Navas", "Engativá", latitudCentroide = 4.7123, longitudCentroide = -74.1189),
            BarrioEntity("11001-031", "Las Ferias", "Engativá", latitudCentroide = 4.6889, longitudCentroide = -74.0889),
            BarrioEntity("11001-032", "Quirigua", "Engativá", latitudCentroide = 4.7045, longitudCentroide = -74.1056),

            // FONTIBÓN (Lat: ~4.66 - 4.68, Lon: ~-74.14)
            BarrioEntity("11001-033", "Fontibón Centro", "Fontibón", latitudCentroide = 4.6734, longitudCentroide = -74.1423),
            BarrioEntity("11001-034", "Modelia", "Fontibón", latitudCentroide = 4.6645, longitudCentroide = -74.1189),
            BarrioEntity("11001-035", "Ciudad Salitre Occidente", "Fontibón", latitudCentroide = 4.6567, longitudCentroide = -74.1112),

            // BOSA (Lat: ~4.60 - 4.62, Lon: ~-74.18)
            BarrioEntity("11001-036", "Bosa Centro", "Bosa", latitudCentroide = 4.6089, longitudCentroide = -74.1823),
            BarrioEntity("11001-037", "El Porvenir", "Bosa", latitudCentroide = 4.6212, longitudCentroide = -74.1956),

            // CIUDAD BOLÍVAR & TUNJUELITO (Lat: ~4.55 - 4.59, Lon: ~-74.14)
            BarrioEntity("11001-038", "Venecia", "Tunjuelito", latitudCentroide = 4.5945, longitudCentroide = -74.1356),
            BarrioEntity("11001-039", "El Tunal", "Tunjuelito", latitudCentroide = 4.5789, longitudCentroide = -74.1312),
            BarrioEntity("11001-040", "El Ensueño / Perdomo", "Ciudad Bolívar", latitudCentroide = 4.5823, longitudCentroide = -74.1589),
            BarrioEntity("11001-041", "Arborizadora Alta", "Ciudad Bolívar", latitudCentroide = 4.5589, longitudCentroide = -74.1523),

            // BARRIOS UNIDOS (Lat: ~4.66 - 4.68, Lon: ~-74.07)
            BarrioEntity("11001-042", "7 de Agosto", "Barrios Unidos", latitudCentroide = 4.6612, longitudCentroide = -74.0712),
            BarrioEntity("11001-043", "12 de Octubre", "Barrios Unidos", latitudCentroide = 4.6712, longitudCentroide = -74.0789),
            BarrioEntity("11001-044", "La Castellana", "Barrios Unidos", latitudCentroide = 4.6823, longitudCentroide = -74.0612),

            // ANTONIO NARIÑO & PUENTE ARANDA & LOS MÁRTIRES
            BarrioEntity("11001-045", "Restrepo", "Antonio Nariño", latitudCentroide = 4.5889, longitudCentroide = -74.1012),
            BarrioEntity("11001-046", "Puente Aranda Centro", "Puente Aranda", latitudCentroide = 4.6123, longitudCentroide = -74.1112),
            BarrioEntity("11001-047", "Muzú / San Gabriel", "Puente Aranda", latitudCentroide = 4.5989, longitudCentroide = -74.1212),
            BarrioEntity("11001-048", "Santa Isabel", "Los Mártires", latitudCentroide = 4.6012, longitudCentroide = -74.0912),

            // SAN CRISTÓBAL & RAFAEL URIBE URIBE & USME
            BarrioEntity("11001-049", "20 de Julio", "San Cristóbal", latitudCentroide = 4.5756, longitudCentroide = -74.0889),
            BarrioEntity("11001-050", "Quiroga / Olaya", "Rafael Uribe Uribe", latitudCentroide = 4.5789, longitudCentroide = -74.1089),
            BarrioEntity("11001-051", "Usme Yomasa", "Usme", latitudCentroide = 4.5212, longitudCentroide = -74.1189),

            // CHOCÓ
            BarrioEntity("CHO-SJP-001", "Centro Cabecera", "Centro", departamento = "Chocó", latitudCentroide = 4.9750, longitudCentroide = -76.2300),
            BarrioEntity("CHO-SJP-002", "La Italia", "Corregimiento", departamento = "Chocó", latitudCentroide = 4.9610, longitudCentroide = -76.2550),
            BarrioEntity("CHO-QUI-001", "Centro Sector Catedral", "Comuna 1", departamento = "Chocó", latitudCentroide = 5.6945, longitudCentroide = -76.6612),
            BarrioEntity("CHO-QUI-003", "Huapango", "Comuna 2", departamento = "Chocó", latitudCentroide = 5.7021, longitudCentroide = -76.6500),
            BarrioEntity("CHO-QUI-006", "El Jazmín", "Comuna 3", departamento = "Chocó", latitudCentroide = 5.6822, longitudCentroide = -76.6428),
            BarrioEntity("CHO-QUI-007", "La Paz", "Comuna 4", departamento = "Chocó", latitudCentroide = 5.6976, longitudCentroide = -76.6587),

            // VALLE DEL CAUCA
            BarrioEntity("VAL-CAL-001", "San Antonio", "Comuna 3", departamento = "Valle del Cauca", latitudCentroide = 3.4475, longitudCentroide = -76.5410),
            BarrioEntity("VAL-CAL-002", "Siloé", "Comuna 20", departamento = "Valle del Cauca", latitudCentroide = 3.4241, longitudCentroide = -76.5540),
            BarrioEntity("VAL-CAL-003", "Terrón Colorado", "Comuna 1", departamento = "Valle del Cauca", latitudCentroide = 3.4567, longitudCentroide = -76.5555),
            BarrioEntity("VAL-CAL-004", "Distrito de Aguablanca", "Comuna 13", departamento = "Valle del Cauca", latitudCentroide = 3.4200, longitudCentroide = -76.4950),
            BarrioEntity("VAL-BUE-001", "Isla Cascajal", "Comuna 1", departamento = "Valle del Cauca", latitudCentroide = 3.8820, longitudCentroide = -77.0270),
            BarrioEntity("VAL-CAR-001", "Centro Cartago", "Comuna 1", departamento = "Valle del Cauca", latitudCentroide = 4.7485, longitudCentroide = -75.9120),
            BarrioEntity("VAL-TUL-001", "Centro Tuluá", "Comuna Centro", departamento = "Valle del Cauca", latitudCentroide = 4.0840, longitudCentroide = -76.2000),

            // RISARALDA
            BarrioEntity("RIS-PER-001", "San Fernando", "Comuna Cuba", departamento = "Risaralda", latitudCentroide = 4.7950, longitudCentroide = -75.7280),
            BarrioEntity("RIS-PER-002", "Centro Pereira", "Comuna Centro", departamento = "Risaralda", latitudCentroide = 4.8130, longitudCentroide = -75.6960),
            BarrioEntity("RIS-PER-003", "Villa Santana", "Comuna Villa Santana", departamento = "Risaralda", latitudCentroide = 4.8080, longitudCentroide = -75.6650),
            BarrioEntity("RIS-DOS-001", "Campestre", "Comuna 2", departamento = "Risaralda", latitudCentroide = 4.8350, longitudCentroide = -75.6720),

            // CALDAS
            BarrioEntity("CAL-MAN-001", "Centro Histórico", "Comuna Cumanday", departamento = "Caldas", latitudCentroide = 5.0675, longitudCentroide = -75.5175),
            BarrioEntity("CAL-MAN-002", "Chipre", "Comuna Atardeceres", departamento = "Caldas", latitudCentroide = 5.0680, longitudCentroide = -75.5310),
            BarrioEntity("CAL-MAN-004", "Bosques del Norte", "Ciudadela del Norte", departamento = "Caldas", latitudCentroide = 5.0800, longitudCentroide = -75.4950),
            BarrioEntity("CAL-VIL-001", "Centro Villamaría", "Zona Centro", departamento = "Caldas", latitudCentroide = 5.0450, longitudCentroide = -75.5150),

            // QUINDÍO
            BarrioEntity("QUI-ARM-001", "Centro Armenia", "Comuna 1", departamento = "Quindío", latitudCentroide = 4.5335, longitudCentroide = -75.6740),
            BarrioEntity("QUI-ARM-002", "Fundadores", "Comuna 7", departamento = "Quindío", latitudCentroide = 4.5420, longitudCentroide = -75.6650),
            BarrioEntity("QUI-CAL-001", "Centro Calarcá", "Comuna Centro", departamento = "Quindío", latitudCentroide = 4.5260, longitudCentroide = -75.6450),

            // CAUCA
            BarrioEntity("CAU-POP-001", "Centro Histórico Popayán", "Comuna 3", departamento = "Cauca", latitudCentroide = 2.4415, longitudCentroide = -76.6060),
            BarrioEntity("CAU-POP-002", "La Esmeralda", "Comuna 8", departamento = "Cauca", latitudCentroide = 2.4550, longitudCentroide = -76.5950),

            // SANTANDER
            BarrioEntity("SAN-BUC-001", "Cabecera del Llano", "Comuna 12", departamento = "Santander", latitudCentroide = 7.1150, longitudCentroide = -73.1100),
            BarrioEntity("SAN-BUC-002", "Centro Bucaramanga", "Comuna 15", departamento = "Santander", latitudCentroide = 7.1200, longitudCentroide = -73.1250),
            BarrioEntity("SAN-LOS-001", "Mesa de los Santos", "La Mesa", departamento = "Santander", latitudCentroide = 6.7720, longitudCentroide = -73.1080)
        )
    }
}
