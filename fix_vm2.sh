sed -i '/val pointSettings/a \
    val allUsers = repository.allUsers.stateIn(viewModelScope, SharingStarted.Lazily, emptyList())\
    val auditLogs = repository.auditLogs.stateIn(viewModelScope, SharingStarted.Lazily, emptyList())' app/src/main/java/com/example/ui/viewmodel/LoyaltyViewModel.kt
