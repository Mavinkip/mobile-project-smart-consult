package com.example.predict2

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class AppointmentAdapter(private val appointmentList: List<Appointment>) :
    RecyclerView.Adapter<AppointmentAdapter.AppointmentViewHolder>() {

    class AppointmentViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val specializationTextView: TextView = view.findViewById(R.id.textViewSpecialization)
        val hospitalTextView: TextView = view.findViewById(R.id.textViewHospital)
        val timeTextView: TextView = view.findViewById(R.id.textViewTime)  // Add time TextView
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AppointmentViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_appointment, parent, false)
        return AppointmentViewHolder(view)
    }

    override fun onBindViewHolder(holder: AppointmentViewHolder, position: Int) {
        val appointment = appointmentList[position]
        holder.specializationTextView.text = appointment.specialization
        holder.hospitalTextView.text = appointment.hospital
        holder.timeTextView.text = appointment.time  // Bind time data
    }

    override fun getItemCount(): Int {
        return appointmentList.size
    }
}
