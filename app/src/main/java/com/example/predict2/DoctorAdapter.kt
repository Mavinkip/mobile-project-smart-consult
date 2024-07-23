package com.example.predict2

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class DoctorAdapter(
    private val doctors: List<Doctor>,
    private val listener: OnDoctorClickListener
) : RecyclerView.Adapter<DoctorAdapter.DoctorViewHolder>() {

    interface OnDoctorClickListener {
        fun onDoctorClick(doctor: Doctor)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DoctorViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_doctor, parent, false)
        return DoctorViewHolder(view)
    }

    override fun onBindViewHolder(holder: DoctorViewHolder, position: Int) {
        val doctor = doctors[position]
        holder.bind(doctor)
        holder.itemView.setOnClickListener {
            listener.onDoctorClick(doctor)
        }
    }

    override fun getItemCount(): Int {
        return doctors.size
    }

    inner class DoctorViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val nameTextView: TextView = itemView.findViewById(R.id.doctorNameTextView)
        private val specialtyTextView: TextView = itemView.findViewById(R.id.doctorSpecialtyTextView)

        fun bind(doctor: Doctor) {
            nameTextView.text = doctor.name
            specialtyTextView.text = doctor.specialty
        }
    }
}
