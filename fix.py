import re

with open("app/src/main/java/com/example/AdminDashboardScreen.kt", "r") as f:
    content = f.read()

new_item = """                NavigationBarItem(
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
"""
# find the last NavigationBarItem
content = content.replace(
    'NavigationBarItem(\n                    icon = { Icon(Icons.Default.Settings',
    new_item + '                NavigationBarItem(\n                    icon = { Icon(Icons.Default.Settings'
)
content = content.replace('selected = selectedTab == 2,\n                    onClick = { selectedTab = 2 },\n                    colors = NavigationBarItemDefaults.colors(\n                        selectedIconColor = com.example.ui.theme.DarkGreen,\n                        selectedTextColor = com.example.ui.theme.DarkGreen,\n                        indicatorColor = com.example.ui.theme.LightGreenCard\n                    )\n                )\n                NavigationBarItem(\n                    icon = { Icon(Icons.Default.Settings, contentDescription = "Settings") },\n                    label = { Text("Settings") },\n                    selected = selectedTab == 2,\n                    onClick = { selectedTab = 2 }',
'selected = selectedTab == 2,\n                    onClick = { selectedTab = 2 },\n                    colors = NavigationBarItemDefaults.colors(\n                        selectedIconColor = com.example.ui.theme.DarkGreen,\n                        selectedTextColor = com.example.ui.theme.DarkGreen,\n                        indicatorColor = com.example.ui.theme.LightGreenCard\n                    )\n                )\n                NavigationBarItem(\n                    icon = { Icon(Icons.Default.Settings, contentDescription = "Settings") },\n                    label = { Text("Settings") },\n                    selected = selectedTab == 3,\n                    onClick = { selectedTab = 3 }')

with open("app/src/main/java/com/example/AdminDashboardScreen.kt", "w") as f:
    f.write(content)
