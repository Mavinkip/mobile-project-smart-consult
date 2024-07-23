package com.example.predict2

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.PopupMenu
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.button.MaterialButton

class Welcome : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_welcome)

        // Setup popup menu for MaterialButton
        val button = findViewById<MaterialButton>(R.id.materialButton)
        button.setOnClickListener { showPopupMenu(button) }
    }

    private fun showPopupMenu(view: View) {
        val popupMenu = PopupMenu(this, view)
        popupMenu.inflate(R.menu.menu_main)
        popupMenu.setOnMenuItemClickListener { item ->
            when (item.itemId) {
                R.id.action_doctor -> {
                    // Navigate to DoctorLoginActivity
                    val intent = Intent(this, DoctorLoginActivity::class.java)
                    startActivity(intent)
                    true
                }
                R.id.action_patient -> {
                    // Navigate to PatientLoginActivity
                    val intent = Intent(this, Login::class.java)
                    startActivity(intent)
                    true
                }
                else -> false
            }
        }
        popupMenu.show()
    }
}
