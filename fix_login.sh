#!/bin/bash
cat << 'INNER' >> app/src/main/java/com/example/MainActivity.kt

@Composable
fun AppRouter(viewModel: LoyaltyViewModel, modifier: Modifier = Modifier) {
    val currentUser by viewModel.currentUser.collectAsStateWithLifecycle()

    Box(modifier = modifier.fillMaxSize()) {
        if (currentUser == null) {
            LoginScreen(
                onLogin = { email, password -> viewModel.login(email, password) }
            )
        } else {
            when (currentUser?.role) {
                com.example.data.model.Role.CUSTOMER -> CustomerDashboardScreen(viewModel)
                com.example.data.model.Role.CASHIER -> CashierScreen(viewModel)
                com.example.data.model.Role.ADMIN -> AdminDashboardScreen(viewModel)
                com.example.data.model.Role.SUPER_ADMIN -> SuperAdminDashboardScreen(viewModel)
                null -> {}
            }
        }
    }
}

@Composable
fun LoginScreen(onLogin: (String, String) -> Unit) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    Column(
        modifier = Modifier.fillMaxSize().padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("Welcome to", fontSize = 16.sp, color = Color.Gray)
        Text("Burpengary Market", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = com.example.ui.theme.DarkGreen)
        Spacer(modifier = Modifier.height(32.dp))
        
        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Email") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(16.dp))
        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Password") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(32.dp))
        Button(
            onClick = { onLogin(email, password) },
            modifier = Modifier.fillMaxWidth().height(56.dp),
            colors = ButtonDefaults.buttonColors(containerColor = com.example.ui.theme.PrimaryGreen)
        ) {
            Text("Login")
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        Text("Hint: Use superadmin@example.com, admin@example.com, cashier@example.com, or customer@example.com. Password is 'password'", fontSize = 12.sp, color = Color.Gray)
    }
}
INNER
