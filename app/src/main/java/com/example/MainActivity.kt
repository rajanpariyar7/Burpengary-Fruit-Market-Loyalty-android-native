package com.example

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.viewmodel.LoyaltyViewModel
import androidx.credentials.CredentialManager
import androidx.credentials.GetCredentialRequest
import androidx.credentials.exceptions.GetCredentialException
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        
        createNotificationChannel()

        setContent {
            MyApplicationTheme {
                val viewModel: LoyaltyViewModel = viewModel()

                val snackbarHostState = remember { SnackbarHostState() }
                val context = androidx.compose.ui.platform.LocalContext.current

                LaunchedEffect(Unit) {
                    viewModel.notifications.collect { (title, message) ->
                        snackbarHostState.showSnackbar(message)
                        showLocalNotification(context, title, message)
                    }
                }

                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    snackbarHost = { SnackbarHost(snackbarHostState) },
                    topBar = {
                        Surface(
                            color = Color.White,
                            shadowElevation = 0.dp,
                            border = androidx.compose.foundation.BorderStroke(1.dp, com.example.ui.theme.BorderSlate)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .windowInsetsPadding(WindowInsets.statusBars)
                                    .padding(horizontal = 24.dp, vertical = 12.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Image(
                                        painter = painterResource(id = R.drawable.logo),
                                        contentDescription = "App Logo",
                                        modifier = Modifier.size(40.dp).padding(end = 12.dp)
                                    )
                                    Column {
                                        Text(
                                            text = "BURPENGARY MARKET",
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = Color.Gray,
                                            letterSpacing = 1.sp
                                        )
                                        Text(
                                            text = "Your FRESH Shop",
                                            fontSize = 16.sp,
                                            fontWeight = FontWeight.ExtraBold,
                                            color = com.example.ui.theme.PrimaryGreen
                                        )
                                    }
                                }
                                val currentUser by viewModel.currentUser.collectAsStateWithLifecycle()
                                if (currentUser != null) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Surface(
                                            color = Color(0xFFE8F5E9),
                                            shape = RoundedCornerShape(12.dp)
                                        ) {
                                            Text(
                                                text = currentUser?.role?.name ?: "USER",
                                                color = com.example.ui.theme.DarkGreen,
                                                fontSize = 10.sp,
                                                fontWeight = FontWeight.Bold,
                                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                            )
                                        }
                                        Spacer(modifier = Modifier.width(8.dp))
                                        IconButton(onClick = { viewModel.logout() }) {
                                            Icon(Icons.AutoMirrored.Filled.ExitToApp, contentDescription = "Logout", tint = com.example.ui.theme.DarkGreen)
                                        }
                                    }
                                }
                            }
                        }
                    }
                ) { innerPadding ->
                    AppRouter(viewModel, Modifier.padding(innerPadding))
                }
            }
        }
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "Loyalty Notifications"
            val descriptionText = "Notifications for loyalty stamps and rewards"
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel("loyalty_channel", name, importance).apply {
                description = descriptionText
            }
            val notificationManager: NotificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    private fun showLocalNotification(context: Context, title: String, message: String) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ActivityCompat.checkSelfPermission(context, android.Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                return
            }
        }

        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent: PendingIntent = PendingIntent.getActivity(
            context, 0, intent, PendingIntent.FLAG_IMMUTABLE
        )

        val builder = NotificationCompat.Builder(context, "loyalty_channel")
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle(title)
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)

        with(NotificationManagerCompat.from(context)) {
            notify(System.currentTimeMillis().toInt(), builder.build())
        }
    }
}

@Composable
fun AppRouter(viewModel: LoyaltyViewModel, modifier: Modifier = Modifier) {
    val currentUser by viewModel.currentUser.collectAsStateWithLifecycle()

    Box(modifier = modifier.fillMaxSize()) {
        if (currentUser == null) {
            var isLoginScreen by remember { mutableStateOf(true) }
            val scope = rememberCoroutineScope()
            val ctx = androidx.compose.ui.platform.LocalContext.current
            if (isLoginScreen) {
                LoginScreen(
                    onLogin = { email, password -> viewModel.login(email, password) },
                    onGoogleSignInClick = {
                        scope.launch {
                            try {
                                val credentialManager = CredentialManager.create(ctx)
                                val googleIdOption = GetGoogleIdOption.Builder()
                                    .setFilterByAuthorizedAccounts(false)
                                    .setServerClientId("222042395227-54o8lsf3g2ciaic3k7hl1aisjkvfvg6j.apps.googleusercontent.com")
                                    .setAutoSelectEnabled(true)
                                    .build()
                                val request = GetCredentialRequest.Builder()
                                    .addCredentialOption(googleIdOption)
                                    .build()
                                val result = credentialManager.getCredential(ctx, request)
                                val credential = result.credential
                                if (credential is androidx.credentials.CustomCredential && credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL) {
                                    val googleIdTokenCredential = GoogleIdTokenCredential.createFrom(credential.data)
                                    viewModel.loginWithGoogle(googleIdTokenCredential.idToken)
                                }
                            } catch (e: Exception) {
                                viewModel.sendNotification("Error", e.localizedMessage ?: "Google Sign-In failed")
                            }
                        }
                    },
                    onNavigateToSignup = { isLoginScreen = false }
                )
            } else {
                SignupScreen(
                    onSignup = { email, phone, name -> viewModel.signup(email, phone, name) },
                    onNavigateToLogin = { isLoginScreen = true }
                )
            }
        } else {
            when (currentUser?.role) {
                com.example.data.model.Role.CUSTOMER -> CustomerDashboardScreen(viewModel)
                com.example.data.model.Role.CASHIER -> CashierScreen(viewModel)
                com.example.data.model.Role.ADMIN -> AdminDashboard(viewModel)
                com.example.data.model.Role.SUPER_ADMIN -> SuperAdminDashboardScreen(viewModel)
                null -> {}
            }
        }
    }
}

@Composable
fun LoginScreen(onLogin: (String, String) -> Unit, onGoogleSignInClick: () -> Unit = {}, onNavigateToSignup: () -> Unit) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    var passwordVisible by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(com.example.ui.theme.BackgroundLight)
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Image(
            painter = painterResource(id = R.drawable.logo),
            contentDescription = "App Logo",
            modifier = Modifier.size(100.dp).padding(bottom = 16.dp)
        )
        Text("Welcome to", fontSize = 16.sp, color = Color.Gray)
        Text("Burpengary Market", fontSize = 28.sp, fontWeight = FontWeight.ExtraBold, color = com.example.ui.theme.DarkGreen)
        Text("Your FRESH Shop", fontSize = 16.sp, fontWeight = FontWeight.Medium, color = com.example.ui.theme.PrimaryGreen)
        Spacer(modifier = Modifier.height(32.dp))
        
        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Email or Phone") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(16.dp))
        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Password") },
            visualTransformation = if (passwordVisible) androidx.compose.ui.text.input.VisualTransformation.None else androidx.compose.ui.text.input.PasswordVisualTransformation(),
            keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(keyboardType = androidx.compose.ui.text.input.KeyboardType.Password),
            trailingIcon = {
                val image = if (passwordVisible)
                    Icons.Filled.Visibility
                else
                    Icons.Filled.VisibilityOff

                val description = if (passwordVisible) "Hide password" else "Show password"

                IconButton(onClick = { passwordVisible = !passwordVisible }) {
                    Icon(imageVector = image, contentDescription = description)
                }
            },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(24.dp))
        Button(
            onClick = { onLogin(email, password) },
            modifier = Modifier.fillMaxWidth().height(56.dp),
            colors = ButtonDefaults.buttonColors(containerColor = com.example.ui.theme.PrimaryGreen)
        ) {
            Text("Login", fontSize = 18.sp, fontWeight = FontWeight.Bold)
        }
        Spacer(modifier = Modifier.height(16.dp))
        OutlinedButton(
            onClick = onGoogleSignInClick,
            modifier = Modifier.fillMaxWidth().height(56.dp),
            colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.DarkGray)
        ) {
            Text("Sign in with Google", fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
        }
        Spacer(modifier = Modifier.height(16.dp))
        TextButton(onClick = onNavigateToSignup) {
            Text("Don't have an account? Sign up", color = com.example.ui.theme.PrimaryGreen)
        }
    }
}

@Composable
fun SignupScreen(onSignup: (String, String, String) -> Unit, onNavigateToLogin: () -> Unit) {
    var email by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var name by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(com.example.ui.theme.BackgroundLight)
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Image(
            painter = painterResource(id = R.drawable.logo),
            contentDescription = "App Logo",
            modifier = Modifier.size(80.dp).padding(bottom = 16.dp)
        )
        Text("Create Account", fontSize = 28.sp, fontWeight = FontWeight.ExtraBold, color = com.example.ui.theme.DarkGreen)
        Text("Join & Get 5 Bonus Points", fontSize = 16.sp, fontWeight = FontWeight.Medium, color = com.example.ui.theme.PrimaryGreen)
        Spacer(modifier = Modifier.height(32.dp))
        
        OutlinedTextField(
            value = name,
            onValueChange = { name = it },
            label = { Text("Full Name") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(16.dp))
        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Email Address") },
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(keyboardType = androidx.compose.ui.text.input.KeyboardType.Email)
        )
        Spacer(modifier = Modifier.height(16.dp))
        OutlinedTextField(
            value = phone,
            onValueChange = { phone = it },
            label = { Text("Phone Number (Optional if Email provided)") },
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(keyboardType = androidx.compose.ui.text.input.KeyboardType.Phone)
        )
        Spacer(modifier = Modifier.height(24.dp))
        Button(
            onClick = { onSignup(email, phone, name) },
            modifier = Modifier.fillMaxWidth().height(56.dp),
            colors = ButtonDefaults.buttonColors(containerColor = com.example.ui.theme.PrimaryGreen)
        ) {
            Text("Sign Up", fontSize = 18.sp, fontWeight = FontWeight.Bold)
        }
        Spacer(modifier = Modifier.height(16.dp))
        TextButton(onClick = onNavigateToLogin) {
            Text("Already have an account? Log in", color = com.example.ui.theme.PrimaryGreen)
        }
    }
}

@Composable
fun DemoUserButton(title: String, modifier: Modifier = Modifier, onClick: () -> Unit) {
    OutlinedButton(
        onClick = onClick,
        modifier = modifier.height(48.dp),
        colors = ButtonDefaults.outlinedButtonColors(contentColor = com.example.ui.theme.PrimaryGreen),
        border = androidx.compose.foundation.BorderStroke(1.dp, com.example.ui.theme.PrimaryGreen),
        shape = RoundedCornerShape(8.dp)
    ) {
        Text(title, fontSize = 12.sp, fontWeight = FontWeight.SemiBold, maxLines = 1)
    }
}
