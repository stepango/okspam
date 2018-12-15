package com.stepango.okspam.input

import android.view.InputDevice
import android.view.InputEvent
import android.view.KeyEvent
import android.view.MotionEvent


class Dpad {

    private var directionPressed = -1 // initialized to -1

    fun getDirectionPressed(event: InputEvent): Int {
        if (!isDpadDevice(event)) {
            return -1
        }

        // If the input event is a MotionEvent, check its hat axis values.
        (event as? MotionEvent)?.apply {

            // Use the hat axis value to find the D-pad direction
            val xaxis: Float = event.getAxisValue(MotionEvent.AXIS_HAT_X)
            val yaxis: Float = event.getAxisValue(MotionEvent.AXIS_HAT_Y)

            directionPressed = when {
                // Check if the AXIS_HAT_X value is -1 or 1, and set the D-pad
                // LEFT and RIGHT direction accordingly.
                xaxis.compareTo(-1.0f) == 0 -> Dpad.LEFT
                xaxis.compareTo(1.0f) == 0 -> Dpad.RIGHT
                // Check if the AXIS_HAT_Y value is -1 or 1, and set the D-pad
                // UP and DOWN direction accordingly.
                yaxis.compareTo(-1.0f) == 0 -> Dpad.UP
                yaxis.compareTo(1.0f) == 0 -> Dpad.DOWN
                else -> directionPressed
            }
        }
        // If the input event is a KeyEvent, check its key code.
        (event as? KeyEvent)?.apply {

            // Use the key code to find the D-pad direction.
            directionPressed = when(event.keyCode) {
                KeyEvent.KEYCODE_DPAD_LEFT -> Dpad.LEFT
                KeyEvent.KEYCODE_DPAD_RIGHT -> Dpad.RIGHT
                KeyEvent.KEYCODE_DPAD_UP -> Dpad.UP
                KeyEvent.KEYCODE_DPAD_DOWN -> Dpad.DOWN
                KeyEvent.KEYCODE_DPAD_CENTER ->  Dpad.CENTER
                else -> directionPressed
            }
        }
        return directionPressed
    }

    companion object {
        internal const val UP = 0
        internal const val LEFT = 1
        internal const val RIGHT = 2
        internal const val DOWN = 3
        internal const val CENTER = 4

        fun isDpadDevice(event: InputEvent): Boolean =
        // Check that input comes from a device with directional pads.
            event.source and InputDevice.SOURCE_DPAD != InputDevice.SOURCE_DPAD
    }
}
