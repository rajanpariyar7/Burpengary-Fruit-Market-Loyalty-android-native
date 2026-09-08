#!/bin/bash
sed -i 's/Role.SUPER_ADMIN, Role.ADMIN -> AdminDashboard(viewModel)/Role.SUPER_ADMIN -> SuperAdminDashboardScreen(viewModel)\n            Role.ADMIN -> AdminDashboard(viewModel)/g' app/src/main/java/com/example/MainActivity.kt
