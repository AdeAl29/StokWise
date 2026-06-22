package com.ade.fuzzyrisk.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.HelpOutline
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Analytics
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Inventory
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PhoneAndroid
import androidx.compose.material.icons.filled.Recommend
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.StackedLineChart
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material.icons.outlined.Inventory2
import androidx.compose.material.icons.outlined.LocalMall
import androidx.compose.material.icons.outlined.People
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.ade.fuzzyrisk.R
import com.ade.fuzzyrisk.data.SalesRecord
import com.ade.fuzzyrisk.domain.FuzzyGraphSpec
import com.ade.fuzzyrisk.domain.FuzzyResult
import com.ade.fuzzyrisk.domain.FuzzyTsukamotoCalculator
import com.ade.fuzzyrisk.ui.ChartBlue
import com.ade.fuzzyrisk.ui.Danger
import com.ade.fuzzyrisk.ui.NumberFormat
import com.ade.fuzzyrisk.ui.PhoneModelOption
import com.ade.fuzzyrisk.ui.PhoneTypeGroup
import com.ade.fuzzyrisk.ui.Success
import com.ade.fuzzyrisk.ui.Warning
import com.ade.fuzzyrisk.ui.dateText
import com.ade.fuzzyrisk.ui.percent
import com.ade.fuzzyrisk.ui.phoneBrandName
import com.ade.fuzzyrisk.ui.phoneImageUrl
import com.ade.fuzzyrisk.ui.phoneModelName
import com.ade.fuzzyrisk.ui.phoneTypeGroupsForRisk
import com.ade.fuzzyrisk.ui.riskColor
import com.ade.fuzzyrisk.ui.riskFromZ

@Composable
fun ThemeToggleButton(darkTheme: Boolean, onThemeChange: (Boolean) -> Unit) {
    IconButton(onClick = { onThemeChange(!darkTheme) }) {
        Icon(
            imageVector = if (darkTheme) Icons.Filled.LightMode else Icons.Filled.DarkMode,
            contentDescription = if (darkTheme) "Aktifkan tema terang" else "Aktifkan tema gelap",
            tint = MaterialTheme.colorScheme.primary
        )
    }
}

@Composable
fun DashboardMetrics(records: List<SalesRecord>) {
    val average = records.map { it.zValue }.average().takeIf { !it.isNaN() } ?: 0.0
    val highRisk = records.count { it.riskLevel == "Tinggi" }
    Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
        MetricCard("Data", records.size.toString(), Icons.Filled.Inventory, MaterialTheme.colorScheme.primary, Modifier.weight(1f))
        MetricCard("Rata-rata", NumberFormat.format(average), Icons.Filled.StackedLineChart, Warning, Modifier.weight(1f))
        MetricCard("Tinggi", highRisk.toString(), Icons.Filled.ErrorOutline, Danger, Modifier.weight(1f))
    }
}

@Composable
fun MetricCard(title: String, value: String, icon: ImageVector, color: Color, modifier: Modifier) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = color.copy(alpha = 0.12f)),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(Modifier.padding(12.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(icon, contentDescription = null, tint = color, modifier = Modifier.size(24.dp))
            Spacer(Modifier.height(6.dp))
            Text(value, fontSize = 20.sp, fontWeight = FontWeight.ExtraBold, color = color, maxLines = 1)
            Text(title, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant, maxLines = 1)
        }
    }
}

@Composable
fun RiskHeroCard(record: SalesRecord?) {
    val label = record?.riskLevel ?: "Belum Ada"
    val z = record?.zValue ?: 0.0
    AppCard {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Column(Modifier.weight(1f)) {
                Text("Status Risiko Hari Ini", fontWeight = FontWeight.Bold)
                Spacer(Modifier.height(8.dp))
                Text(label.uppercase(), fontSize = 30.sp, fontWeight = FontWeight.ExtraBold, color = riskColor(label))
                Text(record?.dateText() ?: "Input data untuk melihat hasil", color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            Gauge(z = z, color = riskColor(label))
        }
    }
}

@Composable
fun Gauge(z: Double, color: Color) {
    val trackColor = MaterialTheme.colorScheme.surfaceVariant
    Box(contentAlignment = Alignment.Center, modifier = Modifier.size(112.dp)) {
        Canvas(Modifier.size(104.dp)) {
            drawArc(
                color = trackColor,
                startAngle = 150f,
                sweepAngle = 240f,
                useCenter = false,
                style = Stroke(14.dp.toPx(), cap = StrokeCap.Round)
            )
            drawArc(
                color = color,
                startAngle = 150f,
                sweepAngle = (240f * (z / 100.0).coerceIn(0.0, 1.0)).toFloat(),
                useCenter = false,
                style = Stroke(14.dp.toPx(), cap = StrokeCap.Round)
            )
        }
        Text(NumberFormat.format(z), fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
    }
}

@Composable
fun SummaryCard(record: SalesRecord?) {
    AppCard {
        Text("Ringkasan Hari Ini", fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(12.dp))
        SummaryRow(Icons.Filled.PhoneAndroid, "Jenis HP", record?.phoneType ?: "-")
        SummaryRow(Icons.Filled.Add, "Barang Masuk", "${record?.incomingStock ?: 0} unit")
        SummaryRow(Icons.Outlined.LocalMall, "Penjualan", "${record?.sales ?: 0} unit")
        SummaryRow(Icons.Outlined.Inventory2, "Stok Akhir", "${record?.stock ?: 0} unit")
        SummaryRow(Icons.Outlined.People, "Permintaan", "${record?.demand ?: 0} unit")
    }
}

@Composable
fun SummaryRow(icon: ImageVector, title: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(icon, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(22.dp))
        Spacer(Modifier.width(12.dp))
        Text(title, modifier = Modifier.weight(1f), fontWeight = FontWeight.SemiBold)
        Text(value, fontWeight = FontWeight.Bold)
    }
}

@Composable
fun TextInputField(label: String, placeholder: String, value: String, onValueChange: (String) -> Unit) {
    Column {
        Text(label, fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(6.dp))
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier.fillMaxWidth(),
            placeholder = { Text(placeholder) },
            leadingIcon = { Icon(Icons.Filled.PhoneAndroid, contentDescription = null) },
            singleLine = true,
            shape = RoundedCornerShape(8.dp)
        )
    }
}

@Composable
fun PhoneBrandDropdownField(
    label: String,
    placeholder: String,
    brands: List<String>,
    selectedBrand: String,
    onBrandSelected: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    Column {
        Text(label, fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(6.dp))
        Box(Modifier.fillMaxWidth()) {
            OutlinedTextField(
                value = selectedBrand,
                onValueChange = {},
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { expanded = true },
                placeholder = { Text(placeholder) },
                leadingIcon = { Icon(Icons.Filled.PhoneAndroid, contentDescription = null) },
                trailingIcon = {
                    IconButton(onClick = { expanded = !expanded }) {
                        Icon(Icons.Filled.KeyboardArrowDown, contentDescription = "Pilih merek HP")
                    }
                },
                readOnly = true,
                singleLine = true,
                shape = RoundedCornerShape(8.dp)
            )
            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false },
                modifier = Modifier.fillMaxWidth()
            ) {
                brands.forEach { brand ->
                    DropdownMenuItem(
                        text = { Text(brand) },
                        leadingIcon = { Icon(Icons.Filled.PhoneAndroid, contentDescription = null) },
                        onClick = {
                            onBrandSelected(brand)
                            expanded = false
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun PhoneModelDropdownField(
    label: String,
    placeholder: String,
    models: List<PhoneModelOption>,
    selectedModel: String,
    enabled: Boolean,
    onModelSelected: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    val modelGroups = remember(models) { models.groupBy { it.year } }

    Column {
        Text(label, fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(6.dp))
        Box(Modifier.fillMaxWidth()) {
            OutlinedTextField(
                value = selectedModel,
                onValueChange = {},
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable(enabled = enabled) { expanded = true },
                enabled = enabled,
                placeholder = { Text(placeholder) },
                leadingIcon = { Icon(Icons.Filled.PhoneAndroid, contentDescription = null) },
                trailingIcon = {
                    IconButton(
                        onClick = { expanded = !expanded },
                        enabled = enabled
                    ) {
                        Icon(Icons.Filled.KeyboardArrowDown, contentDescription = "Pilih tipe HP")
                    }
                },
                readOnly = true,
                singleLine = true,
                shape = RoundedCornerShape(8.dp)
            )
            DropdownMenu(
                expanded = expanded && enabled,
                onDismissRequest = { expanded = false },
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = 360.dp)
            ) {
                modelGroups.forEach { (year, yearModels) ->
                    DropdownMenuItem(
                        text = {
                            Text(
                                year,
                                color = MaterialTheme.colorScheme.primary,
                                fontWeight = FontWeight.ExtraBold
                            )
                        },
                        enabled = false,
                        onClick = {}
                    )
                    yearModels.forEach { model ->
                        DropdownMenuItem(
                            text = { Text(model.name) },
                            leadingIcon = { Icon(Icons.Filled.PhoneAndroid, contentDescription = null) },
                            onClick = {
                                onModelSelected(model.name)
                                expanded = false
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun NumberField(label: String, placeholder: String, value: String, onValueChange: (String) -> Unit) {
    Column {
        Text(label, fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(6.dp))
        OutlinedTextField(
            value = value,
            onValueChange = { onValueChange(it.filter(Char::isDigit)) },
            modifier = Modifier.fillMaxWidth(),
            placeholder = { Text(placeholder) },
            keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(keyboardType = KeyboardType.Number),
            singleLine = true,
            shape = RoundedCornerShape(8.dp)
        )
    }
}

@Composable
fun PhoneImageThumbnail(
    phoneType: String,
    contentDescription: String,
    modifier: Modifier = Modifier.size(54.dp),
    iconSize: Dp = 28.dp
) {
    val imageUrl = phoneImageUrl(phoneType)
    var imageFailed by remember(phoneType) { mutableStateOf(false) }

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.12f)),
        contentAlignment = Alignment.Center
    ) {
        if (imageUrl != null && !imageFailed) {
            AsyncImage(
                model = imageUrl,
                contentDescription = contentDescription,
                contentScale = ContentScale.Fit,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(4.dp),
                onError = { imageFailed = true }
            )
        } else {
            Icon(
                Icons.Filled.PhoneAndroid,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(iconSize)
            )
        }
    }
}

@Composable
fun SelectedPhoneCard(phoneType: String) {
    AppCard {
        Row(verticalAlignment = Alignment.CenterVertically) {
            PhoneImageThumbnail(
                phoneType = phoneType,
                contentDescription = phoneType,
                modifier = Modifier.size(64.dp),
                iconSize = 32.dp
            )
            Spacer(Modifier.width(12.dp))
            Column(Modifier.weight(1f)) {
                Text("HP Dipilih", fontWeight = FontWeight.Bold)
                Text(
                    phoneType,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

@Composable
fun ResultCard(phoneType: String, result: FuzzyResult) {
    AppCard(horizontalAlignment = Alignment.CenterHorizontally) {
        Text("Hasil Perhitungan", fontWeight = FontWeight.Bold)
        if (phoneType.isNotBlank()) {
            Spacer(Modifier.height(8.dp))
            PhoneImageThumbnail(
                phoneType = phoneType,
                contentDescription = phoneType,
                modifier = Modifier.size(84.dp),
                iconSize = 36.dp
            )
            Spacer(Modifier.height(8.dp))
            Text(phoneType, color = MaterialTheme.colorScheme.onSurfaceVariant, textAlign = TextAlign.Center)
        }
        Spacer(Modifier.height(8.dp))
        Text("Tingkat Risiko", color = MaterialTheme.colorScheme.onSurfaceVariant)
        Text(
            result.riskLevel.label.uppercase(),
            fontSize = 28.sp,
            fontWeight = FontWeight.ExtraBold,
            color = riskColor(result.riskLevel.label)
        )
        Text("Nilai Akhir (Z)", color = MaterialTheme.colorScheme.onSurfaceVariant)
        Text(NumberFormat.format(result.zValue), fontSize = 22.sp, fontWeight = FontWeight.Bold)
    }
}

@Composable
fun FuzzyGraphSection(sales: Int, stock: Int, demand: Int, zValue: Double) {
    val inputGraphs = FuzzyTsukamotoCalculator.inputGraphSpecs(sales, stock, demand)
    val outputGraph = FuzzyTsukamotoCalculator.outputGraphSpec(zValue)
    AppCard {
        Text("Grafik Fungsi Keanggotaan", fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(12.dp))
        (inputGraphs + outputGraph).forEachIndexed { index, graph ->
            FuzzyMembershipChart(graph)
            if (index < inputGraphs.size) {
                Spacer(Modifier.height(18.dp))
            }
        }
    }
}

@Composable
fun FuzzyMembershipChart(spec: FuzzyGraphSpec) {
    val axisColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.65f)
    val gridColor = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.65f)
    val markerColor = MaterialTheme.colorScheme.primary
    Column {
        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
            Text(spec.title, fontWeight = FontWeight.SemiBold, modifier = Modifier.weight(1f))
            Text(
                "${spec.currentLabel}: ${formatGraphValue(spec.currentValue)}",
                color = markerColor,
                fontWeight = FontWeight.Bold,
                fontSize = 12.sp
            )
        }
        Spacer(Modifier.height(8.dp))
        Canvas(
            modifier = Modifier
                .fillMaxWidth()
                .height(170.dp)
                .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.28f))
        ) {
            val left = 34.dp.toPx()
            val right = 10.dp.toPx()
            val top = 12.dp.toPx()
            val bottom = size.height - 28.dp.toPx()
            val width = size.width - left - right
            val height = bottom - top

            fun x(value: Double): Float {
                val range = (spec.xMax - spec.xMin).coerceAtLeast(1.0)
                return (left + ((value - spec.xMin) / range).coerceIn(0.0, 1.0) * width).toFloat()
            }

            fun y(value: Double): Float {
                return (bottom - value.coerceIn(0.0, 1.0) * height).toFloat()
            }

            listOf(0.0, 0.5, 1.0).forEach { level ->
                drawLine(
                    color = gridColor,
                    start = Offset(left, y(level)),
                    end = Offset(size.width - right, y(level)),
                    strokeWidth = 1.dp.toPx()
                )
            }
            drawLine(axisColor, Offset(left, top), Offset(left, bottom), strokeWidth = 1.4.dp.toPx())
            drawLine(axisColor, Offset(left, bottom), Offset(size.width - right, bottom), strokeWidth = 1.4.dp.toPx())

            spec.lines.forEach { line ->
                val color = fuzzyLineColor(line.label)
                line.points.zipWithNext().forEach { (start, end) ->
                    drawLine(
                        color = color,
                        start = Offset(x(start.x), y(start.y)),
                        end = Offset(x(end.x), y(end.y)),
                        strokeWidth = 3.dp.toPx(),
                        cap = StrokeCap.Round
                    )
                }
                line.points.forEach { point ->
                    drawCircle(color, radius = 3.5.dp.toPx(), center = Offset(x(point.x), y(point.y)))
                }
            }

            val markerX = x(spec.currentValue)
            drawLine(
                color = markerColor,
                start = Offset(markerX, top),
                end = Offset(markerX, bottom),
                strokeWidth = 1.6.dp.toPx()
            )
            drawCircle(markerColor, radius = 4.dp.toPx(), center = Offset(markerX, bottom))
        }
        Spacer(Modifier.height(6.dp))
        Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
            spec.xTicks.distinct().forEach { tick ->
                Text(formatGraphValue(tick), fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
        Spacer(Modifier.height(8.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp), modifier = Modifier.fillMaxWidth()) {
            spec.lines.forEach { line ->
                Legend(fuzzyLineColor(line.label), line.label)
            }
        }
    }
}

@Composable
fun PhoneBrandHeader(brand: String, count: Int) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 8.dp, bottom = 2.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(36.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.12f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(Icons.Filled.PhoneAndroid, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
        }
        Spacer(Modifier.width(12.dp))
        Column(Modifier.weight(1f)) {
            Text(brand, fontWeight = FontWeight.ExtraBold, fontSize = 18.sp)
            Text("$count data penjualan", color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 12.sp)
        }
    }
}

@Composable
fun RecordCard(record: SalesRecord, onClick: () -> Unit, showModelOnly: Boolean = false) {
    val brand = phoneBrandName(record.phoneType)
    val title = if (showModelOnly) phoneModelName(record.phoneType, brand) else record.phoneType
    AppCard(modifier = Modifier.clickable(onClick = onClick)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            PhoneImageThumbnail(
                phoneType = record.phoneType,
                contentDescription = title.ifBlank { record.phoneType }
            )
            Spacer(Modifier.width(12.dp))
            Column(Modifier.weight(1f)) {
                Text(
                    title.ifBlank { record.phoneType },
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(record.dateText(), color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 12.sp)
            }
            RiskChip(record.riskLevel)
        }
        Spacer(Modifier.height(10.dp))
        Text("Barang Masuk : ${record.incomingStock} unit", color = MaterialTheme.colorScheme.onSurfaceVariant)
        Text("Penjualan : ${record.sales} unit", color = MaterialTheme.colorScheme.onSurfaceVariant)
        Text("Stok Akhir : ${record.stock} unit", color = MaterialTheme.colorScheme.onSurfaceVariant)
        Text("Permintaan : ${record.demand} unit", color = MaterialTheme.colorScheme.onSurfaceVariant)
        Spacer(Modifier.height(8.dp))
        Text("Lihat Detail", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
    }
}

@Composable
fun ProcessCard(title: String, body: String) {
    AppCard {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(title, fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f))
            Icon(Icons.Filled.CheckCircle, contentDescription = null, tint = Success)
        }
        Spacer(Modifier.height(8.dp))
        Text(body, color = MaterialTheme.colorScheme.onSurfaceVariant, lineHeight = 20.sp)
    }
}

@Composable
fun InsightCard(records: List<SalesRecord>) {
    val latest = records.firstOrNull()
    val message = when {
        latest == null -> "Belum ada insight. Tambahkan data penjualan pertama untuk melihat analisis."
        latest.riskLevel == "Tinggi" -> "Risiko terbaru tinggi. Prioritaskan evaluasi stok, promosi, dan produk yang permintaannya rendah."
        latest.riskLevel == "Sedang" -> "Risiko terbaru sedang. Pantau perubahan permintaan dan jaga stok agar tidak menumpuk."
        else -> "Risiko terbaru rendah. Kondisi aman, tetap pantau tren agar keputusan stok tetap akurat."
    }
    AppCard {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Filled.Analytics, contentDescription = null, tint = MaterialTheme.colorScheme.secondary, modifier = Modifier.size(30.dp))
            Spacer(Modifier.width(12.dp))
            Column {
                Text("Insight Cepat", fontWeight = FontWeight.Bold)
                Text(message, color = MaterialTheme.colorScheme.onSurfaceVariant, lineHeight = 20.sp)
            }
        }
    }
}

@Composable
fun TrendChart(records: List<SalesRecord>) {
    val maxValue = (records.flatMap { listOf(it.sales, it.stock, it.demand) }.maxOrNull() ?: 1).coerceAtLeast(1)
    Canvas(
        modifier = Modifier
            .fillMaxWidth()
            .height(220.dp)
            .background(MaterialTheme.colorScheme.surface)
    ) {
        val left = 36.dp.toPx()
        val top = 12.dp.toPx()
        val bottom = size.height - 28.dp.toPx()
        val width = size.width - left - 12.dp.toPx()
        val height = bottom - top
        repeat(4) { i ->
            val y = top + (height / 3f * i)
            drawLine(Color(0xFFE2E8F0), Offset(left, y), Offset(size.width, y), strokeWidth = 1.dp.toPx())
        }
        fun point(index: Int, value: Int): Offset {
            val x = left + if (records.size <= 1) width / 2f else width * index / (records.size - 1)
            val y = bottom - (value.toFloat() / maxValue.toFloat()) * height
            return Offset(x, y)
        }
        fun drawSeries(values: List<Int>, color: Color) {
            values.zipWithNext().forEachIndexed { index, pair ->
                drawLine(color, point(index, pair.first), point(index + 1, pair.second), strokeWidth = 3.dp.toPx(), cap = StrokeCap.Round)
            }
            values.forEachIndexed { index, value -> drawCircle(color, 4.dp.toPx(), point(index, value)) }
        }
        drawSeries(records.map { it.sales }, ChartBlue)
        drawSeries(records.map { it.stock }, Warning)
        drawSeries(records.map { it.demand }, Success)
    }
    Spacer(Modifier.height(12.dp))
    LegendRow(ChartBlue, "Penjualan", Warning, "Stok Akhir", Success, "Permintaan")
}

@Composable
fun LegendRow(c1: Color, t1: String, c2: Color, t2: String, c3: Color, t3: String) {
    Row(horizontalArrangement = Arrangement.spacedBy(12.dp), modifier = Modifier.fillMaxWidth()) {
        Legend(c1, t1)
        Legend(c2, t2)
        Legend(c3, t3)
    }
}

@Composable
fun Legend(color: Color, text: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(Modifier.size(10.dp).clip(CircleShape).background(color))
        Spacer(Modifier.width(6.dp))
        Text(text, color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 12.sp)
    }
}

private fun fuzzyLineColor(label: String): Color {
    return when (label) {
        "Rendah" -> Success
        "Sedang" -> Warning
        "Tinggi" -> Danger
        else -> ChartBlue
    }
}

private fun formatGraphValue(value: Double): String {
    return if (value % 1.0 == 0.0) value.toInt().toString() else NumberFormat.format(value)
}

@Composable
fun RiskSummaryBox(label: String, count: Int, total: Int, modifier: Modifier) {
    AppCard(modifier = modifier, horizontalAlignment = Alignment.CenterHorizontally) {
        Text(label, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Text("$count", fontWeight = FontWeight.ExtraBold, fontSize = 22.sp, color = riskColor(label))
        Text("${percent(count, total)}%", color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 12.sp)
    }
}

@Composable
fun PhoneRiskOverview(records: List<SalesRecord>) {
    AppCard {
        Text("Keterangan Risiko per Jenis HP", fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(10.dp))
        PhoneRiskLine("Rendah", records)
        PhoneRiskLine("Sedang", records)
        PhoneRiskLine("Tinggi", records)
    }
}

@Composable
fun PhoneRiskLine(level: String, records: List<SalesRecord>) {
    val groups = phoneTypeGroupsForRisk(records, level)
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        verticalAlignment = Alignment.Top
    ) {
        Box(
            modifier = Modifier
                .padding(top = 5.dp)
                .size(10.dp)
                .clip(CircleShape)
                .background(riskColor(level))
        )
        Spacer(Modifier.width(10.dp))
        Column(Modifier.weight(1f)) {
            Text(level, fontWeight = FontWeight.Bold, color = riskColor(level))
            if (groups.isEmpty()) {
                Text(
                    "Belum ada jenis HP dengan risiko ${level.lowercase()}.",
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    lineHeight = 20.sp
                )
            } else {
                Spacer(Modifier.height(6.dp))
                PhoneTypeGroupList(groups = groups, color = riskColor(level))
            }
        }
    }
}

@Composable
fun ThemeSettingsCard(darkTheme: Boolean, onThemeChange: (Boolean) -> Unit) {
    AppCard {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = if (darkTheme) Icons.Filled.DarkMode else Icons.Filled.LightMode,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(32.dp)
            )
            Spacer(Modifier.width(14.dp))
            Column(Modifier.weight(1f)) {
                Text("Tema Tampilan", fontWeight = FontWeight.Bold)
                Text(
                    if (darkTheme) "Mode gelap aktif" else "Mode terang aktif",
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Switch(checked = darkTheme, onCheckedChange = onThemeChange)
        }
    }
}

@Composable
fun MenuCard(icon: ImageVector, title: String, subtitle: String, onClick: () -> Unit) {
    AppCard(modifier = Modifier.clickable(onClick = onClick)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(icon, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(32.dp))
            Spacer(Modifier.width(14.dp))
            Column(Modifier.weight(1f)) {
                Text(title, fontWeight = FontWeight.Bold)
                Text(subtitle, color = MaterialTheme.colorScheme.onSurfaceVariant, maxLines = 1, overflow = TextOverflow.Ellipsis)
            }
        }
    }
}

@Composable
fun CategoryCard(title: String, desc: String, color: Color) {
    AppCard {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Filled.ErrorOutline, contentDescription = null, tint = color, modifier = Modifier.size(42.dp))
            Spacer(Modifier.width(16.dp))
            Column {
                Text(title.uppercase(), fontWeight = FontWeight.ExtraBold, color = color)
                Text(desc, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
    }
}

@Composable
fun CategoryPhoneCard(title: String, level: String, records: List<SalesRecord>) {
    val groups = phoneTypeGroupsForRisk(records, level)
    AppCard {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Filled.PhoneAndroid, contentDescription = null, tint = riskColor(level), modifier = Modifier.size(24.dp))
            Spacer(Modifier.width(10.dp))
            Text(title, fontWeight = FontWeight.Bold, color = riskColor(level))
        }
        Spacer(Modifier.height(10.dp))
        if (groups.isEmpty()) {
            Text(
                "Belum ada data. Input jenis HP dan hitung risiko untuk melihat daftar ini.",
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                lineHeight = 21.sp
            )
        } else {
            PhoneTypeGroupList(groups = groups, color = riskColor(level))
        }
    }
}

@Composable
private fun PhoneTypeGroupList(groups: List<PhoneTypeGroup>, color: Color) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
        groups.forEach { group ->
            PhoneTypeGroupRow(group = group, color = color)
        }
    }
}

@Composable
private fun PhoneTypeGroupRow(group: PhoneTypeGroup, color: Color) {
    Row(verticalAlignment = Alignment.Top, modifier = Modifier.fillMaxWidth()) {
        Box(
            modifier = Modifier
                .size(30.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(color.copy(alpha = 0.12f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(Icons.Filled.PhoneAndroid, contentDescription = null, tint = color, modifier = Modifier.size(18.dp))
        }
        Spacer(Modifier.width(10.dp))
        Column(Modifier.weight(1f)) {
            Text(group.brand, fontWeight = FontWeight.Bold)
            Text(
                group.models.joinToString(", "),
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                lineHeight = 20.sp
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SimpleTopBar(title: String, onBack: () -> Unit) {
    TopAppBar(
        title = { Text(title, fontWeight = FontWeight.Bold) },
        navigationIcon = {
            IconButton(onClick = onBack) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Kembali")
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.surface)
    )
}

@Composable
fun AppCard(
    modifier: Modifier = Modifier,
    horizontalAlignment: Alignment.Horizontal = Alignment.Start,
    content: @Composable ColumnScope.() -> Unit
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = horizontalAlignment,
            content = content
        )
    }
}

@Composable
fun EmptyState(text: String) {
    AppCard(horizontalAlignment = Alignment.CenterHorizontally) {
        Icon(Icons.Filled.Inventory, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.size(44.dp))
        Spacer(Modifier.height(8.dp))
        Text(text, color = MaterialTheme.colorScheme.onSurfaceVariant, textAlign = TextAlign.Center)
    }
}

@Composable
fun RiskChip(label: String) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(6.dp))
            .background(riskColor(label).copy(alpha = 0.12f))
            .padding(horizontal = 10.dp, vertical = 5.dp)
    ) {
        Text(label.uppercase(), color = riskColor(label), fontWeight = FontWeight.Bold, fontSize = 11.sp)
    }
}

@Composable
fun AppLogo(size: Dp) {
    Box(
        modifier = Modifier
            .size(size)
            .clip(RoundedCornerShape(18.dp))
            .background(MaterialTheme.colorScheme.primaryContainer),
        contentAlignment = Alignment.Center
    ) {
        Image(
            painter = painterResource(R.drawable.icon),
            contentDescription = null,
            modifier = Modifier.size(size * 0.82f)
        )
    }
}

@Composable
fun AuthTextField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    icon: ImageVector,
    keyboardType: KeyboardType,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    trailingIcon: @Composable (() -> Unit)? = null
) {
    Column(Modifier.fillMaxWidth()) {
        Text(label, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onBackground)
        Spacer(Modifier.height(6.dp))
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier.fillMaxWidth(),
            placeholder = { Text(placeholder, color = MaterialTheme.colorScheme.onSurfaceVariant) },
            leadingIcon = { Icon(icon, contentDescription = null, tint = MaterialTheme.colorScheme.primary) },
            trailingIcon = trailingIcon,
            keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(keyboardType = keyboardType),
            visualTransformation = visualTransformation,
            singleLine = true,
            shape = RoundedCornerShape(8.dp),
            textStyle = androidx.compose.ui.text.TextStyle(color = MaterialTheme.colorScheme.onSurface),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = MaterialTheme.colorScheme.primary,
                unfocusedBorderColor = MaterialTheme.colorScheme.outline,
                focusedTextColor = MaterialTheme.colorScheme.onSurface,
                unfocusedTextColor = MaterialTheme.colorScheme.onSurface,
                focusedPlaceholderColor = MaterialTheme.colorScheme.onSurfaceVariant,
                unfocusedPlaceholderColor = MaterialTheme.colorScheme.onSurfaceVariant,
                focusedContainerColor = MaterialTheme.colorScheme.surface,
                unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                cursorColor = MaterialTheme.colorScheme.primary
            )
        )
    }
}

@Composable
fun PasswordToggleField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    visible: Boolean,
    onToggleVisible: () -> Unit
) {
    AuthTextField(
        label = label,
        value = value,
        onValueChange = onValueChange,
        placeholder = placeholder,
        icon = Icons.Filled.Lock,
        keyboardType = KeyboardType.Password,
        visualTransformation = if (visible) VisualTransformation.None else PasswordVisualTransformation(),
        trailingIcon = {
            IconButton(onClick = onToggleVisible) {
                Icon(
                    imageVector = if (visible) Icons.Filled.VisibilityOff else Icons.Filled.Visibility,
                    contentDescription = if (visible) "Sembunyikan password" else "Tampilkan password"
                )
            }
        }
    )
}

@Composable
fun AuthPickerField(
    label: String,
    value: String,
    placeholder: String,
    onClick: () -> Unit
) {
    Column(Modifier.fillMaxWidth()) {
        Text(label, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onBackground)
        Spacer(Modifier.height(6.dp))
        OutlinedTextField(
            value = value,
            onValueChange = {},
            modifier = Modifier
                .fillMaxWidth()
                .clickable(onClick = onClick),
            placeholder = { Text(placeholder, color = MaterialTheme.colorScheme.onSurfaceVariant) },
            trailingIcon = {
                IconButton(onClick = onClick) {
                    Icon(Icons.Filled.KeyboardArrowDown, contentDescription = "Pilih peran", tint = MaterialTheme.colorScheme.primary)
                }
            },
            readOnly = true,
            singleLine = true,
            shape = RoundedCornerShape(8.dp),
            textStyle = androidx.compose.ui.text.TextStyle(color = MaterialTheme.colorScheme.onSurface),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = MaterialTheme.colorScheme.primary,
                unfocusedBorderColor = MaterialTheme.colorScheme.outline,
                focusedTextColor = MaterialTheme.colorScheme.onSurface,
                unfocusedTextColor = MaterialTheme.colorScheme.onSurface,
                focusedPlaceholderColor = MaterialTheme.colorScheme.onSurfaceVariant,
                unfocusedPlaceholderColor = MaterialTheme.colorScheme.onSurfaceVariant,
                focusedContainerColor = MaterialTheme.colorScheme.surface,
                unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                cursorColor = MaterialTheme.colorScheme.primary
            )
        )
    }
}

@Composable
fun AuthScaffold(content: @Composable ColumnScope.() -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(horizontal = 24.dp)
    ) {
        androidx.compose.foundation.lazy.LazyColumn(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(horizontal = 22.dp, vertical = 34.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        content = content
                    )
                }
            }
        }
    }
}

@Composable
fun RegisterAuthScaffold(content: @Composable ColumnScope.() -> Unit) {
    androidx.compose.foundation.lazy.LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(horizontal = 28.dp),
        verticalArrangement = Arrangement.spacedBy(0.dp)
    ) {
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 48.dp, bottom = 40.dp),
                horizontalAlignment = Alignment.Start,
                content = content
            )
        }
    }
}
