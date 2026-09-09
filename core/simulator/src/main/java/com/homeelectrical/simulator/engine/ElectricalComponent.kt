package com.homeelectrical.simulator.engine

/**
 * يمثل المكونات الكهربائية الأساسية في الدائرة.
 * يدعم أنواعاً واقعية للكهرباء المنزلية.
 */
enum class ComponentType {
    VOLTAGE_SOURCE,      // مصدر الجهد (الشبكة الحكومية)
    RESISTOR,            // مقاومة (سلك أو حمل بسيط)
    LIGHT_BULB,          // لمبة (حمل مقاومي غير خطي مبسط)
    HEATER,              // سخان (حمل عالي الاستهلاك)
    SOCKET,              // مقبس (نقطة توصيل)
    SWITCH,              // قاطع يدوي (ON/OFF)
    MCB,                 // قاطع حماية تلقائي (Miniature Circuit Breaker)
    WIRE,                // سلك توصيل (له مقاومة صغيرة)
    GROUND               // أرضي
}

/**
 * حالة المكون الكهربائي.
 */
enum class ComponentState {
    ACTIVE,              // يعمل بشكل طبيعي
    OPEN,                // مفتوح (مثل قاطع مطفأ أو سلك مقطوع)
    TRIPPED,             // قاطع فصل بسبب حمل زائد
    BURNED,              // محترق (تلف بسبب تيار عالٍ جداً)
    SHORT_CIRCUIT        // قصر دائرة
}

/**
 * نموذج موحد للمكونات الكهربائية في المحاكاة.
 * يستخدم نمط التصميم State لتمثيل الحالة الفيزيائية.
 */
data class ElectricalComponent(
    val id: String,
    val type: ComponentType,
    var state: ComponentState = ComponentState.ACTIVE,
    var resistance: Double = 0.0,       // بالأوم (Ω)
    var ratedPower: Double = 0.0,       // بالوات (W) - للأحمال
    var ratedCurrent: Double = 0.0,     // بالأمبير (A) - للقواطع
    var voltageDrop: Double = 0.0,      // هبوط الجهد عبر المكون
    var currentFlow: Double = 0.0       // التيار المار عبر المكون
) {
    /**
     * حساب المقاومة الديناميكية بناءً على نوع المكون.
     * مثلاً: لمبة 100 واط على 220 فولت لها مقاومة محددة.
     */
    fun calculateDynamicResistance(sourceVoltage: Double): Double {
        if (state == ComponentState.OPEN || state == ComponentState.TRIPPED || state == ComponentState.BURNED) {
            return Double.MAX_VALUE // مقاومة لا نهائية (دائرة مفتوحة)
        }
        if (state == ComponentState.SHORT_CIRCUIT) {
            return 0.0 // مقاومة صفر (قصر دائرة)
        }

        return when (type) {
            ComponentType.WIRE -> 0.05 // مقاومة افتراضية للسلك لكل متر
            ComponentType.SWITCH -> if (state == ComponentState.ACTIVE) 0.0 else Double.MAX_VALUE
            ComponentType.MCB -> if (state == ComponentState.ACTIVE) 0.0 else Double.MAX_VALUE
            ComponentType.LIGHT_BULB, ComponentType.HEATER -> {
                if (ratedPower > 0) {
                    // R = V^2 / P
                    (sourceVoltage * sourceVoltage) / ratedPower
                } else resistance
            }
            else -> resistance
        }
    }
}
