awk '
/if \(isLoginScreen\)/ {
  print "            val scope = rememberCoroutineScope()"
  print "            val ctx = androidx.compose.ui.platform.LocalContext.current"
  print $0
  next
}
/val scope = androidx.compose.runtime.rememberCoroutineScope()/ {
  next
}
/val ctx = androidx.compose.ui.platform.LocalContext.current/ {
  next
}
{ print }' app/src/main/java/com/example/MainActivity.kt > tmp.kt && mv tmp.kt app/src/main/java/com/example/MainActivity.kt
