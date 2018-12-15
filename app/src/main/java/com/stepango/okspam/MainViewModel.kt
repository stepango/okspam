package com.stepango.okspam

import android.view.InputDevice
import com.stepango.okspam.arch.ViewModel
import io.reactivex.Single


interface MainViewModel : ViewModel {

    fun controllerIds(): Single<List<Int>>
}


class MainViewModelImpl : MainViewModel {

    init {
        setup()
    }

    override fun controllerIds(): Single<List<Int>> = Single.create<List<Int>> { emitter ->
        emitter.onSuccess(getGameControllerIds())
    }

    private fun setup() {

    }

    private fun getGameControllerIds(): List<Int> {
        val gameControllerDeviceIds = mutableListOf<Int>()
        val deviceIds = InputDevice.getDeviceIds()
        deviceIds.forEach { deviceId ->
            InputDevice.getDevice(deviceId).apply {

                // Verify that the device has gamepad buttons, control sticks, or both.
                if (sources and InputDevice.SOURCE_GAMEPAD == InputDevice.SOURCE_GAMEPAD
                    || sources and InputDevice.SOURCE_JOYSTICK == InputDevice.SOURCE_JOYSTICK) {
                    // This device is a game controller. Store its device ID.
                    gameControllerDeviceIds
                        .takeIf { !it.contains(deviceId) }
                        ?.add(deviceId)
                }
            }
        }
        return gameControllerDeviceIds
    }

}
