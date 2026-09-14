awk '
/fun LoginScreen\(onLogin:/ {
  print "fun LoginScreen(onLogin: (String, String) -> Unit, onGoogleSignInClick: () -> Unit = {}, onNavigateToSignup: () -> Unit) {"
  next
}
/Text\("Login", fontSize = 18.sp/ {
  print $0
  print "        }"
  print "        Spacer(modifier = Modifier.height(16.dp))"
  print "        OutlinedButton("
  print "            onClick = onGoogleSignInClick,"
  print "            modifier = Modifier.fillMaxWidth().height(56.dp),"
  print "            colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.DarkGray)"
  print "        ) {"
  print "            Text(\"Sign in with Google\", fontSize = 16.sp, fontWeight = FontWeight.SemiBold)"
  next
}
{ print }' app/src/main/java/com/example/MainActivity.kt > tmp.kt && mv tmp.kt app/src/main/java/com/example/MainActivity.kt
