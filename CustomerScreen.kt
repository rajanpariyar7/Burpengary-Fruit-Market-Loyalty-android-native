@Composable
fun CustomerScreen(viewModel: LoyaltyViewModel) {
    val customer by viewModel.currentUser.collectAsStateWithLifecycle()
    val rewards by viewModel.rewards.collectAsStateWithLifecycle()
    val offers by viewModel.offers.collectAsStateWithLifecycle()
    val transactions by viewModel.currentTransactions.collectAsStateWithLifecycle()

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(com.example.ui.theme.BackgroundLight),
        contentPadding = PaddingValues(top = 16.dp, bottom = 120.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        item {
            Text(text = "Your Loyalty Dashboard", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold, color = com.example.ui.theme.ExtraDarkGreen, modifier = Modifier.padding(horizontal = 20.dp, vertical = 8.dp))
        }

        item {
            Text(text = "Your Digital Membership Card", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, color = com.example.ui.theme.ExtraDarkGreen, modifier = Modifier.padding(horizontal = 20.dp, bottom = 8.dp))
            Card(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                border = androidx.compose.foundation.BorderStroke(1.dp, com.example.ui.theme.BorderSlate),
                elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
            ) {
                Column(modifier = Modifier.fillMaxWidth().padding(24.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(imageVector = Icons.Default.QrCode, contentDescription = "QR Code", modifier = Modifier.size(120.dp), tint = com.example.ui.theme.DarkGreen)
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(text = customer?.name ?: "Customer", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    Text(text = customer?.email ?: "", style = MaterialTheme.typography.bodyMedium, color = Color.Gray)
                }
            }
        }

        item {
            Card(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp),
                shape = RoundedCornerShape(32.dp),
                colors = CardDefaults.cardColors(containerColor = com.example.ui.theme.LightGreenCard),
                elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
            ) {
                Box(modifier = Modifier.fillMaxWidth().padding(24.dp)) {
                    Box(modifier = Modifier.size(128.dp).align(Alignment.TopEnd).offset(x = 32.dp, y = (-32).dp).background(Color(0xFFB7E4C7).copy(alpha = 0.5f), shape = RoundedCornerShape(50)))
                    Column(modifier = Modifier.fillMaxWidth()) {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.Top) {
                            Column {
                                Text(text = "CURRENT POINTS", color = com.example.ui.theme.DarkGreen.copy(alpha = 0.8f), fontSize = 14.sp, fontWeight = FontWeight.Medium, letterSpacing = 1.sp)
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(text = "${customer?.points ?: 0}", color = com.example.ui.theme.ExtraDarkGreen, fontSize = 36.sp, fontWeight = FontWeight.Black)
                            }
                            Surface(color = Color.White.copy(alpha = 0.6f), border = androidx.compose.foundation.BorderStroke(1.dp, Color.White.copy(alpha = 0.4f)), shape = RoundedCornerShape(16.dp)) {
                                Text(text = "GOLD TIER", modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp), fontSize = 11.sp, fontWeight = FontWeight.Bold, color = com.example.ui.theme.DarkGreen)
                            }
                        }
                    }
                }
            }
        }

        item {
            val totalStamps = customer?.stamps ?: 0
            val displayStamps = if (totalStamps > 0 && totalStamps % 10 == 0) 10 else totalStamps % 10
            DigitalPunchCard(stamps = displayStamps)
        }
        
        item {
            Text(text = "Track Your Loyalty Points", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, color = com.example.ui.theme.ExtraDarkGreen, modifier = Modifier.padding(horizontal = 20.dp, top = 8.dp))
        }

        if (transactions.isEmpty()) {
            item { Text(text = "No recent activity.", modifier = Modifier.padding(horizontal = 20.dp), color = Color.Gray) }
        } else {
            items(transactions.take(3)) { transaction ->
                TransactionCard(transaction)
            }
            item {
                Text(text = "View all activity", style = MaterialTheme.typography.labelLarge, color = com.example.ui.theme.PrimaryGreen, modifier = Modifier.padding(horizontal = 20.dp))
            }
        }

        item {
            Text(text = "Turn Points Into Rewards", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, color = com.example.ui.theme.ExtraDarkGreen, modifier = Modifier.padding(horizontal = 20.dp, top = 8.dp))
        }

        if (rewards.isEmpty()) {
            item { Text(text = "No rewards available at the moment.", modifier = Modifier.padding(horizontal = 20.dp), color = Color.Gray) }
        } else {
            items(rewards) { reward ->
                RewardCard(
                    reward = reward,
                    customerPoints = customer?.points ?: 0,
                    customerStamps = customer?.stamps ?: 0,
                    onRedeem = { viewModel.redeemReward(reward) }
                )
            }
        }

        if (offers.isNotEmpty()) {
            item {
                Text(text = "Discover Offers & Promotions", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, color = com.example.ui.theme.ExtraDarkGreen, modifier = Modifier.padding(horizontal = 20.dp, top = 8.dp))
            }
            items(offers) { offer ->
                OfferCard(offer)
            }
        }
    }
}

@Composable
fun TransactionCard(transaction: com.example.data.model.PointTransaction) {
    Card(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 2.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Row(modifier = Modifier.fillMaxWidth().padding(16.dp), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
            Column(modifier = Modifier.weight(1f)) {
                Text(text = transaction.description, style = MaterialTheme.typography.bodyLarge, color = com.example.ui.theme.ExtraDarkGreen, fontWeight = FontWeight.Medium)
                Text(text = java.text.SimpleDateFormat("MMM dd, yyyy", java.util.Locale.getDefault()).format(java.util.Date(transaction.timestamp)), style = MaterialTheme.typography.bodySmall, color = Color.Gray)
            }
            val isPositive = transaction.pointChange >= 0
            val color = if (isPositive) com.example.ui.theme.PrimaryGreen else Color.Red
            val sign = if (isPositive) "+" else ""
            Text(text = "$sign${transaction.pointChange}", style = MaterialTheme.typography.titleMedium, color = color, fontWeight = FontWeight.Bold)
        }
    }
}
