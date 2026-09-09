package com.louaykhaled.homeelectricalacademy.core.standards

/** Educational profile. Values are illustrative until a jurisdiction is explicitly selected. */
data class StandardProfile(
    val id: String,
    val name: String,
    val nominalVoltageVolts: Double,
    val frequencyHz: Double,
    val supplyDescription: String,
    val notes: List<String> = emptyList()
)

object StandardProfiles {
    val training230V50Hz = StandardProfile(
        id = "training-230v-50hz",
        name = "تدريب عام • 230 V / 50 Hz",
        nominalVoltageVolts = 230.0,
        frequencyHz = 50.0,
        supplyDescription = "نموذج تعليمي عام للتيار المتردد السكني",
        notes = listOf(
            "لا يمثل هذا الملف كودًا قانونيًا لدولة بعينها.",
            "تُحمّل متطلبات التنفيذ والفحص من ملف الولاية/الدولة عند بناء الوحدات المتقدمة."
        )
    )
}
