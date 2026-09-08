#!/bin/bash
cat << 'INNER' > app/src/main/java/com/example/CustomerDashboardScreen.kt.new
package com.example

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.example.data.model.Offer
import com.example.data.model.User
import com.example.ui.components.generateBarcode
import com.example.ui.components.generateQRCode
import com.example.ui.viewmodel.LoyaltyViewModel
import java.util.*

@Composable
fun CustomerDashboardScreen(viewModel: LoyaltyViewModel) {
    var selectedTab by remember { mutableIntStateOf(0) }
    
    val customer by viewModel.currentUser.collectAsStateWithLifecycle()
    val offers by viewModel.offers.collectAsStateWithLifecycle()
    val pointSettings by viewModel.pointSettings.collectAsStateWithLifecycle()

    Scaffold(
        bottomBar = {
            NavigationBar(
                containerColor = Color(0xFFFBF4F1), // Light pinkish grey as in image
                tonalElevation = 0.dp
            ) {
                NavigationBarItem(
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    icon = { Icon(Icons.Outlined.CreditCard, contentDescription = "My Card") },
                    label = { Text("My Card", fontSize = 10.sp) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = com.example.ui.theme.DarkGreen,
                        selectedTextColor = com.example.ui.theme.DarkGreen,
                        indicatorColor = Color.Transparent,
                        unselectedIconColor = Color.Gray,
                        unselectedTextColor = Color.Gray
                    )
                )
                NavigationBarItem(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    icon = { Icon(Icons.Outlined.ShoppingBag, contentDescription = "Catalog") },
                    label = { Text("Catalog", fontSize = 10.sp) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = com.example.ui.theme.DarkGreen,
                        selectedTextColor = com.example.ui.theme.DarkGreen,
                        indicatorColor = Color.Transparent,
                        unselectedIconColor = Color.Gray,
                        unselectedTextColor = Color.Gray
                    )
                )
                NavigationBarItem(
                    selected = selectedTab == 2,
                    onClick = { selectedTab = 2 },
                    icon = { Icon(Icons.Outlined.Person, contentDescription = "Profile") },
                    label = { Text("Profile", fontSize = 10.sp) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = com.example.ui.theme.DarkGreen,
                        selectedTextColor = com.example.ui.theme.DarkGreen,
                        indicatorColor = Color.Transparent,
                        unselectedIconColor = Color.Gray,
                        unselectedTextColor = Color.Gray
                    )
                )
            }
        }
    ) { padding ->
        Box(modifier = Modifier.padding(padding).fillMaxSize().background(Color(0xFFFBF4F1))) {
            when (selectedTab) {
                0 -> MyCardTab(customer, pointSettings, viewModel)
                1 -> CustomerCatalogTab(offers)
                2 -> ProfileTab(customer, viewModel)
            }
        }
    }
}

@Composable
fun MyCardTab(customer: User?, pointSettings: com.example.data.model.PointSettings?, viewModel: LoyaltyViewModel) {
    var showBarcode by remember { mutableStateOf(true) }
    
    val customerPoints = customer?.points ?: 0
    val discountRate = pointSettings?.discountPer100Points ?: 1.0
    val rewardValue = (customerPoints / 100.0) * discountRate

    LazyColumn(modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp)) {
        item {
            // App Bar Mock
            Row(
                modifier = Modifier.fillMaxWidth().padding(top = 16.dp, bottom = 24.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text("Burpengary Fruit Market", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = com.example.ui.theme.ExtraDarkGreen)
                    Text("Loyalty Rewards & Grocery Engine", fontSize = 12.sp, color = Color.Gray)
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(color = Color(0xFFD4E7C5), shape = RoundedCornerShape(12.dp)) { // Light green background
                        Text("CUSTOMER", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = com.example.ui.theme.DarkGreen, modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp))
                    }
                }
            }
        }
        
        item {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Column {
                    Text("BURPENGARY LOYALTY", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.Gray, letterSpacing = 1.sp)
                    val firstName = customer?.name?.split(" ")?.firstOrNull() ?: "Guest"
                    Text("G'day, $firstName! \uD83D\uDC4B", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = com.example.ui.theme.ExtraDarkGreen)
                }
                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = Color.White,
                    shadowElevation = 2.dp
                ) {
                    Row(modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp), verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.NotificationsNone, contentDescription = "Notifications", modifier = Modifier.size(20.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Surface(color = Color(0xFFFF6D00), shape = RoundedCornerShape(50)) { // Orange dot
                            Text("2", color = Color.White, fontSize = 10.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp))
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.height(24.dp))
        }

        item {
            val threshold = pointSettings?.redemptionThreshold ?: 100
            if (customerPoints >= threshold) {
                Surface(
                    modifier = Modifier.fillMaxWidth().padding(bottom = 24.dp),
                    color = com.example.ui.theme.LightGreenCard,
                    shape = RoundedCornerShape(16.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, com.example.ui.theme.PrimaryGreen)
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("\uD83C\uDF89", fontSize = 24.sp)
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text("Reward Ready!", fontWeight = FontWeight.Bold, color = com.example.ui.theme.DarkGreen, fontSize = 16.sp)
                            Text(String.format("Redeem for $%.2f off at checkout", rewardValue), color = com.example.ui.theme.MediumGreen, fontSize = 14.sp)
                        }
                    }
                }
            }
        }
        
        item {
            // Dark Green Membership Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = com.example.ui.theme.ExtraDarkGreen)
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                        Text("MEMBERSHIP CARD", color = Color.White.copy(alpha = 0.7f), fontSize = 12.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
                        Surface(color = Color(0xFFFF6D00), shape = RoundedCornerShape(8.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)) {
                                Icon(Icons.Default.Star, contentDescription = null, tint = Color.White, modifier = Modifier.size(14.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("$customerPoints PTS", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Burpengary Market Gold", color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                    
                    Spacer(modifier = Modifier.height(24.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        val initial = customer?.name?.firstOrNull()?.toString()?.uppercase(Locale.getDefault()) ?: "G"
                        Surface(shape = RoundedCornerShape(50), color = Color.White, modifier = Modifier.size(48.dp)) {
                            Box(contentAlignment = Alignment.Center) {
                                Text(initial, fontSize = 24.sp, fontWeight = FontWeight.Bold, color = com.example.ui.theme.ExtraDarkGreen)
                            }
                        }
                        Spacer(modifier = Modifier.width(16.dp))
                        Column {
                            Text(customer?.name ?: "Customer User", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                            val userId = customer?.email?.hashCode()?.let { Math.abs(it).toString().take(6) } ?: "000000"
                            Text("ID: BFM-$initial-$userId", color = Color.White.copy(alpha = 0.8f), fontSize = 12.sp)
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(24.dp))
                    // Barcode / QR Area
                    Surface(
                        modifier = Modifier.fillMaxWidth().height(120.dp),
                        color = Color.White,
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center, modifier = Modifier.padding(16.dp)) {
                            val emailStr = customer?.email ?: "guest"
                            if (showBarcode) {
                                val barcodeBitmap = generateBarcode(emailStr, 600, 200)
                                if (barcodeBitmap != null) {
                                    androidx.compose.foundation.Image(bitmap = barcodeBitmap.asImageBitmap(), contentDescription = "Barcode", modifier = Modifier.fillMaxSize())
                                } else {
                                    Text("Barcode Error", color = Color.Red)
                                }
                            } else {
                                val qrBitmap = generateQRCode(emailStr, 400)
                                if (qrBitmap != null) {
                                    androidx.compose.foundation.Image(bitmap = qrBitmap.asImageBitmap(), contentDescription = "QR Code", modifier = Modifier.size(100.dp))
                                } else {
                                    Text("QR Error", color = Color.Red)
                                }
                            }
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                        Row(modifier = Modifier.background(Color.White.copy(alpha = 0.2f), shape = RoundedCornerShape(12.dp)).padding(4.dp)) {
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = if (showBarcode) Color.White else Color.Transparent,
                                modifier = Modifier.clickable { showBarcode = true }
                            ) {
                                Text("Barcode", color = if (showBarcode) com.example.ui.theme.ExtraDarkGreen else Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp))
                            }
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = if (!showBarcode) Color.White else Color.Transparent,
                                modifier = Modifier.clickable { showBarcode = false }
                            ) {
                                Text("QR Code", color = if (!showBarcode) com.example.ui.theme.ExtraDarkGreen else Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp))
                            }
                        }
                        
                        Surface(color = Color(0xFFFF6D00), shape = RoundedCornerShape(8.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)) {
                                Icon(Icons.Default.QrCodeScanner, contentDescription = null, tint = Color.White, modifier = Modifier.size(14.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("SCANNABLE", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
        }
        
        item {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                // Points Balance Card
                Card(
                    modifier = Modifier.weight(1f).height(140.dp),
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    border = androidx.compose.foundation.BorderStroke(1.dp, com.example.ui.theme.BorderSlate)
                ) {
                    Column(modifier = Modifier.padding(16.dp).fillMaxSize()) {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.Top) {
                            Icon(Icons.Default.Stars, contentDescription = null, tint = Color.Gray, modifier = Modifier.size(20.dp))
                            val threshold = pointSettings?.redemptionThreshold ?: 100
                            if (customerPoints >= threshold) {
                                Surface(color = com.example.ui.theme.ExtraDarkGreen, shape = RoundedCornerShape(8.dp)) {
                                    Text("REDEEM", color = Color.White, fontSize = 10.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp))
                                }
                            }
                        }
                        Spacer(modifier = Modifier.weight(1f))
                        Text("POINTS BALANCE", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color.Gray)
                        Text("$customerPoints", fontSize = 28.sp, fontWeight = FontWeight.Black, color = com.example.ui.theme.ExtraDarkGreen)
                        Text("Minimum ${pointSettings?.redemptionThreshold ?: 100} pts for discount", fontSize = 9.sp, color = Color.Gray)
                    }
                }
                
                // Reward Value Card
                Card(
                    modifier = Modifier.weight(1f).height(140.dp),
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    border = androidx.compose.foundation.BorderStroke(1.dp, com.example.ui.theme.BorderSlate)
                ) {
                    Column(modifier = Modifier.padding(16.dp).fillMaxSize()) {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.Top) {
                            Icon(Icons.Default.LocalOffer, contentDescription = null, tint = Color(0xFFFF6D00), modifier = Modifier.size(20.dp))
                            Text("PROMOTIONS & OFFERS", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color(0xFFFF6D00), letterSpacing = 1.sp)
                        }
                        Spacer(modifier = Modifier.weight(1f))
                        Text("ESTIMATED VALUE", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color.Gray)
                        Text(String.format("$%.2f", rewardValue), fontSize = 28.sp, fontWeight = FontWeight.Black, color = com.example.ui.theme.ExtraDarkGreen)
                        Text("Catalog \u2192", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color.Gray)
                    }
                }
            }
            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@Composable
fun CustomerCatalogTab(offers: List<Offer>) {
    LazyColumn(modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp)) {
        item {
            Text("Latest Offers", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, color = com.example.ui.theme.ExtraDarkGreen, modifier = Modifier.padding(vertical = 16.dp))
        }
        
        items(offers) { offer ->
            Card(
                modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                border = androidx.compose.foundation.BorderStroke(1.dp, com.example.ui.theme.BorderSlate)
            ) {
                Column(modifier = Modifier.fillMaxWidth()) {
                    if (offer.imageUrl != null) {
                        AsyncImage(
                            model = offer.imageUrl,
                            contentDescription = offer.title,
                            modifier = Modifier.fillMaxWidth().height(160.dp).background(Color.LightGray),
                            contentScale = ContentScale.Crop
                        )
                    }
                    Column(modifier = Modifier.fillMaxWidth().padding(20.dp)) {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                            Text(text = offer.title, style = MaterialTheme.typography.titleMedium, color = com.example.ui.theme.ExtraDarkGreen, fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f))
                            Text(text = offer.price, style = MaterialTheme.typography.titleMedium, color = com.example.ui.theme.MediumGreen, fontWeight = FontWeight.Bold)
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(text = offer.category.uppercase(Locale.getDefault()), style = MaterialTheme.typography.labelSmall, color = Color.Gray)
                        if (offer.description.isNotEmpty()) {
                            Spacer(modifier = Modifier.height(8.dp))
                            if (offer.description.startsWith("OFFER:")) {
                                Surface(color = com.example.ui.theme.LightGreenCard, shape = RoundedCornerShape(4.dp)) {
                                    Text(text = offer.description, style = MaterialTheme.typography.bodySmall, color = com.example.ui.theme.DarkGreen, modifier = Modifier.padding(6.dp))
                                }
                            } else {
                                Text(text = offer.description, style = MaterialTheme.typography.bodyMedium, color = Color.Gray)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ProfileTab(customer: User?, viewModel: LoyaltyViewModel) {
    Column(modifier = Modifier.fillMaxSize().padding(20.dp), verticalArrangement = Arrangement.Center, horizontalAlignment = Alignment.CenterHorizontally) {
        Icon(Icons.Default.Person, contentDescription = null, modifier = Modifier.size(80.dp), tint = com.example.ui.theme.DarkGreen)
        Spacer(modifier = Modifier.height(16.dp))
        Text(customer?.name ?: "Guest", fontSize = 24.sp, fontWeight = FontWeight.Bold)
        Text(customer?.email ?: "guest@example.com", fontSize = 16.sp, color = Color.Gray)
        Spacer(modifier = Modifier.height(40.dp))
        Button(
            onClick = { viewModel.logout() },
            modifier = Modifier.fillMaxWidth().height(50.dp),
            colors = ButtonDefaults.buttonColors(containerColor = com.example.ui.theme.PrimaryGreen)
        ) {
            Text("Sign Out")
        }
    }
}
INNER
mv app/src/main/java/com/example/CustomerDashboardScreen.kt.new app/src/main/java/com/example/CustomerDashboardScreen.kt
