#!/bin/bash
cat << 'INNER' > app/src/main/java/com/example/MainActivity.kt
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
import com.example.data.local.LoyaltyDatabase
import com.example.data.repository.LoyaltyRepository
import com.example.ui.theme.LoyaltyAppTheme
import com.example.ui.viewmodel.LoyaltyViewModel
import com.example.ui.viewmodel.LoyaltyViewModelFactory

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        
        createNotificationChannel()

        setContent {
            LoyaltyAppTheme {
                val database = LoyaltyDatabase.getDatabase(applicationContext)
                val repository = LoyaltyRepository(database.loyaltyDao())
                val factory = LoyaltyViewModelFactory(repository)
                val viewModel: LoyaltyViewModel = viewModel(factory = factory)

                val snackbarHostState = remember { SnackbarHostState() }
                val context = androidx.compose.ui.platform.LocalContext.current

                LaunchedEffect(Unit) {
                    viewModel.notificationEvent.collect { (title, message) ->
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
                                    IconButton(onClick = { viewModel.logout() }) {
                                        Icon(Icons.AutoMirrored.Filled.ExitToApp, contentDescription = "Logout", tint = com.example.ui.theme.DarkGreen)
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
INNER
