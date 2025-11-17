# KidLockTV – Android TV Kiosk Mode Lock

KidLockTV is a secure Android TV kiosk-mode app that fully restricts access to the TV until it is unlocked using a Google Authenticator (TOTP) one-time code. The app automatically launches on boot and prevents kids or unauthorized users from accessing any screen or system UI.

Perfect for parents, schools, hotels, and public environments where complete control over TV access is required.

---

## 🚀 Features

- **Auto-start on Boot** – Launches automatically when the TV powers on.  
- **Kiosk Mode Lock** – Blocks home screen, apps, settings, and navigation buttons.  
- **Google Authenticator Unlock (TOTP)** – Uses a time-based one-time password for secure unlocking.  
- **Exit Protection** – Only authorized users can exit the lock screen.  
- **Remote-Friendly UI** – Designed for Android TV remote navigation.  
- **Offline & Secure** – No server required; TOTP is validated locally.  
- **Prevents App Switching** – Stops attempts to escape via settings or input devices.

---

## 🔐 How It Works

1. When the TV boots, KidLockTV launches instantly as the kiosk launcher.  
2. The TV remains locked, blocking all access to apps and system menus.  
3. The user must open Google Authenticator on their phone and enter the correct TOTP.  
4. After entering the valid code, the TV unlocks and returns to normal use.  
5. On the next reboot, the lock activates again.

---

## 📦 Tech Stack

- **Android (Kotlin/Java)**
- **Google Authenticator compatible TOTP**
- **Boot Completed Receiver**
- **Launcher Override**
- **Foreground Lock Screen Activity**

---

## ⚙️ Installation (Developer Setup)

1. Clone the repository:

   ```bash
   git clone https://github.com/yourusername/KidLockTV.git


## xml permission
```xml
    <uses-permission android:name="android.permission.RECEIVE_BOOT_COMPLETED" />
    <uses-permission android:name="android.permission.SYSTEM_ALERT_WINDOW"/>

    <uses-feature android:name="android.software.leanback" android:required="true" />
    <uses-feature android:name="android.hardware.touchscreen" android:required="false" />

    <application
        android:allowBackup="true"
       ...

        <receiver android:name=".BootReceiver"
            android:enabled="true"
            android:exported="true">
            <intent-filter>
                <action android:name="android.intent.action.BOOT_COMPLETED" />
                <action android:name="android.intent.action.LOCKED_BOOT_COMPLETED"/>
                <action android:name="android.intent.action.QUICKBOOT_POWERON" />
                <action android:name="com.torx.reboottest.ACTION_TEST_BOOT"/>

            </intent-filter>
        </receiver>

    </application>
```

## overlay other app permission
```kotlin
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
```

## boot receiver
```kotlin
class BootReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        val action = intent?.action
        Log.d(TAG, "BootReceiver onReceive action=$action")

        if (action == Intent.ACTION_BOOT_COMPLETED ||
            action == Intent.ACTION_LOCKED_BOOT_COMPLETED ||
            action == DEBUG_ACTION) {

            context?.let { ctx ->
                launchAppActivity(ctx)
            }
        }
    }

    private fun launchAppActivity(ctx: Context) {
        val pm = ctx.packageManager
        val launchIntent = pm.getLaunchIntentForPackage(ctx.packageName)

        if (launchIntent != null) {
            launchIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            try {
                ctx.startActivity(launchIntent)
                Log.d(TAG, "Launching MainActivity")

            } catch (e: Exception) {
                Log.e(TAG, "Failed to start MainActivity: ${e.message}", e)
            }
        }
    }

    companion object {
        private const val TAG = "BootReceiver"
        const val DEBUG_ACTION = "com.torx.reboottest.ACTION_TEST_BOOT"
    }
}
```


