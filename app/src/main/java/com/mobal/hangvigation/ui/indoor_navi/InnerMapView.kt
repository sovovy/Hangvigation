package com.mobal.hangvigation.ui.indoor_navi

import android.content.Context
import android.graphics.*
import android.util.Log
import android.view.SurfaceHolder
import android.view.SurfaceView
import android.widget.ScrollView
import com.mobal.hangvigation.network.PostCoordResponse
import retrofit2.Response

class InnerMapView(ctx: Context, var img: Bitmap, private val sv_vertical: ScrollView) : SurfaceView(ctx), SurfaceHolder.Callback, Runnable {
    private val mHolder: SurfaceHolder = holder
    private var thread: Thread? = null
    var x: Int
    var y: Int
    var response: Response<PostCoordResponse>? = null

    init {
        holder.addCallback(this)
        x = 0
        y = 0
    }

    private fun doDraw(c: Canvas) {
        c.save()

        // draw map
        val dst = Rect(0, 0, 1240 , 4200)
        c.drawBitmap(img, null, dst, null)

        // test coords
        var a = floatArrayOf(
            a(18),a(88),a(18),a(95),
            a(16),a(58),a(19),a(58),
            a(16),a(70),a(18),a(78)
        )

        drawLine(c, a)
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
//                x = coordToDp(response!!.body().data.x)
//                y = coordToDp(105 - response!!.body().data.y)
//                c.drawCircle(x.toFloat(), y.toFloat(), 30f, it)
                // test
                x = coordToDp(10)
                y = coordToDp(105 - 17)
                c.drawCircle(x.toFloat(), y.toFloat(), 30f, it)
            } catch (e: Exception) {
            }
        }
        // 목적지
        Paint().also {
            it.style = Paint.Style.FILL
            it.color = Color.parseColor("#FFD35C")
            try {
                x = coordToDp(17)
                y = coordToDp(105 - 63)
                c.drawCircle(x.toFloat(), y.toFloat(), 45f, it)
            } catch (e: Exception) {
            }
        }
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
}