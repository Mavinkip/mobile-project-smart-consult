package com.example.predict2

import android.content.Intent
import android.os.Bundle
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class Medication : AppCompatActivity() {

    private lateinit var diseaseMedicationMapping: Map<String, Int>
    private lateinit var medicationDecoder: List<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_medication)

        // Initialize the mapping
        initializeMapping()

        val diseaseInput = findViewById<EditText>(R.id.disease_input)
        val resultText = findViewById<TextView>(R.id.result_text)

        // Retrieve the predicted disease from the intent
        val predictedDisease = intent.getStringExtra("PREDICTED_DISEASE")
        diseaseInput.setText(predictedDisease)

        // Automatically predict medication for the disease
        predictedDisease?.let {
            val medications = predictMedication(it)
            resultText.text = medications?.joinToString(", ") ?: "No medication found for this disease"

            // Return the medications to the calling activity
            val returnIntent = Intent().apply {
                putStringArrayListExtra("MEDICATIONS", ArrayList(medications))
            }
            setResult(RESULT_OK, returnIntent)
            finish() // Close the activity and return to DisplayResultActivity
        }
    }

    private fun initializeMapping() {
        diseaseMedicationMapping = mapOf(
            "Fungal infection" to 0,
            "Allergy" to 1,
            "GERD" to 2,
            "Chronic cholestasis" to 3,
            "Drug Reaction" to 4,
            "Peptic ulcer disease" to 5,
            "AIDS" to 6,
            "Diabetes" to 7,
            "Gastroenteritis" to 8,
            "Bronchial Asthma" to 9,
            "Hypertension" to 10,
            "Migraine" to 11,
            "Cervical spondylosis" to 12,
            "Paralysis (brain hemorrhage)" to 13,
            "Jaundice" to 14,
            "Malaria" to 15,
            "Chickenpox" to 16,
            "Dengue" to 17,
            "Typhoid" to 18,
            "hepatitis A" to 19,
            "Hepatitis B" to 20,
            "Hepatitis C" to 21,
            "Hepatitis D" to 22,
            "Hepatitis E" to 23,
            "Alcoholic hepatitis" to 24,
            "Tuberculosis" to 25,
            "Common Cold" to 26,
            "Pneumonia" to 27,
            "Dimorphic hemorrhoids(piles)" to 28,
            "Heart attack" to 29,
            "Varicose veins" to 30,
            "Hypothyroidism" to 31,
            "Hyperthyroidism" to 32,
            "Hypoglycemia" to 33,
            "Osteoarthritis" to 34,
            "Arthritis" to 35,
            "(vertigo) Paroxysmal Positional Vertigo" to 36,
            "Acne" to 37,
            "Urinary tract infection" to 38,
            "Psoriasis" to 39,
            "Impetigo" to 40
        )
         medicationDecoder = listOf(
            "Fluconazole - Take once daily for fungal infections",
            "Clotrimazole - Apply topically twice daily for fungal skin infections",
            "Terbinafine - Take once daily for fungal nail infections",
            "Loratadine - Take once daily for allergy symptoms",
            "Cetirizine - Take once daily for allergy symptoms",
            "Diphenhydramine - Take every 4-6 hours as needed for allergies or sleep aid",
            "Omeprazole - Take once daily in the morning for GERD",
            "Ranitidine - Take once or twice daily for heartburn or ulcers",
            "Pantoprazole - Take once daily before a meal for acid-related conditions",
            "Ursodeoxycholic acid - Take as directed for gallstones or liver conditions",
            "Rifampicin - Take as directed for tuberculosis or certain bacterial infections",
            "Cholestyramine - Take as directed for lowering cholesterol",
            "Antihistamines - Take as directed for allergy relief",
            "Corticosteroids - Use as directed for inflammation or immune disorders",
            "Proton pump inhibitors - Take as directed for reducing stomach acid",
            "H2 blockers - Take as directed for reducing stomach acid",
            "Antibiotics - Take as directed for bacterial infections",
            "Antiretroviral therapy - Take as directed for HIV treatment",
            "Metformin - Take as directed for diabetes management",
            "Insulin - Use as directed for diabetes management",
            "Glipizide - Take as directed for diabetes management",
            "Oral rehydration solution - Use as directed for dehydration",
            "Antiemetics - Use as directed for nausea and vomiting",
            "Inhalers - Use as directed for asthma or COPD",
            "Oral corticosteroids - Use as directed for inflammation or asthma",
            "ACE inhibitors - Take as directed for hypertension or heart failure",
            "Beta-blockers - Take as directed for hypertension or heart conditions",
            "Calcium channel blockers - Take as directed for hypertension or heart conditions",
            "Sumatriptan - Use as directed for migraine relief",
            "Propranolol - Take as directed for migraine prevention or heart conditions",
            "Topiramate - Take as directed for seizures or migraines",
            "Analgesics - Use as directed for pain relief",
            "Muscle relaxants - Use as directed for muscle spasms or pain",
            "Physical therapy - Follow prescribed exercises and therapies",
            "Rehabilitation therapy - Follow prescribed exercises and therapies",
            "Anticoagulants - Take as directed for blood clot prevention",
            "Liver support supplements - Take as directed for liver health",
            "Artemether-lumefantrine - Take as directed for malaria treatment",
            "Chloroquine - Take as directed for malaria treatment",
            "Doxycycline - Take as directed for bacterial infections or malaria prophylaxis",
            "Acyclovir - Take as directed for herpes or viral infections",
            "Antipyretics - Use as directed for fever reduction",
            "Fluid replacement therapy - Use as directed for dehydration",
            "Symptomatic treatment - Treat specific symptoms as directed",
            "Ciprofloxacin - Take as directed for bacterial infections",
            "Azithromycin - Take as directed for bacterial infections",
            "Ceftriaxone - Take as directed for bacterial infections",
            "Rest - Get adequate rest as needed for recovery",
            "Hydration - Maintain adequate fluid intake for health",
            "Antiviral medications - Take as directed for viral infections",
            "Interferon - Take as directed for viral infections or immune disorders",
            "Direct-acting antiviral agents - Take as directed for hepatitis C or viral infections",
            "Supportive care - Provide comfort and assistance as needed",
            "Pentoxifylline - Take as directed for vascular diseases",
            "Isoniazid - Take as directed for tuberculosis treatment",
            "Pyrazinamide - Take as directed for tuberculosis treatment",
            "Ethambutol - Take as directed for tuberculosis treatment",
            "Symptomatic relief - Provide relief for specific symptoms",
            "Oxygen therapy - Use as directed for respiratory support",
            "Oral pain relievers - Use as directed for pain relief",
            "Topical treatments - Apply as directed for skin conditions",
            "Aspirin - Use as directed for pain relief or heart conditions",
            "Thrombolytics - Use as directed for blood clot treatment",
            "Beta-blockers - Take as directed for heart conditions or hypertension",
            "Compression stockings - Wear as directed for venous conditions",
            "Sclerotherapy - Use as directed for varicose veins treatment",
            "Vein stripping - Surgical procedure for varicose veins",
            "Levothyroxine - Take as directed for thyroid hormone replacement",
            "Methimazole - Take as directed for hyperthyroidism",
            "Propylthiouracil - Take as directed for hyperthyroidism",
            "Fast-acting carbohydrates - Use as directed for hypoglycemia",
            "Acetaminophen - Use as directed for pain relief or fever reduction",
            "NSAIDs - Use as directed for pain relief or inflammation",
            "Topical analgesics - Apply as directed for localized pain relief",
            "DMARDs - Take as directed for rheumatoid arthritis",
            "Biologics - Take as directed for autoimmune diseases",
            "Epley maneuver - Perform as directed for vertigo relief",
            "Vestibular rehabilitation - Follow prescribed exercises for balance disorders",
            "Topical retinoids - Apply as directed for acne treatment",
            "Benzoyl peroxide - Apply as directed for acne treatment",
            "Oral antibiotics - Take as directed for bacterial infections",
            "Pain relievers - Use as directed for pain relief",
            "Topical corticosteroids - Apply as directed for inflammation or skin conditions",
            "Vitamin D analogs - Take as directed for vitamin D deficiency",
            "Topical antibiotics - Apply as directed for bacterial skin infections"
        )

    }

    private fun predictMedication(disease: String): List<String>? {
        val index = diseaseMedicationMapping[disease] ?: return null
        return listOf(medicationDecoder[index])
    }
}
