package com.example.data.model

enum class Role {
    SUPER_ADMIN, ADMIN, CASHIER, CUSTOMER
}

data class User(
    val email: String = "",
    val passwordHash: String = "",
    val name: String = "",
    val role: Role = Role.CUSTOMER,
    val stamps: Int = 0,
    val points: Int = 0,
    val lifetimeStamps: Int = 0,
    val phone: String = "",
    val customerId: String = ""
)

data class Reward(
    val id: String = "",
    val title: String = "",
    val description: String = "",
    val costInPoints: Int = 0,
    val costInStamps: Int = 0,
    val isRedeemed: Boolean = false,
    val userEmail: String? = null
)

data class Offer(
    val id: String = "",
    val title: String = "",
    val price: String = "",
    val category: String = "General",
    val description: String = "",
    val imageUrl: String? = null
)

data class PointSettings(
    val id: Int = 1,
    val pointsPerDollar: Int = 10,
    val discountPer100Points: Double = 1.0,
    val redemptionThreshold: Int = 100,
    val adminWriteEnabled: Boolean = true
)

data class PointTransaction(
    val id: String = "",
    val userEmail: String = "",
    val description: String = "",
    val pointChange: Int = 0,
    val timestamp: Long = System.currentTimeMillis()
)

data class AuditLog(
    val id: String = "",
    val action: String = "",
    val changedBy: String = "",
    val timestamp: Long = System.currentTimeMillis()
)

data class Category(
    val id: String = "",
    val name: String = ""
)
