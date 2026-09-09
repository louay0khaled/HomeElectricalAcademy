package com.louaykhaled.homeelectricalacademy

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.louaykhaled.homeelectricalacademy.core.model.Resistor
import com.louaykhaled.homeelectricalacademy.core.model.VoltageSource
import com.louaykhaled.homeelectricalacademy.core.simulator.IdealCircuitSolver

private val Ink = Color(0xFFEAF0F7)
private val Muted = Color(0xFF9DAABA)
private val SurfaceDark = Color(0xFF111823)
private val SurfaceSoft = Color(0xFF182230)
private val Accent = Color(0xFF35D5B4)
private val Accent2 = Color(0xFF6FA8FF)
private val Background = Color(0xFF0A1017)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent { HomeElectricalAcademyApp() }
    }
}

@Composable
private fun HomeElectricalAcademyApp() {
    var selected by remember { mutableIntStateOf(0) }
    MaterialTheme {
        Scaffold(
            containerColor = Background,
            bottomBar = {
                NavigationBar(containerColor = SurfaceDark) {
                    val labels = listOf("الرئيسية", "المختبر", "الأكاديمية", "المشاريع")
                    val icons = listOf("⌂", "⚡", "◆", "▦")
                    labels.forEachIndexed { index, label ->
                        NavigationBarItem(
                            selected = selected == index,
                            onClick = { selected = index },
                            icon = { Text(icons[index]) },
                            label = { Text(label) }
                        )
                    }
                }
            }
        ) { padding ->
            when (selected) {
                0 -> Dashboard(Modifier.padding(padding))
                1 -> ScientificLab(Modifier.padding(padding))
                2 -> LearningPath(Modifier.padding(padding))
                else -> ProjectHub(Modifier.padding(padding))
            }
        }
    }
}

@Composable
private fun Dashboard(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier.fillMaxSize().background(Brush.verticalGradient(listOf(Background, Color(0xFF101B27))))
            .verticalScroll(rememberScrollState()).padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text("HOME ELECTRICAL ACADEMY", color = Accent, fontWeight = FontWeight.Bold)
        Text("أكاديمية الكهرباء المنزلية", color = Ink, style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
        Text("تعلّم الكهرباء بالمحاكاة، لا بالمشاهدة.", color = Muted)

        Card(colors = CardDefaults.cardColors(containerColor = SurfaceSoft), shape = RoundedCornerShape(24.dp)) {
            Column(Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text("مسار الكهربائي السكني", color = Ink, fontWeight = FontWeight.Bold)
                Text("المستوى 01 • أساسيات الكهرباء", color = Muted)
                Text("أول محطة: الجهد والتيار والمقاومة", color = Ink)
                Button(onClick = {}, modifier = Modifier.fillMaxWidth()) { Text("متابعة التدريب") }
            }
        }

        Text("المختبرات الأساسية", color = Ink, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
        LabCard("01", "قانون أوم", "غيّر الجهد والمقاومة واختبر توقعك للتيار")
        LabCard("02", "الدائرة المفتوحة", "اكتشف لماذا ينعدم التيار عند فتح المسار")
        LabCard("03", "قدرة الحمل", "افهم العلاقة بين الجهد والتيار والقدرة")

        Text("المشروع النهائي", color = Ink, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
        Card(colors = CardDefaults.cardColors(containerColor = Color(0xFF132A2A)), shape = RoundedCornerShape(24.dp)) {
            Column(Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("🏠 منزل التدريب #001", color = Ink, fontWeight = FontWeight.Bold)
                Text("بناء منظومة منزلية كاملة داخل المحاكاة ثم فحصها وتشخيص أعطالها.", color = Muted)
                OutlinedButton(onClick = {}) { Text("استعراض المشروع") }
            }
        }
    }
}

@Composable
private fun LabCard(number: String, title: String, subtitle: String) {
    Card(colors = CardDefaults.cardColors(containerColor = SurfaceDark), shape = RoundedCornerShape(20.dp)) {
        Row(Modifier.fillMaxWidth().padding(18.dp), verticalAlignment = Alignment.CenterVertically) {
            Text(number, color = Accent, fontWeight = FontWeight.Bold)
            Spacer(Modifier.width(16.dp))
            Column(Modifier.weight(1f)) {
                Text(title, color = Ink, fontWeight = FontWeight.Bold)
                Text(subtitle, color = Muted)
            }
            Text("›", color = Accent, style = MaterialTheme.typography.headlineSmall)
        }
    }
}

@Composable
private fun ScientificLab(modifier: Modifier = Modifier) {
    var voltage by remember { mutableFloatStateOf(12f) }
    var resistance by remember { mutableFloatStateOf(6f) }
    var closed by remember { mutableStateOf(true) }
    val solver = remember { IdealCircuitSolver() }
    val state = remember(voltage, resistance, closed) {
        solver.solve(
            VoltageSource("source", voltage.toDouble()),
            Resistor("load", resistance.toDouble()),
            closed
        )
    }

    Column(
        modifier = modifier.fillMaxSize().background(Background).verticalScroll(rememberScrollState()).padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        Text("المختبر العلمي", color = Ink, style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
        Text("التجربة 01 • قانون أوم", color = Accent, fontWeight = FontWeight.Bold)
        Text("غيّر المتغيرات ثم راقب ما يحدث داخل نموذج كهربائي قابل للقياس.", color = Muted)

        Card(colors = CardDefaults.cardColors(containerColor = SurfaceDark), shape = RoundedCornerShape(24.dp)) {
            Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text("النموذج العلمي", color = Ink, fontWeight = FontWeight.Bold)
                Text("I = V / R", color = Accent2, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
                Text("عند ثبات المقاومة، رفع الجهد يرفع التيار. وعند ثبات الجهد، رفع المقاومة يخفض التيار.", color = Muted)
                CircuitDiagram(state.energized)
            }
        }

        Card(colors = CardDefaults.cardColors(containerColor = SurfaceDark), shape = RoundedCornerShape(20.dp)) {
            Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("الجهد", color = Ink, fontWeight = FontWeight.Bold)
                    Text("%.1f V".format(voltage), color = Accent)
                }
                androidx.compose.material3.Slider(
                    value = voltage,
                    onValueChange = { voltage = it },
                    valueRange = 1f..24f
                )
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("المقاومة", color = Ink, fontWeight = FontWeight.Bold)
                    Text("%.1f Ω".format(resistance), color = Accent)
                }
                androidx.compose.material3.Slider(
                    value = resistance,
                    onValueChange = { resistance = it },
                    valueRange = 1f..24f
                )
            }
        }

        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            FilterChip(selected = closed, onClick = { closed = true }, label = { Text("المفتاح مغلق") })
            FilterChip(selected = !closed, onClick = { closed = false }, label = { Text("المفتاح مفتوح") })
        }

        Card(colors = CardDefaults.cardColors(containerColor = SurfaceSoft), shape = RoundedCornerShape(24.dp)) {
            Column(Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("نتيجة المحاكاة", color = Ink, fontWeight = FontWeight.Bold)
                Metric("الجهد", "%.2f V".format(state.voltageVolts))
                Metric("التيار", "%.2f A".format(state.currentAmps))
                Metric("القدرة", "%.2f W".format(state.powerWatts))
                Text(
                    if (state.energized) "✓ المسار مغلق والحمل مُغذّى" else "○ المسار مفتوح ولا يمر تيار",
                    color = if (state.energized) Accent else Muted
                )
            }
        }
    }
}

@Composable
private fun CircuitDiagram(energized: Boolean) {
    Canvas(Modifier.fillMaxWidth().height(140.dp)) {
        val y = size.height / 2f
        val left = 50f
        val right = size.width - 50f
        val wire = if (energized) Accent else Color(0xFF657386)
        drawLine(wire, Offset(left, y), Offset(right - 105f, y), 7f, cap = StrokeCap.Round)
        drawLine(wire, Offset(right - 60f, y), Offset(right, y), 7f, cap = StrokeCap.Round)
        drawCircle(Accent2, 22f, Offset(left, y))
        drawLine(Color.White, Offset(right - 105f, y - 23f), Offset(right - 60f, y + 23f), 7f, cap = StrokeCap.Round)
        drawCircle(if (energized) Accent else Color(0xFF4A5666), 25f, Offset(right, y))
        drawCircle(Background, 15f, Offset(right, y))
    }
}

@Composable
private fun Metric(label: String, value: String) {
    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
        Text(label, color = Muted)
        Text(value, color = Ink, fontWeight = FontWeight.Bold)
    }
}

@Composable
private fun LearningPath(modifier: Modifier = Modifier) {
    val levels = listOf(
        "01" to "أساسيات الكهرباء",
        "02" to "القياس والأدوات",
        "03" to "التيار المتردد والمنزل",
        "04" to "لوحة التوزيع والحماية",
        "05" to "تصميم الدوائر المنزلية",
        "06" to "التنفيذ والمحاكاة",
        "07" to "الفحص والاختبارات",
        "08" to "كشف الأعطال والمشروع النهائي"
    )
    Column(
        modifier = modifier.fillMaxSize().background(Background).verticalScroll(rememberScrollState()).padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text("المسار الاحترافي", color = Ink, style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
        Text("من المبادئ العلمية إلى تصميم منزل كامل داخل المختبر.", color = Muted)
        levels.forEachIndexed { index, pair ->
            Card(colors = CardDefaults.cardColors(containerColor = if (index == 0) SurfaceSoft else SurfaceDark), shape = RoundedCornerShape(20.dp)) {
                Row(Modifier.fillMaxWidth().padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                    Text(pair.first, color = if (index == 0) Accent else Muted, fontWeight = FontWeight.Bold)
                    Spacer(Modifier.width(16.dp))
                    Column(Modifier.weight(1f)) {
                        Text(pair.second, color = Ink, fontWeight = FontWeight.Bold)
                        Text(if (index == 0) "متاح الآن" else "يفتح بعد إتقان المستوى السابق", color = Muted)
                    }
                    Text(if (index == 0) "ابدأ" else "🔒", color = if (index == 0) Accent else Muted)
                }
            }
        }
    }
}

@Composable
private fun ProjectHub(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier.fillMaxSize().background(Background).verticalScroll(rememberScrollState()).padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        Text("المشاريع الواقعية", color = Ink, style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
        Text("المهارة تثبت عندما تستطيع بناء النظام وفحصه وتشخيص أعطاله.", color = Muted)
        val projects = listOf(
            "شقة صغيرة" to "دوائر إنارة ومقابس وأحمال أساسية",
            "منزل عائلي" to "لوحة توزيع، حماية، دوائر مخصصة واختبارات",
            "فيلا تدريبية" to "المشروع النهائي: تصميم + تنفيذ محاكى + أعطال"
        )
        projects.forEachIndexed { index, project ->
            Card(colors = CardDefaults.cardColors(containerColor = SurfaceDark), shape = RoundedCornerShape(24.dp)) {
                Column(Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("المشروع ${index + 1}", color = Accent, fontWeight = FontWeight.Bold)
                    Text(project.first, color = Ink, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                    Text(project.second, color = Muted)
                    OutlinedButton(onClick = {}) { Text(if (index == 0) "استكشف" else "قريبًا") }
                }
            }
        }
    }
}
