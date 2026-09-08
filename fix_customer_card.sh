#!/bin/bash
cat << 'INNER' > patch.txt
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
                        Text("🎉", fontSize = 24.sp)
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
INNER
sed -i '/Spacer(modifier = Modifier.height(24.dp))/!b;n;n;r patch.txt' app/src/main/java/com/example/CustomerDashboardScreen.kt
