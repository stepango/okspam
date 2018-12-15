package com.stepango.okspam

import android.view.KeyEvent


fun Int.isFireKey(): Boolean =
    this == KeyEvent.KEYCODE_DPAD_CENTER || this == KeyEvent.KEYCODE_BUTTON_A
