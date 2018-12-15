package com.stepango.okspam

import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.res.Resources
import android.graphics.PixelFormat
import android.os.IBinder
import android.util.Log
import android.view.*
import android.widget.FrameLayout
import android.widget.ImageView
import com.bumptech.glide.Glide
import kotlin.random.Random

class MainService : Service() {

    private var windowManager: WindowManager? = null

    private var floatyView: View? = null

    override fun onBind(intent: Intent): IBinder? {

        return null
    }

    override fun onCreate() {

        super.onCreate()

        windowManager = getSystemService(Context.WINDOW_SERVICE) as WindowManager

        addOverlayView()
    }

    private fun addOverlayView() {

        val params = WindowManager.LayoutParams(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
            WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
            PixelFormat.TRANSLUCENT
        )

        params.gravity = Gravity.CENTER or Gravity.START
        params.x = 0
        params.y = 0

        val interceptorLayout = object : FrameLayout(this) {

            override fun dispatchKeyEvent(event: KeyEvent): Boolean {
                var handled = false
                val keyCode = event.keyCode
                Log.e("Overlay", "Key: $keyCode")
                if (event.source and InputDevice.SOURCE_GAMEPAD == InputDevice.SOURCE_GAMEPAD) {
                    if (event.repeatCount == 0) {
                        when (keyCode) {
                            KeyEvent.KEYCODE_BUTTON_X -> {
                                //"Button X (Square)".toast()
                                handled = true
                            }
                            KeyEvent.KEYCODE_BUTTON_A -> {
                                //"Button A (Cross)".toast()
                                changeImage()
                                handled = true
                            }
                            KeyEvent.KEYCODE_BUTTON_Y -> {
                                //"Button Y (Triangle)".toast()
                                handled = true
                            }
                            KeyEvent.KEYCODE_BUTTON_B -> {
                                //"Button B (Circle)".toast()
                                handled = true
                            }
                        }
                    }
                    if (handled) {
                        return true
                    }
                }

                // Otherwise don't intercept the event
                return super.dispatchKeyEvent(event)
            }

        }

        floatyView = (getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater).inflate(
            R.layout.floating_view,
            interceptorLayout
        )

        floatyView?.let {
            val imageView = it.findViewById<ImageView>(R.id.canvas)
            Glide.with(this).load(getRandomImage()).asGif().into(imageView)
            imageView.animate().translationX(
                when (Random.nextInt() % 2) {
                    0 -> -1
                    else -> 1
                } * Resources.getSystem().displayMetrics.widthPixels.toFloat()
            ).setDuration(0).start()
            imageView.animate().translationX(0f).setDuration(5000).start()
            windowManager!!.addView(floatyView, params)
        }
        floatyView?.requestFocus()
    }

    private fun changeImage() {
        floatyView?.let {
            val imageView = it.findViewById<ImageView>(R.id.canvas)
            Glide.with(this).load(getRandomImage()).asGif().into(imageView)
        }
    }

    private fun getRandomImage(): Int {
        return when (getRandomInt(1, 3)) {
            1 -> R.raw.gif_1
            2 -> R.raw.gif_2
            else -> R.raw.gif_3
        }
    }

    private fun getRandomInt(min: Int = 0, max: Int = 100) = Random.nextInt(max - min + 1) + min

    override fun onDestroy() {

        super.onDestroy()

        if (floatyView != null) {

            windowManager!!.removeView(floatyView)

            floatyView
        }
    }

}