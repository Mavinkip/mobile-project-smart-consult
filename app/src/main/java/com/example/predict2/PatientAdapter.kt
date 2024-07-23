package com.example.predict2

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView


class PatientAdapter(
    private val context: Context,
    private val patients: List<Patient>,
    private val onPatientClickListener: OnPatientClickListener
) : RecyclerView.Adapter<PatientAdapter.PatientViewHolder>() {

    interface OnPatientClickListener {
        fun onPatientClick(patient: Patient)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PatientViewHolder {
        val view = LayoutInflater.from(context).inflate(android.R.layout.simple_list_item_1, parent, false)
        return PatientViewHolder(view)
    }

    override fun onBindViewHolder(holder: PatientViewHolder, position: Int) {
        val patient = patients[position]
        holder.nameTextView.text = patient.name
        holder.itemView.setOnClickListener {
            onPatientClickListener.onPatientClick(patient)
        }
    }

    override fun getItemCount(): Int {
        return patients.size
    }

    // ViewHolder for holding the view components
    class PatientViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val nameTextView: TextView = itemView.findViewById(android.R.id.text1)
    }
}
