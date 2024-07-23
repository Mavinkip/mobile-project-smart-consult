package com.example.predict2

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View.generateViewId
import android.widget.Button
import android.widget.CheckBox
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import org.tensorflow.lite.Interpreter
import java.io.FileInputStream
import java.nio.MappedByteBuffer
import java.nio.channels.FileChannel

class MainActivity : AppCompatActivity() {

    private val symptomsArray = listOf(
        "back_pain", "constipation", "abdominal_pain", "diarrhoea", "mild_fever", "yellow_urine",
        "yellowing_of_eyes", "acute_liver_failure", "fluid_overload", "swelling_of_stomach",
        "swelled_lymph_nodes", "malaise", "blurred_and_distorted_vision", "phlegm", "throat_irritation",
        "redness_of_eyes", "sinus_pressure", "runny_nose", "congestion", "chest_pain", "weakness_in_limbs",
        "fast_heart_rate", "pain_during_bowel_movements", "pain_in_anal_region", "bloody_stool",
        "irritation_in_anus", "neck_pain", "dizziness", "cramps", "bruising", "obesity", "swollen_legs",
        "swollen_blood_vessels", "puffy_face_and_eyes", "enlarged_thyroid", "brittle_nails",
        "swollen_extremeties", "excessive_hunger", "extra_marital_contacts", "drying_and_tingling_lips",
        "slurred_speech", "knee_pain", "hip_joint_pain", "muscle_weakness", "stiff_neck", "swelling_joints",
        "movement_stiffness", "spinning_movements", "loss_of_balance", "unsteadiness",
        "weakness_of_one_body_side", "loss_of_smell", "bladder_discomfort", "foul_smell_of urine",
        "continuous_feel_of_urine", "passage_of_gases", "internal_itching", "toxic_look_(typhos)",
        "depression", "irritability", "muscle_pain", "altered_sensorium", "red_spots_over_body", "belly_pain",
        "abnormal_menstruation", "dischromic _patches", "watering_from_eyes", "increased_appetite", "polyuria",
        "family_history", "mucoid_sputum", "rusty_sputum", "lack_of_concentration", "visual_disturbances",
        "receiving_blood_transfusion", "receiving_unsterile_injections", "coma", "stomach_bleeding",
        "distention_of_abdomen", "history_of_alcohol_consumption", "fluid_overload", "blood_in_sputum",
        "prominent_veins_on_calf", "palpitations", "painful_walking", "pus_filled_pimples", "blackheads",
        "scurring", "skin_peeling", "silver_like_dusting", "small_dents_in_nails", "inflammatory_nails",
        "blister", "red_sore_around_nose", "yellow_crust_ooze"
    )
    // Define the list of diseases
    private val diseasesArray = listOf(
        "Fungal infection", "Allergy", "GERD", "Chronic cholestasis", "Drug Reaction",
        "Peptic ulcer diseae", "AIDS", "Diabetes", "Gastroenteritis", "Bronchial Asthma",
        "Hypertension", "Migraine", "Cervical spondylosis", "Paralysis (brain hemorrhage)", "Jaundice",
        "Malaria", "Chicken pox", "Dengue", "Typhoid", "hepatitis A", "Hepatitis B", "Hepatitis C", "Hepatitis D",
        "Hepatitis E", "Alcoholic hepatitis", "Tuberculosis", "Common Cold", "Pneumonia", "Dimorphic hemmorhoids(piles)",
        "Heartattack", "Varicoseveins", "Hypothyroidism", "Hyperthyroidism", "Hypoglycemia", "Osteoarthristis", "Arthritis",
        "(vertigo) Paroymsal Positional Vertigo", "Acne", "Urinary tract infection", "Psoriasis", "Impetigo"
    )

    private lateinit var tflite: Interpreter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Find views by their IDs
        val predictButton = findViewById<Button>(R.id.buttonPredict)
        val resultTextView = findViewById<TextView>(R.id.textViewResult)

        // Load TensorFlow Lite model
        tflite = Interpreter(loadModelFile(this))

        // Populate symptoms dynamically
        val symptomsLayout = findViewById<LinearLayout>(R.id.symptoms_layout)
        symptomsArray.forEach { symptom ->
            val checkBox = CheckBox(this).apply {
                text = symptom
                id = generateViewId() // Generate a unique ID for each CheckBox
            }
            symptomsLayout.addView(checkBox)
        }

        // Handle prediction button click
        predictButton.setOnClickListener {
            val selectedSymptoms = mutableListOf<String>()
            for (i in 0 until symptomsLayout.childCount) {
                val checkBox = symptomsLayout.getChildAt(i) as CheckBox
                if (checkBox.isChecked) {
                    selectedSymptoms.add(checkBox.text.toString())
                }
            }

            // Perform prediction
            val predictedDisease = predictDisease(selectedSymptoms)

            // Display result
            resultTextView.text = "Predicted Disease: $predictedDisease\n\nSelected Symptoms:\n${selectedSymptoms.joinToString("\n")}"

            val intent = Intent(this, DisplayResultActivity::class.java).apply {
                putExtra("PREDICTED_DISEASE", predictedDisease)
                putStringArrayListExtra("SELECTED_SYMPTOMS", ArrayList(selectedSymptoms))
            }
            startActivity(intent)
        }
    }

    // Function to load TensorFlow Lite model
    private fun loadModelFile(activity: Activity): MappedByteBuffer {
        val assetFileDescriptor = activity.assets.openFd("my_model.tflite")
        val inputStream = FileInputStream(assetFileDescriptor.fileDescriptor)
        val fileChannel = inputStream.channel
        val startOffset = assetFileDescriptor.startOffset
        val declaredLength = assetFileDescriptor.declaredLength
        return fileChannel.map(FileChannel.MapMode.READ_ONLY, startOffset, declaredLength)
    }

    // Function to perform prediction using TensorFlow Lite model
    private fun predictDisease(selectedSymptoms: List<String>): String {
        // Convert selected symptoms to input vector
        val inputVector = FloatArray(symptomsArray.size)
        for (i in symptomsArray.indices) {
            inputVector[i] = if (symptomsArray[i] in selectedSymptoms) 1.0f else 0.0f
        }

        // Run inference
        val outputVector = Array(1) { FloatArray(diseasesArray.size) } // Use diseasesArray size
        tflite.run(inputVector, outputVector)

        // Get predicted disease
        val maxIndex = outputVector[0].indices.maxByOrNull { outputVector[0][it] } ?: 0
        return diseasesArray[maxIndex]
    }
}
