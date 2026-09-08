#!/bin/bash
START_LINE=$(grep -n "fun OfferCard" app/src/main/java/com/example/MainActivity.kt | cut -d: -f1)
END_LINE=$(grep -n "fun RewardCard" app/src/main/java/com/example/MainActivity.kt | cut -d: -f1)
sed -i "$((START_LINE)),$((END_LINE-1))d" app/src/main/java/com/example/MainActivity.kt

cat << 'INNER' >> app/src/main/java/com/example/MainActivity.kt

@Composable
fun OfferCard(offer: Offer) {
    Card(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 4.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = androidx.compose.foundation.BorderStroke(1.dp, com.example.ui.theme.BorderSlate),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            if (offer.imageUrl != null) {
                coil.compose.AsyncImage(
                    model = offer.imageUrl,
                    contentDescription = offer.title,
                    modifier = Modifier.fillMaxWidth().height(160.dp).background(Color.LightGray),
                    contentScale = androidx.compose.ui.layout.ContentScale.Crop
                )
            }
            Column(modifier = Modifier.fillMaxWidth().padding(20.dp)) {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                    Text(text = offer.title, style = MaterialTheme.typography.titleMedium, color = com.example.ui.theme.ExtraDarkGreen, fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f))
                    Text(text = offer.price, style = MaterialTheme.typography.titleMedium, color = com.example.ui.theme.MediumGreen, fontWeight = FontWeight.Bold)
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(text = offer.category.uppercase(), style = MaterialTheme.typography.labelSmall, color = Color.Gray)
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
INNER
