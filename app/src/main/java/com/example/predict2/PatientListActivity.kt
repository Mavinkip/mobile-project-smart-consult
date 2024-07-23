package com.example.predict2

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class PatientListActivity : AppCompatActivity(), PatientAdapter.OnPatientClickListener {

    private lateinit var db: FirebaseFirestore
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: PatientAdapter
    private val patientList = mutableListOf<Patient>()
    private var currentDoctorId: String? = null  // Initialize as nullable

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_patient_list)

        recyclerView = findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)
        adapter = PatientAdapter(this, patientList, this)
        recyclerView.adapter = adapter

        db = FirebaseFirestore.getInstance()

        // Fetch current authenticated user (doctor) ID
        val auth = FirebaseAuth.getInstance()
        val currentUser = auth.currentUser

        if (currentUser != null) {
            // User is authenticated, fetch doctor's ID
            db.collection("users")
                .whereEqualTo("userType", "doctor")
                .whereEqualTo("email", currentUser.email) // Filter by current doctor's email
                .get()
                .addOnSuccessListener { queryDocumentSnapshots ->
                    if (!queryDocumentSnapshots.isEmpty) {
                        val doctor = queryDocumentSnapshots.documents[0].toObject(Doctor::class.java)
                        currentDoctorId = doctor?.id // Assign the doctor's ID
                        loadPatients()
                    } else {
                        Toast.makeText(this, "Doctor not found", Toast.LENGTH_SHORT).show()
                    }
                }
                .addOnFailureListener { e ->
                    Toast.makeText(this, "Failed to load doctor: ${e.message}", Toast.LENGTH_SHORT).show()
                }
        } else {
            // Handle case where user is not authenticated
            Toast.makeText(this, "User not authenticated", Toast.LENGTH_SHORT).show()
        }
    }

    private fun loadPatients() {
        db.collection("users")
            .whereEqualTo("userType", "patient")
            .get()
            .addOnSuccessListener { queryDocumentSnapshots ->
                patientList.clear()
                for (document in queryDocumentSnapshots) {
                    val patient = document.toObject(Patient::class.java)
                    patientList.add(patient)
                }
                adapter.notifyDataSetChanged()
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Failed to load patients: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    override fun onPatientClick(patient: Patient) {
        currentDoctorId?.let { doctorId ->
            // Log the IDs being sent
            Log.d("PatientListActivity", "Doctor ID: $doctorId, Patient ID: ${patient.id}")

            // Show a toast message with the IDs
            Toast.makeText(this, "Sending Doctor ID: $doctorId, Patient ID: ${patient.id}", Toast.LENGTH_LONG).show()

            // Navigate to Drdash when a patient is clicked
            val intent = Intent(this, Drdash::class.java).apply {
                putExtra("doctorId", doctorId)  // Pass doctor's ID
                putExtra("patientId", patient.id)  // Pass patient's ID
            }
            startActivity(intent)
        } ?: run {
            Toast.makeText(this, "Doctor ID not initialized", Toast.LENGTH_SHORT).show()
        }
    }
}
