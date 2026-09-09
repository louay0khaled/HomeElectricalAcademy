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
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
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
            containerColor = Color(0xFF0A1017),
            bottomBar = { AcademyNavigation(selected) { selected = it } }
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
private fun AcademyNavigation(selected: Int, onSelected: (Int) -> Unit) {
    NavigationBar(containerColor = SurfaceDark) {
        listOf("الرئيسية", "المختبر", "الأكاديمية", "المشاريع").forEachIndexed { index, label ->
            NavigationBarItem(
                selected = selected == index,
                onClick = { onSelected(index) },
                icon = { Text(listOf("⌂", "⚡", "◆", "▦")[index]) },
                label = { Text(label) }
            )
        }
    }
}

@Composable
private fun Dashboard(modifier: Modifier = Modifier) {
    Column(modifier.fillMaxSize().background(Brush.verticalGradient(listOf(Color(0xFF0A1017), Color(0xFF101B27)))).verticalScroll(rememberScrollState()).padding(20.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
        Text("HOME ELECTRICAL ACADEMY", color = Accent, fontWeight = FontWeight.Bold)
        Text("أكاديمية الكهرباء المنزلية", color = Ink, style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
        Text("تعلّم الكهرباء بالمحاكاة، لا بالمشاهدة.", color = Muted)
        Card(colors = CardDefaults.cardColors(containerColor = SurfaceSoft), shape = RoundedCornerShape(24.dp), modifier = Modifier.fillMaxWidth()) {
            Column(Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Column { Text("مسار الكهربائي السكني", color = Ink, fontWeight = FontWeight.Bold); Text("المستوى 01 • أساسيات الكهرباء", color = Muted) }
                    Text("12%", color = Accent, fontWeight = FontWeight.Bold)
                }
                Text("ابدأ بأول مختبر: الجهد والتيار والمقاومة", color = Ink)
                Button(onClick = {}, colors = ButtonDefaults.buttonColors(containerColor = Accent), modifier = Modifier.fillMaxWidth()) { Text("متابعة التدريب", color = Color(0xFF06110E), fontWeight = FontWeight.Bold) }
            }
        }
        Text("مختبرات اليوم", color = Ink, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
        LabCard("01", "افهم قانون أوم", "غيّر الجهد والمقاومة وتوقّع التيار قبل المحاكاة")
        LabCard("02", "المسار المفتوح والمغلق", "اكتشف لماذا يتوقف التيار عند انقطاع المسار")
        LabCard("03", "قدرة الحمل", "راقب العلاقة بين V و I و P داخل الدائرة")
        Text("المشروع الكبير", color = Ink, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
        Card(colors = CardDefaults.cardColors(containerColor = Color(0xFF132A2A)), shape = RoundedCornerShape(24.dp)) {
            Column(Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("🏠 منزل التدريب #001", color = Ink, fontWeight = FontWeight.Bold)
                Text("سنبدأ لاحقًا بتصميم لوحة التوزيع، الدوائر، الإنارة والمقابس واختبارات التسليم.", color = Muted)
                OutlinedButton(onClick = {}) { Text("استعراض المخطط") }
            }
        }
    }
}

@Composable
private fun LabCard(number: String, title: String, subtitle: String) {
    Card(colors = CardDefaults.cardColors(containerColor = SurfaceDark), shape = RoundedCornerShape(20.dp)) {
        Row(Modifier.fillMaxWidth().padding(18.dp), verticalAlignment = Alignment.CenterVertically) {
            androidx.compose.foundation.layout.Box(Modifier.size(46.dp).background(Accent.copy(alpha = 0.14f), RoundedCornerShape(14.dp)), contentAlignment = Alignment.Center) { Text(number, color = Accent, fontWeight = FontWeight.Bold) }
            Spacer(Modifier.width(14.dp))
            Column(Modifier.weight(1f)) { Text(title, color = Ink, fontWeight = FontWeight.Bold); Text(subtitle, color = Muted) }
            Text("›", color = Accent, style = MaterialTheme.typography.headlineSmall)
        }
    }
}

@Composable
private fun ScientificLab(modifier: Modifier = Modifier) {
    var voltage by remember { mutableFloatStateOf(12f) }
    var resistance by remember { mutableFloatStateOf(6f) }
    var closed by remember { mutableStateOf(true) }
    var predicted by remember { mutableFloatStateOf(0f) }
    val state = remember(voltage, resistance, closed) { IdealCircuitSolver().solve(VoltageSource("source", voltage.toDouble()), Resistor("load", resistance.toDouble()), closed) }
    Column(modifier.fillMaxSize().background(Color(0xFF0A1017)).verticalScroll(rememberScrollState()).padding(20.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
        Text("المختبر العلمي", color = Ink, style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
        Text("التجربة 01 • قانون أوم", color = Accent, fontWeight = FontWeight.Bold)
        Text("اقرأ الفكرة، اكتب توقعك، ثم ابنِ النتيجة من داخل النموذج.", color = Muted)
        Card(colors = CardDefaults.cardColors(containerColor = SurfaceDark), shape = RoundedCornerShape(24.dp)) {
            Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text("النموذج العلمي", color = Ink, fontWeight = FontWeight.Bold)
                Text("I = V / R", color = Accent2, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
                Text("كلما زاد الجهد زاد التيار عند ثبات المقاومة، وكلما زادت المقاومة انخفض التيار عند ثبات الجهد.", color = Muted)
                CircuitDiagram(state.energized)
            }
        }
        ControlCard("الجهد", "${"%.1f".format(voltage)} V") { androidx.compose.material3.Slider(voltage, { voltage = it }, 1f..24f) }
        ControlCard("المقاومة", "${"%.1f".format(resistance)} Ω") { androidx.compose.material3.Slider(resistance, { resistance = it }, 1f..24f) }
        Card(colors = CardDefaults.cardColors(containerColor = Color(0xFF121D29)), shape = RoundedCornerShape(20.dp)) {
            Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text("توقع قبل القياس", color = Ink, fontWeight = FontWeight.Bold)
                Text("إذا كانت الدائرة مغلقة، كم تتوقع أن يكون التيار؟", color = Muted)
                Button(onClick = { predicted = voltage / resistance }) { Text("احسب توقعي") }
                Text(if (predicted == 0f) "لم تُسجّل نتيجة بعد" else "توقعك = %.2f A".format(predicted), color = Accent)
            }
        }
        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            FilterChip(selected = closed, onClick = { closed = true }, label = { Text("المفتاح مغلق") })
            FilterChip(selected = !closed, onClick = { closed = false }, label = { Text("المفتاح مفتوح") })
        }
        Card(colors = CardDefaults.cardColors(containerColor = SurfaceSoft), shape = RoundedCornerShape(24.dp)) {
            Column(Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("قراءة المحاكاة", color = Ink, fontWeight = FontWeight.Bold)
                Metric("الجهد", "${"%.2f".format(state.voltageVolts)} V")
                Metric("التيار", "${"%.2f".format(state.currentAmps)} A")
                Metric("القدرة", "${"%.2f".format(state.powerWatts)} W")
                Text(if (state.energized) "✓ المسار الكهربائي مكتمل ضمن النموذج المثالي" else "○ لا يوجد مسار مغلق، لذلك لا يمر تيار", color = if (state.energized) Accent else Muted)
            }
        }
    }
}

@Composable
private fun CircuitDiagram(energized: Boolean) {
    Canvas(Modifier.fillMaxWidth().height(150.dp)) {
        val y = size.height / 2f
        val left = 45f
        val right = size.width - 45f
        val wireColor = if (energized) Accent else Color(0xFF657386)
        drawLine(wireColor, Offset(left, y), Offset(right - 100f, y), strokeWidth = 7f, cap = StrokeCap.Round)
        drawLine(wireColor, Offset(right - 55f, y), Offset(right, y), strokeWidth = 7f, cap = StrokeCap.Round)
        drawCircle(Color(0xFF203244), 30f, Offset(left, y))
        drawCircle(Accent2, 18f, Offset(left, y))
        drawLine(Color.White, Offset(right - 100f, y - 24f), Offset(right - 55f, y + 24f), strokeWidth = 7f, cap = StrokeCap.Round)
        drawCircle(if (energized) Accent else Color(0xFF4A5666), 26f, Offset(right, y))
        drawCircle(Color(0xFF0A1017), 16f, Offset(right, y))
    }
}

@Composable
private fun ControlCard(title: String, value: String, control: @Composable () -> Unit) {
    Card(colors = CardDefaults.cardColors(containerColor = SurfaceDark), shape = RoundedCornerShape(20.dp)) { Column(Modifier.padding(16.dp)) { Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) { Text(title, color = Ink, fontWeight = FontWeight.Bold); Text(value, color = Accent) }; control() } }
}

@Composable
private fun Metric(label: String, value: String) { Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) { Text(label, color = Muted); Text(value, color = Ink, fontWeight = FontWeight.Bold) } }

@Composable
private fun LearningPath(modifier: Modifier = Modifier) {
    val levels = listOf("01" to "أساسيات الكهرباء", "02" to "القياس والأدوات", "03" to "التيار المتردد والمنزل", "04" to "لوحة التوزيع والحماية", "05" to "تصميم الدوائر المنزلية", "06" to "التنفيذ والمحاكاة", "07" to "الفحص والاختبارات", "08" to "كشف الأعطال والمشروع النهائي")
    Column(modifier.fillMaxSize().background(Color(0xFF0A1017)).verticalScroll(rememberScrollState()).padding(20.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Text("المسار الاحترافي", color = Ink, style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
        Text("كل مستوى يربط العلم بالمحاكاة قبل الانتقال إلى التطبيق التالي.", color = Muted)
        levels.forEachIndexed { index, pair ->
            Card(colors = CardDefaults.cardColors(containerColor = if (index == 0) SurfaceSoft else SurfaceDark), shape = RoundedCornerShape(20.dp)) {
                Row(Modifier.fillMaxWidth().padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                    Text(pair.first, color = if (index == 0) Accent else Muted, fontWeight = FontWeight.Bold)
                    Spacer(Modifier.width(16.dp))
                    Column(Modifier.weight(1f)) { Text(pair.second, color = Ink, fontWeight = FontWeight.Bold); Text(if (index == 0) "متاح الآن • 3 مختبرات" else "يفتح بعد إتقان المستوى السابق", color = Muted) }
                    Text(if (index == 0) "ابدأ" else "🔒", color = if (index == 0) Accent else Muted)
                }
            }
        }
    }
}

@Composable
private fun ProjectHub(modifier: Modifier = Modifier) {
    Column(modifier.fillMaxSize().background(Color(0xFF0A1017)).verticalScroll(rememberScrollState()).padding(20.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
        Text("المشاريع الواقعية", color = Ink, style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
        Text("هنا يتحول الطالب من حل التمارين إلى بناء نظام منزل كامل داخل المحاكاة.", color = Muted)
        listOf("شقة صغيرة" to "8 دوائر • إنارة ومقابس وأحمال أساسية", "منزل عائلي" to "16 دائرة • لوحة توزيع وحمايات واختبارات", "فيلا تدريبية" to "مشروع نهائي • تصميم + تمديد + كشف أعطال").forEachIndexed { index, item ->
            Card(colors = CardDefaults.cardColors(containerColor = SurfaceDark), shape = RoundedCornerShape(24.dp)) {
                Column(Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("المشروع ${index + 1}", color = Accent, fontWeight = FontWeight.Bold)
                    Text(item.first, color = Ink, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                    Text(item.second, color = Muted)
                    OutlinedButton(onClick = {}) { Text(if (index == 0) "ابدأ المشروع" else "قريبًا") }
                }
            }
        }
    }
}
