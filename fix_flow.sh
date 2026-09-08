#!/bin/bash
sed -i 's/val settings = kotlinx.coroutines.flow.first(repository.pointSettings)/val settings = kotlinx.coroutines.flow.firstOrNull(repository.pointSettings)/g' app/src/main/java/com/example/ui/viewmodel/LoyaltyViewModel.kt
sed -i '1i import kotlinx.coroutines.flow.firstOrNull\n' app/src/main/java/com/example/ui/viewmodel/LoyaltyViewModel.kt
