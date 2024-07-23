package com.example.predict2

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.widget.Toast
import com.google.firebase.firestore.FirebaseFirestore

class UserListActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var userAdapter: UserAdapter
    private lateinit var db: FirebaseFirestore
    private lateinit var patientId: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_list)

        // Get patientId from Intent
        patientId = intent.getStringExtra("patientId") ?: ""

        // Initialize RecyclerView
        recyclerView = findViewById(R.id.recyclerViewUsers)
        recyclerView.layoutManager = LinearLayoutManager(this)
        userAdapter = UserAdapter()
        recyclerView.adapter = userAdapter

        // Initialize Firestore
        db = FirebaseFirestore.getInstance()

        // Fetch users from Firestore
        fetchUsers()
    }

    private fun fetchUsers() {
        db.collection("users")
            .whereEqualTo("userType", "patient")
            .whereEqualTo("id", patientId)
            .get()
            .addOnSuccessListener { documents ->
                val userList = documents.map { document ->
                    User(
                        id = document.getString("id") ?: "",
                        name = document.getString("name") ?: "",
                        phoneNumber = document.getString("phoneNumber") ?: "",
                        dob = document.getString("dob") ?: "",
                        email = document.getString("email") ?: "",
                        userType = document.getString("userType") ?: ""
                    )
                }
                userAdapter.setUsers(userList)
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Failed to fetch user data: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }
}
