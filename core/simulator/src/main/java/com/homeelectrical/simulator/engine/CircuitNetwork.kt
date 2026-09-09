package com.homeelectrical.simulator.engine

/**
 * نقطة توصيل في الدائرة الكهربائية (Node).
 * تمثل نقاط الربط بين المكونات (مثل نقاط التوصيل في اللوحة أو المقابس).
 */
data class CircuitNode(
    val id: String,
    var voltage: Double = 0.0,  // الجهد عند هذه النقطة
    val connectedComponents: MutableList<String> = mutableListOf() // IDs of connected components
) {
    override fun toString(): String = "Node($id, V=$voltage)"
}

/**
 * تمثيل لاتصال بين مكونين في الدائرة.
 */
data class Connection(
    val fromComponentId: String,
    val toComponentId: String,
    val wireLength: Double = 1.0,  // بالمتر، لحساب مقاومة السلك
    val wireGauge: Double = 2.5    // مقطع السلك بالملم² (افتراضي 2.5mm² للأسلاك المنزلية)
) {
    /**
     * حساب مقاومة السلك بناءً على طوله ومقطعه.
     * R = ρ * L / A
     * حيث ρ (مقاومة النحاس) ≈ 0.0175 Ω·mm²/m
     */
    fun calculateWireResistance(): Double {
        val copperResistivity = 0.0175 // Ω·mm²/m
        return (copperResistivity * wireLength) / wireGauge
    }
}

/**
 * استثناءات خاصة بمحرك المحاكاة.
 */
sealed class SimulationException(message: String) : Exception(message)

class CircuitConfigurationException(message: String) : SimulationException(message)
class OverloadException(val current: Double, val limit: Double) : SimulationException(
    "تيار زائد: $current A يتجاوز الحد المسموح $limit A"
)
class ShortCircuitException(val location: String) : SimulationException(
    "قصر دائرة مكتشف عند: $location"
)
