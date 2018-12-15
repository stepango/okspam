package com.stepango.okspam

import android.app.Activity
import android.os.Bundle
import android.view.InputDevice
import android.view.KeyEvent
import android.view.MotionEvent
import com.stepango.okspam.input.Dpad
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import kotlinx.android.synthetic.main.activity_main.*
import java.util.concurrent.TimeUnit


class ConfigActivity : Activity() {

    private val vm : MainViewModel = MainViewModelImpl()
    private val mDpad = Dpad()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setupViews()
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent): Boolean {
        var handled = false
        if (event.source and InputDevice.SOURCE_GAMEPAD == InputDevice.SOURCE_GAMEPAD) {
            if (event.repeatCount == 0) {
                when (keyCode) {
                    KeyEvent.KEYCODE_BUTTON_X -> {
                        "Button X (Square)".toast()
                        handled = true
                    }
                    KeyEvent.KEYCODE_BUTTON_A -> {
                        "Button A (Cross)".toast()
                        handled = true
                    }
                    KeyEvent.KEYCODE_BUTTON_Y -> {
                        "Button Y (Triangle)".toast()
                        handled = true
                    }
                    KeyEvent.KEYCODE_BUTTON_B -> {
                        "Button B (Circle)".toast()
                        handled = true
                    }
                }
            }
            if (handled) {
                return true
            }
        }

        return super.onKeyDown(keyCode, event)
    }

    override fun onGenericMotionEvent(event: MotionEvent): Boolean {
        if (Dpad.isDpadDevice(event)) {
            when (mDpad.getDirectionPressed(event)) {
                Dpad.LEFT -> {
                    // Do something for LEFT direction press
                    "D-Pad Left".toast()
                    return true
                }
                Dpad.RIGHT -> {
                    // Do something for RIGHT direction press
                    "D-Pad Right".toast()
                    return true
                }
                Dpad.UP -> {
                    // Do something for UP direction press
                    "D-Pad Up".toast()
                    return true
                }
                Dpad.DOWN -> {
                    "D-Pad Down".toast()
                    return true
                }
                Dpad.CENTER -> {
                    "D-Pad Center".toast()
                    return true
                }
            }
        }

        // Check if this event is from a joystick movement and process accordingly.
        //...

        return super.onGenericMotionEvent(event)
    }

    private fun setupViews() {
        // Keep listening to status of controllers
        Observable.interval(0, 5, TimeUnit.SECONDS)
            .observeOn(AndroidSchedulers.mainThread())
            .doOnNext { checkCurrentInputs() }
            .subscribe()
    }

    private fun checkCurrentInputs() {
        vm.controllerIds()
            .map {
                if (it.isNotEmpty()) {
                    "Controller connected!" to R.color.green
                } else {
                    "Controller disconnected!" to R.color.red
                }
            }
            .doOnSuccess {
                controllerStatus.text = it.first
                controllerStatus.setTextColor(resources.getColor(it.second))
            }
            .subscribe()
    }

}
