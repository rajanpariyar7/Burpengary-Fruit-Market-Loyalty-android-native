#!/bin/bash
sed -i 's/val settings = repository.pointSettings.value/val settings = kotlinx.coroutines.flow.firstOrNull(repository.pointSettings)/g' app/src/main/java/com/example/ui/viewmodel/LoyaltyViewModel.kt
