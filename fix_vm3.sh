#!/bin/bash
cat << 'INNER' > app/src/main/java/com/example/ui/viewmodel/LoyaltyViewModel.kt
package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import androidx.room.Room
import com.example.data.local.LoyaltyDatabase
import com.example.data.model.*
import com.example.data.repository.LoyaltyRepository
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.ExperimentalCoroutinesApi

class LoyaltyViewModel(application: Application) : AndroidViewModel(application) {
    private val db = Room.databaseBuilder(
        application,
        LoyaltyDatabase::class.java,
        "loyalty_db"
    ).fallbackToDestructiveMigration().build()
    
    val repository = LoyaltyRepository(db.loyaltyDao())
    private val _currentUserEmail = MutableStateFlow<String?>(null)
    
    @OptIn(ExperimentalCoroutinesApi::class)
    val currentUser = _currentUserEmail.flatMapLatest { email ->
        if (email != null) repository.getUser(email) else flowOf(null)
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = null
    )

    @OptIn(ExperimentalCoroutinesApi::class)
    val currentTransactions = _currentUserEmail.flatMapLatest { email ->
        if (email != null) repository.getTransactions(email) else flowOf(emptyList())
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    val rewards = repository.rewards.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )
    
    val offers = repository.offers.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )
    
    val customers = repository.getAllCustomers().stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )
    
    val pointSettings = repository.pointSettings.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = null
    )

    val allUsers = repository.allUsers.stateIn(
        scope = viewModelScope,
        started = SharingStarted.Lazily,
        initialValue = emptyList()
    )
    
    val auditLogs = repository.auditLogs.stateIn(
        scope = viewModelScope,
        started = SharingStarted.Lazily,
        initialValue = emptyList()
    )

    private val _notifications = MutableSharedFlow<Pair<String, String>>()
    val notifications = _notifications.asSharedFlow()

    init {
        viewModelScope.launch {
            repository.initializeDb()
        }
    }
    
    fun login(email: String, pass: String) {
        viewModelScope.launch {
            val user = repository.getUserSync(email)
            if (user != null && user.passwordHash == pass) {
                _currentUserEmail.value = email
                _notifications.emit(Pair("Success", "Logged in as ${user.name}"))
            } else {
                _notifications.emit(Pair("Error", "Invalid email or password"))
            }
        }
    }
    
    fun signup(email: String, pass: String, name: String) {
        viewModelScope.launch {
            if (repository.getUserSync(email) == null) {
                repository.createUser(User(email, pass, name, Role.CUSTOMER))
                _currentUserEmail.value = email
                _notifications.emit(Pair("Success", "Account created!"))
            } else {
                _notifications.emit(Pair("Error", "Email already exists"))
            }
        }
    }
    
    fun logout() {
        _currentUserEmail.value = null
    }

    fun addStampTo(email: String) {
        viewModelScope.launch {
            repository.addStamp(email)
            repository.insertTransaction(PointTransaction(userEmail = email, description = "Received a stamp", pointChange = 0))
            _notifications.emit(Pair("Stamp Added", "Customer $email received a stamp."))
        }
    }

    fun processPurchase(customerEmail: String, amount: Double) {
        viewModelScope.launch {
            val settings = pointSettings.value
            val pointsEarned = (amount * (settings?.pointsPerDollar ?: 10)).toInt()
            repository.addPoints(customerEmail, pointsEarned)
            repository.insertTransaction(PointTransaction(userEmail = customerEmail, description = "Purchase Reward", pointChange = pointsEarned))
            _notifications.emit(Pair("Purchase Processed", "Added $pointsEarned points to $customerEmail."))
        }
    }

    fun addOffer(title: String, price: String, desc: String, category: String = "General", imageUrl: String? = null) {
        viewModelScope.launch {
            repository.addOffer(Offer(title = title, price = price, description = desc, category = category, imageUrl = imageUrl))
            _notifications.emit(Pair("Offer Added", "$title is now available."))
            
            val currentAdminEmail = _currentUserEmail.value ?: "system"
            repository.logAudit("Added new offer: $title", currentAdminEmail)
        }
    }

    fun redeemReward(reward: Reward) {
        viewModelScope.launch {
            val email = _currentUserEmail.value ?: return@launch
            repository.redeemReward(email, reward)
            
            val pointChange = if (reward.costInPoints > 0) -reward.costInPoints else 0
            val description = "Redeemed: ${reward.title}"
            repository.insertTransaction(PointTransaction(userEmail = email, description = description, pointChange = pointChange))
            
            _notifications.emit(Pair("Reward Redeemed!", "You successfully redeemed: ${reward.title}"))
        }
    }
    
    fun redeemPointsCashier(email: String, points: Int) {
        viewModelScope.launch {
            repository.redeemPoints(email, points)
            repository.insertTransaction(PointTransaction(userEmail = email, description = "Point redemption at register", pointChange = -points))
            _notifications.emit(Pair("Success", "Redeemed $points points for $email"))
            
            val currentCashierEmail = _currentUserEmail.value ?: "cashier"
            repository.logAudit("Redeemed $points points for $email", currentCashierEmail)
        }
    }
    
    fun updateSettings(pointsPerDollar: Int, discountRate: Double, redemptionThreshold: Int, adminWriteEnabled: Boolean) {
        viewModelScope.launch {
            repository.updateSettings(PointSettings(
                pointsPerDollar = pointsPerDollar, 
                discountPer100Points = discountRate,
                redemptionThreshold = redemptionThreshold,
                adminWriteEnabled = adminWriteEnabled
            ))
            
            val currentAdminEmail = _currentUserEmail.value ?: "system"
            repository.logAudit("Updated system settings", currentAdminEmail)
            _notifications.emit(Pair("Settings Saved", "System configuration has been updated."))
        }
    }

    fun changeUserRole(email: String, newRole: Role) {
        viewModelScope.launch {
            val user = repository.getUserSync(email)
            if (user != null) {
                repository.updateUser(user.copy(role = newRole))
                val currentAdminEmail = _currentUserEmail.value ?: "system"
                repository.logAudit("Changed role of $email to $newRole", currentAdminEmail)
                _notifications.emit(Pair("Role Updated", "$email is now a $newRole."))
            }
        }
    }

    fun createCashier(email: String, name: String, pass: String) {
        viewModelScope.launch {
            if (repository.getUserSync(email) == null) {
                repository.createUser(User(
                    email = email,
                    passwordHash = pass,
                    name = name,
                    role = Role.CASHIER
                ))
                _notifications.emit(Pair("Success", "Cashier $name created."))
                val currentAdminEmail = _currentUserEmail.value ?: "system"
                repository.logAudit("Created cashier $email", currentAdminEmail)
            } else {
                _notifications.emit(Pair("Error", "User with email already exists."))
            }
        }
    }

    fun sendNotification(title: String, message: String) {
        viewModelScope.launch {
            _notifications.emit(Pair(title, message))
        }
    }
}
INNER
