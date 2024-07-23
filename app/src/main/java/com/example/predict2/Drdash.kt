package com.example.predict2

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat

import androidx.core.view.WindowInsetsCompat
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.core.View

class Drdash : AppCompatActivity() {
    private var doctorId: String? = null
    private var patientId: String? = null
    private lateinit var db: FirebaseFirestore
    private lateinit var messageCountTextView: TextView
    private lateinit var appointmentCountTextView: TextView

    private var isChatClicked = false
    private var isAppointmentClicked = false

    private var lastMessageTimestamp: Long = 0
    private var lastAppointmentTimestamp: Long = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_drdash)

        db = FirebaseFirestore.getInstance()

        doctorId = intent.getStringExtra("doctorId")
        patientId = intent.getStringExtra("patientId")

        Log.d("Drdash", "Received Doctor ID: $doctorId, Patient ID: $patientId")

        Toast.makeText(this, "Received Doctor ID: $doctorId, Patient ID: $patientId", Toast.LENGTH_LONG).show()

        messageCountTextView = findViewById(R.id.messageCountTextView)
        appointmentCountTextView = findViewById(R.id.appointmentCountTextView)

        findViewById<CardView>(R.id.cardPatients).setOnClickListener {
            val intent = Intent(this, UserListActivity::class.java).apply {
                putExtra("patientId", patientId)
            }
            startActivity(intent)
        }

        findViewById<CardView>(R.id.cardReports).setOnClickListener {
            val intent = Intent(this, FullReportsActivity::class.java).apply {
                putExtra("patientId", patientId)
            }
            startActivity(intent)
        }

        findViewById<CardView>(R.id.cardChat).setOnClickListener {
            isChatClicked = true
            findViewById<CardView>(R.id.cardChat).setCardBackgroundColor(
                ContextCompat.getColor(this, android.R.color.white)) // Set to default color when clicked

            // Remove notification icon
            val cardChatLayout = findViewById<LinearLayout>(R.id.cardChatLayout)
            val notificationIcon = cardChatLayout.findViewWithTag<ImageView>("notificationIcon")
            notificationIcon?.let { cardChatLayout.removeView(it) }

            val intent = Intent(this, ChatActivity::class.java).apply {
                putExtra("senderId", doctorId)
                putExtra("receiverId", patientId)
            }
            startActivity(intent)
        }

        findViewById<CardView>(R.id.cardAppointment).setOnClickListener {
            isAppointmentClicked = true
            findViewById<CardView>(R.id.cardAppointment).setCardBackgroundColor(
                ContextCompat.getColor(this, android.R.color.white)) // Set to default color when clicked

            // Remove notification icon
            val cardAppointmentLayout = findViewById<LinearLayout>(R.id.cardAppointmentLayout)
            val notificationIcon = cardAppointmentLayout.findViewWithTag<ImageView>("notificationIcon")
            notificationIcon?.let { cardAppointmentLayout.removeView(it) }

            val intent = Intent(this, DoctorAppointmentActivity::class.java).apply {
                putExtra("patientId", patientId)
            }
            startActivity(intent)
        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Display number of messages
        displayMessageCount()

        // Display number of appointments
        displayAppointmentCount()
    }

    private fun displayMessageCount() {
        db.collection("chats")
            .document("$doctorId-$patientId")
            .collection("messages")
            .orderBy("timestamp", Query.Direction.DESCENDING)
            .limit(1)
            .addSnapshotListener { querySnapshot, firebaseFirestoreException ->
                if (firebaseFirestoreException != null) {
                    Toast.makeText(this, "Failed to get message count: ${firebaseFirestoreException.message}", Toast.LENGTH_SHORT).show()
                    return@addSnapshotListener
                }

                if (querySnapshot != null && !querySnapshot.isEmpty) {
                    val latestMessageTimestamp = querySnapshot.documents[0].getLong("timestamp") ?: 0

                    if (latestMessageTimestamp > lastMessageTimestamp) {
                        lastMessageTimestamp = latestMessageTimestamp
                        val newMessageCount = querySnapshot.size()

                        // Display new message count
                        messageCountTextView.text = "New Messages: $newMessageCount"

                        // Change card background only if not clicked and add notification icon
                        if (!isChatClicked && newMessageCount > 0) {
                            val cardChat = findViewById<CardView>(R.id.cardChat)
                            cardChat.setCardBackgroundColor(ContextCompat.getColor(this, R.color.green)) // Change to your desired color resource

                            // Add notification icon
                            val notificationIcon = ImageView(this).apply {
                                setImageResource(R.drawable.baseline_notifications_active_24)
                                tag = "notificationIcon"
                                val params = RelativeLayout.LayoutParams(60, 60).apply {
                                    addRule(RelativeLayout.ALIGN_PARENT_END)
                                    setMargins(0, 16, 16, 0)
                                }
                                layoutParams = params
                            }

                            val cardChatLayout = cardChat.findViewById<LinearLayout>(R.id.cardChatLayout)
                            cardChatLayout.addView(notificationIcon)
                        }
                    } else {
                        // Remove notification icon if no new messages
                        val cardChatLayout = findViewById<LinearLayout>(R.id.cardChatLayout)
                        val notificationIcon = cardChatLayout.findViewWithTag<ImageView>("notificationIcon")
                        notificationIcon?.let { cardChatLayout.removeView(it) }
                    }
                }
            }
    }

    private var newAppointmentCount: Int = 0 // Initialize this with the appropriate value

    private fun displayAppointmentCount() {
        appointmentCountTextView = findViewById(R.id.appointmentCountTextView)

        db.collection("appointments")
            .whereEqualTo("userIdNumber", patientId)
            .get()
            .addOnSuccessListener { querySnapshot ->
                val appointmentCount = querySnapshot.size()
                appointmentCountTextView.text = "Appointments: $appointmentCount"
            }
            .addOnFailureListener { exception ->
                Toast.makeText(this, "Failed to get appointment count: ${exception.message}", Toast.LENGTH_SHORT).show()
            }

        messageCountTextView.text = "New Appointment: $newAppointmentCount"

        // Change card background only if not clicked and add notification icon
        val cardAppointmentLayout = findViewById<LinearLayout>(R.id.cardAppointmentLayout)
        if (!isChatClicked && newAppointmentCount > 0) {
            cardAppointmentLayout.setBackgroundColor(ContextCompat.getColor(this, R.color.green)) // Change to your desired color resource

            // Add notification icon
            val notificationIcon = ImageView(this).apply {
                setImageResource(R.drawable.baseline_notifications_active_24)
                tag = "notificationIcon"
                val params = RelativeLayout.LayoutParams(60, 60).apply {
                    addRule(RelativeLayout.ALIGN_PARENT_END)
                    setMargins(0, 16, 16, 0)
                }
                layoutParams = params
            }

            cardAppointmentLayout.addView(notificationIcon)
        } else {
            // Remove notification icon if no new messages
            val notificationIcon = cardAppointmentLayout.findViewWithTag<ImageView>("notificationIcon")
            notificationIcon?.let { cardAppointmentLayout.removeView(it) }
        }
    }
}




