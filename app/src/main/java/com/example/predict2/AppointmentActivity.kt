package com.example.predict2

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class AppointmentActivity : AppCompatActivity() {

    private lateinit var editTextSpecialization: EditText
    private lateinit var editTextHospital: EditText
    private lateinit var buttonSubmit: Button
    private lateinit var buttonViewBookedAppointments: Button

    private lateinit var firestore: FirebaseFirestore
    private lateinit var auth: FirebaseAuth
    private var userId: String? = null
    private var userDob: String? = null
    private var userEmail: String? = null
    private var userIdNumber: String? = null
    private var userName: String? = null
    private var userType: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_appointment)

        // Initialize Firestore and FirebaseAuth
        firestore = FirebaseFirestore.getInstance()
        auth = FirebaseAuth.getInstance()

        // Get the current user ID
        userId = auth.currentUser?.uid

        // Initialize UI elements
        editTextSpecialization = findViewById(R.id.editTextSpecialization)
        editTextHospital = findViewById(R.id.editTextHospital)
        buttonSubmit = findViewById(R.id.buttonSubmit)
        buttonViewBookedAppointments = findViewById(R.id.buttonViewBookedAppointments)

        // Set click listener for the view booked appointments button
        buttonViewBookedAppointments.setOnClickListener {
            startActivity(Intent(this, AppointmentListActivity::class.java))
        }

        // Set click listener for the submit button
        buttonSubmit.setOnClickListener {
            val specialization = editTextSpecialization.text.toString().trim()
            val hospital = editTextHospital.text.toString().trim()

            if (specialization.isNotEmpty() && hospital.isNotEmpty()) {
                saveAppointmentToFirestore(specialization, hospital)
            } else {
                Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show()
            }
        }

        // Retrieve user details from Firestore
        retrieveUserDetails()
    }

    private fun retrieveUserDetails() {
        if (userId == null) {
            Toast.makeText(this, "User not logged in", Toast.LENGTH_SHORT).show()
            return
        }

        firestore.collection("users").document(userId!!).get()
            .addOnSuccessListener { document ->
                if (document != null && document.exists()) {
                    userDob = document.getString("dob")
                    userEmail = document.getString("email")
                    userIdNumber = document.getString("id")
                    userName = document.getString("name")
                    userType = document.getString("userType")
                    Log.d("AppointmentActivity", "User details retrieved successfully")
                } else {
                    Log.d("AppointmentActivity", "No user details found")
                }
            }
            .addOnFailureListener { e ->
                Log.e("AppointmentActivity", "Error retrieving user details: ${e.message}", e)
            }
    }

    private fun saveAppointmentToFirestore(specialization: String, hospital: String) {
        // Ensure the user ID is not null
        if (userId == null || userEmail == null || userIdNumber == null || userName == null || userType == null || userDob == null) {
            Toast.makeText(this, "User details not available", Toast.LENGTH_SHORT).show()
            return
        }

        // Create a new appointment with the provided specialization, hospital, and user details
        val appointment = hashMapOf(
            "specialization" to specialization,
            "hospital" to hospital,
            "userId" to userId,
            "userDob" to userDob,
            "userEmail" to userEmail,
            "userIdNumber" to userIdNumber,
            "userName" to userName,
            "userType" to userType
        )

        // Add the appointment to Firestore
        firestore.collection("appointments")
            .add(appointment)
            .addOnSuccessListener {
                Toast.makeText(this, "Appointment saved successfully", Toast.LENGTH_SHORT).show()
                Log.d("AppointmentActivity", "Appointment saved successfully")
                // Clear the input fields
                editTextSpecialization.text.clear()
                editTextHospital.text.clear()
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Failed to save appointment: ${e.message}", Toast.LENGTH_SHORT).show()
                Log.e("AppointmentActivity", "Failed to save appointment", e)
            }
    }
}
