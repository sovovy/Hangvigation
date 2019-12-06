package com.mobal.hangvigation.ui.indoor_navi

import android.content.Context
import android.content.res.TypedArray
import android.graphics.Color
import android.support.constraint.ConstraintLayout
import android.support.v4.content.ContextCompat
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import com.mobal.hangvigation.R
import kotlinx.android.synthetic.main.floor_button.view.*

class FloorButton(context: Context?, attrs: AttributeSet?) : ConstraintLayout(context, attrs),View.OnClickListener {
    private var bg_click = false

    override fun onClick(v: View?) {
        changeBg()
        (context as IndoorNaviActivity).lastChange(tv_floor_flbtn.text.toString()[0].toString().toInt())
    }

    fun changeBg(){
        bg_click = !bg_click
        if (bg_click){
            cl_floor_flbtn.setBackgroundColor(ContextCompat.getColor(context, R.color.colorPrimary))
            tv_floor_flbtn.setTextColor(Color.WHITE)
        } else {
            cl_floor_flbtn.setBackgroundColor(Color.WHITE)
            tv_floor_flbtn.setTextColor(Color.BLACK)
        }
    }

    init {
        addView(LayoutInflater.from(getContext()).inflate(R.layout.floor_button, this, false))
        if (attrs != null) {
            getAttrs(attrs)
        }
        setOnClickListener(this)
    }

    private fun getAttrs(attrs: AttributeSet){
        val typedArray = context.obtainStyledAttributes(attrs, R.styleable.FloorButton)
        setTypeArray(typedArray)
    }

    private fun setTypeArray(typedArray: TypedArray){
        tv_floor_flbtn.text = typedArray.getString(R.styleable.FloorButton_text)

        if (typedArray.getBoolean(R.styleable.FloorButton_click, false)){
            cl_floor_flbtn.setBackgroundColor(ContextCompat.getColor(context, R.color.colorPrimary))
            tv_floor_flbtn.setTextColor(Color.WHITE)
        } else {
            cl_floor_flbtn.setBackgroundColor(Color.WHITE)
            tv_floor_flbtn.setTextColor(Color.BLACK)
        }
        typedArray.recycle()
    }
}