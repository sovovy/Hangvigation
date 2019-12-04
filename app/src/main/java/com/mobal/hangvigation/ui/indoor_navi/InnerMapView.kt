package com.mobal.hangvigation.ui.indoor_navi

import android.content.Context
import android.graphics.*
import android.util.Log
import android.view.SurfaceHolder
import android.view.SurfaceView
import android.widget.ScrollView
import com.mobal.hangvigation.model.PostCoordResponse
import retrofit2.Response
import kotlin.math.abs

class InnerMapView(ctx: Context, var img: Bitmap, private val sv_vertical: ScrollView) : SurfaceView(ctx), SurfaceHolder.Callback, Runnable {
    private val mHolder: SurfaceHolder = holder
    private var thread: Thread? = null
    var x: Int = 0
    var y: Int = 0
    private var prevX : Int = 0
    private var prevY : Int = 0
    var destX = 17
    var destY = 33
    var responseCoord: Response<PostCoordResponse>? = null
    var route = FloatArray(0)

    init {
        holder.addCallback(this)
    }

    private fun doDraw(c: Canvas) {
        c.save()

        // draw map
        val dst = Rect(0, 0, 1240 , 4200)
        c.drawBitmap(img, null, dst, null)

        drawLine(c, route)
        drawCircle(c)

        c.restore()
    }

    private fun drawLine(c: Canvas, coords: FloatArray) {
        Paint().also {
            it.style = Paint.Style.STROKE
            it.strokeWidth = 100f
            it.color = Color.parseColor("#005CCB")
            it.isDither = true
            it.strokeJoin = Paint.Join.ROUND
            it.strokeCap = Paint.Cap.ROUND
            it.pathEffect = CornerPathEffect(50f)
            it.isAntiAlias = true

            c.drawLines(coords, it)
            invalidate()
        }
    }

    fun a(n:Int): Float{
        return (n*40).toFloat()
    }
    private fun drawCircle(c: Canvas) {
        // 현재 위치
        Paint().also {
            it.style = Paint.Style.FILL
            it.color = Color.parseColor("#FF6A6A")
            try {
                if (abs(prevY-y)<=10 || prevX==0) {
//                    x = coordToDp(responseCoord!!.body().data.x)
//                    y = coordToDp(105 - responseCoord!!.body().data.y)
//                    c.drawCircle(x.toFloat(), y.toFloat(), 30f, it)

                    // test
                    x = coordToDp(17)
                    y = coordToDp(105 - 17)
                    //

                    prevX = x
                    prevY = y
                    c.drawCircle(x.toFloat(), y.toFloat(), 30f, it)
                }
            } catch (e: Exception) {
            }
        }
        // 목적지
        Paint().also {
            it.style = Paint.Style.FILL
            it.color = Color.parseColor("#FFD35C")
            try {
                c.drawCircle(coordToDp(destX).toFloat(), coordToDp(105 - destY).toFloat(), 50f, it)
            } catch (e: Exception) {
            }
        }
    }

    fun moveScreen(posY: Int = responseCoord!!.body().data.y) {
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
}