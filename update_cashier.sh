#!/bin/bash
cat << 'INNER' > app/src/main/java/com/example/CashierScreen.kt
package com.example

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.QrCodeScanner
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
import com.example.ui.viewmodel.LoyaltyViewModel
import com.example.data.model.User
import kotlinx.coroutines.flow.emptyFlow
import androidx.compose.foundation.lazy.LazyColumn

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CashierScreen(viewModel: LoyaltyViewModel) {
    var scannedData by remember { mutableStateOf("") }
    var customerEmail by remember { mutableStateOf("") }
    var purchaseAmount by remember { mutableStateOf("") }
    var pointsToRedeem by remember { mutableStateOf("") }
    
    val pointSettings by viewModel.pointSettings.collectAsStateWithLifecycle()
    
    // We observe the scanned user based on the customerEmail
    val scannedUserFlow = remember(customerEmail) {
        if (customerEmail.isNotBlank()) viewModel.repository.getUser(customerEmail)
        else emptyFlow()
    }
    val scannedUser by scannedUserFlow.collectAsStateWithLifecycle(initialValue = null)

    val focusRequester = remember { FocusRequester() }

    LaunchedEffect(Unit) {
        try {
            focusRequester.requestFocus()
        } catch (e: Exception) {}
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("POS / Scanner", fontWeight = FontWeight.Bold, color = com.example.ui.theme.ExtraDarkGreen) },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White),
                actions = {
                    TextButton(onClick = { viewModel.logout() }) {
                        Text("Logout", color = Color.Red)
                    }
                }
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(padding).background(Color(0xFFFAFAFA)),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    border = androidx.compose.foundation.BorderStroke(1.dp, com.example.ui.theme.BorderSlate)
                ) {
                    Column(modifier = Modifier.padding(20.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.QrCodeScanner, contentDescription = null, tint = com.example.ui.theme.PrimaryGreen)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Scan Customer Code", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                        }
                        Spacer(modifier = Modifier.height(16.dp))
                        OutlinedTextField(
                            value = scannedData,
                            onValueChange = { scannedData = it },
                            label = { Text("Scanner Input (Barcode / QR)") },
                            modifier = Modifier.fillMaxWidth().focusRequester(focusRequester),
                            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                            keyboardActions = KeyboardActions(onDone = { 
                                customerEmail = scannedData.trim()
                                scannedData = "" 
                            }),
                            singleLine = true,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = com.example.ui.theme.PrimaryGreen
                            )
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Button(
                            onClick = { 
                                customerEmail = scannedData.trim()
                                scannedData = "" 
                            },
                            modifier = Modifier.fillMaxWidth(),
                            colors = ButtonDefaults.buttonColors(containerColor = com.example.ui.theme.PrimaryGreen)
                        ) {
                            Text("Confirm Scan")
                        }
                    }
                }
            }

            if (scannedUser != null) {
                val user = scannedUser!!
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
                            Text(user.email, color = Color.LightGray, fontSize = 14.sp)
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
            } else if (customerEmail.isNotBlank()) {
                item {
                    Text("No customer found with that ID.", color = Color.Red, modifier = Modifier.padding(16.dp))
                }
            }
        }
    }
}
INNER
