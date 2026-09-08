#!/bin/bash
sed -i '/fun createCashier/i \
    fun getUserFlow(email: String): kotlinx.coroutines.flow.Flow<com.example.data.model.User?> { \
        return repository.getUser(email) \
    }\
' app/src/main/java/com/example/ui/viewmodel/LoyaltyViewModel.kt
