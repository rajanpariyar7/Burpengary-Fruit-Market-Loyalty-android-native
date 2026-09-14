awk '
/import com.example.ui.viewmodel.LoyaltyViewModel/ {
  print $0
  print "import androidx.credentials.CredentialManager"
  print "import androidx.credentials.GetCredentialRequest"
  print "import androidx.credentials.exceptions.GetCredentialException"
  print "import com.google.android.libraries.identity.googleid.GetGoogleIdOption"
  print "import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential"
  print "import kotlinx.coroutines.launch"
  next
}
/onLogin = { email, password -> viewModel.login\(email, password\) },/ {
  print $0
  print "                    onGoogleSignInClick = {"
  print "                        val scope = androidx.compose.runtime.rememberCoroutineScope()"
  print "                        val ctx = androidx.compose.ui.platform.LocalContext.current"
  print "                        scope.launch {"
  print "                            try {"
  print "                                val credentialManager = CredentialManager.create(ctx)"
  print "                                val googleIdOption = GetGoogleIdOption.Builder()"
  print "                                    .setFilterByAuthorizedAccounts(false)"
  print "                                    .setServerClientId(\"222042395227-54o8lsf3g2ciaic3k7hl1aisjkvfvg6j.apps.googleusercontent.com\")"
  print "                                    .setAutoSelectEnabled(true)"
  print "                                    .build()"
  print "                                val request = GetCredentialRequest.Builder()"
  print "                                    .addCredentialOption(googleIdOption)"
  print "                                    .build()"
  print "                                val result = credentialManager.getCredential(ctx, request)"
  print "                                val credential = result.credential"
  print "                                if (credential is androidx.credentials.CustomCredential && credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL) {"
  print "                                    val googleIdTokenCredential = GoogleIdTokenCredential.createFrom(credential.data)"
  print "                                    viewModel.loginWithGoogle(googleIdTokenCredential.idToken)"
  print "                                }"
  print "                            } catch (e: Exception) {"
  print "                                viewModel.sendNotification(\"Error\", e.localizedMessage ?: \"Google Sign-In failed\")"
  print "                            }"
  print "                        }"
  print "                    },"
  next
}
{ print }' app/src/main/java/com/example/MainActivity.kt > tmp.kt && mv tmp.kt app/src/main/java/com/example/MainActivity.kt
