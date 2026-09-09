package com.homeelectrical.simulator.engine

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import kotlin.test.assertNotNull
import kotlin.test.assertFailsWith

/**
 * اختبارات شاملة لمحرك محاكاة الدوائر الكهربائية.
 * تغطي السيناريوهات الواقعية للكهرباء المنزلية.
 */
class CircuitSimulatorTest {

    @Test
    fun testBasicLightingCircuit() {
        // اختبار دائرة إضاءة بسيطة
        val simulator = CircuitSimulator()
        
        simulator.addComponent(ElectricalComponent("source", ComponentType.VOLTAGE_SOURCE))
        simulator.addComponent(ElectricalComponent("mcb", ComponentType.MCB, ratedCurrent = 10.0))
        simulator.addComponent(ElectricalComponent("bulb", ComponentType.LIGHT_BULB, ratedPower = 60.0))
        
        simulator.addConnection(Connection("source", "mcb"))
        simulator.addConnection(Connection("mcb", "bulb"))
        simulator.addConnection(Connection("bulb", "source"))
        
        val results = simulator.simulate()
        
        // التحقق من أن الدائرة تعمل
        assertTrue(results.isCircuitComplete)
        assertTrue(results.totalCurrent > 0)
        
        // التيار المتوقع: I = P / V = 60 / 220 ≈ 0.27A
        assertEquals(0.27, results.totalCurrent, 0.05)
        
        // القدرة المتوقعة: 60 واط
        assertEquals(60.0, results.totalPower, 5.0)
    }

    @Test
    fun testMultipleLoadsParallel() {
        // اختبار أحمال متوازية متعددة
        val simulator = CircuitSimulator()
        
        simulator.addComponent(ElectricalComponent("source", ComponentType.VOLTAGE_SOURCE))
        simulator.addComponent(ElectricalComponent("mcb", ComponentType.MCB, ratedCurrent = 16.0))
        
        // ثلاث لمبات متوازية
        simulator.addComponent(ElectricalComponent("bulb1", ComponentType.LIGHT_BULB, ratedPower = 60.0))
        simulator.addComponent(ElectricalComponent("bulb2", ComponentType.LIGHT_BULB, ratedPower = 60.0))
        simulator.addComponent(ElectricalComponent("bulb3", ComponentType.LIGHT_BULB, ratedPower = 40.0))
        
        simulator.addConnection(Connection("source", "mcb"))
        simulator.addConnection(Connection("mcb", "bulb1"))
        simulator.addConnection(Connection("mcb", "bulb2"))
        simulator.addConnection(Connection("mcb", "bulb3"))
        simulator.addConnection(Connection("bulb1", "source"))
        simulator.addConnection(Connection("bulb2", "source"))
        simulator.addConnection(Connection("bulb3", "source"))
        
        val results = simulator.simulate()
        
        assertTrue(results.isCircuitComplete)
        
        // إجمالي القدرة: 60 + 60 + 40 = 160 واط
        assertEquals(160.0, results.totalPower, 10.0)
        
        // التيار الإجمالي: I = P / V = 160 / 220 ≈ 0.73A
        assertEquals(0.73, results.totalCurrent, 0.1)
    }

    @Test
    fun testOverloadTrip() {
        // اختبار فصل القاطع عند الحمل الزائد
        val simulator = CircuitSimulator()
        
        simulator.addComponent(ElectricalComponent("source", ComponentType.VOLTAGE_SOURCE))
        simulator.addComponent(ElectricalComponent("mcb", ComponentType.MCB, ratedCurrent = 10.0))
        
        // حمل عالي يتجاوز 10 أمبير (أكثر من 2200 واط)
        simulator.addComponent(ElectricalComponent("heater", ComponentType.HEATER, ratedPower = 3000.0))
        
        simulator.addConnection(Connection("source", "mcb"))
        simulator.addConnection(Connection("mcb", "heater"))
        simulator.addConnection(Connection("heater", "source"))
        
        val results = simulator.simulate()
        
        // التحقق من أن القاطع فصل
        val mcbInfo = simulator.getComponentInfo("mcb")
        assertNotNull(mcbInfo)
        assertEquals(ComponentState.TRIPPED, mcbInfo.state)
    }

    @Test
    fun testOpenSwitch() {
        // اختبار المفتاح المفتوح
        val simulator = CircuitSimulator()
        
        simulator.addComponent(ElectricalComponent("source", ComponentType.VOLTAGE_SOURCE))
        simulator.addComponent(ElectricalComponent("switch", ComponentType.SWITCH, state = ComponentState.OPEN))
        simulator.addComponent(ElectricalComponent("bulb", ComponentType.LIGHT_BULB, ratedPower = 60.0))
        
        simulator.addConnection(Connection("source", "switch"))
        simulator.addConnection(Connection("switch", "bulb"))
        simulator.addConnection(Connection("bulb", "source"))
        
        val results = simulator.simulate()
        
        // الدائرة مفتوحة، لا يجب أن يكون هناك تيار
        assertTrue(!results.isCircuitComplete || results.totalCurrent < 0.01)
    }

    @Test
    fun testToggleSwitch() {
        // اختبار تبديل حالة القاطع
        val simulator = CircuitSimulator()
        
        simulator.addComponent(ElectricalComponent("source", ComponentType.VOLTAGE_SOURCE))
        simulator.addComponent(ElectricalComponent("switch", ComponentType.SWITCH, state = ComponentState.ACTIVE))
        simulator.addComponent(ElectricalComponent("bulb", ComponentType.LIGHT_BULB, ratedPower = 60.0))
        
        simulator.addConnection(Connection("source", "switch"))
        simulator.addConnection(Connection("switch", "bulb"))
        simulator.addConnection(Connection("bulb", "source"))
        
        // المحاكاة الأولى - الدائرة مغلقة
        var results = simulator.simulate()
        assertTrue(results.isCircuitComplete)
        
        // إطفاء المفتاح
        simulator.toggleSwitch("switch")
        
        // المحاكاة الثانية - الدائرة مفتوحة
        results = simulator.simulate()
        assertTrue(!results.isCircuitComplete || results.totalCurrent < 0.01)
        
        // إعادة تشغيل المفتاح
        simulator.toggleSwitch("switch")
        results = simulator.simulate()
        assertTrue(results.isCircuitComplete)
    }

    @Test
    fun testBurnedBulb() {
        // اختبار لمبة محترقة
        val simulator = CircuitSimulator()
        
        simulator.addComponent(ElectricalComponent("source", ComponentType.VOLTAGE_SOURCE))
        simulator.addComponent(ElectricalComponent("bulb", ComponentType.LIGHT_BULB, ratedPower = 60.0))
        
        simulator.addConnection(Connection("source", "bulb"))
        simulator.addConnection(Connection("bulb", "source"))
        
        // المحاكاة الأولى - لمبة سليمة
        var results = simulator.simulate()
        assertTrue(results.isCircuitComplete)
        
        // حرق اللمبة
        simulator.induceFault("bulb", ComponentState.BURNED)
        
        // المحاكاة الثانية - دائرة مفتوحة
        results = simulator.simulate()
        assertTrue(!results.isCircuitComplete || results.totalCurrent < 0.01)
    }

    @Test
    fun testVoltageDropInWires() {
        // اختبار هبوط الجهد في الأسلاك الطويلة
        val simulator = CircuitSimulator()
        
        simulator.addComponent(ElectricalComponent("source", ComponentType.VOLTAGE_SOURCE))
        simulator.addComponent(ElectricalComponent("load", ComponentType.HEATER, ratedPower = 2000.0))
        
        // سلك طويل جداً (100 متر ذهاب وإياب)
        simulator.addConnection(Connection("source", "load", wireLength = 50.0, wireGauge = 1.5))
        simulator.addConnection(Connection("load", "source", wireLength = 50.0, wireGauge = 1.5))
        
        val results = simulator.simulate()
        
        // يجب أن يكون هناك هبوط جهد ملحوظ
        assertTrue(results.wireVoltageDrop > 0)
        assertTrue(results.voltageAtLoad < results.sourceVoltage)
    }

    @Test
    fun testComponentInfo() {
        // اختبار الحصول على معلومات المكون
        val simulator = CircuitSimulator()
        
        simulator.addComponent(ElectricalComponent("bulb", ComponentType.LIGHT_BULB, ratedPower = 100.0))
        simulator.addComponent(ElectricalComponent("source", ComponentType.VOLTAGE_SOURCE))
        
        simulator.addConnection(Connection("source", "bulb"))
        simulator.addConnection(Connection("bulb", "source"))
        
        simulator.simulate()
        
        val info = simulator.getComponentInfo("bulb")
        assertNotNull(info)
        assertEquals(ComponentType.LIGHT_BULB, info.type)
        assertTrue(info.power > 90 && info.power < 110) // حوالي 100 واط
    }

    @Test
    fun testCircuitStatus() {
        // اختبار حالات الدائرة المختلفة
        val simulator = CircuitSimulator()
        
        simulator.addComponent(ElectricalComponent("source", ComponentType.VOLTAGE_SOURCE))
        simulator.addComponent(ElectricalComponent("bulb", ComponentType.LIGHT_BULB, ratedPower = 60.0))
        
        simulator.addConnection(Connection("source", "bulb"))
        simulator.addConnection(Connection("bulb", "source"))
        
        val results = simulator.simulate()
        
        // الحالة يجب أن تكون طبيعية
        assertEquals(CircuitStatus.NORMAL, results.getCircuitStatus())
    }

    @Test
    fun testEmptyCircuit() {
        // اختبار دائرة فارغة
        val simulator = CircuitSimulator()
        
        assertFailsWith<CircuitConfigurationException> {
            simulator.simulate()
        }
    }

    @Test
    fun testNoSourceCircuit() {
        // اختبار دائرة بدون مصدر جهد
        val simulator = CircuitSimulator()
        simulator.addComponent(ElectricalComponent("bulb", ComponentType.LIGHT_BULB, ratedPower = 60.0))
        
        assertFailsWith<CircuitConfigurationException> {
            simulator.simulate()
        }
    }
}
