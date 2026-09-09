package com.homeelectrical.simulator.domain.model

/**
 * نموذج يمثل مكونًا كهربائيًا في الدائرة المنزلية
 * يدعم أنواعًا متعددة: مصدر، حمل، قاطع، سلك، أرضي
 */
sealed class ElectricalComponent(
    val id: String,
    val name: String,
    val type: ComponentType,
    var isConnected: Boolean = true
) {
    abstract fun getResistance(): Double
    abstract fun getMaxCurrent(): Double
    abstract fun clone(): ElectricalComponent
    
    data class PowerSource(
        override val id: String,
        override val name: String = "مصدر الطاقة",
        val voltage: Double = 220.0,
        val frequency: Double = 50.0,
        val maxPower: Double = 5000.0,
        override val isConnected: Boolean = true
    ) : ElectricalComponent(id, name, ComponentType.SOURCE, isConnected) {
        override fun getResistance(): Double = 0.01 // مقاومة داخلية صغيرة
        override fun getMaxCurrent(): Double = maxPower / voltage
        override fun clone() = copy()
    }
    
    data class Load(
        override val id: String,
        override val name: String = "حمل",
        val loadType: LoadType = LoadType.LIGHT,
        val powerRating: Double = 60.0,
        val ratedVoltage: Double = 220.0,
        override val isConnected: Boolean = true
    ) : ElectricalComponent(id, name, ComponentType.LOAD, isConnected) {
        override fun getResistance(): Double {
            return if (powerRating > 0) (ratedVoltage * ratedVoltage) / powerRating else Double.MAX_VALUE
        }
        override fun getMaxCurrent(): Double = powerRating / ratedVoltage
        override fun clone() = copy()
    }
    
    data class CircuitBreaker(
        override val id: String,
        override val name: String = "قاطع دائرة",
        val ratedCurrent: Double = 16.0,
        val breakerType: BreakerType = BreakerType.MCB,
        var isTripped: Boolean = false,
        override val isConnected: Boolean = true
    ) : ElectricalComponent(id, name, ComponentType.BREAKER, isConnected) {
        override fun getResistance(): Double = 0.001 // مقاومة ضئيلة جدًا
        override fun getMaxCurrent(): Double = if (isTripped) 0.0 else ratedCurrent
        override fun clone() = copy()
        
        fun reset() {
            isTripped = false
        }
        
        fun trip() {
            isTripped = true
        }
    }
    
    data class Wire(
        override val id: String,
        override val name: String = "سلك",
        val wireType: WireType = WireType.COPPER_2_5,
        val length: Double = 1.0, // بالمتر
        override val isConnected: Boolean = true
    ) : ElectricalComponent(id, name, ComponentType.WIRE, isConnected) {
        override fun getResistance(): Double = wireType.resistancePerMeter * length
        override fun getMaxCurrent(): Double = wireType.maxCurrent
        override fun clone() = copy()
    }
    
    data class Ground(
        override val id: String,
        override val name: String = "أرضي",
        val groundType: GroundType = GroundType.ROD,
        override val isConnected: Boolean = true
    ) : ElectricalComponent(id, name, ComponentType.GROUND, isConnected) {
        override fun getResistance(): Double = groundType.resistance
        override fun getMaxCurrent(): Double = 100.0 // تيار تسرب مقبول
        override fun clone() = copy()
    }
    
    data class Socket(
        override val id: String,
        override val name: String = "مقبس",
        val socketType: SocketType = SocketType.SCHUKO,
        val hasGround: Boolean = true,
        override val isConnected: Boolean = true
    ) : ElectricalComponent(id, name, ComponentType.SOCKET, isConnected) {
        override fun getResistance(): Double = 0.0001 // مقاومة تماس ضئيلة
        override fun getMaxCurrent(): Double = socketType.maxCurrent
        override fun clone() = copy()
    }
    
    data class Switch(
        override val id: String,
        override val name: String = "مفتاح",
        val switchType: SwitchType = SwitchType.SINGLE_POLE,
        var isOn: Boolean = false,
        override val isConnected: Boolean = true
    ) : ElectricalComponent(id, name, ComponentType.SWITCH, isConnected) {
        override fun getResistance(): Double = if (isOn) 0.0001 else Double.MAX_VALUE
        override fun getMaxCurrent(): Double = 10.0
        override fun clone() = copy()
        
        fun toggle() {
            isOn = !isOn
        }
    }
}

/**
 * أنواع المكونات الكهربائية
 */
enum class ComponentType {
    SOURCE, LOAD, BREAKER, WIRE, GROUND, SOCKET, SWITCH
}

/**
 * أنواع الأحمال الكهربائية المنزلية
 */
enum class LoadType(val displayName: String, val typicalPower: Double) {
    LIGHT("إضاءة", 60.0),
    FAN("مروحة", 75.0),
    HEATER("سخان", 2000.0),
    AIR_CONDITIONER("مكيف هواء", 1500.0),
    REFRIGERATOR("ثلاجة", 150.0),
    WASHING_MACHINE("غسالة", 500.0),
    TV("تلفاز", 100.0),
    COMPUTER("حاسوب", 300.0),
    MICROWAVE("فرن ميكروويف", 1000.0),
    TOASTER("محمصة خبز", 800.0),
    KETTLE("غلاية", 1500.0),
    VACUUM("مكنسة كهربائية", 1200.0),
    HAIR_DRYER("مجفف شعر", 1000.0),
    IRON("مكواة", 1000.0),
    CUSTOM("مخصص", 0.0);
    
    companion object {
        fun fromDisplayName(name: String): LoadType {
            return values().find { it.displayName == name } ?: CUSTOM
        }
    }
}

/**
 * أنواع القواطع الكهربائية
 */
enum class BreakerType(val displayName: String, val responseTime: Double) {
    MCB("قاطع مصغر", 0.1),
    MCCB("قاطع مصبوب", 0.2),
    RCCB("قاطع تسرب أرضي", 0.03),
    RCBO("قاطع مركب", 0.03),
    MAIN("قاطع رئيسي", 0.5);
    
    companion object {
        fun fromDisplayName(name: String): BreakerType {
            return values().find { it.displayName == name } ?: MCB
        }
    }
}

/**
 * أنواع الأسلاك الكهربائية مع مواصفاتها
 */
enum class WireType(
    val displayName: String,
    val crossSection: Double, // mm²
    val resistancePerMeter: Double, // أوم/متر
    val maxCurrent: Double, // أمبير
    val insulation: String
) {
    COPPER_1_5("نحاس 1.5 ملم²", 1.5, 0.012, 16.0, "PVC"),
    COPPER_2_5("نحاس 2.5 ملم²", 2.5, 0.007, 25.0, "PVC"),
    COPPER_4("نحاس 4 ملم²", 4.0, 0.0045, 32.0, "PVC"),
    COPPER_6("نحاس 6 ملم²", 6.0, 0.003, 40.0, "PVC"),
    COPPER_10("نحاس 10 ملم²", 10.0, 0.0018, 55.0, "PVC"),
    ALUMINUM_16("ألومنيوم 16 ملم²", 16.0, 0.0018, 63.0, "XLPE"),
    ALUMINUM_25("ألومنيوم 25 ملم²", 25.0, 0.0011, 80.0, "XLPE");
    
    companion object {
        fun fromDisplayName(name: String): WireType {
            return values().find { it.displayName == name } ?: COPPER_2_5
        }
        
        fun recommendedForLoad(power: Double, voltage: Double = 220.0): WireType {
            val current = power / voltage
            return values().firstOrNull { it.maxCurrent >= current } ?: COPPER_10
        }
    }
}

/**
 * أنواع التأريض
 */
enum class GroundType(val displayName: String, val resistance: Double) {
    ROD("قضيب تأريض", 10.0),
    PLATE("صفيحة تأريض", 5.0),
    MAT("شبكة تأريض", 2.0),
    STRUCTURAL("تأريض هيكلي", 1.0);
    
    companion object {
        fun fromDisplayName(name: String): GroundType {
            return values().find { it.displayName == name } ?: ROD
        }
    }
}

/**
 * أنواع المقابس الكهربائية
 */
enum class SocketType(val displayName: String, val maxCurrent: Double, val pinCount: Int) {
    SCHUKO("شوكو الأوروبي", 16.0, 3),
    TYPE_C("النوع C", 2.5, 2),
    TYPE_F("النوع F", 16.0, 3),
    TYPE_G("النوع G البريطاني", 13.0, 3),
    TYPE_A("النوع A الأمريكي", 15.0, 2),
    TYPE_B("النوع B الأمريكي", 15.0, 3),
    INDUSTRIAL("صناعي", 32.0, 5);
    
    companion object {
        fun fromDisplayName(name: String): SocketType {
            return values().find { it.displayName == name } ?: SCHUKO
        }
    }
}

/**
 * أنواع المفاتيح الكهربائية
 */
enum class SwitchType(val displayName: String, val poleCount: Int) {
    SINGLE_POLE("أحادي القطب", 1),
    DOUBLE_POLE("ثنائي القطب", 2),
    THREE_WAY("ثلاثي المسار", 1),
    FOUR_WAY("رباعي المسار", 1),
    DIMMER("مخفت إضاءة", 1),
    SMART("ذكي", 1);
    
    companion object {
        fun fromDisplayName(name: String): SwitchType {
            return values().find { it.displayName == name } ?: SINGLE_POLE
        }
    }
}
