package com.example.predict2

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import com.google.firebase.auth.FirebaseAuth

class Splash : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        auth = FirebaseAuth.getInstance()

        // Using a Handler to delay the navigation
        Handler(Looper.getMainLooper()).postDelayed({
            val currentUser = auth.currentUser
            if (currentUser != null && currentUser.isEmailVerified) {
                // User is logged in and email is verified, navigate to MainActivity or Drawer activity
                val intent = Intent(this, Welcome::class.java)
                startActivity(intent)
            } else {
                // User is not logged in or email is not verified, navigate to LoginActivity
                val intent = Intent(this, Welcome::class.java)
                startActivity(intent)
            }

            // Finish the splash activity so the user can't go back to it
            finish()
        }, 1000) // Delay duration in milliseconds
    }
}
