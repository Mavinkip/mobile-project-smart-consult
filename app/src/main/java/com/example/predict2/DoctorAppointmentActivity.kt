package com.example.predict2

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore

class DoctorAppointmentActivity : AppCompatActivity() {

    private lateinit var firestore: FirebaseFirestore
    private lateinit var recyclerView: RecyclerView
    private lateinit var appointmentAdapter: AppointmentAdapter
    private val appointmentList = mutableListOf<Appointment>()
    private var patientId: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_appointment_list)

        // Get patientId from intent
        patientId = intent.getStringExtra("patientId")

        // Initialize Firestore
        firestore = FirebaseFirestore.getInstance()

        // Initialize RecyclerView
        recyclerView = findViewById(R.id.recyclerViewAppointments)
        recyclerView.layoutManager = LinearLayoutManager(this)
        appointmentAdapter = AppointmentAdapter(appointmentList)
        recyclerView.adapter = appointmentAdapter

        // Retrieve appointments from Firestore
        retrieveAppointmentsFromFirestore()
    }

    private fun retrieveAppointmentsFromFirestore() {
        if (patientId == null) {
            Log.e("DoctorAppointmentActivity", "Patient ID is null")
            Toast.makeText(this, "Invalid patient ID", Toast.LENGTH_SHORT).show()
            return
        }

        Log.d("DoctorAppointmentActivity", "Retrieving appointments for patient ID: $patientId")

        firestore.collection("appointments")
            .whereEqualTo("userType", "patient")
            .whereEqualTo("userIdNumber", patientId) // Adjusted to filter by userIdNumber
            .get()
            .addOnSuccessListener { documents ->
                if (!documents.isEmpty) {
                    Log.d("DoctorAppointmentActivity", "Appointments found: ${documents.size()}")
                    for (document in documents) {
                        val appointment = document.toObject(Appointment::class.java)
                        appointmentList.add(appointment)
                        Log.d("DoctorAppointmentActivity", "Appointment added: $appointment")
                    }
                    appointmentAdapter.notifyDataSetChanged()
                } else {
                    Log.d("DoctorAppointmentActivity", "No appointments found for patient ID: $patientId")
                    Toast.makeText(this, "No appointments found", Toast.LENGTH_SHORT).show()
                }
            }
            .addOnFailureListener { e ->
                Log.e("DoctorAppointmentActivity", "Error retrieving appointments: ${e.message}", e)
                Toast.makeText(this, "Error retrieving appointments: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }
}
