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
import androidx.compose.material.icons.outlined.Group
import androidx.compose.material.icons.outlined.History
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material.icons.outlined.Wallet
import androidx.compose.material.icons.outlined.ReceiptLong
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.viewmodel.LoyaltyViewModel
import com.example.data.model.Role
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SuperAdminDashboardScreen(viewModel: LoyaltyViewModel) {
    var selectedTab by remember { mutableStateOf(0) }
    
    Scaffold(
        bottomBar = {
            NavigationBar(
                containerColor = Color.White,
                contentColor = com.example.ui.theme.DarkGreen
            ) {
                NavigationBarItem(
                    icon = { Icon(Icons.Outlined.Settings, contentDescription = "Global Config") },
                    label = { Text("Config", fontSize = 10.sp) },
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = com.example.ui.theme.DarkGreen,
                        selectedTextColor = com.example.ui.theme.DarkGreen,
                        indicatorColor = com.example.ui.theme.LightGreenCard
                    )
                )
                NavigationBarItem(
                    icon = { Icon(Icons.Outlined.Group, contentDescription = "Roles") },
                    label = { Text("Roles", fontSize = 10.sp) },
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = com.example.ui.theme.DarkGreen,
                        selectedTextColor = com.example.ui.theme.DarkGreen,
                        indicatorColor = com.example.ui.theme.LightGreenCard
                    )
                )
                NavigationBarItem(
                    icon = { Icon(Icons.Outlined.ReceiptLong, contentDescription = "Transactions") },
                    label = { Text("Transactions", fontSize = 10.sp) },
                    selected = selectedTab == 2,
                    onClick = { selectedTab = 2 },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = com.example.ui.theme.DarkGreen,
                        selectedTextColor = com.example.ui.theme.DarkGreen,
                        indicatorColor = com.example.ui.theme.LightGreenCard
                    )
                )
                NavigationBarItem(
                    icon = { Icon(Icons.Outlined.History, contentDescription = "Audit Logs") },
                    label = { Text("Audit", fontSize = 10.sp) },
                    selected = selectedTab == 3,
                    onClick = { selectedTab = 3 },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = com.example.ui.theme.DarkGreen,
                        selectedTextColor = com.example.ui.theme.DarkGreen,
                        indicatorColor = com.example.ui.theme.LightGreenCard
                    )
                )
            }
        }
    ) { padding ->
        Box(modifier = Modifier.padding(padding).fillMaxSize().background(com.example.ui.theme.BackgroundLight)) {
            when (selectedTab) {
                0 -> SuperAdminSettingsTab(viewModel)
                1 -> SuperAdminUsersTab(viewModel)
                2 -> SuperAdminTransactionsTab(viewModel)
                3 -> SuperAdminAuditLogTab(viewModel)
            }
        }
    }
}

@Composable
fun SuperAdminSettingsTab(viewModel: LoyaltyViewModel) {
    val pointSettings by viewModel.pointSettings.collectAsStateWithLifecycle()
    
    var pointsPerDollar by remember(pointSettings) { mutableStateOf(pointSettings?.pointsPerDollar?.toString() ?: "10") }
    var redemptionThreshold by remember(pointSettings) { mutableStateOf(pointSettings?.redemptionThreshold?.toString() ?: "100") }
    var discountPer100 by remember(pointSettings) { mutableStateOf(pointSettings?.discountPer100Points?.toString() ?: "1.0") }
    var adminWriteEnabled by remember(pointSettings) { mutableStateOf(pointSettings?.adminWriteEnabled ?: false) }
    
    // Google Wallet Settings
    var issuerId by remember { mutableStateOf("3388000000000000000") }
    var issuerClass by remember { mutableStateOf("LoyaltyClass") }
    var serviceAccountJson by remember { mutableStateOf("") }
    
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Text("Global Configuration", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold, color = com.example.ui.theme.ExtraDarkGreen)
            Text("Control system-wide behavior, feature flags, and API keys.", color = Color.Gray, fontSize = 14.sp)
        }
        
        // Loyalty Rules & Feature Flags
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                border = androidx.compose.foundation.BorderStroke(1.dp, com.example.ui.theme.BorderSlate)
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text("Loyalty Rules & Feature Flags", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = com.example.ui.theme.DarkGreen, modifier = Modifier.padding(bottom = 16.dp))
                    
                    Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text("Allow Admin to Edit Settings", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = com.example.ui.theme.DarkGreen)
                            Text("If disabled, store managers (Admins) cannot modify point rules.", fontSize = 12.sp, color = Color.Gray)
                        }
                        Switch(
                            checked = adminWriteEnabled,
                            onCheckedChange = { adminWriteEnabled = it },
                            colors = SwitchDefaults.colors(checkedThumbColor = com.example.ui.theme.PrimaryGreen, checkedTrackColor = com.example.ui.theme.LightGreenCard)
                        )
                    }
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    OutlinedTextField(
                        value = pointsPerDollar,
                        onValueChange = { pointsPerDollar = it },
                        label = { Text("Points Earned per $1 Spent") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    OutlinedTextField(
                        value = redemptionThreshold,
                        onValueChange = { redemptionThreshold = it },
                        label = { Text("Minimum Points to Redeem") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    OutlinedTextField(
                        value = discountPer100,
                        onValueChange = { discountPer100 = it },
                        label = { Text("Discount Value per 100 Points ($)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.fillMaxWidth()
                    )
                    
                    Spacer(modifier = Modifier.height(24.dp))
                    Button(
                        onClick = {
                            val ppd = pointsPerDollar.toIntOrNull() ?: 10
                            val rt = redemptionThreshold.toIntOrNull() ?: 100
                            val dp100 = discountPer100.toDoubleOrNull() ?: 1.0
                            viewModel.updateSettings(ppd, dp100, rt, adminWriteEnabled)
                        },
                        modifier = Modifier.fillMaxWidth().height(50.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = com.example.ui.theme.PrimaryGreen)
                    ) {
                        Text("Save Loyalty Rules & Feature Flags")
                    }
                }
            }
        }
        
        // Google Wallet Settings
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                border = androidx.compose.foundation.BorderStroke(1.dp, com.example.ui.theme.BorderSlate)
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(bottom = 16.dp)) {
                        Icon(Icons.Outlined.Wallet, contentDescription = null, tint = com.example.ui.theme.PrimaryGreen)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Google Wallet API Settings", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = com.example.ui.theme.DarkGreen)
                    }
                    
                    Text("Configure credentials to allow users to add loyalty cards to Google Wallet or Apple Wallet.", fontSize = 12.sp, color = Color.Gray, modifier = Modifier.padding(bottom = 16.dp))

                    OutlinedTextField(
                        value = issuerId,
                        onValueChange = { issuerId = it },
                        label = { Text("Issuer ID") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    OutlinedTextField(
                        value = issuerClass,
                        onValueChange = { issuerClass = it },
                        label = { Text("Issuer Class") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    OutlinedTextField(
                        value = serviceAccountJson,
                        onValueChange = { serviceAccountJson = it },
                        label = { Text("Service Account JSON") },
                        modifier = Modifier.fillMaxWidth(),
                        minLines = 4,
                        maxLines = 8
                    )
                    
                    Spacer(modifier = Modifier.height(24.dp))
                    Button(
                        onClick = {
                            // Dummy save for now
                        },
                        modifier = Modifier.fillMaxWidth().height(50.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = com.example.ui.theme.DarkGreen)
                    ) {
                        Text("Save Wallet Configuration")
                    }
                }
            }
            Spacer(modifier = Modifier.height(40.dp))
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SuperAdminUsersTab(viewModel: LoyaltyViewModel) {
    val allUsers by viewModel.allUsers.collectAsStateWithLifecycle()
    
    var showRoleDialog by remember { mutableStateOf(false) }
    var selectedUserEmail by remember { mutableStateOf("") }
    var selectedUserRole by remember { mutableStateOf(Role.CUSTOMER) }
    
    if (showRoleDialog) {
        AlertDialog(
            onDismissRequest = { showRoleDialog = false },
            title = { Text("Change Role & Permissions") },
            text = {
                Column {
                    Text("Select a new role for $selectedUserEmail:")
                    Spacer(modifier = Modifier.height(16.dp))
                    Role.values().forEach { role ->
                        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth().clickable { selectedUserRole = role }.padding(vertical = 8.dp)) {
                            RadioButton(
                                selected = selectedUserRole == role, 
                                onClick = { selectedUserRole = role },
                                colors = RadioButtonDefaults.colors(selectedColor = com.example.ui.theme.PrimaryGreen)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(role.name)
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { 
                    viewModel.changeUserRole(selectedUserEmail, selectedUserRole)
                    showRoleDialog = false
                }) { Text("Confirm", color = com.example.ui.theme.PrimaryGreen, fontWeight = FontWeight.Bold) }
            },
            dismissButton = {
                TextButton(onClick = { showRoleDialog = false }) { Text("Cancel", color = Color.Gray) }
            },
            containerColor = Color.White
        )
    }

    Column(modifier = Modifier.fillMaxSize().padding(20.dp)) {
        Text("Role & Permissions", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold, color = com.example.ui.theme.ExtraDarkGreen)
        Text("Manage all accounts and their system roles.", color = Color.Gray, fontSize = 14.sp)
        Spacer(modifier = Modifier.height(16.dp))
        
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(allUsers) { user ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    border = androidx.compose.foundation.BorderStroke(1.dp, com.example.ui.theme.BorderSlate)
                ) {
                    Row(modifier = Modifier.fillMaxWidth().padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                        Surface(shape = RoundedCornerShape(50), color = com.example.ui.theme.BackgroundLight, modifier = Modifier.size(48.dp)) {
                            Box(contentAlignment = Alignment.Center) {
                                Text(user.name.firstOrNull()?.toString() ?: "U", fontWeight = FontWeight.Bold, color = com.example.ui.theme.DarkGreen, fontSize = 20.sp)
                            }
                        }
                        Spacer(modifier = Modifier.width(16.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(user.name, fontWeight = FontWeight.Bold, fontSize = 16.sp, color = com.example.ui.theme.DarkGreen)
                            Text(user.email, color = Color.Gray, fontSize = 12.sp)
                        }
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = when (user.role) {
                                Role.SUPER_ADMIN -> Color.Black
                                Role.ADMIN -> com.example.ui.theme.PrimaryGreen
                                Role.CASHIER -> com.example.ui.theme.AccentOrange
                                Role.CUSTOMER -> Color.LightGray
                            },
                            modifier = Modifier.clickable {
                                selectedUserEmail = user.email
                                selectedUserRole = user.role
                                showRoleDialog = true
                            }
                        ) {
                            Text(user.role.name, color = if (user.role == Role.CUSTOMER) Color.DarkGray else Color.White, fontSize = 10.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp))
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun SuperAdminTransactionsTab(viewModel: LoyaltyViewModel) {
    val transactions by viewModel.allTransactions.collectAsStateWithLifecycle()
    val dateFormat = SimpleDateFormat("MMM dd, yyyy HH:mm", Locale.getDefault())

    Column(modifier = Modifier.fillMaxSize().padding(20.dp)) {
        Text("Point Transactions", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold, color = com.example.ui.theme.ExtraDarkGreen)
        Text("Track points added and redeemed by cashiers.", color = Color.Gray, fontSize = 14.sp)
        Spacer(modifier = Modifier.height(16.dp))
        
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(transactions) { tx ->
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
                            Text(tx.description, fontWeight = FontWeight.Bold, fontSize = 14.sp, color = com.example.ui.theme.DarkGreen)
                            Text(tx.userEmail, fontSize = 12.sp, color = Color.Gray)
                        }
                        Column(horizontalAlignment = Alignment.End) {
                            Text(
                                text = "${if (tx.pointChange > 0) "+" else ""}${tx.pointChange}",
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 18.sp,
                                color = if (tx.pointChange > 0) com.example.ui.theme.PrimaryGreen else Color(0xFFD32F2F)
                            )
                            Text(dateFormat.format(Date(tx.timestamp)), fontSize = 10.sp, color = Color.Gray)
                        }
                    }
                }
            }
            if (transactions.isEmpty()) {
                item {
                    Text("No transactions recorded yet.", color = Color.Gray, modifier = Modifier.padding(16.dp))
                }
            }
        }
    }
}

@Composable
fun SuperAdminAuditLogTab(viewModel: LoyaltyViewModel) {
    val auditLogs by viewModel.auditLogs.collectAsStateWithLifecycle(emptyList())
    val dateFormat = SimpleDateFormat("MMM dd, yyyy HH:mm:ss", Locale.getDefault())

    Column(modifier = Modifier.fillMaxSize().padding(20.dp)) {
        Text("System Audit Logs", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold, color = com.example.ui.theme.ExtraDarkGreen)
        Text("Immutable trail of setting and role changes.", color = Color.Gray, fontSize = 14.sp)
        Spacer(modifier = Modifier.height(16.dp))
        
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(auditLogs) { log ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    border = androidx.compose.foundation.BorderStroke(1.dp, com.example.ui.theme.BorderSlate)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Action:", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = Color.Gray)
                            Text(dateFormat.format(Date(log.timestamp)), fontSize = 12.sp, color = Color.Gray)
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(log.action, fontWeight = FontWeight.Bold, fontSize = 14.sp, color = com.example.ui.theme.ExtraDarkGreen)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text("By: ${log.changedBy}", fontSize = 12.sp, color = com.example.ui.theme.MediumGreen)
                    }
                }
            }
            if (auditLogs.isEmpty()) {
                item {
                    Text("No logs recorded yet.", color = Color.Gray, modifier = Modifier.padding(16.dp))
                }
            }
        }
    }
}
