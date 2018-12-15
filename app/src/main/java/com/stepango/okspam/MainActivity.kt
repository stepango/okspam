package com.stepango.okspam

import android.app.Activity
import android.os.Bundle
import android.view.InputDevice
import android.view.KeyEvent
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import kotlinx.android.synthetic.main.activity_main.*
import java.util.concurrent.TimeUnit


class MainActivity : Activity() {

    private val vm : MainViewModel = MainViewModelImpl()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setupViews()
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent): Boolean {
        var handled = false
        if (event.source and InputDevice.SOURCE_GAMEPAD == InputDevice.SOURCE_GAMEPAD) {
            if (event.repeatCount == 0) {
                keyCode.takeIf { it.isFireKey() }?.run {

                    handled = true
                }
            }
            if (handled) {
                return true
            }
        }
        return super.onKeyDown(keyCode, event)
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
