package com.louaykhaled.homeelectricalacademy

import org.junit.Assert.assertEquals
import org.junit.Test

class OhmsLawTest {
    @Test
    fun current_is_voltage_divided_by_resistance() {
        val voltage = 12.0
        val resistance = 6.0
        val current = voltage / resistance
        assertEquals(2.0, current, 1e-9)
    }

    @Test
    fun power_equals_voltage_times_current() {
        val voltage = 12.0
        val current = 2.0
        assertEquals(24.0, voltage * current, 1e-9)
    }
}
