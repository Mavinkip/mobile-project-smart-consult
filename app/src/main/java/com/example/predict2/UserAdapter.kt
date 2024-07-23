package com.example.predict2

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class UserAdapter : RecyclerView.Adapter<UserAdapter.UserViewHolder>() {

    private var users: List<User> = emptyList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_user, parent, false)
        return UserViewHolder(view)
    }

    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
        holder.bind(users[position])
    }

    override fun getItemCount(): Int {
        return users.size
    }

    fun setUsers(users: List<User>) {
        this.users = users
        notifyDataSetChanged()
    }

    class UserViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val textViewName: TextView = itemView.findViewById(R.id.textViewName)
        private val textViewEmail: TextView = itemView.findViewById(R.id.textViewEmail)
        private val textViewPhoneNumber: TextView = itemView.findViewById(R.id.textViewPhoneNumber)
        private val textViewDob: TextView = itemView.findViewById(R.id.textViewDob)
        private val textViewUserType: TextView = itemView.findViewById(R.id.textViewUserType)

        fun bind(user: User) {
            textViewName.text = user.name
            textViewEmail.text = user.email
            textViewPhoneNumber.text = user.phoneNumber
            textViewDob.text = user.dob
            textViewUserType.text = user.userType
        }
    }
}
