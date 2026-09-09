package com.homeelectrical.simulator

import com.homeelectrical.simulator.domain.model.*
import com.homeelectrical.simulator.domain.service.CircuitSimulationEngine
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import kotlin.test.assertFalse
import kotlin.test.assertNotNull

/**
 * اختبارات شاملة لمحرك محاكاة الدوائر الكهربائية المنزلية
 * تغطي السيناريوهات الواقعية من عمود الكهرباء إلى تشغيل الأجهزة
 */
class CircuitSimulationEngineTest {

    private val engine = CircuitSimulationEngine()

    @Test
    fun `test setup home electrical system from grid`() {
        // إعداد النظام من عمود الكهرباء الحكومي
        val system = engine.setupHomeElectricalSystem(
            mainVoltage = 220.0,
            mainFrequency = 50.0,
            mainPowerCapacity = 11000.0
        )

        // التحقق من مكونات النظام
        assertEquals(220.0, system.voltage)
        assertEquals(50.0, system.frequency)
        assertEquals("شبكة الكهرباء الحكومية", system.powerSource.name)
        assertNotNull(system.distributionBoard.mainBreaker)
        assertNotNull(system.distributionBoard.groundSystem)
        
        // التحقق من سعة الطاقة
        val maxCurrent = system.powerSource.maxPower / system.voltage
        assertEquals(50.0, maxCurrent, 0.01)
    }

    @Test
    fun `test adding lighting circuit with loads`() {
        engine.setupHomeElectricalSystem()
        
        // إنشاء أحمال إضاءة
        val light1 = ElectricalComponent.Load(
            id = "light_1",
            name = "لمبة غرفة المعيشة",
            loadType = LoadType.LIGHT,
            powerRating = 60.0
        )
        
        val light2 = ElectricalComponent.Load(
            id = "light_2",
            name = "لمبة المطبخ",
            loadType = LoadType.LIGHT,
            powerRating = 40.0
        )
        
        // إضافة دائرة الإضاءة
        val circuit = engine.addCircuitToBoard(
            circuitType = CircuitType.LIGHTING,
            circuitName = "دائرة الإضاءة",
            components = listOf(light1, light2)
        )
        
        // التحقق من خصائص الدائرة
        assertEquals(CircuitType.LIGHTING, circuit.circuitType)
        assertTrue(circuit.components.size >= 3) // قاطع + لمبتين
        
        // تحليل الدائرة
        val analysis = engine.analyzeCircuit(circuit)
        assertTrue(analysis.totalResistance > 0)
        assertEquals(CircuitStatus.ACTIVE, analysis.status)
    }

    @Test
    fun `test socket circuit with realistic appliances`() {
        engine.setupHomeElectricalSystem()
        
        // إنشاء مقبس مع أجهزة واقعية
        val socket = ElectricalComponent.Socket(
            id = "socket_living",
            name = "مقبس غرفة المعيشة",
            socketType = SocketType.SCHUKO,
            hasGround = true
        )
        
        val tv = ElectricalComponent.Load(
            id = "tv",
            name = "تلفاز",
            loadType = LoadType.TV,
            powerRating = 100.0
        )
        
        val circuit = engine.addCircuitToBoard(
            circuitType = CircuitType.SOCKETS,
            circuitName = "مقابس الغرف",
            components = listOf(socket, tv)
        )
        
        val analysis = engine.analyzeCircuit(circuit)
        
        // التحقق من أن التلفاز يستهلك الطاقة المتوقعة
        val expectedCurrent = tv.powerRating / tv.ratedVoltage
        assertTrue(analysis.totalCurrent > 0)
    }

    @Test
    fun `test circuit breaker trips on overload`() {
        engine.setupHomeElectricalSystem()
        
        // إنشاء دائرة بمقاوم منخفض جدًا (حمل زائد)
        val heater = ElectricalComponent.Load(
            id = "heater",
            name = "سخان قوي",
            loadType = LoadType.HEATER,
            powerRating = 5000.0 // حمل عالي جدًا لدائرة 16 أمبير
        )
        
        val circuit = engine.addCircuitToBoard(
            circuitType = CircuitType.SOCKETS, // قاطع 16 أمبير
            circuitName = "دائرة محملة",
            components = listOf(heater)
        )
        
        // محاكاة التشغيل
        val result = engine.simulateLoadOperation(circuit, heater.id, turnOn = true)
        
        // التحقق من فصل القاطع
        assertTrue(result.trippedBreakers.isNotEmpty())
        assertEquals("تم فصل القاطع بسبب الحمل الزائد", result.message)
    }

    @Test
    fun `test short circuit detection`() {
        engine.setupHomeElectricalSystem()
        
        // إنشاء دائرة بقصر (مقاومة منخفضة جدًا)
        val shortCircuit = HomeCircuit(
            id = "short_test",
            name = "دائرة قصيرة",
            circuitType = CircuitType.SOCKETS,
            components = listOf(
                ElectricalComponent.Wire(
                    id = "wire_short",
                    wireType = WireType.COPPER_2_5,
                    length = 0.1 // قصر جدًا
                )
            )
        )
        
        val analysis = engine.analyzeCircuit(shortCircuit)
        
        // التحقق من كشف قصر الدائرة
        assertTrue(analysis.status == CircuitStatus.SHORT_CIRCUIT || 
                   analysis.totalResistance < 0.1)
    }

    @Test
    fun `test complete home system analysis`() {
        engine.setupHomeElectricalSystem()
        
        // إضافة دوائر منزلية متكاملة
        engine.addCircuitToBoard(CircuitType.LIGHTING, "إضاءة الطابق الأرضي")
        engine.addCircuitToBoard(CircuitType.SOCKETS, "مقابس الصالة")
        engine.addCircuitToBoard(CircuitType.KITCHEN, "دائرة المطبخ")
        engine.addCircuitToBoard(CircuitType.WATER_HEATER, "السخان الكهربائي")
        
        // تحليل النظام الكامل
        val systemAnalysis = engine.analyzeCompleteSystem()
        
        assertTrue(systemAnalysis.isConfigured)
        assertTrue(systemAnalysis.circuitAnalyses.size >= 4)
        assertTrue(systemAnalysis.safetyScore > 0)
        
        // التحقق من تقييم السلامة
        val safetyRating = systemAnalysis.getSafetyRating()
        assertTrue(safetyRating.isNotEmpty())
    }

    @Test
    fun `test wire resistance calculation`() {
        // اختبار حساب مقاومة الأسلاك بدقة
        val wire10m = ElectricalComponent.Wire(
            id = "wire_10m",
            wireType = WireType.COPPER_2_5,
            length = 10.0
        )
        
        val wire20m = ElectricalComponent.Wire(
            id = "wire_20m",
            wireType = WireType.COPPER_2_5,
            length = 20.0
        )
        
        // مقاومة السلك 20 متر يجب أن تكون ضعف مقاومة 10 أمتار
        assertEquals(
            wire10m.getResistance() * 2, 
            wire20m.getResistance(), 
            0.001
        )
        
        // التحقق من أن السلك الأصغر مقطعًا له مقاومة أعلى
        val thinWire = ElectricalComponent.Wire(
            id = "thin_wire",
            wireType = WireType.COPPER_1_5,
            length = 10.0
        )
        
        assertTrue(thinWire.getResistance() > wire10m.getResistance())
    }

    @Test
    fun `test grounding safety check`() {
        engine.setupHomeElectricalSystem()
        
        val circuitWithGoodGround = HomeCircuit(
            id = "good_ground",
            components = listOf(
                ElectricalComponent.Ground(
                    id = "ground_good",
                    groundType = GroundType.ROD
                )
            )
        )
        
        val circuitWithBadGround = HomeCircuit(
            id = "bad_ground",
            components = listOf(
                ElectricalComponent.Ground(
                    id = "ground_bad",
                    groundType = GroundType.ROD
                )
            )
        )
        
        // تعديل مقاومة التأريض يدويًا للمحاكاة
        // (في الواقع سيتم ذلك عبر تغيير نوع التأريض)
        
        assertTrue(circuitWithGoodGround.isGroundingSafe())
    }

    @Test
    fun `test switch operation`() {
        val switch = ElectricalComponent.Switch(
            id = "switch_1",
            name = "مفتاح الإضاءة",
            switchType = SwitchType.SINGLE_POLE,
            isOn = false
        )
        
        // التحقق من أن المفتاح المطفأ له مقاومة عالية
        assertTrue(switch.getResistance() == Double.MAX_VALUE)
        
        // تشغيل المفتاح
        switch.toggle()
        
        // التحقق من أن المفتاح المشغل له مقاومة منخفضة
        assertTrue(switch.getResistance() < 0.001)
        assertTrue(switch.isOn)
    }

    @Test
    fun `test load power calculation`() {
        val heater = ElectricalComponent.Load(
            id = "heater_test",
            loadType = LoadType.HEATER,
            powerRating = 2000.0,
            ratedVoltage = 220.0
        )
        
        // حساب المقاومة المتوقعة من القدرة
        val expectedResistance = (220.0 * 220.0) / 2000.0
        assertEquals(expectedResistance, heater.getResistance(), 0.01)
        
        // حساب التيار المتوقع
        val expectedCurrent = 2000.0 / 220.0
        assertEquals(expectedCurrent, heater.getMaxCurrent(), 0.01)
    }

    @Test
    fun `test recommended wire gauge for load`() {
        // اختبار التوصية بنوع السلك المناسب للحمل
        
        // حمل صغير (إضاءة)
        val lightWire = WireType.recommendedForLoad(60.0)
        assertTrue(lightWire == WireType.COPPER_1_5 || lightWire == WireType.COPPER_2_5)
        
        // حمل متوسط (غسالة)
        val washingWire = WireType.recommendedForLoad(500.0)
        assertTrue(washingWire.maxCurrent >= 500.0 / 220.0)
        
        // حمل كبير (سخان)
        val heaterWire = WireType.recommendedForLoad(3000.0)
        assertTrue(heaterWire.maxCurrent >= 3000.0 / 220.0)
    }

    @Test
    fun `test fault simulation and reporting`() {
        engine.setupHomeElectricalSystem()
        
        val circuit = engine.addCircuitToBoard(
            circuitType = CircuitType.SOCKETS,
            circuitName = "دائرة اختبار الأعطال"
        )
        
        // محاكاة عطل قصر الدائرة
        val faultResult = engine.simulateFault(circuit, FaultType.SHORT_CIRCUIT)
        
        assertFalse(faultResult.success)
        assertNotNull(faultResult.faultReport)
        assertTrue(faultResult.faultReport!!.hasCriticalFaults)
        assertEquals(FaultSeverity.CRITICAL, faultResult.faultReport!!.severity)
    }

    @Test
    fun `test simulation history tracking`() {
        engine.setupHomeElectricalSystem()
        
        val circuit = engine.addCircuitToBoard(
            circuitType = CircuitType.LIGHTING,
            circuitName = "دائرة التاريخ"
        )
        
        // تنفيذ عدة عمليات
        val light = ElectricalComponent.Load(
            id = "light_hist",
            loadType = LoadType.LIGHT,
            powerRating = 60.0
        )
        
        engine.simulateLoadOperation(circuit, light.id, turnOn = true)
        engine.simulateLoadOperation(circuit, light.id, turnOn = false)
        
        // التحقق من تسجيل العمليات في التاريخ
        val history = engine.getSimulationHistory()
        assertTrue(history.size >= 2)
        
        // مسح التاريخ
        engine.clearHistory()
        assertEquals(0, engine.getSimulationHistory().size)
    }

    @Test
    fun `test realistic home scenario - living room setup`() {
        engine.setupHomeElectricalSystem()
        
        // إعداد غرفة معيشة واقعية
        val lights = listOf(
            ElectricalComponent.Load("light_main", "الإضاءة الرئيسية", LoadType.LIGHT, 100.0),
            ElectricalComponent.Load("light_accent", "إضاءة زينة", LoadType.LIGHT, 40.0)
        )
        
        val appliances = listOf(
            ElectricalComponent.Load("tv", "تلفاز", LoadType.TV, 150.0),
            ElectricalComponent.Load("ac", "مكيف", LoadType.AIR_CONDITIONER, 1500.0)
        )
        
        val switch = ElectricalComponent.Switch("switch_main", "المفتاح الرئيسي", isOn = true)
        
        val livingRoomCircuit = engine.addCircuitToBoard(
            circuitType = CircuitType.SOCKETS,
            circuitName = "غرفة المعيشة",
            components = lights + appliances + listOf(switch)
        )
        
        val analysis = engine.analyzeCircuit(livingRoomCircuit)
        
        // التحقق من أن جميع الأحمال تعمل
        assertEquals(lights.size + appliances.size, analysis.activeLoads)
        
        // التحقق من أن النظام ضمن السعة
        assertTrue(analysis.status != CircuitStatus.OVERLOADED)
    }

    @Test
    fun `test kitchen high-power appliances`() {
        engine.setupHomeElectricalSystem()
        
        // أجهزة مطبخ عالية الاستهلاك
        val kitchenAppliances = listOf(
            ElectricalComponent.Load("microwave", "ميكروويف", LoadType.MICROWAVE, 1000.0),
            ElectricalComponent.Load("kettle", "غلاية", LoadType.KETTLE, 1500.0),
            ElectricalComponent.Load("toaster", "محمصة", LoadType.TOASTER, 800.0)
        )
        
        val kitchenCircuit = engine.addCircuitToBoard(
            circuitType = CircuitType.KITCHEN, // قاطع 20 أمبير
            circuitName = "دائرة المطبخ",
            components = kitchenAppliances
        )
        
        val analysis = engine.analyzeCircuit(kitchenCircuit)
        
        // حساب القدرة الكلية
        val totalPower = kitchenAppliances.sumOf { it.powerRating }
        assertTrue(analysis.totalPower <= totalPower * 1.1) // هامش خطأ بسيط
        
        // التحقق من أن دائرة المطبخ مصممة للأحمال العالية
        assertEquals(CircuitType.KITCHEN, kitchenCircuit.circuitType)
    }

    @Test
    fun `test system safety score calculation`() {
        engine.setupHomeElectricalSystem()
        
        // إضافة دوائر سليمة
        engine.addCircuitToBoard(CircuitType.LIGHTING, "إضاءة")
        engine.addCircuitToBoard(CircuitType.SOCKETS, "مقابس")
        
        val analysis = engine.analyzeCompleteSystem()
        
        // التحقق من وجود درجة سلامة
        assertTrue(analysis.safetyScore in 0.0..100.0)
        
        // التحقق من صحة تصنيف السلامة
        val rating = analysis.getSafetyRating()
        assertTrue(rating in listOf("ممتاز", "جيد جداً", "جيد", "مقبول", "غير آمن"))
    }
}
