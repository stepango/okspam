package com.stepango.okspam

import android.widget.Toast


fun String.toast(duration: Int = Toast.LENGTH_SHORT) = Toast.makeText(OkSpamApp.instance, this, duration).show()
