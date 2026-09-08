package com.example.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.data.model.User
import com.example.data.model.Reward
import com.example.data.model.Offer
import com.example.data.model.PointSettings
import com.example.data.model.PointTransaction
import com.example.data.model.AuditLog
import com.example.data.model.Category

@Database(entities = [User::class, Reward::class, Offer::class, PointSettings::class, PointTransaction::class, AuditLog::class, Category::class], version = 7, exportSchema = false)
abstract class LoyaltyDatabase : RoomDatabase() {
    abstract fun loyaltyDao(): LoyaltyDao
}
