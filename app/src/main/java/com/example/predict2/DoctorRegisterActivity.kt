package com.example.predict2

import android.os.Bundle
import android.util.Log
import android.util.Patterns
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class DoctorRegisterActivity : AppCompatActivity() {
    private lateinit var nameEditText: EditText
    private lateinit var licenceIdEditText: EditText
    private lateinit var emailEditText: EditText
    private lateinit var hospitalEditText: EditText
    private lateinit var specialtyEditText: EditText
    private lateinit var passwordEditText: EditText
    private lateinit var registerButton: Button
    private lateinit var progressBarRegister: ProgressBar
    private lateinit var mAuth: FirebaseAuth
    private lateinit var db: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_doctor_register)

        nameEditText = findViewById(R.id.nameEditText)
        licenceIdEditText = findViewById(R.id.licenceIdEditText)
        emailEditText = findViewById(R.id.emailEditText)
        hospitalEditText = findViewById(R.id.hospitalEditText)
        specialtyEditText = findViewById(R.id.specialtyEditText)
        passwordEditText = findViewById(R.id.passwordEditText)
        registerButton = findViewById(R.id.registerButton)
        progressBarRegister = findViewById(R.id.progressBarRegister)

        mAuth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        registerButton.setOnClickListener { registerDoctor() }
    }

    private fun registerDoctor() {
        val name = nameEditText.text.toString().trim()
        val licenceId = licenceIdEditText.text.toString().trim()
        val email = emailEditText.text.toString().trim()
        val hospital = hospitalEditText.text.toString().trim()
        val specialty = specialtyEditText.text.toString().trim()
        val password = passwordEditText.text.toString().trim()

        if (validateData(name, licenceId, email, hospital, specialty, password)) {
            progressBarRegister.visibility = View.VISIBLE
            mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val user = mAuth.currentUser
                        user?.let {
                            val userId = it.uid
                            val doctorId = "D${System.currentTimeMillis()}" // Generate doctor ID
                            val doctor = hashMapOf(
                                "id" to doctorId,
                                "name" to name,
                                "licenceId" to licenceId,
                                "email" to email,
                                "hospital" to hospital,
                                "specialty" to specialty,
                                "userType" to "doctor"
                            )

                            db.collection("users").document(userId).set(doctor)
                                .addOnSuccessListener {
                                    progressBarRegister.visibility = View.GONE
                                    Toast.makeText(this, "Registration successful", Toast.LENGTH_SHORT).show()
                                    // Navigate to doctor list or login screen
                                }
                                .addOnFailureListener { e ->
                                    progressBarRegister.visibility = View.GONE
                                    Log.e("FirestoreError", "Failed to save user data: ${e.message}")
                                    Toast.makeText(this, "Registration failed: ${e.message}", Toast.LENGTH_SHORT).show()
                                }
                        }
                    } else {
                        progressBarRegister.visibility = View.GONE
                        Log.e("AuthError", "Authentication failed: ${task.exception?.message}")
                        Toast.makeText(this, "Authentication failed: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                    }
                }
        }
    }

    private fun validateData(name: String, licenceId: String, email: String, hospital: String, specialty: String, password: String): Boolean {
        if (name.isEmpty()) {
            nameEditText.error = "Name is required"
            return false
        }
        if (licenceId.isEmpty()) {
            licenceIdEditText.error = "Licence ID is required"
            return false
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emailEditText.error = "Email is invalid"
            return false
        }
        if (hospital.isEmpty()) {
            hospitalEditText.error = "Hospital is required"
            return false
        }
        if (specialty.isEmpty()) {
            specialtyEditText.error = "Specialty is required"
            return false
        }
        if (password.length < 6) {
            passwordEditText.error = "Password too short"
            return false
        }
        return true
    }
}
