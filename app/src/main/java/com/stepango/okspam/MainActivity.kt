package com.stepango.okspam

import android.app.Activity
import android.os.Bundle
import android.view.InputDevice
import android.view.KeyEvent
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import kotlinx.android.synthetic.main.activity_main.*
import java.util.concurrent.TimeUnit

import android.widget.Toast
import android.content.Intent
import android.net.Uri
import android.provider.Settings

class MainActivity : Activity() {

    private val vm : MainViewModel = MainViewModelImpl()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setupViews()

        if (Settings.canDrawOverlays(this)) {

            // Launch service right away - the user has already previously granted permission
            launchMainService()
        } else {

            // Check that the user has granted permission, and prompt them if not
            checkDrawOverlayPermission()
        }
    }

    private fun launchMainService() {

        val svc = Intent(this, MainService::class.java)

        startService(svc)

        finish()
    }

    fun checkDrawOverlayPermission() {

        // Checks if app already has permission to draw overlays
        if (!Settings.canDrawOverlays(this)) {

            // If not, form up an Intent to launch the permission request
            val intent = Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:" + getPackageName()))

            // Launch Intent, with the supplied request code
            startActivityForResult(intent, REQUEST_CODE)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent) {

        // Check if a request code is received that matches that which we provided for the overlay draw request
        if (requestCode == REQUEST_CODE) {

            // Double-check that the user granted it, and didn't just dismiss the request
            if (Settings.canDrawOverlays(this)) {

                // Launch the service
                launchMainService()
            } else {

                Toast.makeText(this, "Sorry. Can't draw overlays without permission...", Toast.LENGTH_SHORT).show()
            }
        }
    }

    companion object {

        val REQUEST_CODE = 10101
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
