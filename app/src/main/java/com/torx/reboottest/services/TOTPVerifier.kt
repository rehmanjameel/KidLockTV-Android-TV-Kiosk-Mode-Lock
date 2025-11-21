package com.torx.reboottest.services

import android.os.Build
import androidx.annotation.RequiresApi
import com.eatthepath.otp.TimeBasedOneTimePasswordGenerator
import java.time.Instant
import javax.crypto.spec.SecretKeySpec
import java.util.Base64

object TOTPVerifier {

    @RequiresApi(Build.VERSION_CODES.O)
    fun verifyCode(secretKey: String, userCode: String): Boolean {
        return try {
            // Decode base32 secret
            val decodedKey = Base64.getDecoder().decode(secretKey)
            val keySpec = SecretKeySpec(decodedKey, "HmacSHA1")

            val totp = TimeBasedOneTimePasswordGenerator() // default 30 sec interval

            val currentOtp = totp.generateOneTimePassword(keySpec, Instant.now())

            // Compare codes (zero padded)
            val formatted = String.format("%06d", currentOtp)

            formatted == userCode
        } catch (e: Exception) {
            false
        }
    }
}
