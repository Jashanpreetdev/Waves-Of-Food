package com.techyexamplelogin.foodorderingapp

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.auth
import com.techyexamplelogin.foodorderingapp.databinding.ActivityLoginBinding

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var googleSignInClient: GoogleSignInClient

    private lateinit var email: String
    private lateinit var password: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = Firebase.auth

        // Google SignIn config
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        googleSignInClient = GoogleSignIn.getClient(this, gso)

        setupClicks()
    }


    private fun setupClicks() {

        // Email login ONLY
        binding.loginButton.setOnClickListener {

            email = binding.emailAddress.text.toString().trim()
            password = binding.password.text.toString().trim()

            if (email.isEmpty() || password.isEmpty()) {
                toast("Please enter all the details")
            } else {
                loginUser()
            }
        }

        // Go to Sign Up
        binding.donthavebutton.setOnClickListener {
            startActivity(Intent(this, SignActivity::class.java))
        }

        // Google Login (FORCE chooser)
        binding.googleButton.setOnClickListener {

            googleSignInClient.signOut().addOnCompleteListener {
                launcher.launch(googleSignInClient.signInIntent)
            }
        }
    }

        private fun loginUser() {

        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->

                if (task.isSuccessful) {
                    toast("Login Successful")
                    goToMain()
                } else {
                    toast("Invalid email or password")
                }
            }
    }

    private val launcher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->

            if (result.resultCode == Activity.RESULT_OK) {

                val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)

                if (task.isSuccessful) {

                    val account: GoogleSignInAccount? = task.result
                    val credential =
                        GoogleAuthProvider.getCredential(account?.idToken, null)

                    auth.signInWithCredential(credential)
                        .addOnCompleteListener { authTask ->

                            if (authTask.isSuccessful) {
                                toast("Google Login Successful")
                                goToMain()
                            } else {
                                toast("Google Sign In Failed")
                            }
                        }
                }
            }
        }

    // Auto login if already signed in
    override fun onStart() {
        super.onStart()

        auth.currentUser?.let {
            goToMain()
        }
    }

    private fun goToMain() {
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }

    private fun toast(msg: String) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
    }
}
