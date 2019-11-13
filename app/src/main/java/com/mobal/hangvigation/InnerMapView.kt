package com.mobal.hangvigation

import android.content.Context
import android.graphics.*
import android.util.Log
import android.view.MotionEvent
import android.view.SurfaceHolder
import android.view.SurfaceView
import retrofit2.Response

class InnerMapView(val ctx: Context, val img: Bitmap) : SurfaceView(ctx), SurfaceHolder.Callback, Runnable {
    private val mHolder: SurfaceHolder = holder
    private var thread: Thread? = null
    var x: Int
    var y: Int
    var response: Response<PostCoordResponse>? = null

    init {
        holder.addCallback(this)
//        mThread = DrawThread(mHolder, this)
        x = 0
        y = 0
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        return super.onTouchEvent(event)
    }

    override fun onMeasure(wMS: Int, hMS: Int) {
        setMeasuredDimension(2511, 8505)
    }
    private fun coordToDp(coord: Int) : Int {
        // 이미지 좌표 최대값 (31/105)
        // 캔버스 크기 (2511, 8505)
        return coord*81
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

    private fun doDraw(c: Canvas) {
        // draw map
        val dst = Rect(0, 0, img.width , img.height)
        c.drawBitmap(img, null, dst, null)

        Paint().also {
            it.style = Paint.Style.FILL
            it.color = Color.RED

            try {
                if (response!!.isSuccessful) {
                    x = coordToDp(response!!.body().data.x)
                    y = coordToDp(105 - response!!.body().data.y)
                    Log.d("asd", "x:$x, y:$y")
                    c.drawCircle(x.toFloat(), y.toFloat(), 50f, it)
                } else {
                    Log.d("asdasd", "${response!!.message()}")
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    /* Thread */
    class DrawThread(val Holder: SurfaceHolder, val innerMapView: InnerMapView) : Thread() {
        var bExit: Boolean = false
        val mHolder: SurfaceHolder = Holder

        override fun run(){
            var canvas: Canvas

            while(bExit == false){
                synchronized(mHolder){
                    canvas = mHolder.lockCanvas()

                    /* onDraw()에 있던거 */
                    // draw map
                    val dst = Rect(0, 0, innerMapView.img.width , innerMapView.img.height)
                    canvas.drawBitmap(innerMapView.img, null, dst, null)

                    Paint().also {
                        it.style = Paint.Style.FILL
                        it.color = Color.RED

                        if(innerMapView.response!!.isSuccessful) {
                            innerMapView.x = innerMapView.coordToDp(innerMapView.response!!.body().data.x)
                            innerMapView.y = innerMapView.coordToDp(105 - innerMapView.response!!.body().data.y)
                            Log.d("asd", "x:"+innerMapView.x.toString() + ", y:"+innerMapView.y.toString())
                            canvas.drawCircle(innerMapView.x.toFloat(), innerMapView.y.toFloat(), 50f, it)
                        }
                    }
                    /* onDraw()에 있던거 */

                    mHolder.unlockCanvasAndPost(canvas)
                }
            }
        }
    }
}

// TODO
// 서버에서 받은 현재 위치 값을 canvas 좌표에서 찾아 점 그리기
// 맵 확대-축소 가능하게