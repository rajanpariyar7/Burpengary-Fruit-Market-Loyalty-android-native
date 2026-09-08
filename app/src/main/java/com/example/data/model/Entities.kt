package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

enum class Role {
    SUPER_ADMIN, ADMIN, CASHIER, CUSTOMER
}

@Entity(tableName = "users")
data class User(
    @PrimaryKey val email: String,
    val passwordHash: String,
    val name: String,
    val role: Role,
    val stamps: Int = 0,
    val points: Int = 0,
    val lifetimeStamps: Int = 0,
    val phone: String = "",
    val customerId: String = ""
)

@Entity(tableName = "rewards")
data class Reward(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val title: String,
    val description: String,
    val costInPoints: Int = 0,
    val costInStamps: Int = 0,
    val isRedeemed: Boolean = false,
    val userEmail: String? = null
)

@Entity(tableName = "offers")
data class Offer(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val title: String,
    val price: String,
    val category: String = "General",
    val description: String,
    val imageUrl: String? = null
)

@Entity(tableName = "point_settings")
data class PointSettings(
    @PrimaryKey val id: Int = 1,
    val pointsPerDollar: Int = 10,
    val discountPer100Points: Double = 1.0,
    val redemptionThreshold: Int = 100,
    val adminWriteEnabled: Boolean = true
)

@Entity(tableName = "transactions")
data class PointTransaction(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val userEmail: String,
    val description: String,
    val pointChange: Int,
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "audit_logs")
data class AuditLog(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val action: String,
    val changedBy: String,
    val timestamp: Long = System.currentTimeMillis()
)
@Entity(tableName = "categories")
data class Category(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String
)
