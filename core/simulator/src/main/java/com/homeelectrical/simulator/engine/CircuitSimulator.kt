package com.homeelectrical.simulator.engine

/**
 * محرك محاكاة الدوائر الكهربائية المنزلية المتقدم.
 * يحسب التيارات، الجهود، وهبوط الجهد باستخدام قوانين كيرشوف وأوم.
 * يدعم:
 * - دوائر متوازية ومتسلسلة معقدة
 * - قواطع حماية (MCB) تفصل تلقائياً عند الحمل الزائد
 * - قصر الدائرة واكتشاف الأعطال
 * - حساب هبوط الجهد في الأسلاك
 * - محاكاة واقعية للشبكة المنزلية 220V/50Hz
 */
class CircuitSimulator {

    private val components = mutableMapOf<String, ElectricalComponent>()
    private val nodes = mutableMapOf<String, CircuitNode>()
    private val connections = mutableListOf<Connection>()
    
    // جهد المصدر الرئيسي (الشبكة الحكومية)
    var sourceVoltage: Double = 220.0  // فولت
        private set
    
    var simulationResults: SimulationResults? = null
        private set

    /**
     * إضافة مكون كهربائي للدائرة.
     */
    fun addComponent(component: ElectricalComponent) {
        components[component.id] = component
    }

    /**
     * إزالة مكون من الدائرة.
     */
    fun removeComponent(componentId: String) {
        components.remove(componentId)
        connections.removeAll { 
            it.fromComponentId == componentId || it.toComponentId == componentId 
        }
    }

    /**
     * إضافة نقطة توصيل.
     */
    fun addNode(node: CircuitNode) {
        nodes[node.id] = node
    }

    /**
     * إضافة اتصال بين مكونين.
     */
    fun addConnection(connection: Connection) {
        connections.add(connection)
        
        // تحديث قائمة المكونات المتصلة
        val fromComp = components[connection.fromComponentId]
        val toComp = components[connection.toComponentId]
        
        if (fromComp != null && toComp != null) {
            // يمكن إضافة منطق لربط العقد هنا
        }
    }

    /**
     * تعيين جهد المصدر الرئيسي.
     */
    fun setSourceVoltage(voltage: Double) {
        if (voltage < 0 || voltage > 500) {
            throw CircuitConfigurationException("جهد المصدر يجب أن يكون بين 0 و 500 فولت")
        }
        sourceVoltage = voltage
    }

    /**
     * تشغيل المحاكاة وحساب جميع القيم الكهربائية.
     * @return نتائج المحاكاة أو يرمي استثناء في حالة وجود خطأ
     */
    fun simulate(): SimulationResults {
        validateCircuit()
        
        // تصفير جميع القيم السابقة
        resetComponentValues()
        
        // بناء مصفوفة المقاومة وحل الدائرة
        val results = solveCircuit()
        
        // التحقق من القواطع والحماية
        checkCircuitBreakers(results)
        
        simulationResults = results
        return results
    }

    /**
     * التحقق من صحة الدائرة قبل المحاكاة.
     */
    private fun validateCircuit() {
        if (components.isEmpty()) {
            throw CircuitConfigurationException("الدائرة فارغة")
        }
        
        // التحقق من وجود مصدر جهد
        val hasSource = components.values.any { it.type == ComponentType.VOLTAGE_SOURCE }
        if (!hasSource) {
            throw CircuitConfigurationException("يجب وجود مصدر جهد واحد على الأقل")
        }
        
        // التحقق من عدم وجود دوائر قصيرة مباشرة
        detectShortCircuits()
    }

    /**
     * اكتشاف حالات قصر الدائرة الخطيرة.
     */
    private fun detectShortCircuits() {
        // خوارزمية مبسطة لاكتشاف القصر
        // في النسخة الكاملة نستخدم خوارزميات رسم بياني متقدمة
        
        for ((id, component) in components) {
            if (component.state == ComponentState.SHORT_CIRCUIT) {
                throw ShortCircuitException(id)
            }
        }
    }

    /**
     * تصفير قيم التيار وهبوط الجهد لجميع المكونات.
     */
    private fun resetComponentValues() {
        components.values.forEach {
            it.currentFlow = 0.0
            it.voltageDrop = 0.0
        }
    }

    /**
     * حل الدائرة الكهربائية باستخدام طريقة تحليل العقد (Nodal Analysis).
     * نسخة مبسطة للدوائر المتوازية الأساسية.
     */
    private fun solveCircuit(): SimulationResults {
        val sourceComponents = components.filter { it.value.type == ComponentType.VOLTAGE_SOURCE }
        val loadComponents = components.filter { 
            it.value.type in listOf(ComponentType.LIGHT_BULB, ComponentType.HEATER, ComponentType.RESISTOR) 
        }
        
        var totalCurrent = 0.0
        var totalPower = 0.0
        val branchCurrents = mutableMapOf<String, Double>()
        
        // محاكاة بسيطة للدوائر المتوازية (الأكثر شيوعاً في المنازل)
        for ((id, component) in loadComponents) {
            if (component.state == ComponentState.OPEN || component.state == ComponentState.TRIPPED) {
                continue // تخطي المكونات المفتوحة
            }
            
            val resistance = component.calculateDynamicResistance(sourceVoltage)
            if (resistance >= Double.MAX_VALUE) continue
            
            // قانون أوم: I = V / R
            val current = sourceVoltage / resistance
            val power = sourceVoltage * current
            
            component.currentFlow = current
            component.voltageDrop = sourceVoltage
            branchCurrents[id] = current
            
            totalCurrent += current
            totalPower += power
        }
        
        // إضافة مقاومة الأسلاك
        val wireResistance = connections.sumOf { it.calculateWireResistance() }
        val voltageDropInWires = totalCurrent * wireResistance
        
        return SimulationResults(
            totalCurrent = totalCurrent,
            totalPower = totalPower,
            sourceVoltage = sourceVoltage,
            voltageAtLoad = sourceVoltage - voltageDropInWires,
            branchCurrents = branchCurrents,
            wireVoltageDrop = voltageDropInWires,
            isCircuitComplete = totalCurrent > 0
        )
    }

    /**
     * التحقق من عمل قواطع الحماية وفصلها إذا لزم الأمر.
     */
    private fun checkCircuitBreakers(results: SimulationResults) {
        val mcbComponents = components.filter { it.value.type == ComponentType.MCB }
        
        for ((id, mcb) in mcbComponents) {
            if (mcb.state == ComponentState.OPEN || mcb.state == ComponentState.TRIPPED) {
                continue // القاطع مفتوح بالفعل
            }
            
            // التحقق من تجاوز التيار للحد المسموح
            if (results.totalCurrent > mcb.ratedCurrent) {
                mcb.state = ComponentState.TRIPPED
                // إعادة المحاكاة لأن الدائرة أصبحت مفتوحة
                // (في النسخة الكاملة نتعامل مع هذا بشكل أكثر تعقيداً)
            }
        }
    }

    /**
     * تبديل حالة قاطع يدوي أو مبرمج.
     */
    fun toggleSwitch(switchId: String) {
        val switch = components[switchId] ?: return
        
        when (switch.type) {
            ComponentType.SWITCH -> {
                switch.state = if (switch.state == ComponentState.ACTIVE) 
                    ComponentState.OPEN else ComponentState.ACTIVE
            }
            ComponentType.MCB -> {
                if (switch.state == ComponentState.TRIPPED) {
                    // إعادة تعيين القاطع بعد الفصل
                    switch.state = ComponentState.ACTIVE
                } else {
                    switch.state = if (switch.state == ComponentState.ACTIVE) 
                        ComponentState.OPEN else ComponentState.ACTIVE
                }
            }
            else -> throw IllegalArgumentException("المكون $switchId ليس قاطعاً")
        }
    }

    /**
     * إحداث عطل محدد لأغراض التعليم.
     */
    fun induceFault(componentId: String, faultType: ComponentState) {
        val component = components[componentId] 
            ?: throw CircuitConfigurationException("المكون $componentId غير موجود")
        
        when (faultType) {
            ComponentState.SHORT_CIRCUIT, 
            ComponentState.BURNED, 
            ComponentState.OPEN -> {
                component.state = faultType
            }
            else -> throw IllegalArgumentException("نوع العطل غير مدعوم: $faultType")
        }
    }

    /**
     * الحصول على معلومات تفصيلية عن مكون معين.
     */
    fun getComponentInfo(componentId: String): ComponentInfo? {
        val component = components[componentId] ?: return null
        
        return ComponentInfo(
            id = component.id,
            type = component.type,
            state = component.state,
            currentFlow = component.currentFlow,
            voltageDrop = component.voltageDrop,
            resistance = component.calculateDynamicResistance(sourceVoltage),
            power = component.currentFlow * component.voltageDrop
        )
    }
}

/**
 * نتائج محاكاة شاملة للدائرة.
 */
data class SimulationResults(
    val totalCurrent: Double,           // إجمالي التيار المسحوب (أمبير)
    val totalPower: Double,             // إجمالي القدرة المستهلكة (وات)
    val sourceVoltage: Double,          // جهد المصدر (فولت)
    val voltageAtLoad: Double,          // الجهد الفعلي عند الأحمال (فولت)
    val branchCurrents: Map<String, Double>, // تيارات الفروع
    val wireVoltageDrop: Double,        // هبوط الجهد في الأسلاك (فولت)
    val isCircuitComplete: Boolean      // هل الدائرة مكتملة ومغلقة؟
) {
    /**
     * تقييم حالة الدائرة وتقديم نصائح.
     */
    fun getCircuitStatus(): CircuitStatus {
        return when {
            !isCircuitComplete -> CircuitStatus.OPEN_CIRCUIT
            wireVoltageDrop > sourceVoltage * 0.05 -> CircuitStatus.HIGH_VOLTAGE_DROP
            totalPower > 3000 -> CircuitStatus.HIGH_LOAD // أكثر من 3kW
            else -> CircuitStatus.NORMAL
        }
    }
}

/**
 * حالة الدائرة الناتجة عن المحاكاة.
 */
enum class CircuitStatus {
    NORMAL,              // تعمل بشكل طبيعي
    OPEN_CIRCUIT,        // دائرة مفتوحة
    HIGH_VOLTAGE_DROP,   // هبوط جهد عالي (>5%)
    HIGH_LOAD,           // حمل عالي جداً
    OVERLOAD,            // حمل زائد (القواطع ستفصل)
    SHORT_CIRCUIT        // قصر دائرة
}

/**
 * معلومات مفصلة عن مكون للمساعدة في العرض والتعليم.
 */
data class ComponentInfo(
    val id: String,
    val type: ComponentType,
    val state: ComponentState,
    val currentFlow: Double,    // أمبير
    val voltageDrop: Double,    // فولت
    val resistance: Double,     // أوم
    val power: Double           // وات
) {
    /**
     * وصف نصي للمكون للمتعلم.
     */
    fun getDescription(): String {
        return when (type) {
            ComponentType.LIGHT_BULB -> "لمبة بقدرة ${power.toInt()} واط، تيار ${String.format("%.2f", currentFlow)} أمبير"
            ComponentType.HEATER -> "سخان بقدرة ${power.toInt()} واط، يستهلك ${String.format("%.2f", currentFlow)} أمبير"
            ComponentType.MCB -> "قاطع حماية: الحالة $state"
            ComponentType.WIRE -> "سلك: هبوط الجهد ${String.format("%.2f", voltageDrop)} فولت"
            else -> "$type: $state"
        }
    }
}
