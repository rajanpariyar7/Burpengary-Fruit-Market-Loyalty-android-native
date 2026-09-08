#!/bin/bash
cat << 'INNER' > app/src/main/java/com/example/CashierScreen.kt
package com.example

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.components.ScannerCameraPreview
import com.example.ui.viewmodel.LoyaltyViewModel
import com.example.data.model.User
import kotlinx.coroutines.delay
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState

@OptIn(ExperimentalPermissionsApi::class, ExperimentalMaterial3Api::class)
@Composable
fun CashierScreen(viewModel: LoyaltyViewModel) {
    var searchQuery by remember { mutableStateOf("") }
    var selectedCustomer by remember { mutableStateOf<User?>(null) }
    var purchaseAmount by remember { mutableStateOf("") }
    var pointsToRedeem by remember { mutableStateOf("") }
    
    val pointSettings by viewModel.pointSettings.collectAsStateWithLifecycle()
    
    // Live search results
    val searchResultsFlow = remember(searchQuery) {
        if (searchQuery.isNotBlank() && searchQuery.length > 2) viewModel.searchUsers(searchQuery)
        else kotlinx.coroutines.flow.emptyFlow<List<User>>()
    }
    val searchResults by searchResultsFlow.collectAsStateWithLifecycle(initialValue = emptyList())
    
    val cameraPermissionState = rememberPermissionState(android.Manifest.permission.CAMERA)
    var isScanningMode by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Point of Sale", fontWeight = FontWeight.Bold, color = Color.White) },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = com.example.ui.theme.DarkGreen),
                actions = {
                    IconButton(onClick = { viewModel.logout() }) {
                        Icon(Icons.Default.Logout, contentDescription = "Logout", tint = Color.White)
                    }
                }
            )
        }
    ) { padding ->
        Column(modifier = Modifier.padding(padding).fillMaxSize().background(Color(0xFFFAFAFA))) {
            
            // Mode Toggle
            if (selectedCustomer == null) {
                TabRow(selectedTabIndex = if (isScanningMode) 1 else 0, containerColor = Color.White) {
                    Tab(
                        selected = !isScanningMode,
                        onClick = { isScanningMode = false },
                        text = { Text("Search Customer", fontWeight = FontWeight.Bold) }
                    )
                    Tab(
                        selected = isScanningMode,
                        onClick = { 
                            if (!cameraPermissionState.status.isGranted) {
                                cameraPermissionState.launchPermissionRequest()
                            }
                            isScanningMode = true
                        },
                        text = { Text("Scan QR / Barcode", fontWeight = FontWeight.Bold) }
                    )
                }
            }

            if (selectedCustomer == null) {
                if (isScanningMode) {
                    if (cameraPermissionState.status.isGranted) {
                        Box(modifier = Modifier.fillMaxWidth().weight(1f)) {
                            ScannerCameraPreview(onBarcodeScanned = { barcode ->
                                searchQuery = barcode
                                isScanningMode = false
                            })
                            Box(modifier = Modifier.fillMaxSize().background(Color.Black.copy(alpha = 0.5f))) {
                                Box(
                                    modifier = Modifier.align(Alignment.Center).size(250.dp).background(Color.Transparent, RoundedCornerShape(16.dp)).padding(2.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Surface(modifier = Modifier.fillMaxSize(), color = Color.Transparent, shape = RoundedCornerShape(16.dp), border = androidx.compose.foundation.BorderStroke(4.dp, Color.Green)) {}
                                }
                            }
                        }
                    } else {
                        Column(modifier = Modifier.fillMaxSize().padding(32.dp), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
                            Icon(Icons.Default.CameraAlt, contentDescription = null, modifier = Modifier.size(64.dp), tint = Color.Gray)
                            Spacer(modifier = Modifier.height(16.dp))
                            Text("Camera permission is required to scan.", color = Color.Gray)
                            Spacer(modifier = Modifier.height(16.dp))
                            Button(onClick = { cameraPermissionState.launchPermissionRequest() }) {
                                Text("Grant Permission")
                            }
                        }
                    }
                } else {
                    Column(modifier = Modifier.fillMaxSize().padding(20.dp)) {
                        OutlinedTextField(
                            value = searchQuery,
                            onValueChange = { searchQuery = it },
                            label = { Text("Search by Name, Phone, Email, or ID") },
                            modifier = Modifier.fillMaxWidth(),
                            leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                            singleLine = true
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        
                        LazyColumn(modifier = Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            items(searchResults) { user ->
                                Card(
                                    modifier = Modifier.fillMaxWidth().clickable {
                                        selectedCustomer = user
                                        searchQuery = ""
                                    },
                                    colors = CardDefaults.cardColors(containerColor = Color.White),
                                    border = androidx.compose.foundation.BorderStroke(1.dp, Color.LightGray)
                                ) {
                                    Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                                        Surface(shape = RoundedCornerShape(50), color = com.example.ui.theme.LightGreenCard, modifier = Modifier.size(40.dp)) {
                                            Box(contentAlignment = Alignment.Center) {
                                                Text(user.name.firstOrNull()?.toString() ?: "C", color = com.example.ui.theme.DarkGreen, fontWeight = FontWeight.Bold)
                                            }
                                        }
                                        Spacer(modifier = Modifier.width(16.dp))
                                        Column {
                                            Text(user.name, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                                            if (user.phone.isNotBlank()) Text(user.phone, fontSize = 12.sp, color = Color.Gray)
                                            else Text(user.email, fontSize = 12.sp, color = Color.Gray)
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            } else {
                // Customer Profile Dashboard
                LazyColumn(modifier = Modifier.fillMaxSize(), contentPadding = PaddingValues(20.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                    item {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                            TextButton(onClick = { selectedCustomer = null }) {
                                Icon(Icons.Default.ArrowBack, contentDescription = null)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Back to Search")
                            }
                        }
                    }

                    val user = selectedCustomer!!
                    val discountRate = pointSettings?.discountPer100Points ?: 1.0
                    val rewardValue = (user.points / 100.0) * discountRate
                    
                    item {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = com.example.ui.theme.ExtraDarkGreen)
                        ) {
                            Column(modifier = Modifier.padding(20.dp)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.CheckCircle, contentDescription = null, tint = Color.Green)
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text("Customer Identified", color = Color.White, fontWeight = FontWeight.Bold)
                                }
                                Spacer(modifier = Modifier.height(16.dp))
                                Text(user.name, color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                                Text(if (user.phone.isNotBlank()) user.phone else user.email, color = Color.LightGray, fontSize = 14.sp)
                                Spacer(modifier = Modifier.height(16.dp))
                                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                    Column {
                                        Text("POINTS", color = Color.Gray, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                        Text("${user.points}", color = Color.White, fontSize = 24.sp, fontWeight = FontWeight.Bold)
                                    }
                                    Column(horizontalAlignment = Alignment.End) {
                                        Text("REWARD VALUE", color = Color.Gray, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                        Text(String.format("$%.2f", rewardValue), color = Color(0xFFFF6D00), fontSize = 24.sp, fontWeight = FontWeight.Bold)
                                    }
                                }
                            }
                        }
                    }
                    item {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = Color.White),
                            border = androidx.compose.foundation.BorderStroke(1.dp, com.example.ui.theme.BorderSlate)
                        ) {
                            Column(modifier = Modifier.padding(20.dp)) {
                                Text("Process Purchase", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = com.example.ui.theme.DarkGreen)
                                Spacer(modifier = Modifier.height(16.dp))
                                OutlinedTextField(
                                    value = purchaseAmount,
                                    onValueChange = { purchaseAmount = it },
                                    label = { Text("Purchase Amount ($)") },
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                    modifier = Modifier.fillMaxWidth()
                                )
                                Spacer(modifier = Modifier.height(16.dp))
                                Button(
                                    onClick = {
                                         viewModel.processPurchase(user.email, purchaseAmount.toDoubleOrNull() ?: 0.0)
                                         purchaseAmount = ""
                                         // Optimistic update locally
                                         selectedCustomer = selectedCustomer?.copy(points = selectedCustomer!!.points + ((purchaseAmount.toDoubleOrNull() ?: 0.0) * (pointSettings?.pointsPerDollar ?: 10)).toInt())
                                    },
                                    modifier = Modifier.fillMaxWidth().height(56.dp),
                                    colors = ButtonDefaults.buttonColors(containerColor = com.example.ui.theme.PrimaryGreen)
                                ) {
                                    Text("Complete Purchase & Add Points")
                                }
                            }
                        }
                    }
                    item {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = Color.White),
                            border = androidx.compose.foundation.BorderStroke(1.dp, com.example.ui.theme.BorderSlate)
                        ) {
                            Column(modifier = Modifier.padding(20.dp)) {
                                Text("Redeem Points", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = com.example.ui.theme.DarkGreen)
                                Text("Minimum 100 points for discount.", fontSize = 12.sp, color = Color.Gray)
                                Spacer(modifier = Modifier.height(16.dp))
                                OutlinedTextField(
                                    value = pointsToRedeem,
                                    onValueChange = { pointsToRedeem = it },
                                    label = { Text("Points to Redeem (e.g. 100, 200)") },
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                    modifier = Modifier.fillMaxWidth()
                                )
                                Spacer(modifier = Modifier.height(16.dp))
                                Button(
                                    onClick = {
                                        val points = pointsToRedeem.toIntOrNull() ?: 0
                                        if (points > 0 && user.points >= points) {
                                            viewModel.redeemPointsCashier(user.email, points)
                                            pointsToRedeem = ""
                                            selectedCustomer = selectedCustomer?.copy(points = selectedCustomer!!.points - points)
                                        } else {
                                            viewModel.sendNotification("Error", "Invalid points or insufficient balance.")
                                        }
                                    },
                                    modifier = Modifier.fillMaxWidth().height(56.dp),
                                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF6D00))
                                ) {
                                    Text("Redeem & Apply Discount")
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
INNER
