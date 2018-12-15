package com.stepango.okspam

import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.res.Resources
import android.graphics.PixelFormat
import android.util.Log
import android.view.*
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import io.reactivex.Observable
import io.reactivex.disposables.Disposable
import io.reactivex.rxkotlin.Observables
import java.util.concurrent.TimeUnit
import kotlin.random.Random


class MainService : Service() {

    private var request: SpamRequest? = null

    private var windowManager: WindowManager? = null
    private var floatyView: View? = null
    private var disposables = mutableListOf<Disposable>()

    override fun onBind(intent: Intent) = null

    override fun onCreate() {
        super.onCreate()

        windowManager = getSystemService(Context.WINDOW_SERVICE) as WindowManager
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {

        Log.e("Service", "onStartComm")
        request = intent?.extras?.getParcelable(KEY_SPAM)

        if (request == null) onDestroy()

        addOverlayView()

        return super.onStartCommand(intent, flags, startId)
    }

    private fun addOverlayView() {
        cleanup()

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
                // Block all key events
                return true
            }

        }

        floatyView = (getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater).inflate(
            R.layout.floating_view,
            interceptorLayout
        )

        floatyView?.let {
            val imageView = it.findViewById<ImageView>(R.id.canvas)
            Glide.with(this).load(request!!.imageUrl).asGif().into(imageView)
            imageView.animate().translationX(
                when (Random.nextInt() % 2) {
                    0 -> -1
                    else -> 1
                } * Resources.getSystem().displayMetrics.widthPixels.toFloat()
            ).setDuration(0).start()
            imageView.animate().translationX(0f).setDuration(3000).start()

            val textView = it.findViewById<TextView>(R.id.message)
            request?.message?.let { msg ->
                textView.text = msg

                val textColorChanger = Observable.interval(500, TimeUnit.MILLISECONDS)
                    .doOnNext { counter ->
                        if (counter % 2 == 0L) {
                            textView.setTextColor(resources.getColor(R.color.white))
                        } else {
                            textView.setTextColor(resources.getColor(R.color.black))
                        }
                    }
                    .subscribe()

                disposables.add(textColorChanger)
            }

            val disappearSoon = Observable.timer(10, TimeUnit.SECONDS)
                .doOnNext {
                    onDestroy()
                }
                .subscribe()
            disposables.add(disappearSoon)

            windowManager!!.addView(floatyView, params)
        }
        floatyView?.requestFocus()
    }

    override fun onDestroy() {
        super.onDestroy()

        cleanup()

        if (floatyView != null) {
            windowManager!!.removeView(floatyView)
            floatyView = null
        }
    }

    private fun cleanup() {
        disposables.forEach {
            if (!it.isDisposed) it.dispose()
        }
        disposables.clear()
    }

}
