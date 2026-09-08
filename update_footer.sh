#!/bin/bash
sed -i '/if (offers.isNotEmpty()) {/i \
        item { \
            Text(text = "Important Information", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = Color.Gray, modifier = Modifier.padding(horizontal = 20.dp, top = 24.dp)) \
            Text(text = "This application is a customer loyalty and membership platform for Burpengary Market & Grocery. Purchase processing and payment are not performed through this application. Loyalty points, rewards and promotions are subject to the applicable store terms and conditions.", style = MaterialTheme.typography.bodySmall, color = Color.Gray, modifier = Modifier.padding(horizontal = 20.dp)) \
        }' app/src/main/java/com/example/MainActivity.kt
