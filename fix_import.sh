#!/bin/bash
sed -i '1d' app/src/main/java/com/example/ui/viewmodel/LoyaltyViewModel.kt
sed -i '2i import kotlinx.coroutines.flow.firstOrNull' app/src/main/java/com/example/ui/viewmodel/LoyaltyViewModel.kt
