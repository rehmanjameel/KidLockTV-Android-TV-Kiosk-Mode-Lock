package com.torx.reboottest

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log

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
