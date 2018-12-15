package com.stepango.okspam

import android.app.Application


class OkSpamApp : Application() {

    override fun onCreate() {
        super.onCreate()
        instance = this
    }

    companion object {
        lateinit var instance: OkSpamApp
            private set
    }

}
