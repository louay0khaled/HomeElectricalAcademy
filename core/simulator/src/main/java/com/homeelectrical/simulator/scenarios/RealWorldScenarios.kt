package com.homeelectrical.simulator.scenarios

import com.homeelectrical.simulator.engine.*

/**
 * سيناريوهات محاكاة واقعية للكهرباء المنزلية.
 * كل سيناريو يمثل حالة عملية يواجهها الكهربائي في الواقع.
 */
object RealWorldScenarios {

    /**
     * السيناريو 1: دائرة إضاءة غرفة نوم بسيطة.
     * لمبة واحدة 60 واط، قاطع 10 أمبير، سلك 2 متر.
     */
    fun createBedroomLightingCircuit(): CircuitSimulator {
        val simulator = CircuitSimulator()
        
        // مصدر الجهد (الشبكة الحكومية)
        simulator.addComponent(
            ElectricalComponent("source_1", ComponentType.VOLTAGE_SOURCE)
        )
        
        // القاطع الرئيسي للغرفة (MCB 10A)
        simulator.addComponent(
            ElectricalComponent("mcb_1", ComponentType.MCB, ratedCurrent = 10.0)
        )
        
        // المفتاح اليدوي لللمبة
        simulator.addComponent(
            ElectricalComponent("switch_1", ComponentType.SWITCH, state = ComponentState.ACTIVE)
        )
        
        // اللمبة (60 واط)
        simulator.addComponent(
            ElectricalComponent("bulb_1", ComponentType.LIGHT_BULB, ratedPower = 60.0)
        )
        
        // أسلاك التوصيل (افتراضي 2 متر لكل وصلة)
        simulator.addConnection(Connection("source_1", "mcb_1", wireLength = 2.0))
        simulator.addConnection(Connection("mcb_1", "switch_1", wireLength = 3.0))
        simulator.addConnection(Connection("switch_1", "bulb_1", wireLength = 2.5))
        simulator.addConnection(Connection("bulb_1", "source_1", wireLength = 2.5)) // العودة
        
        return simulator
    }

    /**
     * السيناريو 2: دائرة مقبس كهربائي (بريزة) للأجهزة العامة.
     * مقبس واحد، قاطع 16 أمبير، يمكن توصيل أجهزة مختلفة.
     */
    fun createSocketCircuit(): CircuitSimulator {
        val simulator = CircuitSimulator()
        
        simulator.addComponent(
            ElectricalComponent("source_1", ComponentType.VOLTAGE_SOURCE)
        )
        
        // قاطع 16 أمبير للمقابس
        simulator.addComponent(
            ElectricalComponent("mcb_socket_1", ComponentType.MCB, ratedCurrent = 16.0)
        )
        
        // المقبس نفسه (نقطة توصيل)
        simulator.addComponent(
            ElectricalComponent("socket_1", ComponentType.SOCKET)
        )
        
        simulator.addConnection(Connection("source_1", "mcb_socket_1", wireLength = 5.0))
        simulator.addConnection(Connection("mcb_socket_1", "socket_1", wireLength = 4.0))
        simulator.addConnection(Connection("socket_1", "source_1", wireLength = 4.0))
        
        return simulator
    }

    /**
     * السيناريو 3: حمل زائد - تشغيل عدة أجهزة عالية الاستهلاك على مقبس واحد.
     * سخان 2000 واط + مكواة 1000 واط + مجفف شعر 1500 واط = 4500 واط
     * على قاطع 16 أمبير (الحد الأقصى ≈ 3500 واط) → سيفصل القاطع!
     */
    fun createOverloadScenario(): CircuitSimulator {
        val simulator = createSocketCircuit()
        
        // إضافة أحمال متعددة تمثل أجهزة موصلة بالمقبس
        simulator.addComponent(
            ElectricalComponent("heater_1", ComponentType.HEATER, ratedPower = 2000.0)
        )
        simulator.addComponent(
            ElectricalComponent("iron_1", ComponentType.HEATER, ratedPower = 1000.0)
        )
        simulator.addComponent(
            ElectricalComponent("dryer_1", ComponentType.HEATER, ratedPower = 1500.0)
        )
        
        // توصيل الأحمال بشكل متوازي (كما في الواقع)
        simulator.addConnection(Connection("socket_1", "heater_1", wireLength = 1.0))
        simulator.addConnection(Connection("socket_1", "iron_1", wireLength = 1.0))
        simulator.addConnection(Connection("socket_1", "dryer_1", wireLength = 1.0))
        
        // إكمال الدائرة للعودة
        simulator.addConnection(Connection("heater_1", "source_1", wireLength = 1.0))
        simulator.addConnection(Connection("iron_1", "source_1", wireLength = 1.0))
        simulator.addConnection(Connection("dryer_1", "source_1", wireLength = 1.0))
        
        return simulator
    }

    /**
     * السيناريو 4: قصر دائرة - تماس سلك الطور بالحياد.
     * يؤدي لتيار عالٍ جداً وفصل فوري للقاطع.
     */
    fun createShortCircuitScenario(): CircuitSimulator {
        val simulator = CircuitSimulator()
        
        simulator.addComponent(
            ElectricalComponent("source_1", ComponentType.VOLTAGE_SOURCE)
        )
        
        simulator.addComponent(
            ElectricalComponent("mcb_1", ComponentType.MCB, ratedCurrent = 16.0)
        )
        
        // نقطة القصر (تماس مباشر)
        simulator.addComponent(
            ElectricalComponent("short_point", ComponentType.WIRE, state = ComponentState.SHORT_CIRCUIT)
        )
        
        simulator.addConnection(Connection("source_1", "mcb_1", wireLength = 3.0))
        simulator.addConnection(Connection("mcb_1", "short_point", wireLength = 5.0))
        simulator.addConnection(Connection("short_point", "source_1", wireLength = 5.0))
        
        return simulator
    }

    /**
     * السيناريو 5: دائرة متكاملة لمنزل صغير.
     * تشمل: لوحة توزيع، دوائر إضاءة، مقابس، أجهزة كبيرة.
     */
    fun createSmallHouseCircuit(): CircuitSimulator {
        val simulator = CircuitSimulator()
        
        // المصدر الرئيسي (عمود الكهرباء الحكومي)
        simulator.addComponent(
            ElectricalComponent("main_source", ComponentType.VOLTAGE_SOURCE)
        )
        
        // العداد الرئيسي
        simulator.addComponent(
            ElectricalComponent("main_meter", ComponentType.RESISTOR, resistance = 0.01)
        )
        
        // القاطع الرئيسي للمنزل (32 أمبير)
        simulator.addComponent(
            ElectricalComponent("main_mcb", ComponentType.MCB, ratedCurrent = 32.0)
        )
        
        // دائرة الإضاءة (قاطع 10 أمبير)
        simulator.addComponent(
            ElectricalComponent("lighting_mcb", ComponentType.MCB, ratedCurrent = 10.0)
        )
        simulator.addComponent(
            ElectricalComponent("living_room_bulb", ComponentType.LIGHT_BULB, ratedPower = 60.0)
        )
        simulator.addComponent(
            ElectricalComponent("bedroom_bulb", ComponentType.LIGHT_BULB, ratedPower = 60.0)
        )
        simulator.addComponent(
            ElectricalComponent("kitchen_bulb", ComponentType.LIGHT_BULB, ratedPower = 40.0)
        )
        
        // دائرة المقابس (قاطع 16 أمبير)
        simulator.addComponent(
            ElectricalComponent("socket_mcb", ComponentType.MCB, ratedCurrent = 16.0)
        )
        simulator.addComponent(
            ElectricalComponent("living_socket", ComponentType.SOCKET)
        )
        simulator.addComponent(
            ElectricalComponent("tv_load", ComponentType.RESISTOR, ratedPower = 150.0)
        )
        
        // دائرة السخان (قاطع 20 أمبير)
        simulator.addComponent(
            ElectricalComponent("heater_mcb", ComponentType.MCB, ratedCurrent = 20.0)
        )
        simulator.addComponent(
            ElectricalComponent("water_heater", ComponentType.HEATER, ratedPower = 2500.0)
        )
        
        // توصيلات اللوحة الرئيسية
        simulator.addConnection(Connection("main_source", "main_meter", wireLength = 2.0))
        simulator.addConnection(Connection("main_meter", "main_mcb", wireLength = 1.0))
        
        // توصيل دوائر الفروع
        simulator.addConnection(Connection("main_mcb", "lighting_mcb", wireLength = 1.0))
        simulator.addConnection(Connection("main_mcb", "socket_mcb", wireLength = 1.0))
        simulator.addConnection(Connection("main_mcb", "heater_mcb", wireLength = 1.0))
        
        // توصيل الأحمال
        simulator.addConnection(Connection("lighting_mcb", "living_room_bulb", wireLength = 8.0))
        simulator.addConnection(Connection("lighting_mcb", "bedroom_bulb", wireLength = 10.0))
        simulator.addConnection(Connection("lighting_mcb", "kitchen_bulb", wireLength = 6.0))
        
        simulator.addConnection(Connection("socket_mcb", "living_socket", wireLength = 5.0))
        simulator.addConnection(Connection("living_socket", "tv_load", wireLength = 1.0))
        
        simulator.addConnection(Connection("heater_mcb", "water_heater", wireLength = 7.0))
        
        // وصلات العودة للمصدر (النيوترال)
        simulator.addConnection(Connection("living_room_bulb", "main_source", wireLength = 8.0))
        simulator.addConnection(Connection("bedroom_bulb", "main_source", wireLength = 10.0))
        simulator.addConnection(Connection("kitchen_bulb", "main_source", wireLength = 6.0))
        simulator.addConnection(Connection("tv_load", "main_source", wireLength = 6.0))
        simulator.addConnection(Connection("water_heater", "main_source", wireLength = 7.0))
        
        return simulator
    }

    /**
     * السيناريو 6: هبوط جهد بسبب سلك طويل جداً أو مقطع صغير.
     * سلك 50 متر بمقطع 1.5mm² يحمل تيار 10 أمبير.
     */
    fun createVoltageDropScenario(): CircuitSimulator {
        val simulator = CircuitSimulator()
        
        simulator.addComponent(
            ElectricalComponent("source_1", ComponentType.VOLTAGE_SOURCE)
        )
        
        simulator.addComponent(
            ElectricalComponent("mcb_1", ComponentType.MCB, ratedCurrent = 16.0)
        )
        
        // سلك طويل جداً (50 متر) بمقطع صغير
        simulator.addConnection(Connection("source_1", "mcb_1", wireLength = 50.0, wireGauge = 1.5))
        
        simulator.addComponent(
            ElectricalComponent("load_1", ComponentType.HEATER, ratedPower = 2000.0)
        )
        
        simulator.addConnection(Connection("mcb_1", "load_1", wireLength = 25.0, wireGauge = 1.5))
        simulator.addConnection(Connection("load_1", "source_1", wireLength = 25.0, wireGauge = 1.5))
        
        return simulator
    }

    /**
     * السيناريو 7: عطل لمبة محترقة.
     */
    fun createBurnedBulbScenario(): CircuitSimulator {
        val simulator = createBedroomLightingCircuit()
        
        // إحداث عطل بحرق اللمبة
        simulator.induceFault("bulb_1", ComponentState.BURNED)
        
        return simulator
    }

    /**
     * السيناريو 8: مقارنة استهلاك الأجهزة المختلفة.
     * أداة تعليمية لفهم العلاقة بين القدرة والتيار.
     */
    fun createApplianceComparison(): Map<String, ApplianceData> {
        return mapOf(
            "لمبة LED" to ApplianceData(10, 0.045),
            "لمبة متوهجة" to ApplianceData(60, 0.27),
            "تلفاز LCD" to ApplianceData(150, 0.68),
            "ثلاجة" to ApplianceData(200, 0.91),
            "غسالة ملابس" to ApplianceData(500, 2.27),
            "مكواة" to ApplianceData(1000, 4.55),
            "سخان ماء" to ApplianceData(2500, 11.36),
            "مكيف هواء" to ApplianceData(3500, 15.91),
            "فرن كهربائي" to ApplianceData(4000, 18.18)
        )
    }
}

/**
 * بيانات جهاز للمقارنة التعليمية.
 */
data class ApplianceData(
    val powerWatts: Int,      // القدرة بالوات
    val currentAmps: Double   // التيار بالأمبير (عند 220V)
) {
    companion object {
        private const val VOLTAGE = 220.0
    }
    
    /**
     * وصف تعليمي للجهاز.
     */
    fun getEducationalDescription(): String {
        return """
            |الجهاز: $powerWatts واط
            |التيار المسحوب: ${String.format("%.2f", currentAmps)} أمبير
            |التكلفة التقريبية للساعة: ${String.format("%.2f", powerWatts / 1000.0 * 0.1)} دولار
            |عدد الأجهزة المشابهة المسموح بها على قاطع 16A: ${(16.0 / currentAmps).toInt()}
        """.trimMargin()
    }
}
