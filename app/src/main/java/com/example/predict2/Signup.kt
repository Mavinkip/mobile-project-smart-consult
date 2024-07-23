package com.example.predict2

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Patterns
import android.view.View
import android.widget.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class Signup : AppCompatActivity() {

    private lateinit var editTextName: EditText
    private lateinit var editTextPhoneNumber: EditText
    private lateinit var editTextDateOfBirth: EditText
    private lateinit var editTextEmail: EditText
    private lateinit var editTextPassword: EditText
    private lateinit var progressBar: ProgressBar
    private lateinit var mAuth: FirebaseAuth
    private lateinit var db: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signup)

        // Initialize the views
        editTextName = findViewById(R.id.editTextName)
        editTextPhoneNumber = findViewById(R.id.editTextPhoneNumber)
        editTextDateOfBirth = findViewById(R.id.editTextDateOfBirth)
        editTextEmail = findViewById(R.id.editTextEmail)
        editTextPassword = findViewById(R.id.editTextPassword)
        progressBar = findViewById(R.id.progressBar)

        // Initialize Firebase Auth and Firestore
        mAuth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        // Set up button listeners
        findViewById<Button>(R.id.buttonSignUp).setOnClickListener { createAccount() }
        findViewById<TextView>(R.id.backloginbt).setOnClickListener { onLoginClick() }
    }

    private fun createAccount() {
        val name = editTextName.text.toString()
        val phoneNumber = editTextPhoneNumber.text.toString()
        val dateOfBirth = editTextDateOfBirth.text.toString()
        val email = editTextEmail.text.toString()
        val password = editTextPassword.text.toString()

        if (validateData(name, phoneNumber, dateOfBirth, email, password)) {
            createAccountInFirebase(name, phoneNumber, dateOfBirth, email, password)
        }
    }

    private fun validateData(name: String, phoneNumber: String, dateOfBirth: String, email: String, password: String): Boolean {
        if (name.isEmpty()) {
            editTextName.error = "Name is required"
            return false
        }
        if (phoneNumber.isEmpty()) {
            editTextPhoneNumber.error = "Phone number is required"
            return false
        }
        if (dateOfBirth.isEmpty()) {
            editTextDateOfBirth.error = "Date of birth is required"
            return false
        }
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

    private fun createAccountInFirebase(name: String, phoneNumber: String, dateOfBirth: String, email: String, password: String) {
        changeInProgress(true)
        mAuth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                changeInProgress(false)
                if (task.isSuccessful) {
                    val user = mAuth.currentUser
                    user?.let {
                        val userId = it.uid
                        val userData = hashMapOf(
                            "id" to userId,
                            "name" to name,
                            "phoneNumber" to phoneNumber,
                            "dob" to dateOfBirth,
                            "email" to email,
                            "userType" to "patient" // or change accordingly
                        )

                        db.collection("users").document(userId).set(userData)
                            .addOnSuccessListener {
                                Toast.makeText(this, "Successfully created account. Check your email for verification.", Toast.LENGTH_SHORT).show()
                                user.sendEmailVerification()
                                FirebaseAuth.getInstance().signOut()
                                finish()
                            }
                            .addOnFailureListener {
                                Toast.makeText(this, "Failed to save user data", Toast.LENGTH_SHORT).show()
                            }
                    }
                } else {
                    Toast.makeText(this, task.exception?.localizedMessage, Toast.LENGTH_SHORT).show()
                }
            }
    }

    private fun changeInProgress(inProgress: Boolean) {
        progressBar.visibility = if (inProgress) View.VISIBLE else View.GONE
        findViewById<Button>(R.id.buttonSignUp).visibility = if (inProgress) View.GONE else View.VISIBLE
    }

    private fun onLoginClick() {
        // Handle the login button click
        startActivity(Intent(this, Login::class.java))
        finish()
    }
}
