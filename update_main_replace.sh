#!/bin/bash
sed -i 's/CustomerScreen(viewModel)/CustomerDashboardScreen(viewModel)/g' app/src/main/java/com/example/MainActivity.kt

# Find line number of "fun CustomerScreen"
START_LINE=$(grep -n "fun CustomerScreen" app/src/main/java/com/example/MainActivity.kt | cut -d: -f1)
# Find line number of "fun TransactionCard"
END_LINE=$(grep -n "fun TransactionCard" app/src/main/java/com/example/MainActivity.kt | cut -d: -f1)
# Delete everything from @Composable before CustomerScreen to just before @Composable of TransactionCard
sed -i "$((START_LINE-1)),$((END_LINE-2))d" app/src/main/java/com/example/MainActivity.kt
