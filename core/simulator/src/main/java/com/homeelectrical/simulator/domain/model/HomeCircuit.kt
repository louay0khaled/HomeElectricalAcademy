package com.homeelectrical.simulator.domain.model

/**
 * نموذج يمثل دائرة كهربائية منزلية كاملة
 * تحاكي المسار من عمود الكهرباء إلى اللوحة الرئيسية ثم إلى الدوائر الفرعية
 */
data class HomeCircuit(
    val id: String,
    val name: String = "دائرة منزلية",
    val circuitType: CircuitType = CircuitType.LIGHTING,
    val components: List<ElectricalComponent> = emptyList(),
    val connections: List<Connection> = emptyList()
) {
    /**
     * حساب المقاومة الكلية للدائرة
     */
    fun getTotalResistance(): Double {
        return components.filter { it.isConnected }.sumOf { it.getResistance() }
    }
    
    /**
     * حساب التيار الكلي باستخدام قانون أوم
     */
    fun getTotalCurrent(voltage: Double = 220.0): Double {
        val totalR = getTotalResistance()
        return if (totalR > 0 && totalR != Double.MAX_VALUE) voltage / totalR else 0.0
    }
    
    /**
     * حساب القدرة المستهلكة
     */
    fun getTotalPower(voltage: Double = 220.0): Double {
        val current = getTotalCurrent(voltage)
        return voltage * current
    }
    
    /**
     * التحقق من وجود قصر في الدائرة
     */
    fun hasShortCircuit(): Boolean {
        val totalR = getTotalResistance()
        return totalR < 0.1 && totalR > 0.0
    }
    
    /**
     * التحقق من الحمل الزائد
     */
    fun isOverloaded(): Boolean {
        val breakers = components.filterIsInstance<ElectricalComponent.CircuitBreaker>()
        if (breakers.isEmpty()) return false
        
        val current = getTotalCurrent()
        return breakers.any { breaker -> 
            current > breaker.getMaxCurrent() && !breaker.isTripped 
        }
    }
    
    /**
     * محاكاة حالة القواطع
     */
    fun simulateBreakers(): List<ElectricalComponent.CircuitBreaker> {
        val breakers = components.filterIsInstance<ElectricalComponent.CircuitBreaker>().toMutableList()
        val current = getTotalCurrent()
        
        breakers.forEach { breaker ->
            if (current > breaker.ratedCurrent && !breaker.isTripped) {
                breaker.trip()
            }
        }
        
        return breakers
    }
    
    /**
     * إعادة تعيين جميع القواطع
     */
    fun resetAllBreakers() {
        components.filterIsInstance<ElectricalComponent.CircuitBreaker>().forEach { it.reset() }
    }
    
    /**
     * الحصول على جميع الأحمال النشطة
     */
    fun getActiveLoads(): List<ElectricalComponent.Load> {
        return components.filterIsInstance<ElectricalComponent.Load>()
            .filter { it.isConnected }
    }
    
    /**
     * التحقق من سلامة التأريض
     */
    fun isGroundingSafe(): Boolean {
        val grounds = components.filterIsInstance<ElectricalComponent.Ground>()
        return grounds.isNotEmpty() && grounds.all { it.getResistance() < 10.0 }
    }
}

/**
 * أنواع الدوائر المنزلية
 */
enum class CircuitType(val displayName: String, val typicalBreaker: Double, val wireGauge: WireType) {
    LIGHTING("إضاءة", 10.0, WireType.COPPER_1_5),
    SOCKETS("مقابس عامة", 16.0, WireType.COPPER_2_5),
    AIR_CONDITIONER("مكيف هواء", 20.0, WireType.COPPER_4),
    KITCHEN("مطبخ", 20.0, WireType.COPPER_4),
    WATER_HEATER("سخان مياه", 25.0, WireType.COPPER_6),
    WASHING_MACHINE("غسالة", 16.0, WireType.COPPER_2_5),
    MAIN("رئيسية", 63.0, WireType.ALUMINUM_25);
    
    companion object {
        fun fromDisplayName(name: String): CircuitType {
            return values().find { it.displayName == name } ?: SOCKETS
        }
    }
}

/**
 * نموذج يمثل اتصالاً بين مكونين كهربائيين
 */
data class Connection(
    val id: String,
    val fromComponentId: String,
    val toComponentId: String,
    val connectionType: ConnectionType = ConnectionType.SERIES,
    val wireLength: Double = 0.5
)

/**
 * أنواع الاتصالات
 */
enum class ConnectionType {
    SERIES,      // توصيل على التوالي
    PARALLEL,    // توصيل على التوازي
    STAR,        // توصيل نجمة
    DELTA        // توصيل دلتا
}

/**
 * نموذج يمثل لوحة توزيع كهربائية منزلية
 */
data class DistributionBoard(
    val id: String,
    val name: String = "لوحة التوزيع",
    val mainBreaker: ElectricalComponent.CircuitBreaker? = null,
    val rccb: ElectricalComponent.CircuitBreaker? = null, // قاطع تسرب أرضي
    val circuits: List<HomeCircuit> = emptyList(),
    val groundSystem: ElectricalComponent.Ground? = null,
    val surgeProtector: Boolean = false
) {
    /**
     * حساب الحمل الكلي على اللوحة
     */
    fun getTotalLoad(): Double {
        return circuits.sumOf { it.getTotalPower() }
    }
    
    /**
     * التحقق من أن اللوحة ضمن السعة الآمنة
     */
    fun isWithinCapacity(): Boolean {
        val mainBreaker = mainBreaker ?: return true
        val totalCurrent = circuits.sumOf { it.getTotalCurrent() }
        return totalCurrent <= mainBreaker.ratedCurrent
    }
    
    /**
     * محاكاة أعطال اللوحة
     */
    fun simulateFaults(): FaultReport {
        val faults = mutableListOf<Fault>()
        
        // فحص الحمل الزائد
        if (!isWithinCapacity()) {
            faults.add(Fault(FaultType.OVERLOAD, "حمل زائد على اللوحة الرئيسية"))
        }
        
        // فحص القواطع المفصولة
        circuits.forEach { circuit ->
            circuit.components.filterIsInstance<ElectricalComponent.CircuitBreaker>()
                .filter { it.isTripped }
                .forEach { breaker ->
                    faults.add(Fault(FaultType.BREAKER_TRIPPED, "قاطع مفصول: ${breaker.name}"))
                }
        }
        
        // فحص التأريض
        if (groundSystem != null && groundSystem.getResistance() > 10.0) {
            faults.add(Fault(FaultType.POOR_GROUNDING, "تأريض غير كافٍ"))
        }
        
        // فحص قصر الدائرة
        circuits.forEach { circuit ->
            if (circuit.hasShortCircuit()) {
                faults.add(Fault(FaultType.SHORT_CIRCUIT, "قصر دائرة في: ${circuit.name}"))
            }
        }
        
        return FaultReport(faults)
    }
    
    /**
     * إضافة دائرة جديدة
     */
    fun addCircuit(circuit: HomeCircuit): DistributionBoard {
        return copy(circuits = circuits + circuit)
    }
    
    /**
     * إزالة دائرة
     */
    fun removeCircuit(circuitId: String): DistributionBoard {
        return copy(circuits = circuits.filter { it.id != circuitId })
    }
}

/**
 * تقرير الأعطال
 */
data class FaultReport(
    val faults: List<Fault> = emptyList(),
    val severity: FaultSeverity = calculateSeverity(faults)
) {
    companion object {
        fun calculateSeverity(faults: List<Fault>): FaultSeverity {
            if (faults.isEmpty()) return FaultSeverity.NONE
            
            return when {
                faults.any { it.type == FaultType.SHORT_CIRCUIT } -> FaultSeverity.CRITICAL
                faults.any { it.type == FaultType.OVERLOAD } -> FaultSeverity.HIGH
                faults.any { it.type == FaultType.POOR_GROUNDING } -> FaultSeverity.MEDIUM
                else -> FaultSeverity.LOW
            }
        }
    }
    
    val hasCriticalFaults: Boolean
        get() = severity == FaultSeverity.CRITICAL
    
    val faultCount: Int
        get() = faults.size
    
    fun getSummary(): String {
        return when (severity) {
            FaultSeverity.NONE -> "لا توجد أعطال"
            FaultSeverity.LOW -> "أعطال بسيطة: $faultCount"
            FaultSeverity.MEDIUM -> "أعطال متوسطة: $faultCount"
            FaultSeverity.HIGH -> "أعطال خطيرة: $faultCount"
            FaultSeverity.CRITICAL -> "أعطال حرجة! تدخل فوري مطلوب"
        }
    }
}

/**
 * نموذج العطل
 */
data class Fault(
    val type: FaultType,
    val description: String,
    val location: String = "",
    val timestamp: Long = System.currentTimeMillis()
)

/**
 * أنواع الأعطال
 */
enum class FaultType(val displayName: String) {
    SHORT_CIRCUIT("قصر دائرة"),
    OVERLOAD("حمل زائد"),
    BREAKER_TRIPPED("قاطع مفصول"),
    POOR_GROUNDING("تأريض ضعيف"),
    OPEN_CIRCUIT("دائرة مفتوحة"),
    VOLTAGE_DROP("هبوط جهد"),
    OVERVOLTAGE("جهد عالي"),
    LEAKAGE_CURRENT("تيار تسرب"),
    LOOSE_CONNECTION("اتصال رخو"),
    DAMAGED_INSULATION("عزل تالف")
}

/**
 * شدة العطل
 */
enum class FaultSeverity(val displayName: String, val colorCode: String) {
    NONE("لا يوجد", "#4CAF50"),
    LOW("منخفض", "#FFC107"),
    MEDIUM("متوسط", "#FF9800"),
    HIGH("عالي", "#F44336"),
    CRITICAL("حرج", "#B71C1C")
}
