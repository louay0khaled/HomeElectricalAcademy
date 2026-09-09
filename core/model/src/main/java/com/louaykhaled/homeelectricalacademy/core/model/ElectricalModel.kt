package com.louaykhaled.homeelectricalacademy.core.model

data class ElectricalPoint(val id: String)

data class Resistor(
    val id: String,
    val resistanceOhms: Double,
    val enabled: Boolean = true
)

data class VoltageSource(
    val id: String,
    val voltageVolts: Double,
    val enabled: Boolean = true
)

data class CircuitState(
    val voltageVolts: Double,
    val resistanceOhms: Double,
    val currentAmps: Double,
    val powerWatts: Double,
    val energized: Boolean
)
