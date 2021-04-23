package com.apps.mapdemo.heartanimation

import android.content.Context
import android.graphics.BitmapFactory
import android.os.Handler
import android.os.Message
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.RelativeLayout
import com.apps.mapdemo.heartanimation.AbstractPathAnimator.Config.Companion.fromTypeArray
import com.apps.mapdemo.heartanimation.MainActivity.Companion.MSG_SHOW
import java.lang.ref.WeakReference
import java.util.*

class HeartLayout : RelativeLayout, View.OnClickListener {



    private var mAnimator: AbstractPathAnimator? = null
    private var attrs: AttributeSet? = null
    private var defStyleAttr = 0
    private var onHearLayoutListener: OnHearLayoutListener? = null
    fun setOnHearLayoutListener(onHearLayoutListener: OnHearLayoutListener?) {
        this.onHearLayoutListener = onHearLayoutListener
    }

    interface OnHearLayoutListener {
        fun onAddFavor(): Boolean
    }

    constructor(context: Context) : super(context) {
        findViewById(context)
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        this.attrs = attrs
        findViewById(context)
    }

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        this.attrs = attrs
        this.defStyleAttr = defStyleAttr
        findViewById(context)
    }

    private fun findViewById(context: Context) {
        LayoutInflater.from(context).inflate(R.layout.ly_periscope, this)
        var bitmap = BitmapFactory.decodeResource(resources, R.drawable.live_like_icon)
        dHeight = bitmap.getWidth() / 2
        dWidth = bitmap.getHeight() / 2
        textHight = sp2px(getContext(), 20f) + dHeight / 2
        pointx = dWidth //The x coordinate of the random floating direction
        bitmap.recycle()
    }

    private var mHeight = 0
    private var mWidth = 0
    private var textHight = 0
    private var dHeight = 0
    private var dWidth = 0
    private var initX = 0
    private var pointx = 0

    inner class HeartHandler(layout: HeartLayout) : Handler() {
        var wf: WeakReference<HeartLayout>
        override fun handleMessage(msg: Message) {
            super.handleMessage(msg)
            val layout = wf.get() ?: return
            when (msg.what) {
                MSG_SHOW -> addFavor()
            }
        }



        init {
            wf = WeakReference(layout)
        }
    }

    inner class HeartThread : Runnable {
        private var time: Long = 0
        private var allSize = 0
        fun addTask(time: Long, size: Int) {
            this.time = time
            allSize += size
        }

        fun clean() {
            allSize = 0
        }

        override fun run() {
            if (heartHandler == null) return
            if (allSize > 0) {
                heartHandler!!.sendEmptyMessage(MSG_SHOW)
                allSize--
            }
            postDelayed(this, time)
        }
    }

    private fun init(attrs: AttributeSet?, defStyleAttr: Int) {
        val a = context.obtainStyledAttributes(
                attrs, R.styleable.HeartLayout, defStyleAttr, 0)
        if (pointx <= initX && pointx >= 0) {
            pointx -= 10
        } else if (pointx >= -initX && pointx <= 0) {
            pointx += 10
        } else pointx = initX
        mAnimator = PathAnimator(fromTypeArray(a, initX.toFloat(), textHight.toFloat(), pointx, dWidth, dHeight))
        a.recycle()
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        //Get the width and height of itself
        mWidth = measuredWidth
        mHeight = measuredHeight
        initX = mWidth / 2 - dWidth / 2
    }

    var animator: AbstractPathAnimator?
        get() = mAnimator
        set(animator) {
            clearAnimation()
            mAnimator = animator
        }

    override fun clearAnimation() {
        for (i in 0 until childCount) {
            getChildAt(i).clearAnimation()
        }
        removeAllViews()
    }

    private val random = Random()
    fun addFavor() {
        val heartView = HeartView(context)
        heartView.setDrawable(drawableIds[random.nextInt(8)])
        init(attrs, defStyleAttr)
        mAnimator!!.start(heartView, this)
    }

    private var nowTime: Long = 0
    private var lastTime: Long = 0
    fun addFavor(size: Int) {
        var size = size
        size = when (sizeOfInt(size)) {
            1 -> size % 10
            else -> size % 100
        }
        if (size == 0) return
        nowTime = System.currentTimeMillis()
        var time = nowTime - lastTime
        if (lastTime == 0L) time = (2 * 1000).toLong() //The first time is divided into 2 seconds to display
        time = time / (size + 15)
        if (heartThread == null) {
            heartThread = HeartThread()
        }
        if (heartHandler == null) {
            heartHandler = HeartHandler(this)
            heartHandler!!.post(heartThread!!)
        }
        heartThread!!.addTask(time, size)
        lastTime = nowTime
    }

    fun addHeart(color: Int) {
        val heartView = HeartView(context)
        heartView.setColor(color)
        init(attrs, defStyleAttr)
        mAnimator!!.start(heartView, this)
    }

    fun addHeart(color: Int, heartResId: Int, heartBorderResId: Int) {
        val heartView = HeartView(context)
        heartView.setColorAndDrawables(color, heartResId, heartBorderResId)
        init(attrs, defStyleAttr)
        mAnimator!!.start(heartView, this)
    }

    override fun onClick(v: View) {
        val i = v.id
        if (i == R.id.img) {
            if (onHearLayoutListener != null) {
                val isAdd = onHearLayoutListener!!.onAddFavor()
                if (isAdd) addFavor()
            }
        }
    }

    fun clean() {
        if (heartThread != null) {
            heartThread!!.clean()
        }
    }

    fun release() {
        if (heartHandler != null) {
            heartHandler!!.removeCallbacks(heartThread!!)
            heartThread = null
            heartHandler = null
        }
    }

    companion object {
        private var heartHandler: HeartHandler? = null
        private var heartThread: HeartThread? = null
        fun sp2px(context: Context, spValue: Float): Int {
            val fontScale = context.resources.displayMetrics.scaledDensity
            return (spValue * fontScale + 0.5f).toInt()
        }

        private val drawableIds = intArrayOf(R.drawable.heart_icon, R.drawable.a, R.drawable.b, R.drawable.c, R.drawable.d, R.drawable.e, R.drawable.f, R.drawable.heart_icon)
        val sizeTable = intArrayOf(9, 99, 999, 9999, 99999, 999999, 9999999,
                99999999, 999999999, Int.MAX_VALUE)

        fun sizeOfInt(x: Int): Int {
            var i = 0
            while (true) {
                if (x <= sizeTable[i]) return i + 1
                i++
            }
        }
    }
}