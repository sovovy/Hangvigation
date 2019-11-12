package com.mobal.hangvigation

import android.content.Context
import android.graphics.*
import android.util.Log
import android.view.MotionEvent
import android.view.SurfaceHolder
import android.view.SurfaceView
import retrofit2.Response


class InnerMapView(val ctx: Context, val img: Bitmap) : SurfaceView(ctx), SurfaceHolder.Callback {
    private val mHolder: SurfaceHolder = holder
    private var mThread: DrawThread
    var x: Int
    var y: Int
    val response: Response<PostCoordResponse>? = null

    init {
        holder.addCallback(this)
        mThread = DrawThread(mHolder, this)
        x = 0
        y = 0
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        return super.onTouchEvent(event)
    }

//    override fun onDraw(canvas: Canvas) {
//        super.onDraw(canvas)
//        setBackgroundColor(Color.WHITE)
//
//        // draw map
////        val dst = Rect(0, 0, img.width , img.height)
////        canvas.drawBitmap(img, null, dst, null)
////
////
////        Paint().also {
////            it.style = Paint.Style.FILL
////            it.color = Color.RED
////            canvas.drawCircle(coordToDp(10, 10), 0f, 50f, it)
////        }
//
////        Line
//
////        val MyPaint = Paint()
////        MyPaint.strokeWidth = 5f
////        MyPaint.style = Paint.Style.STROKE
////        MyPaint.color = Color.GRAY
////
////        val path = Path()
////        path.moveTo(100f, 100f)
////        path.lineTo(100f, 100f)
////        path.lineTo(100f, 200f)
////        path.lineTo(200f, 100f)
////        path.lineTo(200f, 200f)
////        path.lineTo(300f, 100f)
////        canvas.drawPath(path, MyPaint)
//    }

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
        mThread = DrawThread(mHolder, this)
        mThread.start()
    }

    override fun surfaceDestroyed(holder: SurfaceHolder?) {
        var retry = true
        while (retry) {
            try {
                mThread.join() // Thread 종료 기다리기
                break
            } catch (e: Exception) {
                e.printStackTrace()
            }
            retry = false
        }
    }

    override fun surfaceChanged(holder: SurfaceHolder?, format: Int, width: Int, height: Int) {

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
                            innerMapView.x = innerMapView.coordToDp(innerMapView.response.body().data.x)
                            innerMapView.y = innerMapView.coordToDp(105 - innerMapView.response.body().data.y)
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