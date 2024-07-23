package com.example.predict2

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class PrescriptionAdapter(private val prescriptions: List<Prescription>) : RecyclerView.Adapter<PrescriptionAdapter.PrescriptionViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PrescriptionViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.item_prescription, parent, false)
        return PrescriptionViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: PrescriptionViewHolder, position: Int) {
        val prescription = prescriptions[position]
        holder.predictedDiseaseTextView.text = prescription.predictedDisease
        holder.selectedSymptomsTextView.text = prescription.selectedSymptoms
        holder.medicationsTextView.text = prescription.medications
        holder.timestampTextView.text = prescription.timestamp
    }

    override fun getItemCount() = prescriptions.size

    class PrescriptionViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val predictedDiseaseTextView: TextView = itemView.findViewById(R.id.textViewPredictedDiseaseValue)
        val selectedSymptomsTextView: TextView = itemView.findViewById(R.id.textViewSelectedSymptomsValue)
        val medicationsTextView: TextView = itemView.findViewById(R.id.textViewMedicationsValue)
        val timestampTextView: TextView = itemView.findViewById(R.id.textViewTimestampValue)
    }
}
