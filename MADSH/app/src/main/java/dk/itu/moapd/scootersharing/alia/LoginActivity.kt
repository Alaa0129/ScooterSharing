package dk.itu.moapd.scootersharing.alia

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.FirebaseAuthUIActivityResultContract
import com.firebase.ui.auth.data.model.FirebaseAuthUIAuthenticationResult
import com.google.android.material.snackbar.Snackbar

class LoginActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        createSignInIntent()
    }

    private val signInLauncher = registerForActivityResult(
        FirebaseAuthUIActivityResultContract()
    ) { result ->
        onSignInResult(result)
    }

    private fun createSignInIntent() {
        // Choose authentication providers.
        val providers = arrayListOf(
            AuthUI.IdpConfig.EmailBuilder().build())
        // Create and launch sign-in intent.
        val signInIntent = AuthUI.getInstance()
            .createSignInIntentBuilder()
            .setAvailableProviders(providers)
            .setLogo(R.drawable.firebase_icon_24)
            .setTheme(R.style.Theme_FirebaseAuthentication)
            .build()
        signInLauncher.launch(signInIntent)
    }

    private fun onSignInResult(result: FirebaseAuthUIAuthenticationResult) {
        if (result.resultCode == RESULT_OK) {
            // Sign in success, update UI with the signed-in user's information.
            snackBar("User logged in the app.")
            startMainActivity()
        } else
        // If sign in fails, display a message to the user.
            snackBar("Authentication failed.")
    }

    private fun startMainActivity() {
        val intent = Intent(this,
            MainActivity::class.java)
        startActivity(intent)
        finish()
    }

    /**
     * Make a standard snack-bar that just contains text.
     */
    private fun snackBar(text: CharSequence, duration: Int = Snackbar.LENGTH_SHORT) {
        Snackbar
            .make(findViewById(android.R.id.content), text, duration)
            .show()
    }
}