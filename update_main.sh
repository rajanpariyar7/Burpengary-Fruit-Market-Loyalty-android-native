#!/bin/bash
head -n 342 app/src/main/java/com/example/MainActivity.kt > temp_main.kt
cat CustomerScreen.kt >> temp_main.kt
cat << 'INNER_EOF' >> temp_main.kt

@Composable
fun OfferCard(offer: Offer) {
    Card(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 4.dp),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = androidx.compose.foundation.BorderStroke(1.dp, com.example.ui.theme.BorderSlate),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(modifier = Modifier.fillMaxWidth().padding(20.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Text(text = offer.title, style = MaterialTheme.typography.titleMedium, color = com.example.ui.theme.ExtraDarkGreen, fontWeight = FontWeight.Bold)
                Text(text = offer.price, style = MaterialTheme.typography.titleMedium, color = com.example.ui.theme.MediumGreen, fontWeight = FontWeight.Bold)
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = offer.description, style = MaterialTheme.typography.bodyMedium, color = Color.Gray)
        }
    }
}

@Composable
fun RewardCard(reward: Reward, customerPoints: Int, customerStamps: Int, onRedeem: () -> Unit) {
    val canRedeem = (reward.costInStamps > 0 && customerStamps >= reward.costInStamps) ||
                    (reward.costInPoints > 0 && customerPoints >= reward.costInPoints)
    Card(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 4.dp),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = androidx.compose.foundation.BorderStroke(1.dp, com.example.ui.theme.BorderSlate),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Row(modifier = Modifier.fillMaxWidth().padding(20.dp), verticalAlignment = Alignment.CenterVertically) {
            Column(modifier = Modifier.weight(1f)) {
                Text(text = reward.title, style = MaterialTheme.typography.titleMedium, color = com.example.ui.theme.ExtraDarkGreen, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(4.dp))
                Text(text = reward.description, style = MaterialTheme.typography.bodyMedium, color = Color.Gray)
                Spacer(modifier = Modifier.height(8.dp))
                val costText = if (reward.costInStamps > 0) "${reward.costInStamps} Stamps" else "${reward.costInPoints} Points"
                Text(text = "Cost: $costText", style = MaterialTheme.typography.labelLarge, color = com.example.ui.theme.MediumGreen, fontWeight = FontWeight.Bold)
            }
            Spacer(modifier = Modifier.width(16.dp))
            Button(
                onClick = onRedeem,
                enabled = canRedeem,
                colors = ButtonDefaults.buttonColors(containerColor = com.example.ui.theme.PrimaryGreen, disabledContainerColor = com.example.ui.theme.BorderSlate, disabledContentColor = Color.Gray),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Redeem")
            }
        }
    }
}
INNER_EOF
mv temp_main.kt app/src/main/java/com/example/MainActivity.kt
