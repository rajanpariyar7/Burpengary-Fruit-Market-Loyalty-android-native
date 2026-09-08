#!/bin/bash
cat << 'INNER' > app/src/main/java/com/example/CustomerDashboardScreen.kt.sed
/Spacer(modifier = Modifier.height(24.dp))/!b
n
/}/!b
n
/Spacer(modifier = Modifier.height(24.dp))/!b
n
/}/!b
n
/item {/!b
n
/val threshold = pointSettings?.redemptionThreshold ?: 100/!b
INNER
