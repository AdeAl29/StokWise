package com.ade.fuzzyrisk.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Assessment
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.PhoneAndroid
import androidx.compose.material.icons.filled.Recommend
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.automirrored.filled.HelpOutline
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material.icons.filled.Analytics
import androidx.compose.material.icons.outlined.Inventory2
import androidx.compose.material.icons.outlined.LocalMall
import androidx.compose.material.icons.outlined.People
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.ade.fuzzyrisk.data.SalesRecord
import com.ade.fuzzyrisk.domain.FuzzyResult
import com.ade.fuzzyrisk.ui.buildPhoneType
import com.ade.fuzzyrisk.ui.BottomItem
import com.ade.fuzzyrisk.ui.Danger
import com.ade.fuzzyrisk.ui.IndonesianPhoneBrands
import com.ade.fuzzyrisk.ui.NumberFormat
import com.ade.fuzzyrisk.ui.Success
import com.ade.fuzzyrisk.ui.Warning
import com.ade.fuzzyrisk.ui.dateText
import com.ade.fuzzyrisk.ui.phoneBrandName
import com.ade.fuzzyrisk.ui.phoneRecordGroups
import com.ade.fuzzyrisk.ui.phoneModelsForBrand
import com.ade.fuzzyrisk.ui.riskColor
import com.ade.fuzzyrisk.ui.riskFromZ
import com.ade.fuzzyrisk.ui.components.AppCard
import com.ade.fuzzyrisk.ui.components.CategoryCard
import com.ade.fuzzyrisk.ui.components.CategoryPhoneCard
import com.ade.fuzzyrisk.ui.components.DashboardMetrics
import com.ade.fuzzyrisk.ui.components.EmptyState
import com.ade.fuzzyrisk.ui.components.FuzzyGraphSection
import com.ade.fuzzyrisk.ui.components.InsightCard
import com.ade.fuzzyrisk.ui.components.MenuCard
import com.ade.fuzzyrisk.ui.components.NumberField
import com.ade.fuzzyrisk.ui.components.PhoneBrandDropdownField
import com.ade.fuzzyrisk.ui.components.PhoneBrandHeader
import com.ade.fuzzyrisk.ui.components.PhoneImageThumbnail
import com.ade.fuzzyrisk.ui.components.PhoneModelDropdownField
import com.ade.fuzzyrisk.ui.components.PhoneRiskOverview
import com.ade.fuzzyrisk.ui.components.ProcessCard
import com.ade.fuzzyrisk.ui.components.RecordCard
import com.ade.fuzzyrisk.ui.components.ResultCard
import com.ade.fuzzyrisk.ui.components.RiskHeroCard
import com.ade.fuzzyrisk.ui.components.RiskSummaryBox
import com.ade.fuzzyrisk.ui.components.SelectedPhoneCard
import com.ade.fuzzyrisk.ui.components.SimpleTopBar
import com.ade.fuzzyrisk.ui.components.SummaryCard
import com.ade.fuzzyrisk.ui.components.SummaryRow
import com.ade.fuzzyrisk.ui.components.ThemeSettingsCard
import com.ade.fuzzyrisk.ui.components.ThemeToggleButton
import com.ade.fuzzyrisk.ui.components.TrendChart

@Composable
fun MainScaffold(
    navController: NavHostController,
    currentRoute: String,
    content: @Composable () -> Unit
) {
    Scaffold(
        bottomBar = { BottomNav(navController, currentRoute) },
        containerColor = MaterialTheme.colorScheme.background
    ) { padding ->
        Box(Modifier.padding(padding)) { content() }
    }
}

@Composable
fun BottomNav(navController: NavHostController, currentRoute: String) {
    val items = listOf(
        BottomItem("dashboard", "Beranda", Icons.Filled.Dashboard),
        BottomItem("data", "Data", Icons.Filled.Assessment),
        BottomItem("stats", "Riwayat", Icons.Filled.Analytics),
        BottomItem("info", "Info", Icons.Filled.Info)
    )
    NavigationBar(containerColor = MaterialTheme.colorScheme.surface) {
        items.forEach { item ->
            NavigationBarItem(
                selected = currentRoute == item.route,
                onClick = {
                    navController.navigate(item.route) {
                        launchSingleTop = true
                        popUpTo("dashboard") { saveState = true }
                        restoreState = true
                    }
                },
                icon = { Icon(item.icon, contentDescription = item.label) },
                label = { Text(item.label) }
            )
        }
    }
}

@Composable
fun DashboardScreen(
    records: List<SalesRecord>,
    darkTheme: Boolean,
    onThemeChange: (Boolean) -> Unit,
    onInput: () -> Unit
) {
    val latest = records.firstOrNull()
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Column(Modifier.weight(1f)) {
                    Text("Beranda", fontSize = 24.sp, fontWeight = FontWeight.Bold)
                    Text("Pantau risiko penjualan hari ini", color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
                ThemeToggleButton(darkTheme, onThemeChange)
                IconButton(onClick = onInput) {
                    Icon(Icons.Filled.Add, contentDescription = "Tambah data", tint = MaterialTheme.colorScheme.primary)
                }
            }
        }
        item { RiskHeroCard(latest) }
        item { DashboardMetrics(records) }
        item { SummaryCard(latest) }
        item {
            Button(
                onClick = onInput,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
            ) {
                Icon(Icons.Filled.Add, contentDescription = null)
                Spacer(Modifier.width(8.dp))
                Text("Input Data Baru")
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InputScreen(
    records: List<SalesRecord>,
    onBack: () -> Unit,
    onCalculate: (Int, Int, Int) -> FuzzyResult,
    onSave: (String, Int, Int, Int, Int, FuzzyResult) -> Unit
) {
    var phoneBrand by rememberSaveable { mutableStateOf("") }
    var phoneModel by rememberSaveable { mutableStateOf("") }
    var sales by rememberSaveable { mutableStateOf("") }
    var incomingStock by rememberSaveable { mutableStateOf("") }
    var demand by rememberSaveable { mutableStateOf("") }
    var result by remember { mutableStateOf<FuzzyResult?>(null) }
    var calculatedSales by rememberSaveable { mutableStateOf(0) }
    var calculatedStock by rememberSaveable { mutableStateOf(0) }
    var calculatedDemand by rememberSaveable { mutableStateOf(0) }
    var error by rememberSaveable { mutableStateOf("") }
    val phoneModelOptions = remember(phoneBrand) { phoneModelsForBrand(phoneBrand) }
    val selectedPhoneType = remember(phoneBrand, phoneModel) { buildPhoneType(phoneBrand, phoneModel) }
    val previousStock = remember(records, selectedPhoneType) {
        if (selectedPhoneType.isBlank()) return@remember 0
        records
            .firstOrNull { it.phoneType.equals(selectedPhoneType, ignoreCase = true) }
            ?.stock ?: 0
    }
    val previewSales = sales.toIntOrNull() ?: 0
    val previewIncomingStock = incomingStock.toIntOrNull() ?: 0
    val previewFinalStock = previousStock + previewIncomingStock - previewSales

    Scaffold(
        topBar = { SimpleTopBar("Input Data Baru", onBack) },
        containerColor = MaterialTheme.colorScheme.background
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            item {
                Text("Pilih merek dan tipe HP dari daftar 2025/2026, lalu masukkan data penjualan untuk menghitung tingkat risikonya.", color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            item {
                PhoneBrandDropdownField(
                    label = "Merek HP",
                    placeholder = "Pilih merek HP",
                    brands = IndonesianPhoneBrands,
                    selectedBrand = phoneBrand,
                    onBrandSelected = {
                        phoneBrand = it
                        phoneModel = ""
                    }
                )
            }
            item {
                PhoneModelDropdownField(
                    label = "Tipe / Model HP",
                    placeholder = if (phoneBrand.isBlank()) "Pilih merek HP dulu" else "Pilih tipe/model HP",
                    models = phoneModelOptions,
                    selectedModel = phoneModel,
                    enabled = phoneBrand.isNotBlank(),
                    onModelSelected = { phoneModel = it }
                )
            }
            if (phoneModel.isNotBlank()) {
                item { SelectedPhoneCard(selectedPhoneType) }
            }
            item { NumberField("Barang Masuk (unit)", "Masukkan barang masuk", incomingStock) { incomingStock = it } }
            item { NumberField("Jumlah Penjualan (unit)", "Masukkan jumlah penjualan", sales) { sales = it } }
            item { NumberField("Jumlah Permintaan (unit)", "Masukkan jumlah permintaan", demand) { demand = it } }
            if (phoneModel.isNotBlank()) {
                item {
                    AppCard {
                        Text("Stok Otomatis", fontWeight = FontWeight.Bold)
                        Spacer(Modifier.height(10.dp))
                        SummaryRow(Icons.Outlined.Inventory2, "Stok sebelumnya", "$previousStock unit")
                        SummaryRow(Icons.Filled.Add, "Barang masuk", "$previewIncomingStock unit")
                        SummaryRow(Icons.Outlined.LocalMall, "Penjualan", "$previewSales unit")
                        SummaryRow(
                            Icons.Outlined.Inventory2,
                            "Stok akhir",
                            "$previewFinalStock unit"
                        )
                    }
                }
            }
            if (error.isNotBlank()) {
                item { Text(error, color = Danger, fontWeight = FontWeight.SemiBold) }
            }
            item {
                Button(
                    onClick = {
                        val parsedSales = sales.toIntOrNull()
                        val parsedIncomingStock = incomingStock.toIntOrNull()
                        val parsedDemand = demand.toIntOrNull()
                        val normalizedBrand = phoneBrand.trim()
                        val normalizedModel = phoneModel.trim()
                        val normalizedPhoneType = buildPhoneType(normalizedBrand, normalizedModel)
                        if (normalizedBrand.isBlank()) {
                            error = "Merek HP wajib dipilih."
                            return@Button
                        }
                        if (normalizedModel.isBlank()) {
                            error = "Tipe HP wajib diisi."
                            return@Button
                        }
                        if (parsedSales == null || parsedIncomingStock == null || parsedDemand == null) {
                            error = "Barang masuk, penjualan, dan permintaan harus berupa angka."
                            return@Button
                        }
                        if (parsedSales < 0 || parsedIncomingStock < 0 || parsedDemand < 0) {
                            error = "Barang masuk, penjualan, dan permintaan tidak boleh bernilai negatif."
                            return@Button
                        }
                        val finalStock = previousStock + parsedIncomingStock - parsedSales
                        if (finalStock < 0) {
                            error = "Stok akhir tidak boleh negatif. Tambahkan barang masuk atau kurangi penjualan."
                            return@Button
                        }
                        val calculated = onCalculate(parsedSales, finalStock, parsedDemand)
                        calculatedSales = parsedSales
                        calculatedStock = finalStock
                        calculatedDemand = parsedDemand
                        result = calculated
                        onSave(normalizedPhoneType, parsedSales, parsedIncomingStock, finalStock, parsedDemand, calculated)
                        error = ""
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp)
                ) {
                    Text("Proses Hitung")
                }
            }
            result?.let {
                item { ResultCard(selectedPhoneType, it) }
                item {
                    FuzzyGraphSection(
                        sales = calculatedSales,
                        stock = calculatedStock,
                        demand = calculatedDemand,
                        zValue = it.zValue
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DataScreen(records: List<SalesRecord>, onDetail: (Long) -> Unit) {
    var query by rememberSaveable { mutableStateOf("") }
    var selectedBrand by rememberSaveable { mutableStateOf<String?>(null) }
    val brandFilters = remember(records) {
        phoneRecordGroups(records).map { it.brand }
    }
    val activeBrand = selectedBrand.takeIf { brand -> brandFilters.any { it == brand } }
    val filteredRecords = remember(records, query, activeBrand) {
        val normalizedQuery = query.trim()
        records.filter { record ->
            val matchesBrand = activeBrand == null ||
                phoneBrandName(record.phoneType).equals(activeBrand, ignoreCase = true)
            val matchesQuery = normalizedQuery.isBlank() ||
                record.riskLevel.contains(normalizedQuery, ignoreCase = true) ||
                    record.phoneType.contains(normalizedQuery, ignoreCase = true) ||
                    record.dateText().contains(normalizedQuery, ignoreCase = true) ||
                    record.sales.toString().contains(normalizedQuery) ||
                    record.incomingStock.toString().contains(normalizedQuery) ||
                    record.stock.toString().contains(normalizedQuery) ||
                    record.demand.toString().contains(normalizedQuery)
            matchesBrand && matchesQuery
        }
    }
    val groupedRecords = remember(filteredRecords) {
        phoneRecordGroups(filteredRecords)
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("Data Penjualan", fontSize = 24.sp, fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f))
                Icon(Icons.Filled.Search, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
        item {
            OutlinedTextField(
                value = query,
                onValueChange = { query = it },
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("Cari jenis HP, tanggal, risiko, atau angka") },
                leadingIcon = { Icon(Icons.Filled.Search, contentDescription = null) },
                singleLine = true
            )
        }
        if (brandFilters.isNotEmpty()) {
            item {
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    item {
                        FilterChip(
                            selected = activeBrand == null,
                            onClick = { selectedBrand = null },
                            label = { Text("All") }
                        )
                    }
                    items(brandFilters, key = { it }) { brand ->
                        FilterChip(
                            selected = activeBrand == brand,
                            onClick = { selectedBrand = brand },
                            label = { Text(brand) }
                        )
                    }
                }
            }
        }
        if (records.isEmpty()) {
            item { EmptyState("Data penjualan akan tampil setelah proses hitung pertama.") }
        }
        if (records.isNotEmpty() && filteredRecords.isEmpty()) {
            item { EmptyState("Data tidak ditemukan untuk pencarian ini.") }
        }
        groupedRecords.forEach { group ->
            item(key = "brand-${group.brand}") {
                PhoneBrandHeader(group.brand, group.records.size)
            }
            items(group.records, key = { it.id }) { record ->
                RecordCard(record, onClick = { onDetail(record.id) }, showModelOnly = true)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailScreen(record: SalesRecord?, onBack: () -> Unit) {
    Scaffold(
        topBar = { SimpleTopBar("Detail Perhitungan", onBack) },
        containerColor = MaterialTheme.colorScheme.background
    ) { padding ->
        if (record == null) {
            Box(Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                Text("Data tidak ditemukan", color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            return@Scaffold
        }
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            item {
                AppCard {
                    Text("Data Input", fontWeight = FontWeight.Bold)
                    Spacer(Modifier.height(10.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        PhoneImageThumbnail(
                            phoneType = record.phoneType,
                            contentDescription = record.phoneType,
                            modifier = Modifier.size(58.dp),
                            iconSize = 30.dp
                        )
                        Spacer(Modifier.width(12.dp))
                        Column(Modifier.weight(1f)) {
                            Text("Jenis HP", fontWeight = FontWeight.SemiBold)
                            Text(record.phoneType, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                    Spacer(Modifier.height(10.dp))
                    SummaryRow(Icons.Filled.Add, "Barang Masuk", "${record.incomingStock} unit")
                    SummaryRow(Icons.Outlined.LocalMall, "Penjualan", "${record.sales} unit")
                    SummaryRow(Icons.Outlined.Inventory2, "Stok Akhir", "${record.stock} unit")
                    SummaryRow(Icons.Outlined.People, "Permintaan", "${record.demand} unit")
                }
            }
            item {
                FuzzyGraphSection(
                    sales = record.sales,
                    stock = record.stock,
                    demand = record.demand,
                    zValue = record.zValue
                )
            }
            item { ProcessCard("1. Fuzzyfikasi", record.fuzzification) }
            item { ProcessCard("2. Inferensi Aturan", record.inference) }
            item { ProcessCard("3. Defuzzyfikasi", record.defuzzification) }
            item {
                AppCard(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("Hasil Akhir", fontWeight = FontWeight.Bold)
                    Text("Tingkat Risiko", color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text(record.riskLevel.uppercase(), fontSize = 28.sp, fontWeight = FontWeight.ExtraBold, color = riskColor(record.riskLevel))
                    Text("Nilai Akhir (Z)", color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text(NumberFormat.format(record.zValue), fontSize = 22.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
fun StatisticScreen(records: List<SalesRecord>) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            Text("Riwayat / Statistik", fontSize = 24.sp, fontWeight = FontWeight.Bold)
            Text("Tren penjualan, stok akhir, dan permintaan", color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
        item { InsightCard(records) }
        item {
            AppCard {
                Text("Grafik Tren", fontWeight = FontWeight.Bold)
                Spacer(Modifier.height(16.dp))
                TrendChart(records.take(10).reversed())
            }
        }
        item { PhoneRiskOverview(records) }
        item {
            val low = records.count { it.riskLevel == "Rendah" }
            val medium = records.count { it.riskLevel == "Sedang" }
            val high = records.count { it.riskLevel == "Tinggi" }
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                RiskSummaryBox("Rendah", low, records.size, Modifier.weight(1f))
                RiskSummaryBox("Sedang", medium, records.size, Modifier.weight(1f))
                RiskSummaryBox("Tinggi", high, records.size, Modifier.weight(1f))
            }
        }
        item {
            val average = records.map { it.zValue }.average().takeIf { !it.isNaN() } ?: 0.0
            AppCard(horizontalAlignment = Alignment.CenterHorizontally) {
                Text("Rata-rata Risiko", color = MaterialTheme.colorScheme.onSurfaceVariant)
                Text(riskFromZ(average).uppercase(), fontSize = 24.sp, fontWeight = FontWeight.ExtraBold, color = riskColor(riskFromZ(average)))
                Text("Z ${NumberFormat.format(average)}", color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
    }
}

@Composable
fun InfoHubScreen(
    navController: NavHostController,
    latest: SalesRecord?,
    darkTheme: Boolean,
    onLogout: () -> Unit,
    onThemeChange: (Boolean) -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Text("Info", fontSize = 24.sp, fontWeight = FontWeight.Bold)
            Text("Panduan, kategori risiko, dan rekomendasi", color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
        item { ThemeSettingsCard(darkTheme, onThemeChange) }
        item { MenuCard(Icons.Filled.Category, "Kategori Risiko", "Rendah, sedang, dan tinggi") { navController.navigate("category") } }
        item { MenuCard(Icons.Filled.Recommend, "Rekomendasi", latest?.riskLevel ?: "Berdasarkan hasil terbaru") { navController.navigate("recommendation") } }
        item { MenuCard(Icons.Filled.Info, "Tentang Aplikasi", "FuzzyRisk versi 1.0.0") { navController.navigate("about") } }
        item { MenuCard(Icons.AutoMirrored.Filled.HelpOutline, "Bantuan", "Pertanyaan umum aplikasi") { navController.navigate("help") } }
        item { MenuCard(Icons.Filled.Info, "Keluar Akun", "Hapus sesi login pada perangkat ini") { onLogout() } }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategoryScreen(records: List<SalesRecord>, onBack: () -> Unit) {
    Scaffold(topBar = { SimpleTopBar("Kategori Risiko", onBack) }, containerColor = MaterialTheme.colorScheme.background) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            item { CategoryCard("Rendah", "Kondisi penjualan aman. Stok dan permintaan seimbang.", Success) }
            item { CategoryPhoneCard("HP Risiko Rendah", "Rendah", records) }
            item { CategoryCard("Sedang", "Kondisi penjualan cukup. Perlu perhatian pada stok dan permintaan.", Warning) }
            item { CategoryPhoneCard("HP Risiko Sedang", "Sedang", records) }
            item { CategoryCard("Tinggi", "Risiko tinggi. Segera lakukan tindakan untuk menghindari kerugian.", Danger) }
            item { CategoryPhoneCard("HP Risiko Tinggi", "Tinggi", records) }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecommendationScreen(latest: SalesRecord?, onBack: () -> Unit) {
    val level = latest?.riskLevel ?: "Sedang"
    val recommendations = when (level) {
        "Rendah" -> listOf("Pertahankan pola stok saat ini.", "Pantau permintaan agar tetap stabil.", "Jaga kualitas promosi yang sudah berjalan.")
        "Tinggi" -> listOf("Kurangi stok produk yang lambat terjual.", "Tingkatkan promosi untuk mempercepat penjualan.", "Evaluasi harga dan produk dengan permintaan rendah.")
        else -> listOf("Evaluasi stok agar tidak menumpuk.", "Pantau perubahan permintaan mingguan.", "Tingkatkan promosi untuk menjaga stabilitas penjualan.")
    }
    Scaffold(topBar = { SimpleTopBar("Rekomendasi", onBack) }, containerColor = MaterialTheme.colorScheme.background) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            item {
                AppCard {
                    Text("Status Risiko Saat Ini", fontWeight = FontWeight.Bold)
                    Text(level.uppercase(), fontSize = 28.sp, fontWeight = FontWeight.ExtraBold, color = riskColor(level))
                }
            }
            items(recommendations) { recommendation ->
                AppCard {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.AutoMirrored.Filled.TrendingUp, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                        Spacer(Modifier.width(14.dp))
                        Text(recommendation, color = MaterialTheme.colorScheme.onSurface, fontWeight = FontWeight.SemiBold)
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AboutScreen(onBack: () -> Unit) {
    Scaffold(topBar = { SimpleTopBar("Tentang Aplikasi", onBack) }, containerColor = MaterialTheme.colorScheme.background) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(28.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            com.ade.fuzzyrisk.ui.components.AppLogo(96.dp)
            Spacer(Modifier.height(20.dp))
            Text("FuzzyRisk", fontSize = 28.sp, fontWeight = FontWeight.ExtraBold)
            Text("Klasifikasi Risiko Penjualan Handphone", textAlign = TextAlign.Center, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Text("Berbasis Fuzzy Tsukamoto", color = MaterialTheme.colorScheme.onSurfaceVariant)
            Spacer(Modifier.height(18.dp))
            Text("Versi 1.0.0", fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(30.dp))
            Text(
                "Aplikasi ini digunakan untuk mengklasifikasikan tingkat risiko penjualan handphone berdasarkan data penjualan, stok, dan permintaan secara offline.",
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                lineHeight = 22.sp
            )
            Spacer(Modifier.height(40.dp))
            Text("(c) 2026 FuzzyRisk", color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HelpScreen(onBack: () -> Unit) {
    val questions = listOf(
        "Apa itu tingkat risiko penjualan?" to "Klasifikasi kondisi penjualan berdasarkan keseimbangan penjualan, stok, dan permintaan.",
        "Bagaimana cara input data?" to "Buka Input Data Baru, isi jenis HP dan tiga angka, lalu tekan Proses Hitung.",
        "Bagaimana hasil perhitungan diperoleh?" to "Sistem memakai fuzzyfikasi, inferensi aturan IF-THEN, dan defuzzyfikasi weighted average.",
        "Apa itu Fuzzy Tsukamoto?" to "Metode fuzzy dengan konsekuen monoton dan hasil akhir berupa nilai crisp Z."
    )
    Scaffold(topBar = { SimpleTopBar("Bantuan", onBack) }, containerColor = MaterialTheme.colorScheme.background) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            items(questions) { item ->
                AppCard {
                    Text(item.first, fontWeight = FontWeight.Bold)
                    Spacer(Modifier.height(6.dp))
                    Text(item.second, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
        }
    }
}
