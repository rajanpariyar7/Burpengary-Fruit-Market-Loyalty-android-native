package com.example

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.example.ui.viewmodel.LoyaltyViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminDashboard(viewModel: LoyaltyViewModel) {
    var selectedTab by remember { mutableStateOf(0) }
    val currentUser by viewModel.currentUser.collectAsStateWithLifecycle()
    
    Scaffold(
        bottomBar = {
            NavigationBar(
                containerColor = Color.White,
                contentColor = com.example.ui.theme.DarkGreen
            ) {
                NavigationBarItem(
                    icon = { Icon(Icons.Default.Dashboard, contentDescription = "Dashboard") },
                    label = { Text("Stats", fontSize = 10.sp) },
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = com.example.ui.theme.DarkGreen,
                        selectedTextColor = com.example.ui.theme.DarkGreen,
                        indicatorColor = com.example.ui.theme.LightGreenCard
                    )
                )
                NavigationBarItem(
                    icon = { Icon(Icons.Default.ShoppingBag, contentDescription = "Offers") },
                    label = { Text("Offers", fontSize = 10.sp) },
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = com.example.ui.theme.DarkGreen,
                        selectedTextColor = com.example.ui.theme.DarkGreen,
                        indicatorColor = com.example.ui.theme.LightGreenCard
                    )
                )
                NavigationBarItem(
                    icon = { Icon(Icons.Default.ReceiptLong, contentDescription = "Transactions") },
                    label = { Text("Audit", fontSize = 10.sp) },
                    selected = selectedTab == 2,
                    onClick = { selectedTab = 2 },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = com.example.ui.theme.DarkGreen,
                        selectedTextColor = com.example.ui.theme.DarkGreen,
                        indicatorColor = com.example.ui.theme.LightGreenCard
                    )
                )
                NavigationBarItem(
                    icon = { Icon(Icons.Default.Group, contentDescription = "Customers") },
                    label = { Text("Customers", fontSize = 10.sp) },
                    selected = selectedTab == 3,
                    onClick = { selectedTab = 3 },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = com.example.ui.theme.DarkGreen,
                        selectedTextColor = com.example.ui.theme.DarkGreen,
                        indicatorColor = com.example.ui.theme.LightGreenCard
                    )
                )
                NavigationBarItem(
                    icon = { Icon(Icons.Default.Settings, contentDescription = "Settings") },
                    label = { Text("Settings", fontSize = 10.sp) },
                    selected = selectedTab == 4,
                    onClick = { selectedTab = 4 },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = com.example.ui.theme.DarkGreen,
                        selectedTextColor = com.example.ui.theme.DarkGreen,
                        indicatorColor = com.example.ui.theme.LightGreenCard
                    )
                )
            }
        }
    ) { padding ->
        Box(modifier = Modifier.padding(padding).fillMaxSize().background(Color(0xFFFAFAFA))) {
            when (selectedTab) {
                0 -> AdminStatsDashboardTab(viewModel)
                1 -> AdminOffersManagerTab(viewModel)
                2 -> AdminTransactionsTab(viewModel)
                3 -> AdminCustomersTab(viewModel)
                4 -> AdminSettingsTab(viewModel, currentUser?.name ?: "Admin")
            }
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminCatalogTab(viewModel: LoyaltyViewModel) {
    val offers by viewModel.offers.collectAsStateWithLifecycle()
    val dbCategories by viewModel.categories.collectAsStateWithLifecycle()
    var selectedCategory by remember { mutableStateOf("ALL") }
    
    val categories = listOf("ALL") + dbCategories.map { it.name.uppercase() }.distinct()
    
    Column(modifier = Modifier.fillMaxSize()) {
        
        androidx.compose.foundation.lazy.LazyRow(
            contentPadding = PaddingValues(horizontal = 20.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(categories) { category ->
                val isSelected = selectedCategory == category
                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = if (isSelected) com.example.ui.theme.ExtraDarkGreen else Color.White,
                    border = androidx.compose.foundation.BorderStroke(1.dp, if (isSelected) Color.Transparent else com.example.ui.theme.BorderSlate),
                    modifier = Modifier.clickable { selectedCategory = category }
                ) {
                    Text(
                        text = category,
                        color = if (isSelected) Color.White else Color.DarkGray,
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp,
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                    )
                }
            }
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        val filteredOffers = if (selectedCategory == "ALL") offers else offers.filter { it.category.equals(selectedCategory, ignoreCase = true) }
        
        LazyColumn(
            contentPadding = PaddingValues(horizontal = 20.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            val chunkedOffers = filteredOffers.chunked(2)
            items(chunkedOffers) { rowOffers ->
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    for (offer in rowOffers) {
                        Card(
                            modifier = Modifier.weight(1f).height(260.dp),
                            colors = CardDefaults.cardColors(containerColor = Color.White),
                            border = androidx.compose.foundation.BorderStroke(1.dp, com.example.ui.theme.BorderSlate),
                            shape = RoundedCornerShape(16.dp)
                        ) {
                            Box(modifier = Modifier.fillMaxSize()) {
                                Column {
                                    if (offer.imageUrl != null) {
                                        AsyncImage(
                                            model = offer.imageUrl,
                                            contentDescription = offer.title,
                                            modifier = Modifier.fillMaxWidth().height(140.dp).background(Color.LightGray),
                                            contentScale = ContentScale.Crop
                                        )
                                    } else {
                                        Box(modifier = Modifier.fillMaxWidth().height(140.dp).background(Color(0xFFF0F0F0)), contentAlignment = Alignment.Center) {
                                            Icon(Icons.Default.ImageNotSupported, contentDescription = "No Image", tint = Color.LightGray, modifier = Modifier.size(48.dp))
                                        }
                                    }
                                    Column(modifier = Modifier.padding(12.dp)) {
                                        Text(offer.title, fontWeight = FontWeight.Bold, maxLines = 2, fontSize = 14.sp)
                                        Spacer(modifier = Modifier.height(8.dp))
                                        Text(offer.price, color = com.example.ui.theme.MediumGreen, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                            Text("/ ea", color = Color.Gray, fontSize = 12.sp)
                                            Surface(color = com.example.ui.theme.BackgroundLight, shape = RoundedCornerShape(4.dp)) {
                                                Text("In Stock", fontSize = 10.sp, color = Color.DarkGray, modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp))
                                            }
                                        }
                                    }
                                }
                                IconButton(
                                    onClick = { viewModel.deleteOffer(offer) },
                                    modifier = Modifier.align(Alignment.TopEnd).padding(4.dp).background(Color.White.copy(alpha=0.7f), androidx.compose.foundation.shape.CircleShape).size(32.dp)
                                ) {
                                    Icon(Icons.Default.Delete, contentDescription = "Delete", tint = Color.Red, modifier = Modifier.size(16.dp))
                                }
                            }
                        }
                    }
                    if (rowOffers.size == 1) {
                        Spacer(modifier = Modifier.weight(1f))
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminManageTab(viewModel: LoyaltyViewModel) {
    val dbCategories by viewModel.categories.collectAsStateWithLifecycle()
    
    var offerTitle by remember { mutableStateOf("") }
    var offerCategory by remember { mutableStateOf("") }
    var offerPrice by remember { mutableStateOf("") }
    var offerDesc by remember { mutableStateOf("") }
    var isSpecialOffer by remember { mutableStateOf(false) }
    var imageUri by remember { mutableStateOf<Uri?>(null) }
    
    var newCategoryName by remember { mutableStateOf("") }
    
    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        imageUri = uri
    }
    
    var expandedCategory by remember { mutableStateOf(false) }

    LazyColumn(modifier = Modifier.fillMaxSize().padding(horizontal = 20.dp, vertical = 16.dp)) {
        item {
            Text("Manage Categories", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold, color = com.example.ui.theme.ExtraDarkGreen, modifier = Modifier.padding(bottom = 16.dp))
        }
        item {
            Card(
                modifier = Modifier.fillMaxWidth().padding(bottom = 24.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                border = androidx.compose.foundation.BorderStroke(1.dp, com.example.ui.theme.BorderSlate)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        OutlinedTextField(
                            value = newCategoryName, 
                            onValueChange = { newCategoryName = it }, 
                            label = { Text("New Category") },
                            modifier = Modifier.weight(1f)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Button(
                            onClick = { 
                                if (newCategoryName.isNotBlank()) {
                                    viewModel.addCategory(newCategoryName.uppercase())
                                    newCategoryName = ""
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = com.example.ui.theme.PrimaryGreen)
                        ) {
                            Text("Add")
                        }
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    Text("Existing Categories", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    Spacer(modifier = Modifier.height(8.dp))
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        for (cat in dbCategories) {
                            Row(
                                modifier = Modifier.fillMaxWidth().background(Color(0xFFFAFAFA), RoundedCornerShape(8.dp)).padding(8.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(cat.name, fontWeight = FontWeight.Bold, color = com.example.ui.theme.DarkGreen)
                                IconButton(onClick = { viewModel.deleteCategory(cat) }) {
                                    Icon(Icons.Default.Delete, contentDescription = "Delete", tint = Color.Red)
                                }
                            }
                        }
                        if (dbCategories.isEmpty()) {
                            Text("No categories yet.", color = Color.Gray, fontSize = 12.sp)
                        }
                    }
                }
            }
        }
        item {
            Card(
                modifier = Modifier.fillMaxWidth().padding(bottom = 24.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                border = androidx.compose.foundation.BorderStroke(1.dp, com.example.ui.theme.BorderSlate)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    OutlinedTextField(value = offerTitle, onValueChange = { offerTitle = it }, label = { Text("Product Title") }, modifier = Modifier.fillMaxWidth())
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    ExposedDropdownMenuBox(
                        expanded = expandedCategory,
                        onExpandedChange = { expandedCategory = !expandedCategory }
                    ) {
                        OutlinedTextField(
                            value = offerCategory,
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Category") },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedCategory) },
                            colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors(),
                            modifier = Modifier.menuAnchor().fillMaxWidth()
                        )
                        ExposedDropdownMenu(
                            expanded = expandedCategory,
                            onDismissRequest = { expandedCategory = false }
                        ) {
                            dbCategories.forEach { cat ->
                                DropdownMenuItem(
                                    text = { Text(cat.name) },
                                    onClick = {
                                        offerCategory = cat.name
                                        expandedCategory = false
                                    }
                                )
                            }
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(value = offerPrice, onValueChange = { offerPrice = it }, label = { Text("Price (e.g. $2.99)") }, modifier = Modifier.fillMaxWidth())
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(value = offerDesc, onValueChange = { offerDesc = it }, label = { Text("Description") }, modifier = Modifier.fillMaxWidth(), minLines = 3)
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    if (imageUri != null) {
                        AsyncImage(model = imageUri, contentDescription = "Selected Image", modifier = Modifier.fillMaxWidth().height(150.dp).background(Color.LightGray), contentScale = ContentScale.Crop)
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                    
                    Button(
                        onClick = { photoPickerLauncher.launch("image/*") },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(containerColor = Color.Gray)
                    ) {
                        Icon(Icons.Default.Image, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(if (imageUri == null) "Select Image" else "Change Image")
                    }
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Checkbox(checked = isSpecialOffer, onCheckedChange = { isSpecialOffer = it })
                        Text("Mark as Special Bonus Offer")
                    }
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    Button(
                        onClick = {
                            val desc = if (isSpecialOffer) "OFFER: $offerDesc" else offerDesc
                            viewModel.addOffer(offerTitle, offerPrice, desc, offerCategory.ifBlank { "General" }, imageUri?.toString())
                            offerTitle = ""
                            offerCategory = ""
                            offerPrice = ""
                            offerDesc = ""
                            imageUri = null
                            isSpecialOffer = false
                        },
                        modifier = Modifier.fillMaxWidth().height(50.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = com.example.ui.theme.PrimaryGreen)
                    ) {
                        Text("Publish Product")
                    }
                }
            }
        }
    }
}

@Composable
fun AdminSettingsTab(viewModel: LoyaltyViewModel, adminName: String) {
    val pointSettings by viewModel.pointSettings.collectAsStateWithLifecycle()
    
    var pointsPerDollar by remember(pointSettings) { mutableStateOf(pointSettings?.pointsPerDollar?.toString() ?: "10") }
    var redemptionThreshold by remember(pointSettings) { mutableStateOf(pointSettings?.redemptionThreshold?.toString() ?: "100") }
    var discountPer100 by remember(pointSettings) { mutableStateOf(pointSettings?.discountPer100Points?.toString() ?: "1.0") }
    
    val isWriteEnabled = pointSettings?.adminWriteEnabled ?: false

    LazyColumn(
        modifier = Modifier.fillMaxSize().background(Color(0xFFFAFAFA)),
        contentPadding = PaddingValues(20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                Surface(
                    shape = RoundedCornerShape(50),
                    color = com.example.ui.theme.PrimaryGreen,
                    modifier = Modifier.size(64.dp)
                ) {
                    Icon(Icons.Default.Person, contentDescription = null, tint = Color.White, modifier = Modifier.padding(12.dp))
                }
                Spacer(modifier = Modifier.width(16.dp))
                Column {
                    Text(adminName, color = com.example.ui.theme.ExtraDarkGreen, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                    Text("STORE MANAGER", color = Color.Gray, fontSize = 12.sp, letterSpacing = 1.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
        
        item {
            Text("Loyalty Engine Configuration", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, color = com.example.ui.theme.ExtraDarkGreen, modifier = Modifier.padding(top = 16.dp))
        }

        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                border = androidx.compose.foundation.BorderStroke(1.dp, com.example.ui.theme.BorderSlate)
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    if (!isWriteEnabled) {
                        Surface(color = Color(0xFFFFEBEE), shape = RoundedCornerShape(8.dp), modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp)) {
                            Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Lock, contentDescription = "Locked", tint = Color(0xFFD32F2F))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Settings are locked. Contact Super Admin to modify point rules.", color = Color(0xFFD32F2F), fontSize = 12.sp)
                            }
                        }
                    }

                    OutlinedTextField(
                        value = pointsPerDollar,
                        onValueChange = { pointsPerDollar = it },
                        label = { Text("Points Earned per $1 Spent") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.fillMaxWidth(),
                        enabled = isWriteEnabled
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    OutlinedTextField(
                        value = redemptionThreshold,
                        onValueChange = { redemptionThreshold = it },
                        label = { Text("Minimum Points to Redeem") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.fillMaxWidth(),
                        enabled = isWriteEnabled
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    OutlinedTextField(
                        value = discountPer100,
                        onValueChange = { discountPer100 = it },
                        label = { Text("Discount Value per 100 Points ($)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.fillMaxWidth(),
                        enabled = isWriteEnabled
                    )
                    
                    Spacer(modifier = Modifier.height(24.dp))
                    Button(
                        onClick = {
                            val ppd = pointsPerDollar.toIntOrNull() ?: 10
                            val rt = redemptionThreshold.toIntOrNull() ?: 100
                            val dp100 = discountPer100.toDoubleOrNull() ?: 1.0
                            viewModel.updateSettings(ppd, dp100, rt, isWriteEnabled)
                        },
                        modifier = Modifier.fillMaxWidth().height(50.dp),
                        enabled = isWriteEnabled,
                        colors = ButtonDefaults.buttonColors(containerColor = com.example.ui.theme.PrimaryGreen)
                    ) {
                        Text("Save Loyalty Rules & Feature Flags")
                    }
                }
            }
        }
    }
}

@Composable
fun AdminTransactionsTab(viewModel: LoyaltyViewModel) {
    val transactions by viewModel.allTransactions.collectAsStateWithLifecycle()
    val dateFormat = java.text.SimpleDateFormat("MMM dd, yyyy HH:mm", java.util.Locale.getDefault())

    var filterQuery by remember { mutableStateOf("") }
    var selectedSection by remember { mutableIntStateOf(0) }
    
    val sectionTx = when (selectedSection) {
        1 -> transactions.filter { it.pointChange > 0 }
        2 -> transactions.filter { it.pointChange < 0 }
        else -> transactions
    }
    
    val filteredTx = if (filterQuery.isBlank()) sectionTx else sectionTx.filter {
        it.description.contains(filterQuery, ignoreCase = true) || it.userEmail.contains(filterQuery, ignoreCase = true)
    }

    Column(modifier = Modifier.fillMaxSize().padding(20.dp)) {
        Text("Point Transactions", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold, color = com.example.ui.theme.ExtraDarkGreen)
        Text("Detailed report of points added and redeemed by cashiers.", color = Color.Gray, fontSize = 14.sp)
        Spacer(modifier = Modifier.height(16.dp))
        
        TabRow(selectedTabIndex = selectedSection, containerColor = Color.Transparent, contentColor = com.example.ui.theme.PrimaryGreen) {
            Tab(selected = selectedSection == 0, onClick = { selectedSection = 0 }, text = { Text("All", fontWeight = FontWeight.Bold) })
            Tab(selected = selectedSection == 1, onClick = { selectedSection = 1 }, text = { Text("Points Added", fontWeight = FontWeight.Bold) })
            Tab(selected = selectedSection == 2, onClick = { selectedSection = 2 }, text = { Text("Points Redeemed", fontWeight = FontWeight.Bold) })
        }
        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = filterQuery,
            onValueChange = { filterQuery = it },
            label = { Text("Search cashier email or description") },
            modifier = Modifier.fillMaxWidth(),
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) }
        )
        Spacer(modifier = Modifier.height(16.dp))
        
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(filteredTx) { tx ->
                // Extract cashier email if present in description
                val cashierEmailStr = if (tx.description.contains("by cashier", ignoreCase = true)) {
                    tx.description.substringAfterLast("by cashier").trim()
                } else if (tx.description.contains("Redemption by", ignoreCase = true)) {
                    tx.description.substringAfterLast("Redemption by").trim()
                } else {
                    ""
                }
                val mainDesc = if (cashierEmailStr.isNotEmpty()) {
                    tx.description.substringBefore(" by cashier").substringBefore(" by")
                } else { tx.description }

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    border = androidx.compose.foundation.BorderStroke(1.dp, com.example.ui.theme.BorderSlate)
                ) {
                    Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                        Surface(
                            shape = RoundedCornerShape(50),
                            color = if (tx.pointChange > 0) com.example.ui.theme.LightGreenCard else Color(0xFFFFEBEE),
                            modifier = Modifier.size(40.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    if (tx.pointChange > 0) Icons.Default.ArrowUpward else Icons.Default.ArrowDownward, 
                                    contentDescription = null,
                                    tint = if (tx.pointChange > 0) com.example.ui.theme.PrimaryGreen else Color(0xFFD32F2F)
                                )
                            }
                        }
                        Spacer(modifier = Modifier.width(16.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(mainDesc, fontWeight = FontWeight.Bold, fontSize = 14.sp, color = com.example.ui.theme.DarkGreen)
                            Text("Customer: ${tx.userEmail}", fontSize = 12.sp, color = Color.Gray)
                            if (cashierEmailStr.isNotEmpty()) {
                                Text("Cashier: $cashierEmailStr", fontSize = 12.sp, color = com.example.ui.theme.PrimaryGreen)
                            }
                        }
                        Column(horizontalAlignment = Alignment.End) {
                            Text(
                                text = "${if (tx.pointChange > 0) "+" else ""}${tx.pointChange}",
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 18.sp,
                                color = if (tx.pointChange > 0) com.example.ui.theme.PrimaryGreen else Color(0xFFD32F2F)
                            )
                            Text(dateFormat.format(java.util.Date(tx.timestamp)), fontSize = 10.sp, color = Color.Gray)
                        }
                    }
                }
            }
            if (filteredTx.isEmpty()) {
                item {
                    Text("No transactions found.", color = Color.Gray, modifier = Modifier.padding(16.dp))
                }
            }
        }
    }
}

@Composable
fun AdminStatsDashboardTab(viewModel: LoyaltyViewModel) {
    val customers by viewModel.customers.collectAsStateWithLifecycle()
    val transactions by viewModel.allTransactions.collectAsStateWithLifecycle()
    val offers by viewModel.offers.collectAsStateWithLifecycle()

    val totalCustomers = customers.size
    val activeOffers = offers.size
    
    val today = java.util.Calendar.getInstance()
    today.set(java.util.Calendar.HOUR_OF_DAY, 0)
    today.set(java.util.Calendar.MINUTE, 0)
    today.set(java.util.Calendar.SECOND, 0)
    today.set(java.util.Calendar.MILLISECOND, 0)
    val startOfDay = today.timeInMillis

    val pointsIssuedToday = transactions.filter { it.timestamp >= startOfDay && it.pointChange > 0 }.sumOf { it.pointChange }
    val redemptionsToday = transactions.filter { it.timestamp >= startOfDay && it.pointChange < 0 }.sumOf { -it.pointChange }

    Column(modifier = Modifier.fillMaxSize().padding(20.dp)) {
        Text("Store Dashboard", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold, color = com.example.ui.theme.ExtraDarkGreen)
        Text("Overview of loyalty program performance.", color = Color.Gray, fontSize = 14.sp)
        Spacer(modifier = Modifier.height(24.dp))
        
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            Card(modifier = Modifier.weight(1f).aspectRatio(1f), colors = CardDefaults.cardColors(containerColor = Color.White), border = androidx.compose.foundation.BorderStroke(1.dp, com.example.ui.theme.BorderSlate)) {
                Column(modifier = Modifier.padding(16.dp).fillMaxSize(), verticalArrangement = Arrangement.Center, horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(totalCustomers.toString(), fontSize = 36.sp, fontWeight = FontWeight.ExtraBold, color = com.example.ui.theme.PrimaryGreen)
                    Text("Total Customers", fontSize = 12.sp, color = Color.Gray)
                }
            }
            Card(modifier = Modifier.weight(1f).aspectRatio(1f), colors = CardDefaults.cardColors(containerColor = Color.White), border = androidx.compose.foundation.BorderStroke(1.dp, com.example.ui.theme.BorderSlate)) {
                Column(modifier = Modifier.padding(16.dp).fillMaxSize(), verticalArrangement = Arrangement.Center, horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(activeOffers.toString(), fontSize = 36.sp, fontWeight = FontWeight.ExtraBold, color = com.example.ui.theme.AccentOrange)
                    Text("Active Offers", fontSize = 12.sp, color = Color.Gray)
                }
            }
        }
        Spacer(modifier = Modifier.height(16.dp))
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            Card(modifier = Modifier.weight(1f).aspectRatio(1f), colors = CardDefaults.cardColors(containerColor = Color.White), border = androidx.compose.foundation.BorderStroke(1.dp, com.example.ui.theme.BorderSlate)) {
                Column(modifier = Modifier.padding(16.dp).fillMaxSize(), verticalArrangement = Arrangement.Center, horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(pointsIssuedToday.toString(), fontSize = 36.sp, fontWeight = FontWeight.ExtraBold, color = com.example.ui.theme.PrimaryGreen)
                    Text("Points Issued Today", fontSize = 12.sp, color = Color.Gray)
                }
            }
            Card(modifier = Modifier.weight(1f).aspectRatio(1f), colors = CardDefaults.cardColors(containerColor = Color.White), border = androidx.compose.foundation.BorderStroke(1.dp, com.example.ui.theme.BorderSlate)) {
                Column(modifier = Modifier.padding(16.dp).fillMaxSize(), verticalArrangement = Arrangement.Center, horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(redemptionsToday.toString(), fontSize = 36.sp, fontWeight = FontWeight.ExtraBold, color = Color(0xFFD32F2F))
                    Text("Points Redeemed Today", fontSize = 12.sp, color = Color.Gray, textAlign = androidx.compose.ui.text.style.TextAlign.Center)
                }
            }
        }
    }
}

@Composable
fun AdminCustomersTab(viewModel: LoyaltyViewModel) {
    var searchQuery by remember { mutableStateOf("") }
    val allCustomers by viewModel.customers.collectAsStateWithLifecycle()
    
    val filteredCustomers = if (searchQuery.isBlank()) {
        allCustomers
    } else {
        allCustomers.filter { it.name.contains(searchQuery, ignoreCase = true) || it.email.contains(searchQuery, ignoreCase = true) }
    }

    Column(modifier = Modifier.fillMaxSize().padding(20.dp)) {
        Text("Customer Database", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold, color = com.example.ui.theme.ExtraDarkGreen)
        Text("View and search customer profiles.", color = Color.Gray, fontSize = 14.sp)
        Spacer(modifier = Modifier.height(16.dp))
        
        OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            label = { Text("Search by name or email") },
            modifier = Modifier.fillMaxWidth(),
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) }
        )
        Spacer(modifier = Modifier.height(16.dp))
        
        LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxSize()) {
            items(filteredCustomers) { customer ->
                Card(modifier = Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = Color.White), border = androidx.compose.foundation.BorderStroke(1.dp, com.example.ui.theme.BorderSlate)) {
                    Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                        Surface(shape = RoundedCornerShape(50), color = com.example.ui.theme.BackgroundLight, modifier = Modifier.size(40.dp)) {
                            Box(contentAlignment = Alignment.Center) {
                                Text(customer.name.firstOrNull()?.toString() ?: "C", fontWeight = FontWeight.Bold, color = com.example.ui.theme.DarkGreen)
                            }
                        }
                        Spacer(modifier = Modifier.width(16.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(customer.name, fontWeight = FontWeight.Bold, fontSize = 16.sp, color = com.example.ui.theme.DarkGreen)
                            Text(customer.email, fontSize = 12.sp, color = Color.Gray)
                        }
                        Text("${customer.points} pts", fontWeight = FontWeight.ExtraBold, fontSize = 16.sp, color = com.example.ui.theme.PrimaryGreen)
                    }
                }
            }
        }
    }
}

@Composable
fun AdminOffersManagerTab(viewModel: LoyaltyViewModel) {
    var showAddProduct by remember { mutableStateOf(false) }
    
    Column(modifier = Modifier.fillMaxSize()) {
        if (!showAddProduct) {
            Row(modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 16.dp), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Text("Offers Manager", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold, color = com.example.ui.theme.ExtraDarkGreen)
                Button(onClick = { showAddProduct = true }, colors = ButtonDefaults.buttonColors(containerColor = com.example.ui.theme.PrimaryGreen)) {
                    Text("Add Offer")
                }
            }
            Box(modifier = Modifier.weight(1f)) {
                AdminCatalogTab(viewModel) // Reusing existing grid view
            }
        } else {
            Row(modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 16.dp), verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = { showAddProduct = false }) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                }
                Spacer(modifier = Modifier.width(8.dp))
                Text("Add New Offer", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold, color = com.example.ui.theme.ExtraDarkGreen)
            }
            Box(modifier = Modifier.weight(1f)) {
                AdminManageTab(viewModel) // Reusing existing add form
            }
        }
    }
}
