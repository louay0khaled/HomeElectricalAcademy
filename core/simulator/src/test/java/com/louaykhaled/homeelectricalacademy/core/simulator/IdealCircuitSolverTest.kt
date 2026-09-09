package com.louaykhaled.homeelectricalacademy.core.simulator

import com.louaykhaled.homeelectricalacademy.core.model.Lamp
import com.louaykhaled.homeelectricalacademy.core.model.Resistor
import com.louaykhaled.homeelectricalacademy.core.model.Switch
import com.louaykhaled.homeelectricalacademy.core.model.VoltageSource
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class IdealCircuitSolverTest {
    private val solver = IdealCircuitSolver()

    @Test
    fun solves_closed_ideal_resistive_circuit() {
        val state = solver.solve(VoltageSource("source", 12.0), Resistor("load", 6.0), true)
        assertTrue(state.energized)
        assertEquals(2.0, state.currentAmps, 1e-9)
        assertEquals(24.0, state.powerWatts, 1e-9)
    }

    @Test
    fun open_path_has_no_current() {
        val state = solver.solve(VoltageSource("source", 12.0), Resistor("load", 6.0), false)
        assertFalse(state.energized)
        assertEquals(0.0, state.currentAmps, 1e-9)
        assertEquals(0.0, state.powerWatts, 1e-9)
    }

    @Test
    fun series_lamp_exposes_measurements() {
        val result = solver.solveSeries(
            VoltageSource("source", 12.0),
            Switch("sw", closed = true),
            Lamp("lamp", resistanceOhms = 6.0)
        )
        assertTrue(result.state.energized)
        assertEquals(2.0, result.state.currentAmps, 1e-9)
        assertEquals(3, result.measurements.size)
        assertEquals(2.0, result.measurements[1].value, 1e-9)
    }
}
