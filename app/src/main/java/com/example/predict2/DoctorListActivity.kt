package com.example.predict2

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class DoctorListActivity : AppCompatActivity(), DoctorAdapter.OnDoctorClickListener {
    private lateinit var doctorsRecyclerView: RecyclerView
    private lateinit var adapter: DoctorAdapter
    private val doctorList = mutableListOf<Doctor>()
    private lateinit var db: FirebaseFirestore
    private var currentPatientId: String? = null // Nullable, assuming this will hold the current patient's ID

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_doctor_list)

        doctorsRecyclerView = findViewById(R.id.doctorsRecyclerView)
        doctorsRecyclerView.layoutManager = LinearLayoutManager(this)

        adapter = DoctorAdapter(doctorList, this)
        doctorsRecyclerView.adapter = adapter

        db = FirebaseFirestore.getInstance()

        // Fetch current authenticated user (patient) ID
        val auth = FirebaseAuth.getInstance()
        val currentUser = auth.currentUser

        if (currentUser != null) {
            // User is authenticated, fetch patient's ID
            db.collection("users")
                .whereEqualTo("userType", "patient")
                .whereEqualTo("email", currentUser.email) // Filter by current patient's email
                .get()
                .addOnSuccessListener { queryDocumentSnapshots ->
                    if (!queryDocumentSnapshots.isEmpty) {
                        val patient = queryDocumentSnapshots.documents[0].toObject(Patient::class.java)
                        currentPatientId = patient?.id // Assign the patient's ID
                        loadDoctors()
                    } else {
                        Toast.makeText(this, "Patient not found", Toast.LENGTH_SHORT).show()
                    }
                }
                .addOnFailureListener { e ->
                    Toast.makeText(this, "Failed to load patient: ${e.message}", Toast.LENGTH_SHORT).show()
                }
        } else {
            // Handle case where user is not authenticated
            Toast.makeText(this, "User not authenticated", Toast.LENGTH_SHORT).show()
        }
    }

    private fun loadDoctors() {
        db.collection("users")
            .whereEqualTo("userType", "doctor")
            .get()
            .addOnSuccessListener { queryDocumentSnapshots ->
                doctorList.clear()
                for (document in queryDocumentSnapshots) {
                    val doctor = document.toObject(Doctor::class.java)
                    doctor.specialty = document.getString("specialty") ?: ""
                    doctorList.add(doctor)
                }
                adapter.notifyDataSetChanged()
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Failed to load doctors: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    override fun onDoctorClick(doctor: Doctor) {
        currentPatientId?.let {
            // Navigate to ChatActivity when a doctor is clicked
            val intent = Intent(this, ChatActivity::class.java).apply {
                putExtra("senderId", it)  // Set patient's ID as senderId
                putExtra("receiverId", doctor.id) // Set doctor's ID as receiverId
            }
            startActivity(intent)
        } ?: run {
            Toast.makeText(this, "Patient ID not initialized", Toast.LENGTH_SHORT).show()
        }
    }
}
