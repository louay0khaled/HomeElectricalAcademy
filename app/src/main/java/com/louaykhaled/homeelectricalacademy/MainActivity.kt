package com.louaykhaled.homeelectricalacademy

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent { HomeElectricalAcademyApp() }
    }
}

@androidx.compose.runtime.Composable
private fun HomeElectricalAcademyApp() {
    MaterialTheme {
        Surface(modifier = Modifier.fillMaxSize()) {
            LabHome()
        }
    }
}

@androidx.compose.runtime.Composable
private fun LabHome() {
    var voltage by remember { mutableFloatStateOf(12f) }
    var resistance by remember { mutableFloatStateOf(6f) }
    var simulated by remember { mutableStateOf(false) }

    val current = voltage / resistance
    val power = voltage * current

    Column(
        modifier = Modifier.fillMaxSize().padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text("Home Electrical Academy", style = MaterialTheme.typography.headlineMedium)
        Text("Milestone 1 • Scientific Circuit Lab", style = MaterialTheme.typography.titleMedium)
        Text("غيّر القيم، توقّع النتيجة، ثم شغّل المحاكاة. هذه أول نواة للمختبر التفاعلي.")

        Card(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("مصدر الجهد: ${"%.1f".format(voltage)} V")
                Slider(value = voltage, onValueChange = { voltage = it; simulated = false }, valueRange = 1f..24f)
                Text("المقاومة: ${"%.1f".format(resistance)} Ω")
                Slider(value = resistance, onValueChange = { resistance = it; simulated = false }, valueRange = 1f..24f)
                Spacer(Modifier.height(4.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Button(onClick = { simulated = true }) { Text("تشغيل المحاكاة") }
                    Button(onClick = { voltage = 12f; resistance = 6f; simulated = false }) { Text("إعادة ضبط") }
                }
            }
        }

        Card(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("النتيجة العلمية", style = MaterialTheme.typography.titleLarge)
                Text("قانون أوم: I = V / R")
                Text("التيار المتوقع: ${"%.2f".format(current)} A")
                Text("القدرة: ${"%.2f".format(power)} W")
                if (simulated) {
                    Text("المحاكاة: الدائرة المغلقة تعمل وفق النموذج المثالي.")
                    Text("الخطوة التالية: إضافة مصدر وحمل ومفتاح وقياس متعدد النقاط.")
                } else {
                    Text("الحالة: توقّع النتيجة أولًا، ثم شغّل المحاكاة.")
                }
            }
        }
    }
}
