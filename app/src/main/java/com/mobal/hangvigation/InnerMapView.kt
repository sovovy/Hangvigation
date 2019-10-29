package com.mobal.hangvigation

import android.content.Context
import android.view.View
import android.graphics.*
import android.util.Log
import android.view.SurfaceHolder
import android.view.SurfaceView


class InnerMapView(val ctx: Context, val img: Bitmap) : SurfaceView(ctx), SurfaceHolder.Callback {
    private val mHolder: SurfaceHolder = holder
    private var mThread: DrawThread

    init {
        holder.addCallback(this)
        mThread = DrawThread(mHolder, this)
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
    private fun coordToDp(coord: Int) : Float {
        // 이미지 좌표 최대값 (31/105)
        // 캔버스 크기 (2511, 8505)
        return coord*81f
    }

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
//        if(mThread != null)

    }

    class DrawThread(val Holder: SurfaceHolder, val innerMapView: InnerMapView) : Thread() {
        var bExit: Boolean = false
        val mHolder: SurfaceHolder = Holder

        override fun run(){
            var canvas: Canvas

            while(bExit == false){
                synchronized(mHolder){
                    canvas = mHolder.lockCanvas()


                    // 여기에 onDraw()에 있던 내용이 들어가야댐ㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡ
                    // draw map
                    val dst = Rect(0, 0, innerMapView.img.width , innerMapView.img.height)
//                    Log.d("tag", innerMapView.img.width.toString() +" "+ innerMapView.img.height.toString())
                    canvas.drawBitmap(innerMapView.img, null, dst, null)


                    Paint().also {
                        it.style = Paint.Style.FILL
                        it.color = Color.RED
                        // y는 반대여서 105에서 좌표값을 빼줘야댐
                        canvas.drawCircle(innerMapView.coordToDp(17), innerMapView.coordToDp(105-72), 50f, it)
                    }
                    //ㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡ

                    mHolder.unlockCanvasAndPost(canvas)
                }
            }
        }
    }
}

// TODO
// 서버랑 통신
// 서버에서 받은 현재 위치 값을 canvas 좌표에서 찾아 점 그리기

// 라인 위에 점만 지울 방법 궁리 (현재 위치 변할때마다 점 위치도 변하게)
// coordToDp 함수 생각 (좌표값->캔버스 좌표)
// 맵 확대-축소 가능하게