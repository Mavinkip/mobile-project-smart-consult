package com.example.predict2

import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class PrescriptionActivity : AppCompatActivity() {

    private lateinit var firestore: FirebaseFirestore
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_prescription)

        // Initialize Firebase Auth and Firestore
        auth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()

        // Retrieve data from intent
        val predictedDisease = intent.getStringExtra("PREDICTED_DISEASE")
        val selectedSymptoms = intent.getStringExtra("SELECTED_SYMPTOMS")
        val medications = intent.getStringExtra("MEDICATIONS")
        val timestamp = intent.getStringExtra("TIMESTAMP") ?: getCurrentTimestamp()

        // Initialize views
        val predictedDiseaseTextView = findViewById<TextView>(R.id.textViewPredictedDiseaseValue)
        val selectedSymptomsTextView = findViewById<TextView>(R.id.textViewSelectedSymptomsValue)
        val medicationsTextView = findViewById<TextView>(R.id.textViewMedicationsValue)
        val timestampTextView = findViewById<TextView>(R.id.textViewTimestampValue)

        // Display data in views
        predictedDiseaseTextView.text = predictedDisease
        selectedSymptomsTextView.text = selectedSymptoms
        medicationsTextView.text = medications
        timestampTextView.text = timestamp

        // Save data to Firestore
        savePrescriptionToFirestore(predictedDisease, selectedSymptoms, medications, timestamp)
    }

    private fun savePrescriptionToFirestore(predictedDisease: String?, selectedSymptoms: String?, medications: String?, timestamp: String) {
        val user = auth.currentUser
        if (user != null) {
            val prescription = hashMapOf(
                "predictedDisease" to predictedDisease,
                "selectedSymptoms" to selectedSymptoms,
                "medications" to medications,
                "timestamp" to timestamp
            )

            firestore.collection("users").document(user.uid).collection("prescriptions")
                .add(prescription)
                .addOnSuccessListener {
                    Toast.makeText(this, "Prescription saved successfully", Toast.LENGTH_SHORT).show()
                }
                .addOnFailureListener { e ->
                    Toast.makeText(this, "Error saving prescription: ${e.message}", Toast.LENGTH_SHORT).show()
                }
        } else {
            Toast.makeText(this, "User not logged in", Toast.LENGTH_SHORT).show()
        }
    }

    private fun getCurrentTimestamp(): String {
        val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
        return sdf.format(Date())
    }
}
