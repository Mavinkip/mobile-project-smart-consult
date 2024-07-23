package com.example.predict2

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import java.text.SimpleDateFormat
import java.util.*

class DisplayResultActivity : AppCompatActivity() {

    private lateinit var showMedicationButton: Button
    private lateinit var medicationsTextView: TextView

    private var predictedDisease: String? = null
    private var selectedSymptoms: ArrayList<String>? = null
    private var medications: ArrayList<String>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_display_result)

        // Retrieve the predicted disease and selected symptoms from intent
        predictedDisease = intent.getStringExtra("PREDICTED_DISEASE")
        selectedSymptoms = intent.getStringArrayListExtra("SELECTED_SYMPTOMS")

        // Initialize views
        val predictedDiseaseTextView = findViewById<TextView>(R.id.textViewPredictedDiseaseValue)
        val selectedSymptomsTextView = findViewById<TextView>(R.id.textViewSelectedSymptomsValue)
        medicationsTextView = findViewById(R.id.medications_text_view)
        showMedicationButton = findViewById(R.id.medic)

        // Display the predicted disease and selected symptoms
        predictedDiseaseTextView.text = predictedDisease
        selectedSymptomsTextView.text = selectedSymptoms?.joinToString("\n")

        // Setup button click listener
        showMedicationButton.setOnClickListener {
            val intent = Intent(this, Medication::class.java).apply {
                putExtra("PREDICTED_DISEASE", predictedDisease)
            }
            startActivityForResult(intent, REQUEST_CODE_MEDICATION)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE_MEDICATION && resultCode == RESULT_OK) {
            medications = data?.getStringArrayListExtra("MEDICATIONS")
            medicationsTextView.text = medications?.joinToString(", ") ?: "No medication found for this disease"

            // Format and display prescription in PrescriptionActivity
            val intent = Intent(this, PrescriptionActivity::class.java).apply {
                putExtra("PREDICTED_DISEASE", predictedDisease)
                putExtra("SELECTED_SYMPTOMS", selectedSymptoms?.joinToString("\n"))
                putExtra("MEDICATIONS", medications?.joinToString(", ","\n") ?: "No medication found for this disease")
                putExtra("TIMESTAMP", getCurrentTimestamp())
            }
            startActivity(intent)
        }
    }

    private fun getCurrentTimestamp(): String {
        return SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(Date())
    }

    companion object {
        private const val REQUEST_CODE_MEDICATION = 1
    }
}
