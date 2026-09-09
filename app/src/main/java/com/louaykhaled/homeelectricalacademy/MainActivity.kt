package com.louaykhaled.homeelectricalacademy

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.louaykhaled.homeelectricalacademy.core.model.Resistor
import com.louaykhaled.homeelectricalacademy.core.model.VoltageSource
import com.louaykhaled.homeelectricalacademy.core.simulator.IdealCircuitSolver

private val Bg = Color(0xFF071017)
private val Panel = Color(0xFF101B25)
private val Panel2 = Color(0xFF16242F)
private val TextMain = Color(0xFFEAF2F7)
private val TextMuted = Color(0xFF95A7B4)
private val Teal = Color(0xFF2ED6B4)
private val Blue = Color(0xFF74A9FF)
private val Amber = Color(0xFFFFC857)
private val Danger = Color(0xFFFF6B6B)

enum class AppIcon { HOME, BOOK, METER, PANEL, PROJECT, SOCKET, LAMP, SWITCH, BREAKER, CABLE }

data class Lesson(val title: String, val subtitle: String, val body: String)
data class Project(val title: String, val body: String)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent { ElectricalAcademyApp() }
    }
}

@Composable
private fun ElectricalAcademyApp() {
    var tab by remember { mutableIntStateOf(0) }
    var lesson by remember { mutableStateOf<Lesson?>(null) }
    var project by remember { mutableStateOf<Project?>(null) }

    MaterialTheme {
        Scaffold(
            containerColor = Bg,
            bottomBar = {
                NavigationBar(containerColor = Panel) {
                    val items = listOf(
                        "الرئيسية" to AppIcon.HOME,
                        "التعلّم" to AppIcon.BOOK,
                        "الأدوات" to AppIcon.METER,
                        "اللوحة" to AppIcon.PANEL,
                        "المشاريع" to AppIcon.PROJECT
                    )
                    items.forEachIndexed { i, item ->
                        NavigationBarItem(
                            selected = tab == i,
                            onClick = { tab = i },
                            icon = { ElectricIcon(item.second, if (tab == i) Teal else TextMuted, 24) },
                            label = { Text(item.first) }
                        )
                    }
                }
            }
        ) { pad ->
            when (tab) {
                0 -> HomeScreen(onTab = { tab = it }, onLesson = { lesson = it }, onProject = { project = it }, modifier = Modifier.padding(pad))
                1 -> AcademyScreen(onLesson = { lesson = it }, modifier = Modifier.padding(pad))
                2 -> ToolsScreen(modifier = Modifier.padding(pad))
                3 -> PanelScreen(modifier = Modifier.padding(pad))
                else -> ProjectsScreen(onProject = { project = it }, modifier = Modifier.padding(pad))
            }
        }
    }

    lesson?.let { l ->
        AlertDialog(
            onDismissRequest = { lesson = null },
            containerColor = Panel,
            title = { Text(l.title, color = TextMain, fontWeight = FontWeight.Bold) },
            text = { Text(l.body, color = TextMuted) },
            confirmButton = {
                TextButton(onClick = { lesson = null }) { Text("تم الفهم", color = Teal) }
            }
        )
    }
    project?.let { p ->
        AlertDialog(
            onDismissRequest = { project = null },
            containerColor = Panel,
            title = { Text(p.title, color = TextMain, fontWeight = FontWeight.Bold) },
            text = { Text(p.body, color = TextMuted) },
            confirmButton = {
                TextButton(onClick = { project = null }) { Text("بدء المشروع", color = Teal) }
            }
        )
    }
}

@Composable
private fun HomeScreen(
    onTab: (Int) -> Unit,
    onLesson: (Lesson) -> Unit,
    onProject: (Project) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(18.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
        Text("HOME ELECTRICAL ACADEMY", color = Teal, fontWeight = FontWeight.Bold)
        Text("أكاديمية الكهرباء المنزلية", color = TextMain, style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
        Text("تطبيق عملي للكهربائي: تعلّم، قِس، صمّم، اختبر وشخّص.", color = TextMuted)

        Card(colors = CardDefaults.cardColors(containerColor = Panel2), shape = RoundedCornerShape(24.dp)) {
            Column(Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    ElectricIcon(AppIcon.BOOK, Teal, 34)
                    Spacer(Modifier.width(12.dp))
                    Column {
                        Text("المسار النشط", color = TextMuted)
                        Text("كهربائي سكني • المستوى 01", color = TextMain, fontWeight = FontWeight.Bold)
                    }
                }
                Text("ابدأ بالأساسيات ثم انتقل إلى القياس والإنارة والمقابس ولوحة التوزيع وكشف الأعطال.", color = TextMuted)
                Button(onClick = { onLesson(Lesson("أساسيات الكهرباء", "المستوى 01", "ابدأ بفهم الجهد والتيار والمقاومة والقدرة، ثم طبّقها في دوائر إنارة ومقابس بسيطة.")) }, modifier = Modifier.fillMaxWidth()) {
                    Text("ابدأ التدريب")
                }
            }
        }

        SectionTitle("الوصول السريع")
        QuickCard("قانون أوم", "احسب التيار والقدرة بمحاكاة مباشرة", AppIcon.METER) { onTab(2) }
        QuickCard("الإنارة والمفتاح", "تعلّم مسار المفتاح والحمل واختبره", AppIcon.LAMP) { onLesson(Lesson("دائرة إنارة منزلية", "مفتاح + مصباح", "في دائرة الإنارة التدريبية، يمر مسار التغذية عبر المفتاح إلى المصباح. جرّب تغيير حالة المفتاح ومراقبة النتيجة.")) }
        QuickCard("لوحة التوزيع", "شغّل القواطع واكتشف الحمل على كل دائرة", AppIcon.PANEL) { onTab(3) }
        QuickCard("كشف عطل", "تدرّب على عطل مفتاح أو قاطع أو حمل", AppIcon.BREAKER) { onProject(Project("تشخيص عطل", "افحص الدائرة بالتسلسل: التغذية، القاطع، المفتاح، الحمل، ثم المسار الراجع. هذا نموذج تدريبي وليس بديلاً عن إجراءات السلامة والفحص الفعلي.")) }

        SectionTitle("المهارات التي ستبنيها")
        SkillRow("قراءة مخطط دائرة", "فهم مسار التغذية والعودة")
        SkillRow("القياس", "استخدام الملتيميتر بصورة صحيحة داخل التدريب")
        SkillRow("التوزيع", "فهم القواطع والدوائر المنزلية")
        SkillRow("التشخيص", "عزل الخلل خطوة بخطوة")
    }
}

@Composable
private fun AcademyScreen(onLesson: (Lesson) -> Unit, modifier: Modifier = Modifier) {
    val lessons = listOf(
        Lesson("01 • أساسيات الكهرباء", "الجهد، التيار، المقاومة والقدرة", "افهم المفاهيم التي يحتاجها الكهربائي قبل لمس أي دائرة، ثم اختبرها في المختبر."),
        Lesson("02 • القياس والأدوات", "الملتيميتر، مفك الفحص وأدوات العمل", "تعلّم وظيفة كل أداة ومتى تستخدمها، مع التركيز على الاختيار الصحيح لوضع القياس."),
        Lesson("03 • دوائر الإنارة", "المفتاح، المصباح، التمديد", "اربط المفهوم بالمخطط الواقعي: تغذية، مفتاح، حمل ومسار عودة."),
        Lesson("04 • المقابس والأحمال", "المقابس والأجهزة المنزلية", "صمّم دوائر مقابس بسيطة وتعرّف على الأحمال التي تحتاج إلى دائرة مخصصة."),
        Lesson("05 • لوحة التوزيع", "القاطع الرئيسي والقواطع الفرعية", "تعرّف إلى بنية لوحة التدريب وكيف تتوزع دوائر الإنارة والمقابس والأحمال."),
        Lesson("06 • الحماية والتأريض", "الحماية من الأعطال والصعق", "مبادئ القواطع ووسائل الحماية والتأريض ضمن شرح تدريبي عام."),
        Lesson("07 • الفحص وكشف الأعطال", "من الأعراض إلى سبب العطل", "تدرّب على تشخيص دائرة لا تعمل باستخدام ترتيب فحص منطقي."),
        Lesson("08 • مشروع منزل", "تصميم منظومة منزلية تدريبية", "اجمع ما تعلمته في مشروع شقة صغيرة مع دوائر إنارة ومقابس ولوحة توزيع.")
    )
    Column(modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(18.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Text("المسار الاحترافي", color = TextMain, style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
        Text("كل بطاقة قابلة للفتح وتشرح لك ماذا تتعلم وماذا تطبق.", color = TextMuted)
        lessons.forEach { l ->
            Card(colors = CardDefaults.cardColors(containerColor = Panel), shape = RoundedCornerShape(20.dp), modifier = Modifier.clickable { onLesson(l) }) {
                Row(Modifier.fillMaxWidth().padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                    ElectricIcon(AppIcon.BOOK, Teal, 30)
                    Spacer(Modifier.width(14.dp))
                    Column(Modifier.weight(1f)) {
                        Text(l.title, color = TextMain, fontWeight = FontWeight.Bold)
                        Text(l.subtitle, color = TextMuted)
                    }
                    Text("فتح", color = Teal, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
private fun ToolsScreen(modifier: Modifier = Modifier) {
    var mode by remember { mutableIntStateOf(0) }
    Column(modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(18.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
        Text("أدوات الكهربائي", color = TextMain, style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
        Text("أدوات عملية للتدريب والحساب والفحص.", color = TextMuted)
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            FilterChip(selected = mode == 0, onClick = { mode = 0 }, label = { Text("الحساب") })
            FilterChip(selected = mode == 1, onClick = { mode = 1 }, label = { Text("ملتيميتر") })
            FilterChip(selected = mode == 2, onClick = { mode = 2 }, label = { Text("الأدوات") })
        }
        when (mode) {
            0 -> OhmCalculator()
            1 -> MultimeterTrainer()
            else -> ToolCatalog()
        }
    }
}

@Composable
private fun OhmCalculator() {
    var voltage by remember { mutableFloatStateOf(230f) }
    var resistance by remember { mutableFloatStateOf(46f) }
    val current = voltage / resistance.coerceAtLeast(0.1f)
    val power = voltage * current
    Card(colors = CardDefaults.cardColors(containerColor = Panel), shape = RoundedCornerShape(22.dp)) {
        Column(Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) { ElectricIcon(AppIcon.METER, Blue, 30); Spacer(Modifier.width(10.dp)); Text("حاسبة الجهد والتيار", color = TextMain, fontWeight = FontWeight.Bold) }
            ValueSlider("الجهد", voltage, "%.0f V", 12f..250f) { voltage = it }
            ValueSlider("المقاومة", resistance, "%.0f Ω", 1f..200f) { resistance = it }
            ResultLine("التيار", "%.2f A".format(current))
            ResultLine("القدرة", "%.0f W".format(power))
            Text("تنبيه: هذه الحاسبة تدريبية ولا تفترض أن أي قيمة هي اختيار آمن لتنفيذ فعلي.", color = Amber)
        }
    }
}

@Composable
private fun ValueSlider(label: String, value: Float, format: String, range: ClosedFloatingPointRange<Float>, onChange: (Float) -> Unit) {
    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) { Text(label, color = TextMain, fontWeight = FontWeight.Bold); Text(format.format(value), color = Teal) }
    Slider(value = value, onValueChange = onChange, valueRange = range)
}

@Composable
private fun MultimeterTrainer() {
    var dc by remember { mutableStateOf(false) }
    var reading by remember { mutableFloatStateOf(228f) }
    Card(colors = CardDefaults.cardColors(containerColor = Panel), shape = RoundedCornerShape(22.dp)) {
        Column(Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) { ElectricIcon(AppIcon.METER, Blue, 32); Spacer(Modifier.width(10.dp)); Text("مدرّب الملتيميتر", color = TextMain, fontWeight = FontWeight.Bold) }
            Text("اختر نوع القياس ثم اضبط القراءة التدريبية.", color = TextMuted)
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("وضع DC", color = TextMain)
                Spacer(Modifier.width(10.dp))
                Switch(checked = dc, onCheckedChange = { dc = it })
                Text(if (dc) "جهد مستمر" else "جهد متردد", color = Teal)
            }
            Slider(value = reading, onValueChange = { reading = it }, valueRange = 0f..250f)
            Card(colors = CardDefaults.cardColors(containerColor = Panel2), shape = RoundedCornerShape(16.dp)) {
                Column(Modifier.padding(16.dp)) {
                    Text("شاشة الجهاز", color = TextMuted)
                    Text("%.1f V".format(reading), color = TextMain, style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
                }
            }
            Text("في القياس الحقيقي، اختر الوظيفة والمدى الصحيحين وتحقق من مواضع المجسات قبل القياس.", color = Amber)
        }
    }
}

@Composable
private fun ToolCatalog() {
    val tools = listOf(
        AppIcon.METER to ("ملتيميتر" to "قياس الجهد والتيار والمقاومة"),
        AppIcon.SOCKET to ("فاحص المقابس" to "تدريب على فحص حالة المقبس"),
        AppIcon.CABLE to ("أسلاك وكابلات" to "التعرّف على أنواع المسارات ضمن التدريب"),
        AppIcon.BREAKER to ("قاطع" to "التعرّف على وظيفة القاطع في اللوحة"),
        AppIcon.SWITCH to ("مفتاح" to "عنصر التحكم في دائرة الإنارة"),
        AppIcon.LAMP to ("مصباح" to "حمل إنارة داخل الدائرة")
    )
    tools.forEach { item ->
        Card(colors = CardDefaults.cardColors(containerColor = Panel), shape = RoundedCornerShape(18.dp)) {
            Row(Modifier.fillMaxWidth().padding(15.dp), verticalAlignment = Alignment.CenterVertically) {
                ElectricIcon(item.first, Teal, 30)
                Spacer(Modifier.width(14.dp))
                Column { Text(item.second.first, color = TextMain, fontWeight = FontWeight.Bold); Text(item.second.second, color = TextMuted) }
            }
        }
    }
}

@Composable
private fun PanelScreen(modifier: Modifier = Modifier) {
    var mainOn by remember { mutableStateOf(true) }
    var lights by remember { mutableStateOf(true) }
    var sockets by remember { mutableStateOf(true) }
    var kitchen by remember { mutableStateOf(false) }
    var ac by remember { mutableStateOf(false) }
    val load = (if (lights) 600 else 0) + (if (sockets) 1800 else 0) + (if (kitchen) 3000 else 0) + (if (ac) 1800 else 0)
    Column(modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(18.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Text("لوحة التوزيع التدريبية", color = TextMain, style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
        Text("محاكاة تعليمية للدوائر والقواطع، وليست مخطط تنفيذ فعلي.", color = TextMuted)
        DistributionPanel(mainOn, lights, sockets, kitchen, ac)
        CircuitToggle("القاطع الرئيسي", AppIcon.BREAKER, mainOn) { mainOn = it }
        CircuitToggle("دائرة الإنارة", AppIcon.LAMP, lights && mainOn) { lights = it }
        CircuitToggle("دائرة المقابس", AppIcon.SOCKET, sockets && mainOn) { sockets = it }
        CircuitToggle("دائرة المطبخ", AppIcon.SOCKET, kitchen && mainOn) { kitchen = it }
        CircuitToggle("دائرة المكيف", AppIcon.CABLE, ac && mainOn) { ac = it }
        Card(colors = CardDefaults.cardColors(containerColor = if (load > 6000 && mainOn) Color(0xFF3A2420) else Panel), shape = RoundedCornerShape(20.dp)) {
            Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text("ملخص اللوحة", color = TextMain, fontWeight = FontWeight.Bold)
                ResultLine("الحمل التدريبي", "${load} W")
                ResultLine("الدوائر النشطة", listOf(lights, sockets, kitchen, ac).count { it && mainOn }.toString())
                Text(if (!mainOn) "القاطع الرئيسي مفصول" else if (load > 6000) "حالة تدريب: حمل مرتفع" else "اللوحة تعمل في وضع التدريب", color = if (load > 6000) Danger else Teal)
            }
        }
        Text("ملاحظة السلامة: القيم والأحمال هنا أمثلة تدريبية. التنفيذ الحقيقي يعتمد على الكود المحلي وحسابات الأحمال واختيار الحماية المناسبة.", color = Amber)
    }
}

@Composable
private fun DistributionPanel(mainOn: Boolean, lights: Boolean, sockets: Boolean, kitchen: Boolean, ac: Boolean) {
    Card(colors = CardDefaults.cardColors(containerColor = Color(0xFF0D151C)), shape = RoundedCornerShape(20.dp)) {
        Column(Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) { ElectricIcon(AppIcon.PANEL, Teal, 34); Spacer(Modifier.width(10.dp)); Text("MAIN DISTRIBUTION", color = TextMain, fontWeight = FontWeight.Bold) }
            PanelRow("MAIN", mainOn)
            PanelRow("LIGHTING", mainOn && lights)
            PanelRow("SOCKETS", mainOn && sockets)
            PanelRow("KITCHEN", mainOn && kitchen)
            PanelRow("A/C", mainOn && ac)
        }
    }
}

@Composable
private fun PanelRow(label: String, on: Boolean) {
    Row(Modifier.fillMaxWidth().padding(horizontal = 8.dp, vertical = 4.dp), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
        Text(label, color = TextMuted)
        Row(verticalAlignment = Alignment.CenterVertically) { Text(if (on) "ON" else "OFF", color = if (on) Teal else TextMuted, fontWeight = FontWeight.Bold); Spacer(Modifier.width(8.dp)); ElectricIcon(AppIcon.BREAKER, if (on) Teal else TextMuted, 22) }
    }
}

@Composable
private fun CircuitToggle(title: String, icon: AppIcon, checked: Boolean, onChange: (Boolean) -> Unit) {
    Card(colors = CardDefaults.cardColors(containerColor = Panel), shape = RoundedCornerShape(18.dp), modifier = Modifier.clickable { onChange(!checked) }) {
        Row(Modifier.fillMaxWidth().padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
            ElectricIcon(icon, Teal, 28)
            Spacer(Modifier.width(12.dp))
            Text(title, color = TextMain, modifier = Modifier.weight(1f), fontWeight = FontWeight.Bold)
            Switch(checked = checked, onCheckedChange = onChange)
        }
    }
}

@Composable
private fun ProjectsScreen(onProject: (Project) -> Unit, modifier: Modifier = Modifier) {
    val projects = listOf(
        Project("مشروع غرفة نوم", "ابنِ دائرة إنارة مع مفتاح ومصباح، ثم أضف مقبساً ضمن نموذج التدريب."),
        Project("مشروع مطبخ", "حدّد الدوائر المطلوبة للأحمال المنزلية المختلفة ثم راقب توزيعها على لوحة التدريب."),
        Project("مشروع شقة صغيرة", "أنشئ لوحة تدريبية تضم الإنارة والمقابس والمطبخ والمكيف مع فحص حالة كل دائرة."),
        Project("مشروع كشف عطل", "أوجد العطل من الأعراض: دائرة بلا تغذية، قاطع مفصول، مفتاح مفتوح أو حمل غير متصل.")
    )
    Column(modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(18.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Text("المشاريع العملية", color = TextMain, style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
        Text("لا يوجد زر وهمي: كل مشروع يفتح شرحاً ويمكنك بدء مساره من هنا.", color = TextMuted)
        projects.forEach { p ->
            Card(colors = CardDefaults.cardColors(containerColor = Panel), shape = RoundedCornerShape(20.dp), modifier = Modifier.clickable { onProject(p) }) {
                Row(Modifier.fillMaxWidth().padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                    ElectricIcon(AppIcon.PROJECT, Blue, 30)
                    Spacer(Modifier.width(12.dp))
                    Column(Modifier.weight(1f)) { Text(p.title, color = TextMain, fontWeight = FontWeight.Bold); Text(p.body, color = TextMuted) }
                    Text("فتح", color = Teal, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
private fun SectionTitle(text: String) { Text(text, color = TextMain, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold) }

@Composable
private fun QuickCard(title: String, subtitle: String, icon: AppIcon, onClick: () -> Unit) {
    Card(colors = CardDefaults.cardColors(containerColor = Panel), shape = RoundedCornerShape(20.dp), modifier = Modifier.clickable(onClick = onClick)) {
        Row(Modifier.fillMaxWidth().padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            ElectricIcon(icon, Teal, 32)
            Spacer(Modifier.width(14.dp))
            Column(Modifier.weight(1f)) { Text(title, color = TextMain, fontWeight = FontWeight.Bold); Text(subtitle, color = TextMuted) }
            Text("›", color = Teal, style = MaterialTheme.typography.headlineMedium)
        }
    }
}

@Composable
private fun SkillRow(title: String, body: String) {
    Card(colors = CardDefaults.cardColors(containerColor = Panel), shape = RoundedCornerShape(16.dp)) {
        Row(Modifier.fillMaxWidth().padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
            Canvas(Modifier.size(12.dp)) { drawCircle(Teal, 6f, Offset(6f, 6f)) }
            Spacer(Modifier.width(12.dp))
            Column { Text(title, color = TextMain, fontWeight = FontWeight.Bold); Text(body, color = TextMuted) }
        }
    }
}

@Composable
private fun ResultLine(label: String, value: String) {
    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) { Text(label, color = TextMuted); Text(value, color = TextMain, fontWeight = FontWeight.Bold) }
}

@Composable
private fun ElectricIcon(icon: AppIcon, color: Color, sizeDp: Int) {
    Canvas(Modifier.size(sizeDp.dp)) {
        val w = size.width
        val h = size.height
        val stroke = (sizeDp.coerceAtMost(34) / 7f).coerceAtLeast(2f)
        when (icon) {
            AppIcon.HOME -> {
                val roof = androidx.compose.ui.graphics.Path().apply { moveTo(w * .12f, h * .45f); lineTo(w * .5f, h * .12f); lineTo(w * .88f, h * .45f) }
                drawPath(roof, color, style = androidx.compose.ui.graphics.drawscope.Stroke(stroke, cap = StrokeCap.Round, join = StrokeJoin.Round))
                drawLine(color, Offset(w*.2f,h*.43f), Offset(w*.2f,h*.88f), stroke, cap=StrokeCap.Round)
                drawLine(color, Offset(w*.8f,h*.43f), Offset(w*.8f,h*.88f), stroke, cap=StrokeCap.Round)
                drawLine(color, Offset(w*.2f,h*.88f), Offset(w*.8f,h*.88f), stroke, cap=StrokeCap.Round)
                drawLine(color, Offset(w*.48f,h*.88f), Offset(w*.48f,h*.62f), stroke, cap=StrokeCap.Round)
                drawLine(color, Offset(w*.62f,h*.88f), Offset(w*.62f,h*.62f), stroke, cap=StrokeCap.Round)
            }
            AppIcon.BOOK -> {
                drawRoundRect(color, topLeft=Offset(w*.18f,h*.18f), size=Size(w*.64f,h*.64f), cornerRadius=androidx.compose.ui.geometry.CornerRadius(w*.08f), style=androidx.compose.ui.graphics.drawscope.Stroke(stroke))
                drawLine(color, Offset(w*.34f,h*.18f), Offset(w*.34f,h*.82f), stroke)
                drawLine(color, Offset(w*.48f,h*.32f), Offset(w*.70f,h*.32f), stroke)
                drawLine(color, Offset(w*.48f,h*.48f), Offset(w*.70f,h*.48f), stroke)
            }
            AppIcon.METER -> {
                drawRoundRect(color, topLeft=Offset(w*.18f,h*.08f), size=Size(w*.64f,h*.84f), cornerRadius=androidx.compose.ui.geometry.CornerRadius(w*.08f), style=androidx.compose.ui.graphics.drawscope.Stroke(stroke))
                drawLine(color, Offset(w*.30f,h*.29f), Offset(w*.70f,h*.29f), stroke)
                drawCircle(color, w*.16f, Offset(w*.5f,h*.55f), style=androidx.compose.ui.graphics.drawscope.Stroke(stroke))
                drawCircle(color, w*.07f, Offset(w*.5f,h*.76f))
            }
            AppIcon.PANEL -> {
                drawRoundRect(color, topLeft=Offset(w*.14f,h*.08f), size=Size(w*.72f,h*.84f), cornerRadius=androidx.compose.ui.geometry.CornerRadius(w*.06f), style=androidx.compose.ui.graphics.drawscope.Stroke(stroke))
                for (i in 0..2) drawRoundRect(color, topLeft=Offset(w*.25f,h*(.22f+i*.22f)), size=Size(w*.5f,h*.12f), cornerRadius=androidx.compose.ui.geometry.CornerRadius(3f), style=androidx.compose.ui.graphics.drawscope.Stroke(stroke))
            }
            AppIcon.PROJECT -> {
                drawRoundRect(color, topLeft=Offset(w*.14f,h*.12f), size=Size(w*.72f,h*.76f), cornerRadius=androidx.compose.ui.geometry.CornerRadius(w*.08f), style=androidx.compose.ui.graphics.drawscope.Stroke(stroke))
                drawLine(color, Offset(w*.3f,h*.34f), Offset(w*.7f,h*.34f), stroke)
                drawLine(color, Offset(w*.3f,h*.5f), Offset(w*.7f,h*.5f), stroke)
                drawLine(color, Offset(w*.3f,h*.66f), Offset(w*.58f,h*.66f), stroke)
            }
            AppIcon.SOCKET -> {
                drawRoundRect(color, topLeft=Offset(w*.2f,h*.14f), size=Size(w*.6f,h*.72f), cornerRadius=androidx.compose.ui.geometry.CornerRadius(w*.1f), style=androidx.compose.ui.graphics.drawscope.Stroke(stroke))
                drawRoundRect(color, topLeft=Offset(w*.36f,h*.32f), size=Size(w*.10f,h*.28f), cornerRadius=androidx.compose.ui.geometry.CornerRadius(2f), style=androidx.compose.ui.graphics.drawscope.Stroke(stroke))
                drawRoundRect(color, topLeft=Offset(w*.54f,h*.32f), size=Size(w*.10f,h*.28f), cornerRadius=androidx.compose.ui.geometry.CornerRadius(2f), style=androidx.compose.ui.graphics.drawscope.Stroke(stroke))
                drawCircle(color, w*.04f, Offset(w*.5f,h*.72f))
            }
            AppIcon.LAMP -> {
                drawCircle(color, w*.24f, Offset(w*.5f,h*.42f), style=androidx.compose.ui.graphics.drawscope.Stroke(stroke))
                drawLine(color, Offset(w*.42f,h*.63f), Offset(w*.58f,h*.63f), stroke)
                drawLine(color, Offset(w*.43f,h*.70f), Offset(w*.57f,h*.70f), stroke)
                for (i in 0..7) { val a = i * Math.PI / 4; val x1 = w*.5f + kotlin.math.cos(a).toFloat()*w*.36f; val y1 = h*.42f + kotlin.math.sin(a).toFloat()*h*.36f; val x2 = w*.5f + kotlin.math.cos(a).toFloat()*w*.45f; val y2 = h*.42f + kotlin.math.sin(a).toFloat()*h*.45f; drawLine(color, Offset(x1,y1), Offset(x2,y2), stroke, cap=StrokeCap.Round) }
            }
            AppIcon.SWITCH -> {
                drawCircle(color, w*.18f, Offset(w*.25f,h*.58f)); drawCircle(color, w*.18f, Offset(w*.75f,h*.42f)); drawLine(color, Offset(w*.25f,h*.58f), Offset(w*.68f,h*.25f), stroke, cap=StrokeCap.Round)
                drawLine(color, Offset(w*.25f,h*.72f), Offset(w*.25f,h*.58f), stroke); drawLine(color, Offset(w*.75f,h*.28f), Offset(w*.75f,h*.42f), stroke)
            }
            AppIcon.BREAKER -> {
                drawRoundRect(color, topLeft=Offset(w*.18f,h*.16f), size=Size(w*.64f,h*.68f), cornerRadius=androidx.compose.ui.geometry.CornerRadius(w*.06f), style=androidx.compose.ui.graphics.drawscope.Stroke(stroke))
                drawLine(color, Offset(w*.5f,h*.68f), Offset(w*.5f,h*.34f), stroke, cap=StrokeCap.Round)
                drawCircle(color, w*.05f, Offset(w*.5f,h*.27f))
            }
            AppIcon.CABLE -> {
                drawLine(color, Offset(w*.12f,h*.52f), Offset(w*.58f,h*.52f), stroke, cap=StrokeCap.Round)
                drawLine(color, Offset(w*.58f,h*.52f), Offset(w*.82f,h*.30f), stroke, cap=StrokeCap.Round)
                drawLine(color, Offset(w*.78f,h*.24f), Offset(w*.9f,h*.36f), stroke, cap=StrokeCap.Round)
                drawLine(color, Offset(w*.76f,h*.32f), Offset(w*.88f,h*.44f), stroke, cap=StrokeCap.Round)
            }
        }
    }
}
