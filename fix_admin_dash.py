import re

with open("app/src/main/java/com/example/AdminDashboardScreen.kt", "r") as f:
    content = f.read()

admin_dashboard = """@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminDashboard(viewModel: LoyaltyViewModel) {
    var selectedTab by remember { mutableStateOf(0) }
    val currentUser by viewModel.currentUser.collectAsStateWithLifecycle()
    
    Scaffold(
        bottomBar = {
            NavigationBar(
                containerColor = Color.White,
                contentColor = com.example.ui.theme.DarkGreen
            ) {
                NavigationBarItem(
                    icon = { Icon(Icons.Default.ShoppingBag, contentDescription = "Catalog") },
                    label = { Text("Catalog") },
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = com.example.ui.theme.DarkGreen,
                        selectedTextColor = com.example.ui.theme.DarkGreen,
                        indicatorColor = com.example.ui.theme.LightGreenCard
                    )
                )
                NavigationBarItem(
                    icon = { Icon(Icons.Default.AddCircle, contentDescription = "Manage") },
                    label = { Text("Manage") },
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = com.example.ui.theme.DarkGreen,
                        selectedTextColor = com.example.ui.theme.DarkGreen,
                        indicatorColor = com.example.ui.theme.LightGreenCard
                    )
                )
                NavigationBarItem(
                    icon = { Icon(Icons.Default.ReceiptLong, contentDescription = "Transactions") },
                    label = { Text("Audit") },
                    selected = selectedTab == 2,
                    onClick = { selectedTab = 2 },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = com.example.ui.theme.DarkGreen,
                        selectedTextColor = com.example.ui.theme.DarkGreen,
                        indicatorColor = com.example.ui.theme.LightGreenCard
                    )
                )
                NavigationBarItem(
                    icon = { Icon(Icons.Default.Settings, contentDescription = "Settings") },
                    label = { Text("Settings") },
                    selected = selectedTab == 3,
                    onClick = { selectedTab = 3 },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = com.example.ui.theme.DarkGreen,
                        selectedTextColor = com.example.ui.theme.DarkGreen,
                        indicatorColor = com.example.ui.theme.LightGreenCard
                    )
                )
            }
        }
    ) { padding ->
        Box(modifier = Modifier.padding(padding).fillMaxSize().background(Color(0xFFFAFAFA))) {
            when (selectedTab) {
                0 -> AdminCatalogTab(viewModel)
                1 -> AdminManageTab(viewModel)
                2 -> AdminTransactionsTab(viewModel)
                3 -> AdminSettingsTab(viewModel, currentUser?.name ?: "Admin")
            }
        }
    }
}
"""

start_idx = content.find("@OptIn(ExperimentalMaterial3Api::class)\n@Composable\nfun AdminCatalogTab(viewModel: LoyaltyViewModel)")
if start_idx != -1:
    content = content[:start_idx] + admin_dashboard + "\n\n" + content[start_idx:]
    with open("app/src/main/java/com/example/AdminDashboardScreen.kt", "w") as f:
        f.write(content)
