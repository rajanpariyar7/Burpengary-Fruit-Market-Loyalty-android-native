#!/bin/bash
cat << 'INNER' > app/src/main/java/com/example/AdminDashboardScreen.kt
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
                    icon = { Icon(Icons.Default.ShoppingBag, contentDescription = "Catalog") },
                    label = { Text("Catalog") },
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = com.example.ui.theme.DarkGreen,
                        selectedTextColor = com.example.ui.theme.DarkGreen,
                        indicatorColor = com.example.ui.theme.LightGreenCard
                    )
                )
                NavigationBarItem(
                    icon = { Icon(Icons.Default.AddCircle, contentDescription = "Manage") },
                    label = { Text("Manage") },
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = com.example.ui.theme.DarkGreen,
                        selectedTextColor = com.example.ui.theme.DarkGreen,
                        indicatorColor = com.example.ui.theme.LightGreenCard
                    )
                )
                NavigationBarItem(
                    icon = { Icon(Icons.Default.Settings, contentDescription = "Settings") },
                    label = { Text("Settings") },
                    selected = selectedTab == 2,
                    onClick = { selectedTab = 2 },
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
                0 -> AdminCatalogTab(viewModel)
                1 -> AdminManageTab(viewModel)
                2 -> AdminSettingsTab(viewModel, currentUser?.name ?: "Admin")
            }
        }
    }
}

@Composable
fun AdminCatalogTab(viewModel: LoyaltyViewModel) {
    val offers by viewModel.offers.collectAsStateWithLifecycle()
    var selectedCategory by remember { mutableStateOf("ALL") }
    
    val categories = listOf("ALL", "FRUITS", "VEGETABLES", "BAKERY", "DAIRY", "MEAT", "GROCERY")
    
    Column(modifier = Modifier.fillMaxSize()) {
        Text("Product Catalog", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold, color = com.example.ui.theme.ExtraDarkGreen, modifier = Modifier.padding(20.dp))
        
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
            contentPadding = PaddingValues(horizontal = 20.dp, bottom = 24.dp),
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

@Composable
fun AdminManageTab(viewModel: LoyaltyViewModel) {
    var offerTitle by remember { mutableStateOf("") }
    var offerCategory by remember { mutableStateOf("") }
    var offerPrice by remember { mutableStateOf("") }
    var offerDesc by remember { mutableStateOf("") }
    var isSpecialOffer by remember { mutableStateOf(false) }
    var imageUri by remember { mutableStateOf<Uri?>(null) }
    
    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        imageUri = uri
    }

    LazyColumn(modifier = Modifier.fillMaxSize().padding(horizontal = 20.dp, vertical = 16.dp)) {
        item {
            Text("Add New Offer", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold, color = com.example.ui.theme.ExtraDarkGreen, modifier = Modifier.padding(bottom = 16.dp))
        }
        item {
            Card(
                modifier = Modifier.fillMaxWidth().padding(bottom = 24.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                border = androidx.compose.foundation.BorderStroke(1.dp, com.example.ui.theme.BorderSlate)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    OutlinedTextField(value = offerTitle, onValueChange = { offerTitle = it }, label = { Text("Offer Title") }, modifier = Modifier.fillMaxWidth())
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(value = offerCategory, onValueChange = { offerCategory = it }, label = { Text("Category (e.g. FRUITS)") }, modifier = Modifier.fillMaxWidth())
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(value = offerPrice, onValueChange = { offerPrice = it }, label = { Text("Price (e.g. \$2.99)") }, modifier = Modifier.fillMaxWidth())
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
                            viewModel.addOffer(offerTitle, offerPrice, desc, offerCategory, imageUri?.toString())
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
                        Text("Publish Offer")
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
                Spacer(modifier = Modifier.weight(1f))
                IconButton(onClick = { viewModel.logout() }) {
                    Icon(Icons.Default.Logout, contentDescription = "Logout", tint = Color.Red)
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
                        Text("Save Configuration")
                    }
                }
            }
        }
    }
}
INNER
