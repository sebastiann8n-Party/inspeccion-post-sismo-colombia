package com.example.ui.map

import android.graphics.Color
import com.example.data.InspeccionConFotos
import com.example.data.InspeccionEntity
import com.google.common.truth.Truth.assertThat
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

/**
 * Pruebas unitarias para el calculador de pesos y colores del mapa de calor.
 * Usa Robolectric ya que Color.rgb pertenece al framework de Android.
 */
@RunWith(RobolectricTestRunner::class)
@Config(manifest=Config.NONE)
class HeatMapDataCalculatorTest {

    private lateinit var calculator: HeatMapDataCalculator

    @Before
    fun setUp() {
        calculator = HeatMapDataCalculator()
    }

    // --- Cálculo de pesos según habitabilidad ---

    @Test
    fun calculateWeightedPoints_rojoHab_returnsWeight1() {
        val inspeccion = createInspeccion(habitabilidad = "ROJO")
        val points = calculator.calculateWeightedPoints(listOf(inspeccion))
        assertThat(points).hasSize(1)
        assertThat(points[0].weight).isEqualTo(1.0)
    }

    @Test
    fun calculateWeightedPoints_naranjaHab_returnsWeight07() {
        val inspeccion = createInspeccion(habitabilidad = "NARANJA")
        val points = calculator.calculateWeightedPoints(listOf(inspeccion))
        assertThat(points[0].weight).isEqualTo(0.7)
    }

    @Test
    fun calculateWeightedPoints_amarilloHab_returnsWeight04() {
        val inspeccion = createInspeccion(habitabilidad = "AMARILLO")
        val points = calculator.calculateWeightedPoints(listOf(inspeccion))
        assertThat(points[0].weight).isEqualTo(0.4)
    }

    @Test
    fun calculateWeightedPoints_verdeHab_returnsWeight01() {
        val inspeccion = createInspeccion(habitabilidad = "VERDE")
        val points = calculator.calculateWeightedPoints(listOf(inspeccion))
        assertThat(points[0].weight).isEqualTo(0.1)
    }

    @Test
    fun calculateWeightedPoints_unknownHab_returnsWeight0() {
        val inspeccion = createInspeccion(habitabilidad = "DESCONOCIDO")
        val points = calculator.calculateWeightedPoints(listOf(inspeccion))
        assertThat(points[0].weight).isEqualTo(0.0)
    }

    // --- Interpolación de colores ---

    @Test
    fun interpolateColor_maxWeight_returnsRed() {
        val color = calculator.interpolateColor(1.0)
        assertThat(color).isEqualTo(Color.rgb(255, 0, 0))
    }

    @Test
    fun interpolateColor_minWeight_returnsGreen() {
        val color = calculator.interpolateColor(0.0)
        assertThat(color).isEqualTo(Color.rgb(0, 255, 0))
    }

    @Test
    fun interpolateColor_midWeight_returnsYellowish() {
        val color = calculator.interpolateColor(0.5)
        val red = Color.red(color)
        val green = Color.green(color)
        assertThat(red).isGreaterThan(100)
        assertThat(green).isGreaterThan(100)
    }

    // --- Conjunto vacío ---

    @Test
    fun calculateWeightedPoints_emptyList_returnsEmptyList() {
        val points = calculator.calculateWeightedPoints(emptyList())
        assertThat(points).isEmpty()
    }

    // --- Múltiples inspecciones ---

    @Test
    fun calculateWeightedPoints_multipleInspections_returnsCorrectCount() {
        val inspecciones = listOf(
            createInspeccion(id = "F-001", habitabilidad = "ROJO"),
            createInspeccion(id = "F-002", habitabilidad = "VERDE"),
            createInspeccion(id = "F-003", habitabilidad = "AMARILLO")
        )
        val points = calculator.calculateWeightedPoints(inspecciones)
        assertThat(points).hasSize(3)
    }

    // --- Helper ---

    private fun createInspeccion(
        id: String = "F-001",
        habitabilidad: String = "VERDE"
    ): InspeccionConFotos {
        return InspeccionConFotos(
            inspeccion = InspeccionEntity(
                id = id,
                latitud = 4.7110,
                longitud = -74.0721,
                habitabilidad = habitabilidad,
                direccion = "",
                localidad = "",
                barrio = "",
                fechaHora = "",
                inspectorLider = "",
                estado = ""
            ),
            fotos = emptyList()
        )
    }
}
