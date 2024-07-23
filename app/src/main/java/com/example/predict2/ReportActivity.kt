package com.example.predict2

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class ReportActivity : AppCompatActivity() {

    private lateinit var firestore: FirebaseFirestore
    private lateinit var auth: FirebaseAuth
    private lateinit var recyclerView: RecyclerView
    private lateinit var prescriptionAdapter: PrescriptionAdapter
    private val prescriptionList = mutableListOf<Prescription>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_report)

        // Initialize Firebase Auth and Firestore
        auth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()

        // Initialize RecyclerView
        recyclerView = findViewById(R.id.recyclerViewPrescriptions)
        recyclerView.layoutManager = LinearLayoutManager(this)
        prescriptionAdapter = PrescriptionAdapter(prescriptionList)
        recyclerView.adapter = prescriptionAdapter

        // Retrieve data from Firestore
        retrievePrescriptionsFromFirestore()
    }

    private fun retrievePrescriptionsFromFirestore() {
        val user = auth.currentUser
        if (user != null) {
            firestore.collection("users").document(user.uid).collection("prescriptions")
                .get()
                .addOnSuccessListener { documents ->
                    if (!documents.isEmpty) {
                        for (document in documents) {
                            val prescription = document.toObject(Prescription::class.java)
                            prescriptionList.add(prescription)
                        }
                        prescriptionAdapter.notifyDataSetChanged()
                    } else {
                        Toast.makeText(this, "No prescription data found", Toast.LENGTH_SHORT).show()
                    }
                }
                .addOnFailureListener { e ->
                    Toast.makeText(this, "Error retrieving prescriptions: ${e.message}", Toast.LENGTH_SHORT).show()
                }
        } else {
            Toast.makeText(this, "User not logged in", Toast.LENGTH_SHORT).show()
        }
    }
}
