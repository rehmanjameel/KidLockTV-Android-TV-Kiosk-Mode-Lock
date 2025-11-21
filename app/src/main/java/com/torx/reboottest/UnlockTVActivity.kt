package com.torx.reboottest

import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.torx.reboottest.services.TOTPVerifier

class UnlockTVActivity : AppCompatActivity() {

    private lateinit var unlockET: TextInputEditText
    private lateinit var unlockTIL: TextInputLayout
    private lateinit var unlockButton: MaterialButton
    private val secretKey = "JBSWY3DPEHPK3PXP"   // same secret saved earlier

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_unlock_tvactivity)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        unlockET = findViewById(R.id.unlockTIET)
        unlockTIL = findViewById(R.id.unlockTIL)
        unlockButton = findViewById(R.id.unlockButton)
        unlockButton.setOnClickListener {
            val inputCode = unlockET.text.toString()

            if (inputCode.length != 6) {
                unlockTIL.error = "Enter 6‑digit code"
                return@setOnClickListener
            }

            val success = TOTPVerifier.verifyCode(secretKey, inputCode)

            if (success) {
                unlockTIL.error = ""
                unlockTV()
            } else {
                unlockTIL.error = "Invalid code! Try again."
            }
        }
    }

    private fun unlockTV() {
        Toast.makeText(this, "Unlocked!", Toast.LENGTH_SHORT).show()

        // Example: finish your kiosk mode
        finishAffinity()
    }
}