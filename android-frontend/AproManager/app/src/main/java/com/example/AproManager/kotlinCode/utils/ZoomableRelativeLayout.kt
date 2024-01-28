package com.example.AproManager.kotlinCode.utils

// ZoomableRelativeLayout.kt
import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.ScaleGestureDetector
import android.widget.RelativeLayout

class ZoomableRelativeLayout(context: Context, attrs: AttributeSet) : RelativeLayout(context, attrs) {

    private var mScaleFactor = 1f
    private lateinit var mScaleGestureDetector: ScaleGestureDetector

    init {
        init(context)
    }

    private fun init(context: Context) {
        mScaleGestureDetector = ScaleGestureDetector(context, ScaleListener())
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        mScaleGestureDetector.onTouchEvent(event)
        return true
    }

    private inner class ScaleListener : ScaleGestureDetector.SimpleOnScaleGestureListener() {
        override fun onScale(detector: ScaleGestureDetector): Boolean {
            mScaleFactor *= detector.scaleFactor
            mScaleFactor = Math.max(0.1f, Math.min(mScaleFactor, 10.0f)) // Prevent scaling too small or too large

            scaleX = mScaleFactor
            scaleY = mScaleFactor
            return true
        }
    }
}
