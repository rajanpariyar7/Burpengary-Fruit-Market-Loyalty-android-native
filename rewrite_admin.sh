#!/bin/bash
# Remove old AdminDashboard
START_LINE=$(grep -n "fun AdminDashboard" app/src/main/java/com/example/MainActivity.kt | cut -d: -f1)
END_LINE=$(grep -n "fun CashierScreen" app/src/main/java/com/example/MainActivity.kt | cut -d: -f1)
sed -i "$((START_LINE-1)),$((END_LINE-2))d" app/src/main/java/com/example/MainActivity.kt
