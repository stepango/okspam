package com.stepango.okspam

import android.app.Application
import android.app.NotificationChannelGroup
import android.app.NotificationManager
import android.content.Context


class OkSpamApp : Application() {

    override fun onCreate() {
        super.onCreate()
        instance = this

        // The id of the group.
        val groupId = "my_group_01"
// The user-visible name of the group.
        val groupName = getString(R.string.default_notification_channel_id)
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannelGroup(NotificationChannelGroup(groupId, groupName))
    }

    companion object {
        lateinit var instance: OkSpamApp
            private set
    }

}
