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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.louaykhaled.homeelectricalacademy.core.model.Resistor
import com.louaykhaled.homeelectricalacademy.core.model.VoltageSource
import com.louaykhaled.homeelectricalacademy.core.simulator.IdealCircuitSolver

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent { HomeElectricalAcademyApp() }
    }
}

@Composable
private fun HomeElectricalAcademyApp() {
    MaterialTheme { Surface(Modifier.fillMaxSize()) { CircuitLabScreen() } }
}

@Composable
private fun CircuitLabScreen() {
    var voltage by remember { mutableFloatStateOf(12f) }
    var resistance by remember { mutableFloatStateOf(6f) }
    var closed by remember { mutableStateOf(false) }
    val solver = remember { IdealCircuitSolver() }
    val state = solver.solve(
        VoltageSource("source", voltage.toDouble()),
        Resistor("load", resistance.toDouble()),
        closed
    )

    Column(
        modifier = Modifier.fillMaxSize().padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text("Home Electrical Academy", style = MaterialTheme.typography.headlineMedium)
        Text("المختبر العلمي • الدائرة الأولى", style = MaterialTheme.typography.titleMedium)
        Text("اشرح القانون، توقّع النتيجة، غيّر الحالة، ثم راقب ما يحسبه نموذج الدائرة.")

        Card(Modifier.fillMaxWidth()) {
            Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("مصدر الجهد: ${"%.1f".format(voltage)} V")
                Slider(voltage, { voltage = it }, 1f..24f)
                Text("المقاومة: ${"%.1f".format(resistance)} Ω")
                Slider(resistance, { resistance = it }, 1f..24f)
                Spacer(Modifier.height(4.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Button(onClick = { closed = !closed }) {
                        Text(if (closed) "فتح المفتاح" else "إغلاق المفتاح")
                    }
                    Button(onClick = { voltage = 12f; resistance = 6f; closed = false }) {
                        Text("إعادة ضبط")
                    }
                }
            }
        }

        Card(Modifier.fillMaxWidth()) {
            Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("القياسات", style = MaterialTheme.typography.titleLarge)
                Text("المسار: ${if (state.energized) "مغلق" else "مفتوح"}")
                Text("التيار: ${"%.2f".format(state.currentAmps)} A")
                Text("القدرة: ${"%.2f".format(state.powerWatts)} W")
                Text("النموذج: I = V / R ، P = V × I")
            }
        }
    }
}
