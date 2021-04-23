package com.apps.mapdemo.heartanimation

import android.view.ViewGroup
import android.content.res.TypedArray
import android.graphics.Path
import android.view.View
import java.util.*
import java.util.concurrent.atomic.AtomicInteger

abstract class AbstractPathAnimator(protected var mConfig: Config) {
    private val mRandom: Random
    fun randomRotation(): Float {
        return mRandom.nextFloat() * 28.6f - 14.3f
    }

    fun createPath(counter: AtomicInteger, view: View, factor: Int): Path {
        var factor = factor
        val r = mRandom
        var x = r.nextInt(mConfig.xRand)
        var x2 = r.nextInt(mConfig.xRand)
        val y = view.height - mConfig.initY
        var y2 = counter.toInt() * 15 + mConfig.animLength * factor + r.nextInt(mConfig.animLengthRand)
        factor = y2 / mConfig.bezierFactor
        x = mConfig.xPointFactor + x
        x2 = mConfig.xPointFactor + x2
        val y3 = y - y2
        y2 = y - y2 / 2
        val p = Path()
        p.moveTo(mConfig.initX.toFloat(), y.toFloat())
        p.cubicTo(mConfig.initX.toFloat(), (y - factor).toFloat(), x.toFloat(), (y2 + factor).toFloat(), x.toFloat(), y2.toFloat())
        p.moveTo(x.toFloat(), y2.toFloat())
        p.cubicTo(x.toFloat(), (y2 - factor).toFloat(), x2.toFloat(), (y3 + factor).toFloat(), x2.toFloat(), y3.toFloat())
        return p
    }

    abstract fun start(child: View?, parent: ViewGroup?)
    class Config {
        var initX = 0
        var initY = 0
        var xRand = 0
        var animLengthRand = 0
        var bezierFactor = 0
        var xPointFactor = 0
        var animLength = 0
        @JvmField
        var heartWidth = 0
        @JvmField
        var heartHeight = 0
        @JvmField
        var animDuration = 0

        companion object {
            @JvmStatic
            fun fromTypeArray(typedArray: TypedArray, x: Float, y: Float, pointx: Int, heartWidth: Int, heartHeight: Int): Config {
                val config = Config()
                val res = typedArray.resources
                config.initX = typedArray.getDimension(R.styleable.HeartLayout_initX,
                        x).toInt()
                config.initY = typedArray.getDimension(R.styleable.HeartLayout_initY,
                        y).toInt()
                config.xRand = typedArray.getDimension(R.styleable.HeartLayout_xRand,
                        res.getDimensionPixelOffset(R.dimen.heart_anim_bezier_x_rand).toFloat()).toInt()
                config.animLength = typedArray.getDimension(R.styleable.HeartLayout_animLength,
                        res.getDimensionPixelOffset(R.dimen.heart_anim_length).toFloat()).toInt() //Animation length
                config.animLengthRand = typedArray.getDimension(R.styleable.HeartLayout_animLengthRand,
                        res.getDimensionPixelOffset(R.dimen.heart_anim_length_rand).toFloat()).toInt()
                config.bezierFactor = typedArray.getInteger(R.styleable.HeartLayout_bezierFactor,
                        res.getInteger(R.integer.heart_anim_bezier_factor))
                config.xPointFactor = pointx
                config.heartWidth = heartWidth
                config.heartHeight = heartHeight
                config.animDuration = typedArray.getInteger(R.styleable.HeartLayout_anim_duration,
                        res.getInteger(R.integer.anim_duration)) //Duration
                return config
            }
        }
    }

    init {
        mRandom = Random()
    }
}