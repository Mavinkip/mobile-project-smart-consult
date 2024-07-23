package com.example.predict2

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Patterns
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth

class Login : AppCompatActivity() {
    private lateinit var editTextEmail: EditText
    private lateinit var editTextPassword: EditText
    private lateinit var progressBar: ProgressBar
    private lateinit var firebaseAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        // Initialize Firebase Auth
        firebaseAuth = FirebaseAuth.getInstance()

        // Initialize views
        editTextEmail = findViewById(R.id.editTextEmail)
        editTextPassword = findViewById(R.id.editTextPassword)
        progressBar = findViewById(R.id.progressBar)

        // Set up login button listener
        findViewById<Button>(R.id.buttonLogin).setOnClickListener { loginUser() }
    }

    private fun loginUser() {
        val email = editTextEmail.text.toString()
        val password = editTextPassword.text.toString()

        if (validateData(email, password)) {
            loginUserInFirebase(email, password)
        }
    }

    private fun validateData(email: String, password: String): Boolean {
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            editTextEmail.error = "Email is invalid"
            return false
        }
        if (password.length < 6) {
            editTextPassword.error = "Password too short"
            return false
        }
        return true
    }

    private fun loginUserInFirebase(email: String, password: String) {
        changeInProgress(true)
        firebaseAuth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                changeInProgress(false)
                if (task.isSuccessful) {
                    Toast.makeText(this, "Login successful", Toast.LENGTH_SHORT).show()
                    // Redirect to another activity after successful login
                    startActivity(Intent(this, Drawer::class.java))
                    finish()
                } else {
                    Toast.makeText(this, task.exception?.localizedMessage, Toast.LENGTH_SHORT).show()
                }
            }
    }

    private fun changeInProgress(inProgress: Boolean) {
        progressBar.visibility = if (inProgress) View.VISIBLE else View.GONE
        findViewById<Button>(R.id.buttonLogin).visibility = if (inProgress) View.GONE else View.VISIBLE
    }

    fun onSignupClick(view: View) {
        // Handle the signup prompt click
        startActivity(Intent(this, Signup::class.java))
        finish()
    }
}