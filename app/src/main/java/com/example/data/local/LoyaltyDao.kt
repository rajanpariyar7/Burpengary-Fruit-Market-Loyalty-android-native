package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Delete
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.model.User
import com.example.data.model.Reward
import com.example.data.model.Offer
import com.example.data.model.PointSettings
import com.example.data.model.PointTransaction
import com.example.data.model.AuditLog
import kotlinx.coroutines.flow.Flow

@Dao
interface LoyaltyDao {
    @Query("SELECT * FROM transactions WHERE userEmail = :email ORDER BY timestamp DESC")
    fun getTransactions(email: String): Flow<List<PointTransaction>>

    @Query("SELECT * FROM transactions ORDER BY timestamp DESC")
    fun getAllTransactions(): Flow<List<PointTransaction>>
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTransaction(transaction: PointTransaction)

    @Query("SELECT * FROM categories")
    fun getCategories(): kotlinx.coroutines.flow.Flow<List<com.example.data.model.Category>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCategory(category: com.example.data.model.Category)

    @Delete
    suspend fun deleteCategory(category: com.example.data.model.Category)

    @Delete
    suspend fun deleteOffer(offer: com.example.data.model.Offer)

    @Query("SELECT * FROM users WHERE email LIKE '%' || :query || '%' OR name LIKE '%' || :query || '%' OR phone LIKE '%' || :query || '%' OR customerId LIKE '%' || :query || '%'")
    fun searchUsers(query: String): Flow<List<User>>

    @Query("SELECT * FROM users WHERE email = :email LIMIT 1")
    fun getUser(email: String): Flow<User?>
    
    @Query("SELECT * FROM users WHERE email = :email LIMIT 1")
    suspend fun getUserSync(email: String): User?
    
    @Query("SELECT * FROM users WHERE role = 'CUSTOMER'")
    fun getAllCustomers(): Flow<List<User>>

    @Query("SELECT COUNT(*) FROM users WHERE role = 'CUSTOMER'")
    suspend fun getCustomerCount(): Int

    @Query("SELECT * FROM users")
    fun getAllUsers(): Flow<List<User>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(user: User)

    @Update
    suspend fun updateUser(user: User)

    @Query("SELECT * FROM rewards")
    fun getRewards(): Flow<List<Reward>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRewards(rewards: List<Reward>)

    @Update
    suspend fun updateReward(reward: Reward)
    
    @Query("SELECT * FROM offers")
    fun getOffers(): Flow<List<Offer>>
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOffer(offer: Offer)
    
    @Query("DELETE FROM offers WHERE id = :id")
    suspend fun deleteOffer(id: Int)
    
    @Query("SELECT * FROM point_settings WHERE id = 1 LIMIT 1")
    fun getPointSettings(): Flow<PointSettings?>
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPointSettings(settings: PointSettings)

    @Query("SELECT * FROM audit_logs ORDER BY timestamp DESC")
    fun getAuditLogs(): Flow<List<AuditLog>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAuditLog(log: AuditLog)
}
