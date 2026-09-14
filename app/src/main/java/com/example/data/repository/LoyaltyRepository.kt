package com.example.data.repository

import com.example.data.model.*
import com.google.firebase.FirebaseApp
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import java.util.UUID

class LoyaltyRepository {
    private val db = FirebaseFirestore.getInstance(FirebaseApp.getInstance(), "burpengary-fruit-market-loyalty-database")

    val rewards: Flow<List<Reward>> = callbackFlow {
        val listener = db.collection("rewards").addSnapshotListener { snapshot, _ ->
            if (snapshot != null) {
                trySend(snapshot.toObjects(Reward::class.java))
            }
        }
        awaitClose { listener.remove() }
    }

    val offers: Flow<List<Offer>> = callbackFlow {
        val listener = db.collection("offers").addSnapshotListener { snapshot, _ ->
            if (snapshot != null) {
                trySend(snapshot.toObjects(Offer::class.java))
            }
        }
        awaitClose { listener.remove() }
    }

    val pointSettings: Flow<PointSettings?> = callbackFlow {
        val listener = db.collection("settings").document("main").addSnapshotListener { snapshot, _ ->
            if (snapshot != null && snapshot.exists()) {
                trySend(snapshot.toObject(PointSettings::class.java))
            } else {
                trySend(null)
            }
        }
        awaitClose { listener.remove() }
    }

    val allUsers: Flow<List<User>> = callbackFlow {
        val listener = db.collection("users").addSnapshotListener { snapshot, _ ->
            if (snapshot != null) {
                trySend(snapshot.toObjects(User::class.java))
            }
        }
        awaitClose { listener.remove() }
    }

    val auditLogs: Flow<List<AuditLog>> = callbackFlow {
        val listener = db.collection("auditLogs").orderBy("timestamp", Query.Direction.DESCENDING).addSnapshotListener { snapshot, _ ->
            if (snapshot != null) {
                trySend(snapshot.toObjects(AuditLog::class.java))
            }
        }
        awaitClose { listener.remove() }
    }

    val categories: Flow<List<Category>> = callbackFlow {
        val listener = db.collection("categories").addSnapshotListener { snapshot, _ ->
            if (snapshot != null) {
                trySend(snapshot.toObjects(Category::class.java))
            }
        }
        awaitClose { listener.remove() }
    }

    suspend fun insertCategory(name: String) {
        val id = UUID.randomUUID().toString()
        db.collection("categories").document(id).set(Category(id, name)).await()
    }

    suspend fun deleteCategory(category: Category) {
        db.collection("categories").document(category.id).delete().await()
    }

    suspend fun deleteOffer(offer: Offer) {
        db.collection("offers").document(offer.id).delete().await()
    }

    fun searchUsers(query: String): Flow<List<User>> = callbackFlow {
        val listener = db.collection("users").addSnapshotListener { snapshot, _ ->
            if (snapshot != null) {
                val all = snapshot.toObjects(User::class.java)
                val filtered = if (query.isBlank()) all else all.filter {
                    it.name.contains(query, ignoreCase = true) || 
                    it.email.contains(query, ignoreCase = true) || 
                    it.phone.contains(query, ignoreCase = true)
                }
                trySend(filtered)
            }
        }
        awaitClose { listener.remove() }
    }

    fun getUser(email: String): Flow<User?> = callbackFlow {
        if (email.isBlank()) {
            trySend(null)
            return@callbackFlow
        }
        val listener = db.collection("users").document(email).addSnapshotListener { snapshot, _ ->
            if (snapshot != null && snapshot.exists()) {
                trySend(snapshot.toObject(User::class.java))
            } else {
                trySend(null)
            }
        }
        awaitClose { listener.remove() }
    }

    suspend fun getUserSync(email: String): User? {
        if (email.isBlank()) return null
        return try {
            val doc = db.collection("users").document(email).get().await()
            if (doc.exists()) doc.toObject(User::class.java) else null
        } catch (e: Exception) {
            null
        }
    }

    suspend fun getCustomerCount(): Int {
        val snapshot = db.collection("users").whereEqualTo("role", Role.CUSTOMER.name).get().await()
        return snapshot.size()
    }

    fun getAllCustomers(): Flow<List<User>> = callbackFlow {
        val listener = db.collection("users").whereEqualTo("role", Role.CUSTOMER.name).addSnapshotListener { snapshot, _ ->
            if (snapshot != null) {
                trySend(snapshot.toObjects(User::class.java))
            }
        }
        awaitClose { listener.remove() }
    }

    fun getTransactions(email: String): Flow<List<PointTransaction>> = callbackFlow {
        if (email.isBlank()) {
            trySend(emptyList())
            return@callbackFlow
        }
        val listener = db.collection("transactions")
            .whereEqualTo("userEmail", email)
            .orderBy("timestamp", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, _ ->
                if (snapshot != null) {
                    trySend(snapshot.toObjects(PointTransaction::class.java))
                }
            }
        awaitClose { listener.remove() }
    }

    fun getAllTransactions(): Flow<List<PointTransaction>> = callbackFlow {
        val listener = db.collection("transactions").orderBy("timestamp", Query.Direction.DESCENDING).addSnapshotListener { snapshot, _ ->
            if (snapshot != null) {
                trySend(snapshot.toObjects(PointTransaction::class.java))
            }
        }
        awaitClose { listener.remove() }
    }

    suspend fun insertTransaction(transaction: PointTransaction) {
        val id = UUID.randomUUID().toString()
        val toInsert = transaction.copy(id = id)
        db.collection("transactions").document(id).set(toInsert).await()
    }

    suspend fun logAudit(action: String, byEmail: String) {
        val id = UUID.randomUUID().toString()
        val log = AuditLog(id = id, action = action, changedBy = byEmail, timestamp = System.currentTimeMillis())
        db.collection("auditLogs").document(id).set(log).await()
    }

    suspend fun initializeDb() {
        try {
            val settings = db.collection("settings").document("main").get().await()
            if (!settings.exists()) {
                db.collection("settings").document("main").set(PointSettings()).await()
            }
            
            val admin = getUserSync("admin@example.com")
            if (admin == null) {
                createUser(User(
                    email = "admin@example.com",
                    name = "Store Manager",
                    role = Role.ADMIN
                ))
                createUser(User(
                    email = "cashier@example.com",
                    name = "Market Cashier",
                    role = Role.CASHIER
                ))
                
                insertReward(Reward(title = "Free Coffee", description = "Get a free medium coffee at the market cafe.", costInStamps = 10))
                insertReward(Reward(title = "$5 Off Produce", description = "Get $5 off your next fresh produce purchase.", costInPoints = 500))
                
                addOffer(Offer(title = "Fresh Strawberries", price = "$1.99", category = "FRUITS", description = "OFFER: Amazing value! $1.99 per punnet!", imageUrl = null))
                
                insertTransaction(PointTransaction(userEmail = "customer@example.com", description = "Purchase: $5.0 by cashier cashier@example.com", pointChange = 50))
            }
        } catch (e: Exception) {
            // Silently fail if Firebase rules block initialization before the user is authenticated.
        }
    }

    suspend fun createUser(user: User) {
        db.collection("users").document(user.email).set(user).await()
    }

    suspend fun updateUser(user: User) {
        db.collection("users").document(user.email).set(user).await()
    }

    suspend fun addStamp(email: String) {
        val current = getUserSync(email) ?: return
        updateUser(current.copy(stamps = current.stamps + 1, lifetimeStamps = current.lifetimeStamps + 1))
    }

    suspend fun resetStamps(email: String) {
        val current = getUserSync(email) ?: return
        updateUser(current.copy(stamps = 0))
    }

    suspend fun addPoints(email: String, points: Int) {
        val current = getUserSync(email) ?: return
        updateUser(current.copy(points = current.points + points))
    }

    suspend fun addOffer(offer: Offer) {
        val id = UUID.randomUUID().toString()
        db.collection("offers").document(id).set(offer.copy(id = id)).await()
    }

    suspend fun insertReward(reward: Reward) {
        val id = UUID.randomUUID().toString()
        db.collection("rewards").document(id).set(reward.copy(id = id)).await()
    }

    suspend fun redeemReward(email: String, reward: Reward) {
        val current = getUserSync(email) ?: return
        if (reward.costInStamps > 0 && current.stamps >= reward.costInStamps) {
            updateUser(current.copy(stamps = current.stamps - reward.costInStamps))
        } else if (reward.costInPoints > 0 && current.points >= reward.costInPoints) {
            updateUser(current.copy(points = current.points - reward.costInPoints))
        }
    }

    suspend fun redeemPoints(email: String, pointsToDeduct: Int) {
        val current = getUserSync(email) ?: return
        if (current.points >= pointsToDeduct) { 
            updateUser(current.copy(points = current.points - pointsToDeduct))
        }
    }

    suspend fun updateSettings(settings: PointSettings) {
        db.collection("settings").document("main").set(settings).await()
    }
}
