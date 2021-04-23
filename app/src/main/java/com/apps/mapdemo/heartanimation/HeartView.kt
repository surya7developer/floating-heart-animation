package com.apps.mapdemo.heartanimation

import android.content.Context
import android.graphics.*
import android.graphics.drawable.BitmapDrawable
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatImageView

/**
 * Piaoxin animated interface
 */
class HeartView : AppCompatImageView {
    private var mHeartResId = R.drawable.live_like_icon
    private var mHeartBorderResId = R.drawable.live_like_icon

    constructor(context: Context?) : super(context!!) {}
    constructor(context: Context?, attrs: AttributeSet?) : super(context!!, attrs) {}
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(context!!, attrs, defStyleAttr) {}

    fun setDrawable(resourceId: Int) {
        val heart = BitmapFactory.decodeResource(resources, resourceId)
        // Sets a drawable as the content of this ImageView.
        setImageDrawable(BitmapDrawable(resources, heart))
    }

    fun setColor(color: Int) {
        val heart = createHeart(color)
        setImageDrawable(BitmapDrawable(resources, heart))
    }

    fun setColorAndDrawables(color: Int, heartResId: Int, heartBorderResId: Int) {
        if (heartResId != mHeartResId) {
            sHeart = null
        }
        if (heartBorderResId != mHeartBorderResId) {
            sHeartBorder = null
        }
        mHeartResId = heartResId
        mHeartBorderResId = heartBorderResId
        setColor(color)
    }

    fun createHeart(color: Int): Bitmap? {
        if (sHeart == null) {
            sHeart = BitmapFactory.decodeResource(resources, mHeartResId)
        }
        if (sHeartBorder == null) {
            sHeartBorder = BitmapFactory.decodeResource(resources, mHeartBorderResId)
        }
        val heart = sHeart
        val heartBorder = sHeartBorder
        val bm = createBitmapSafely(heartBorder!!.width, heartBorder.height)
                ?: return null
        val canvas = sCanvas
        canvas.setBitmap(bm)
        val p = sPaint
        canvas.drawBitmap(heartBorder, 0f, 0f, p)
        p.colorFilter = PorterDuffColorFilter(color, PorterDuff.Mode.SRC_ATOP)
        val dx = (heartBorder.width - heart!!.width) / 2f
        val dy = (heartBorder.height - heart.height) / 2f
        canvas.drawBitmap(heart, dx, dy, p)
        p.colorFilter = null
        canvas.setBitmap(null)
        return bm
    }

    companion object {
        //Anti-aliasing when drawing
        private val sPaint = Paint(Paint.ANTI_ALIAS_FLAG or Paint.FILTER_BITMAP_FLAG)
        private val sCanvas = Canvas()
        private var sHeart: Bitmap? = null
        private var sHeartBorder: Bitmap? = null
        private fun createBitmapSafely(width: Int, height: Int): Bitmap? {
            try {
                return Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
            } catch (error: OutOfMemoryError) {
                error.printStackTrace()
            }
            return null
        }
    }
}