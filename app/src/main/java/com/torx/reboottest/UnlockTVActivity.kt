package com.torx.reboottest

import android.Manifest
import android.app.KeyguardManager
import android.os.Build
import android.os.Bundle
import android.view.KeyEvent
import android.view.WindowManager
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.annotation.RequiresPermission
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
    private val secretKey = "JBSWY3DPEHPK3PXP"

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_unlock_tvactivity)

        unlockET = findViewById(R.id.unlockTIET)
        unlockTIL = findViewById(R.id.unlockTIL)
        unlockButton = findViewById(R.id.unlockButton)

        unlockButton.setOnClickListener {
            val inputCode = unlockET.text.toString()

            if (inputCode.length != 6) {
                unlockTIL.error = "Enter 6-digit code"
                return@setOnClickListener
            }

            val success = TOTPVerifier.verifyCode(secretKey, inputCode)

            if (success) {
                unlockTIL.error = null
                unlockTV()
            } else {
                unlockTIL.error = "Invalid code!"
            }
        }

        // LOCK DEVICE INTO YOUR APP
        try {
            startLockTask()   // home/back locked
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun unlockTV() {
        try {
            stopLockTask()  // unlock device
        } catch (e: Exception) {}

        Toast.makeText(this, "Unlocked!", Toast.LENGTH_SHORT).show()
        finishAffinity()
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        return true // consume all keys
    }
}
