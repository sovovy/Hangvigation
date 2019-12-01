package com.mobal.hangvigation.ui.indoor_navi

import android.content.Context
import android.graphics.*
import android.util.Log
import android.view.MotionEvent
import android.view.ScaleGestureDetector
import android.view.SurfaceHolder
import android.view.SurfaceView
import android.widget.ScrollView
import com.mobal.hangvigation.network.PostCoordResponse
import retrofit2.Response

class InnerMapView(ctx: Context, private val img: Bitmap, private val sv_vertical: ScrollView) : SurfaceView(ctx), SurfaceHolder.Callback, Runnable {
    private val mHolder: SurfaceHolder = holder
    private var thread: Thread? = null
    var x: Int
    var y: Int
    private val MIN_ZOOM: Float = 1f
    private val MAX_ZOOM: Float = 5f
    private var scaleFactor = 1f
    private val NONE: Int = 0
    private val DRAG: Int = 1
    private val ZOOM: Int = 2
    private var mode: Int = NONE
    private var startX: Float = 0f
    private var startY: Float = 0f
    private var translateX: Float = 0f
    private var translateY: Float = 0f
    private var previousTranslateX: Float = 0f
    private var previousTranslateY: Float = 0f
    private var dragged: Boolean = false
    private val detector: ScaleGestureDetector
    private val displayHeight: Int
    private val displayWidth: Int
    var response: Response<PostCoordResponse>? = null

    init {
        holder.addCallback(this)
        x = 0
        y = 0
        detector = ScaleGestureDetector(ctx, ScaleListener())
        displayHeight = height
        displayWidth = width
    }
    // Zoom 출처: https://stackoverflow.com/questions/25757544/android-scale-zoom-zoomout-bitmap-on-canvas
    override fun onTouchEvent(event: MotionEvent?): Boolean {
        when(MotionEvent.ACTION_MASK) {
            MotionEvent.ACTION_DOWN -> {
                mode = DRAG
                Log.d("asdasd", "action down")
                startX = event!!.x - previousTranslateX
                startY = event!!.y - previousTranslateY
            }
            MotionEvent.ACTION_MOVE -> {
                translateX = event!!.x - startX
                translateY = event!!.y - startY
                Log.d("asdasd", "action move")
                val distance = Math.sqrt(
                    Math.pow((event.x - (startX + previousTranslateX)).toDouble(), 2.0)
                            + Math.pow((event.y - (startY + previousTranslateY)).toDouble(), 2.0)
                )

                if (distance > 0) {
                    dragged = true
                }
            }
            MotionEvent.ACTION_UP -> {
                Log.d("asdasd", "action up")
                mode = NONE
                dragged = false
                previousTranslateX = translateX
                previousTranslateY = translateY
            }
            MotionEvent.ACTION_POINTER_UP -> {
                Log.d("asdasd", "action pointer up")
                mode = DRAG
                previousTranslateX = translateX
                previousTranslateY = translateY
            }
        }
        detector.onTouchEvent(event)

        if ((mode == DRAG && scaleFactor != 1f && dragged) || mode == ZOOM) {
            invalidate()
        }
        return true
//        return super.onTouchEvent(event)
}

    private fun doDraw(c: Canvas) {
        c.save()
        c.scale(scaleFactor, scaleFactor)


        // draw map
        val dst = Rect(0, 0, 1240 , 4200)
        c.drawBitmap(img, null, dst, null)

        Paint().also {
            it.style = Paint.Style.FILL
            it.color = Color.parseColor("#FF4545")

            try {
                x = coordToDp(response!!.body().data.x)
                y = coordToDp(105 - response!!.body().data.y)
                c.drawCircle(x.toFloat(), y.toFloat(), 30f, it)
            } catch (e: Exception) {
//                e.printStackTrace()
            }
        }

        if((translateX * -1) < 0) {
            translateX = 0F
        } else if((translateX * -1) > (scaleFactor - 1) * displayWidth) {
            translateX = (1 - scaleFactor) * displayWidth
        }

        if(translateY * -1 < 0) {
            translateY = 0F
        } else if((translateY * -1) > (scaleFactor - 1) * displayHeight) {
            translateY = (1 - scaleFactor) * displayHeight
        }

        c.translate(translateX / scaleFactor, translateY / scaleFactor)
        c.restore()
    }

    fun moveScreen(posY: Int = response!!.body().data.y) {
        // max Y val : 2352
        sv_vertical.scrollTo(0, ((105-posY)*22.4).toInt())
    }

    override fun onMeasure(wMS: Int, hMS: Int) {
        setMeasuredDimension(1240, 4200)
    }
    private fun coordToDp(coord: Int) : Int {
        // 이미지 좌표 최대값 (31/105)
        // 캔버스 크기 (2511, 8505) (before)
        // 캔버스 크기 (1240, 4200)
        return coord*40
    }

    /* SurfaceView */
    override fun surfaceCreated(holder: SurfaceHolder?) {
        thread = Thread(this)
        thread!!.start()
    }

    override fun surfaceDestroyed(holder: SurfaceHolder?) {
        try {
            thread!!.interrupt()
        } catch (e: Exception) {
            Log.e(this.javaClass.name, e.message)
        }
    }

    override fun surfaceChanged(holder: SurfaceHolder?, format: Int, width: Int, height: Int) {
    }

    override fun run() {
        while(!Thread.currentThread().isInterrupted) {
            var canvas: Canvas? = null
            try {
                canvas = mHolder.lockCanvas()
                synchronized(mHolder) {
                    doDraw(canvas)
                    Thread.sleep(50)
                }
            } catch (e: InterruptedException) {
                e.printStackTrace()
            } finally {
                if(canvas != null)
                    mHolder.unlockCanvasAndPost(canvas)
            }
        }
    }

    private inner class ScaleListener : ScaleGestureDetector.SimpleOnScaleGestureListener() {
        override fun onScale(detector: ScaleGestureDetector): Boolean {
            scaleFactor *= detector.scaleFactor
            scaleFactor = Math.max(MIN_ZOOM, Math.min(scaleFactor, MAX_ZOOM))
            return true
        }
    }
}