package com.mobal.hangvigation

import android.content.Context
import android.view.View
import android.graphics.*


class InnerMapView(val ctx: Context, val img: Bitmap) : View(ctx){
    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        setBackgroundColor(Color.WHITE)

        // draw map
        val dst = Rect(0, 0, img.width , img.height)
        canvas.drawBitmap(img, null, dst, null)


        Paint().also {
            it.style = Paint.Style.FILL
            it.color = Color.RED
            canvas.drawCircle(coordToDp(10, 10), 0f, 50f, it)
        }

//        Line

//        val MyPaint = Paint()
//        MyPaint.strokeWidth = 5f
//        MyPaint.style = Paint.Style.STROKE
//        MyPaint.color = Color.GRAY
//
//        val path = Path()
//        path.moveTo(100f, 100f)
//        path.lineTo(100f, 100f)
//        path.lineTo(100f, 200f)
//        path.lineTo(200f, 100f)
//        path.lineTo(200f, 200f)
//        path.lineTo(300f, 100f)
//        canvas.drawPath(path, MyPaint)
    }

    override fun onMeasure(wMS: Int, hMS: Int) {
        setMeasuredDimension(7468, 2179)
    }
    private fun coordToDp(coord: Int, total: Int) : Float {
        return 10f
    }
}

// TODO
// 라인 위에 점만 지울 방법 궁리
// coordToDp 함수 생각
// 이미지 크기 바꾼거에 대해 값 다시 구하기