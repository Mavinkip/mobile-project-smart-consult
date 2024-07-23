package com.example.predict2

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore

class FullReportsActivity : AppCompatActivity() {

    private lateinit var firestore: FirebaseFirestore
    private lateinit var recyclerView: RecyclerView
    private lateinit var prescriptionAdapter: PrescriptionAdapter
    private val prescriptionList = mutableListOf<Prescription>()
    private var patientId: String? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_report)
        patientId = intent.getStringExtra("patientId")




        // Initialize Firebase Firestore
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
        Log.d("FullReportsActivity", "Retrieving prescriptions from Firestore")

        firestore.collection("users")                            .whereEqualTo("userType", "patient")
            .whereEqualTo("userType", "patient")
            .whereEqualTo("id", patientId)



            .get()
            .addOnSuccessListener { userDocuments ->
                if (!userDocuments.isEmpty) {
                    Log.d("FullReportsActivity", "Users found: ${userDocuments.size()}")
                    for (userDocument in userDocuments) {
                        val userId = userDocument.id
                        firestore.collection("users")
                            .document(userId).collection("prescriptions")
                            .get()
                            .addOnSuccessListener { prescriptionDocuments ->
                                if (!prescriptionDocuments.isEmpty) {
                                    Log.d("FullReportsActivity", "Prescriptions found for user $userId: ${prescriptionDocuments.size()}")
                                    for (prescriptionDocument in prescriptionDocuments) {
                                        val prescription = prescriptionDocument.toObject(Prescription::class.java)
                                        prescriptionList.add(prescription)
                                        Log.d("FullReportsActivity", "Prescription added: $prescription")
                                    }
                                    prescriptionAdapter.notifyDataSetChanged()
                                } else {
                                    Log.d("FullReportsActivity", "No prescription data found for user $userId")
                                }
                            }
                            .addOnFailureListener { e ->
                                Log.e("FullReportsActivity", "Error retrieving prescriptions for user $userId: ${e.message}", e)
                                Toast.makeText(this, "Error retrieving prescriptions for user $userId: ${e.message}", Toast.LENGTH_SHORT).show()
                            }
                    }
                } else {
                    Log.d("FullReportsActivity", "No users found")
                    Toast.makeText(this, "No users found", Toast.LENGTH_SHORT).show()
                }
            }
            .addOnFailureListener { e ->
                Log.e("FullReportsActivity", "Error retrieving users: ${e.message}", e)
                Toast.makeText(this, "Error retrieving users: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }
}
