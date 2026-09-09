package com.louaykhaled.homeelectricalacademy.core.simulator

import com.louaykhaled.homeelectricalacademy.core.model.CircuitState
import com.louaykhaled.homeelectricalacademy.core.model.Resistor
import com.louaykhaled.homeelectricalacademy.core.model.VoltageSource

class IdealCircuitSolver {
    fun solve(source: VoltageSource, load: Resistor, closedPath: Boolean): CircuitState {
        if (!source.enabled || !load.enabled || !closedPath || load.resistanceOhms <= 0.0) {
            return CircuitState(source.voltageVolts, load.resistanceOhms, 0.0, 0.0, false)
        }
        val current = source.voltageVolts / load.resistanceOhms
        return CircuitState(
            voltageVolts = source.voltageVolts,
            resistanceOhms = load.resistanceOhms,
            currentAmps = current,
            powerWatts = source.voltageVolts * current,
            energized = true
        )
    }
}
