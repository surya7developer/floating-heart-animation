package com.apps.mapdemo.heartanimation

import android.graphics.Path
import android.graphics.PathMeasure
import android.os.Handler
import android.os.Looper
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.LinearInterpolator
import android.view.animation.Transformation
import java.util.concurrent.atomic.AtomicInteger

/**
 * Floating Heart Path Animator
 */
class PathAnimator(config: Config?) : AbstractPathAnimator(config!!) {
    private val mCounter = AtomicInteger(0)
    private val mHandler: Handler
    override fun start(child: View?, parent: ViewGroup?) {
        parent!!.addView(child, ViewGroup.LayoutParams(mConfig.heartWidth, mConfig.heartHeight))
        val anim = FloatAnimation(createPath(mCounter, parent, 2), randomRotation(), parent, child)
        anim.duration = mConfig.animDuration.toLong()
        anim.interpolator = LinearInterpolator()
        anim.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationEnd(animation: Animation) {
                mHandler.post { parent.removeView(child) }
                mCounter.decrementAndGet()
            }

            override fun onAnimationRepeat(animation: Animation) {}
            override fun onAnimationStart(animation: Animation) {
                mCounter.incrementAndGet()
            }
        })
        anim.interpolator = LinearInterpolator()
        child!!.startAnimation(anim)
    }

    internal class FloatAnimation(path: Path?, rotation: Float, parent: View?, child: View?) : Animation() {
        private val mPm: PathMeasure
        private val mView: View?
        private val mDistance: Float
        private val mRotation: Float
        override fun applyTransformation(factor: Float, transformation: Transformation) {
            val matrix = transformation.matrix
            mPm.getMatrix(mDistance * factor, matrix, PathMeasure.POSITION_MATRIX_FLAG)
            mView!!.rotation = mRotation * factor
            var scale = 1f
            if (3000.0f * factor < 200.0f) {
                scale = scale(factor.toDouble(), 0.0, 0.06666667014360428, 0.20000000298023224, 1.100000023841858)
            } else if (3000.0f * factor < 300.0f) {
                scale = scale(factor.toDouble(), 0.06666667014360428, 0.10000000149011612, 1.100000023841858, 1.0)
            }
            mView.scaleX = scale
            mView.scaleY = scale
            transformation.alpha = 1.0f - factor
        }

        init {
            mPm = PathMeasure(path, false)
            mDistance = mPm.length
            mView = child
            mRotation = rotation
            parent!!.setLayerType(View.LAYER_TYPE_HARDWARE, null)
        }
    }

    companion object {
        private fun scale(a: Double, b: Double, c: Double, d: Double, e: Double): Float {
            return ((a - b) / (c - b) * (e - d) + d).toFloat()
        }
    }

    init {
        mHandler = Handler(Looper.getMainLooper())
    }
}