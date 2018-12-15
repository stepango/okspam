package com.stepango.okspam

import android.content.Intent
import android.util.Log
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class MyFirebaseMessagingService : FirebaseMessagingService() {

    override fun onMessageReceived(remoteMessage: RemoteMessage?) {
        Log.d("Pokemon", "Says")
        val svc = Intent(this, MainService::class.java)
        stopService(svc)
        startService(svc)
    }
}
