package com.homeelectrical.simulator.domain.service

import com.homeelectrical.simulator.domain.model.*
import kotlin.math.sqrt

/**
 * محرك محاكاة الدوائر الكهربائية المنزلية المتقدم
 * يحاكي سلوك الكهرباء من عمود الحكومة إلى اللوحة الرئيسية ثم إلى جميع الدوائر الفرعية
 */
class CircuitSimulationEngine {
    
    private var distributionBoard: DistributionBoard? = null
    private val simulationHistory = mutableListOf<SimulationState>()
    
    /**
     * إعداد نظام كهربائي منزلي كامل يبدأ من عمود الكهرباء الحكومي
     */
    fun setupHomeElectricalSystem(
        mainVoltage: Double = 220.0,
        mainFrequency: Double = 50.0,
        mainPowerCapacity: Double = 11000.0 // 50 أمبير × 220 فولت
    ): ElectricalSystem {
        val mainBreaker = ElectricalComponent.CircuitBreaker(
            id = "main_breaker",
            name = "القاطع الرئيسي",
            ratedCurrent = 50.0,
            breakerType = BreakerType.MAIN
        )
        
        val rccb = ElectricalComponent.CircuitBreaker(
            id = "rccb_main",
            name = "قاطع التسرب الأرضي الرئيسي",
            ratedCurrent = 63.0,
            breakerType = BreakerType.RCCB
        )
        
        val groundSystem = ElectricalComponent.Ground(
            id = "ground_main",
            name = "نظام التأريض الرئيسي",
            groundType = GroundType.ROD
        )
        
        val powerSource = ElectricalComponent.PowerSource(
            id = "grid_source",
            name = "شبكة الكهرباء الحكومية",
            voltage = mainVoltage,
            frequency = mainFrequency,
            maxPower = mainPowerCapacity
        )
        
        distributionBoard = DistributionBoard(
            id = "main_board",
            name = "لوحة التوزيع الرئيسية",
            mainBreaker = mainBreaker,
            rccb = rccb,
            groundSystem = groundSystem,
            circuits = emptyList(),
            surgeProtector = true
        )
        
        return ElectricalSystem(
            powerSource = powerSource,
            distributionBoard = distributionBoard!!,
            voltage = mainVoltage,
            frequency = mainFrequency
        )
    }
    
    /**
     * إضافة دائرة جديدة للوحة التوزيع
     */
    fun addCircuitToBoard(
        circuitType: CircuitType,
        circuitName: String,
        components: List<ElectricalComponent> = emptyList()
    ): HomeCircuit {
        val board = distributionBoard ?: throw IllegalStateException("لم يتم إعداد لوحة التوزيع")
        
        val breaker = ElectricalComponent.CircuitBreaker(
            id = "breaker_${circuitType.name.lowercase()}",
            name = "قاطع ${circuitType.displayName}",
            ratedCurrent = circuitType.typicalBreaker,
            breakerType = when (circuitType) {
                CircuitType.WATER_HEATER, CircuitType.AIR_CONDITIONER -> BreakerType.MCB
                CircuitType.KITCHEN -> BreakerType.MCB
                else -> BreakerType.MCB
            }
        )
        
        val circuit = HomeCircuit(
            id = "circuit_${circuitType.name.lowercase()}_${System.currentTimeMillis()}",
            name = circuitName,
            circuitType = circuitType,
            components = listOf(breaker) + components
        )
        
        distributionBoard = board.addCircuit(circuit)
        return circuit
    }
    
    /**
     * حساب تحليل دقيق للدائرة باستخدام قوانين كيرشوف
     */
    fun analyzeCircuit(circuit: HomeCircuit): CircuitAnalysis {
        val totalResistance = circuit.getTotalResistance()
        val totalCurrent = circuit.getTotalCurrent()
        val totalPower = circuit.getTotalPower()
        
        // حساب هبوط الجهد في الأسلاك
        val wires = circuit.components.filterIsInstance<ElectricalComponent.Wire>()
        val voltageDrop = wires.sumOf { wire ->
            totalCurrent * wire.getResistance()
        }
        
        // حساب كفاءة الدائرة
        val efficiency = if (totalResistance > 0) {
            val loadResistance = circuit.components
                .filterIsInstance<ElectricalComponent.Load>()
                .sumOf { it.getResistance() }
            (loadResistance / totalResistance) * 100
        } else 0.0
        
        // تحديد حالة الدائرة
        val status = when {
            circuit.hasShortCircuit() -> CircuitStatus.SHORT_CIRCUIT
            circuit.isOverloaded() -> CircuitStatus.OVERLOADED
            circuit.components.filterIsInstance<ElectricalComponent.CircuitBreaker>()
                .any { it.isTripped } -> CircuitStatus.BREAKER_TRIPPED
            !circuit.isGroundingSafe() -> CircuitStatus.UNSAFE_GROUNDING
            totalCurrent > 0 -> CircuitStatus.ACTIVE
            else -> CircuitStatus.INACTIVE
        }
        
        return CircuitAnalysis(
            circuitId = circuit.id,
            circuitName = circuit.name,
            totalResistance = totalResistance,
            totalCurrent = totalCurrent,
            totalPower = totalPower,
            voltageDrop = voltageDrop,
            efficiency = efficiency,
            status = status,
            activeLoads = circuit.getActiveLoads().size,
            timestamp = System.currentTimeMillis()
        )
    }
    
    /**
     * محاكاة تشغيل الأحمال وتأثيرها على الدائرة
     */
    fun simulateLoadOperation(
        circuit: HomeCircuit,
        loadId: String,
        turnOn: Boolean
    ): SimulationResult {
        val updatedComponents = circuit.components.map { component ->
            if (component is ElectricalComponent.Load && component.id == loadId) {
                component.copy(isConnected = turnOn)
            } else {
                component
            }
        }
        
        val updatedCircuit = circuit.copy(components = updatedComponents)
        val analysis = analyzeCircuit(updatedCircuit)
        
        // التحقق من تأثير التشغيل على القواطع
        val trippedBreakers = updatedCircuit.simulateBreakers()
        
        val result = SimulationResult(
            success = if (turnOn) analysis.status != CircuitStatus.BREAKER_TRIPPED else true,
            message = when {
                trippedBreakers.isNotEmpty() -> "تم فصل القاطع بسبب الحمل الزائد"
                turnOn -> "تم تشغيل الحمل بنجاح"
                else -> "تم إيقاف الحمل"
            },
            circuitAnalysis = analysis,
            trippedBreakers = trippedBreakers.map { it.id }
        )
        
        simulationHistory.add(SimulationState(
            timestamp = System.currentTimeMillis(),
            action = if (turnOn) "LOAD_ON" else "LOAD_OFF",
            targetId = loadId,
            result = result
        ))
        
        return result
    }
    
    /**
     * محاكاة عطل في الدائرة
     */
    fun simulateFault(circuit: HomeCircuit, faultType: FaultType): SimulationResult {
        val faults = when (faultType) {
            FaultType.SHORT_CIRCUIT -> {
                // محاكاة قصر دائرة بإضافة مقاومة صغيرة جدًا
                listOf(Fault(faultType, "قصر دائرة محاكى", circuit.name))
            }
            FaultType.OVERLOAD -> {
                // محاكاة حمل زائد بإضافة أحمال إضافية
                listOf(Fault(faultType, "حمل زائد محاكى", circuit.name))
            }
            FaultType.POOR_GROUNDING -> {
                listOf(Fault(faultType, "تأريض ضعيف محاكى", circuit.name))
            }
            else -> {
                listOf(Fault(faultType, "عطل محاكى: ${faultType.displayName}", circuit.name))
            }
        }
        
        val faultReport = FaultReport(faults)
        
        val result = SimulationResult(
            success = false,
            message = "تم محاكاة العطل: ${faultType.displayName}",
            faultReport = faultReport,
            circuitAnalysis = analyzeCircuit(circuit)
        )
        
        simulationHistory.add(SimulationState(
            timestamp = System.currentTimeMillis(),
            action = "FAULT_SIMULATION",
            targetId = circuit.id,
            result = result
        ))
        
        return result
    }
    
    /**
     * تحليل شامل لمنظومة الكهرباء المنزلية
     */
    fun analyzeCompleteSystem(): SystemAnalysis {
        val board = distributionBoard ?: return SystemAnalysis(
            isConfigured = false,
            message = "لم يتم إعداد النظام"
        )
        
        val circuitAnalyses = board.circuits.map { analyzeCircuit(it) }
        val faultReport = board.simulateFaults()
        val totalLoad = board.getTotalLoad()
        val isWithinCapacity = board.isWithinCapacity()
        
        // حساب مؤشرات الأداء
        val efficiency = circuitAnalyses.averageOrNull { it.efficiency } ?: 0.0
        val safetyScore = calculateSafetyScore(board, faultReport)
        
        return SystemAnalysis(
            isConfigured = true,
            distributionBoard = board,
            circuitAnalyses = circuitAnalyses,
            totalLoad = totalLoad,
            isWithinCapacity = isWithinCapacity,
            faultReport = faultReport,
            efficiency = efficiency,
            safetyScore = safetyScore,
            timestamp = System.currentTimeMillis()
        )
    }
    
    /**
     * حساب درجة السلامة للنظام
     */
    private fun calculateSafetyScore(board: DistributionBoard, faultReport: FaultReport): Double {
        var score = 100.0
        
        // خصم نقاط للأعطال
        score -= faultReport.faults.size * 10.0
        
        // خصم إضافي للأعطال الحرجة
        if (faultReport.hasCriticalFaults) {
            score -= 30.0
        }
        
        // فحص التأريض
        if (board.groundSystem != null) {
            val groundResistance = board.groundSystem.getResistance()
            if (groundResistance > 10.0) score -= 20.0
            else if (groundResistance > 5.0) score -= 10.0
        } else {
            score -= 25.0 // عدم وجود تأريض
        }
        
        // فحص سعة اللوحة
        if (!board.isWithinCapacity()) {
            score -= 15.0
        }
        
        return score.coerceIn(0.0, 100.0)
    }
    
    /**
     * الحصول على سجل المحاكاة
     */
    fun getSimulationHistory(): List<SimulationState> {
        return simulationHistory.toList()
    }
    
    /**
     * مسح سجل المحاكاة
     */
    fun clearHistory() {
        simulationHistory.clear()
    }
}

/**
 * نموذج يمثل النظام الكهربائي الكامل
 */
data class ElectricalSystem(
    val powerSource: ElectricalComponent.PowerSource,
    val distributionBoard: DistributionBoard,
    val voltage: Double,
    val frequency: Double
)

/**
 * تحليل مفصل لدائرة كهربائية
 */
data class CircuitAnalysis(
    val circuitId: String,
    val circuitName: String,
    val totalResistance: Double,
    val totalCurrent: Double,
    val totalPower: Double,
    val voltageDrop: Double,
    val efficiency: Double,
    val status: CircuitStatus,
    val activeLoads: Int,
    val timestamp: Long
)

/**
 * حالة الدائرة الكهربائية
 */
enum class CircuitStatus(val displayName: String) {
    ACTIVE("نشطة"),
    INACTIVE("خاملة"),
    SHORT_CIRCUIT("قصر دائرة"),
    OVERLOADED("محملة زائداً"),
    BREAKER_TRIPPED("قاطع مفصول"),
    UNSAFE_GROUNDING("تأريض غير آمن"),
    OPEN_CIRCUIT("دائرة مفتوحة")
}

/**
 * نتيجة المحاكاة
 */
data class SimulationResult(
    val success: Boolean,
    val message: String,
    val circuitAnalysis: CircuitAnalysis? = null,
    val faultReport: FaultReport? = null,
    val trippedBreakers: List<String> = emptyList()
)

/**
 * حالة في سجل المحاكاة
 */
data class SimulationState(
    val timestamp: Long,
    val action: String,
    val targetId: String,
    val result: SimulationResult
)

/**
 * تحليل شامل للنظام الكهربائي
 */
data class SystemAnalysis(
    val isConfigured: Boolean,
    val distributionBoard: DistributionBoard? = null,
    val circuitAnalyses: List<CircuitAnalysis> = emptyList(),
    val totalLoad: Double = 0.0,
    val isWithinCapacity: Boolean = true,
    val faultReport: FaultReport? = null,
    val efficiency: Double = 0.0,
    val safetyScore: Double = 0.0,
    val timestamp: Long = System.currentTimeMillis(),
    val message: String = ""
) {
    fun getSafetyRating(): String {
        return when {
            safetyScore >= 90 -> "ممتاز"
            safetyScore >= 75 -> "جيد جداً"
            safetyScore >= 60 -> "جيد"
            safetyScore >= 40 -> "مقبول"
            else -> "غير آمن"
        }
    }
}
