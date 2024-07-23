package com.example.predict2

data class Prescription(
    val predictedDisease: String? = null,
    val selectedSymptoms: String? = null,
    val medications: String? = null,
    val timestamp: String? = null
)
