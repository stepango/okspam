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
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import java.util.concurrent.TimeUnit
import kotlin.random.Random


class MainService : Service() {

    private val demoMessages = listOf(
        "Hello!",
        "Did you notice?",
        "Your phone is locked",
        "Take it easy...",
        "You need to chill",
        "Go out and play",
        "Good day!"
    )
    private var messageCounter = 0

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

        //if (request == null) onDestroy()

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

            val textView = it.findViewById<TextView>(R.id.message)
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
            windowManager!!.addView(floatyView, params)
        }
        floatyView?.requestFocus()

        Observable.interval(0, 10, TimeUnit.SECONDS)
            .map { it.toInt() }
            .observeOn(AndroidSchedulers.mainThread())
            .doOnNext {
                if (it == demoMessages.size) {
                    onDestroy()
                } else {
                    displayNextMessage(it)
                }
            }
            .subscribe()

    }

    override fun onDestroy() {
        super.onDestroy()

        cleanup()

        if (floatyView != null) {
            windowManager!!.removeView(floatyView)
            floatyView = null
        }
    }

    private fun displayNextMessage(index: Int) {
        floatyView?.let {
            val imageView = it.findViewById<ImageView>(R.id.canvas)
            Glide.with(this).load(getRandomImage()).asGif().into(imageView)
            imageView.animate().translationX(
                when (Random.nextInt() % 2) {
                    0 -> -1
                    else -> 1
                } * Resources.getSystem().displayMetrics.widthPixels.toFloat()
            ).setDuration(0).start()
            imageView.animate().translationX(0f).setDuration(1000).start()

            val textView = it.findViewById<TextView>(R.id.message)
            textView.text = demoMessages[index]

            val disappearSoon = Observable.timer(5, TimeUnit.SECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .doOnNext {
                    clearViews()
                }
                .subscribe()
            disposables.add(disappearSoon)
        }
    }

    private fun getRandomImage(): Int {
        val fields = R.raw::class.java.fields
        return fields[getRandomInt(0, fields.size - 1)].get(null) as Int
    }

    private fun getRandomInt(min: Int = 0, max: Int = 100) = Random.nextInt(max - min + 1) + min

    private fun clearViews() {

        floatyView?.let {
            val imageView = it.findViewById<ImageView>(R.id.canvas)
            imageView.setImageDrawable(null)
        }
    }

    private fun cleanup() {
        disposables.forEach {
            if (!it.isDisposed) it.dispose()
        }
        disposables.clear()
    }

}
