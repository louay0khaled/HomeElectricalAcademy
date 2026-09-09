package com.louaykhaled.homeelectricalacademy.core.simulator

import com.louaykhaled.homeelectricalacademy.core.model.CircuitState
import com.louaykhaled.homeelectricalacademy.core.model.Lamp
import com.louaykhaled.homeelectricalacademy.core.model.Resistor
import com.louaykhaled.homeelectricalacademy.core.model.SimulationResult
import com.louaykhaled.homeelectricalacademy.core.model.Switch
import com.louaykhaled.homeelectricalacademy.core.model.VoltageSource
import com.louaykhaled.homeelectricalacademy.core.model.Measurement

class IdealCircuitSolver {
    fun solve(source: VoltageSource, load: Resistor, closedPath: Boolean): CircuitState {
        val resistance = load.resistanceOhms
        if (!source.enabled || !load.enabled || !closedPath || resistance <= 0.0) {
            return CircuitState(source.voltageVolts, resistance, 0.0, 0.0, false, closedPath)
        }
        val current = source.voltageVolts / resistance
        return CircuitState(source.voltageVolts, resistance, current, source.voltageVolts * current, true, true)
    }

    fun solveSeries(source: VoltageSource, switch: Switch, lamp: Lamp): SimulationResult {
        val state = solve(source, Resistor(lamp.id, lamp.resistanceOhms, lamp.enabled), switch.closed && switch.enabled)
        return SimulationResult(
            state = state,
            measurements = listOf(
                Measurement("Source voltage", state.voltageVolts, "V"),
                Measurement("Circuit current", state.currentAmps, "A"),
                Measurement("Lamp power", state.powerWatts, "W")
            )
        )
    }
}
