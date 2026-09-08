#!/bin/bash
sed -i 's/if (customerPoints >= 100) {/val threshold = pointSettings?.redemptionThreshold ?: 100\n                            if (customerPoints >= threshold) {/g' app/src/main/java/com/example/CustomerDashboardScreen.kt
sed -i 's/Text("Minimum 100 pts for discount", fontSize = 9.sp, color = Color.Gray)/Text("Minimum ${pointSettings?.redemptionThreshold ?: 100} pts for discount", fontSize = 9.sp, color = Color.Gray)/g' app/src/main/java/com/example/CustomerDashboardScreen.kt
