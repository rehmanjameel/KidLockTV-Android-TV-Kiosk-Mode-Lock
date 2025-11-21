package com.torx.reboottest

import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.widget.ImageView
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.zxing.BarcodeFormat
import com.google.zxing.WriterException
import com.journeyapps.barcodescanner.BarcodeEncoder
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import androidx.core.net.toUri
import com.google.android.material.button.MaterialButton

class MainActivity : AppCompatActivity() {

    private val requestOverlayPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            if (Settings.canDrawOverlays(this)) {
                // Permission granted, proceed with drawing overlay
            } else {
                // Permission denied
            }
        }
    }

    fun checkOverlayPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            if (!Settings.canDrawOverlays(this)) {
                val intent = Intent(
                    Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                    "package:$packageName".toUri()
                )
                requestOverlayPermissionLauncher.launch(intent)
            } else {
                // Permission already granted, proceed
            }
        } else {
            // For older Android versions, permission is granted by default if declared
        }
    }

    private lateinit var btnDone: MaterialButton
    private lateinit var btnNext: MaterialButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        val imageView = findViewById<ImageView>(R.id.qrImageView)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        checkOverlayPermission()
//        imageView.scaleType = ImageView.ScaleType.FIT_CENTER
//        setContentView(imageView)

        btnDone = findViewById(R.id.btnScanned)
        btnNext = findViewById(R.id.btnContinue)
        btnNext.isEnabled = false

        // Example secret key — in real app, get from user input or stored value
        val secretKey = "JBSWY3DPEHPK3PXP"  // Base32 key

        // Generate QR code for Google Authenticator
        val otpAuthUrl = "otpauth://totp/TVLockerApp?secret=$secretKey&issuer=TVLocker"

        // Launch QR code generation in background
        CoroutineScope(Dispatchers.Default).launch {
            try {
                val barcodeEncoder = BarcodeEncoder()
                val bitmap = barcodeEncoder.encodeBitmap(
                    otpAuthUrl,
                    BarcodeFormat.QR_CODE,
                    800, 800
                )
                withContext(Dispatchers.Main) {
                    imageView.setImageBitmap(bitmap)
                }
            } catch (e: WriterException) {
                e.printStackTrace()
            }
        }

        btnDone.setOnClickListener {
            btnNext.isEnabled = true
        }

        btnNext.setOnClickListener {
            val intent = Intent(this, UnlockTVActivity::class.java)
            startActivity(intent)
        }
    }
}