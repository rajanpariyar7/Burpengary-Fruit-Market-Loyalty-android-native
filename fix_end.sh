#!/bin/bash
sed -i '150,$d' app/src/main/java/com/example/ui/viewmodel/LoyaltyViewModel.kt
cat << 'INNER' >> app/src/main/java/com/example/ui/viewmodel/LoyaltyViewModel.kt
    fun redeemPointsCashier(email: String, points: Int) {
        viewModelScope.launch {
            val settings = repository.pointSettings.value
            repository.redeemPoints(email, points)
            repository.insertTransaction(PointTransaction(userEmail = email, description = "Point redemption at register", pointChange = -points))
            _notifications.emit(Pair("Success", "Redeemed $points points for $email"))
        }
    }
    
    fun updateSettings(pointsPerDollar: Int, discountRate: Double) {
        viewModelScope.launch {
            repository.updateSettings(PointSettings(pointsPerDollar = pointsPerDollar, discountPer100Points = discountRate))
        }
    }

    fun createCashier(email: String, name: String, pass: String) {
        viewModelScope.launch {
            if (repository.getUserSync(email) == null) {
                repository.createUser(com.example.data.model.User(
                    email = email,
                    passwordHash = pass,
                    name = name,
                    role = com.example.data.model.Role.CASHIER
                ))
                _notifications.emit(Pair("Success", "Cashier $name created."))
            } else {
                _notifications.emit(Pair("Error", "User with email already exists."))
            }
        }
    }
}
INNER
