package com.example.data.repository

import com.example.data.local.LoyaltyDao
import com.example.data.model.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull

class LoyaltyRepository(private val dao: LoyaltyDao) {
    val rewards: Flow<List<Reward>> = dao.getRewards()
    val offers: Flow<List<Offer>> = dao.getOffers()
    val pointSettings: Flow<PointSettings?> = dao.getPointSettings()
    val allUsers: Flow<List<User>> = dao.getAllUsers()
    val auditLogs: Flow<List<AuditLog>> = dao.getAuditLogs()
    val categories: Flow<List<com.example.data.model.Category>> = dao.getCategories()

    suspend fun insertCategory(name: String) {
        dao.insertCategory(com.example.data.model.Category(name = name))
    }
    suspend fun deleteCategory(category: com.example.data.model.Category) = dao.deleteCategory(category)
    suspend fun deleteOffer(offer: com.example.data.model.Offer) = dao.deleteOffer(offer)

    fun searchUsers(query: String): Flow<List<User>> = dao.searchUsers(query)
    fun getUser(email: String): Flow<User?> = dao.getUser(email)
    suspend fun getUserSync(email: String): User? = dao.getUserSync(email)
    suspend fun getCustomerCount(): Int = dao.getCustomerCount()
    
    fun getAllCustomers(): Flow<List<User>> = dao.getAllCustomers()
    fun getTransactions(email: String): Flow<List<PointTransaction>> = dao.getTransactions(email)
    fun getAllTransactions(): Flow<List<PointTransaction>> = dao.getAllTransactions()
    
    suspend fun insertTransaction(transaction: PointTransaction) {
        dao.insertTransaction(transaction)
    }

    suspend fun logAudit(action: String, byEmail: String) {
        dao.insertAuditLog(AuditLog(action = action, changedBy = byEmail))
    }

    suspend fun initializeDb() {
        if (dao.getPointSettings().firstOrNull() == null) {
            dao.insertPointSettings(PointSettings())
        }
        if (dao.getUserSync("rajanpariyar.com.np@gmail.com") == null) {
            dao.insertUser(User(
                email = "rajanpariyar.com.np@gmail.com",
                passwordHash = "Burp@login4testbyGoogle@26",
                name = "Super Admin",
                role = Role.SUPER_ADMIN
            ))
            
            dao.insertUser(User(
                email = "admin@example.com",
                passwordHash = "password",
                name = "Store Manager",
                role = Role.ADMIN
            ))
            
            dao.insertUser(User(
                email = "customer@example.com",
                passwordHash = "password",
                name = "Customer One",
                role = Role.CUSTOMER,
                points = 100,
                stamps = 2
            ))
            
            dao.insertUser(User(
                email = "cashier@example.com",
                passwordHash = "password",
                name = "Market Cashier",
                role = Role.CASHIER
            ))

            dao.insertRewards(
                listOf(
                    Reward(title = "Free Coffee", description = "Get a free medium coffee at the market cafe.", costInStamps = 10),
                    Reward(title = "$5 Off Produce", description = "Get $5 off your next fresh produce purchase.", costInPoints = 500),
                    Reward(title = "Reusable Tote Bag", description = "Redeem for a Burpengary Market canvas tote bag.", costInPoints = 1000)
                )
            )
            
            dao.insertOffer(Offer(title = "Fresh Strawberries", price = "$1.99", category = "FRUITS", description = "OFFER: Amazing value! $1.99 per punnet!", imageUrl = null))
            dao.insertOffer(Offer(title = "Local Cavendish Bananas", price = "$1.49", category = "FRUITS", description = "Fresh locally sourced bananas.", imageUrl = null))
            dao.insertOffer(Offer(title = "Free Range Eggs 12pk", price = "$4.99", category = "GROCERY", description = "Locally sourced.", imageUrl = null))
            
            // Add some mock transactions for customer
            dao.insertTransaction(PointTransaction(userEmail = "customer@example.com", description = "Purchase: $5.0 by cashier cashier@example.com", pointChange = 50))
            dao.insertTransaction(PointTransaction(userEmail = "customer@example.com", description = "Register Redemption by cashier@example.com", pointChange = -20))
        }
    }
    
    suspend fun createUser(user: User) {
        dao.insertUser(user)
    }
    
    suspend fun updateUser(user: User) {
        dao.insertUser(user)
    }

    suspend fun addStamp(email: String) {
        val current = dao.getUserSync(email) ?: return
        val newStamps = current.stamps + 1
        val newLifetime = current.lifetimeStamps + 1
        dao.updateUser(current.copy(stamps = newStamps, lifetimeStamps = newLifetime))
    }

    suspend fun resetStamps(email: String) {
        val current = dao.getUserSync(email) ?: return
        dao.updateUser(current.copy(stamps = 0))
    }

    suspend fun addPoints(email: String, points: Int) {
        val current = dao.getUserSync(email) ?: return
        dao.updateUser(current.copy(points = current.points + points))
    }
    
    suspend fun addOffer(offer: Offer) {
        dao.insertOffer(offer)
    }

    suspend fun redeemReward(email: String, reward: Reward) {
        val current = dao.getUserSync(email) ?: return
        if (reward.costInStamps > 0 && current.stamps >= reward.costInStamps) {
            dao.updateUser(current.copy(stamps = current.stamps - reward.costInStamps))
        } else if (reward.costInPoints > 0 && current.points >= reward.costInPoints) {
            dao.updateUser(current.copy(points = current.points - reward.costInPoints))
        }
    }
    
    suspend fun redeemPoints(email: String, pointsToDeduct: Int) {
        val current = dao.getUserSync(email) ?: return
        if (current.points >= pointsToDeduct) { 
            dao.updateUser(current.copy(points = current.points - pointsToDeduct))
        }
    }
    
    suspend fun updateSettings(settings: PointSettings) {
        dao.insertPointSettings(settings)
    }
}
