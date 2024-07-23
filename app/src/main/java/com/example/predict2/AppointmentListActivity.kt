package com.example.predict2

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class AppointmentListActivity : AppCompatActivity() {

    private lateinit var firestore: FirebaseFirestore
    private lateinit var recyclerView: RecyclerView
    private lateinit var appointmentAdapter: AppointmentAdapter
    private val appointmentList = mutableListOf<Appointment>()
    private lateinit var auth: FirebaseAuth // Declare FirebaseAuth instance

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_appointment_list)

        // Initialize Firestore and FirebaseAuth
        firestore = FirebaseFirestore.getInstance()
        auth = FirebaseAuth.getInstance()

        // Initialize RecyclerView
        recyclerView = findViewById(R.id.recyclerViewAppointments)
        recyclerView.layoutManager = LinearLayoutManager(this)
        appointmentAdapter = AppointmentAdapter(appointmentList)
        recyclerView.adapter = appointmentAdapter

        // Retrieve appointments from Firestore
        retrieveAppointmentsFromFirestore()
    }

    private fun retrieveAppointmentsFromFirestore() {
        Log.d("AppointmentListActivity", "Retrieving appointments from Firestore")

        // Get current user ID
        val currentUser = auth.currentUser
        val currentUserId = currentUser?.uid

        if (currentUserId == null) {
            Toast.makeText(this, "User not logged in", Toast.LENGTH_SHORT).show()
            return
        }

        firestore.collection("appointments")
            .whereEqualTo("userType", "patient")
            .whereEqualTo("userId", currentUserId)
            .get()
            .addOnSuccessListener { documents ->
                appointmentList.clear() // Clear the list before adding new appointments
                if (!documents.isEmpty) {
                    Log.d("AppointmentListActivity", "Appointments found: ${documents.size()}")
                    for (document in documents) {
                        val appointment = document.toObject(Appointment::class.java)
                        appointmentList.add(appointment)
                        Log.d("AppointmentListActivity", "Appointment added: $appointment")
                    }
                    appointmentAdapter.notifyDataSetChanged()
                } else {
                    Log.d("AppointmentListActivity", "No appointments found")
                    Toast.makeText(this, "No appointments found", Toast.LENGTH_SHORT).show()
                }
            }
            .addOnFailureListener { e ->
                Log.e("AppointmentListActivity", "Error retrieving appointments: ${e.message}", e)
                Toast.makeText(this, "Error retrieving appointments: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }
}
