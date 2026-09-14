package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
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
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.auth.FirebaseAuth
import android.os.Bundle

class LoyaltyViewModel(application: Application) : AndroidViewModel(application) {
    private val analytics = FirebaseAnalytics.getInstance(application)
    
    val repository = LoyaltyRepository()
    private val auth = FirebaseAuth.getInstance()
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

    val pointSettings = repository.pointSettings.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = null
    )
    
    val categories = repository.categories.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )
    
    val allUsers = repository.allUsers.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )
    
    val allCustomers = repository.getAllCustomers().stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    val allTransactions = repository.getAllTransactions().stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )
    
    val auditLogs = repository.auditLogs.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    private val _notifications = MutableSharedFlow<Pair<String, String>>()
    val notifications = _notifications.asSharedFlow()

    init {
        viewModelScope.launch {
            repository.initializeDb()
        }
        auth.addAuthStateListener { firebaseAuth ->
            val user = firebaseAuth.currentUser
            _currentUserEmail.value = user?.email
        }
    }

    fun loginWithGoogle(idToken: String) {
        val credential = com.google.firebase.auth.GoogleAuthProvider.getCredential(idToken, null)
        auth.signInWithCredential(credential)
            .addOnSuccessListener { authResult ->
                viewModelScope.launch {
                    val email = authResult.user?.email ?: return@launch
                    val name = authResult.user?.displayName ?: email.substringBefore("@")
                    val user = repository.getUserSync(email)
                    if (user != null) {
                        _notifications.emit(Pair("Success", "Logged in as ${user.name}"))
                    } else {
                        repository.createUser(User(
                            email = email,
                            name = name,
                            role = Role.CUSTOMER,
                            points = 5
                        ))
                        _notifications.emit(Pair("Success", "Account created! 5 bonus points awarded."))
                    }
                }
            }
            .addOnFailureListener {
                viewModelScope.launch { _notifications.emit(Pair("Error", it.localizedMessage ?: "Google Sign-In failed")) }
            }
    }

    fun login(email: String, pass: String) {
        if (email.isBlank() || pass.isBlank()) {
            viewModelScope.launch { _notifications.emit(Pair("Error", "Email and Password required")) }
            return
        }
        auth.signInWithEmailAndPassword(email, pass)
            .addOnSuccessListener {
                viewModelScope.launch {
                    val user = repository.getUserSync(email)
                    if (user != null) {
                        _notifications.emit(Pair("Success", "Logged in as ${user.name}"))
                    } else {
                        // User has firebase auth but no firestore document
                        repository.createUser(User(
                            email = email,
                            name = email.substringBefore("@"),
                            role = Role.CUSTOMER
                        ))
                    }
                }
            }
            .addOnFailureListener {
                viewModelScope.launch { _notifications.emit(Pair("Error", it.localizedMessage ?: "Login failed")) }
            }
    }

    fun signup(emailOrPhone: String, phoneOptional: String, name: String) {
        val identifier = if (emailOrPhone.isBlank()) phoneOptional else emailOrPhone
        if (identifier.isBlank()) {
            viewModelScope.launch { _notifications.emit(Pair("Error", "Email or Phone required")) }
            return
        }
        
        // For simplicity, we assume identifier is an email for Firebase Auth.
        // If it's a phone, in a real app we'd use phone auth or generate a dummy email.
        val defaultPassword = "password" // Or generate one
        
        auth.createUserWithEmailAndPassword(identifier, defaultPassword)
            .addOnSuccessListener {
                viewModelScope.launch {
                    repository.createUser(User(
                        email = identifier,
                        passwordHash = defaultPassword,
                        name = name,
                        role = Role.CUSTOMER,
                        phone = phoneOptional,
                        points = 5 // Bonus 5 points upon signup!
                    ))
                    _notifications.emit(Pair("Success", "Account created! 5 bonus points awarded. Your password is $defaultPassword"))
                }
            }
            .addOnFailureListener {
                viewModelScope.launch { _notifications.emit(Pair("Error", it.localizedMessage ?: "Signup failed")) }
            }
    }

    fun changeUserPassword(email: String, newPass: String) {
        viewModelScope.launch {
            val user = repository.getUserSync(email)
            if (user != null) {
                repository.updateUser(user.copy(passwordHash = newPass))
                val currentAdmin = _currentUserEmail.value ?: "system"
                repository.logAudit("Changed password for $email", currentAdmin)
                _notifications.emit(Pair("Success", "Password updated for $email"))
            }
        }
    }

    fun logOfferClick(offerTitle: String) {
        val bundle = Bundle().apply {
            putString(FirebaseAnalytics.Param.ITEM_NAME, offerTitle)
            putString(FirebaseAnalytics.Param.CONTENT_TYPE, "special_offer")
        }
        analytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle)
    }

    fun logPointRedemption(points: Int, description: String) {
        val bundle = Bundle().apply {
            putInt(FirebaseAnalytics.Param.VALUE, points)
            putString(FirebaseAnalytics.Param.ITEM_NAME, description)
            putString("currency", "POINTS")
        }
        analytics.logEvent(FirebaseAnalytics.Event.SPEND_VIRTUAL_CURRENCY, bundle)
    }

    fun logout() {
        auth.signOut()
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
            val currentCashierEmail = _currentUserEmail.value ?: "unknown_cashier"
            val settings = pointSettings.value
            val pointsEarned = (amount * (settings?.pointsPerDollar ?: 10)).toInt()
            repository.addPoints(customerEmail, pointsEarned)
            
            val desc = "Purchase: $amount by cashier $currentCashierEmail"
            repository.insertTransaction(PointTransaction(userEmail = customerEmail, description = desc, pointChange = pointsEarned))
            repository.logAudit("Added $pointsEarned points to $customerEmail (Cashier: $currentCashierEmail)", currentCashierEmail)
            
            _notifications.emit(Pair("Purchase Processed", "Added $pointsEarned points to $customerEmail."))
        }
    }

    fun addCategory(name: String) {
        viewModelScope.launch {
            repository.insertCategory(name)
        }
    }

    fun deleteCategory(category: Category) {
        viewModelScope.launch {
            repository.deleteCategory(category)
        }
    }

    fun deleteOffer(offer: Offer) {
        viewModelScope.launch {
            repository.deleteOffer(offer)
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
            
            if (reward.costInPoints > 0) {
                logPointRedemption(reward.costInPoints, reward.title)
            }
            
            repository.logAudit("Self-Redeemed Voucher: ${reward.title}", email)
            
            _notifications.emit(Pair("Reward Redeemed!", "You successfully redeemed: ${reward.title}"))
        }
    }

    fun redeemPointsCashier(email: String, points: Int) {
        viewModelScope.launch {
            val currentCashierEmail = _currentUserEmail.value ?: "unknown_cashier"
            repository.redeemPoints(email, points)
            
            val desc = "Register Redemption by $currentCashierEmail"
            repository.insertTransaction(PointTransaction(userEmail = email, description = desc, pointChange = -points))
            
            logPointRedemption(points, "Register Redemption")
            repository.logAudit("Redeemed $points points for customer $email (Cashier: $currentCashierEmail)", currentCashierEmail)
            
            _notifications.emit(Pair("Success", "Redeemed $points points for $email"))
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

    fun searchUsers(query: String): kotlinx.coroutines.flow.Flow<List<com.example.data.model.User>> { 
        return repository.searchUsers(query)
    }
    
    fun getUserFlow(email: String): kotlinx.coroutines.flow.Flow<com.example.data.model.User?> { 
        return repository.getUser(email) 
    }

    fun createCashier(email: String, name: String, pass: String) {
        auth.createUserWithEmailAndPassword(email, pass)
            .addOnSuccessListener {
                viewModelScope.launch {
                    repository.createUser(User(
                        email = email,
                        passwordHash = pass,
                        name = name,
                        role = Role.CASHIER
                    ))
                    _notifications.emit(Pair("Success", "Cashier $name created."))
                    val currentAdminEmail = _currentUserEmail.value ?: "system"
                    repository.logAudit("Created cashier $email", currentAdminEmail)
                }
            }
            .addOnFailureListener {
                viewModelScope.launch { _notifications.emit(Pair("Error", it.localizedMessage ?: "Failed to create cashier")) }
            }
    }

    fun sendNotification(title: String, message: String) {
        viewModelScope.launch {
            _notifications.emit(Pair(title, message))
        }
    }
}
