package com.stepango.okspam

import android.app.Service
import android.content.Context
import android.content.Intent
import android.graphics.PixelFormat
import android.os.IBinder
import android.util.Log
import android.view.*
import android.widget.FrameLayout
import com.bumptech.glide.Glide


class MainService : Service(), View.OnTouchListener {

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
            0,
            PixelFormat.TRANSLUCENT
        )

        params.gravity = Gravity.CENTER or Gravity.START
        params.x = 0
        params.y = 0

        val interceptorLayout = object : FrameLayout(this) {

            override fun dispatchKeyEvent(event: KeyEvent): Boolean {

                // Only fire on the ACTION_DOWN event, or you'll get two events (one for _DOWN, one for _UP)
                if (event.action === KeyEvent.ACTION_DOWN) {

                    // Check if the HOME button is pressed
                    if (event.keyCode === KeyEvent.KEYCODE_BACK) {

                        Log.v(TAG, "BACK Button Pressed")

                        // As we've taken action, we'll return true to prevent other apps from consuming the event as well
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
            it.setOnTouchListener(this)
            Glide.with(this).load(R.raw.gif_1).asGif().into(it.findViewById(R.id.canvas))

            windowManager!!.addView(floatyView, params)
        }
    }

    override fun onDestroy() {

        super.onDestroy()

        if (floatyView != null) {

            windowManager!!.removeView(floatyView)

            floatyView
        }
    }

    override fun onTouch(view: View, motionEvent: MotionEvent): Boolean {

        Log.v(TAG, "onTouch...")

        // Kill service
        onDestroy()

        return true
    }

    companion object {

        private val TAG = MainService::class.java.simpleName
    }
}