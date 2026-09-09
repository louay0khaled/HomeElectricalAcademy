package com.louaykhaled.homeelectricalacademy.core.model

enum class ComponentKind { SOURCE, SWITCH, RESISTOR, LAMP, METER, PROTECTION }

data class ElectricalPoint(val id: String, val label: String = id)

data class Terminal(val id: String, val componentId: String)

data class Resistor(val id: String, val resistanceOhms: Double, val enabled: Boolean = true)

data class VoltageSource(val id: String, val voltageVolts: Double, val enabled: Boolean = true)

data class Switch(val id: String, val closed: Boolean = false, val enabled: Boolean = true)

data class Lamp(val id: String, val resistanceOhms: Double = 24.0, val enabled: Boolean = true)

data class CircuitState(
    val voltageVolts: Double,
    val resistanceOhms: Double,
    val currentAmps: Double,
    val powerWatts: Double,
    val energized: Boolean,
    val switchClosed: Boolean,
    val fault: String? = null
)

data class Measurement(
    val name: String,
    val value: Double,
    val unit: String,
    val valid: Boolean = true
)

data class SimulationResult(
    val state: CircuitState,
    val measurements: List<Measurement> = emptyList()
)
