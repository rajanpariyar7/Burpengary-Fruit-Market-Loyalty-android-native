package com.example

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.example.data.model.Offer
import com.example.data.model.User
import com.example.ui.components.generateBarcode
import com.example.ui.components.generateQRCode
import com.example.ui.theme.*
import com.example.ui.viewmodel.LoyaltyViewModel
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import java.util.*

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun CustomerDashboardScreen(viewModel: LoyaltyViewModel) {
    var selectedTab by remember { mutableIntStateOf(0) }
    
    val customer by viewModel.currentUser.collectAsStateWithLifecycle()
    val offers by viewModel.offers.collectAsStateWithLifecycle()
    val pointSettings by viewModel.pointSettings.collectAsStateWithLifecycle()

    // Request notification permission on Android 13+
    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
        val notificationPermissionState = rememberPermissionState(android.Manifest.permission.POST_NOTIFICATIONS)
        LaunchedEffect(Unit) {
            if (!notificationPermissionState.status.isGranted) {
                notificationPermissionState.launchPermissionRequest()
            }
        }
    }

    Scaffold(
        containerColor = Color(0xFFF3F4F1), // Very light greenish gray from screenshot
        bottomBar = {
            Surface(
                color = Color.White,
                shadowElevation = 8.dp,
                shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
            ) {
                NavigationBar(
                    containerColor = Color.Transparent,
                    tonalElevation = 0.dp,
                    modifier = Modifier.padding(horizontal = 8.dp).height(80.dp)
                ) {
                    NavigationBarItem(
                        selected = selectedTab == 0,
                        onClick = { selectedTab = 0 },
                        icon = { Icon(if (selectedTab == 0) Icons.Filled.Dashboard else Icons.Outlined.Dashboard, contentDescription = "Home") },
                        label = { Text("Home", fontSize = 10.sp, fontWeight = if (selectedTab == 0) FontWeight.Bold else FontWeight.Normal) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = DarkGreen,
                            selectedTextColor = DarkGreen,
                            indicatorColor = Color.Transparent,
                            unselectedIconColor = Color.Gray,
                            unselectedTextColor = Color.Gray
                        )
                    )
                    NavigationBarItem(
                        selected = selectedTab == 1,
                        onClick = { selectedTab = 1 },
                        icon = { Icon(if (selectedTab == 1) Icons.Filled.QrCode else Icons.Outlined.QrCode, contentDescription = "ID") },
                        label = { Text("ID", fontSize = 10.sp, fontWeight = if (selectedTab == 1) FontWeight.Bold else FontWeight.Normal) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = DarkGreen,
                            selectedTextColor = DarkGreen,
                            indicatorColor = Color.Transparent,
                            unselectedIconColor = Color.Gray,
                            unselectedTextColor = Color.Gray
                        )
                    )
                    NavigationBarItem(
                        selected = selectedTab == 2,
                        onClick = { selectedTab = 2 },
                        icon = { Icon(if (selectedTab == 2) Icons.Filled.ShoppingBag else Icons.Outlined.ShoppingBag, contentDescription = "Catalog") },
                        label = { Text("Catalog", fontSize = 10.sp, fontWeight = if (selectedTab == 2) FontWeight.Bold else FontWeight.Normal) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = DarkGreen,
                            selectedTextColor = DarkGreen,
                            indicatorColor = Color.Transparent,
                            unselectedIconColor = Color.Gray,
                            unselectedTextColor = Color.Gray
                        )
                    )
                    NavigationBarItem(
                        selected = selectedTab == 3,
                        onClick = { selectedTab = 3 },
                        icon = { Icon(if (selectedTab == 3) Icons.Filled.History else Icons.Outlined.History, contentDescription = "History") },
                        label = { Text("History", fontSize = 10.sp, fontWeight = if (selectedTab == 3) FontWeight.Bold else FontWeight.Normal) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = DarkGreen,
                            selectedTextColor = DarkGreen,
                            indicatorColor = Color.Transparent,
                            unselectedIconColor = Color.Gray,
                            unselectedTextColor = Color.Gray
                        )
                    )
                    NavigationBarItem(
                        selected = selectedTab == 4,
                        onClick = { selectedTab = 4 },
                        icon = { Icon(if (selectedTab == 4) Icons.Filled.Person else Icons.Outlined.Person, contentDescription = "Profile") },
                        label = { Text("Profile", fontSize = 10.sp, fontWeight = if (selectedTab == 4) FontWeight.Bold else FontWeight.Normal) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = DarkGreen,
                            selectedTextColor = DarkGreen,
                            indicatorColor = Color.Transparent,
                            unselectedIconColor = Color.Gray,
                            unselectedTextColor = Color.Gray
                        )
                    )
                }
            }
        }
    ) { padding ->
        Box(modifier = Modifier.padding(padding).fillMaxSize().background(Color(0xFFF3F4F1))) {
            when (selectedTab) {
                0 -> CustomerHomeTab(customer, pointSettings, offers, viewModel)
                1 -> CustomerQRTab(customer)
                2 -> CustomerCatalogTab(customer, pointSettings, offers, viewModel)
                3 -> CustomerHistoryTab(viewModel)
                4 -> ProfileTab(customer, viewModel)
            }
        }
    }
}

@Composable
fun CustomerHomeTab(customer: User?, pointSettings: com.example.data.model.PointSettings?, offers: List<Offer>, viewModel: LoyaltyViewModel) {
    var selectedCodeType by remember { mutableStateOf("Barcode") } // "Barcode", "QR Code", "Scannable"
    
    val customerPoints = customer?.points ?: 0
    val discountRate = pointSettings?.discountPer100Points ?: 1.0
    val rewardValue = (customerPoints / 100.0) * discountRate

    LazyColumn(
        modifier = Modifier.fillMaxSize().padding(horizontal = 20.dp),
        contentPadding = PaddingValues(top = 16.dp, bottom = 40.dp)
    ) {
        // Greeting
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(
                        shape = CircleShape,
                        color = Color(0xFFFCE4EC),
                        modifier = Modifier.size(40.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(Icons.Filled.ShoppingCart, contentDescription = "Cart", tint = Color(0xFFD81B60), modifier = Modifier.size(20.dp))
                        }
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text("BURPENGARY LOYALTY", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color.Gray, letterSpacing = 1.sp)
                        val firstName = customer?.name?.substringBefore(" ") ?: "Guest"
                        Text("G'day, $firstName! \uD83D\uDC4B", fontSize = 22.sp, fontWeight = FontWeight.ExtraBold, color = Color.Black)
                    }
                }
                Surface(
                    shape = CircleShape,
                    color = Color.White,
                    modifier = Modifier.size(40.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(Icons.Outlined.Notifications, contentDescription = "Notifications", tint = Color.Black, modifier = Modifier.size(24.dp))
                        Box(
                            modifier = Modifier.align(Alignment.TopEnd).padding(2.dp).size(14.dp).background(AccentOrange, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("2", color = Color.White, fontSize = 8.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.height(24.dp))
        }

        // Loyalty Card
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF2C5E3B)) // Forest Green
            ) {
                Column(modifier = Modifier.fillMaxWidth().padding(24.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("MEMBERSHIP CARD", color = Color.White.copy(alpha = 0.8f), fontSize = 10.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
                        Surface(
                            color = AccentOrange,
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)) {
                                Icon(Icons.Filled.Star, contentDescription = null, tint = Color.White, modifier = Modifier.size(14.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("$customerPoints PTS", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Burpengary Market Gold", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Color.White)
                    
                    Spacer(modifier = Modifier.height(24.dp))
                    
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(
                            shape = CircleShape,
                            color = Color(0xFFD4E1D4),
                            modifier = Modifier.size(48.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                val initial = customer?.name?.firstOrNull()?.toString()?.uppercase() ?: "G"
                                Text(initial, fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Color(0xFF2C5E3B))
                            }
                        }
                        Spacer(modifier = Modifier.width(16.dp))
                        Column {
                            Text(customer?.name ?: "Guest", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color.White)
                            // We use a mock ID or generate one based on email
                            val customerId = customer?.email?.hashCode()?.toString()?.takeLast(6) ?: "000000"
                            Text("ID: BFM-$customerId", fontSize = 14.sp, color = Color.White.copy(alpha = 0.8f))
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(24.dp))
                    
                    val emailStr = customer?.email ?: "guest"
                    
                    // Display either barcode or QR code based on selection
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = Color.White,
                        modifier = Modifier.fillMaxWidth().height(120.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
                            if (selectedCodeType == "QR Code") {
                                val qrBitmap = generateQRCode(emailStr, 400)
                                if (qrBitmap != null) {
                                    Image(bitmap = qrBitmap.asImageBitmap(), contentDescription = "QR Code", modifier = Modifier.fillMaxSize().padding(12.dp))
                                }
                            } else {
                                val barcodeBitmap = generateBarcode(emailStr, 600, 200)
                                if (barcodeBitmap != null) {
                                    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.padding(12.dp)) {
                                        Image(
                                            bitmap = barcodeBitmap.asImageBitmap(), 
                                            contentDescription = "Barcode", 
                                            modifier = Modifier.fillMaxWidth().weight(1f),
                                            contentScale = ContentScale.Fit
                                        )
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Text(emailStr, fontSize = 10.sp, color = Color.Black)
                                    }
                                }
                            }
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        ToggleBtn(text = "Barcode", icon = Icons.Default.ViewColumn, isSelected = selectedCodeType == "Barcode", onClick = { selectedCodeType = "Barcode" }, modifier = Modifier.weight(1f))
                        ToggleBtn(text = "QR Code", icon = Icons.Default.QrCode, isSelected = selectedCodeType == "QR Code", onClick = { selectedCodeType = "QR Code" }, modifier = Modifier.weight(1f))
                        ToggleBtn(text = "SCANNABLE", icon = Icons.Default.CropFree, isSelected = true, onClick = {}, modifier = Modifier.weight(1f), isOrange = true)
                    }
                }
            }
            Spacer(modifier = Modifier.height(24.dp))
        }
        
        // 2x1 Grid for Points and Reward Value
        item {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                // Points Balance
                Card(
                    modifier = Modifier.weight(1f).height(160.dp),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White)
                ) {
                    Column(modifier = Modifier.padding(16.dp).fillMaxSize()) {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Outlined.StarBorder, contentDescription = null, tint = Color.Black)
                            Surface(color = AccentOrange, shape = RoundedCornerShape(8.dp)) {
                                Text("EARNING", color = Color.White, fontSize = 10.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp))
                            }
                        }
                        Spacer(modifier = Modifier.height(16.dp))
                        Text("POINTS BALANCE", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color.Gray)
                        Text("$customerPoints", fontSize = 32.sp, fontWeight = FontWeight.ExtraBold, color = Color.Black)
                        Spacer(modifier = Modifier.weight(1f))
                        Text("Minimum 100 pts for discount", fontSize = 10.sp, color = Color.Gray)
                    }
                }
                
                // Reward Value
                Card(
                    modifier = Modifier.weight(1f).height(160.dp),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White)
                ) {
                    Column(modifier = Modifier.padding(16.dp).fillMaxSize()) {
                        Surface(
                            shape = CircleShape,
                            color = Color(0xFFFFF3E0),
                            modifier = Modifier.size(32.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(Icons.Outlined.LocalOffer, contentDescription = null, tint = AccentOrange, modifier = Modifier.size(16.dp))
                            }
                        }
                        Spacer(modifier = Modifier.height(16.dp))
                        Text("REWARD VALUE", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = AccentOrange)
                        Text(String.format("$%.2f", rewardValue), fontSize = 32.sp, fontWeight = FontWeight.ExtraBold, color = Color.Black)
                        Spacer(modifier = Modifier.height(8.dp))
                        Box(modifier = Modifier.height(4.dp).width(60.dp).background(DarkGreen, RoundedCornerShape(2.dp)))
                    }
                }
            }
            Spacer(modifier = Modifier.height(24.dp))
        }

        // Promotions & Offers Section (As Grid)
        item {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.Bottom) {
                Column {
                    Text("PROMOTIONS & OFFERS", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = AccentOrange)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text("Special Bonus Deals", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Color.Black)
                }
                Text("Catalog →", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color.DarkGray)
            }
            Spacer(modifier = Modifier.height(16.dp))
        }
        
        // Show Special Bonus Deals in Grid
        val chunkedOffers = offers.chunked(2)
        chunkedOffers.forEach { rowOffers ->
            item {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    for (offer in rowOffers) {
                        Card(
                            modifier = Modifier.weight(1f).height(180.dp).clickable { 
                                viewModel.logOfferClick(offer.title)
                                viewModel.sendNotification("Offer Viewed", "Details for ${offer.title}")
                            },
                            shape = RoundedCornerShape(20.dp),
                            colors = CardDefaults.cardColors(containerColor = Color.White)
                        ) {
                            Column {
                                Box(modifier = Modifier.fillMaxWidth().height(100.dp).background(LightGreenCard.copy(alpha = 0.5f))) {
                                    if (offer.imageUrl != null) {
                                        AsyncImage(
                                            model = offer.imageUrl,
                                            contentDescription = offer.title,
                                            modifier = Modifier.fillMaxSize(),
                                            contentScale = ContentScale.Crop
                                        )
                                    } else {
                                        Icon(Icons.Outlined.LocalOffer, contentDescription = null, modifier = Modifier.align(Alignment.Center).size(40.dp), tint = PrimaryGreen)
                                    }
                                }
                                Column(modifier = Modifier.padding(12.dp)) {
                                    Text(offer.title, fontWeight = FontWeight.Bold, fontSize = 14.sp, color = DarkGreen, maxLines = 1)
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(offer.price, fontWeight = FontWeight.Bold, fontSize = 14.sp, color = PrimaryGreen)
                                }
                            }
                        }
                    }
                    if (rowOffers.size == 1) {
                        Spacer(modifier = Modifier.weight(1f)) // fill empty space if odd number of items
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}

@Composable
fun ToggleBtn(text: String, icon: androidx.compose.ui.graphics.vector.ImageVector, isSelected: Boolean, isOrange: Boolean = false, onClick: () -> Unit, modifier: Modifier = Modifier) {
    Surface(
        onClick = onClick,
        modifier = modifier.height(36.dp),
        shape = RoundedCornerShape(8.dp),
        color = if (isOrange) AccentOrange else if (isSelected) Color.White else Color.White.copy(alpha = 0.2f)
    ) {
        Row(
            modifier = Modifier.fillMaxSize(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(icon, contentDescription = text, tint = if (isSelected || isOrange) Color.Black else Color.White, modifier = Modifier.size(14.dp))
            Spacer(modifier = Modifier.width(4.dp))
            Text(text, color = if (isSelected || isOrange) Color.Black else Color.White, fontSize = 10.sp, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
fun CustomerCatalogTab(customer: User?, pointSettings: com.example.data.model.PointSettings?, offers: List<Offer>, viewModel: LoyaltyViewModel) {
    var showRedeemDialog by remember { mutableStateOf(false) }
    var pointsToRedeem by remember { mutableStateOf("") }
    
    val customerPoints = customer?.points ?: 0
    val discountRate = pointSettings?.discountPer100Points ?: 1.0
    val threshold = pointSettings?.redemptionThreshold ?: 100
    
    if (showRedeemDialog) {
        AlertDialog(
            onDismissRequest = { showRedeemDialog = false },
            title = { Text("Redeem Points", fontWeight = FontWeight.Bold, color = DarkGreen) },
            text = {
                Column {
                    Text("You have $customerPoints points available.", fontSize = 14.sp)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Exchange points for a digital voucher you can use at checkout.", fontSize = 12.sp, color = Color.Gray)
                    Spacer(modifier = Modifier.height(16.dp))
                    OutlinedTextField(
                        value = pointsToRedeem,
                        onValueChange = { pointsToRedeem = it },
                        label = { Text("Points to Redeem") },
                        modifier = Modifier.fillMaxWidth(),
                        keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(keyboardType = androidx.compose.ui.text.input.KeyboardType.Number)
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val pts = pointsToRedeem.toIntOrNull() ?: 0
                        if (pts >= threshold && pts <= customerPoints) {
                            val rewardValue = (pts / 100.0) * discountRate
                            viewModel.redeemReward(com.example.data.model.Reward(title = "$$rewardValue Discount Voucher", description = "Voucher created by user", costInPoints = pts))
                            showRedeemDialog = false
                            pointsToRedeem = ""
                        } else {
                            viewModel.sendNotification("Error", "Invalid amount. Minimum is $threshold points.")
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = AccentOrange)
                ) {
                    Text("Create Voucher")
                }
            },
            dismissButton = {
                TextButton(onClick = { showRedeemDialog = false }) {
                    Text("Cancel", color = Color.Gray)
                }
            },
            containerColor = Color.White
        )
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize().padding(horizontal = 24.dp),
        contentPadding = PaddingValues(top = 24.dp, bottom = 40.dp)
    ) {
        item {
            Card(
                modifier = Modifier.fillMaxWidth().padding(bottom = 24.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = PrimaryGreen)
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text("Your Points Balance", color = Color.White.copy(alpha = 0.8f), fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    Text("$customerPoints", color = Color.White, fontSize = 36.sp, fontWeight = FontWeight.ExtraBold)
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(
                        onClick = { showRedeemDialog = true },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(containerColor = AccentOrange)
                    ) {
                        Text("Redeem for Voucher", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
        
        item {
            Text("Product Catalog", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.ExtraBold, color = DarkGreen)
            Spacer(modifier = Modifier.height(16.dp))
        }
        
        items(offers) { offer ->
            Card(
                modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Row(modifier = Modifier.fillMaxWidth().padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                    Surface(
                        modifier = Modifier.size(80.dp),
                        shape = RoundedCornerShape(16.dp),
                        color = LightGreenCard.copy(alpha = 0.5f)
                    ) {
                        if (offer.imageUrl != null) {
                            AsyncImage(
                                model = offer.imageUrl,
                                contentDescription = offer.title,
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Crop
                            )
                        } else {
                            Icon(Icons.Outlined.Image, contentDescription = null, modifier = Modifier.padding(24.dp), tint = PrimaryGreen)
                        }
                    }
                    Spacer(modifier = Modifier.width(16.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(text = offer.title, fontSize = 16.sp, color = DarkGreen, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(text = offer.category.replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }, fontSize = 12.sp, color = Color.Gray)
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(text = offer.price, fontSize = 16.sp, color = PrimaryGreen, fontWeight = FontWeight.Bold)
                    }
                    Surface(
                        shape = CircleShape,
                        color = PrimaryGreen,
                        modifier = Modifier.size(36.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(Icons.Filled.Add, contentDescription = "Add", tint = Color.White, modifier = Modifier.size(20.dp))
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ProfileTab(customer: User?, viewModel: LoyaltyViewModel) {
    var showPasswordDialog by remember { mutableStateOf(false) }
    
    if (showPasswordDialog) {
        var newPassword by remember { mutableStateOf("") }
        AlertDialog(
            onDismissRequest = { showPasswordDialog = false },
            title = { Text("Change Password", fontWeight = FontWeight.Bold, color = DarkGreen) },
            text = {
                Column {
                    Text("Enter your new app password below. Remember to save it securely.", fontSize = 14.sp)
                    Spacer(modifier = Modifier.height(16.dp))
                    OutlinedTextField(
                        value = newPassword,
                        onValueChange = { newPassword = it },
                        label = { Text("New Password") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (newPassword.isNotBlank()) {
                            customer?.email?.let { email ->
                                viewModel.changeUserPassword(email, newPassword)
                            }
                            showPasswordDialog = false
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryGreen)
                ) {
                    Text("Save")
                }
            },
            dismissButton = {
                TextButton(onClick = { showPasswordDialog = false }) {
                    Text("Cancel", color = Color.Gray)
                }
            },
            containerColor = Color.White
        )
    }

    Column(
        modifier = Modifier.fillMaxSize().padding(24.dp), 
        verticalArrangement = Arrangement.Center, 
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Surface(
            shape = CircleShape,
            color = LightGreenCard,
            modifier = Modifier.size(100.dp)
        ) {
            Box(contentAlignment = Alignment.Center) {
                val initial = customer?.name?.firstOrNull()?.toString()?.uppercase() ?: "G"
                Text(initial, fontSize = 40.sp, fontWeight = FontWeight.Bold, color = PrimaryGreen)
            }
        }
        Spacer(modifier = Modifier.height(24.dp))
        Text(customer?.name ?: "Guest", fontSize = 24.sp, fontWeight = FontWeight.ExtraBold, color = DarkGreen)
        Text(customer?.email ?: "guest@example.com", fontSize = 14.sp, color = Color.Gray)
        Spacer(modifier = Modifier.height(48.dp))
        
        OutlinedButton(
            onClick = { showPasswordDialog = true },
            modifier = Modifier.fillMaxWidth().height(56.dp),
            colors = ButtonDefaults.outlinedButtonColors(contentColor = PrimaryGreen),
            border = androidx.compose.foundation.BorderStroke(1.dp, PrimaryGreen),
            shape = RoundedCornerShape(16.dp)
        ) {
            Text("Change Password", fontSize = 16.sp, fontWeight = FontWeight.Bold)
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        Button(
            onClick = { viewModel.logout() },
            modifier = Modifier.fillMaxWidth().height(56.dp),
            colors = ButtonDefaults.buttonColors(containerColor = PrimaryGreen),
            shape = RoundedCornerShape(16.dp)
        ) {
            Text("Sign Out", fontSize = 16.sp, fontWeight = FontWeight.Bold)
        }
    }
}


@Composable
fun CustomerQRTab(customer: User?) {
    val email = customer?.email ?: "Unknown"
    val name = customer?.name ?: "Customer"
    
    // Generate QR using the helper function
    var qrBitmap by remember { mutableStateOf<androidx.compose.ui.graphics.ImageBitmap?>(null) }
    var barcodeBitmap by remember { mutableStateOf<androidx.compose.ui.graphics.ImageBitmap?>(null) }
    
    LaunchedEffect(email) {
        val qr = generateQRCode(email, 800)
        qrBitmap = qr?.asImageBitmap()
        val bc = generateBarcode(email, 800, 200)
        barcodeBitmap = bc?.asImageBitmap()
    }
    
    Column(
        modifier = Modifier.fillMaxSize().padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("Your Member ID", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold, color = DarkGreen)
        Text("Show this at checkout to earn points.", color = Color.Gray, fontSize = 14.sp)
        Spacer(modifier = Modifier.height(32.dp))
        
        Card(
            modifier = Modifier.fillMaxWidth().aspectRatio(0.8f),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
        ) {
            Column(
                modifier = Modifier.fillMaxSize().padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                if (qrBitmap != null) {
                    Image(bitmap = qrBitmap!!, contentDescription = "QR Code", modifier = Modifier.weight(1f).aspectRatio(1f))
                } else {
                    Box(modifier = Modifier.weight(1f).aspectRatio(1f).background(Color.LightGray))
                }
                Spacer(modifier = Modifier.height(16.dp))
                if (barcodeBitmap != null) {
                    Image(bitmap = barcodeBitmap!!, contentDescription = "Barcode", modifier = Modifier.fillMaxWidth().height(60.dp), contentScale = ContentScale.FillBounds)
                } else {
                    Box(modifier = Modifier.fillMaxWidth().height(60.dp).background(Color.LightGray))
                }
                Spacer(modifier = Modifier.height(16.dp))
                Text(email, fontWeight = FontWeight.Bold, color = Color.Gray, letterSpacing = 2.sp)
            }
        }
    }
}

@Composable
fun CustomerHistoryTab(viewModel: LoyaltyViewModel) {
    val customer by viewModel.currentUser.collectAsStateWithLifecycle()
    val transactions by viewModel.allTransactions.collectAsStateWithLifecycle()
    
    val myTransactions = transactions.filter { it.userEmail == customer?.email }.sortedByDescending { it.timestamp }
    val dateFormat = java.text.SimpleDateFormat("MMM dd, yyyy HH:mm", java.util.Locale.getDefault())

    Column(modifier = Modifier.fillMaxSize().padding(24.dp)) {
        Text("Transaction History", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.ExtraBold, color = DarkGreen)
        Spacer(modifier = Modifier.height(16.dp))
        
        if (myTransactions.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("No recent transactions.", color = Color.Gray)
            }
        } else {
            LazyColumn(modifier = Modifier.fillMaxSize(), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                items(myTransactions) { tx ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                            Surface(
                                shape = CircleShape,
                                color = if (tx.pointChange > 0) LightGreenCard else Color(0xFFFFEBEE),
                                modifier = Modifier.size(48.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(
                                        if (tx.pointChange > 0) Icons.Default.ArrowUpward else Icons.Default.ArrowDownward,
                                        contentDescription = null,
                                        tint = if (tx.pointChange > 0) PrimaryGreen else Color(0xFFD32F2F)
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.width(16.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(tx.description, fontWeight = FontWeight.Bold, fontSize = 16.sp, color = DarkGreen)
                                Text(dateFormat.format(java.util.Date(tx.timestamp)), fontSize = 12.sp, color = Color.Gray)
                            }
                            Text(
                                text = "${if (tx.pointChange > 0) "+" else ""}${tx.pointChange}",
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 20.sp,
                                color = if (tx.pointChange > 0) PrimaryGreen else Color(0xFFD32F2F)
                            )
                        }
                    }
                }
            }
        }
    }
}
