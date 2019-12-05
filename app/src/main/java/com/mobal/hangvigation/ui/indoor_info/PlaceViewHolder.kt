package com.mobal.hangvigation.ui.indoor_info

import android.support.constraint.ConstraintLayout
import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.TextView
import com.mobal.hangvigation.R

class PlaceViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    var place_cl : ConstraintLayout = itemView.findViewById(R.id.cl_place_item)
    var name_tv : TextView = itemView.findViewById(R.id.tv_place_item)
}